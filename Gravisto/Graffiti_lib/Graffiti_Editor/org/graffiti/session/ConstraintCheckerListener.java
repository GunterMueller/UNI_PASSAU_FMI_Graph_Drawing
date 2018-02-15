// =============================================================================
//
//   ConstraintCheckerListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ConstraintCheckerListener.java 5768 2010-05-07 18:42:39Z gleissner $

/*
 * $Id: ConstraintCheckerListener.java 5768 2010-05-07 18:42:39Z gleissner $
 */

package org.graffiti.session;

/**
 * Defines a listener to the <code>GraphConstraintChecker</code>. The method
 * <code>checkFailed</code> is called every time the
 * <code>GraphConstraintChecker</code> finds an unsatisfied constraint.
 * 
 * @see GraphConstraintChecker
 */
public interface ConstraintCheckerListener {

    /**
     * Handles the message received by the constraint checker indicating an
     * unsatisfied constraint.
     * 
     * @param msg
     *            the message telling about the unsatisfied constraint.
     */
    public void checkFailed(String msg);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
