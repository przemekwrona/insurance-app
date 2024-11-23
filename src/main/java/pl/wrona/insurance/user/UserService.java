package pl.wrona.insurance.user;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.wrona.insurance.api.model.CreateUserRequest;
import pl.wrona.insurance.api.model.CreateUserResponse;

@Service
@AllArgsConstructor
public class UserService {

    public CreateUserResponse createUser(CreateUserRequest createUserRequest) {
        return null;
    }
}
