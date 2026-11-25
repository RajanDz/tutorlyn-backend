package com.tutorlyn.tutorlyn.controller;

import com.tutorlyn.tutorlyn.exception.InvalidRefreshTokenException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionController {


    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ProblemDetail invalidRefreshToken(InvalidRefreshTokenException ex) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,ex.getMessage());
    }
}
