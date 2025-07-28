package com.bookstore.booksmanagementsystem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoBooksFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public NoBooksFoundException(String message) {
		super(message);
	}
}
