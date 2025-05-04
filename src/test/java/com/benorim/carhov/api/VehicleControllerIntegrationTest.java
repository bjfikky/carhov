package com.benorim.carhov.api;

import com.benorim.carhov.dto.auth.JwtResponseDTO;
import com.benorim.carhov.dto.auth.LoginRequestDTO;
import com.benorim.carhov.dto.auth.MessageResponseDTO;
import com.benorim.carhov.dto.auth.SignupRequestDTO;
import com.benorim.carhov.dto.vehicle.CreateVehicleDTO;
import com.benorim.carhov.dto.vehicle.UpdateVehicleDTO;
import com.benorim.carhov.dto.vehicle.VehicleDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.Role;
import com.benorim.carhov.entity.Vehicle;
import com.benorim.carhov.enums.RoleType;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.RoleRepository;
import com.benorim.carhov.repository.VehicleRepository;
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
import org.testcontainers.utility.TestcontainersConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestcontainersConfiguration.class)
class VehicleControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CarHovUserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;

    private CarHovUser testUser;
    private String authToken;
    private Vehicle testVehicle;

    @BeforeEach
    void setUp() {
        // Clean up test data before each test
        vehicleRepository.deleteAll();
        userRepository.deleteAll();
        
        // Ensure the USER role exists
        if (!roleRepository.existsByName(RoleType.ROLE_USER.name())) {
            Role userRole = Role.builder().name(RoleType.ROLE_USER.name()).build();
            roleRepository.save(userRole);
        }
        
        // Create a test user and get authentication token
        SignupRequestDTO signupRequest = new SignupRequestDTO();
        signupRequest.setDisplayName("Vehicle Test User");
        signupRequest.setEmail("vehicle-test@example.com");
        signupRequest.setPhone("1234567890");
        signupRequest.setPassword("password123");
        
        restTemplate.postForEntity("/api/auth/signup", signupRequest, MessageResponseDTO.class);
        
        // Login to get auth token
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("vehicle-test@example.com");
        loginRequest.setPassword("password123");
        
        ResponseEntity<JwtResponseDTO> loginResponse = restTemplate.postForEntity(
                "/api/auth/signin",
                loginRequest,
                JwtResponseDTO.class
        );
        
        authToken = loginResponse.getBody().getToken();
        testUser = userRepository.findByEmail("vehicle-test@example.com").orElseThrow();
        
        // Create a test vehicle for update/get/delete tests
        testVehicle = Vehicle.builder()
                .user(testUser)
                .make("Toyota")
                .model("Camry")
                .year(2020)
                .color("Silver")
                .licensePlate("TEST123")
                .passengerCapacity(5)
                .build();
        
        vehicleRepository.save(testVehicle);
    }

    @Test
    void createVehicle_Success() {
        // Arrange
        CreateVehicleDTO createVehicleDTO = new CreateVehicleDTO();
        createVehicleDTO.setUserId(testUser.getId());
        createVehicleDTO.setMake("Honda");
        createVehicleDTO.setModel("Civic");
        createVehicleDTO.setYear(2022);
        createVehicleDTO.setColor("Blue");
        createVehicleDTO.setLicensePlate("ABC123");
        createVehicleDTO.setCapacity(4);
        
        // Set up headers with authentication token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<CreateVehicleDTO> requestEntity = new HttpEntity<>(createVehicleDTO, headers);
        
        // Act
        ResponseEntity<VehicleDTO> response = restTemplate.exchange(
                "/api/vehicles",
                HttpMethod.POST,
                requestEntity,
                VehicleDTO.class
        );
        
        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        VehicleDTO vehicleDTO = response.getBody();
        assertNotNull(vehicleDTO);
        assertNotNull(vehicleDTO.getId());
        assertEquals(testUser.getId(), vehicleDTO.getUserId());
        assertEquals("Honda", vehicleDTO.getMake());
        assertEquals("Civic", vehicleDTO.getModel());
        assertEquals(Integer.valueOf(2022), vehicleDTO.getYear());
        assertEquals("Blue", vehicleDTO.getColor());
        assertEquals("ABC123", vehicleDTO.getLicensePlate());
        assertEquals(Integer.valueOf(4), vehicleDTO.getPassengerCapacity());
        
        // Verify vehicle was created in the database
        List<Vehicle> userVehicles = vehicleRepository.findByUserId(testUser.getId());
        assertEquals(2, userVehicles.size()); // Now 2 vehicles because we have the test vehicle
        Vehicle savedVehicle = userVehicles.stream()
                .filter(v -> v.getLicensePlate().equals("ABC123"))
                .findFirst()
                .orElseThrow();
        assertEquals("Honda", savedVehicle.getMake());
        assertEquals("Civic", savedVehicle.getModel());
        assertEquals("ABC123", savedVehicle.getLicensePlate());
    }
    
    @Test
    void createVehicle_Unauthorized_Fails() {
        // Arrange - Try without authentication
        CreateVehicleDTO createVehicleDTO = new CreateVehicleDTO();
        createVehicleDTO.setUserId(testUser.getId());
        createVehicleDTO.setMake("Toyota");
        createVehicleDTO.setModel("Corolla");
        createVehicleDTO.setYear(2021);
        createVehicleDTO.setColor("Red");
        createVehicleDTO.setLicensePlate("XYZ789");
        createVehicleDTO.setCapacity(5);
        
        // Act - No authentication header
        ResponseEntity<Object> response = restTemplate.postForEntity(
                "/api/vehicles",
                createVehicleDTO,
                Object.class
        );
        
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        
        // Verify no vehicle was created
        List<Vehicle> userVehicles = vehicleRepository.findByUserId(testUser.getId());
        assertEquals(1, userVehicles.size()); // Just the test vehicle
    }
    
    @Test
    void createVehicle_InvalidData_ReturnsBadRequest() {
        // Arrange - Invalid vehicle data (missing required fields)
        CreateVehicleDTO invalidVehicleDTO = new CreateVehicleDTO();
        invalidVehicleDTO.setUserId(testUser.getId());
        // Missing make, model, etc.
        
        // Set up headers with authentication token
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<CreateVehicleDTO> requestEntity = new HttpEntity<>(invalidVehicleDTO, headers);
        
        // Act
        ResponseEntity<Object> response = restTemplate.exchange(
                "/api/vehicles",
                HttpMethod.POST,
                requestEntity,
                Object.class
        );
        
        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        
        // Verify no vehicle was created
        List<Vehicle> userVehicles = vehicleRepository.findByUserId(testUser.getId());
        assertEquals(1, userVehicles.size()); // Just the test vehicle
    }
    
    @Test
    void createVehicle_WrongUser_ReturnsForbidden() {
        // Arrange - Create another user
        SignupRequestDTO anotherUser = new SignupRequestDTO();
        anotherUser.setDisplayName("Another User");
        anotherUser.setEmail("another-user@example.com");
        anotherUser.setPhone("9876543210");
        anotherUser.setPassword("password123");
        
        restTemplate.postForEntity("/api/auth/signup", anotherUser, MessageResponseDTO.class);
        CarHovUser otherUser = userRepository.findByEmail("another-user@example.com").orElseThrow();
        
        // Now try to create a vehicle for the other user (which should fail)
        CreateVehicleDTO createVehicleDTO = new CreateVehicleDTO();
        createVehicleDTO.setUserId(otherUser.getId()); // This is not the authenticated user
        createVehicleDTO.setMake("Ford");
        createVehicleDTO.setModel("Mustang");
        createVehicleDTO.setYear(2023);
        createVehicleDTO.setColor("Black");
        createVehicleDTO.setLicensePlate("MUSCLE1");
        createVehicleDTO.setCapacity(2);
        
        // Set up headers with authentication token (of the first user)
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<CreateVehicleDTO> requestEntity = new HttpEntity<>(createVehicleDTO, headers);
        
        // Act
        ResponseEntity<Object> response = restTemplate.exchange(
                "/api/vehicles",
                HttpMethod.POST,
                requestEntity,
                Object.class
        );
        
        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        
        // Verify no vehicle was created
        List<Vehicle> userVehicles = vehicleRepository.findByUserId(otherUser.getId());
        assertEquals(0, userVehicles.size());
    }
    
    // New tests for remaining endpoints
    
    @Test
    void updateVehicle_Success() {
        // Arrange
        UpdateVehicleDTO updateVehicleDTO = new UpdateVehicleDTO();
        updateVehicleDTO.setColor("Black"); // Only color can be updated
        
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<UpdateVehicleDTO> requestEntity = new HttpEntity<>(updateVehicleDTO, headers);
        
        // Act
        ResponseEntity<VehicleDTO> response = restTemplate.exchange(
                "/api/vehicles/" + testVehicle.getId(),
                HttpMethod.PUT,
                requestEntity,
                VehicleDTO.class
        );
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        VehicleDTO vehicleDTO = response.getBody();
        assertNotNull(vehicleDTO);
        assertEquals("Black", vehicleDTO.getColor());
        
        // Verify vehicle was updated in the database
        Vehicle updatedVehicle = vehicleRepository.findById(testVehicle.getId()).orElseThrow();
        assertEquals("Black", updatedVehicle.getColor());
    }
    
    @Test
    void updateVehicle_Unauthorized_Fails() {
        // Arrange
        UpdateVehicleDTO updateVehicleDTO = new UpdateVehicleDTO();
        updateVehicleDTO.setColor("Red");
        
        // Act - No authentication header
        ResponseEntity<Object> response = restTemplate.exchange(
                "/api/vehicles/" + testVehicle.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateVehicleDTO),
                Object.class
        );
        
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        
        // Verify vehicle was not updated
        Vehicle unchangedVehicle = vehicleRepository.findById(testVehicle.getId()).orElseThrow();
        assertEquals("Silver", unchangedVehicle.getColor());
    }
    
    @Test
    void deleteVehicle_Success() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/vehicles/" + testVehicle.getId(),
                HttpMethod.DELETE,
                requestEntity,
                Void.class
        );
        
        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        
        // Verify vehicle was deleted from the database
        assertEquals(0, vehicleRepository.findByUserId(testUser.getId()).size());
    }
    
    @Test
    void deleteVehicle_Unauthorized_Fails() {
        // Act - No authentication header
        ResponseEntity<Object> response = restTemplate.exchange(
                "/api/vehicles/" + testVehicle.getId(),
                HttpMethod.DELETE,
                new HttpEntity<>(null),
                Object.class
        );
        
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        
        // Verify vehicle was not deleted
        assertEquals(1, vehicleRepository.findByUserId(testUser.getId()).size());
    }
    
    @Test
    void getVehicle_Success() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act
        ResponseEntity<VehicleDTO> response = restTemplate.exchange(
                "/api/vehicles/" + testVehicle.getId(),
                HttpMethod.GET,
                requestEntity,
                VehicleDTO.class
        );
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        VehicleDTO vehicleDTO = response.getBody();
        assertNotNull(vehicleDTO);
        assertEquals(testVehicle.getId(), vehicleDTO.getId());
        assertEquals(testUser.getId(), vehicleDTO.getUserId());
        assertEquals("Toyota", vehicleDTO.getMake());
        assertEquals("Camry", vehicleDTO.getModel());
        assertEquals("Silver", vehicleDTO.getColor());
    }
    
    @Test
    void getVehicle_Unauthorized_Fails() {
        // Act - No authentication header
        ResponseEntity<Object> response = restTemplate.exchange(
                "/api/vehicles/" + testVehicle.getId(),
                HttpMethod.GET,
                new HttpEntity<>(null),
                Object.class
        );
        
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void getVehiclesByUserId_Success() {
        // Create a second vehicle for the user
        Vehicle secondVehicle = Vehicle.builder()
                .user(testUser)
                .make("Honda")
                .model("Accord")
                .year(2021)
                .color("Red")
                .licensePlate("SECOND1")
                .passengerCapacity(5)
                .build();
        
        vehicleRepository.save(secondVehicle);
        
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act
        ResponseEntity<VehicleDTO[]> response = restTemplate.exchange(
                "/api/vehicles/user/" + testUser.getId(),
                HttpMethod.GET,
                requestEntity,
                VehicleDTO[].class
        );
        
        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        VehicleDTO[] vehicles = response.getBody();
        assertNotNull(vehicles);
        assertEquals(2, vehicles.length);
    }
    
    @Test
    void getVehiclesByUserId_Unauthorized_Fails() {
        // Act - No authentication header
        ResponseEntity<Object> response = restTemplate.exchange(
                "/api/vehicles/user/" + testUser.getId(),
                HttpMethod.GET,
                new HttpEntity<>(null),
                Object.class
        );
        
        // Assert
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    
    @Test
    void getVehiclesByUserId_WrongUser_ReturnsForbidden() {
        // Arrange - Create another user
        SignupRequestDTO anotherUser = new SignupRequestDTO();
        anotherUser.setDisplayName("Another User");
        anotherUser.setEmail("another-user@example.com");
        anotherUser.setPhone("9876543210");
        anotherUser.setPassword("password123");
        
        restTemplate.postForEntity("/api/auth/signup", anotherUser, MessageResponseDTO.class);
        CarHovUser otherUser = userRepository.findByEmail("another-user@example.com").orElseThrow();
        
        // Setup auth header for first user
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        
        // Act - Try to get vehicles for the other user
        ResponseEntity<Object> response = restTemplate.exchange(
                "/api/vehicles/user/" + otherUser.getId(),
                HttpMethod.GET,
                requestEntity,
                Object.class
        );
        
        // Assert
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }
} 