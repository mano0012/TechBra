package com.techbra.bff.domain.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends DomainException {
    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
    }
}