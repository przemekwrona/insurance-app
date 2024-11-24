package pl.wrona.insurance.account;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SubAccountService {

    private final SubAccountRepository subAccountRepository;

    @Transactional
    public boolean hasSubAccount(UUID accountNumber, CurrencyCode currencyCode) {
        return subAccountRepository.existsByAccountNumberAndCurrencyCode(accountNumber, currencyCode);
    }

    @Transactional
    public SubAccount findSubAccount(UUID accountNumber, CurrencyCode currencyCode) {
        return subAccountRepository.findByAccountNumberAndCurrencyCode(accountNumber, currencyCode);
    }

    @Transactional
    public SubAccount save(SubAccount subAccount) {
        return subAccountRepository.save(subAccount);
    }
}
