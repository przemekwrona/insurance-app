package pl.wrona.insurance.account;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.wrona.insurance.user.AppUser;

import java.util.Set;
import java.util.UUID;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID accountNumber;

    @ManyToOne
    @JoinColumn(name="user_id", nullable = false)
    private AppUser appUser;

    @OneToMany(mappedBy = "subAccountId.account", cascade = CascadeType.ALL)
    private Set<SubAccount> subAccounts;
}
