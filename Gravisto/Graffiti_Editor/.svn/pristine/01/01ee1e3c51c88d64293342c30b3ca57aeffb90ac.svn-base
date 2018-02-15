/*
 * Created on 06.07.2004
 */

package org.graffiti.plugins.algorithms.clustering;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An Exception to be thrown by the clustering algorithms.
 * 
 * @author Markus K�ser
 * @version $Revision 1.1 $
 */
public class ClusteringException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 2052329681640097245L;

    /** Error message */
    private static final String UNKNOWN_CLUSTERING_ERROR = "An unknown "
            + "internal error occured during the run of the clustering "
            + "algorithm. The execution was terminated.\n";

    /**
     * The severity of the <code> ClusteringException </code>. This is important
     * to the logging mechanism.
     */
    Level severity = Level.SEVERE;

    /**
     * Returns a <code> ClusteringException </code> with standard values for
     * error message and severity for a given <code> Throwable </code>
     * 
     * @param t
     *            the source for the exception
     * @return the standard <code> ClusteringException </code>
     */
    public static final ClusteringException getStandardException(Throwable t) {
        return new ClusteringException(UNKNOWN_CLUSTERING_ERROR + "\n"
                + t.getMessage(), t, Level.SEVERE);
    }

    /**
     * Returns a <code> ClusteringException </code> with standard values for
     * error message and severity for a given <code> Throwable </code>
     * 
     * @return the standard <code> ClusteringException </code>
     */
    public static final ClusteringException getStandardException() {
        return new ClusteringException(UNKNOWN_CLUSTERING_ERROR, Level.SEVERE);
    }

    /**
     * Constructs a new ClusteringException with null as its detail message.
     */
    public ClusteringException() {
    }

    /**
     * Creates a new ClusteringException with given message
     * 
     * @param message
     *            the message
     */
    public ClusteringException(String message) {
        super(message);
    }

    /**
     * Creates a new ClusteringException with given message
     * 
     * @param message
     *            the message
     * @param throwable
     *            the cause of the ClusteringException
     */
    public ClusteringException(String message, Throwable throwable) {
        super(message, throwable);
    }

    /**
     * Constructs a new ClusteringException exception with the specified detail
     * message and severity.
     * 
     * @param message
     *            the detail message
     * @param severity
     *            the severity of the <code> ClusteringException </code>
     */
    public ClusteringException(String message, Level severity) {
        super(message);
        this.severity = severity;
    }

    /**
     * Constructs a new ClusteringException exception with the specified detail
     * message, cause and severity.
     * 
     * @param message
     *            the detail message
     * @param cause
     *            the cause of the <code> ClusteringException </code>
     * @param severity
     *            the severity of the <code> ClusteringException </code>
     */
    public ClusteringException(String message, Throwable cause, Level severity) {
        super(message, cause);
        this.severity = severity;
    }

    /**
     * Returns the severity of the <code> ClusteringException </code>
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
