package com.micro.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(InvalidSSNException.class)
	public ResponseEntity<String> handleInvalidSsnException(InvalidSSNException ex) {
		return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
	}

//	@ExceptionHandler(Exception.class)
//	public ResponseEntity<String> handleAllExceptions(Exception ex) {
//		return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
//	}
	@ExceptionHandler(ExternalServiceException.class)
	public ResponseEntity<Map<String, String>> handleExternalServiceError(ExternalServiceException ex) {
	    Map<String, String> errorResponse = new HashMap<>();
	    errorResponse.put("message", ex.getMessage());
	    errorResponse.put("status", "SERVICE_UNAVAILABLE");
	    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(errorResponse);
	}

}
