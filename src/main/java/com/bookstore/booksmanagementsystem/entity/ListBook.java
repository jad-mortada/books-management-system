package com.bookstore.booksmanagementsystem.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "list_books", uniqueConstraints = { @UniqueConstraint(columnNames = { "list_id", "book_id" }) })
public class ListBook {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "list_id")
	private ListEntity listEntity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id")
	private Book book;

	public ListBook() {
	}

	public ListBook(ListEntity listEntity, Book book) {
		this.listEntity = listEntity;
		this.book = book;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ListEntity getListEntity() {
		return listEntity;
	}

	public void setListEntity(ListEntity listEntity) {
		this.listEntity = listEntity;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ListBook that = (ListBook) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "ListBook{" + "id=" + id + ", listEntityId=" + (listEntity != null ? listEntity.getId() : "null")
				+ ", bookId=" + (book != null ? book.getId() : "null") + '}';
	}
}
