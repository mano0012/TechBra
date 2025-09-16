package com.techbra.customer.infrastructure.config;

import com.techbra.customer.application.UserApplicationService;
import com.techbra.customer.domain.PasswordEncoder;
import com.techbra.customer.domain.UserRepository;
import com.techbra.customer.domain.UserService;
import com.techbra.customer.infrastructure.persistence.UserRepositoryImpl;
import com.techbra.customer.infrastructure.security.SpringPasswordEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuração de beans para injeção de dependências
 */
@Configuration
public class BeanConfig {
    

    
    @Bean
    public UserService userService(UserRepository userRepository, org.springframework.security.crypto.password.PasswordEncoder springPasswordEncoder) {
        PasswordEncoder passwordEncoder = new SpringPasswordEncoder(springPasswordEncoder);
        return new UserService(userRepository, passwordEncoder);
    }
    
    @Bean
    public UserApplicationService userApplicationService(UserService userService) {
        return new UserApplicationService(userService);
    }
}