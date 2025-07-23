package com.bookstore.booksmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;

public class CustomerBookOrderItemDTO {
    private Long id;

    @NotNull
    private Long orderId;

    @NotNull
    private Long bookId;

    @NotNull
    private Integer quantity;

    private Boolean isPrepared;

    private BookCondition conditionType;

    private String bookTitle;
    private String bookAuthor;
    private Double bookPrice;

    public enum BookCondition {
        NEW, USED
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Boolean getIsPrepared() {
        return isPrepared;
    }

    public void setIsPrepared(Boolean isPrepared) {
        this.isPrepared = isPrepared;
    }

    public BookCondition getConditionType() {
        return conditionType;
    }

    public void setConditionType(BookCondition conditionType) {
        this.conditionType = conditionType;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public String getBookAuthor() {
        return bookAuthor;
    }

    public void setBookAuthor(String bookAuthor) {
        this.bookAuthor = bookAuthor;
    }

    public Double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(Double bookPrice) {
        this.bookPrice = bookPrice;
    }
}
