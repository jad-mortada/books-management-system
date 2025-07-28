
package com.bookstore.booksmanagementsystem.controller;

import com.bookstore.booksmanagementsystem.dto.ClassDTO;
import com.bookstore.booksmanagementsystem.service.ClassService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({ "/api/classes", "/api/class" })
public class ClassController {

	private final ClassService classService;

	public ClassController(ClassService classService) {
		this.classService = classService;
	}

	@GetMapping
	public ResponseEntity<List<ClassDTO>> getAllClasses() {
		List<ClassDTO> classes = classService.getAllClasses();
		return ResponseEntity.ok(classes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<ClassDTO> getClassById(@PathVariable Long id) {
		ClassDTO classDTO = classService.getClassById(id);
		return ResponseEntity.ok(classDTO);
	}

	@GetMapping("/by-school/{schoolId}")
	public ResponseEntity<List<ClassDTO>> getClassesBySchool(@PathVariable Long schoolId) {
		List<ClassDTO> classes = classService.getClassesBySchool(schoolId);
		return ResponseEntity.ok(classes);
	}

	@PostMapping
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ClassDTO> createClass(@Valid @RequestBody ClassDTO classDTO) {
		ClassDTO createdClass = classService.createClass(classDTO);
		return new ResponseEntity<>(createdClass, HttpStatus.CREATED);
	}

	@PutMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ClassDTO> updateClass(@PathVariable Long id, @Valid @RequestBody ClassDTO classDTO) {
		ClassDTO updatedClass = classService.updateClass(id, classDTO);
		return ResponseEntity.ok(updatedClass);
	}

	@DeleteMapping("/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteClass(@PathVariable Long id) {
		classService.deleteClass(id);
		return ResponseEntity.ok("Class with ID '" + id + "' successfully deleted");
	}
}
