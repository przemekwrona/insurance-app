package pl.wrona.insurance.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Builder
@Embeddable
@NoArgsConstructor
@AllArgsConstructor
public class SubAccountId implements Serializable {

    @Enumerated(EnumType.STRING)
    private CurrencyCode currencyCode;

    @ManyToOne
    @JoinColumn(name = "account_number", nullable = false)
    private Account account;

}
