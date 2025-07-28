package com.bookstore.booksmanagementsystem.controller;

import com.bookstore.booksmanagementsystem.dto.BookDTO;
import com.bookstore.booksmanagementsystem.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private final BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<BookDTO> createBook(@Valid @RequestBody BookDTO bookDTO) {
		BookDTO createdBook = bookService.createBook(bookDTO);
		return new ResponseEntity<>(createdBook, HttpStatus.CREATED);
	}

	@GetMapping("/{id}")
	public ResponseEntity<BookDTO> getBookById(@PathVariable Long id) {
		BookDTO bookDTO = bookService.getBookById(id);
		return ResponseEntity.ok(bookDTO);
	}

	@GetMapping
	public ResponseEntity<List<BookDTO>> getAllBooks(@RequestParam(required = false) String query) {
		List<BookDTO> books;
		if (query != null && !query.isEmpty()) {
			books = bookService.searchBooks(query);
		} else {
			books = bookService.getAllBooks();
		}
		return ResponseEntity.ok(books);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
		BookDTO updatedBook = bookService.updateBook(id, bookDTO);
		return ResponseEntity.ok(updatedBook);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteBook(@PathVariable Long id) {
		BookDTO book = bookService.getBookById(id);
		bookService.deleteBook(id);
		return ResponseEntity.ok("Book '" + book.getTitle() + "' successfully deleted");
	}
}
