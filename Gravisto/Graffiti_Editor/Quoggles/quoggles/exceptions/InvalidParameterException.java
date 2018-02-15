package quoggles.exceptions;

/**
 * Indicates that the parameters of a box are not valid (for the current input
 * etc.).
 */
public class InvalidParameterException extends QueryExecutionException {

    /**
     * 
     */
    public InvalidParameterException() {
        super();
    }

    /**
     * @param message
     */
    public InvalidParameterException(String message) {
        super(message);
    }

}
