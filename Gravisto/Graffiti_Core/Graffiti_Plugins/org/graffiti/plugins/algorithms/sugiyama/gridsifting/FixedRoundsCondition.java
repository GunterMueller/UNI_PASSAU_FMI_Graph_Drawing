// =============================================================================
//
//   FixedRoundsCondition.java
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
public class FixedRoundsCondition extends TerminalCondition {

    public static boolean DO_IT = false;// TODO erase that!
    
    private int round;

    private int roundCount;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void init() {
        round = -1;
        roundCount = parameters.getRoundCount();
        DO_IT = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean update(int delta, SugiyamaData data) {
        round++;

        if (round == roundCount) {
            data
                    .putObject(SugiyamaBenchmarkAdapter.EFFECTIVE_ROUNDS_KEY,
                            round);
        }
        
        DO_IT = roundCount - round <= 3;

        return round >= roundCount;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
