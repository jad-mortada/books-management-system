package com.bookstore.booksmanagementsystem.repository;

import com.bookstore.booksmanagementsystem.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByEmail(String email);
    Optional<Admin> findByEmailIgnoreCase(String email);
    @Query("SELECT a FROM Admin a WHERE LOWER(TRIM(a.email)) = LOWER(TRIM(:email))")
    Optional<Admin> findByEmailNormalized(@Param("email") String email);
    List<Admin> findByRoles(String roles);
}
