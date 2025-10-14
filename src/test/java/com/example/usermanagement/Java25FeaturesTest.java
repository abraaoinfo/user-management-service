package com.example.usermanagement;

import org.junit.jupiter.api.Test;
import java.util.Optional;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple test demonstrating Java 25 features work correctly
 * 
 * This test shows that Java 25 features (records, pattern matching, virtual threads)
 * work perfectly, even though Spring Boot 4.0.0-M3 has breaking changes.
 */
class Java25FeaturesTest {

    @Test
    void shouldDemonstrateJava25Records() {
        // Given - Java 25 Record
        record Person(String name, int age) {
            public String greet() {
                return "Hello, I'm %s and I'm %d years old".formatted(name, age);
            }
        }

        // When
        var person = new Person("João", 30);

        // Then
        assertThat(person.name()).isEqualTo("João");
        assertThat(person.age()).isEqualTo(30);
        assertThat(person.greet()).isEqualTo("Hello, I'm João and I'm 30 years old");
    }

    @Test
    void shouldDemonstratePatternMatching() {
        // Given - Pattern matching with simple classes
        interface Shape {
            double area();
        }

        record Circle(double radius) implements Shape {
            public double area() {
                return Math.PI * radius * radius;
            }
        }

        record Rectangle(double width, double height) implements Shape {
            public double area() {
                return width * height;
            }
        }

        // When
        Shape circle = new Circle(5.0);
        Shape rectangle = new Rectangle(4.0, 6.0);

        // Then - Pattern matching switch
        var circleArea = switch (circle) {
            case Circle c -> c.area();
            case Rectangle r -> r.area();
            default -> 0.0;
        };

        var rectangleArea = switch (rectangle) {
            case Circle c -> c.area();
            case Rectangle r -> r.area();
            default -> 0.0;
        };

        assertThat(circleArea).isEqualTo(78.54, org.assertj.core.data.Offset.offset(0.01));
        assertThat(rectangleArea).isEqualTo(24.0);
    }

    @Test
    void shouldDemonstrateVirtualThreads() throws InterruptedException {
        // Given - Virtual threads
        var results = new java.util.concurrent.ConcurrentLinkedQueue<String>();
        var virtualThread = Thread.ofVirtual()
            .name("test-virtual-thread")
            .start(() -> {
                results.add("Virtual thread executed");
                results.add("Thread name: " + Thread.currentThread().getName());
                results.add("Is virtual: " + Thread.currentThread().isVirtual());
            });

        // When
        virtualThread.join();

        // Then
        assertThat(results).hasSize(3);
        assertThat(results).contains("Virtual thread executed");
        assertThat(results).contains("Thread name: test-virtual-thread");
        assertThat(results).contains("Is virtual: true");
    }

    @Test
    void shouldDemonstrateTextBlocks() {
        // Given - Java 25 Text Blocks
        var json = """
            {
                "name": "João Silva",
                "email": "joao@example.com",
                "age": 30,
                "address": {
                    "street": "Rua das Flores",
                    "city": "São Paulo"
                }
            }
            """;

        // When
        var lines = json.lines().toList();

        // Then
        assertThat(lines).hasSize(9);
        assertThat(json).contains("João Silva");
        assertThat(json).contains("joao@example.com");
        assertThat(json).contains("Rua das Flores");
    }

    @Test
    void shouldDemonstrateOptionalChaining() {
        // Given
        record User(String name, Optional<String> email) {}
        
        var user1 = new User("João", Optional.of("joao@example.com"));
        var user2 = new User("Maria", Optional.empty());

        // When & Then - Optional chaining
        var email1 = user1.email().map(String::toUpperCase).orElse("NO_EMAIL");
        var email2 = user2.email().map(String::toUpperCase).orElse("NO_EMAIL");

        assertThat(email1).isEqualTo("JOAO@EXAMPLE.COM");
        assertThat(email2).isEqualTo("NO_EMAIL");
    }

    @Test
    void shouldDemonstrateVarKeyword() {
        // Given - var keyword with complex types
        var numbers = List.of(1, 2, 3, 4, 5);
        var users = Map.of(
            "joao", "João Silva",
            "maria", "Maria Santos"
        );

        // When
        var sum = numbers.stream().mapToInt(Integer::intValue).sum();
        var userNames = users.values().stream().toList();

        // Then
        assertThat(sum).isEqualTo(15);
        assertThat(userNames).containsExactlyInAnyOrder("João Silva", "Maria Santos");
    }

    @Test
    void shouldDemonstrateStringFormatting() {
        // Given
        var name = "João";
        var age = 30;
        var city = "São Paulo";

        // When - Modern string formatting
        var message = "Hello, I'm %s, %d years old, from %s".formatted(name, age, city);
        var template = String.format("Hello, I'm %s, %d years old, from %s", name, age, city);

        // Then
        assertThat(message).isEqualTo("Hello, I'm João, 30 years old, from São Paulo");
        assertThat(template).isEqualTo("Hello, I'm João, 30 years old, from São Paulo");
    }
}
