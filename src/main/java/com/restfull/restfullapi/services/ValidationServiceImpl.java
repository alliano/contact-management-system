package com.restfull.restfullapi.services;

import java.util.Set;

import org.springframework.stereotype.Service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.AllArgsConstructor;

@Service @AllArgsConstructor
public class ValidationServiceImpl implements ValidationService {

    private final Validator validator;

    @Override
    public void validate(Object request) {
        Set<ConstraintViolation<Object>> constraintViolations = this.validator.validate(request);
        if(constraintViolations.size() != 0) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }
    
}
