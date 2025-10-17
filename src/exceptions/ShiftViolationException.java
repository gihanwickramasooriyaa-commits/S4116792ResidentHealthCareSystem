package exceptions;

/** Thrown when staff shift rules (e.g., >8h/day) are violated. */
public class ShiftViolationException extends Exception {
    private static final long serialVersionUID = 1L;

    public ShiftViolationException(String message) {
        super(message);
    }
}
