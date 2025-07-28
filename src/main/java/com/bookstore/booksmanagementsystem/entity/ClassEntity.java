package com.bookstore.booksmanagementsystem.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "classes")
public class ClassEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name")
	private String name;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "school_id", nullable = false)
	private School school;

	@Column(nullable = false)
	private Integer year;

	public ClassEntity() {
	}

	public ClassEntity(String name, School school, Integer year) {
		this.name = name;
		this.school = school;
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

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		ClassEntity that = (ClassEntity) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "ClassEntity{" + "id=" + id + ", name='" + name + '\'' + ", year=" + year + ", schoolId="
				+ (school != null ? school.getId() : "null") + '}';
	}
}
