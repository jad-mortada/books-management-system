package com.bookstore.booksmanagementsystem.exception;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Custom exceptions
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<String> handleDuplicateResource(DuplicateResourceException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    // Authentication/Authorization exceptions
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> handleAuthenticationException(AuthenticationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
               .body("Authentication failed: " + ex.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
               .body("Invalid credentials: " + ex.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
        String message = ex.getMessage();
        if (message == null || message.isEmpty() || message.contains("Access Denied")) {
            message = "Only admins can perform this action";
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
               .body("Access denied: " + message);
    }

    // Validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        StringBuilder errors = new StringBuilder();
        ex.getBindingResult().getFieldErrors()
          .forEach(error -> errors.append(error.getField())
                        .append(": ").append(error.getDefaultMessage()).append("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body("Validation errors: " + errors);
    }

    // Database exceptions
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<String> handleDataAccessErrors(DataAccessException ex) {
        // Provide a user-friendly message for database errors
        return ResponseEntity.status(HttpStatus.CONFLICT)
               .body("Database error: Possible duplicate or constraint violation");
    }

    // Catch-all for any other exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAllExceptions(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
               .body("An unexpected error occurred");
    }
}
