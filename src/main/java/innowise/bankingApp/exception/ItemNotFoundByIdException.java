package innowise.bankingApp.exception;

import lombok.Getter;

@Getter
public class ItemNotFoundByIdException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.ITEM_NOT_FOUND;

    public ItemNotFoundByIdException(Long id) {
        super(ErrorCode.ITEM_NOT_FOUND.formatMessage(id));
    }
}