package com.bookstore.booksmanagementsystem.service.impl;

import com.bookstore.booksmanagementsystem.dto.CustomerDTO;
import com.bookstore.booksmanagementsystem.entity.Customer;
import com.bookstore.booksmanagementsystem.exception.DuplicateResourceException;
import com.bookstore.booksmanagementsystem.exception.ResourceNotFoundException;
import com.bookstore.booksmanagementsystem.repository.CustomerRepository;
import com.bookstore.booksmanagementsystem.repository.AdminRepository;
import com.bookstore.booksmanagementsystem.service.CustomerService;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {

    // Admin operations have been moved to AdminService; no admin-related methods remain here.

    @Override
    public CustomerDTO getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found for email: " + email));
        return modelMapper.map(customer, CustomerDTO.class);
    }

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        String email = customerDTO.getEmail() != null ? customerDTO.getEmail().trim().toLowerCase() : null;
        customerDTO.setEmail(email);
        if (customerRepository.findByEmailNormalized(email).isPresent()) {
            throw new DuplicateResourceException("Email is already in use");
        }
        // Also ensure email is not used by any admin/super admin
        if (adminRepository.findByEmailNormalized(email).isPresent()) {
            throw new DuplicateResourceException("Email is already in use");
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
        return customers.stream().map(customer -> modelMapper.map(customer, CustomerDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO) {
        Customer existingCustomer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer", "id", id));

        if (customerDTO.getEmail() != null && !customerDTO.getEmail().isEmpty()) {
            String email = customerDTO.getEmail().trim().toLowerCase();
            customerDTO.setEmail(email);
            customerRepository.findByEmailNormalized(email).ifPresent(c -> {
                if (!c.getId().equals(id)) {
                    throw new DuplicateResourceException("Email is already in use");
                }
            });
            // Also block switching to an email used by any admin
            adminRepository.findByEmailNormalized(email).ifPresent(a -> {
                throw new DuplicateResourceException("Email is already in use");
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

    // Customers remain ROLE_USER; role mutation APIs are handled elsewhere if needed.

    private final CustomerRepository customerRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AdminRepository adminRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, ModelMapper modelMapper,
            PasswordEncoder passwordEncoder, AdminRepository adminRepository) {
        this.customerRepository = customerRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.adminRepository = adminRepository;
    }
}
