package com.bookstore.booksmanagementsystem.security;

import com.bookstore.booksmanagementsystem.dto.CustomerDTO;
import com.bookstore.booksmanagementsystem.dto.CustomerBookOrderDTO;
import com.bookstore.booksmanagementsystem.service.CustomerService;
import com.bookstore.booksmanagementsystem.service.CustomerBookOrderService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service("securityService")
public class SecurityService {

    private final CustomerService customerService;
    private final CustomerBookOrderService customerBookOrderService;

    public SecurityService(CustomerService customerService, CustomerBookOrderService customerBookOrderService) {
        this.customerService = customerService;
        this.customerBookOrderService = customerBookOrderService;
    }

    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || !(authentication.getPrincipal() instanceof UserDetails)) {
            return null;
        }
        return ((UserDetails) authentication.getPrincipal()).getUsername();
    }

    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isCustomerOwner(Long customerId) {
        String authenticatedEmail = getAuthenticatedUserEmail();
        if (authenticatedEmail == null) {
            return false;
        }
        try {
            CustomerDTO customer = customerService.getCustomerById(customerId);
            return customer.getEmail().equals(authenticatedEmail);
        } catch (Exception e) {
            System.err.println("Error checking customer ownership for ID " + customerId + ": " + e.getMessage());
            return false;
        }
    }

    public boolean isCustomerOwnerOrAdmin(Long customerId) {
        return isAdmin() || isCustomerOwner(customerId);
    }

    public boolean isOrderOwner(Long orderId) {
        String authenticatedEmail = getAuthenticatedUserEmail();
        if (authenticatedEmail == null) {
            return false;
        }
        try {
            CustomerBookOrderDTO order = customerBookOrderService.getCustomerBookOrderById(orderId);
            CustomerDTO orderCustomer = customerService.getCustomerById(order.getCustomerId());
            return orderCustomer.getEmail().equals(authenticatedEmail);
        } catch (Exception e) {
            System.err.println("Error checking order ownership for ID " + orderId + ": " + e.getMessage());
            return false;
        }
    }
}
