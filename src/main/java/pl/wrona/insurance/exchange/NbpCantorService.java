package pl.wrona.insurance.exchange;

import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.wrona.insurance.client.NbpClient;
import pl.wrona.nbp.api.model.NbpExchangeRates;
import pl.wrona.nbp.api.model.NbpExchangeResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@AllArgsConstructor
public class NbpCantorService {

    public static final String DEFAULT_NBP_RESPONSE_FORMAT = "json";
    public static final String PLN_CURRENCY_CODE = "PLN";
    public static final String PLN_CURRENCY_NAME = "złoty polski";
    public static final String NBP_TABLE_SOURCE_C = "C";
    private final NbpClient nbpClient;

    @Cacheable(value = "nbp_exchange_rate")
    public NbpExchangeResponse exchangeRates(String currencyCode, LocalDate exchangeDate) {
        if (PLN_CURRENCY_CODE.equalsIgnoreCase(currencyCode)) {
            return new NbpExchangeResponse()
                    .code(PLN_CURRENCY_CODE)
                    .currency(PLN_CURRENCY_NAME)
                    .table(NBP_TABLE_SOURCE_C)
                    .addRatesItem(new NbpExchangeRates()
                            .no(getPlnNo(exchangeDate))
                            .ask(BigDecimal.ONE)
                            .bid(BigDecimal.ONE)
                            .effectiveDate(exchangeDate));

        }
        return nbpClient.exchangeRatesByDay(currencyCode, exchangeDate, DEFAULT_NBP_RESPONSE_FORMAT).getBody();
    }

    public BigDecimal getExchangeRates(String sellCurrencyCode, String buyCurrencyCode, LocalDate exchangeDate) {
        NbpExchangeResponse sellCurrencyRate = exchangeRates(sellCurrencyCode, exchangeDate);
        NbpExchangeResponse buyCurrencyResponse = exchangeRates(buyCurrencyCode, exchangeDate);

        BigDecimal sellExchangeRate = sellCurrencyRate.getRates().stream()
                .findFirst()
                .map(NbpExchangeRates::getAsk)
                .orElseThrow();

        BigDecimal buyExchangeRate = buyCurrencyResponse.getRates().stream()
                .findFirst()
                .map(NbpExchangeRates::getBid)
                .orElseThrow();

        return sellExchangeRate.divide(buyExchangeRate, 5, RoundingMode.HALF_UP);
    }

    private String getPlnNo(LocalDate exchangeDate) {
        return "%s/%s/NBP/%s".formatted(exchangeDate.getDayOfYear(), NBP_TABLE_SOURCE_C, exchangeDate.getYear());
    }
}
