package com.bookstore.booksmanagementsystem.service;

import com.bookstore.booksmanagementsystem.dto.SchoolDTO;
import java.util.List;

public interface SchoolService {
	List<SchoolDTO> getAllSchools();

	SchoolDTO getSchoolByName(String name);

	SchoolDTO getSchoolById(Long id);

	List<SchoolDTO> searchSchoolsByName(String partialName);

	SchoolDTO createSchool(SchoolDTO schoolDTO);

	SchoolDTO updateSchool(Long id, SchoolDTO schoolDTO);

	void deleteSchool(Long id);
}
