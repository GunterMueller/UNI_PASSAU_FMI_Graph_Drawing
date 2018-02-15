package quoggles.exceptions;

/**
 * Indicates that the elements of a collection do not have compatible types.
 */
public class NoHomogeneousTypeException extends QueryExecutionException {

    /**
     * 
     */
    public NoHomogeneousTypeException() {
        super();
    }

    /**
     * @param message
     */
    public NoHomogeneousTypeException(String message) {
        super(message);
    }

}
