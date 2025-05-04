package com.benorim.carhov.api;

import com.benorim.carhov.config.TestContainerConfig;
import com.benorim.carhov.dto.auth.JwtResponseDTO;
import com.benorim.carhov.dto.auth.LoginRequestDTO;
import com.benorim.carhov.dto.auth.MessageResponseDTO;
import com.benorim.carhov.dto.auth.SignupRequestDTO;
import com.benorim.carhov.dto.rideSchedule.CreateRideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.RideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.RideScheduleSearchResultDTO;
import com.benorim.carhov.dto.rideSchedule.SearchRideScheduleDTO;
import com.benorim.carhov.dto.rideSchedule.UpdateRideScheduleDTO;
import com.benorim.carhov.entity.CarHovUser;
import com.benorim.carhov.entity.RideSchedule;
import com.benorim.carhov.entity.Vehicle;
import com.benorim.carhov.enums.DayOfWeek;
import com.benorim.carhov.repository.CarHovUserRepository;
import com.benorim.carhov.repository.RideScheduleRepository;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@Import(TestContainerConfig.class)
public class RideScheduleControllerIntegrationTest {

    public static final String TEST_PASSWORD = "password123";
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private RideScheduleRepository rideScheduleRepository;

    @Autowired
    private CarHovUserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    private CarHovUser testUser;
    private Vehicle testVehicle;
    private RideSchedule testRideSchedule;
    private String authToken;

    @BeforeEach
    void setUp() {
        // Clean up the repositories
        rideScheduleRepository.deleteAll();
        vehicleRepository.deleteAll();
        userRepository.deleteAll();

        // Create test user
        SignupRequestDTO signupRequest = new SignupRequestDTO();
        signupRequest.setDisplayName("Vehicle Test User");
        signupRequest.setEmail("vehicle-test@example.com");
        signupRequest.setPhone("1234567890");
        signupRequest.setPassword(TEST_PASSWORD);

        restTemplate.postForEntity("/api/auth/signup", signupRequest, MessageResponseDTO.class);
        testUser = userRepository.findByEmail("vehicle-test@example.com").orElseThrow();

        // Create test vehicle
        testVehicle = new Vehicle();
        testVehicle.setUser(testUser);
        testVehicle.setMake("Toyota");
        testVehicle.setModel("Camry");
        testVehicle.setYear(2020);
        testVehicle.setColor("Black");
        testVehicle.setLicensePlate("ABC123");
        testVehicle.setPassengerCapacity(3);
        testVehicle = vehicleRepository.save(testVehicle);

        // Create test ride schedule
        testRideSchedule = new RideSchedule();
        testRideSchedule.setUser(testUser);
        testRideSchedule.setVehicle(testVehicle);
        testRideSchedule.setStartLatitude(37.7749);
        testRideSchedule.setStartLongitude(-122.4194);
        testRideSchedule.setEndLatitude(37.3382);
        testRideSchedule.setEndLongitude(-121.8863);
        testRideSchedule.setDayList(List.of(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY));
        testRideSchedule.setDepartureTime(LocalTime.of(9, 0));
        testRideSchedule.setAvailableSeats(4);
        testRideSchedule.setAvailable(true);
        testRideSchedule = rideScheduleRepository.save(testRideSchedule);

        // Login to get auth token
        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(testUser.getEmail());
        loginRequest.setPassword(TEST_PASSWORD);

        ResponseEntity<JwtResponseDTO> loginResponse = restTemplate.postForEntity(
                "/api/auth/signin",
                loginRequest,
                JwtResponseDTO.class
        );

        authToken = loginResponse.getBody().getToken();
    }

    @Test
    void createRideSchedule_Success() {
        // Arrange
        CreateRideScheduleDTO createDTO = new CreateRideScheduleDTO();
        createDTO.setUserId(testUser.getId());
        createDTO.setVehicleId(testVehicle.getId());
        createDTO.setStartLatitude(37.7749);
        createDTO.setStartLongitude(-122.4194);
        createDTO.setEndLatitude(37.3382);
        createDTO.setEndLongitude(-121.8863);
        createDTO.setDayList(List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));
        createDTO.setDepartureTime(LocalTime.of(8, 30));
        createDTO.setAvailableSeats(3);
        createDTO.setAvailable(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<CreateRideScheduleDTO> requestEntity = new HttpEntity<>(createDTO, headers);

        // Act
        ResponseEntity<RideScheduleDTO> response = restTemplate.exchange(
                "/api/ride-schedules",
                HttpMethod.POST,
                requestEntity,
                RideScheduleDTO.class
        );

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        RideScheduleDTO rideScheduleDTO = response.getBody();
        assertNotNull(rideScheduleDTO);
        assertEquals(testUser.getId(), rideScheduleDTO.getUserId());
        assertEquals(testVehicle.getId(), rideScheduleDTO.getVehicleId());
        assertEquals(37.7749, rideScheduleDTO.getStartLatitude());
        assertEquals(-122.4194, rideScheduleDTO.getStartLongitude());
        assertEquals(37.3382, rideScheduleDTO.getEndLatitude());
        assertEquals(-121.8863, rideScheduleDTO.getEndLongitude());
        assertEquals(2, rideScheduleDTO.getDayList().size());
        assertEquals(3, rideScheduleDTO.getAvailableSeats());
        assertTrue(rideScheduleDTO.getAvailable());
    }

    @Test
    void createRideSchedule_InvalidInput_ReturnsBadRequest() {
        // Arrange
        CreateRideScheduleDTO createDTO = new CreateRideScheduleDTO();
        // Missing required fields will cause a Bad Request

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<CreateRideScheduleDTO> requestEntity = new HttpEntity<>(createDTO, headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/ride-schedules",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void getRideSchedule_Success() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<RideScheduleDTO> response = restTemplate.exchange(
                "/api/ride-schedules/{id}",
                HttpMethod.GET,
                requestEntity,
                RideScheduleDTO.class,
                testRideSchedule.getId()
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RideScheduleDTO rideScheduleDTO = response.getBody();
        assertNotNull(rideScheduleDTO);
        assertEquals(testRideSchedule.getId(), rideScheduleDTO.getId());
        assertEquals(testUser.getId(), rideScheduleDTO.getUserId());
        assertEquals(testVehicle.getId(), rideScheduleDTO.getVehicleId());
        assertEquals(37.7749, rideScheduleDTO.getStartLatitude());
        assertEquals(-122.4194, rideScheduleDTO.getStartLongitude());
        assertEquals(37.3382, rideScheduleDTO.getEndLatitude());
        assertEquals(-121.8863, rideScheduleDTO.getEndLongitude());
        assertEquals(2, rideScheduleDTO.getDayList().size());
        assertEquals(4, rideScheduleDTO.getAvailableSeats());
        assertTrue(rideScheduleDTO.getAvailable());
    }

    @Test
    void getRideSchedule_NotFound() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/ride-schedules/{id}",
                HttpMethod.GET,
                requestEntity,
                String.class,
                999L
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getAllRideSchedules_Success() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<RideScheduleDTO[]> response = restTemplate.exchange(
                "/api/ride-schedules",
                HttpMethod.GET,
                requestEntity,
                RideScheduleDTO[].class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RideScheduleDTO[] rideSchedules = response.getBody();
        assertNotNull(rideSchedules);
        assertEquals(1, rideSchedules.length);
        assertEquals(testRideSchedule.getId(), rideSchedules[0].getId());
        assertEquals(testUser.getId(), rideSchedules[0].getUserId());
        assertEquals(testVehicle.getId(), rideSchedules[0].getVehicleId());
    }

    @Test
    void getRideSchedulesByUserId_Success() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<RideScheduleDTO[]> response = restTemplate.exchange(
                "/api/ride-schedules/user/{userId}",
                HttpMethod.GET,
                requestEntity,
                RideScheduleDTO[].class,
                testUser.getId()
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RideScheduleDTO[] rideSchedules = response.getBody();
        assertNotNull(rideSchedules);
        assertEquals(1, rideSchedules.length);
        assertEquals(testRideSchedule.getId(), rideSchedules[0].getId());
        assertEquals(testUser.getId(), rideSchedules[0].getUserId());
    }

    @Test
    void getRideSchedulesByUserId_NotFound() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<RideScheduleDTO[]> response = restTemplate.exchange(
                "/api/ride-schedules/user/{userId}",
                HttpMethod.GET,
                requestEntity,
                RideScheduleDTO[].class,
                999L
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RideScheduleDTO[] rideSchedules = response.getBody();
        assertNotNull(rideSchedules);
        assertEquals(0, rideSchedules.length);
    }

    @Test
    void updateRideSchedule_Success() {
        // Arrange
        UpdateRideScheduleDTO updateDTO = new UpdateRideScheduleDTO();
        updateDTO.setStartLatitude(37.7833);
        updateDTO.setStartLongitude(-122.4167);
        updateDTO.setEndLatitude(37.3352);
        updateDTO.setEndLongitude(-121.8811);
        updateDTO.setDayList(List.of(DayOfWeek.TUESDAY, DayOfWeek.THURSDAY));
        updateDTO.setDepartureTime(LocalTime.of(10, 0));
        updateDTO.setAvailableSeats(2);
        updateDTO.setAvailable(true);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<UpdateRideScheduleDTO> requestEntity = new HttpEntity<>(updateDTO, headers);

        // Act
        ResponseEntity<RideScheduleDTO> response = restTemplate.exchange(
                "/api/ride-schedules/{id}",
                HttpMethod.PUT,
                requestEntity,
                RideScheduleDTO.class,
                testRideSchedule.getId()
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RideScheduleDTO rideScheduleDTO = response.getBody();
        assertNotNull(rideScheduleDTO);
        assertEquals(37.7833, rideScheduleDTO.getStartLatitude());
        assertEquals(-122.4167, rideScheduleDTO.getStartLongitude());
        assertEquals(37.3352, rideScheduleDTO.getEndLatitude());
        assertEquals(-121.8811, rideScheduleDTO.getEndLongitude());
        assertEquals(2, rideScheduleDTO.getDayList().size());
        assertEquals("TUESDAY", rideScheduleDTO.getDayList().get(0).name());
        assertEquals("THURSDAY", rideScheduleDTO.getDayList().get(1).name());
        assertEquals(LocalTime.of(10, 0), rideScheduleDTO.getDepartureTime());
        assertEquals(2, rideScheduleDTO.getAvailableSeats());
        assertTrue(rideScheduleDTO.getAvailable());
    }

    @Test
    void updateRideSchedule_NotFound() {
        // Arrange
        UpdateRideScheduleDTO updateDTO = new UpdateRideScheduleDTO();
        updateDTO.setAvailableSeats(2);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<UpdateRideScheduleDTO> requestEntity = new HttpEntity<>(updateDTO, headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/ride-schedules/{id}",
                HttpMethod.PUT,
                requestEntity,
                String.class,
                999L
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteRideSchedule_Success() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<Void> response = restTemplate.exchange(
                "/api/ride-schedules/{id}",
                HttpMethod.DELETE,
                requestEntity,
                Void.class,
                testRideSchedule.getId()
        );

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());

        // Verify the ride schedule was deleted
        ResponseEntity<String> getResponse = restTemplate.exchange(
                "/api/ride-schedules/{id}",
                HttpMethod.GET,
                requestEntity,
                String.class,
                testRideSchedule.getId()
        );
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void deleteRideSchedule_NotFound() {
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/ride-schedules/{id}",
                HttpMethod.DELETE,
                requestEntity,
                String.class,
                999L
        );

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void searchRideSchedules_Success() {
        // Arrange
        SearchRideScheduleDTO searchDTO = new SearchRideScheduleDTO();
        searchDTO.setStartLatitude(37.7749);
        searchDTO.setStartLongitude(-122.4194);
        searchDTO.setEndLatitude(37.3382);
        searchDTO.setEndLongitude(-121.8863);
        searchDTO.setRadiusInMiles(10.0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<SearchRideScheduleDTO> requestEntity = new HttpEntity<>(searchDTO, headers);

        // Act
        ResponseEntity<RideScheduleSearchResultDTO[]> response = restTemplate.exchange(
                "/api/ride-schedules/search",
                HttpMethod.POST,
                requestEntity,
                RideScheduleSearchResultDTO[].class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RideScheduleSearchResultDTO[] searchResults = response.getBody();
        assertNotNull(searchResults);
        assertEquals(1, searchResults.length);
        assertEquals(testRideSchedule.getId(), searchResults[0].getId());
        assertEquals(37.7749, searchResults[0].getStartLatitude());
        assertEquals(-122.4194, searchResults[0].getStartLongitude());
        assertEquals(37.3382, searchResults[0].getEndLatitude());
        assertEquals(-121.8863, searchResults[0].getEndLongitude());
    }

    @Test
    void searchRideSchedules_NoResults() {
        // Arrange
        SearchRideScheduleDTO searchDTO = new SearchRideScheduleDTO();
        searchDTO.setStartLatitude(40.7128);  // New York coordinates
        searchDTO.setStartLongitude(-74.0060);
        searchDTO.setEndLatitude(40.7614);
        searchDTO.setEndLongitude(-73.9776);
        searchDTO.setRadiusInMiles(5.0);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<SearchRideScheduleDTO> requestEntity = new HttpEntity<>(searchDTO, headers);

        // Act
        ResponseEntity<RideScheduleSearchResultDTO[]> response = restTemplate.exchange(
                "/api/ride-schedules/search",
                HttpMethod.POST,
                requestEntity,
                RideScheduleSearchResultDTO[].class
        );

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        RideScheduleSearchResultDTO[] searchResults = response.getBody();
        assertNotNull(searchResults);
        assertEquals(0, searchResults.length);
    }

    @Test
    void searchRideSchedules_InvalidInput() {
        // Arrange
        SearchRideScheduleDTO searchDTO = new SearchRideScheduleDTO();
        // Missing required fields

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(authToken);
        HttpEntity<SearchRideScheduleDTO> requestEntity = new HttpEntity<>(searchDTO, headers);

        // Act
        ResponseEntity<String> response = restTemplate.exchange(
                "/api/ride-schedules/search",
                HttpMethod.POST,
                requestEntity,
                String.class
        );

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
} 