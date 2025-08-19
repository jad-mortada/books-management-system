package com.bookstore.booksmanagementsystem.service.impl;

import com.bookstore.booksmanagementsystem.dto.ClassDTO;
import com.bookstore.booksmanagementsystem.entity.ClassEntity;
import com.bookstore.booksmanagementsystem.entity.School;
import com.bookstore.booksmanagementsystem.exception.DuplicateResourceException;
import com.bookstore.booksmanagementsystem.exception.ResourceNotFoundException;
import com.bookstore.booksmanagementsystem.repository.ClassEntityRepository;
import com.bookstore.booksmanagementsystem.repository.SchoolRepository;
import com.bookstore.booksmanagementsystem.service.ClassService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClassServiceImpl implements ClassService {

    private final ClassEntityRepository classEntityRepository;
    private final SchoolRepository schoolRepository;
    private final ModelMapper modelMapper;

    public ClassServiceImpl(ClassEntityRepository classEntityRepository, SchoolRepository schoolRepository,
                            ModelMapper modelMapper) {
        this.classEntityRepository = classEntityRepository;
        this.schoolRepository = schoolRepository;
        this.modelMapper = modelMapper;
    }

    private ClassDTO mapToDTOWithSchool(ClassEntity classEntity) {
        ClassDTO dto = modelMapper.map(classEntity, ClassDTO.class);
        School school = classEntity.getSchool();
        dto.setSchoolId(school.getId());
        dto.setSchoolName(school.getName());
        dto.setSchoolAddress(school.getAddress());
        return dto;
    }

    @Override
    @Transactional
    public ClassDTO createClass(ClassDTO classDTO) {
        School school = schoolRepository.findById(classDTO.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", classDTO.getSchoolId()));

        // Check for duplicate class (name, school, year combination)
        classEntityRepository
                .findByNameAndSchoolIdAndYear(classDTO.getName(), classDTO.getSchoolId(), classDTO.getYear())
                .ifPresent(c -> {
                    throw new DuplicateResourceException("Class", "name, schoolId, year",
                            classDTO.getName() + ", " + classDTO.getSchoolId() + ", " + classDTO.getYear());
                });

        ClassEntity classEntity = modelMapper.map(classDTO, ClassEntity.class);
        classEntity.setSchool(school); // Set the actual School entity

        ClassEntity savedClass = classEntityRepository.save(classEntity);
        return mapToDTOWithSchool(savedClass);
    }

    @Override
    public ClassDTO getClassById(Long id) {
        ClassEntity classEntity = classEntityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", id));
        return mapToDTOWithSchool(classEntity);
    }

    @Override
    public List<ClassDTO> getAllClasses() {
        List<ClassEntity> classes = classEntityRepository.findAll();
        return classes.stream()
                .map(this::mapToDTOWithSchool)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ClassDTO updateClass(Long id, ClassDTO classDTO) {
        ClassEntity existingClass = classEntityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", id));

        School school = schoolRepository.findById(classDTO.getSchoolId())
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", classDTO.getSchoolId()));

        // Check for duplicate class (name, school, year combination) excluding itself
        classEntityRepository
                .findByNameAndSchoolIdAndYear(classDTO.getName(), classDTO.getSchoolId(), classDTO.getYear())
                .ifPresent(c -> {
                    if (!c.getId().equals(id)) {
                        throw new DuplicateResourceException("Class", "name, schoolId, year",
                                classDTO.getName() + ", " + classDTO.getSchoolId() + ", " + classDTO.getYear());
                    }
                });

        existingClass.setName(classDTO.getName());
        existingClass.setYear(classDTO.getYear());
        existingClass.setSchool(school); // Update the associated school if changed

        ClassEntity updatedClass = classEntityRepository.save(existingClass);
        return mapToDTOWithSchool(updatedClass);
    }

    @Override
    @Transactional
    public void deleteClass(Long id) {
        ClassEntity classEntity = classEntityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Class", "id", id));
        classEntityRepository.delete(classEntity);
    }

    @Override
    public List<ClassDTO> getClassesBySchool(Long schoolId) {
        // First, check if the school exists
        if (!schoolRepository.existsById(schoolId)) {
            throw new ResourceNotFoundException("School", "id", schoolId);
        }
        List<ClassEntity> classes = classEntityRepository.findBySchoolId(schoolId);
        return classes.stream()
                .map(this::mapToDTOWithSchool)
                .collect(Collectors.toList());
    }
}