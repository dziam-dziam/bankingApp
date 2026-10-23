package innowise.bankingApp.exceptions;

public class TooManyPaymentCardsException extends RuntimeException {
    public TooManyPaymentCardsException() {
        super("User cannot have more than 5 cards assigned to them.");
    }
}
