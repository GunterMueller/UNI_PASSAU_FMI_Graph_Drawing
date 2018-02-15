// =============================================================================
//
//   IsomorphismAlgorithmResult.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.isomorphism;

import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;

/**
 * Class that represents the result of an algorithm. It provides a toString
 * method that returns the results in a human readable way.
 * 
 * @author mary-k
 * @version $Revision$ $Date$
 */
public class IsomorphismAlgorithmResult extends DefaultAlgorithmResult {

    /*
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.resultMap.values().toString();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
