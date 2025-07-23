package com.bookstore.booksmanagementsystem.service;

import com.bookstore.booksmanagementsystem.dto.ClassDTO;

import java.util.List;

public interface ClassService {
	ClassDTO createClass(ClassDTO classDTO);

	ClassDTO getClassById(Long id);

	List<ClassDTO> getAllClasses();

	ClassDTO updateClass(Long id, ClassDTO classDTO);

	void deleteClass(Long id);

	List<ClassDTO> getClassesBySchool(Long schoolId);
}
