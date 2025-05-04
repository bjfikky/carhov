package com.benorim.carhov.api;

import com.benorim.carhov.config.TestContainerConfig;
import com.benorim.carhov.dto.auth.JwtResponseDTO;
import com.benorim.carhov.dto.auth.LoginRequestDTO;
import com.benorim.carhov.dto.auth.MessageResponseDTO;
import com.benorim.carhov.dto.auth.SignupRequestDTO;
import com.benorim.carhov.entity.Role;
import com.benorim.carhov.enums.RoleType;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestContainerConfig.class)
class AuthControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CarHovUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @BeforeEach
    void setUp() {
        // Clean up test data before each test
        userRepository.deleteAll();
        
        // Ensure the USER role exists
        if (!roleRepository.existsByName(RoleType.ROLE_USER.name())) {
            Role userRole = Role.builder().name(RoleType.ROLE_USER.name()).build();
            roleRepository.save(userRole);
        }
    }

    @Test
    void registerUser_Success() {
        // Arrange
        SignupRequestDTO signupRequest = new SignupRequestDTO();
        signupRequest.setDisplayName("Test User");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPhone("1234567890");
        signupRequest.setPassword("password123");
        
        // Act
        ResponseEntity<MessageResponseDTO> response = restTemplate.postForEntity(
                "/api/auth/signup",
                signupRequest,
                MessageResponseDTO.class
        );
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("User registered successfully!", response.getBody().getMessage());
        
        // Verify user was created in the database
        assertTrue(userRepository.existsByEmail("test@example.com"));
        assertEquals(1, userRepository.count());
    }

    @Test
    void registerUser_DuplicateEmail_ReturnsBadRequest() {
        // Arrange - Create a user first
        SignupRequestDTO firstUser = new SignupRequestDTO();
        firstUser.setDisplayName("First User");
        firstUser.setEmail("duplicate@example.com");
        firstUser.setPhone("1234567890");
        firstUser.setPassword("password123");
        
        restTemplate.postForEntity("/api/auth/signup", firstUser, MessageResponseDTO.class);
        
        // Now try to register with the same email
        SignupRequestDTO duplicateUser = new SignupRequestDTO();
        duplicateUser.setDisplayName("Second User");
        duplicateUser.setEmail("duplicate@example.com");
        duplicateUser.setPhone("9876543210");
        duplicateUser.setPassword("different123");
        
        // Act
        ResponseEntity<MessageResponseDTO> response = restTemplate.postForEntity(
                "/api/auth/signup",
                duplicateUser,
                MessageResponseDTO.class
        );
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Error: Email is already in use!", response.getBody().getMessage());
        
        // Verify only one user exists
        assertEquals(1, userRepository.count());
    }

    @Test
    void registerUser_InvalidRequest_ReturnsBadRequest() {
        // Arrange - Missing required fields
        SignupRequestDTO invalidRequest = new SignupRequestDTO();
        invalidRequest.setDisplayName(""); // Empty display name
        invalidRequest.setEmail("not-an-email"); // Invalid email
        invalidRequest.setPassword("pwd"); // Too short password
        
        // Act
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/auth/signup",
                invalidRequest,
                Object.class // Using Object to capture the validation error response
        );
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        // Verify no user was created
        assertEquals(0, userRepository.count());
    }

    @Test
    void authenticateUser_Success() {
        // Arrange - Create a user first
        SignupRequestDTO firstUser = new SignupRequestDTO();
        firstUser.setDisplayName("First User");
        firstUser.setEmail("test@example.com");
        firstUser.setPhone("1234567890");
        firstUser.setPassword("password123");

        restTemplate.postForEntity("/api/auth/signup", firstUser, MessageResponseDTO.class);

        // Act - Login with the newly created user
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        ResponseEntity<JwtResponseDTO> response = restTemplate.postForEntity("/api/auth/signin", loginRequest, JwtResponseDTO.class);

        JwtResponseDTO responseBody = response.getBody();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(responseBody);
        assertEquals(responseBody.getEmail(), firstUser.getEmail());
        assertEquals("Bearer", responseBody.getType());
        assertNotNull(responseBody.getToken());
        assertNotNull(responseBody.getRefreshToken());
        assertEquals(RoleType.ROLE_USER.name(), responseBody.getRoles().getFirst());
    }

    @Test
    void authenticateUser_Fail() {
        // Arrange - Create a user first
        SignupRequestDTO firstUser = new SignupRequestDTO();
        firstUser.setDisplayName("First User");
        firstUser.setEmail("test@example.com");
        firstUser.setPhone("1234567890");
        firstUser.setPassword("password123");

        restTemplate.postForEntity("/api/auth/signup", firstUser, MessageResponseDTO.class);

        // Act - Login with the newly created user, but with a wrong password
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("wrong-password");

        ResponseEntity<JwtResponseDTO> response = restTemplate.postForEntity("/api/auth/signin", loginRequest, JwtResponseDTO.class);

        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void logOutUser_Success() {
        // Arrange - Create a user first
        SignupRequestDTO firstUser = new SignupRequestDTO();
        firstUser.setDisplayName("First User");
        firstUser.setEmail("test@example.com");
        firstUser.setPhone("1234567890");
        firstUser.setPassword("password123");

        restTemplate.postForEntity("/api/auth/signup", firstUser, MessageResponseDTO.class);

        // Act - Login with the newly created user
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        ResponseEntity<JwtResponseDTO> signInResponse = restTemplate.postForEntity("/api/auth/signin", loginRequest, JwtResponseDTO.class);
        String token = signInResponse.getBody().getToken();

        // Add the Authorization header
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Make the signout request with the token
        ResponseEntity<MessageResponseDTO> response = restTemplate.exchange(
                "/api/auth/signout",
                HttpMethod.POST,
                requestEntity,
                MessageResponseDTO.class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("Log out successful!", response.getBody().getMessage());
    }
} 