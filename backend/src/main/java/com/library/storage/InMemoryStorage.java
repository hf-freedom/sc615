package com.library.storage;

import com.library.entity.Book;
import com.library.entity.BorrowRecord;
import com.library.entity.Reservation;
import com.library.entity.User;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryStorage {
    private final Map<String, Book> books = new ConcurrentHashMap<>();
    private final Map<String, User> users = new ConcurrentHashMap<>();
    private final Map<String, BorrowRecord> borrowRecords = new ConcurrentHashMap<>();
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

    public InMemoryStorage() {
        initData();
    }

    private void initData() {
        Book book1 = new Book();
        book1.setId("1");
        book1.setIsbn("9787111544937");
        book1.setTitle("Java编程思想");
        book1.setAuthor("Bruce Eckel");
        book1.setCategory("计算机");
        book1.setTotalQuantity(5);
        book1.setAvailableQuantity(5);
        book1.setBorrowCount(0);
        books.put(book1.getId(), book1);

        Book book2 = new Book();
        book2.setId("2");
        book2.setIsbn("9787115428028");
        book2.setTitle("深入理解Java虚拟机");
        book2.setAuthor("周志明");
        book2.setCategory("计算机");
        book2.setTotalQuantity(3);
        book2.setAvailableQuantity(3);
        book2.setBorrowCount(0);
        books.put(book2.getId(), book2);

        Book book3 = new Book();
        book3.setId("3");
        book3.setIsbn("9787506352444");
        book3.setTitle("百年孤独");
        book3.setAuthor("加西亚·马尔克斯");
        book3.setCategory("文学");
        book3.setTotalQuantity(4);
        book3.setAvailableQuantity(4);
        book3.setBorrowCount(0);
        books.put(book3.getId(), book3);

        Book book4 = new Book();
        book4.setId("4");
        book4.setIsbn("9787020002208");
        book4.setTitle("红楼梦");
        book4.setAuthor("曹雪芹");
        book4.setCategory("文学");
        book4.setTotalQuantity(6);
        book4.setAvailableQuantity(6);
        book4.setBorrowCount(0);
        books.put(book4.getId(), book4);

        Book book5 = new Book();
        book5.setId("5");
        book5.setIsbn("9787111213826");
        book5.setTitle("算法导论");
        book5.setAuthor("Thomas H. Cormen");
        book5.setCategory("计算机");
        book5.setTotalQuantity(2);
        book5.setAvailableQuantity(2);
        book5.setBorrowCount(0);
        books.put(book5.getId(), book5);

        User user1 = new User();
        user1.setId("1");
        user1.setUsername("zhangsan");
        user1.setName("张三");
        user1.setEmail("zhangsan@example.com");
        user1.setPhone("13800138001");
        user1.setPenaltyScore(0);
        user1.setCanBorrow(true);
        user1.setMaxBorrowLimit(5);
        users.put(user1.getId(), user1);

        User user2 = new User();
        user2.setId("2");
        user2.setUsername("lisi");
        user2.setName("李四");
        user2.setEmail("lisi@example.com");
        user2.setPhone("13800138002");
        user2.setPenaltyScore(0);
        user2.setCanBorrow(true);
        user2.setMaxBorrowLimit(5);
        users.put(user2.getId(), user2);

        User user3 = new User();
        user3.setId("3");
        user3.setUsername("wangwu");
        user3.setName("王五");
        user3.setEmail("wangwu@example.com");
        user3.setPhone("13800138003");
        user3.setPenaltyScore(0);
        user3.setCanBorrow(true);
        user3.setMaxBorrowLimit(5);
        users.put(user3.getId(), user3);

        book1.setAvailableQuantity(3);
        book1.setBorrowCount(2);
        book2.setAvailableQuantity(2);
        book2.setBorrowCount(1);
        book3.setAvailableQuantity(3);
        book3.setBorrowCount(1);
        book4.setAvailableQuantity(5);
        book4.setBorrowCount(1);

        Calendar cal = Calendar.getInstance();
        
        BorrowRecord record1 = new BorrowRecord();
        record1.setId("1");
        record1.setUserId("1");
        record1.setBookId("1");
        record1.setBorrowDate(new Date());
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 2);
        record1.setDueDate(cal.getTime());
        record1.setReturnDate(null);
        record1.setIsReturned(false);
        record1.setIsOverdue(false);
        record1.setOverdueDays(0);
        record1.setPenaltyScore(0);
        record1.setReminderSent(false);
        borrowRecords.put(record1.getId(), record1);

        BorrowRecord record2 = new BorrowRecord();
        record2.setId("2");
        record2.setUserId("2");
        record2.setBookId("2");
        record2.setBorrowDate(new Date());
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 5);
        record2.setDueDate(cal.getTime());
        record2.setReturnDate(null);
        record2.setIsReturned(false);
        record2.setIsOverdue(false);
        record2.setOverdueDays(0);
        record2.setPenaltyScore(0);
        record2.setReminderSent(true);
        borrowRecords.put(record2.getId(), record2);

        BorrowRecord record3 = new BorrowRecord();
        record3.setId("3");
        record3.setUserId("3");
        record3.setBookId("3");
        record3.setBorrowDate(new Date());
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, 8);
        record3.setDueDate(cal.getTime());
        record3.setReturnDate(null);
        record3.setIsReturned(false);
        record3.setIsOverdue(false);
        record3.setOverdueDays(0);
        record3.setPenaltyScore(0);
        record3.setReminderSent(false);
        borrowRecords.put(record3.getId(), record3);

        BorrowRecord record4 = new BorrowRecord();
        record4.setId("4");
        record4.setUserId("1");
        record4.setBookId("4");
        record4.setBorrowDate(new Date());
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        record4.setDueDate(cal.getTime());
        record4.setReturnDate(null);
        record4.setIsReturned(false);
        record4.setIsOverdue(false);
        record4.setOverdueDays(0);
        record4.setPenaltyScore(0);
        record4.setReminderSent(false);
        borrowRecords.put(record4.getId(), record4);

        Reservation reservation1 = new Reservation();
        reservation1.setId("1");
        reservation1.setUserId("1");
        reservation1.setBookId("1");
        reservation1.setReservationDate(new Date());
        reservation1.setExpireDate(cal.getTime());
        reservation1.setIsNotified(true);
        reservation1.setIsFulfilled(false);
        reservation1.setQueuePosition(1);
        reservations.put(reservation1.getId(), reservation1);

        Reservation reservation2 = new Reservation();
        reservation2.setId("2");
        reservation2.setUserId("2");
        reservation2.setBookId("1");
        reservation2.setReservationDate(new Date());
        reservation2.setExpireDate(cal.getTime());
        reservation2.setIsNotified(false);
        reservation2.setIsFulfilled(false);
        reservation2.setQueuePosition(2);
        reservations.put(reservation2.getId(), reservation2);

        Reservation reservation3 = new Reservation();
        reservation3.setId("3");
        reservation3.setUserId("3");
        reservation3.setBookId("1");
        reservation3.setReservationDate(new Date());
        reservation3.setExpireDate(cal.getTime());
        reservation3.setIsNotified(false);
        reservation3.setIsFulfilled(false);
        reservation3.setQueuePosition(3);
        reservations.put(reservation3.getId(), reservation3);

        Reservation reservation4 = new Reservation();
        reservation4.setId("4");
        reservation4.setUserId("1");
        reservation4.setBookId("2");
        reservation4.setReservationDate(new Date());
        reservation4.setExpireDate(cal.getTime());
        reservation4.setIsNotified(false);
        reservation4.setIsFulfilled(false);
        reservation4.setQueuePosition(1);
        reservations.put(reservation4.getId(), reservation4);

        Reservation reservation5 = new Reservation();
        reservation5.setId("5");
        reservation5.setUserId("3");
        reservation5.setBookId("2");
        reservation5.setReservationDate(new Date());
        reservation5.setExpireDate(cal.getTime());
        reservation5.setIsNotified(false);
        reservation5.setIsFulfilled(false);
        reservation5.setQueuePosition(2);
        reservations.put(reservation5.getId(), reservation5);
    }

    public Map<String, Book> getBooks() {
        return books;
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public Map<String, BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    public Map<String, Reservation> getReservations() {
        return reservations;
    }

    public List<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public List<BorrowRecord> getAllBorrowRecords() {
        return new ArrayList<>(borrowRecords.values());
    }

    public List<Reservation> getAllReservations() {
        return new ArrayList<>(reservations.values());
    }

    public Book getBookById(String id) {
        return books.get(id);
    }

    public User getUserById(String id) {
        return users.get(id);
    }

    public BorrowRecord getBorrowRecordById(String id) {
        return borrowRecords.get(id);
    }

    public Reservation getReservationById(String id) {
        return reservations.get(id);
    }

    public void saveBook(Book book) {
        books.put(book.getId(), book);
    }

    public void saveUser(User user) {
        users.put(user.getId(), user);
    }

    public void saveBorrowRecord(BorrowRecord record) {
        borrowRecords.put(record.getId(), record);
    }

    public void saveReservation(Reservation reservation) {
        reservations.put(reservation.getId(), reservation);
    }

    public void deleteReservation(String id) {
        reservations.remove(id);
    }
}
