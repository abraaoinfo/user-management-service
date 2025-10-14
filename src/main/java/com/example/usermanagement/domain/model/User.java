package com.example.usermanagement.domain.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * User entity using modern Java patterns
 * Compatible with Java 21+ (uses records internally)
 */
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email", unique = true),
    @Index(name = "idx_user_cpf", columnList = "cpf", unique = true)
})
@Getter
@Setter
@NullMarked
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;
    
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "address_id")
    private @Nullable Address address;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Default constructor for JPA
    protected User() {}
    
    // Constructor for creating new users
    public User(String name, String email, String cpf) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.cpf = Objects.requireNonNull(cpf, "CPF cannot be null");
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Factory method using record pattern
    public static User from(CreateUserRequest request) {
        return new User(request.name(), request.email(), request.cpf());
    }
    
    // Business logic using modern Java patterns
    public void updateBasicInfo(String name, String email) {
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.email = Objects.requireNonNull(email, "Email cannot be null");
        this.updatedAt = LocalDateTime.now();
    }
    
    public void updateAddress(@Nullable Address address) {
        this.address = address;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean hasAddress() {
        return address != null && address.isComplete();
    }
    
    public boolean hasValidPostalCode() {
        return address != null && address.getPostalCode() != null && 
               address.getPostalCode().matches("\\d{8}");
    }
    
    // Convert to record for API responses
    public UserRecord toRecord() {
        return new UserRecord(
            id, name, email, cpf, 
            address != null ? address.toRecord() : null,
            createdAt, updatedAt
        );
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        User user = (User) obj;
        return Objects.equals(id, user.id) && 
               Objects.equals(email, user.email);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
    
    @Override
    public String toString() {
        return "User{id=%d, name='%s', email='%s'}".formatted(id, name, email);
    }
    
    /**
     * Record for API responses - immutable and modern
     */
    public record UserRecord(
        Long id,
        String name,
        String email,
        String cpf,
        Address.@Nullable AddressRecord address,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        public static UserRecord from(User user) {
            return new UserRecord(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getAddress() != null ? user.getAddress().toRecord() : null,
                user.getCreatedAt(),
                user.getUpdatedAt()
            );
        }
    }
    
    /**
     * Create user request record
     */
    public record CreateUserRequest(
        String name,
        String email,
        String cpf,
        @Nullable String postalCode
    ) {}
    
    /**
     * Update user request record
     */
    public record UpdateUserRequest(
        @Nullable String name,
        @Nullable String email,
        @Nullable String postalCode
    ) {}


}