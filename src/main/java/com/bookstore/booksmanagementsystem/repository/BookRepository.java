package com.bookstore.booksmanagementsystem.repository;

import com.bookstore.booksmanagementsystem.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
	Optional<Book> findByIsbn(String isbn);

	@Query("SELECT b FROM Book b WHERE " + "LOWER(b.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(b.author) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR "
			+ "LOWER(b.isbn) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
	List<Book> searchBooksByTitleAuthorOrIsbn(@Param("searchTerm") String searchTerm);
}
