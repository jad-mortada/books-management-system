package com.bookstore.booksmanagementsystem.service.impl;

import com.bookstore.booksmanagementsystem.dto.BookDTO;
import com.bookstore.booksmanagementsystem.entity.Book;
import com.bookstore.booksmanagementsystem.exception.DuplicateResourceException;
import com.bookstore.booksmanagementsystem.exception.ResourceNotFoundException;
import com.bookstore.booksmanagementsystem.repository.BookRepository;
import com.bookstore.booksmanagementsystem.service.BookService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookServiceImpl implements BookService {

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private final BookRepository bookRepository;
    private final ModelMapper modelMapper;

    public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public BookDTO createBook(BookDTO bookDTO) {
        if (bookDTO.getIsbn() != null && !bookDTO.getIsbn().isEmpty()) {
            bookRepository.findByIsbn(bookDTO.getIsbn())
                    .ifPresent(b -> {
                        throw new DuplicateResourceException("Book", "ISBN", bookDTO.getIsbn());
                    });
        }

        Book book = modelMapper.map(bookDTO, Book.class);
        Book savedBook = bookRepository.save(book);
        return modelMapper.map(savedBook, BookDTO.class);
    }

    @Override
    public BookDTO getBookById(Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
        return modelMapper.map(book, BookDTO.class);
    }

    @Override
    public List<BookDTO> getAllBooks() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public BookDTO updateBook(Long id, BookDTO bookDTO) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));

        if (bookDTO.getIsbn() != null && !bookDTO.getIsbn().isEmpty()) {
            bookRepository.findByIsbn(bookDTO.getIsbn()).ifPresent(b -> {
                if (!b.getId().equals(id)) {
                    throw new DuplicateResourceException("Book", "ISBN", bookDTO.getIsbn());
                }
            });
        }

        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setPublisher(bookDTO.getPublisher());
        existingBook.setPrice(bookDTO.getPrice());

        Book updatedBook = bookRepository.save(existingBook);
        return modelMapper.map(updatedBook, BookDTO.class);
    }

    @Override
    public void deleteBook(Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Book", "id", id));
            // Force fetch collections to avoid lazy loading issues
            book.getOfficialListAssociations().size();
            book.getCustomerOrderItems().size();
            // Clear associations
            book.getOfficialListAssociations().clear();
            book.getCustomerOrderItems().clear();
            bookRepository.delete(book);
        } catch (Exception e) {
            logger.error("Error deleting book with id {}: {}", id, e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public List<BookDTO> searchBooks(String query) {
        List<Book> books = bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrIsbnContainingIgnoreCase(query, query, query);
        return books.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }
}
