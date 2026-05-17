package com.library.dto;

import lombok.Data;

@Data
public class BorrowRequest {
    private String userId;
    private String bookId;
}
