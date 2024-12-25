package practicum.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class StatsExceptionHandler {
    @ExceptionHandler(IncorrectRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIncorrectRequestException(IncorrectRequestException e) {
        return e.getMessage();
    }

}
