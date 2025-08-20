package com.bookstore.booksmanagementsystem.service;

import com.bookstore.booksmanagementsystem.dto.BookDTO;

import java.util.List;

public interface BookService {
	BookDTO createBook(BookDTO bookDTO);

	BookDTO getBookById(Long id);

	List<BookDTO> getAllBooks();

	BookDTO updateBook(Long id, BookDTO bookDTO);

	void deleteBook(Long id);

	List<BookDTO> searchBooks(String query);

	BookDTO updateBookImage(Long id, String imageUrl);
}
