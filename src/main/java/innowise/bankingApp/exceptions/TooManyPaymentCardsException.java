package innowise.bankingApp.exceptions;

import lombok.Getter;

@Getter
public class TooManyPaymentCardsException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.TOO_MANY_PAYMENT_CARDS;

    public TooManyPaymentCardsException() {
        super(ErrorCode.TOO_MANY_PAYMENT_CARDS.formatMessage());
    }

}