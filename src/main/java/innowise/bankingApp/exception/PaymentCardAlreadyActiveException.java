package innowise.bankingApp.exception;

import lombok.Getter;

@Getter
public class PaymentCardAlreadyActiveException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.PAYMENT_CARD_ALREADY_ACTIVE;

    public PaymentCardAlreadyActiveException() {
        super(ErrorCode.PAYMENT_CARD_ALREADY_ACTIVE.formatMessage());
    }

}