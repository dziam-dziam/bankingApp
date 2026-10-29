package innowise.bankingApp.exceptions;

import lombok.Getter;

@Getter
public class UserAlreadyDeactivatedException extends RuntimeException {

    private final ErrorCode errorCode = ErrorCode.USER_ALREADY_DEACTIVATED;

    public UserAlreadyDeactivatedException() {
        super(ErrorCode.USER_ALREADY_DEACTIVATED.formatMessage());
    }

}