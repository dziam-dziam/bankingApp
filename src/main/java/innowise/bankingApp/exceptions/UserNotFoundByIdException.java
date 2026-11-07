package innowise.bankingApp.exceptions;

import lombok.Getter;

@Getter
public class UserNotFoundByIdException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

    public UserNotFoundByIdException(Long id) {
        super(ErrorCode.USER_NOT_FOUND.formatMessage(id));
    }

}