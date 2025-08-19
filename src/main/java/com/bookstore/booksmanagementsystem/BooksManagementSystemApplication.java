package com.bookstore.booksmanagementsystem;

import com.bookstore.booksmanagementsystem.entity.Admin;
import com.bookstore.booksmanagementsystem.repository.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BooksManagementSystemApplication {

    private static final Logger log = LoggerFactory.getLogger(BooksManagementSystemApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(BooksManagementSystemApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		return new ModelMapper();
	}

	@Bean
	public CommandLineRunner createDefaultSuperAdmin(AdminRepository adminRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			if (adminRepository.findByEmail("superadmin@bookstore.com").isEmpty()) {
				Admin superAdmin = new Admin();
				superAdmin.setFirstName("Super");
				superAdmin.setLastName("Admin");
				superAdmin.setEmail("superadmin@bookstore.com");
				superAdmin.setPassword(passwordEncoder.encode("secureAdminPass123!"));
				superAdmin.setPhoneNumber("111-222-3333");
				superAdmin.setRoles("ROLE_SUPER_ADMIN");
				adminRepository.save(superAdmin);
				log.info("Default super admin created in admins table: superadmin@bookstore.com");
			} else {
				log.info("Default super admin already exists in admins table.");
			}
		};
	}
}
