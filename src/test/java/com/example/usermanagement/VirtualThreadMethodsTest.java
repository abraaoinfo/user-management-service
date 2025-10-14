package com.example.usermanagement;

import com.example.usermanagement.application.service.UserServiceImpl;
import com.example.usermanagement.domain.model.User;
import com.example.usermanagement.domain.repository.UserRepository;
import com.example.usermanagement.infrastructure.client.ViaCepClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@SpringBootTest
class VirtualThreadMethodsTest {

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ViaCepClient viaCepClient;

    @Autowired
    private Executor virtualThreadExecutor;

    @Test
    void testProcessUsersWithVirtualThreads() throws Exception {
        System.out.println("=== Testing Virtual Threads for User Processing ===");
        
        var requests = List.of(
            new User.CreateUserRequest("João Silva", "joao@email.com", "12345678901", "01310100"),
            new User.CreateUserRequest("Maria Santos", "maria@email.com", "98765432109", "20040020"),
            new User.CreateUserRequest("Pedro Costa", "pedro@email.com", "11122233344", "30112000")
        );
        
        var startTime = System.currentTimeMillis();
        
        var result = userService.createUserLote(requests).get();
        
        var endTime = System.currentTimeMillis();
        
        System.out.println("Result: " + result);
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }

    @Test
    void testValidateAddressesWithVirtualThreads() throws Exception {
        System.out.println("\n=== Testing Virtual Threads for Address Validation ===");
        
        var postalCodes = List.of("01310100", "20040020", "30112000", "40070110", "50050130");
        
        var startTime = System.currentTimeMillis();
        
        var result = userService.validateMultipleAddressesAsync(postalCodes).get();
        
        var endTime = System.currentTimeMillis();
        
        System.out.println("Result: " + result);
        System.out.println("Time taken: " + (endTime - startTime) + "ms");
    }

    @Test
    void demonstrateVirtualThreadExecutor() throws Exception {
        System.out.println("\n=== Demonstrating Virtual Thread Executor ===");
        
        var futures = List.of(
            CompletableFuture.supplyAsync(() -> {
                System.out.println("Task 1 - Thread: " + Thread.currentThread() + " (Virtual: " + Thread.currentThread().isVirtual() + ")");
                try { Thread.sleep(500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return "Task 1 completed";
            }, virtualThreadExecutor),
            
            CompletableFuture.supplyAsync(() -> {
                System.out.println("Task 2 - Thread: " + Thread.currentThread() + " (Virtual: " + Thread.currentThread().isVirtual() + ")");
                try { Thread.sleep(300); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return "Task 2 completed";
            }, virtualThreadExecutor),
            
            CompletableFuture.supplyAsync(() -> {
                System.out.println("Task 3 - Thread: " + Thread.currentThread() + " (Virtual: " + Thread.currentThread().isVirtual() + ")");
                try { Thread.sleep(700); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
                return "Task 3 completed";
            }, virtualThreadExecutor)
        );
        
        var startTime = System.currentTimeMillis();
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        var endTime = System.currentTimeMillis();
        
        futures.forEach(future -> System.out.println("Result: " + future.join()));
        System.out.println("All tasks completed in: " + (endTime - startTime) + "ms");
    }
}
