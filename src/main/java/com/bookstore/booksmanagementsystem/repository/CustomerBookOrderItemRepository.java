package com.bookstore.booksmanagementsystem.repository;

import com.bookstore.booksmanagementsystem.entity.CustomerBookOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerBookOrderItemRepository extends JpaRepository<CustomerBookOrderItem, Long> {
    Optional<CustomerBookOrderItem> findByCustomerBookOrderIdAndBookId(Long orderId, Long bookId);
    List<CustomerBookOrderItem> findByCustomerBookOrderId(Long orderId);
}
