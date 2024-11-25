package pl.wrona.insurance.exchange;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wrona.insurance.BusinessException;
import pl.wrona.insurance.DateProvider;
import pl.wrona.insurance.account.SubAccountService;
import pl.wrona.insurance.api.model.ExchangeMoneyAccountStatus;
import pl.wrona.insurance.api.model.ExchangeMoneyRequest;
import pl.wrona.insurance.api.model.ExchangeMoneyResponse;
import pl.wrona.insurance.api.model.ExchangeRequest;
import pl.wrona.insurance.api.model.ExchangeResponse;
import pl.wrona.insurance.account.CurrencyCode;
import pl.wrona.insurance.account.SubAccount;
import pl.wrona.insurance.client.NbpCantorService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ExchangeService {

    private final NbpCantorService nbpCantorService;
    private final SubAccountService subAccountService;
    private final DateProvider dateProvider;

    public ExchangeResponse exchange(ExchangeRequest exchangeRequest) {
        return exchange(exchangeRequest, dateProvider.now());
    }

    @SneakyThrows
    public ExchangeResponse exchange(ExchangeRequest exchangeRequest, LocalDate exchangeDate) {
        BigDecimal exchangeRate = nbpCantorService.getExchangeRates(exchangeRequest.getSourceCurrencyCode(), exchangeRequest.getTargetCurrencyCode(), exchangeDate);

        return new ExchangeResponse()
                .sourceCurrencyCode(exchangeRequest.getSourceCurrencyCode())
                .targetCurrencyCode(exchangeRequest.getTargetCurrencyCode())
                .sourceAmount(exchangeRequest.getAmount().setScale(2, RoundingMode.HALF_UP))
                .targetAmount(exchangeRequest.getAmount().multiply(exchangeRate).setScale(2, RoundingMode.DOWN));
    }

    @SneakyThrows
    @Transactional
    public ExchangeMoneyResponse exchangeMoney(UUID accountId, ExchangeMoneyRequest exchangeMoneyRequest) {
        List<String> errors = new ArrayList<>();

        CurrencyCode sourceCurrencyCode = null;
        try {
            sourceCurrencyCode = CurrencyCode.valueOf(exchangeMoneyRequest.getSourceCurrencyCode());
        } catch (IllegalArgumentException ex) {
            errors.add("Sub account for currency %s is not supported".formatted(exchangeMoneyRequest.getSourceCurrencyCode()));
        }

        CurrencyCode targetCurrencyCode = null;
        try {
            targetCurrencyCode = CurrencyCode.valueOf(exchangeMoneyRequest.getTargetCurrencyCode());
        } catch (IllegalArgumentException ex) {
            errors.add("Sub account for currency %s is not supported".formatted(exchangeMoneyRequest.getTargetCurrencyCode()));
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(HttpStatus.FORBIDDEN, errors);
        }

        if (!subAccountService.hasSubAccount(accountId, sourceCurrencyCode)) {
            errors.add("Account %s does not have assigned sub account for currency %s".formatted(accountId, exchangeMoneyRequest.getSourceCurrencyCode()));
        }

        if (!subAccountService.hasSubAccount(accountId, targetCurrencyCode)) {
            errors.add("Account %s does not have assigned sub account for currency %s".formatted(accountId, exchangeMoneyRequest.getTargetCurrencyCode()));
        }

        if (!errors.isEmpty()) {
            throw new BusinessException(HttpStatus.FORBIDDEN, errors);
        }

        SubAccount sourceSubAccount = subAccountService.findSubAccount(accountId, sourceCurrencyCode);
        SubAccount targetSubAccount = subAccountService.findSubAccount(accountId, targetCurrencyCode);

        if (sourceSubAccount.getAmount().compareTo(exchangeMoneyRequest.getAmount()) < 0) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "There are insufficient funds in account number %s maintained in %s.".formatted(accountId, sourceCurrencyCode));
        }

        ExchangeRequest exchangeRequest = new ExchangeRequest()
                .sourceCurrencyCode(exchangeMoneyRequest.getSourceCurrencyCode())
                .targetCurrencyCode(exchangeMoneyRequest.getTargetCurrencyCode())
                .amount(exchangeMoneyRequest.getAmount());

        ExchangeResponse exchangeResponse = exchange(exchangeRequest, dateProvider.now());

        sourceSubAccount.setAmount(sourceSubAccount.getAmount().subtract(exchangeMoneyRequest.getAmount()).setScale(2, RoundingMode.HALF_UP));
        targetSubAccount.setAmount(targetSubAccount.getAmount().add(exchangeResponse.getTargetAmount()).setScale(2, RoundingMode.HALF_UP));

        SubAccount savedSourceSubAccount = subAccountService.save(sourceSubAccount);
        SubAccount savedTargetSubAccount = subAccountService.save(targetSubAccount);

        return new ExchangeMoneyResponse()
                .accountId(accountId)
                .addAccountsItem(new ExchangeMoneyAccountStatus()
                        .currencyCode(savedSourceSubAccount.getSubAccountId().getCurrencyCode().name())
                        .amount(savedSourceSubAccount.getAmount().setScale(2, RoundingMode.HALF_UP)))
                .addAccountsItem(new ExchangeMoneyAccountStatus()
                        .currencyCode(savedTargetSubAccount.getSubAccountId().getCurrencyCode().name())
                        .amount(savedTargetSubAccount.getAmount().setScale(2, RoundingMode.HALF_UP)));
    }
}
