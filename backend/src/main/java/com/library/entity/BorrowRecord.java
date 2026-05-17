package com.library.entity;

import lombok.Data;
import java.util.Date;

@Data
public class BorrowRecord {
    private String id;
    private String userId;
    private String bookId;
    private Date borrowDate;
    private Date dueDate;
    private Date returnDate;
    private Boolean isReturned;
    private Boolean isOverdue;
    private Integer overdueDays;
    private Integer penaltyScore;
    private Boolean reminderSent;
}
