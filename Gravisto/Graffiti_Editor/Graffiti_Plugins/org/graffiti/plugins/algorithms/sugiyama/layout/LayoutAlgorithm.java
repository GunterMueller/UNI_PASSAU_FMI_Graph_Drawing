// =============================================================================
//
//   LayoutAlgorithm.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LayoutAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.layout;

import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;

/**
 * This interface has to be implemented by all algorithms in the fourth phase of
 * the sugiyama-algorithm.<br>
 * All information about the layout of the nodes can be found in the
 * <i>NodeLayers</i>-object stored in <i>SugiyamaData</i>.
 * 
 * @author Ferdinand H&uuml;bner
 */
public interface LayoutAlgorithm extends SugiyamaAlgorithm {

    /**
     * <p>
     * This method is an indicator for the framework to declare support for
     * "arbitrary" <i>xpos</i> attributes on the nodes of the graph.
     * </p>
     * <p>
     * In this case, support for arbitrary xpos attributes has the following
     * meaning:
     * </p>
     * <p>
     * If an algorithm does not implement support for arbitrary xpos attributes,
     * it expects that the index of the node in its respective layer is equal to
     * its xpos attribute.<br />
     * That implies that there are no "gaps" between the nodes on a given level
     * and that the attribute xpos is a positive integer.
     * </p>
     * <p>
     * If an algorithm does implement support for arbitrary xpos attributes, it
     * is able to understand hints for x coordinate assignment from the previous
     * phase and does provide support for both gaps on a level and the
     * possibility that the attribute xpos might be a positive double value (in
     * contrast to an integer value if it does not support arbitrary xpos
     * attributes).
     * </p>
     */
    public boolean supportsArbitraryXPos();

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
