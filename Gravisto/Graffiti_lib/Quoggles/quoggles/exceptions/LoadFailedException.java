package quoggles.exceptions;

import java.io.IOException;

/**
 * Thrown if loading of a file was not successful. 
 */
public class LoadFailedException extends IOException {

    /**
     * @param message
     */
    public LoadFailedException(String message) {
        super(message);
    }

    /**
     * @param cause
     */
    public LoadFailedException(Throwable cause) {
        super(cause.getLocalizedMessage());
    }

}
