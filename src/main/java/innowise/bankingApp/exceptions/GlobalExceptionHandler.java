package innowise.bankingApp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UserNotFoundByIdException.class, PaymentCardNotFoundByIdException.class})
    public ProblemDetail handleNotFound(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, exception.getMessage()
        );
    }

    @ExceptionHandler({TooManyPaymentCardsException.class})
    public ProblemDetail handleConflict(RuntimeException exception) {
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, exception.getMessage()
        );
    }

    @ExceptionHandler({PaymentCardAlreadyActiveException.class})
    public ProblemDetail handleAlreadyActiveCard(RuntimeException exception){
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, exception.getMessage()
        );
    }

    @ExceptionHandler({PaymentCardAlreadyDeactivatedException.class})
    public ProblemDetail handleAlreadyDeactivatedCard(RuntimeException exception){
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, exception.getMessage()
        );
    }
    @ExceptionHandler({UserAlreadyActiveException.class})
    public ProblemDetail handleAlreadyActiveUser(RuntimeException exception){
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, exception.getMessage()
        );
    }
    @ExceptionHandler({UserAlreadyDeactivatedException.class})
    public ProblemDetail handleAlreadyDeactivatedUser(RuntimeException exception){
        return ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, exception.getMessage()
        );
    }
}
