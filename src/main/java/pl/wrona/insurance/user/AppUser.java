package pl.wrona.insurance.user;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.wrona.insurance.account.Account;

import java.util.Set;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUser {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "app_user_id_sequence")
    @SequenceGenerator(name = "app_user_id_sequence", sequenceName = "app_user_id_seq", allocationSize = 1)
    private Long userId;

    private String firstname;

    private String surname;

    @OneToMany(mappedBy = "appUser")
    private Set<Account> accounts;
}
