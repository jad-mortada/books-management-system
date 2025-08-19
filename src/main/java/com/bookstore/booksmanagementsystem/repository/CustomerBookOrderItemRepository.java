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

    Optional<CustomerBookOrderItem> findByCustomerBookOrderIdAndBookIdAndConditionTypeAndOfficialListIdAndSchoolIdAndClassEntityId(
            Long orderId,
            Long bookId,
            CustomerBookOrderItem.BookCondition conditionType,
            Long officialListId,
            Long schoolId,
            Long classId
    );

    // Matches likely DB unique key (5 columns, excluding class)
    Optional<CustomerBookOrderItem> findByCustomerBookOrderIdAndBookIdAndConditionTypeAndOfficialListIdAndSchoolId(
            Long orderId,
            Long bookId,
            CustomerBookOrderItem.BookCondition conditionType,
            Long officialListId,
            Long schoolId
    );

    // Fallbacks when DB unique index excludes some columns
    Optional<CustomerBookOrderItem> findByCustomerBookOrderIdAndBookIdAndConditionType(
            Long orderId,
            Long bookId,
            CustomerBookOrderItem.BookCondition conditionType
    );
}
