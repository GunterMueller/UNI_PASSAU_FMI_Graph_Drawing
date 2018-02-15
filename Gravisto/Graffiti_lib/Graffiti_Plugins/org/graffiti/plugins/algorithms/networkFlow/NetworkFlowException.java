// ==============================================================================
//
//   NetworkFlowException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NetworkFlowException.java 5766 2010-05-07 18:39:06Z gleissner $

/*
 * Created on 23.06.2004
 */

package org.graffiti.plugins.algorithms.networkFlow;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An Exception to be thrown by the network-flow algorithms.
 * 
 * @author Markus K�ser
 * @version $Revision 1.1 $
 */
public class NetworkFlowException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -7017023945813987807L;

    /** Error message */
    private static final String UNKNOWN_NETWORK_FLOW_ERROR = "An unknown "
            + "internal error occured during the run of the network-flow"
            + " algorithm. The execution was terminated.\n";

    /**
     * The severity of the <code> NetworkFlowException </code>. This is
     * important to the logging mechanism.
     */
    Level severity = Level.SEVERE;

    /**
     * Returns a <code> NetworkFlowException </code> with standard values for
     * error message and severity for a given <code> Throwable </code>
     * 
     * @param t
     *            the source for the exception
     * @return the standard <code> NetworkFlowException </code>
     */
    public static final NetworkFlowException getStandardException(Throwable t) {
        return new NetworkFlowException(UNKNOWN_NETWORK_FLOW_ERROR + "\n"
                + t.getMessage(), t, Level.SEVERE);
    }

    /**
     * Returns a <code> NetworkFlowException </code> with standard values for
     * error message and severity for a given <code> Throwable </code>
     * 
     * @return the standard <code> NetworkFlowException </code>
     */
    public static final NetworkFlowException getStandardException() {
        return new NetworkFlowException(UNKNOWN_NETWORK_FLOW_ERROR,
                Level.SEVERE);
    }

    /**
     * Constructs a new NetworkFlowException with null as its detail message.
     */
    public NetworkFlowException() {
        super();
    }

    /**
     * Constructs a new NetworkFlowException with the specified detail message.
     * 
     * @param message
     *            the detail message
     */
    public NetworkFlowException(String message) {
        super(message);
    }

    /**
     * Constructs a new NetworkFlowException exception with the specified detail
     * message and cause.
     * 
     * @param message
     *            the detail message
     * @param cause
     *            the cause of the <code> NetworkFlowException </code>
     */
    public NetworkFlowException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new NetworkFlowException exception with the specified detail
     * message and severity.
     * 
     * @param message
     *            the detail message
     * @param severity
     *            the severity of the <code> NetworkFlowException </code>
     */
    public NetworkFlowException(String message, Level severity) {
        super(message);
        this.severity = severity;
    }

    /**
     * Constructs a new NetworkFlowException exception with the specified detail
     * message, cause and severity.
     * 
     * @param message
     *            the detail message
     * @param cause
     *            the cause of the <code> NetworkFlowException </code>
     * @param severity
     *            the severity of the <code> NetworkFlowException </code>
     */
    public NetworkFlowException(String message, Throwable cause, Level severity) {
        super(message, cause);
        this.severity = severity;
    }

    /**
     * Returns the severity of the <code> NetworkFlowException </code>
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

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
