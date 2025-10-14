package com.example.usermanagement.infrastructure.client;

import com.example.usermanagement.domain.model.Address;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

/**
 * Modern RestClient for ViaCEP integration using Java 25 patterns
 * Uses @Observed for observability and virtual threads
 */
@Component
@NullMarked
@Observed(name = "viacep.client")
public class ViaCepClient {
    
    private final RestClient restClient;
    
    public ViaCepClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder
            .baseUrl("https://viacep.com.br/ws")
            .defaultHeader("Accept", "application/json")
            .build();
    }
    
    /**
     * Fetches address data from ViaCEP API using modern patterns
     * Returns Optional for null-safety
     */
    @Observed(name = "viacep.find-address")
    public Optional<Address.ViaCepResponse> findAddress(@Nullable String postalCode) {
        try {
            // Clean postal code (remove dashes and spaces)
            String cleanPostalCode = postalCode.replaceAll("[^\\d]", "");
            
            if (cleanPostalCode.length() != 8) {
                return Optional.empty();
            }
            
            // Use modern RestClient with pattern matching
            var response = restClient
                .get()
                .uri("/{postalCode}/json", cleanPostalCode)
                .retrieve()
                .body(Address.ViaCepResponse.class);
            
            return Optional.ofNullable(response)
                .filter(r -> r.erro() == null); // Filter out error responses
                
        } catch (RestClientException e) {
            // Log error but don't expose internal details
            System.err.println("Error calling ViaCEP API for postal code: " + postalCode);
            return Optional.empty();
        }
    }
    
    /**
     * Validates if postal code exists in ViaCEP
     * Uses modern Java patterns
     */
    public boolean isValidPostalCode(String postalCode) {
        return findAddress(postalCode).isPresent();
    }
    
    /**
     * Creates Address entity from postal code
     * Uses functional programming patterns
     */
    public Optional<Address> createAddressFromPostalCode(@Nullable String postalCode) {
        return findAddress(postalCode)
            .map(Address::fromViaCep);
    }
}
