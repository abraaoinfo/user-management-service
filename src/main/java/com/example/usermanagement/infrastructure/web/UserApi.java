package com.example.usermanagement.infrastructure.web;

import com.example.usermanagement.domain.model.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(
    name = "User Management", 
    description = "API for managing users with address validation via ViaCEP",
    externalDocs = @io.swagger.v3.oas.annotations.ExternalDocumentation(
        description = "ViaCEP API Documentation",
        url = "https://viacep.com.br/ws/"
    )
)
public interface UserApi {

    @Operation(
        summary = "Create a new user",
        description = """
            Creates a new user in the system. If a postal code (CEP) is provided, 
            the system will automatically enrich the user's address using the ViaCEP API.
            
            - Automatic address validation via ViaCEP
            - Email and CPF uniqueness validation
            - Virtual threads for high concurrency
            - Null-safety with JSpecify annotations
            """,
        operationId = "createUser"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "User created successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.UserRecord.class),
                examples = @ExampleObject(
                    name = "User with address",
                    summary = "User created with address from ViaCEP",
                    value = """
                        {
                          "id": 1,
                          "name": "João Silva",
                          "email": "joao.silva@example.com",
                          "cpf": "12345678901",
                          "address": {
                            "postalCode": "01310100",
                            "street": "Praça da Sé",
                            "neighborhood": "Sé",
                            "city": "São Paulo",
                            "state": "SP",
                            "complement": null
                          },
                          "createdAt": "2024-01-15T10:30:00",
                          "updatedAt": "2024-01-15T10:30:00"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid input data",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Validation Error",
                    summary = "Invalid CPF format",
                    value = """
                        {
                          "type": "https://example.com/problems/validation-error",
                          "title": "Validation Failed",
                          "status": 400,
                          "detail": "CPF must be 11 digits",
                          "instance": "/api/v1/users",
                          "timestamp": "2024-01-15T10:30:00Z"
                        }
                        """
                )
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "User already exists",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Conflict Error",
                    summary = "Email or CPF already in use",
                    value = """
                        {
                          "type": "https://example.com/problems/conflict-error",
                          "title": "Conflict",
                          "status": 409,
                          "detail": "Email or CPF already in use",
                          "instance": "/api/v1/users",
                          "timestamp": "2024-01-15T10:30:00Z"
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<User.UserRecord> createUser(
        @Parameter(
            description = "User creation request with optional postal code for address enrichment",
            required = true,
            example = """
                {
                  "name": "João Silva",
                  "email": "joao.silva@example.com",
                  "cpf": "12345678901",
                  "postalCode": "01310100"
                }
                """
        )
        @RequestBody User.CreateUserRequest request
    );

    @Operation(
        summary = "Get user by ID",
        description = """
            Retrieves a specific user by their unique identifier.
            
            - Fast lookup by primary key
            - Virtual threads for concurrent access
            - Null-safety guaranteed
            """,
        operationId = "getUserById"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User found successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.UserRecord.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Not Found",
                    summary = "User with ID not found",
                    value = """
                        {
                          "type": "https://example.com/problems/not-found",
                          "title": "Not Found",
                          "status": 404,
                          "detail": "User not found with ID: 999",
                          "instance": "/api/v1/users/999",
                          "timestamp": "2024-01-15T10:30:00Z"
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<User.UserRecord> getUserById(
        @Parameter(
            description = "Unique identifier of the user",
            required = true,
            example = "1"
        )
        @PathVariable Long id
    );

    @Operation(
        summary = "Get all users with pagination",
        description = """
            Retrieves a paginated list of all users in the system.
            
            - Pagination support for large datasets
            - Sorting capabilities
            - Virtual threads for high performance
            - Efficient database queries
            """,
        operationId = "getAllUsers"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Users retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = Page.class)
            )
        )
    })
    ResponseEntity<Page<User.UserRecord>> getAllUsers(
        @Parameter(
            description = "Pagination and sorting parameters",
            example = "page=0&size=10&sort=name,asc"
        )
        Pageable pageable
    );

    @Operation(
        summary = "Update user information",
        description = """
            Updates an existing user's information. If a new postal code is provided,
            the address will be automatically updated using the ViaCEP API.
            
            - Partial updates supported
            - Automatic address enrichment
            - Email/CPF uniqueness validation
            - Virtual threads for concurrent updates
            """,
        operationId = "updateUser"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "User updated successfully",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = User.UserRecord.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Email or CPF already in use"
        )
    })
    ResponseEntity<User.UserRecord> updateUser(
        @Parameter(
            description = "Unique identifier of the user to update",
            required = true,
            example = "1"
        )
        @PathVariable Long id,
        @Parameter(
            description = "Updated user information",
            required = true,
            example = """
                {
                  "name": "João Silva Santos",
                  "email": "joao.santos@example.com",
                  "postalCode": "04567890"
                }
                """
        )
        @RequestBody User.UpdateUserRequest request
    );

    @Operation(
        summary = "Delete user",
        description = """
            Permanently deletes a user from the system.
            
            - Cascade delete for related data
            - Virtual threads for concurrent operations
            - Immediate response (204 No Content)
            """,
        operationId = "deleteUser"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "User deleted successfully"
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found"
        )
    })
    ResponseEntity<Void> deleteUser(
        @Parameter(
            description = "Unique identifier of the user to delete",
            required = true,
            example = "1"
        )
        @PathVariable Long id
    );

    @Operation(
        summary = "Get user statistics",
        description = """
            Retrieves statistics about users in the system.
            
            - Real-time statistics
            - Address completion metrics
            - Virtual threads for fast aggregation
            """,
        operationId = "getUserStats"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Statistics retrieved successfully",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "User Statistics",
                    summary = "Current system statistics",
                    value = """
                        {
                          "totalUsers": 150,
                          "usersWithAddress": 120,
                          "usersWithoutAddress": 30,
                          "addressCompletionRate": 80.0
                        }
                        """
                )
            )
        )
    })
    ResponseEntity<UserStats> getUserStats();

    @Schema(description = "User statistics information")
    record UserStats(
        @Schema(description = "Total number of users", example = "150")
        long totalUsers,
        
        @Schema(description = "Number of users with complete address", example = "120")
        long usersWithAddress,
        
        @Schema(description = "Number of users without address", example = "30")
        long usersWithoutAddress,
        
        @Schema(description = "Address completion rate as percentage", example = "80.0")
        double addressCompletionRate
    ) {
        public static UserStats of(long totalUsers, long usersWithAddress) {
            long usersWithoutAddress = totalUsers - usersWithAddress;
            double completionRate = totalUsers > 0 ? (double) usersWithAddress / totalUsers * 100 : 0.0;
            
            return new UserStats(totalUsers, usersWithAddress, usersWithoutAddress, completionRate);
        }
    }
}
