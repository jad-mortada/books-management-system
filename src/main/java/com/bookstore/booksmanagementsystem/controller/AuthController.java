package com.bookstore.booksmanagementsystem.controller;

import com.bookstore.booksmanagementsystem.dto.AuthResponseDTO;
import com.bookstore.booksmanagementsystem.dto.CustomerDTO;
import com.bookstore.booksmanagementsystem.dto.LoginDTO;
import com.bookstore.booksmanagementsystem.repository.AdminRepository;
import com.bookstore.booksmanagementsystem.repository.CustomerRepository;
import com.bookstore.booksmanagementsystem.security.JwtTokenProvider;
import com.bookstore.booksmanagementsystem.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;
    private final JwtTokenProvider tokenProvider;
    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;

    public AuthController(AuthenticationManager authenticationManager, CustomerService customerService,
            JwtTokenProvider tokenProvider, AdminRepository adminRepository, CustomerRepository customerRepository) {
        this.authenticationManager = authenticationManager;
        this.customerService = customerService;
        this.tokenProvider = tokenProvider;
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> authenticateCustomer(@Valid @RequestBody LoginDTO loginDto) {
        String email = loginDto.getEmail() != null ? loginDto.getEmail().trim().toLowerCase() : null;
        // This endpoint is for CUSTOMER logins only. Block admin/super-admin emails here.
        if (adminRepository.findByEmailNormalized(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Admin email detected. Please use the admin login endpoint.");
        }
        // Optional: also ensure email exists in customers to avoid timing user enumeration via auth errors.
        if (customerRepository.findByEmailNormalized(email).isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, loginDto.getPassword()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponseDTO(jwt));
    }

    @PostMapping("/login/admin")
    public ResponseEntity<?> authenticateAdmin(@Valid @RequestBody LoginDTO loginDto) {
        String email = loginDto.getEmail() != null ? loginDto.getEmail().trim().toLowerCase() : null;
        // Admin-only login endpoint. Block customer emails here.
        if (customerRepository.findByEmailNormalized(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Customer email detected. Please use the customer login endpoint.");
        }
        if (adminRepository.findByEmailNormalized(email).isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(email, loginDto.getPassword()));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication);
        return ResponseEntity.ok(new AuthResponseDTO(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
        // Normalize email and enforce global uniqueness at the API boundary too
        String email = customerDTO.getEmail() != null ? customerDTO.getEmail().trim().toLowerCase() : null;
        customerDTO.setEmail(email);
        if (email == null || email.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is required");
        }
        if (customerRepository.findByEmailNormalized(email).isPresent() ||
                adminRepository.findByEmailNormalized(email).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email is already in use");
        }
        CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
        return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
    }
}
