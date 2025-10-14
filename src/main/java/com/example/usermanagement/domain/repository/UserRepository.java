package com.example.usermanagement.domain.repository;

import com.example.usermanagement.domain.model.User;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

@NullMarked
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    Optional<User> findByCpf(String cpf);
    
    boolean existsByEmail(String email);
    
    boolean existsByCpf(String cpf);
    
    @Query("SELECT u FROM User u WHERE LOWER(u.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<User> findByNameContainingIgnoreCase(@Param("name") String name, Pageable pageable);
    
    @Query("SELECT u FROM User u WHERE u.address IS NOT NULL AND u.address.city IS NOT NULL")
    Page<User> findUsersWithCompleteAddress(Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.address.city = :city")
    long countByCity(@Param("city") String city);
}
