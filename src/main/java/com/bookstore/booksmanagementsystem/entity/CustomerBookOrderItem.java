package com.bookstore.booksmanagementsystem.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "customer_book_order_items")
public class CustomerBookOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_book_order_id")
    private CustomerBookOrder customerBookOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "is_prepared")
    private Boolean isPrepared;

    @Enumerated(EnumType.STRING)
    @Column(name = "condition_type")
    private BookCondition conditionType;

    public enum BookCondition {
        NEW, USED
    }

    public CustomerBookOrderItem() {
        this.isPrepared = false;
        this.conditionType = BookCondition.NEW;
    }

    public CustomerBookOrderItem(CustomerBookOrder customerBookOrder, Book book, Integer quantity, Boolean isPrepared, BookCondition conditionType) {
        this.customerBookOrder = customerBookOrder;
        this.book = book;
        this.quantity = quantity;
        this.isPrepared = isPrepared;
        this.conditionType = conditionType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CustomerBookOrder getCustomerBookOrder() {
        return customerBookOrder;
    }

    public void setCustomerBookOrder(CustomerBookOrder customerBookOrder) {
        this.customerBookOrder = customerBookOrder;
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerBookOrderItem that = (CustomerBookOrderItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CustomerBookOrderItem{" +
               "id=" + id +
               ", orderId=" + (customerBookOrder != null ? customerBookOrder.getId() : "null") +
               ", bookId=" + (book != null ? book.getId() : "null") +
               ", quantity=" + quantity +
               ", isPrepared=" + isPrepared +
               ", conditionType=" + conditionType +
               '}';
    }
}
