// =============================================================================
//
//   GraphElementShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphElementShape.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

import java.awt.geom.Rectangle2D;

/**
 * Interface combining <code>NodeShape</code> and <code>EdgeShape</code>.
 */
public interface GraphElementShape extends GraffitiShape {

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public Rectangle2D getRealBounds2D();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
