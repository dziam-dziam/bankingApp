package innowise.bankingApp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(UserNotFoundByIdException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundByIdException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(PaymentCardNotFoundByIdException.class)
    public ProblemDetail handlePaymentCardNotFound(PaymentCardNotFoundByIdException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(TooManyPaymentCardsException.class)
    public ProblemDetail handleTooManyPaymentCards(TooManyPaymentCardsException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(PaymentCardAlreadyActiveException.class)
    public ProblemDetail handlePaymentCardAlreadyActive(PaymentCardAlreadyActiveException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(PaymentCardAlreadyDeactivatedException.class)
    public ProblemDetail handlePaymentCardAlreadyDeactivated(PaymentCardAlreadyDeactivatedException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(UserAlreadyActiveException.class)
    public ProblemDetail handleUserAlreadyActive(UserAlreadyActiveException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }

    @ExceptionHandler(UserAlreadyDeactivatedException.class)
    public ProblemDetail handleUserAlreadyDeactivated(UserAlreadyDeactivatedException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
    }
}