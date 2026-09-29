package com.example.wagetrack.exception;

public class InvalidPaymentException extends RuntimeException {
    public InvalidPaymentException(String message) { super(message); }
}
