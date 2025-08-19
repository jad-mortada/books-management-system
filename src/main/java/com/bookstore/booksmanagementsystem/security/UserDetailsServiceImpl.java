package com.bookstore.booksmanagementsystem.security;

import com.bookstore.booksmanagementsystem.entity.Admin;
import com.bookstore.booksmanagementsystem.entity.Customer;
import com.bookstore.booksmanagementsystem.repository.AdminRepository;
import com.bookstore.booksmanagementsystem.repository.CustomerRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AdminRepository adminRepository;
    private final CustomerRepository customerRepository;

    public UserDetailsServiceImpl(AdminRepository adminRepository, CustomerRepository customerRepository) {
        this.adminRepository = adminRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1) Try admin table first
        Admin admin = adminRepository.findByEmail(email).orElse(null);
        if (admin != null) {
            Collection<org.springframework.security.core.GrantedAuthority> authorities = Arrays
                    .stream(admin.getRoles().split(","))
                    .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.trim()))
                    .collect(Collectors.toList());
            return new org.springframework.security.core.userdetails.User(admin.getEmail(), admin.getPassword(), authorities);
        }

        // 2) Fallback to customers table
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        Collection<org.springframework.security.core.GrantedAuthority> authorities = Arrays
                .stream(customer.getRoles().split(","))
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.trim()))
                .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(customer.getEmail(), customer.getPassword(), authorities);
    }
}
