package com.bookstore.booksmanagementsystem.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "lists", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"class_id", "year"})
})
public class ListEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "class_id")
    private ClassEntity classEntity;

    @Column(name = "year")
    private Integer year;

    @OneToMany(mappedBy = "listEntity", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ListBook> listBooks = new HashSet<>();

    public ListEntity() {
    }

    public ListEntity(ClassEntity classEntity, Integer year) {
        this.classEntity = classEntity;
        this.year = year;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ClassEntity getClassEntity() {
        return classEntity;
    }

    public void setClassEntity(ClassEntity classEntity) {
        this.classEntity = classEntity;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Set<ListBook> getListBooks() {
        return listBooks;
    }

    public void setListBooks(Set<ListBook> listBooks) {
        this.listBooks = listBooks;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ListEntity that = (ListEntity) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ListEntity{" +
               "id=" + id +
               ", classEntity=" + (classEntity != null ? classEntity.getName() : "null") +
               ", year=" + year +
               '}';
    }
}
