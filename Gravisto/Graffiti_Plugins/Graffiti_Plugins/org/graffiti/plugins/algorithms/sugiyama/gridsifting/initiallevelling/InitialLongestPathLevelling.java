// =============================================================================
//
//   InitialLongestPathLevelling.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting.initiallevelling;

import org.graffiti.plugins.algorithms.sugiyama.levelling.LongestPath;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class InitialLongestPathLevelling extends
        InitialProxyLevelling<LongestPath> {
    public InitialLongestPathLevelling() {
        super(new LongestPath());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
