package com.library.controller;

import com.library.common.Result;
import com.library.dto.*;
import com.library.entity.*;
import com.library.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LibraryController {

    @Autowired
    private LibraryService libraryService;

    @PostMapping("/borrow")
    public Result<BorrowRecord> borrowBook(@RequestBody BorrowRequest request) {
        return libraryService.borrowBook(request);
    }

    @PostMapping("/return")
    public Result<BorrowRecord> returnBook(@RequestBody ReturnRequest request) {
        return libraryService.returnBook(request);
    }

    @PostMapping("/renew")
    public Result<BorrowRecord> renewBook(@RequestBody RenewRequest request) {
        return libraryService.renewBook(request);
    }

    @PostMapping("/reserve")
    public Result<Reservation> reserveBook(@RequestBody ReservationRequest request) {
        return libraryService.reserveBook(request);
    }

    @GetMapping("/books")
    public Result<List<Book>> getAllBooks() {
        return Result.success(libraryService.getAllBooks());
    }

    @GetMapping("/users")
    public Result<List<User>> getAllUsers() {
        return Result.success(libraryService.getAllUsers());
    }

    @GetMapping("/borrow-records")
    public Result<List<BorrowRecord>> getAllBorrowRecords() {
        return Result.success(libraryService.getAllBorrowRecords());
    }

    @GetMapping("/reservations")
    public Result<List<Reservation>> getAllReservations() {
        return Result.success(libraryService.getAllReservations());
    }

    @GetMapping("/user/{userId}/borrow-records")
    public Result<List<BorrowRecord>> getUserBorrowRecords(@PathVariable String userId) {
        return Result.success(libraryService.getUserBorrowRecords(userId));
    }

    @GetMapping("/book/{bookId}/reservations")
    public Result<List<Reservation>> getBookReservations(@PathVariable String bookId) {
        return Result.success(libraryService.getBookReservations(bookId));
    }

    @GetMapping("/statistics/popular-books")
    public Result<List<Book>> getPopularBooks() {
        return Result.success(libraryService.getPopularBooks());
    }

    @GetMapping("/statistics/overdue-users")
    public Result<List<User>> getOverdueUsers() {
        return Result.success(libraryService.getOverdueUsers());
    }

    @GetMapping("/statistics/borrow")
    public Result<Map<String, Object>> getBorrowStatistics() {
        return Result.success(libraryService.getBorrowStatistics());
    }
}
