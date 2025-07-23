package com.bookstore.booksmanagementsystem.exception;

import java.time.LocalDateTime;

public class ErrorDetails {
    private LocalDateTime timestamp;
    private String message;
    private String details;
    private int statusCode;

    public ErrorDetails(LocalDateTime timestamp, String message, String details, int statusCode) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
        this.statusCode = statusCode;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
