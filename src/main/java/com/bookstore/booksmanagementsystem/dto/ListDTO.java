package com.bookstore.booksmanagementsystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ListDTO {
    private Long id;

    @NotNull(message = "Class ID cannot be null")
    private Long classId;

    @NotNull(message = "Year cannot be null")
    @Min(value = 2000, message = "Year must be after 2000")
    private Integer year;

    private String className;
    private String schoolName;

    @JsonIgnore
    private Set<ListBookDTO> listBooks = new HashSet<>();

    public ListDTO() {
    }

    public ListDTO(Long id, Long classId, Integer year, String className, String schoolName) {
        this.id = id;
        this.classId = classId;
        this.year = year;
        this.className = className;
        this.schoolName = schoolName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClassId() {
        return classId;
    }

    public void setClassId(Long classId) {
        this.classId = classId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
    }

    public Set<ListBookDTO> getListBooks() {
        return listBooks;
    }

    public void setListBooks(Set<ListBookDTO> listBooks) {
        this.listBooks = listBooks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListDTO listDTO = (ListDTO) o;
        return Objects.equals(id, listDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ListDTO{" +
               "id=" + id +
               ", classId=" + classId +
               ", year=" + year +
               ", className='" + className + '\'' +
               ", schoolName='" + schoolName + '\'' +
               '}';
    }
}
