package com.bookstore.booksmanagementsystem.service;

import com.bookstore.booksmanagementsystem.dto.AdminDTO;

import java.util.List;

public interface AdminService {
    List<AdminDTO> getAllAdmins();
    AdminDTO createAdmin(AdminDTO adminDTO);
    AdminDTO updateAdmin(Long id, AdminDTO adminDTO);
    void deleteAdmin(Long id);
    AdminDTO getCurrentAdmin();
}
