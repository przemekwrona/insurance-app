package pl.wrona.insurance;

import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DateProvider {

    public LocalDate now() {
        return LocalDate.now();
    }
}
