package com.bookstore.booksmanagementsystem.controller;

import com.bookstore.booksmanagementsystem.dto.SchoolDTO;
import com.bookstore.booksmanagementsystem.service.SchoolService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schools")
public class SchoolController {

	private final SchoolService schoolService;

	public SchoolController(SchoolService schoolService) {
		this.schoolService = schoolService;
	}

	@GetMapping
	public ResponseEntity<?> getSchools(@RequestParam(required = false) String name) {
		if (name == null || name.trim().isEmpty()) {
			List<SchoolDTO> schools = schoolService.getAllSchools();
			if (schools.isEmpty()) {
				return ResponseEntity.ok("No schools available");
			}
			return ResponseEntity.ok(schools);
		} else {
			List<SchoolDTO> schools = schoolService.searchSchoolsByName(name.trim());
			if (schools.isEmpty()) {
				return ResponseEntity.status(404).body("No schools found matching: " + name);
			}
			return ResponseEntity.ok(schools);
		}
	}

	@GetMapping("/{id}")
	public ResponseEntity<SchoolDTO> getSchoolById(@PathVariable Long id) {
		SchoolDTO school = schoolService.getSchoolById(id);
		return ResponseEntity.ok(school);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<SchoolDTO> createSchool(@Valid @RequestBody SchoolDTO schoolDTO) {
		SchoolDTO createdSchool = schoolService.createSchool(schoolDTO);
		return new ResponseEntity<>(createdSchool, HttpStatus.CREATED);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<SchoolDTO> updateSchool(@PathVariable Long id, @Valid @RequestBody SchoolDTO schoolDTO) {
		SchoolDTO updatedSchool = schoolService.updateSchool(id, schoolDTO);
		return ResponseEntity.ok(updatedSchool);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}")
	public ResponseEntity<String> deleteSchool(@PathVariable Long id) {
		SchoolDTO school = schoolService.getSchoolById(id);
		schoolService.deleteSchool(id);
		return ResponseEntity.ok("School '" + school.getName() + "' successfully deleted");
	}
}
