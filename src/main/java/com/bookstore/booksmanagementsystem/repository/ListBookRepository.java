package com.bookstore.booksmanagementsystem.repository;

import com.bookstore.booksmanagementsystem.entity.ListBook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ListBookRepository extends JpaRepository<ListBook, Long> {
	Optional<ListBook> findByListEntityIdAndBookId(Long listId, Long bookId);

	List<ListBook> findByListEntityId(Long listId);
}
