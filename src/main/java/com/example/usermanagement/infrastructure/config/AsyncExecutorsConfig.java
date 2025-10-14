package com.example.usermanagement.infrastructure.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
class AsyncExecutorsConfig {

    @Bean(destroyMethod = "close")
    ExecutorService ioVirtualExecutor() {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
