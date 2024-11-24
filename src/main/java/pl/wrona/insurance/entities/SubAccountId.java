package pl.wrona.insurance.entities;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Builder;

import java.io.Serializable;

@Builder
@Embeddable
public class SubAccountId implements Serializable {

    private String currencyCode;

    @ManyToOne
    @JoinColumn(name = "account_number", nullable = false)
    private Account account;

}
