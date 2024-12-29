package practicum.util;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.InputMismatchException;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleArgumentException(MethodArgumentNotValidException e) {
        return new ErrorResponse(Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage(),
                "Not passed validation", HttpStatus.BAD_REQUEST.toString(), LocalDateTime.now());
    }

    @ExceptionHandler(AdditionalEmailValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleAdditionalEmailValidationException(AdditionalEmailValidationException e) {
        return new ErrorResponse(e.getMessage(), "Not passed validation",
                HttpStatus.BAD_REQUEST.toString(), LocalDateTime.now());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConstraintViolationException(ConstraintViolationException e) {
        return new ErrorResponse(e.getMessage(), "Integrity constraint has been violated.",
                HttpStatus.CONFLICT.toString(), LocalDateTime.now());
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleObjectNotFoundException(ObjectNotFoundException e) {
        return new ErrorResponse(e.getMessage(), "The required object was not found.",
                HttpStatus.NOT_FOUND.toString(), LocalDateTime.now());
    }

    @ExceptionHandler(InputMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInputMismatchException(InputMismatchException e) {
        return new ErrorResponse(e.getMessage(), "Incorrectly made request.",
                HttpStatus.BAD_REQUEST.toString(), LocalDateTime.now());
    }

    @ExceptionHandler(ForbiddenActionException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleForbiddenDateException(ForbiddenActionException e) {
        return new ErrorResponse(e.getMessage(), "For the requested operation the conditions are not met.",
                HttpStatus.CONFLICT.toString(), LocalDateTime.now());
    }
}
