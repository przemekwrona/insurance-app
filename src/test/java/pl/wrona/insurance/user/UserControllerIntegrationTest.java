package pl.wrona.insurance.user;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.wrona.insurance.api.model.CreateUserRequest;

import java.math.BigDecimal;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class UserControllerIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
    }

    @Test
    void shouldCreateUser() {
        with().contentType(ContentType.JSON)
                .body(new CreateUserRequest()
                        .firstname("Jan")
                        .surname("Kowalski")
                        .amount(BigDecimal.valueOf(82.11)))
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(200)
                .assertThat()
                .body("accountNumber", notNullValue());
    }

    @Test
    void shouldThrowErrorBecauseFirstnameIsRequired() {
        with().contentType(ContentType.JSON)
                .body(new CreateUserRequest()
                        .surname("Kowalski")
                        .amount(BigDecimal.valueOf(102.32)))
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(400)
                .assertThat()
                .body("detail", equalTo("Invalid request content."));
    }

    @Test
    void shouldThrowErrorBecauseSurnameIsRequired() {
        with().contentType(ContentType.JSON)
                .body(new CreateUserRequest()
                        .firstname("Jan")
                        .amount(BigDecimal.valueOf(102.32)))
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(400)
                .assertThat()
                .body("detail", equalTo("Invalid request content."));
    }

    @Test
    void shouldThrowErrorBecauseAmountIsRequired() {
        with().contentType(ContentType.JSON)
                .body(new CreateUserRequest()
                        .firstname("Jan")
                        .surname("Kowlaksi"))
                .when()
                .post("/api/v1/users")
                .then()
                .statusCode(400)
                .assertThat()
                .body("detail", equalTo("Invalid request content."));
    }

}