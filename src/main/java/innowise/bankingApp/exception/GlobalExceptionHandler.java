package innowise.bankingApp.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
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

    @ExceptionHandler(ItemNotFoundByIdException.class)
    public ProblemDetail handleItemNotFound(
            ItemNotFoundByIdException exception
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(OrderNotFoundByIdException.class)
    public ProblemDetail handleOrderNotFound(
            OrderNotFoundByIdException exception
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                exception.getMessage()
        );
    }

    @ExceptionHandler(ItemInUseException.class)
    public ProblemDetail handleItemInUse(
            ItemInUseException exception
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                exception.getMessage()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail handleDataConflict(
            DataIntegrityViolationException exception
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "The operation violates a database constraint"
        );
    }

    @ExceptionHandler({
            ConstraintViolationException.class,
            IllegalArgumentException.class,
            PropertyReferenceException.class
    })
    public ProblemDetail handleInvalidRequest(
            RuntimeException exception
    ) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                "Invalid request or sort property"
        );
    }
}