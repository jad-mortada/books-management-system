package com.bookstore.booksmanagementsystem.controller;

import com.bookstore.booksmanagementsystem.dto.AuthResponseDTO;
import com.bookstore.booksmanagementsystem.dto.CustomerDTO;
import com.bookstore.booksmanagementsystem.dto.LoginDTO;
import com.bookstore.booksmanagementsystem.security.JwtTokenProvider;
import com.bookstore.booksmanagementsystem.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthenticationManager authenticationManager;
	private final CustomerService customerService;
	private final JwtTokenProvider tokenProvider;

	public AuthController(AuthenticationManager authenticationManager, CustomerService customerService,
			JwtTokenProvider tokenProvider) {
		this.authenticationManager = authenticationManager;
		this.customerService = customerService;
		this.tokenProvider = tokenProvider;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponseDTO> authenticateUser(@Valid @RequestBody LoginDTO loginDto) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = tokenProvider.createToken(authentication);
		return ResponseEntity.ok(new AuthResponseDTO(jwt));
	}

	@PostMapping("/register")
	public ResponseEntity<CustomerDTO> registerCustomer(@Valid @RequestBody CustomerDTO customerDTO) {
		CustomerDTO createdCustomer = customerService.createCustomer(customerDTO);
		return new ResponseEntity<>(createdCustomer, HttpStatus.CREATED);
	}
}
