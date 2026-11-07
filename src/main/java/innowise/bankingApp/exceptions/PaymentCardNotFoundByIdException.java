package innowise.bankingApp.exceptions;

import lombok.Getter;

@Getter
public class PaymentCardNotFoundByIdException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.PAYMENT_CARD_NOT_FOUND;

    public PaymentCardNotFoundByIdException(Long id) {
        super(ErrorCode.PAYMENT_CARD_NOT_FOUND.formatMessage(id));
    }

}