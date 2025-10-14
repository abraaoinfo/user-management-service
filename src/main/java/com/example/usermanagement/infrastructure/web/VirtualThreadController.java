package com.example.usermanagement.infrastructure.web;

import com.example.usermanagement.infrastructure.service.VirtualThreadService;
import io.micrometer.observation.annotation.Observed;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/virtual-threads")
@Observed(name = "virtual-thread.controller")
public class VirtualThreadController {

    private final VirtualThreadService virtualThreadService;

    public VirtualThreadController(VirtualThreadService virtualThreadService) {
        this.virtualThreadService = virtualThreadService;
    }

    @GetMapping("/info")
    @Observed(name = "virtual-thread.info")
    public ResponseEntity<String> getThreadInfo() {
        return ResponseEntity.ok(virtualThreadService.getThreadInfo());
    }

    @GetMapping("/single-task")
    @Observed(name = "virtual-thread.single-task")
    public CompletableFuture<ResponseEntity<String>> singleTask() {
        return virtualThreadService.simulateIOOperation("Single Task")
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/batch-tasks")
    @Observed(name = "virtual-thread.batch-tasks")
    public CompletableFuture<ResponseEntity<String>> batchTasks() {
        return virtualThreadService.batchOperations()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping("/parallel-tasks")
    @Observed(name = "virtual-thread.parallel-tasks")
    public CompletableFuture<ResponseEntity<String>> parallelTasks() {
        return virtualThreadService.parallelProcessing()
                .thenApply(ResponseEntity::ok);
    }
}
