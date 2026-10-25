package innowise.bankingApp.exceptions;

public class UserAlreadyDeactivatedException extends RuntimeException {
    public UserAlreadyDeactivatedException() {
        super("User is already deactivated.");
    }
}
