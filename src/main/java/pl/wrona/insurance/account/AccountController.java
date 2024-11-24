package pl.wrona.insurance.account;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.wrona.insurance.api.AccountApi;
import pl.wrona.insurance.api.model.AccountDetailsResponse;

import java.util.UUID;

@RestController
@AllArgsConstructor
public class AccountController implements AccountApi {

    private final AccountService accountService;

    @Override
    public ResponseEntity<AccountDetailsResponse> findAccountById(UUID accountNumber) {
        return ResponseEntity.ok(accountService.findAccountById(accountNumber));
    }
}
