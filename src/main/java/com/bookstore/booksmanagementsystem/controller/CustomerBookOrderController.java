package com.bookstore.booksmanagementsystem.controller;

import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderDTO;
import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderItemDTO;
import com.bookstore.booksmanagementsystem.entity.CustomerBookOrder.OrderStatus;
import com.bookstore.booksmanagementsystem.service.CustomerBookOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer-orders")
public class CustomerBookOrderController {

    private final CustomerBookOrderService customerBookOrderService;

    public CustomerBookOrderController(CustomerBookOrderService customerBookOrderService) {
        this.customerBookOrderService = customerBookOrderService;
    }

    @PostMapping("/generate-personalized-order")
    @PreAuthorize("hasRole('ADMIN') or (@securityService.isCustomerOwner(#orderDTO.customerId))")
    public ResponseEntity<CustomerBookOrderDTO> generatePersonalizedOrder(
            @Valid @RequestBody CustomerBookOrderDTO orderDTO) {
        CustomerBookOrderDTO createdOrder = customerBookOrderService.generatePersonalizedOrder(
                orderDTO.getCustomerId(),
                orderDTO.getSchoolId(),
                orderDTO.getClassId(),
                Integer.parseInt(orderDTO.getOfficialListYear()));

        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isOrderOwner(#id)")
    public ResponseEntity<CustomerBookOrderDTO> getOrderById(@PathVariable("id") Long orderId) {
        CustomerBookOrderDTO orderDTO = customerBookOrderService.getCustomerBookOrderById(orderId);
        return ResponseEntity.ok(orderDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CustomerBookOrderDTO>> getAllOrders() {
        List<CustomerBookOrderDTO> orders = customerBookOrderService.getAllCustomerBookOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/by-customer/{customerId}")
    @PreAuthorize("hasRole('ADMIN') or @securityService.isCustomerOwner(#customerId)")
    public ResponseEntity<List<CustomerBookOrderDTO>> getOrdersByCustomerId(@PathVariable Long customerId) {
        List<CustomerBookOrderDTO> orders = customerBookOrderService.getCustomerBookOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<CustomerBookOrderDTO> updateOrderStatus(
            @PathVariable("id") Long orderId,
            @RequestParam OrderStatus newStatus) {
        CustomerBookOrderDTO updatedOrder = customerBookOrderService.updateOrderStatus(orderId, newStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/items/{itemId}")
    public ResponseEntity<CustomerBookOrderItemDTO> updateOrderItem(
            @PathVariable("itemId") Long orderItemId,
            @RequestParam(required = false) Boolean isPrepared,
            @RequestParam(required = false) CustomerBookOrderItemDTO.BookCondition condition) {
        CustomerBookOrderItemDTO updatedItem = customerBookOrderService.updateOrderItemStatus(orderItemId, isPrepared, condition);
        return ResponseEntity.ok(updatedItem);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable("id") Long orderId) {
        CustomerBookOrderDTO order = customerBookOrderService.getCustomerBookOrderById(orderId);
        customerBookOrderService.deleteCustomerBookOrder(orderId);
        return ResponseEntity.ok("Order with ID '" + orderId + "' successfully deleted");
    }
}
