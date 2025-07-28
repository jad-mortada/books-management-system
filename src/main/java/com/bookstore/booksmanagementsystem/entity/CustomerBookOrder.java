package com.bookstore.booksmanagementsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "customer_book_orders")
public class CustomerBookOrder {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_id")
	private Customer customer;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "official_list_id")
	private ListEntity officialList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "school_id")
	private School school;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "class_id")
	private ClassEntity classEntity;

	@Column(name = "order_date")
	private LocalDateTime orderDate;

	@OneToMany(mappedBy = "customerBookOrder", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CustomerBookOrderItem> orderItems = new HashSet<>();

	public CustomerBookOrder() {
	}

	public CustomerBookOrder(Customer customer, ListEntity officialList, School school, ClassEntity classEntity) {
		this.customer = customer;
		this.officialList = officialList;
		this.school = school;
		this.classEntity = classEntity;
		this.orderDate = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public ListEntity getOfficialList() {
		return officialList;
	}

	public void setOfficialList(ListEntity officialList) {
		this.officialList = officialList;
	}

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	public ClassEntity getClassEntity() {
		return classEntity;
	}

	public void setClassEntity(ClassEntity classEntity) {
		this.classEntity = classEntity;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public Set<CustomerBookOrderItem> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(Set<CustomerBookOrderItem> orderItems) {
		this.orderItems = orderItems;
	}

	@PrePersist
	protected void onCreate() {
		if (orderDate == null) {
			orderDate = LocalDateTime.now();
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		CustomerBookOrder that = (CustomerBookOrder) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "CustomerBookOrder{" + "id=" + id + ", customerId=" + (customer != null ? customer.getId() : "null")
				+ ", officialListId=" + (officialList != null ? officialList.getId() : "null") + ", schoolId="
				+ (school != null ? school.getId() : "null") + ", classId="
				+ (classEntity != null ? classEntity.getId() : "null") + ", orderDate=" + orderDate + '}';
	}
}
