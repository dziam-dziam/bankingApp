package innowise.bankingApp.exceptions;

public class UserAlreadyActiveException extends RuntimeException {
    public UserAlreadyActiveException() {
        super("User is already active");
    }
}
