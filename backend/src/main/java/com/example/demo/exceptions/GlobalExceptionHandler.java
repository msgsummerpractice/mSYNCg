package com.example.demo.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
	    ValidationException exception,
	    HttpServletRequest request
    ) {
	List<FieldValidationError> fieldErrors = new ArrayList<>();

	if (exception.getField() != null && !exception.getField().isBlank()) {
	    fieldErrors.add(new FieldValidationError(exception.getField(), exception.getReason()));
	}

	ErrorResponse response = new ErrorResponse(
		Instant.now(),
		HttpStatus.BAD_REQUEST.value(),
		"Validation Error",
		exception.getMessage(),
		request.getRequestURI(),
		fieldErrors
	);

	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
	    MethodArgumentNotValidException exception,
	    HttpServletRequest request
    ) {
	List<FieldValidationError> fieldErrors = exception.getBindingResult()
		.getFieldErrors()
		.stream()
		.map(this::toFieldValidationError)
		.toList();

	ErrorResponse response = new ErrorResponse(
		Instant.now(),
		HttpStatus.BAD_REQUEST.value(),
		"Validation Error",
		"Request validation failed",
		request.getRequestURI(),
		fieldErrors
	);

	return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
	    Exception exception,
	    HttpServletRequest request
    ) {
	ErrorResponse response = new ErrorResponse(
		Instant.now(),
		HttpStatus.INTERNAL_SERVER_ERROR.value(),
		"Internal Server Error",
		exception.getMessage(),
		request.getRequestURI(),
		List.of()
	);

	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private FieldValidationError toFieldValidationError(FieldError fieldError) {
	String reason = fieldError.getDefaultMessage() == null
		? "Invalid value"
		: fieldError.getDefaultMessage();

	return new FieldValidationError(fieldError.getField(), reason);
    }

    public record FieldValidationError(String field, String reason) {
    }

    public record ErrorResponse(
	    Instant timestamp,
	    int status,
	    String error,
	    String message,
	    String path,
	    List<FieldValidationError> fieldErrors
    ) {
    }
}
