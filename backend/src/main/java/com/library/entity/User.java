package com.library.entity;

import lombok.Data;

@Data
public class User {
    private String id;
    private String username;
    private String name;
    private String email;
    private String phone;
    private Integer penaltyScore;
    private Boolean canBorrow;
    private Integer maxBorrowLimit;
}
