// =============================================================================
//
//   BranchSelector.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev.selectors;

import org.graffiti.plugins.algorithms.chebyshev.AbstractSubAlgorithm;
import org.graffiti.plugins.algorithms.chebyshev.cores.MCMSearch;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class BranchSelector extends AbstractSubAlgorithm {
    public abstract void select(int value, MCMSearch search);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
