package com.example.usermanagement;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.threads.virtual.enabled=true",
    "server.tomcat.virtual-threads=true"
})
class VirtualThreadTest {

    @Test
    void demonstrateVirtualThreads() throws Exception {
        System.out.println("=== Virtual Threads Demo ===");
        
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        
        var startTime = System.currentTimeMillis();
        
        var futures = java.util.stream.IntStream.rangeClosed(1, 5)
                .mapToObj(i -> CompletableFuture.supplyAsync(() -> {
                    try {
                        System.out.println("Task " + i + " - Thread: " + Thread.currentThread() + 
                                         " (Virtual: " + Thread.currentThread().isVirtual() + ")");
                        Thread.sleep(1000);
                        return "Task " + i + " completed";
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return "Task " + i + " interrupted";
                    }
                }, executor))
                .toList();
        
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
        
        var endTime = System.currentTimeMillis();
        System.out.println("All tasks completed in: " + (endTime - startTime) + "ms");
        
        futures.forEach(future -> System.out.println("Result: " + future.join()));
    }

    @Test
    void compareVirtualVsPlatformThreads() throws Exception {
        System.out.println("\n=== Virtual vs Platform Threads ===");
        
        var virtualExecutor = Executors.newVirtualThreadPerTaskExecutor();
        var platformExecutor = Executors.newFixedThreadPool(5);
        
        var startTime = System.currentTimeMillis();
        
        var virtualFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                return "Virtual thread completed";
            } catch (InterruptedException e) {
                return "Virtual thread interrupted";
            }
        }, virtualExecutor);
        
        var platformFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(1000);
                return "Platform thread completed";
            } catch (InterruptedException e) {
                return "Platform thread interrupted";
            }
        }, platformExecutor);
        
        CompletableFuture.allOf(virtualFuture, platformFuture).get();
        
        var endTime = System.currentTimeMillis();
        System.out.println("Both tasks completed in: " + (endTime - startTime) + "ms");
        System.out.println("Virtual: " + virtualFuture.join());
        System.out.println("Platform: " + platformFuture.join());
        
        platformExecutor.shutdown();
    }
}
