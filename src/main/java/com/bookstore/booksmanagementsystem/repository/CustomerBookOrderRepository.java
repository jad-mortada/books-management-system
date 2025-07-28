package com.bookstore.booksmanagementsystem.repository;

import com.bookstore.booksmanagementsystem.entity.CustomerBookOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerBookOrderRepository extends JpaRepository<CustomerBookOrder, Long> {
	Optional<CustomerBookOrder> findByCustomerIdAndOfficialListIdAndSchoolIdAndClassEntityId(Long customerId,
			Long officialListId, Long schoolId, Long classId);

	List<CustomerBookOrder> findByCustomerId(Long customerId);
}
