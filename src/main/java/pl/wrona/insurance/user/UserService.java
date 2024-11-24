package pl.wrona.insurance.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.wrona.insurance.account.AccountRepository;
import pl.wrona.insurance.api.model.CreateUserRequest;
import pl.wrona.insurance.api.model.CreateUserResponse;
import pl.wrona.insurance.account.Account;
import pl.wrona.insurance.account.CurrencyCode;
import pl.wrona.insurance.account.SubAccount;
import pl.wrona.insurance.account.SubAccountId;

import java.math.BigDecimal;
import java.util.HashSet;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {

        AppUser newAppUser = AppUser.builder()
                .firstname(createUserRequest.getFirstname())
                .surname(createUserRequest.getSurname())
                .build();

        userRepository.save(newAppUser);

        Account newAccount = Account.builder()
                .appUser(newAppUser)
                .subAccounts(new HashSet<>())
                .build();

        SubAccount subAccountPln = SubAccount.builder()
                .subAccountId(SubAccountId.builder()
                        .account(newAccount)
                        .currencyCode(CurrencyCode.PLN)
                        .build())
                .amount(createUserRequest.getAmount())
                .build();

        newAccount.getSubAccounts().add(subAccountPln);

        SubAccount subAccountUsd = SubAccount.builder()
                .subAccountId(SubAccountId.builder()
                        .account(newAccount)
                        .currencyCode(CurrencyCode.USD)
                        .build())
                .amount(BigDecimal.ZERO)
                .build();

        newAccount.getSubAccounts().add(subAccountUsd);

        Account savedAccount = accountRepository.save(newAccount);

        return new CreateUserResponse()
                .accountNumber(savedAccount.getAccountNumber());
    }
}
