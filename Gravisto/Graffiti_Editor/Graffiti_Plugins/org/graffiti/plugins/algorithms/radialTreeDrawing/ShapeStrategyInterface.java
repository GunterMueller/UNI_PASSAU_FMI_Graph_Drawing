// =============================================================================
//
//   ShapeStrategyInterface.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.radialTreeDrawing;

import org.graffiti.graph.Node;

/**
 * This interface describes a common signature for all shape handling strategies
 * 
 * @author Andreas Schindler
 * @version $Revision$ $Date$
 */
public interface ShapeStrategyInterface {

    /**
     * initialize the variables before applying a certain shape strategy
     * 
     * @param n
     *            a node
     * @param radius
     *            the radius
     * @param startAngle
     *            the wedge start angle
     */
    public void setNodeParameters(Node n, double radius, double startAngle);

    /**
     * start the handling algorithm for the current shape.
     */
    public void handleShape();

    /**
     * store the calculated values as node attributes.
     */
    public void storeCalculatedValues();
}
