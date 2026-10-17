package innowise.bankingApp.Exceptions;

public class PaymentCardNotFoundByIdException extends RuntimeException {
    public PaymentCardNotFoundByIdException(Long id) {
        super("Payment card with id: " + id + " was not found");
    }
}
