package innowise.bankingApp.exceptions;

public class PaymentCardAlreadyActiveException extends RuntimeException {
    public PaymentCardAlreadyActiveException( ) {
        super("Pyment card is already active");
    }
}
