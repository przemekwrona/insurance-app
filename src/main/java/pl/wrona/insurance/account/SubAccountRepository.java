package pl.wrona.insurance.account;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SubAccountRepository extends JpaRepository<SubAccount, SubAccountId> {

    @Query("select case when count(sa) > 0 then true else false end from SubAccount sa WHERE sa.subAccountId.account.accountNumber = :accountNumber AND sa.subAccountId.currencyCode = :currencyCode")
    boolean existsByAccountNumberAndCurrencyCode(@Param("accountNumber") UUID accountNumber, @Param("currencyCode") CurrencyCode currencyCode);

    @Query("select sa from SubAccount sa WHERE sa.subAccountId.account.accountNumber = :accountNumber AND sa.subAccountId.currencyCode = :currencyCode")
    SubAccount findByAccountNumberAndCurrencyCode(@Param("accountNumber") UUID accountNumber, @Param("currencyCode") CurrencyCode currencyCode);
}
