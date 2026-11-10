package innowise.bankingApp.exception;

import lombok.Getter;

@Getter
public class PaymentCardAlreadyDeactivatedException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.PAYMENT_CARD_ALREADY_DEACTIVATED;

    public PaymentCardAlreadyDeactivatedException() {
        super(ErrorCode.PAYMENT_CARD_ALREADY_DEACTIVATED.formatMessage());
    }

}