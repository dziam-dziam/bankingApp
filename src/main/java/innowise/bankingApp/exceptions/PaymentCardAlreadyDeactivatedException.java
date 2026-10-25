package innowise.bankingApp.exceptions;

public class PaymentCardAlreadyDeactivatedException extends RuntimeException {
    public PaymentCardAlreadyDeactivatedException() {
        super("Payment card is already deactivated");
    }
}
