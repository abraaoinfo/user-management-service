package com.example.usermanagement.infrastructure.web;

import com.example.usermanagement.domain.model.User;
import com.example.usermanagement.domain.service.UserService;
import io.micrometer.observation.annotation.Observed;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api/v1/users")
@NullMarked
@Observed(name = "user.controller")
public class UserController implements UserApi {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }



    @Override
    @PostMapping
    @Observed(name = "user.create")
    public ResponseEntity<User.UserRecord> createUser(@RequestBody User.CreateUserRequest request) {

        var userRecord = userService.createUser(request);

        // For now, return a simple error response
        return userRecord.map(record -> ResponseEntity
                .status(HttpStatus.CREATED).body(record))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.CONFLICT).build());

    }



    @Override
    @GetMapping("/{id}")
    @Observed(name = "user.get-by-id")
    public ResponseEntity<User.UserRecord> getUserById(@PathVariable Long id) {
        var userRecord = userService.findById(id);

        return userRecord.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }



    @GetMapping("/by-email")
    @Observed(name = "user.get-by-email")
    public ResponseEntity<?> getUserByEmail(@RequestParam @Nullable String email) {
        var userRecord = userService.findByEmail(email);

        if (userRecord.isEmpty()) {
            var problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    "User not found with email: " + email
            );
            problem.setTitle("User Not Found");
            problem.setProperty("email", email);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
        }

        return ResponseEntity.ok(userRecord.get());
    }



    @Override
    @GetMapping
    @Observed(name = "user.get-all")
    public ResponseEntity<Page<User.UserRecord>> getAllUsers(Pageable pageable) {
        var users = userService.findAll(pageable);
        return ResponseEntity.ok(users);
    }



    @GetMapping("/search")
    @Observed(name = "user.search")
    public ResponseEntity<Page<User.UserRecord>> searchUsers(@RequestParam @Nullable String name,
                                                             Pageable pageable) {
        var users = userService.searchByName(name, pageable);
        return ResponseEntity.ok(users);
    }



    @Override
    @PutMapping("/{id}")
    @Observed(name = "user.update")
    public ResponseEntity<User.UserRecord> updateUser(@PathVariable Long id,
                                                      @RequestBody User.UpdateUserRequest request) {
        var userRecord = userService.updateUser(id, request);

        return userRecord.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());

    }

    @PostMapping("/{id}/validate-address")
    @Observed(name = "user.validate-address")
    public ResponseEntity<?> validateAddress(@PathVariable Long id,
                                             @RequestParam @Nullable String postalCode) {
        var userRecord = userService.validateAndEnrichAddress(id, postalCode);

        if (userRecord.isEmpty()) {
            var problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.NOT_FOUND,
                    "User not found with ID: " + id
            );
            problem.setTitle("User Not Found");
            problem.setProperty("userId", id);

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
        }

        return ResponseEntity.ok(userRecord.get());
    }

    /**
     * Delete user
     */
    @Override
    @DeleteMapping("/{id}")
    @Observed(name = "user.delete")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean deleted = userService.deleteUser(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }


    @Override
    @GetMapping("/stats")
    @Observed(name = "user.stats")
    public ResponseEntity<UserApi.UserStats> getUserStats() {
        var stats = userService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    @PostMapping("/batch-process")
    @Observed(name = "user.batch-process")
    public java.util.concurrent.CompletableFuture<ResponseEntity<String>> processBatchUsers(@RequestBody java.util.List<User.CreateUserRequest> requests) {
        return userService.createUserLote(requests)
                .thenApply(ResponseEntity::ok);
    }

    @PostMapping("/validate-addresses")
    @Observed(name = "user.validate-addresses")
    public java.util.concurrent.CompletableFuture<ResponseEntity<String>> validateAddresses(@RequestBody java.util.List<String> postalCodes) {
        return userService.validateMultipleAddressesAsync(postalCodes)
                .thenApply(ResponseEntity::ok);
    }
}
