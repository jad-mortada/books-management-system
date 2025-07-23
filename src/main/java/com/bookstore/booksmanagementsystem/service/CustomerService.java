package com.bookstore.booksmanagementsystem.service;

import com.bookstore.booksmanagementsystem.dto.CustomerDTO;
import java.util.List;

public interface CustomerService {
	CustomerDTO createCustomer(CustomerDTO customerDTO);

	CustomerDTO getCustomerById(Long id);

	List<CustomerDTO> getAllCustomers();

	CustomerDTO updateCustomer(Long id, CustomerDTO customerDTO);

	void deleteCustomer(Long id);

	CustomerDTO updateCustomerRole(Long id, String newRole);
}
