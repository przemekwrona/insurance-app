package pl.wrona.insurance.account;

import io.restassured.RestAssured;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerIntegrationTest {
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
    @Sql({"/sql/test_account_details.sql"})
    void shouldThrowErrorIfAccountDoesNotExists() {
        get("/api/v1/accounts/5079bcf9-d913-4fa5-ac96-4bb89f452775")
                .then()
                .statusCode(200)
                .assertThat()
                .body("owner.firstname", equalTo("Laren"))
                .body("owner.surname", equalTo("Pateman"))
                .body("accountNumber", equalTo("5079bcf9-d913-4fa5-ac96-4bb89f452775"))
                .body("subAccounts[0].currencyCode", equalTo("PLN"))
                .body("subAccounts[0].amount", equalTo(120.00F))
                .body("subAccounts[1].currencyCode", equalTo("USD"))
                .body("subAccounts[1].amount", equalTo(40.00F));
    }

}