package com.example.usermanagement.infrastructure.client;

import com.example.usermanagement.domain.model.Address;
import io.micrometer.observation.annotation.Observed;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

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
    
    @Observed(name = "viacep.find-address")
    public Optional<Address.ViaCepResponse> findAddress(@Nullable String postalCode) {
        try {
            String cleanPostalCode = postalCode.replaceAll("[^\\d]", "");
            
            if (cleanPostalCode.length() != 8) {
                return Optional.empty();
            }
            
            var response = restClient
                .get()
                .uri("/{postalCode}/json", cleanPostalCode)
                .retrieve()
                .body(Address.ViaCepResponse.class);
            
            return Optional.ofNullable(response)
                .filter(r -> r.erro() == null); // Filter out error responses
                
        } catch (RestClientException e) {
            System.err.println("Error calling ViaCEP API for postal code: " + postalCode);
            return Optional.empty();
        }
    }
    
    public boolean isValidPostalCode(String postalCode) {
        return findAddress(postalCode).isPresent();
    }
    
    public Optional<Address> createAddressFromPostalCode(@Nullable String postalCode) {
        return findAddress(postalCode)
            .map(Address::fromViaCep);
    }
}
