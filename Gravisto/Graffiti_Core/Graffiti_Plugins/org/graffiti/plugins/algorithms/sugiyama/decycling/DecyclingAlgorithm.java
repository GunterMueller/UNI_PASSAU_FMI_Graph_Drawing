// =============================================================================
//
//   DecyclingAlgorithm.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DecyclingAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.decycling;

import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;

/**
 * This interface extends the SugiyamaAlgorithm-Interface. Each algorithm that
 * processes the decycling-phase has to implement this interface and provide a
 * method - undo - to restore the edges to their original direction.
 * <p>
 * Your implementation has to <b>set the attribute hasBeenDecycled in the
 * sugiyama attribute-tree</b> after decycling the graph!
 * <p>
 * It is recommended that you put the reversed <tt>Edge</tt>s into SugiyamaData
 * (getReversedEdges). If you put them there, you can use
 * <i>reverseBendedEdge(SugiyamaData)</i> from <b>EdgeUtil</b> to reverse your
 * edges.
 */
public interface DecyclingAlgorithm extends SugiyamaAlgorithm {

    /**
     * Re-reverse all edges that had been reversed!
     */
    public void undo();

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
