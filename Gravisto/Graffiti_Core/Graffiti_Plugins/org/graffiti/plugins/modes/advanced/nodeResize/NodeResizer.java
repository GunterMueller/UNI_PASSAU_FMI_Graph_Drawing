// =============================================================================
//
//   NodeResizer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeResizer.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.nodeResize;

import java.awt.Point;

/**
 * Each implementor can resize nodes of some special type (for example rect
 * nodes). When starting resizing, the NodeResizer is constructed. It then keeps
 * track of the size of the node until resizing is finished.
 */
public interface NodeResizer {

    /**
     * Updates the size of the node associated to this NodeResizer.
     * 
     * @param minNodeSize
     *            the node may not get smaller than this value;
     * @param position
     *            position given by input-event
     */
    public void updateNode(double minNodeSize, Point position);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
