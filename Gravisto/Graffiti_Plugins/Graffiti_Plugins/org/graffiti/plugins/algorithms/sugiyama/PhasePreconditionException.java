// =============================================================================
//
//   PhasePreconditionException.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PhasePreconditionException.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama;

import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * This class represents an exeption that is thrown instead of a
 * <code>PreconditionException</code>.
 * 
 * Each phase in the sugiyama-framework may have its own preconditions that need
 * to be checked before executing the individual phase.
 * 
 * @author Ferdinand H�bner
 */
public class PhasePreconditionException extends PreconditionException {

    /**
     * 
     */
    private static final long serialVersionUID = 2086532707428519422L;

    /**
     * Default constructor for a <code>PhasePreconditionException</code>.
     * Creates a new Exception with the error-message represented by the
     * <code>String</code> s
     * 
     * @param s
     *            The <code>String</code> that represents this exception's
     *            error-message.
     */
    public PhasePreconditionException(String s) {
        super(s);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
