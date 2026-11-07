package innowise.bankingApp.exceptions;

import lombok.Getter;

@Getter
public class UserAlreadyActiveException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.USER_ALREADY_ACTIVE;

    public UserAlreadyActiveException() {
        super(ErrorCode.USER_ALREADY_ACTIVE.formatMessage());
    }

}