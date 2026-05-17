package com.library.entity;

import lombok.Data;
import java.util.Date;

@Data
public class Reservation {
    private String id;
    private String userId;
    private String bookId;
    private Date reservationDate;
    private Date expireDate;
    private Boolean isNotified;
    private Boolean isFulfilled;
    private Integer queuePosition;
}
