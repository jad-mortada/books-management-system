package com.bookstore.booksmanagementsystem.service.impl;

import com.bookstore.booksmanagementsystem.dto.SchoolDTO;
import com.bookstore.booksmanagementsystem.entity.School;
import com.bookstore.booksmanagementsystem.exception.ResourceNotFoundException;
import com.bookstore.booksmanagementsystem.repository.SchoolRepository;
import com.bookstore.booksmanagementsystem.service.SchoolService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SchoolServiceImpl implements SchoolService {

    private final SchoolRepository schoolRepository;
    private final ModelMapper modelMapper;

    public SchoolServiceImpl(SchoolRepository schoolRepository, ModelMapper modelMapper) {
        this.schoolRepository = schoolRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<SchoolDTO> getAllSchools() {
        List<School> schools = schoolRepository.findAll();
        return schools.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public SchoolDTO getSchoolByName(String name) {
        School school = schoolRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("School", "name", name));
        return convertToDto(school);
    }

    @Override
    public SchoolDTO getSchoolById(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", id));
        return convertToDto(school);
    }

    @Override
    public List<SchoolDTO> searchSchoolsByName(String partialName) {
        List<School> schools = schoolRepository.findByNameContainingIgnoreCase(partialName);
        return schools.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SchoolDTO createSchool(SchoolDTO schoolDTO) {
        // Check for duplicate school by name before saving
        if (schoolRepository.findByName(schoolDTO.getName()).isPresent()) {
            throw new com.bookstore.booksmanagementsystem.exception.DuplicateResourceException(
                "School", "name", schoolDTO.getName());
        }
        School school = modelMapper.map(schoolDTO, School.class);
        School savedSchool = schoolRepository.save(school);
        return convertToDto(savedSchool);
    }

    @Override
    @Transactional
    public SchoolDTO updateSchool(Long id, SchoolDTO schoolDTO) {
        School existingSchool = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", id));

        // Check for duplicate school name excluding current school
        schoolRepository.findByName(schoolDTO.getName()).ifPresent(school -> {
            if (!school.getId().equals(id)) {
                throw new com.bookstore.booksmanagementsystem.exception.DuplicateResourceException(
                    "School", "name", schoolDTO.getName());
            }
        });

        existingSchool.setName(schoolDTO.getName());
        existingSchool.setAddress(schoolDTO.getAddress());
        existingSchool.setPhoneNumber(schoolDTO.getPhoneNumber());

        School updatedSchool = schoolRepository.save(existingSchool);
        return convertToDto(updatedSchool);
    }

    @Override
    @Transactional
    public void deleteSchool(Long id) {
        School school = schoolRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("School", "id", id));
        schoolRepository.delete(school);
    }

    private SchoolDTO convertToDto(School school) {
        SchoolDTO dto = new SchoolDTO();
        dto.setId(school.getId());
        dto.setName(school.getName());
        dto.setAddress(school.getAddress());
        dto.setPhoneNumber(school.getPhoneNumber());
        // Do not set classes or classesMessage to exclude them from output
        return dto;
    }
}
