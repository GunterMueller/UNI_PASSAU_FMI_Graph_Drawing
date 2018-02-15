// =============================================================================
//
//   RandomSelector.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev.selectors;

import java.util.ArrayList;

import org.graffiti.plugins.algorithms.chebyshev.AuxNode;
import org.graffiti.plugins.algorithms.chebyshev.cores.MCMSearch;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class RandomSelector extends BranchSelector {
    /**
     * {@inheritDoc}
     */
    @Override
    public void select(int value, MCMSearch search) {
        ArrayList<AuxNode> list = search.getWorstNodes();
        int index = parameters.getRandom().nextInt(list.size());
        search.lock(list.get(index).getLocalId());
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
