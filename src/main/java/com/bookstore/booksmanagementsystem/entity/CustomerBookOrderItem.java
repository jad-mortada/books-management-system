package com.bookstore.booksmanagementsystem.entity;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(
    name = "customer_book_order_items",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_order_book_condition",
            columnNames = {"customer_book_order_id", "book_id", "condition_type"}
        )
    }
)
public class CustomerBookOrderItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "customer_book_order_id")
	private CustomerBookOrder customerBookOrder;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "book_id")
	private Book book;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "official_list_id")
	private ListEntity officialList;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "school_id")
	private School school;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "class_id")
	private ClassEntity classEntity;

	@Column(name = "quantity")
	private Integer quantity;

	@Enumerated(EnumType.STRING)
	@Column(name = "condition_type")
	private BookCondition conditionType;

	@Column(name = "unit_price")
	private Double unitPrice;

	@Column(name = "subtotal")
	private Double subtotal;

	public enum BookCondition {
		NEW, USED
	}

	public CustomerBookOrderItem() {
		this.conditionType = BookCondition.NEW;
	}

	public CustomerBookOrderItem(CustomerBookOrder customerBookOrder, Book book, Integer quantity,
			BookCondition conditionType) {
		this.customerBookOrder = customerBookOrder;
		this.book = book;
		this.quantity = quantity;
		this.conditionType = conditionType;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public CustomerBookOrder getCustomerBookOrder() {
		return customerBookOrder;
	}

	public void setCustomerBookOrder(CustomerBookOrder customerBookOrder) {
		this.customerBookOrder = customerBookOrder;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
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

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public BookCondition getConditionType() {
		return conditionType;
	}

	public void setConditionType(BookCondition conditionType) {
		this.conditionType = conditionType;
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Double getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(Double subtotal) {
		this.subtotal = subtotal;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		CustomerBookOrderItem that = (CustomerBookOrderItem) o;
		if (id != null && that.id != null) {
			return Objects.equals(id, that.id);
		}
		// If not persisted yet, compare by business keys
		return Objects.equals(book, that.book) && Objects.equals(customerBookOrder, that.customerBookOrder)
				&& Objects.equals(officialList, that.officialList) && Objects.equals(school, that.school)
				&& Objects.equals(classEntity, that.classEntity) && Objects.equals(conditionType, that.conditionType)
				&& Objects.equals(quantity, that.quantity);
	}

	@Override
	public int hashCode() {
		if (id != null) {
			return Objects.hash(id);
		}
		// If not persisted yet, hash by business keys
		return Objects.hash(book, customerBookOrder, officialList, school, classEntity, conditionType, quantity);
	}

	@Override
	public String toString() {
		return "CustomerBookOrderItem{" + "id=" + id + ", orderId="
				+ (customerBookOrder != null ? customerBookOrder.getId() : "null") + ", bookId="
				+ (book != null ? book.getId() : "null") + ", quantity=" + quantity + ", conditionType=" + conditionType
				+ '}';
	}
}