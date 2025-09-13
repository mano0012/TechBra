package com.techbra.bff.domain.port.out;

import com.techbra.bff.domain.model.User;

public interface AuthenticationPort {
    User authenticate(String email, String password);
    String generateToken(User user);
    boolean validateToken(String token);
    User getUserFromToken(String token);
}