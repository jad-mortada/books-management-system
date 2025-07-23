package com.bookstore.booksmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ClassDTO {

    private Long id;

    @NotBlank(message = "Class name cannot be empty")
    private String name;

    @NotNull(message = "School ID cannot be null")
    private Long schoolId;

    @NotNull(message = "Year cannot be null")
    private Integer year;

    public ClassDTO() {
    }

    public ClassDTO(Long id, String name, Long schoolId, Integer year) {
        this.id = id;
        this.name = name;
        this.schoolId = schoolId;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getSchoolId() {
        return schoolId;
    }

    public void setSchoolId(Long schoolId) {
        this.schoolId = schoolId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }
}
