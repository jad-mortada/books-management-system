package com.bookstore.booksmanagementsystem.service.impl;

import com.bookstore.booksmanagementsystem.dto.CustomerDTO;
import com.bookstore.booksmanagementsystem.entity.Customer;
import com.bookstore.booksmanagementsystem.exception.DuplicateResourceException;
import com.bookstore.booksmanagementsystem.exception.ResourceNotFoundException;
import com.bookstore.booksmanagementsystem.repository.CustomerRepository;
import com.bookstore.booksmanagementsystem.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper, PasswordEncoder passwordEncoder) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        if (customerRepository.findByEmail(customerDTO.getEmail()).isPresent()) {
            throw new DuplicateResourceException("Customer", "email", customerDTO.getEmail());
        }
        Customer customer = modelMapper.map(customerDTO, Customer.class);
        customer.setPassword(passwordEncoder.encode(customerDTO.getPassword()));
        customer.setRoles("ROLE_USER");
        Customer savedCustomer = customerRepository.save(customer);
        return modelMapper.map(savedCustomer, CustomerDTO.class);
    }

    @Override
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        return modelMapper.map(customer, CustomerDTO.class);
    }

    @Override
    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream()
                .map(customer -> modelMapper.map(customer, CustomerDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        if (customerDTO.getEmail() != null && !customerDTO.getEmail().isEmpty()) {
            customerRepository.findByEmail(customerDTO.getEmail()).ifPresent(c -> {
                if (!c.getId().equals(id)) {
                    throw new DuplicateResourceException("Customer", "email", customerDTO.getEmail());
                }
            });
        }

        existingCustomer.setFirstName(customerDTO.getFirstName());
        existingCustomer.setLastName(customerDTO.getLastName());
        existingCustomer.setEmail(customerDTO.getEmail());
        existingCustomer.setPhoneNumber(customerDTO.getPhoneNumber());
        if (customerDTO.getPassword() != null && !customerDTO.getPassword().isEmpty()) {
            existingCustomer.setPassword(passwordEncoder.encode(customerDTO.getPassword()));
        }

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return modelMapper.map(updatedCustomer, CustomerDTO.class);
    }

    @Override
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));
        customerRepository.delete(customer);
    }

    @Override
    public CustomerDTO updateCustomerRole(Long id, String newRole) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        String formattedRole = newRole.toUpperCase();
        if (!formattedRole.startsWith("ROLE_")) {
            formattedRole = "ROLE_" + formattedRole;
        }

        if (!formattedRole.equals("ROLE_USER") && !formattedRole.equals("ROLE_ADMIN")) {
            throw new IllegalArgumentException("Invalid role specified: " + newRole + ". Allowed roles are 'USER' and 'ADMIN'.");
        }

        customer.setRoles(formattedRole);
        Customer updatedCustomer = customerRepository.save(customer);
        return modelMapper.map(updatedCustomer, CustomerDTO.class);
    }
}
