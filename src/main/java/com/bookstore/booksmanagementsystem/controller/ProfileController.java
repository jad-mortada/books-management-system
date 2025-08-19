package com.bookstore.booksmanagementsystem.controller;

import com.bookstore.booksmanagementsystem.dto.ProfileDTO;
import com.bookstore.booksmanagementsystem.entity.Admin;
import com.bookstore.booksmanagementsystem.entity.Customer;
import com.bookstore.booksmanagementsystem.repository.AdminRepository;
import com.bookstore.booksmanagementsystem.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;

    public ProfileController(AdminRepository adminRepository, CustomerRepository customerRepository) {
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public ResponseEntity<ProfileDTO> me(Authentication authentication) {
        String email = authentication.getName();

        // Try admin first
        ProfileDTO dto = adminRepository.findByEmail(email)
                .map(this::toProfileDTO)
                .orElseGet(() -> customerRepository.findByEmail(email)
                        .map(this::toProfileDTO)
                        .orElse(null));

        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    private ProfileDTO toProfileDTO(Admin admin) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(admin.getId());
        dto.setFirstName(admin.getFirstName());
        dto.setLastName(admin.getLastName());
        dto.setEmail(admin.getEmail());
        dto.setPhoneNumber(admin.getPhoneNumber());
        dto.setRoles(admin.getRoles());
        boolean isSuper = admin.getRoles() != null && admin.getRoles().contains("ROLE_SUPER_ADMIN");
        dto.setUserType(isSuper ? "SUPER_ADMIN" : "ADMIN");
        return dto;
    }

    private ProfileDTO toProfileDTO(Customer c) {
        ProfileDTO dto = new ProfileDTO();
        dto.setId(c.getId());
        dto.setFirstName(c.getFirstName());
        dto.setLastName(c.getLastName());
        dto.setEmail(c.getEmail());
        dto.setPhoneNumber(c.getPhoneNumber());
        dto.setRoles(c.getRoles());
        dto.setUserType("USER");
        return dto;
    }
}
