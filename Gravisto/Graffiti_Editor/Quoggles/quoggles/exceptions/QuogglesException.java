/*
 * 
 */
package quoggles.exceptions;

/**
 *
 */
public class QuogglesException extends Exception {

    /**
     * 
     */
    public QuogglesException() {
        super();
    }

    /**
     * @param message
     */
    public QuogglesException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public QuogglesException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause
     */
    public QuogglesException(Throwable cause) {
        super(cause);
    }

}
