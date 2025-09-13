package com.techbra.bff.infrastructure.client;

import com.techbra.bff.infrastructure.dto.CustomerDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "customer-service", url = "${microservices.customer-service.url}")
public interface CustomerServiceClient {
    
    @PostMapping("/api/customers/authenticate")
    CustomerDto authenticate(@RequestBody AuthRequest request);
    
    @GetMapping("/api/customers/{id}")
    CustomerDto getCustomer(@PathVariable String id);
    
    class AuthRequest {
        private String email;
        private String password;
        
        public AuthRequest() {}
        
        public AuthRequest(String email, String password) {
            this.email = email;
            this.password = password;
        }
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }
}