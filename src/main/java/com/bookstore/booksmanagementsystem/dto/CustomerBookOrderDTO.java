package com.bookstore.booksmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CustomerBookOrderDTO {
	private Long id;

	@NotNull(message = "Customer ID cannot be null")
	private Long customerId;
	private String customerFirstName;
	private String customerLastName;

	private LocalDateTime orderDate;

	private Set<CustomerBookOrderItemDTO> orderItems = new HashSet<>();

	public CustomerBookOrderDTO() {
	}

	public CustomerBookOrderDTO(Long id, Long customerId, String customerFirstName, String customerLastName,
			LocalDateTime orderDate) {
		this.id = id;
		this.customerId = customerId;
		this.customerFirstName = customerFirstName;
		this.customerLastName = customerLastName;
		this.orderDate = orderDate;

	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public String getCustomerFirstName() {
		return customerFirstName;
	}

	public void setCustomerFirstName(String customerFirstName) {
		this.customerFirstName = customerFirstName;
	}

	public String getCustomerLastName() {
		return customerLastName;
	}

	public void setCustomerLastName(String customerLastName) {
		this.customerLastName = customerLastName;
	}

	public LocalDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(LocalDateTime orderDate) {
		this.orderDate = orderDate;
	}

	public Set<CustomerBookOrderItemDTO> getOrderItems() {
		return orderItems;
	}

	public void setOrderItems(Set<CustomerBookOrderItemDTO> orderItems) {
		this.orderItems = orderItems;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		CustomerBookOrderDTO that = (CustomerBookOrderDTO) o;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "CustomerBookOrderDTO{" + "id=" + id + ", customerId=" + customerId + ", orderDate=" + orderDate +

				'}';
	}
}
