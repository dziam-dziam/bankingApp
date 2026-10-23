package innowise.bankingApp.exceptions;

public class PaymentCardNotFoundByIdException extends RuntimeException {
    public PaymentCardNotFoundByIdException(Long id) {
        super("Payment card with id: " + id + " was not found");
    }
}
