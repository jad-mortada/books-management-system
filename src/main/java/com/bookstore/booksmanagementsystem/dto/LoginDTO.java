package com.bookstore.booksmanagementsystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class LoginDTO {
	@NotEmpty(message = "Email cannot be empty")
	@Email(message = "Email should be valid")
	private String email;

	@NotEmpty(message = "Password cannot be empty")
	@Size(min = 6, message = "Password must be at least 6 characters long")
	private String password;

	public LoginDTO() {
	}

	public LoginDTO(String email, String password) {
		this.email = email;
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
