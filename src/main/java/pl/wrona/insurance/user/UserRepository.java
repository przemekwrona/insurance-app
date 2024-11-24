package pl.wrona.insurance.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.wrona.insurance.entities.AppUser;

@Repository
public interface UserRepository extends JpaRepository<AppUser, Long> {
}
