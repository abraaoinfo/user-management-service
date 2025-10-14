package com.example.usermanagement.application.service;

import com.example.usermanagement.domain.model.User;
import com.example.usermanagement.domain.repository.UserRepository;
import com.example.usermanagement.domain.service.UserService;
import com.example.usermanagement.infrastructure.client.ViaCepClient;
import com.example.usermanagement.infrastructure.web.UserApi;
import io.micrometer.observation.annotation.Observed;
import org.jspecify.annotations.NullMarked;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@NullMarked
@Observed(name = "user.service")
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ViaCepClient viaCepClient;
    private final Executor virtualThreadExecutor;

    public UserServiceImpl(UserRepository userRepository,
                           ViaCepClient viaCepClient,
                           Executor virtualThreadExecutor) {
        this.userRepository = Objects.requireNonNull(userRepository);
        this.viaCepClient = Objects.requireNonNull(viaCepClient);
        this.virtualThreadExecutor = Objects.requireNonNull(virtualThreadExecutor);
    }

    @Override
    @Transactional
    @Observed(name = "user.create")
    public Optional<User.UserRecord> createUser(User.CreateUserRequest request) {
        Objects.requireNonNull(request);

        if (userRepository.existsByEmail(request.email())) return Optional.empty();
        if (userRepository.existsByCpf(request.cpf()))   return Optional.empty();

        var user = User.from(request);

        var postal = request.postalCode();
        if (postal != null && !postal.isBlank()) {
            viaCepClient.createAddressFromPostalCode(postal).ifPresent(user::updateAddress);
        }

        return Optional.of(userRepository.save(user).toRecord());
    }

    @Override
    @Transactional
    @Observed(name = "user.update")
    public Optional<User.UserRecord> updateUser(Long id, User.UpdateUserRequest request) {
        Objects.requireNonNull(id);
        Objects.requireNonNull(request);

        return userRepository.findById(id).map(user -> {
            updateUserFields(user, request);

            var postal = request.postalCode();
            if (postal != null && !postal.isBlank()) {
                viaCepClient.createAddressFromPostalCode(postal).ifPresent(user::updateAddress);
            }

            return userRepository.save(user).toRecord();
        });
    }

    @Override
    @Transactional(readOnly = true)
    @Observed(name = "user.find-by-id")
    public Optional<User.UserRecord> findById(Long id) {
        Objects.requireNonNull(id);
        return userRepository.findById(id).map(User::toRecord);
    }

    @Override
    @Transactional(readOnly = true)
    @Observed(name = "user.find-by-email")
    public Optional<User.UserRecord> findByEmail(String email) {
        Objects.requireNonNull(email);
        return userRepository.findByEmail(email).map(User::toRecord);
    }

    @Override
    @Transactional(readOnly = true)
    @Observed(name = "user.find-all")
    public Page<User.UserRecord> findAll(Pageable pageable) {
        Objects.requireNonNull(pageable);
        return userRepository.findAll(pageable).map(User::toRecord);
    }

    @Override
    @Transactional(readOnly = true)
    @Observed(name = "user.search-by-name")
    public Page<User.UserRecord> searchByName(String name, Pageable pageable) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(pageable);
        return userRepository.findByNameContainingIgnoreCase(name, pageable).map(User::toRecord);
    }

    @Override
    @Transactional
    @Observed(name = "user.delete")
    public boolean deleteUser(Long id) {
        Objects.requireNonNull(id);
        if (!userRepository.existsById(id)) return false;
        userRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    @Observed(name = "user.validate-address")
    public Optional<User.UserRecord> validateAndEnrichAddress(Long userId, String postalCode) {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(postalCode);

        return userRepository.findById(userId).map(user -> {
            viaCepClient.createAddressFromPostalCode(postalCode).ifPresent(user::updateAddress);
            return userRepository.save(user).toRecord();
        });
    }

    @Override
    @Transactional(readOnly = true)
    @Observed(name = "user.get-stats")
    public UserApi.UserStats getUserStats() {
        var total = userRepository.count();
        var withAddress = userRepository.findUsersWithCompleteAddress(Pageable.unpaged()).getTotalElements();
        return UserApi.UserStats.of(total, withAddress);
    }

    private void updateUserFields(User user, User.UpdateUserRequest request) {
        if (request.name() != null && !request.name().isBlank()) {
            user.setName(request.name());
        }
        if (request.email() != null && !request.email().isBlank()) {
            if (!userRepository.existsByEmail(request.email())) {
                user.setEmail(request.email());
            }
        }
    }

    @Observed(name = "user.process-batch-virtual-threads")
    public CompletableFuture<String> createUserLote(List<User.CreateUserRequest> requests) {
        Objects.requireNonNull(requests);
        return CompletableFuture.supplyAsync(() -> {
            var futures = requests.stream().map(req ->
                    CompletableFuture.supplyAsync(() -> {
                        var user = User.from(req);
                        var saved = userRepository.save(user);
                        return "Created: " + saved.getName();
                    }, virtualThreadExecutor)
            ).toList();

            var results = futures.stream().map(CompletableFuture::join).toList();
            return "Processed " + results.size() + " users: " + String.join(", ", results);
        }, virtualThreadExecutor);
    }

    @Observed(name = "user.async-address-validation")
    public CompletableFuture<String> validateMultipleAddressesAsync(List<String> postalCodes) {
        Objects.requireNonNull(postalCodes);
        var futures = postalCodes.stream().map(code ->
                CompletableFuture.supplyAsync(() ->
                                viaCepClient.createAddressFromPostalCode(code).isPresent() ? "Valid: " + code : "Invalid: " + code,
                        virtualThreadExecutor
                )
        ).toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> "Address validation completed: " +
                        String.join(", ", futures.stream().map(CompletableFuture::join).toList()));
    }
}
