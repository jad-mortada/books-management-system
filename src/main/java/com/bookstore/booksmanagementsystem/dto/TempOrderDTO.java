package com.bookstore.booksmanagementsystem.dto;

import java.time.LocalDateTime;
import java.util.List;

public class TempOrderDTO {
    private Long id;
    private Long customerId;
    private String customerFirstName;
    private String customerLastName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<TempOrderItemDTO> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCustomerFirstName() { return customerFirstName; }
    public void setCustomerFirstName(String customerFirstName) { this.customerFirstName = customerFirstName; }
    public String getCustomerLastName() { return customerLastName; }
    public void setCustomerLastName(String customerLastName) { this.customerLastName = customerLastName; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public List<TempOrderItemDTO> getItems() { return items; }
    public void setItems(List<TempOrderItemDTO> items) { this.items = items; }
}
