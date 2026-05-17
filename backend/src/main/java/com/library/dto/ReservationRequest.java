package com.library.dto;

import lombok.Data;

@Data
public class ReservationRequest {
    private String userId;
    private String bookId;
}
