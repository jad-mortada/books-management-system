package com.bookstore.booksmanagementsystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.Objects;

public class ListBookDTO {
	private Long id;

	@NotNull(message = "List ID cannot be null")
	private Long listId;

	@NotNull(message = "Book ID cannot be null")
	private Long bookId;

	private String bookTitle;
	private String bookAuthor;
	private Double bookPrice;
	private String listYear;
	private String listClassName;

	public ListBookDTO() {
	}

	public ListBookDTO(Long id, Long listId, Long bookId, String bookTitle, String bookAuthor, Double bookPrice,
			String listYear, String listClassName) {
		this.id = id;
		this.listId = listId;
		this.bookId = bookId;
		this.bookTitle = bookTitle;
		this.bookAuthor = bookAuthor;
		this.bookPrice = bookPrice;
		this.listYear = listYear;
		this.listClassName = listClassName;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getListId() {
		return listId;
	}

	public void setListId(Long listId) {
		this.listId = listId;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
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

	public String getListYear() {
		return listYear;
	}

	public void setListYear(String listYear) {
		this.listYear = listYear;
	}

	public String getListClassName() {
		return listClassName;
	}

	public void setListClassName(String listClassName) {
		this.listClassName = listClassName;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ListBookDTO that = (ListBookDTO) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "ListBookDTO{" + "id=" + id + ", listId=" + listId + ", bookId=" + bookId + '}';
	}
}
