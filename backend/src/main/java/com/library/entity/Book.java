package com.library.entity;

import lombok.Data;

@Data
public class Book {
    private String id;
    private String isbn;
    private String title;
    private String author;
    private String category;
    private Integer totalQuantity;
    private Integer availableQuantity;
    private Integer borrowCount;
}
