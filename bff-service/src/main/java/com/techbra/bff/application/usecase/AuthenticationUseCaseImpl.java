package com.techbra.bff.application.usecase;

import com.techbra.bff.application.dto.LoginRequest;
import com.techbra.bff.application.dto.LoginResponse;
import com.techbra.bff.domain.model.User;
import com.techbra.bff.domain.port.in.AuthenticationUseCase;
import com.techbra.bff.domain.port.out.AuthenticationPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationUseCaseImpl implements AuthenticationUseCase {

    private final AuthenticationPort authenticationPort;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public AuthenticationUseCaseImpl(AuthenticationPort authenticationPort) {
        this.authenticationPort = authenticationPort;
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = authenticationPort.authenticate(request.getEmail(), request.getPassword());

        if (user == null) {
            throw new RuntimeException("Credenciais inválidas");
        }

        String token = authenticationPort.generateToken(user);

        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getName(),
                user.getRole(),
                jwtExpiration);
    }

    @Override
    public boolean validateToken(String token) {
        return authenticationPort.validateToken(token);
    }

    @Override
    public void logout(String token) {
        // TODO: implementar cache de tokens e remover/invalidar o token
    }
}