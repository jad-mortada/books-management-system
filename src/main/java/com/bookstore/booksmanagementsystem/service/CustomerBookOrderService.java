package com.bookstore.booksmanagementsystem.service;

import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderDTO;
import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderItemDTO;

import java.util.List;

public interface CustomerBookOrderService {
	CustomerBookOrderDTO createCustomerBookOrder(CustomerBookOrderDTO orderDTO);

	CustomerBookOrderDTO getCustomerBookOrderById(Long id);

	List<CustomerBookOrderDTO> getAllCustomerBookOrders();

	List<CustomerBookOrderDTO> getCustomerBookOrdersByCustomerId(Long customerId);

	CustomerBookOrderDTO updateCustomerBookOrder(Long id, CustomerBookOrderDTO orderDTO);

	void deleteCustomerBookOrder(Long id);

	CustomerBookOrderDTO generatePersonalizedOrder(CustomerBookOrderDTO orderDTO);
}
