package com.example.usermanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class UserManagementApplication {

     static void main(String[] args) {
        System.setProperty("spring.threads.virtual.enabled", "true");
        SpringApplication.run(UserManagementApplication.class, args);
        IO.println("🚀 User Management Service Started! API: http://localhost:8080/swagger-ui.html");
    }
}
