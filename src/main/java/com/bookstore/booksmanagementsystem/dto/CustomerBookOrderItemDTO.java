package com.bookstore.booksmanagementsystem.dto;

import jakarta.validation.constraints.NotNull;

public class CustomerBookOrderItemDTO {
	private Long id;

	@NotNull
	private Long orderId;

	@NotNull
	private Long bookId;

	@NotNull
	private Integer quantity;

	private BookCondition conditionType;

	private String bookTitle;
	private String bookAuthor;
	private Double bookPrice;
	private String imageUrl;

	// New fields to support multiple lists
	@NotNull(message = "Official List ID cannot be null")
	private Long officialListId;

	@NotNull(message = "School ID cannot be null")
	private Long schoolId;

	private String schoolName;

	@NotNull(message = "Class ID cannot be null")
	private Long classId;

	private String className;

	private Integer year;

	// Committed pricing copied from temp items at approval time
	private Double unitPrice;
	private Double subtotal;

	public enum BookCondition {
		NEW, USED
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
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

	public String getBookTitle() {
		return bookTitle;
	}

	public void setBookTitle(String bookTitle) {
		this.bookTitle = bookTitle;
	}

	public String getBookAuthor() {
		return bookAuthor;
	}

	public void setBookAuthor(String bookAuthor) {
		this.bookAuthor = bookAuthor;
	}

	public Double getBookPrice() {
		return bookPrice;
	}

	public void setBookPrice(Double bookPrice) {
		this.bookPrice = bookPrice;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Long getOfficialListId() {
		return officialListId;
	}

	public void setOfficialListId(Long officialListId) {
		this.officialListId = officialListId;
	}

	public Long getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(Long schoolId) {
		this.schoolId = schoolId;
	}

	public Long getClassId() {
		return classId;
	}

	public void setClassId(Long classId) {
		this.classId = classId;
	}

	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
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
}
