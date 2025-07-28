package com.bookstore.booksmanagementsystem.service.impl;

import com.bookstore.booksmanagementsystem.dto.BookDTO;
import com.bookstore.booksmanagementsystem.entity.Book;
import com.bookstore.booksmanagementsystem.exception.DuplicateResourceException;
import com.bookstore.booksmanagementsystem.exception.ResourceNotFoundException;
import com.bookstore.booksmanagementsystem.repository.BookRepository;
import com.bookstore.booksmanagementsystem.repository.CustomerBookOrderItemRepository;
import com.bookstore.booksmanagementsystem.repository.ListBookRepository;
import com.bookstore.booksmanagementsystem.service.BookService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

	private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

	private final BookRepository bookRepository;
	private final ListBookRepository listBookRepository;
	private final CustomerBookOrderItemRepository customerBookOrderItemRepository;
	private final ModelMapper modelMapper;

	public BookServiceImpl(BookRepository bookRepository, ListBookRepository listBookRepository,
			CustomerBookOrderItemRepository customerBookOrderItemRepository, ModelMapper modelMapper) {
		this.bookRepository = bookRepository;
		this.listBookRepository = listBookRepository;
		this.customerBookOrderItemRepository = customerBookOrderItemRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public BookDTO createBook(BookDTO bookDTO) {
		if (bookDTO.getIsbn() == null || bookDTO.getIsbn().isEmpty()) {
			throw new IllegalArgumentException("ISBN cannot be empty");
		}
		if (bookRepository.findByIsbn(bookDTO.getIsbn()).isPresent()) {
			throw new DuplicateResourceException("Book", "ISBN", bookDTO.getIsbn());
		}

		Book book = modelMapper.map(bookDTO, Book.class);
		Book savedBook = bookRepository.save(book);
		return modelMapper.map(savedBook, BookDTO.class);
	}

	@Override
	public BookDTO getBookById(Long id) {
		Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
		return modelMapper.map(book, BookDTO.class);
	}

	@Override
	public List<BookDTO> getAllBooks() {
		List<Book> books = bookRepository.findAll();
		return books.stream().map(book -> modelMapper.map(book, BookDTO.class)).collect(Collectors.toList());
	}

	@Override
	public BookDTO updateBook(Long id, BookDTO bookDTO) {
		if (bookDTO.getIsbn() == null || bookDTO.getIsbn().isEmpty()) {
			throw new IllegalArgumentException("ISBN cannot be empty");
		}
		Book existingBook = bookRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

		bookRepository.findByIsbn(bookDTO.getIsbn()).ifPresent(b -> {
			if (!b.getId().equals(id)) {
				throw new DuplicateResourceException("Book", "ISBN", bookDTO.getIsbn());
			}
		});

		existingBook.setTitle(bookDTO.getTitle());
		existingBook.setAuthor(bookDTO.getAuthor());
		existingBook.setIsbn(bookDTO.getIsbn());
		existingBook.setPublisher(bookDTO.getPublisher());
		existingBook.setPrice(bookDTO.getPrice());

		Book updatedBook = bookRepository.save(existingBook);
		return modelMapper.map(updatedBook, BookDTO.class);
	}

	@Override
	@Transactional
	public void deleteBook(Long id) {
		try {
			Book book = bookRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
			// Rely on JPA cascade and orphanRemoval to delete related entities
			bookRepository.delete(book);
			bookRepository.flush(); // Ensure delete is executed immediately to catch constraint violations
		} catch (Exception e) {
			logger.error("Error deleting book with id {}: {}", id, e.getMessage(), e);
			throw new RuntimeException(
					"Failed to delete book with id " + id + ". It may be referenced by other records.");
		}
	}

	@Override
	public List<BookDTO> searchBooks(String query) {
		List<Book> books = bookRepository.searchBooksByTitleAuthorOrIsbn(query);
		if (books.isEmpty()) {
			throw new RuntimeException("No books found matching the search criteria.");
		}
		return books.stream().map(book -> modelMapper.map(book, BookDTO.class)).collect(Collectors.toList());
	}
}
