// =============================================================================
//
//   MCMAbstractAlgorithm.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev;

import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class MCMAbstractAlgorithm extends AbstractAlgorithm implements
        SugiyamaAlgorithm {
    /**
     * The data of the sugiyama framework this algorithm operates on.
     */
    protected SugiyamaData data;

    /**
     * {@inheritDoc}
     */
    public SugiyamaData getData() {
        return data;
    }

    /**
     * {@inheritDoc}
     */
    public void setData(SugiyamaData data) {
        this.data = data;
    }

    /**
     * {@inheritDoc} This implementation only supports the horizontal sugiyama.
     */
    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

    /**
     * {@inheritDoc} This implementation returns {@code false}.
     */
    public boolean supportsBigNodes() {
        return false;
    }

    /**
     * {@inheritDoc} This implementation returns {@code false}.
     */
    public boolean supportsConstraints() {
        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
