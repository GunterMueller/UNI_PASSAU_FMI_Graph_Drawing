// =============================================================================
//
//   PreconditionException.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PreconditionException.java 5767 2010-05-07 18:42:02Z gleissner $

/*
 * $Id: PreconditionException.java 5767 2010-05-07 18:42:02Z gleissner $
 */

package org.graffiti.plugin.algorithm;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Throws in the context of precondition failures.
 */
public class PreconditionException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 4160776037182505133L;
    /** DOCUMENT ME! */
    private List<Entry> errors;

    /**
     * Creates a new PreconditionException object.
     * 
     * @param msg
     *            DOCUMENT ME!
     */
    public PreconditionException(String msg) {
        this();
        add(msg);
    }

    /**
     * Creates a new PreconditionException object.
     */
    public PreconditionException() {
        this.errors = new LinkedList<Entry>();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public boolean isEmpty() {
        return errors.isEmpty();
    }

    /**
     * @see java.lang.Throwable#getMessage()
     */
    @Override
    public String getMessage() {
        StringBuffer sb = new StringBuffer();
        sb.append("The following preconditions are not satisfied:\n");

        for (Entry error : errors) {
            sb.append("- ");
            sb.append(error.cause);
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param cause
     *            DOCUMENT ME!
     * @param source
     *            DOCUMENT ME!
     */
    public void add(String cause, Object source) {
        errors.add(new Entry(cause, source));
    }

    /**
     * DOCUMENT ME!
     * 
     * @param cause
     *            DOCUMENT ME!
     */
    public void add(String cause) {
        errors.add(new Entry(cause, null));
    }

    /**
     * Returns an iterator over all <code>Error</code>s.
     * 
     * @return an iterator.
     */
    public Iterator<Entry> iterator() {
        return errors.iterator();
    }

    /**
     * Contains a cause and the source object (ie.: a Graph, Node or Edge).
     * 
     * @version $Revision: 5767 $
     */
    public class Entry {
        /** DOCUMENT ME! */
        public Object source;

        /** DOCUMENT ME! */
        public String cause;

        /**
         * Creates a new Entry object.
         * 
         * @param cause
         *            DOCUMENT ME!
         * @param source
         *            DOCUMENT ME!
         */
        public Entry(String cause, Object source) {
            this.cause = cause;
            this.source = source;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
