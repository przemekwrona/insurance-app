package pl.wrona.insurance.account;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wrona.insurance.api.model.AccountDetailsResponse;
import pl.wrona.insurance.api.model.AccountOwner;
import pl.wrona.insurance.api.model.SubAccountStatus;

import java.util.Comparator;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public AccountDetailsResponse findAccountById(UUID accountNumber) {
        var account = accountRepository.getReferenceById(accountNumber);

        var subAccounts = account.getSubAccounts().stream()
                .map(subAccount -> new SubAccountStatus()
                        .currencyCode(subAccount.getSubAccountId().getCurrencyCode().name())
                        .amount(subAccount.getAmount()))
                .sorted(Comparator.comparing(SubAccountStatus::getCurrencyCode))
                .toList();

        return new AccountDetailsResponse()
                .accountNumber(accountNumber)
                .owner(new AccountOwner()
                        .firstname(account.getAppUser().getFirstname())
                        .surname(account.getAppUser().getSurname()))
                .subAccounts(subAccounts);
    }
}
