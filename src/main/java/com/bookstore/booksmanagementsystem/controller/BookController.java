package com.bookstore.booksmanagementsystem.controller;

import com.bookstore.booksmanagementsystem.dto.BookDTO;
import com.bookstore.booksmanagementsystem.service.BookService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/books")
public class BookController {

	private final BookService bookService;

	public BookController(BookService bookService) {
		this.bookService = bookService;
	}

	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
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

	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<BookDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookDTO bookDTO) {
		BookDTO updatedBook = bookService.updateBook(id, bookDTO);
		return ResponseEntity.ok(updatedBook);
	}

	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	@PostMapping(path = "/{id}/image")
	public ResponseEntity<BookDTO> uploadBookImage(@PathVariable Long id, @RequestParam("file") MultipartFile file)
			throws IOException {
		if (file == null || file.isEmpty()) {
			return ResponseEntity.badRequest().build();
		}
		Path uploadRoot = Paths.get("uploads", "books");
		Files.createDirectories(uploadRoot);
		String original = file.getOriginalFilename() == null ? "image" : file.getOriginalFilename();
		String ext = original.contains(".") ? original.substring(original.lastIndexOf('.')) : "";
		String storedName = "book_" + id + "_" + System.currentTimeMillis() + ext;
		Path destination = uploadRoot.resolve(storedName);
		Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
		String publicPath = "/uploads/books/" + storedName;
		String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
				.path(publicPath)
				.toUriString();
		BookDTO dto = bookService.updateBookImage(id, publicUrl);
		return ResponseEntity.ok(dto);
	}

	@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteBook(@PathVariable Long id) {
		BookDTO book = bookService.getBookById(id);
		bookService.deleteBook(id);
		return ResponseEntity.ok("Book '" + book.getTitle() + "' successfully deleted");
	}
}
