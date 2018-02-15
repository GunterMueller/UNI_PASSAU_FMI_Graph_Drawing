// =============================================================================
//
//   NodeShape.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NodeShape.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

import org.graffiti.graphics.NodeGraphicAttribute;

/**
 * Interface extending <code>GraphElementShape</code>. Classes implementing this
 * interface are shapes that are displayed for nodes. They must implement a
 * method returning the intersection between themselves and a
 * <code>Line2D</code> so that the clipping between the node and incoming /
 * outgoing edges can be calculated.
 */
public interface NodeShape extends GraphElementShape {

    /**
     * Calculates the intersection point between this node shape and a line. For
     * irregularly shaped objects, the intersection that is nearest to the
     * <b>end point</b> of the line is returned.
     * 
     * @param line
     *            the line with which the intersection should be calculated.
     * 
     * @return the intersection point between this node shape and the line.
     */
    public Point2D getIntersection(Line2D line);

    /**
     * Shapes itself according to the graphics attribute found in the
     * CollectionAttribute <code>graphics</code>
     * 
     * @param graphics
     *            the <code>CollectionAttribute</code> accoring to which this
     *            shape is constructed.
     */
    public void buildShape(NodeGraphicAttribute graphics)
            throws ShapeNotFoundException;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
