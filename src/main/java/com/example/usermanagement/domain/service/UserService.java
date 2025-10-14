package com.example.usermanagement.domain.service;

import com.example.usermanagement.domain.model.User;
import com.example.usermanagement.infrastructure.web.UserApi;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@NullMarked
public interface UserService {

    Optional<User.UserRecord> createUser(User.@Nullable CreateUserRequest request);
    

    Optional<User.UserRecord> updateUser(@Nullable Long id, User.@Nullable UpdateUserRequest request);
    

    Optional<User.UserRecord> findById(@Nullable Long id);
    

    Optional<User.UserRecord> findByEmail(@Nullable String email);
    

    Page<User.UserRecord> findAll(Pageable pageable);
    

    Page<User.UserRecord> searchByName(@Nullable String name, Pageable pageable);
    

    boolean deleteUser(@Nullable  Long id);
    

    Optional<User.UserRecord> validateAndEnrichAddress(Long userId, @Nullable String postalCode);

    CompletableFuture<String> createUserLote(List<User.CreateUserRequest> requests);

    CompletableFuture<String> validateMultipleAddressesAsync(List<String> postalCodes);

    UserApi.UserStats getUserStats();
}
