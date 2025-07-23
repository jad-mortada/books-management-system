package com.bookstore.booksmanagementsystem.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "schools")
public class School {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name = "phone_number")
    private String phoneNumber;

    @OneToMany(mappedBy = "school", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClassEntity> classes = new HashSet<>();

    @Transient
    private String classesMessage;

    public School() {
    }

    public School(String name, String address, String phoneNumber) {
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
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

    public Set<ClassEntity> getClasses() {
        return classes;
    }

    public void setClasses(Set<ClassEntity> classes) {
        this.classes = classes;
    }

    public String getClassesMessage() {
        return classesMessage;
    }

    public void setClassesMessage(String classesMessage) {
        this.classesMessage = classesMessage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        School school = (School) o;
        return Objects.equals(id, school.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "School{" +
               "id=" + id +
               ", name='" + name + '\'' +
               ", address='" + address + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", classesMessage='" + classesMessage + '\'' +
               '}';
    }
}
