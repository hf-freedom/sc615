package com.library.service;

import com.library.common.Result;
import com.library.dto.*;
import com.library.entity.*;
import com.library.storage.InMemoryStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class LibraryService {

    @Autowired
    private InMemoryStorage storage;

    private static final int MAX_PENALTY_SCORE = 10;
    private static final int BORROW_DAYS = 30;
    private static final int RESERVATION_EXPIRE_DAYS = 7;

    public Result<BorrowRecord> borrowBook(BorrowRequest request) {
        User user = storage.getUserById(request.getUserId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        if (!user.getCanBorrow()) {
            return Result.error("用户当前无法借阅，请先处理逾期书籍");
        }

        long currentBorrowCount = storage.getAllBorrowRecords().stream()
                .filter(r -> r.getUserId().equals(request.getUserId()) && !r.getIsReturned())
                .count();
        if (currentBorrowCount >= user.getMaxBorrowLimit()) {
            return Result.error("已达到最大借阅数量限制");
        }

        Book book = storage.getBookById(request.getBookId());
        if (book == null) {
            return Result.error("图书不存在");
        }

        if (book.getAvailableQuantity() <= 0) {
            return Result.error("图书已被借完，可预约");
        }

        book.setAvailableQuantity(book.getAvailableQuantity() - 1);
        book.setBorrowCount(book.getBorrowCount() + 1);
        storage.saveBook(book);

        BorrowRecord record = new BorrowRecord();
        record.setId(UUID.randomUUID().toString());
        record.setUserId(request.getUserId());
        record.setBookId(request.getBookId());
        record.setBorrowDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, BORROW_DAYS);
        record.setDueDate(cal.getTime());
        record.setIsReturned(false);
        record.setIsOverdue(false);
        record.setOverdueDays(0);
        record.setPenaltyScore(0);
        record.setReminderSent(false);
        storage.saveBorrowRecord(record);

        return Result.success("借阅成功", record);
    }

    public Result<BorrowRecord> returnBook(ReturnRequest request) {
        BorrowRecord record = storage.getBorrowRecordById(request.getBorrowRecordId());
        if (record == null) {
            return Result.error("借阅记录不存在");
        }

        if (record.getIsReturned()) {
            return Result.error("该书已归还");
        }

        record.setReturnDate(new Date());
        record.setIsReturned(true);

        calculateOverdue(record);

        if (record.getPenaltyScore() > 0) {
            User user = storage.getUserById(record.getUserId());
            user.setPenaltyScore(user.getPenaltyScore() + record.getPenaltyScore());
            if (user.getPenaltyScore() >= MAX_PENALTY_SCORE) {
                user.setCanBorrow(false);
            }
            storage.saveUser(user);
        }

        Book book = storage.getBookById(record.getBookId());
        book.setAvailableQuantity(book.getAvailableQuantity() + 1);
        storage.saveBook(book);

        notifyNextReservation(record.getBookId());

        storage.saveBorrowRecord(record);
        return Result.success("归还成功", record);
    }

    public Result<BorrowRecord> renewBook(RenewRequest request) {
        BorrowRecord record = storage.getBorrowRecordById(request.getBorrowRecordId());
        if (record == null) {
            return Result.error("借阅记录不存在");
        }

        if (record.getIsReturned()) {
            return Result.error("该书已归还，无法续借");
        }

        if (record.getIsOverdue()) {
            return Result.error("该书已逾期，请先归还并处理逾期");
        }

        boolean hasReservation = storage.getAllReservations().stream()
                .anyMatch(r -> r.getBookId().equals(record.getBookId()) && !r.getIsFulfilled());
        if (hasReservation) {
            return Result.error("该书已有其他用户预约，无法续借");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTime(record.getDueDate());
        cal.add(Calendar.DAY_OF_MONTH, BORROW_DAYS);
        record.setDueDate(cal.getTime());
        storage.saveBorrowRecord(record);

        return Result.success("续借成功", record);
    }

    public Result<Reservation> reserveBook(ReservationRequest request) {
        User user = storage.getUserById(request.getUserId());
        if (user == null) {
            return Result.error("用户不存在");
        }

        Book book = storage.getBookById(request.getBookId());
        if (book == null) {
            return Result.error("图书不存在");
        }

        if (book.getAvailableQuantity() > 0) {
            return Result.error("该书尚有库存，可直接借阅");
        }

        boolean alreadyReserved = storage.getAllReservations().stream()
                .anyMatch(r -> r.getUserId().equals(request.getUserId()) 
                        && r.getBookId().equals(request.getBookId()) 
                        && !r.getIsFulfilled());
        if (alreadyReserved) {
            return Result.error("您已预约该书");
        }

        boolean alreadyBorrowed = storage.getAllBorrowRecords().stream()
                .anyMatch(r -> r.getUserId().equals(request.getUserId()) 
                        && r.getBookId().equals(request.getBookId()) 
                        && !r.getIsReturned());
        if (alreadyBorrowed) {
            return Result.error("您已借阅该书");
        }

        int queuePosition = (int) storage.getAllReservations().stream()
                .filter(r -> r.getBookId().equals(request.getBookId()) && !r.getIsFulfilled())
                .count() + 1;

        Reservation reservation = new Reservation();
        reservation.setId(UUID.randomUUID().toString());
        reservation.setUserId(request.getUserId());
        reservation.setBookId(request.getBookId());
        reservation.setReservationDate(new Date());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, RESERVATION_EXPIRE_DAYS);
        reservation.setExpireDate(cal.getTime());
        reservation.setIsNotified(false);
        reservation.setIsFulfilled(false);
        reservation.setQueuePosition(queuePosition);
        storage.saveReservation(reservation);

        return Result.success("预约成功", reservation);
    }

    private void calculateOverdue(BorrowRecord record) {
        long diff = record.getReturnDate().getTime() - record.getDueDate().getTime();
        long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
        if (days > 0) {
            record.setIsOverdue(true);
            record.setOverdueDays((int) days);
            record.setPenaltyScore((int) days);
        }
    }

    private void notifyNextReservation(String bookId) {
        List<Reservation> reservations = storage.getAllReservations().stream()
                .filter(r -> r.getBookId().equals(bookId) && !r.getIsFulfilled())
                .sorted(Comparator.comparing(Reservation::getReservationDate))
                .collect(Collectors.toList());

        if (!reservations.isEmpty()) {
            Reservation first = reservations.get(0);
            first.setIsNotified(true);
            storage.saveReservation(first);
        }
    }

    public void calculateOverduePenalty() {
        Date now = new Date();
        storage.getAllBorrowRecords().forEach(record -> {
            if (!record.getIsReturned() && !record.getIsOverdue()) {
                long diff = now.getTime() - record.getDueDate().getTime();
                long days = TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
                if (days > 0) {
                    record.setIsOverdue(true);
                    record.setOverdueDays((int) days);
                    record.setPenaltyScore((int) days);
                    storage.saveBorrowRecord(record);

                    User user = storage.getUserById(record.getUserId());
                    user.setPenaltyScore(user.getPenaltyScore() + (int) days);
                    if (user.getPenaltyScore() >= MAX_PENALTY_SCORE) {
                        user.setCanBorrow(false);
                    }
                    storage.saveUser(user);
                }
            }
        });
    }

    public void sendReturnReminders() {
        Date now = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.DAY_OF_MONTH, 3);
        Date reminderDate = cal.getTime();

        storage.getAllBorrowRecords().forEach(record -> {
            if (!record.getIsReturned() && !record.getReminderSent() 
                    && record.getDueDate().before(reminderDate)) {
                record.setReminderSent(true);
                storage.saveBorrowRecord(record);
            }
        });
    }

    public void updateReservationQueue() {
        Map<String, List<Reservation>> bookReservations = storage.getAllReservations().stream()
                .filter(r -> !r.getIsFulfilled())
                .collect(Collectors.groupingBy(Reservation::getBookId));

        bookReservations.forEach((bookId, reservations) -> {
            reservations.sort(Comparator.comparing(Reservation::getReservationDate));
            for (int i = 0; i < reservations.size(); i++) {
                reservations.get(i).setQueuePosition(i + 1);
                storage.saveReservation(reservations.get(i));
            }
        });
    }

    public List<Book> getPopularBooks() {
        return storage.getAllBooks().stream()
                .sorted((b1, b2) -> b2.getBorrowCount() - b1.getBorrowCount())
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<User> getOverdueUsers() {
        Set<String> overdueUserIds = storage.getAllBorrowRecords().stream()
                .filter(r -> r.getIsOverdue() && !r.getIsReturned())
                .map(BorrowRecord::getUserId)
                .collect(Collectors.toSet());
        return storage.getAllUsers().stream()
                .filter(u -> overdueUserIds.contains(u.getId()))
                .collect(Collectors.toList());
    }

    public Map<String, Object> getBorrowStatistics() {
        Map<String, Object> stats = new HashMap<>();
        long totalBorrowed = storage.getAllBorrowRecords().size();
        long currentBorrowed = storage.getAllBorrowRecords().stream()
                .filter(r -> !r.getIsReturned())
                .count();
        long overdueCount = storage.getAllBorrowRecords().stream()
                .filter(r -> r.getIsOverdue() && !r.getIsReturned())
                .count();
        long returnedCount = storage.getAllBorrowRecords().stream()
                .filter(BorrowRecord::getIsReturned)
                .count();

        stats.put("totalBorrowed", totalBorrowed);
        stats.put("currentBorrowed", currentBorrowed);
        stats.put("overdueCount", overdueCount);
        stats.put("returnedCount", returnedCount);
        stats.put("turnoverRate", totalBorrowed > 0 ? 
                String.format("%.2f", (double) returnedCount / totalBorrowed * 100) + "%" : "0%");
        return stats;
    }

    public List<Book> getAllBooks() {
        return storage.getAllBooks();
    }

    public List<User> getAllUsers() {
        return storage.getAllUsers();
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        return storage.getAllBorrowRecords();
    }

    public List<Reservation> getAllReservations() {
        return storage.getAllReservations();
    }

    public List<BorrowRecord> getUserBorrowRecords(String userId) {
        return storage.getAllBorrowRecords().stream()
                .filter(r -> r.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    public List<Reservation> getBookReservations(String bookId) {
        return storage.getAllReservations().stream()
                .filter(r -> r.getBookId().equals(bookId))
                .collect(Collectors.toList());
    }
}
