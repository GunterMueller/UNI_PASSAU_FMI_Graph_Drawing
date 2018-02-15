/*
 * Created on 25.10.2004
 */

package org.graffiti.plugins.algorithms.betweenness;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An Exception to be thrown by the betweenness algorithm.
 * 
 * @author Markus K�ser
 * @version $Revision 1.1 $
 */
public class BetweennessException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -7919600818949899399L;

    /** Error message */
    private static final String UNKNOWN_BETWEENNESS_ERROR = "An unknown "
            + "internal error occured during the run of the betweenness"
            + " algorithm. The execution was terminated.\n";

    /**
     * The severity of the <code> BetweennessException </code>. This is
     * important to the logging mechanism.
     */
    Level severity = Level.SEVERE;

    /**
     * Returns a <code> BetweennessException </code> with standard values for
     * error message and severity for a given <code> Throwable </code>
     * 
     * @param t
     *            the source for the exception
     * @return the standard <code> BetweennessException </code>
     */
    public static final BetweennessException getStandardException(Throwable t) {
        return new BetweennessException(UNKNOWN_BETWEENNESS_ERROR + "\n"
                + t.getMessage(), t, Level.SEVERE);
    }

    /**
     * Returns a <code> BetweennessException </code> with standard values for
     * error message and severity for a given <code> Throwable </code>
     * 
     * @return the standard <code> BetweennessException </code>
     */
    public static final BetweennessException getStandardException() {
        return new BetweennessException(UNKNOWN_BETWEENNESS_ERROR, Level.SEVERE);
    }

    /**
     * Constructs a new BetweennessException with null as its detail message.
     */
    public BetweennessException() {
        super();
    }

    /**
     * Constructs a new BetweennessException with the specified detail message.
     * 
     * @param message
     *            the detail message
     */
    public BetweennessException(String message) {
        super(message);
    }

    /**
     * Constructs a new BetweennessException exception with the specified detail
     * message and cause.
     * 
     * @param message
     *            the detail message
     * @param cause
     *            the cause of the <code> BetweennessException </code>
     */
    public BetweennessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new BetweennessException exception with the specified detail
     * message and severity.
     * 
     * @param message
     *            the detail message
     * @param severity
     *            the severity of the <code> BetweennessException </code>
     */
    public BetweennessException(String message, Level severity) {
        super(message);
        this.severity = severity;
    }

    /**
     * Constructs a new BetweennessException exception with the specified detail
     * message, cause and severity.
     * 
     * @param message
     *            the detail message
     * @param cause
     *            the cause of the <code> BetweennessException </code>
     * @param severity
     *            the severity of the <code> BetweennessException </code>
     */
    public BetweennessException(String message, Throwable cause, Level severity) {
        super(message, cause);
        this.severity = severity;
    }

    /**
     * Returns the severity of the <code> BetweennessException </code>
     * 
     * @return the severity
     */
    public Level getSeverity() {
        return severity;
    }

    /**
     * Assigns the content of this exception to the given logger.
     * 
     * @param logger
     *            the logger
     */
    public void log(Logger logger) {
        logger.log(getSeverity(), getMessage(), this);
    }
}
