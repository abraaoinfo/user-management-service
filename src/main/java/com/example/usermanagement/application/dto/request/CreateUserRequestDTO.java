package com.example.usermanagement.application.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.jspecify.annotations.Nullable;

/**
 * Traditional DTO approach for Create User Request
 * Alternative to using records in domain models
 */
public record CreateUserRequestDTO(
    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name,
    
    @NotBlank(message = "Email is mandatory")
    @Email(message = "Email should be valid")
    String email,
    
    @NotBlank(message = "CPF is mandatory")
    @Pattern(regexp = "\\d{11}", message = "CPF must be 11 digits")
    String cpf,
    
    @Nullable
    @Pattern(regexp = "\\d{8}", message = "Postal code must be 8 digits")
    String postalCode
) {
    // Factory method for conversion
    public static CreateUserRequestDTO from(String name, String email, String cpf, @Nullable String postalCode) {
        return new CreateUserRequestDTO(name, email, cpf, postalCode);
    }
}
