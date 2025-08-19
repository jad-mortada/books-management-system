package com.bookstore.booksmanagementsystem.repository;

import com.bookstore.booksmanagementsystem.entity.Book;
import com.bookstore.booksmanagementsystem.entity.TempCustomerOrder;
import com.bookstore.booksmanagementsystem.entity.TempCustomerOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TempCustomerOrderItemRepository extends JpaRepository<TempCustomerOrderItem, Long> {
    Optional<TempCustomerOrderItem> findFirstByTempOrderAndBook(TempCustomerOrder tempOrder, Book book);
    Optional<TempCustomerOrderItem> findFirstByTempOrderAndBookAndConditionType(TempCustomerOrder tempOrder, Book book, TempCustomerOrderItem.ConditionType conditionType);
    void deleteAllByTempOrder(TempCustomerOrder tempOrder);
}
