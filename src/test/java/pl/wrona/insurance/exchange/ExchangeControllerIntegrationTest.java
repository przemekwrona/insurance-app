package pl.wrona.insurance.exchange;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.wrona.insurance.DateProvider;
import pl.wrona.insurance.api.model.ExchangeMoneyRequest;
import pl.wrona.insurance.client.NbpClient;
import pl.wrona.nbp.api.model.NbpExchangeRates;
import pl.wrona.nbp.api.model.NbpExchangeResponse;

import java.math.BigDecimal;
import java.time.LocalDate;

import static io.restassured.RestAssured.with;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ExchangeControllerIntegrationTest {

    @MockBean
    NbpClient nbpClient;

    @MockBean
    DateProvider dateProvider;

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
        LocalDate now = LocalDate.of(2024, 11, 10);

        NbpExchangeResponse nbpExchangeResponse = new NbpExchangeResponse()
                .table("C")
                .currency("dolar amerykański")
                .code("USD")
                .addRatesItem(new NbpExchangeRates()
                        .no("225/C/NBP/2024")
                        .effectiveDate(now)
                        .bid(BigDecimal.valueOf(4.0571))
                        .ask(BigDecimal.valueOf(4.1391)));

        when(dateProvider.now()).thenReturn(now);
        when(nbpClient.exchangeRatesByDay("USD", now, "json")).thenReturn(ResponseEntity.ok(nbpExchangeResponse));

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
                .body("accountId", equalTo("db9d12f4-d3ee-4baf-8a16-f21f969deb9d"))
                .body("accounts", hasSize(2))
                .body("accounts[0].currencyCode", equalTo("PLN"))
                .body("accounts[0].amount", equalTo(110.00F))
                .body("accounts[1].currencyCode", equalTo("USD"))
                .body("accounts[1].amount", equalTo(42.46F));
    }

}