package com.bookstore.booksmanagementsystem.repository;

import com.bookstore.booksmanagementsystem.entity.ListEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ListEntityRepository extends JpaRepository<ListEntity, Long> {
    Optional<ListEntity> findByClassEntityIdAndYear(Long classId, Integer year);
}
