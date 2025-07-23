package com.bookstore.booksmanagementsystem.security;

import com.bookstore.booksmanagementsystem.entity.Customer;
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

    private final CustomerRepository customerRepository;

    public UserDetailsServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Customer not found with email: " + email));

        Collection<org.springframework.security.core.GrantedAuthority> authorities =
                Arrays.stream(customer.getRoles().split(","))
                        .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.trim()))
                        .collect(Collectors.toList());

        return new org.springframework.security.core.userdetails.User(
                customer.getEmail(),
                customer.getPassword(),
                authorities
        );
    }
}
