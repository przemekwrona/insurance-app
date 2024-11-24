package pl.wrona.insurance.exchange;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wrona.insurance.account.SubAccountService;
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
import java.util.UUID;

@Service
@AllArgsConstructor
public class ExchangeService {

    private final NbpCantorService nbpCantorService;
    private final SubAccountService subAccountService;

    public ExchangeResponse exchange(ExchangeRequest exchangeRequest) {
        LocalDate exchangeDate = LocalDate.now().minusDays(3L);
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

    @Transactional
    public ExchangeMoneyResponse exchangeMoney(UUID accountId, ExchangeMoneyRequest exchangeMoneyRequest) {
        CurrencyCode sourceCurrencyCode = CurrencyCode.valueOf(exchangeMoneyRequest.getSourceCurrencyCode());
        CurrencyCode targetCurrencyCode = CurrencyCode.valueOf(exchangeMoneyRequest.getTargetCurrencyCode());

        if (!subAccountService.hasSubAccount(accountId, sourceCurrencyCode)) {
            return null;
        }

        if (!subAccountService.hasSubAccount(accountId, targetCurrencyCode)) {
            return null;
        }

        SubAccount sourceSubAccount = subAccountService.findSubAccount(accountId, sourceCurrencyCode);
        SubAccount targetSubAccount = subAccountService.findSubAccount(accountId, targetCurrencyCode);

        if (sourceSubAccount.getAmount().compareTo(exchangeMoneyRequest.getAmount()) < 0) {
            return null;
        }

        ExchangeRequest exchangeRequest = new ExchangeRequest()
                .sourceCurrencyCode(exchangeMoneyRequest.getSourceCurrencyCode())
                .targetCurrencyCode(exchangeMoneyRequest.getTargetCurrencyCode())
                .amount(exchangeMoneyRequest.getAmount());

        ExchangeResponse exchangeResponse = exchange(exchangeRequest, LocalDate.now().minusDays(3L));

        sourceSubAccount.setAmount(sourceSubAccount.getAmount().subtract(exchangeMoneyRequest.getAmount()));
        targetSubAccount.setAmount(targetSubAccount.getAmount().add(exchangeResponse.getTargetAmount()));

        SubAccount savedSourceSubAccount = subAccountService.save(sourceSubAccount);
        SubAccount savedTargetSubAccount = subAccountService.save(targetSubAccount);

        return new ExchangeMoneyResponse()
                .accountId(accountId);
    }
}
