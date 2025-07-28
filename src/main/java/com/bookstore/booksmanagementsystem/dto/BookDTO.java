package com.bookstore.booksmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.Objects;

public class BookDTO {
	private Long id;

	@NotBlank(message = "Title cannot be empty")
	@Size(min = 2, max = 255, message = "Title must be between 2 and 255 characters")
	private String title;

	@Size(max = 100, message = "Author name cannot exceed 100 characters")
	private String author;

	@Size(max = 17, message = "ISBN cannot exceed 17 characters (e.g., ISBN-13 format)")
	private String isbn;

	@Size(max = 100, message = "Publisher name cannot exceed 100 characters")
	private String publisher;

	@NotNull(message = "Price cannot be null")
	@PositiveOrZero(message = "Price must be zero or positive")
	private Double price;

	public BookDTO() {
	}

	public BookDTO(Long id, String title, String author, String isbn, String publisher, Double price) {
		this.id = id;
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

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		BookDTO bookDTO = (BookDTO) o;
		return Objects.equals(id, bookDTO.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "BookDTO{" + "id=" + id + ", title='" + title + '\'' + ", author='" + author + '\'' + ", isbn='" + isbn
				+ '\'' + ", price=" + price + '}';
	}
}
