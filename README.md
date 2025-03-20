# CarHov
CarHov is a modern carpooling application designed to bridge the gap 
between drivers and passengers, making commuting more efficient, 
cost-effective, and environmentally friendly. The platform allows 
users to create and join rides effortlessly by leveraging real-time 
geolocation, secure payment integration, and smart matching algorithms. 

Whether you're a driver with empty seats or a passenger looking for a 
convenient ride, CarHov streamlines the process with an intuitive
interface and powerful backend services built on Spring Boot. 
Users can easily search for rides based on their origin, destination, and 
timing preferences, making daily commuting or intercity travel a seamless experience.

This repository is strictly for CarHov backend api and services. The front-end
can be found here... [link to be updated]


## Steps to Run CarHov Locally
### Step 1: Prerequisites
1. **Install Java Development Kit (JDK):**
    - Ensure Java 21 is installed.
    - Verify by running:
      ```bash
      java -version
      ```

2. **Install a Build Tool:**
    - Use either **Maven** or **Gradle**, depending on your project setup.
    - Verify installation with:
      ```bash
      mvn -v   # For Maven
      gradle -v  # For Gradle
      ```

3. **Set Up Your IDE:**
    - Use **IntelliJ IDEA** or another Java IDE.
    - Install necessary plugins for Spring Boot (e.g., Spring Assistant).

4. **Database Setup (if required):**
    - Ensure your database (e.g., MySQL, PostgreSQL) is installed and running.
    - Create a database for the application and note its connection details.

---

### Step 2: Import the Project
1. **Download or Clone the Project:**
    - If it's hosted on GitHub or another repository, clone it:
      ```bash
      git clone <repository-url>
      cd <project-folder>
      ```

2. **Open the Project in IntelliJ:**
    - Select "Open or Import" and choose the project folder.
    - Wait for IntelliJ to index files and download dependencies (if using Maven/Gradle).

---

### Step 3: Configure
1. Start a local postgres db
```bash
   make start-db
```

### Step 4: Build the Application

Using Maven:
Open a terminal in the project folder and run:
`mvn clean install`

Fix any build errors before proceeding.

---

### Step 5: Run the Application

Using IntelliJ:
Locate the main class `CarhovApplication.java`.
Right-click and select Run.

Using Command Line:
Navigate to the project folder and run:
Maven:
`mvn spring-boot:run`

---
### Step 6: Verify the Application

Open a browser or API testing tool (e.g., Postman).
Access the version URL:
http://localhost:8080/api/version

Response should look like:
```json
{
    "application": "carhov",
    "version": "0.0.1-SNAPSHOT"
}
```

---

# Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/3.4.1/maven-plugin)
* [Create an OCI image](https://docs.spring.io/spring-boot/3.4.1/maven-plugin/build-image.html)
* [Spring Boot Testcontainers support](https://docs.spring.io/spring-boot/3.4.1/reference/testing/testcontainers.html#testing.testcontainers)
* [Testcontainers Postgres Module Reference Guide](https://java.testcontainers.org/modules/databases/postgres/)
* [Spring Data JPA](https://docs.spring.io/spring-boot/3.4.1/reference/data/sql.html#data.sql.jpa-and-spring-data)
* [JTE](https://jte.gg/)
* [OAuth2 Client](https://docs.spring.io/spring-boot/3.4.1/reference/web/spring-security.html#web.security.oauth2.client)
* [Spring Security](https://docs.spring.io/spring-boot/3.4.1/reference/web/spring-security.html)
* [Testcontainers](https://java.testcontainers.org/)
* [Spring Web](https://docs.spring.io/spring-boot/3.4.1/reference/web/servlet.html)

### Guides

The following guides illustrate how to use some features concretely:

* [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
* [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
* [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
* [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
* [Building a RESTful Web Service](https://spring.io/guides/gs/rest-service/)
* [Serving Web Content with Spring MVC](https://spring.io/guides/gs/serving-web-content/)
* [Building REST services with Spring](https://spring.io/guides/tutorials/rest/)

## JTE

This project has been configured to use [JTE precompiled templates](https://jte.gg/pre-compiling/).

However, to ease development, those are not enabled out of the box.
For production deployments, you should remove

```properties
gg.jte.development-mode=true
```

from the `application.properties` file and set

```properties
gg.jte.use-precompiled-templates=true
```

instead.
For more details, please take a look at [the official documentation](https://jte.gg/spring-boot-starter-3/).

### Testcontainers support

This project
uses [Testcontainers at development time](https://docs.spring.io/spring-boot/3.4.1/reference/features/dev-services.html#features.dev-services.testcontainers).

Testcontainers has been configured to use the following Docker images:

* [`postgres:latest`](https://hub.docker.com/_/postgres)

Please review the tags of the used images and set them to the same as you're running in production.

### Maven Parent overrides

Due to Maven's design, elements are inherited from the parent POM to the project POM.
While most of the inheritance is fine, it also inherits unwanted elements like `<license>` and `<developers>` from the
parent.
To prevent this, the project POM contains empty overrides for these elements.
If you manually switch to a different parent and actually want the inheritance, you need to remove those overrides.

