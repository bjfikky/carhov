# CarHov - Modern Carpooling Platform

[![Java](https://img.shields.io/badge/Java-21-orange)](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.1-brightgreen)](https://spring.io/projects/spring-boot)

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Architecture](#architecture)
- [Technology Stack](#technology-stack)
- [API Documentation](#api-documentation)
- [Setup and Installation](#setup-and-installation)
- [Development Guidelines](#development-guidelines)
- [Testing](#testing)
- [Security](#security)
- [Contributing](#contributing)
- [License](#license)
- [Reference Documentation](#reference-documentation)

## Overview
**Still a work in progress, but core functionalities added ...**

CarHov is a modern carpooling application designed to bridge the gap between drivers and passengers, making commuting more efficient, cost-effective, and environmentally friendly. The platform allows users to create and join rides effortlessly by leveraging real-time geolocation, secure payment integration, and smart matching algorithms.

Whether you're a driver with empty seats or a passenger looking for a convenient ride, CarHov streamlines the process with an intuitive interface and powerful backend services built on Spring Boot. Users can easily search for rides based on their origin, destination, and timing preferences, making daily commuting or intercity travel a seamless experience.

This repository contains the backend API and services. The front-end repository can be found here [link to be updated].

## Features

### Core Functionality
- **User Management**
  - Registration, authentication, and profile management
  - Role-based access control (User, Admin, Super Admin)
  - JWT-based authentication with refresh tokens

- **Ride Management**
  - Create, update, and delete ride schedules
  - Recurring ride scheduling (daily, weekly patterns)
  - Seat availability management

- **Search & Matching**
  - Geolocation-based ride search with customizable radius
  - Advanced filtering by departure time and available seats
  - Matching algorithm based on proximity and schedules

- **Geographic Services**
  - Distance calculation using the Haversine formula
  - Proximity checks for ride search
  - Location validation

- **Ride Booking**
  - [ _still under development_ ]

### Security Features
- Secure password management with encryption
- HTTPS support for all communications
- Role-based endpoint protection
- User ownership validation for profile modifications
- Aspect-oriented security implementation

## Architecture

CarHov follows a layered architecture pattern:

```
┌───────────────────┐
│   Security Layer  │ ← Authentication, authorization, and security filters
├───────────────────┤
│   API Layer       │ ← RESTful controllers and DTOs
├───────────────────┤
│   Business Layer  │ ← Business logic and service implementations
├───────────────────┤
│   Data Layer      │ ← JPA repositories and entities
└───────────────────┘
```

### Key Components
- **Security Layer**: Authentication, authorization, JWT handling, and security aspects
- **API Layer**: RESTful controllers, DTOs, and request/response mapping
- **Service Layer**: Core business logic, validation, and service implementation
- **Data Layer**: Entities, repositories, and database interactions

## Technology Stack

- **Backend**
  - Java 21
  - Spring Boot 3.4.1
  - Spring Security with JWT
  - Spring Data JPA
  - Hibernate
  - PostgreSQL / MySQL

- **Build Tools**
  - Maven

- **Testing**
  - JUnit 5
  - Mockito
  - Testcontainers

- **DevOps & Infrastructure**
  - Docker
  - Swagger/OpenAPI for API documentation

## API Documentation

While the application is running, the Swagger UI is available at:
```
http://localhost:8080/swagger-ui/index.html
```

### Key Endpoints

#### Authentication
- `POST /api/auth/signup` - Register a new user
- `POST /api/auth/signin` - Authenticate and receive JWT token
- `POST /api/auth/refreshtoken` - Refresh an existing token

#### User Management
- `GET /api/users/{userId}` - Get user profile
- `PUT /api/users/{userId}` - Update user profile (owner only)
- `DELETE /api/users/{userId}` - Delete user profile (owner only)

#### Admin Operations
- `GET /api/admin/users` - Get all admin users
- `POST /api/admin/users/create` - Create admin user
- `GET /api/admin/users/{id}` - Get admin user by ID
- `PUT /api/admin/users/{id}` - Update admin user
- `DELETE /api/admin/users/{id}` - Delete admin user

#### Ride Scheduling
- `POST /api/rides` - Create a new ride schedule
- `GET /api/rides/search` - Search for rides based on location and criteria
- `GET /api/rides/{id}` - Get a specific ride schedule
- `PUT /api/rides/{id}` - Update a ride schedule
- `DELETE /api/rides/{id}` - Delete a ride schedule

## Setup and Installation

### Prerequisites
- Java Development Kit (JDK) 21
- Maven or Gradle (build tools)
- MySQL/PostgreSQL database server
- IDE (IntelliJ IDEA recommended)

### Step 1: Clone the Repository
```bash
git clone <repository-url>
cd carhov
```

### Step 2: Database Configuration
Configure your database connection in `src/main/resources/application.properties`:

```properties
# Database settings
spring.datasource.url=jdbc:mysql://localhost:3306/carhov_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Hibernate settings
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

### Step 3: JWT Configuration
Set up your JWT configuration in the same properties file:

```properties
# JWT settings
carhov.app.jwtSecret=your_jwt_secret_key
carhov.app.jwtExpirationMs=86400000
carhov.app.jwtRefreshExpirationMs=604800000
```

### Step 4: Build and Run
Using Maven:
```bash
mvn clean install
mvn spring-boot:run
```

Using your IDE:
- Open the project in your IDE
- Run the `CarhovApplication.java` main class

### Step 5: Verify Installation
Once the application is running, access:
```
http://localhost:8080/api/version
```

Expected response:
```json
{
    "application": "carhov",
    "version": "0.0.1-SNAPSHOT"
}
```

### Step 6: Run Tests
Using Maven, while docker desktop is also running on your computer:
```bash
mvn clean test
```

## Development Guidelines

### Coding Standards
- Follow Java naming conventions
- Use descriptive names for classes, methods, and variables
- Apply proper exception handling

### Git Workflow
- Create feature branches from `main`
- Use meaningful commit messages
- Submit PRs for code review before merging

### DTO Pattern
- Use DTOs for all API requests and responses
- Use mappers to convert between DTOs and entities
- Validate DTOs at the controller level

## Testing

### Unit Testing
Unit tests should be written for:
- Service classes
- Repository custom methods
- Utility classes
- Security components

Example running tests:
```bash
mvn test
```

### Integration Testing
Integration tests use Testcontainers to spin up databases:
```bash
mvn verify
```

## Security

CarHov implements several security measures:

1. **Authentication**: JWT-based with refresh tokens
2. **Authorization**: Role-based access control
3. **Resource Protection**: Aspect-based user ownership validation
4. **Password Security**: BCrypt password encoding
5. **Input Validation**: Request validation via annotations

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request
6. Wait for my review 


## Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.1/maven-plugin)
* [Spring Boot Testcontainers support](https://docs.spring.io/spring-boot/3.4.1/reference/testing/testcontainers.html#testing.testcontainers)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.1/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [Spring Security](https://docs.spring.io/spring-boot/3.4.1/reference/web/spring-security.html)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.1/reference/web/servlet.html)

