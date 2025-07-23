package com.bookstore.booksmanagementsystem.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "author")
    private String author;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "price", nullable = false)
    private Double price;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ListBook> officialListAssociations = new HashSet<>();

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CustomerBookOrderItem> customerOrderItems = new HashSet<>();

    public Book() {
    }

    public Book(String title, String author, String isbn, String publisher, Double price) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publisher = publisher;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Set<ListBook> getOfficialListAssociations() {
        return officialListAssociations;
    }

    public void setOfficialListAssociations(Set<ListBook> officialListAssociations) {
        this.officialListAssociations = officialListAssociations;
    }

    public Set<CustomerBookOrderItem> getCustomerOrderItems() {
        return customerOrderItems;
    }

    public void setCustomerOrderItems(Set<CustomerBookOrderItem> customerOrderItems) {
        this.customerOrderItems = customerOrderItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Book book = (Book) o;
        return Objects.equals(id, book.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Book{" +
               "id=" + id +
               ", title='" + title + '\'' +
               ", author='" + author + '\'' +
               ", isbn='" + isbn + '\'' +
               ", price=" + price +
               '}';
    }
}
