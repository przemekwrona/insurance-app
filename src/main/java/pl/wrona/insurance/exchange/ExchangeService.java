package pl.wrona.insurance.exchange;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wrona.insurance.api.model.ExchangeRequest;
import pl.wrona.insurance.api.model.ExchangeResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;

@Service
@AllArgsConstructor
public class ExchangeService {

    private final NbpCantorService nbpCantorService;

    public ExchangeResponse exchange(ExchangeRequest exchangeRequest) {
        LocalDate exchangeDate = LocalDate.now().minusDays(1L);
        return exchange(exchangeRequest, exchangeDate);
    }

    public ExchangeResponse exchange(ExchangeRequest exchangeRequest, LocalDate exchangeDate) {
        BigDecimal exchangeRate = nbpCantorService.getExchangeRates(exchangeRequest.getSourceCurrencyCode(), exchangeRequest.getTargetCurrencyCode(), exchangeDate);

        return new ExchangeResponse()
                .sourceCurrencyCode(exchangeRequest.getSourceCurrencyCode())
                .targetCurrencyCode(exchangeRequest.getTargetCurrencyCode())
                .sourceAmount(exchangeRequest.getAmount().setScale(2, RoundingMode.HALF_UP))
                .targetAmount(exchangeRequest.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.DOWN));
    }
}
