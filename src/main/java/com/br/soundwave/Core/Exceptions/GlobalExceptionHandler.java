package com.br.soundwave.Core.Exceptions;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(ex.getMessage());
    }
    
    @ExceptionHandler(NotUsingMFAExcpetion.class)
    public ResponseEntity<String> handleBadRequest(NotUsingMFAExcpetion ex) {
        return ResponseEntity.status(HttpStatus.SC_TEMPORARY_REDIRECT).body(ex.getMessage());
    }

}
