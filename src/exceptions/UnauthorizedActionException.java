package exceptions;

/** Thrown when a user without the right role tries to perform an action. */
public class UnauthorizedActionException extends Exception {
    private static final long serialVersionUID = 1L;

    public UnauthorizedActionException(String message) {
        super(message);
    }
}
