package com.bookstore.booksmanagementsystem.repository;

import com.bookstore.booksmanagementsystem.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {
	Optional<School> findByName(String name);

	List<School> findByNameContainingIgnoreCase(String name);

	Optional<School> findByNameAndAddress(String name, String address);
}
