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
@RequestMapping("/api/classes")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ClassDTO> createClass(@Valid @RequestBody ClassDTO classDTO) {
        ClassDTO createdClass = classService.createClass(classDTO);
        return new ResponseEntity<>(createdClass, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassDTO> getClassById(@PathVariable Long id) {
        ClassDTO classDTO = classService.getClassById(id);
        return ResponseEntity.ok(classDTO);
    }

    @GetMapping
    public ResponseEntity<List<ClassDTO>> getAllClasses(@RequestParam(required = false) Long schoolId) {
        List<ClassDTO> classes;
        if (schoolId != null) {
            classes = classService.getClassesBySchool(schoolId);
        } else {
            classes = classService.getAllClasses();
        }
        return ResponseEntity.ok(classes);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ClassDTO> updateClass(@PathVariable Long id, @Valid @RequestBody ClassDTO classDTO) {
        ClassDTO updatedClass = classService.updateClass(id, classDTO);
        return ResponseEntity.ok(updatedClass);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClass(@PathVariable Long id) {
        ClassDTO classDTO = classService.getClassById(id);
        classService.deleteClass(id);
        return ResponseEntity.ok("Class '" + classDTO.getName() + "' successfully deleted");
    }
}
