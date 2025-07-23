package com.bookstore.booksmanagementsystem;

import com.bookstore.booksmanagementsystem.entity.Customer;
import com.bookstore.booksmanagementsystem.repository.CustomerRepository;
import org.modelmapper.ModelMapper;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class BooksManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BooksManagementSystemApplication.class, args);
    }

    @Bean
    public ModelMapper modelMapper(){
        return new ModelMapper();
    }

    @Bean
    public CommandLineRunner createDefaultAdmin(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (customerRepository.findByEmail("superadmin@bookstore.com").isEmpty()) {
                Customer admin = new Customer();
                admin.setFirstName("Super");
                admin.setLastName("Admin");
                admin.setEmail("superadmin@bookstore.com");
                admin.setPassword(passwordEncoder.encode("secureAdminPass123!"));
                admin.setPhoneNumber("111-222-3333");
                admin.setRoles("ROLE_ADMIN");
                customerRepository.save(admin);
                System.out.println("Default admin user created: superadmin@bookstore.com");
            } else {
                System.out.println("Default admin user already exists.");
            }
        };
    }
}
