package innowise.bankingApp.exception;

import lombok.Getter;

@Getter
public class OrderNotFoundByIdException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.ORDER_NOT_FOUND;

    public OrderNotFoundByIdException(Long id) {
        super(ErrorCode.ORDER_NOT_FOUND.formatMessage(id));
    }
}