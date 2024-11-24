package pl.wrona.insurance.exchange;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.wrona.insurance.api.ExchangeApi;
import pl.wrona.insurance.api.model.ExchangeMoneyRequest;
import pl.wrona.insurance.api.model.ExchangeMoneyResponse;
import pl.wrona.insurance.api.model.ExchangeRequest;
import pl.wrona.insurance.api.model.ExchangeResponse;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class ExchangeController implements ExchangeApi {

    private final ExchangeService exchangeService;

    @Override
    public ResponseEntity<ExchangeResponse> exchange(ExchangeRequest exchangeRequest) {
        return ResponseEntity.ok(exchangeService.exchange(exchangeRequest));
    }

    @Override
    public ResponseEntity<ExchangeMoneyResponse> exchangeMoney(UUID accountId, ExchangeMoneyRequest exchangeMoneyRequest) {
        return ResponseEntity.ok(exchangeService.exchangeMoney(accountId, exchangeMoneyRequest));
    }
}
