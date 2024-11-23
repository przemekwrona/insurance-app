package pl.wrona.insurance.user;

import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import pl.wrona.insurance.api.UserApi;
import pl.wrona.insurance.api.model.CreateUserRequest;
import pl.wrona.insurance.api.model.CreateUserResponse;

@RestController
@AllArgsConstructor
public class UserController implements UserApi {

    private final UserService userService;

    @Override
    public ResponseEntity<CreateUserResponse> createUser(CreateUserRequest createUserRequest) {
        return ResponseEntity.ok(userService.createUser(createUserRequest));
    }
}
