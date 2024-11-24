package pl.wrona.insurance.account;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubAccount {

    @EmbeddedId
    private SubAccountId subAccountId;

    private BigDecimal amount;
}
