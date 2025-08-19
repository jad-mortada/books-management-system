package com.bookstore.booksmanagementsystem.service.impl;

import com.bookstore.booksmanagementsystem.dto.AdminDTO;
import com.bookstore.booksmanagementsystem.entity.Admin;
import com.bookstore.booksmanagementsystem.exception.DuplicateResourceException;
import com.bookstore.booksmanagementsystem.exception.ResourceNotFoundException;
import com.bookstore.booksmanagementsystem.repository.AdminRepository;
import com.bookstore.booksmanagementsystem.repository.CustomerRepository;
import com.bookstore.booksmanagementsystem.service.AdminService;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;

    public AdminServiceImpl(AdminRepository adminRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder,
                            CustomerRepository customerRepository) {
        this.adminRepository = adminRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<AdminDTO> getAllAdmins() {
        // Return only normal admins (UI already filters, but keep backend strict)
        List<Admin> admins = adminRepository.findByRoles("ROLE_ADMIN");
        return admins.stream().map(a -> modelMapper.map(a, AdminDTO.class)).collect(Collectors.toList());
    }

    @Override
    public AdminDTO createAdmin(AdminDTO adminDTO) {
        // Normalize email
        String email = adminDTO.getEmail() != null ? adminDTO.getEmail().trim().toLowerCase() : null;
        adminDTO.setEmail(email);
        // Enforce global email uniqueness across admins and customers (case-insensitive)
        adminRepository.findByEmailIgnoreCase(email).ifPresent(a -> {
            throw new DuplicateResourceException("Email is already in use");
        });
        customerRepository.findByEmailIgnoreCase(email).ifPresent(c -> {
            throw new DuplicateResourceException("Email is already in use");
        });
        Admin admin = modelMapper.map(adminDTO, Admin.class);
        if (adminDTO.getPassword() == null || adminDTO.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password is required for new admin");
        }
        admin.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        admin.setRoles("ROLE_ADMIN"); // Always create as normal admin
        Admin saved = adminRepository.save(admin);
        return modelMapper.map(saved, AdminDTO.class);
    }

    @Override
    public AdminDTO updateAdmin(Long id, AdminDTO adminDTO) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));

        if ("ROLE_SUPER_ADMIN".equals(admin.getRoles())) {
            throw new IllegalArgumentException("Cannot modify a super admin using this endpoint");
        }

        if (adminDTO.getEmail() != null && !adminDTO.getEmail().isEmpty()) {
            String email = adminDTO.getEmail().trim().toLowerCase();
            adminDTO.setEmail(email);
            adminRepository.findByEmailIgnoreCase(email).ifPresent(existing -> {
                if (!existing.getId().equals(id)) {
                    throw new DuplicateResourceException("Email is already in use");
                }
            });
            // Also prevent using an email that exists in customers
            customerRepository.findByEmailIgnoreCase(email).ifPresent(c -> {
                throw new DuplicateResourceException("Email is already in use");
            });
        }

        admin.setFirstName(adminDTO.getFirstName());
        admin.setLastName(adminDTO.getLastName());
        admin.setEmail(adminDTO.getEmail());
        admin.setPhoneNumber(adminDTO.getPhoneNumber());
        if (adminDTO.getPassword() != null && !adminDTO.getPassword().isEmpty()) {
            admin.setPassword(passwordEncoder.encode(adminDTO.getPassword()));
        }

        Admin updated = adminRepository.save(admin);
        return modelMapper.map(updated, AdminDTO.class);
    }

    @Override
    public void deleteAdmin(Long id) {
        Admin admin = adminRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", id));
        if ("ROLE_SUPER_ADMIN".equals(admin.getRoles())) {
            throw new IllegalArgumentException("Cannot delete a super admin");
        }
        adminRepository.delete(admin);
    }

    @Override
    public AdminDTO getCurrentAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth != null ? auth.getName() : null;
        if (email == null || email.isEmpty()) {
            throw new ResourceNotFoundException("Admin", "email", "<current>");
        }
        Admin admin = adminRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "email", email));
        return modelMapper.map(admin, AdminDTO.class);
    }
}
