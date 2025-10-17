package exceptions;

/** Thrown when attempting to place/move a resident into an occupied bed. */
public class BedOccupiedException extends Exception {
    private static final long serialVersionUID = 1L;

    public BedOccupiedException(String message) {
        super(message);
    }
}
