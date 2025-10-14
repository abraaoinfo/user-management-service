package com.example.usermanagement.infrastructure.service;

import io.micrometer.observation.annotation.Observed;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

@Service
@Observed(name = "virtual-thread.service")
public class VirtualThreadService {

    @Observed(name = "virtual-thread.simulate-io")
    public CompletableFuture<String> simulateIOOperation(String taskName) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                System.out.println("Virtual Thread: " + Thread.currentThread() + " - Task: " + taskName);
                Thread.sleep(1000);
                return "Completed: " + taskName;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return "Interrupted: " + taskName;
            }
        }, Executors.newVirtualThreadPerTaskExecutor());
    }

    @Observed(name = "virtual-thread.batch-operations")
    public CompletableFuture<String> batchOperations() {
        var futures = java.util.stream.IntStream.rangeClosed(1, 10)
                .mapToObj(i -> simulateIOOperation("Task-" + i))
                .toList();

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v -> "All " + futures.size() + " tasks completed");
    }

    @Observed(name = "virtual-thread.parallel-processing")
    public CompletableFuture<String> parallelProcessing() {
        var executor = Executors.newVirtualThreadPerTaskExecutor();
        
        var task1 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);
                return "Task 1 completed";
            } catch (InterruptedException e) {
                return "Task 1 interrupted";
            }
        }, executor);

        var task2 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(300);
                return "Task 2 completed";
            } catch (InterruptedException e) {
                return "Task 2 interrupted";
            }
        }, executor);

        var task3 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(700);
                return "Task 3 completed";
            } catch (InterruptedException e) {
                return "Task 3 interrupted";
            }
        }, executor);

        return CompletableFuture.allOf(task1, task2, task3)
                .thenApply(v -> String.format("Results: %s, %s, %s", 
                    task1.join(), task2.join(), task3.join()));
    }

    public String getThreadInfo() {
        return String.format("Current Thread: %s, Is Virtual: %s", 
            Thread.currentThread(), Thread.currentThread().isVirtual());
    }
}
