package com.bookstore.booksmanagementsystem.repository;

import com.bookstore.booksmanagementsystem.entity.TempCustomerOrder;
import com.bookstore.booksmanagementsystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TempCustomerOrderRepository extends JpaRepository<TempCustomerOrder, Long> {
    Optional<TempCustomerOrder> findFirstByCustomerAndStatus(Customer customer, TempCustomerOrder.Status status);
    List<TempCustomerOrder> findAllByStatus(TempCustomerOrder.Status status);
    List<TempCustomerOrder> findAllByCustomerAndStatusIn(Customer customer, java.util.Collection<TempCustomerOrder.Status> statuses);
}
