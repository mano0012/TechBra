package com.techbra.bff.web.controller;

import com.techbra.bff.application.dto.LoginRequest;
import com.techbra.bff.application.dto.LoginResponse;
import com.techbra.bff.domain.port.in.AuthenticationUseCase;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
// TODO: Adicionar a URL do front que vai ter permissão para chamar
// @CrossOrigin(origins = {"http://localhost:8080", "https://app.cliente.com"})
public class AuthController {

    private final AuthenticationUseCase authenticationUseCase;

    public AuthController(AuthenticationUseCase authenticationUseCase) {
        this.authenticationUseCase = authenticationUseCase;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authenticationUseCase.login(request);
        return ResponseEntity.ok(response);
    }

    /*
     * @PostMapping("/logout")
     * public ResponseEntity<Void> logout(@RequestHeader("Authorization") String
     * token) {
     * String jwtToken = token.replace("Bearer ", "");
     * authenticationUseCase.logout(jwtToken);
     * return ResponseEntity.ok().build();
     * }
     */
}