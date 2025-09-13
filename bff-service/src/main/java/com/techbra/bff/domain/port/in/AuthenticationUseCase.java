package com.techbra.bff.domain.port.in;

import com.techbra.bff.application.dto.LoginRequest;
import com.techbra.bff.application.dto.LoginResponse;

public interface AuthenticationUseCase {
    LoginResponse login(LoginRequest request);
    boolean validateToken(String token);
    void logout(String token);
}