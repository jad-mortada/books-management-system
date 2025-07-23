package com.bookstore.booksmanagementsystem.dto;

import com.bookstore.booksmanagementsystem.entity.CustomerBookOrder.OrderStatus;
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

    @NotNull(message = "Official List ID cannot be null")
    private Long officialListId;
    private String officialListYear;
    private String officialListClassName;
    private String officialListSchoolName;

    @NotNull(message = "School ID cannot be null")
    private Long schoolId;

    @NotNull(message = "Class ID cannot be null")
    private Long classId;

    private LocalDateTime orderDate;
    private OrderStatus status;

    private Set<CustomerBookOrderItemDTO> orderItems = new HashSet<>();

    public CustomerBookOrderDTO() {
    }

    public CustomerBookOrderDTO(Long id, Long customerId, String customerFirstName, String customerLastName, Long officialListId, String officialListYear, String officialListClassName, String officialListSchoolName, Long schoolId, Long classId, LocalDateTime orderDate, OrderStatus status) {
        this.id = id;
        this.customerId = customerId;
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.officialListId = officialListId;
        this.officialListYear = officialListYear;
        this.officialListClassName = officialListClassName;
        this.officialListSchoolName = officialListSchoolName;
        this.schoolId = schoolId;
        this.classId = classId;
        this.orderDate = orderDate;
        this.status = status;
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

    public Long getOfficialListId() {
        return officialListId;
    }

    public void setOfficialListId(Long officialListId) {
        this.officialListId = officialListId;
    }

    public String getOfficialListYear() {
        return officialListYear;
    }

    public void setOfficialListYear(String officialListYear) {
        this.officialListYear = officialListYear;
    }

    public String getOfficialListClassName() {
        return officialListClassName;
    }

    public void setOfficialListClassName(String officialListClassName) {
        this.officialListClassName = officialListClassName;
    }

    public String getOfficialListSchoolName() {
        return officialListSchoolName;
    }

    public void setOfficialListSchoolName(String officialListSchoolName) {
        this.officialListSchoolName = officialListSchoolName;
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

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Set<CustomerBookOrderItemDTO> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(Set<CustomerBookOrderItemDTO> orderItems) {
        this.orderItems = orderItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomerBookOrderDTO that = (CustomerBookOrderDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "CustomerBookOrderDTO{" +
               "id=" + id +
               ", customerId=" + customerId +
               ", officialListId=" + officialListId +
               ", orderDate=" + orderDate +
               ", status=" + status +
               '}';
    }
}
