package pl.wrona.insurance.config;

import feign.FeignException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import pl.wrona.insurance.BusinessException;
import pl.wrona.insurance.api.model.Error;
import pl.wrona.insurance.api.model.ErrorResponse;

import java.util.List;

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@ControllerAdvice
public class InsuranceExceptionResolver extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = {BusinessException.class})
    protected ResponseEntity<Object> handleConflict(BusinessException ex, WebRequest request) {
        List<Error> errors = ex.getMessages().stream().map(msg -> new Error().message(msg)).toList();
        return handleExceptionInternal(ex, new ErrorResponse().errors(errors), new HttpHeaders(), ex.getHttpStatus(), request);
    }

    @ExceptionHandler(value = {FeignException.class})
    protected ResponseEntity<Object> handleFeignNotFound(FeignException ex, WebRequest request) {
        List<Error> errors = List.of(new Error().message("No exchange rate was found."));
        return handleExceptionInternal(ex, new ErrorResponse().errors(errors), new HttpHeaders(), SERVICE_UNAVAILABLE, request);
    }

}
