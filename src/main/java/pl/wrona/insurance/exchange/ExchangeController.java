package pl.wrona.insurance.exchange;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.wrona.insurance.api.ExchangeApi;
import pl.wrona.insurance.api.model.ExchangeRequest;
import pl.wrona.insurance.api.model.ExchangeResponse;

@RestController
@AllArgsConstructor
public class ExchangeController implements ExchangeApi {

    private final ExchangeService exchangeService;

    @Override
    public ResponseEntity<ExchangeResponse> exchange(ExchangeRequest exchangeRequest) {
        return ResponseEntity.ok(exchangeService.exchange(exchangeRequest));
    }
}
