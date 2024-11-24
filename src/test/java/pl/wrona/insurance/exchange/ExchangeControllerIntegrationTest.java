package pl.wrona.insurance.exchange;

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
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.wrona.insurance.api.model.ExchangeMoneyRequest;

import java.math.BigDecimal;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExchangeControllerIntegrationTest {

    @LocalServerPort
    private Integer port;

    @Container
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

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
    void shouldThrowErrorIfAccountDoesNotExists() {
        with().contentType(ContentType.JSON)
                .body(new ExchangeMoneyRequest()
                        .sourceCurrencyCode("PLN")
                        .targetCurrencyCode("EUR")
                        .amount(BigDecimal.valueOf(45.98)))
                .when()
                .post("/api/v1/accounts/f195d922-ff51-47f6-97e0-e6701b11783b/exchange")
                .then()
                .statusCode(403)
                .assertThat()
                .body("errors", hasSize(2))
                .body("errors.message[0]", equalTo("Account f195d922-ff51-47f6-97e0-e6701b11783b does not have assigned sub account for currency PLN"))
                .body("errors.message[1]", equalTo("Account f195d922-ff51-47f6-97e0-e6701b11783b does not have assigned sub account for currency EUR"));
    }

    @Test
    void shouldThrowErrorIfCurrencyCodeDoesNotExist() {
        with().contentType(ContentType.JSON)
                .body(new ExchangeMoneyRequest()
                        .sourceCurrencyCode("AAB")
                        .targetCurrencyCode("EUR")
                        .amount(BigDecimal.valueOf(45.98)))
                .when()
                .post("/api/v1/accounts/f195d922-ff51-47f6-97e0-e6701b11783b/exchange")
                .then()
                .statusCode(403)
                .assertThat()
                .body("errors", hasSize(1))
                .body("errors.message[0]", equalTo("Sub account for currency AAB is not supported"));
    }

    @Test
    @Sql({"/sql/test_case_exchange_if_there_is_no_money.sql"})
    void shouldThrowErrorIfThereIsNOSufficientAmountOfMoneyOnSourceSubAccount() {
        with().contentType(ContentType.JSON)
                .body(new ExchangeMoneyRequest()
                        .sourceCurrencyCode("PLN")
                        .targetCurrencyCode("USD")
                        .amount(BigDecimal.valueOf(5000.00)))
                .when()
                .post("/api/v1/accounts/784dea12-6940-4873-8bcf-216a68b57638/exchange")
                .then()
                .statusCode(403)
                .assertThat()
                .body("errors", hasSize(1))
                .body("errors.message[0]", equalTo("There are insufficient funds in account number 784dea12-6940-4873-8bcf-216a68b57638 maintained in PLN."));
    }

    @Test
    @Sql({"/sql/test_case_exchange.sql"})
    void shouldExchangeMoney() {
        with().contentType(ContentType.JSON)
                .body(new ExchangeMoneyRequest()
                        .sourceCurrencyCode("PLN")
                        .targetCurrencyCode("USD")
                        .amount(BigDecimal.valueOf(10.00)))
                .when()
                .post("/api/v1/accounts/db9d12f4-d3ee-4baf-8a16-f21f969deb9d/exchange")
                .then()
                .statusCode(200)
                .assertThat()
                .body("errors", hasSize(1))
                .body("errors.message[0]", equalTo("There are insufficient funds in account number 784dea12-6940-4873-8bcf-216a68b57638 maintained in PLN."));
    }

}