package com.example.usermanagement.domain.repository;

import com.example.usermanagement.domain.model.User;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Modern repository interface using Spring Data JPA
 * Compatible with Java 21+ patterns
 */
@NullMarked
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email using Optional for null-safety
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by CPF using Optional for null-safety
     */
    Optional<User> findByCpf(String cpf);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if CPF exists
     */
    boolean existsByCpf(String cpf);
    
    /**
     * Find users by name containing (case-insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    /**
     * Find users with complete address
     */
    @Query("SELECT u FROM User u WHERE u.address IS NOT NULL AND u.address.city IS NOT NULL")
    Page<User> findUsersWithCompleteAddress(Pageable pageable);
    
    /**
     * Count users by city
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.address.city = :city")
    long countByCity(@Param("city") String city);
}
