// =============================================================================
//
//   GraphConstraint.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphConstraint.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.mode;

import org.graffiti.graph.Graph;
import org.graffiti.session.UnsatisfiedConstraintException;

/**
 * A <code>GraphConstraint</code> is a constraint to the graph which can be
 * validated and which is supposed to be satisfied all the time.
 * <code>GraphConstraints</code> can be combined in an arbitrary way.
 * 
 * @see org.graffiti.session.GraphConstraintChecker
 */
public interface GraphConstraint {

    /**
     * Checks whether the specified graph satisfies the defined constraint.
     * 
     * @throws UnsatisfiedConstraintException
     *             if the graph does not satisfy the defined constraint.
     */
    public void validate(Graph g) throws UnsatisfiedConstraintException;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
