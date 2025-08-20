package com.bookstore.booksmanagementsystem.controller;
import com.bookstore.booksmanagementsystem.dto.ProfileDTO;
import com.bookstore.booksmanagementsystem.entity.Admin;
import com.bookstore.booksmanagementsystem.entity.Customer;
import com.bookstore.booksmanagementsystem.repository.AdminRepository;
import com.bookstore.booksmanagementsystem.repository.CustomerRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfileController(AdminRepository adminRepository, CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
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

    @PutMapping("/password")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest body, Authentication authentication) {
        if (body == null || !StringUtils.hasText(body.getCurrentPassword()) || !StringUtils.hasText(body.getNewPassword())) {
            return ResponseEntity.badRequest().body("Invalid request body");
        }
        String email = authentication.getName();

        // Admin first
        Admin admin = adminRepository.findByEmailNormalized(email).orElse(null);
        if (admin != null) {
            if (!passwordEncoder.matches(body.getCurrentPassword(), admin.getPassword())) {
                return ResponseEntity.status(400).body("Current password is incorrect");
            }
            admin.setPassword(passwordEncoder.encode(body.getNewPassword()));
            adminRepository.save(admin);
            return ResponseEntity.ok().build();
        }

        Customer customer = customerRepository.findByEmailNormalized(email).orElse(null);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        if (!passwordEncoder.matches(body.getCurrentPassword(), customer.getPassword())) {
            return ResponseEntity.status(400).body("Current password is incorrect");
        }
        customer.setPassword(passwordEncoder.encode(body.getNewPassword()));
        customerRepository.save(customer);
        return ResponseEntity.ok().build();
    }

    @PostMapping(value = "/avatar", consumes = {"multipart/form-data"})
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public ResponseEntity<?> uploadAvatar(@RequestPart("file") MultipartFile file, Authentication authentication, HttpServletRequest request) throws IOException {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("No file uploaded");
        }
        String email = authentication.getName();

        // Resolve current user (admin first)
        Admin admin = adminRepository.findByEmailNormalized(email).orElse(null);
        boolean isAdmin = admin != null;
        Long userId;
        String userTypeFolder;
        if (isAdmin) {
            userId = admin.getId();
            userTypeFolder = "admins";
        } else {
            Customer customer = customerRepository.findByEmailNormalized(email).orElse(null);
            if (customer == null) return ResponseEntity.notFound().build();
            userId = customer.getId();
            userTypeFolder = "customers";
        }

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "avatar" : file.getOriginalFilename());
        String filename = userId + "_" + Instant.now().toEpochMilli() + "_" + original;
        Path uploadBase = Paths.get("uploads", "avatars", userTypeFolder);
        Files.createDirectories(uploadBase);
        Path target = uploadBase.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        String baseUrl = request.getScheme() + "://" + request.getServerName() + (request.getServerPort() != 80 && request.getServerPort() != 443 ? ":" + request.getServerPort() : "");
        String avatarUrl = baseUrl + "/uploads/avatars/" + userTypeFolder + "/" + filename;

        if (isAdmin) {
            admin.setAvatarUrl(avatarUrl);
            adminRepository.save(admin);
        } else {
            Customer customer = customerRepository.findByEmailNormalized(email).orElseThrow();
            customer.setAvatarUrl(avatarUrl);
            customerRepository.save(customer);
        }

        return ResponseEntity.ok().body(new AvatarResponse(avatarUrl));
    }

    @DeleteMapping("/avatar")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN','ADMIN','USER')")
    public ResponseEntity<?> deleteAvatar(Authentication authentication) {
        String email = authentication.getName();

        // Try admin first
        Admin admin = adminRepository.findByEmailNormalized(email).orElse(null);
        if (admin != null) {
            admin.setAvatarUrl(null);
            adminRepository.save(admin);
            return ResponseEntity.ok().build();
        }

        Customer customer = customerRepository.findByEmailNormalized(email).orElse(null);
        if (customer == null) {
            return ResponseEntity.notFound().build();
        }
        customer.setAvatarUrl(null);
        customerRepository.save(customer);
        return ResponseEntity.ok().build();
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
        dto.setAvatarUrl(admin.getAvatarUrl());
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
        dto.setAvatarUrl(c.getAvatarUrl());
        return dto;
    }

    public static class ChangePasswordRequest {
        private String currentPassword;
        private String newPassword;

        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class AvatarResponse {
        private String avatarUrl;
        public AvatarResponse(String avatarUrl) { this.avatarUrl = avatarUrl; }
        public String getAvatarUrl() { return avatarUrl; }
        public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
    }
}
