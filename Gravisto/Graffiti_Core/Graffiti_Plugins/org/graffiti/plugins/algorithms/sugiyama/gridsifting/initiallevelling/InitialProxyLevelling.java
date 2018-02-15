// =============================================================================
//
//   InitialProxyLevelling.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting.initiallevelling;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.sugiyama.levelling.LevellingAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class InitialProxyLevelling<A extends LevellingAlgorithm> extends
        InitialLevelling {
    protected final A algorithm;

    protected InitialProxyLevelling(A algorithm) {
        this.algorithm = algorithm;
    }

    protected void setParameters() {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(Graph graphCopy, SugiyamaData dataCopy) {
        algorithm.attach(graphCopy);
        algorithm.setData(dataCopy);

        setParameters();

        try {
            algorithm.check();
        } catch (PreconditionException e) {
            throw new RuntimeException(e);
        }

        algorithm.execute();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
