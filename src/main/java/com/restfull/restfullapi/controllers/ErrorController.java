package com.restfull.restfullapi.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import com.restfull.restfullapi.dtos.WebResponse;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice(basePackages = "com.restfull.restfullapi.controllers")
public class ErrorController {
    
    @ExceptionHandler(value = {
        ConstraintViolationException.class
    })
    public ResponseEntity<WebResponse<String>> constrainViolationException(ConstraintViolationException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(WebResponse.<String>builder().errors(exception.getMessage()).build());     
    }

    @ExceptionHandler(value = {
        ResponseStatusException.class
    })
    public ResponseEntity<WebResponse<String>> apiException(ResponseStatusException exception) {
       return ResponseEntity.status(exception.getStatusCode()).body(WebResponse.<String>builder().errors(exception.getReason()).build());
    }
}
