package com.bookstore.booksmanagementsystem.repository;

import com.bookstore.booksmanagementsystem.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByEmailIgnoreCase(String email);
    @Query("SELECT c FROM Customer c WHERE LOWER(TRIM(c.email)) = LOWER(TRIM(:email))")
    Optional<Customer> findByEmailNormalized(@Param("email") String email);
}
