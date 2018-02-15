// =============================================================================
//
//   NoImprovementCondition.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import org.graffiti.plugins.algorithms.sugiyama.gridsifting.benchmark.SugiyamaBenchmarkAdapter;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class NoImprovementCondition extends TerminalCondition {
    private int round;

    private int roundCount;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        round = -1;
        roundCount = parameters.getRoundCount();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean update(int delta, SugiyamaData data) {
        round++;

        boolean isStopping = delta == 0 || round >= roundCount;

        data.putObject(SugiyamaBenchmarkAdapter.EFFECTIVE_ROUNDS_KEY, round);

        return isStopping;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
