package innowise.bankingApp.exception;

import lombok.Getter;

@Getter
public class ItemInUseException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.ITEM_IN_USE;

    public ItemInUseException(Long id) {
        super(ErrorCode.ITEM_IN_USE.formatMessage(id));
    }
}