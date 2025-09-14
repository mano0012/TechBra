package com.techbra.bff.infrastructure.client;

import com.techbra.bff.infrastructure.dto.CustomerDto;
import com.techbra.bff.infrastructure.dto.AuthRequest;
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

}