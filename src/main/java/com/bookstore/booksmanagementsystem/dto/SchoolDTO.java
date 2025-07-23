package com.bookstore.booksmanagementsystem.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Objects;

public class SchoolDTO {
    private Long id;

    @NotBlank(message = "School name cannot be empty")
    @Size(min = 2, max = 100, message = "School name must be between 2 and 100 characters")
    private String name;

    @Size(max = 255, message = "Address cannot exceed 255 characters")
    private String address;

    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phoneNumber;

    public SchoolDTO() {
    }

    public SchoolDTO(Long id, String name, String address, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
    }

    // Getters and Setters
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SchoolDTO schoolDTO = (SchoolDTO) o;
        return Objects.equals(id, schoolDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "SchoolDTO{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", address='" + address + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               '}';
    }
}
