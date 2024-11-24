package pl.wrona.insurance;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@AllArgsConstructor
public class BusinessException extends Exception {

    private HttpStatus httpStatus;
    private List<String> messages;

    public BusinessException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.messages = List.of(message);
    }
}
