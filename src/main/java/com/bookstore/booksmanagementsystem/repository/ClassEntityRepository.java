package com.bookstore.booksmanagementsystem.repository;

import com.bookstore.booksmanagementsystem.entity.ClassEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity, Long> {
	Optional<ClassEntity> findByNameAndSchoolIdAndYear(String name, Long schoolId, Integer year);

	List<ClassEntity> findBySchoolId(Long schoolId);
}
