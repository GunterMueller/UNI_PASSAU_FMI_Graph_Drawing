// =============================================================================
//
//   GraphElementData.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.exporters.pstricks;

import java.awt.Color;

import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.GraphElementGraphicAttribute;
import org.graffiti.plugin.view.GraphElementShape;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;

/**
 * @author lessenic
 * @version $Revision$ $Date$
 */
public abstract class GraphElementData implements GraphicElement,
        Comparable<GraphElementData> {

    protected GraphElementGraphicAttribute graphElementGraphicAttribute;

    protected GraphElementShape shape;

    /**
     * Creates a new instance of GraphElementData.
     * 
     * @param graphElement
     */
    public GraphElementData(GraphElement graphElement) {
        graphElementGraphicAttribute = (GraphElementGraphicAttribute) graphElement
                .getAttribute("graphics");

        try {
            shape = (GraphElementShape) InstanceLoader
                    .createInstance(graphElementGraphicAttribute.getShape());

        } catch (InstanceCreationException e) {
            e.printStackTrace();
        }

    }

    /*
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(GraphElementData other) {
        return (int) Math.signum(other.getZ() - getZ());
    }

    /**
     * Returns the fill color
     * 
     * @return fill color
     */
    public Color getFillColor() {
        return graphElementGraphicAttribute.getFillcolor().getColor();
    }

    /**
     * Returns the frame color
     * 
     * @return frame color
     */
    public Color getFrameColor() {
        return graphElementGraphicAttribute.getFramecolor().getColor();
    }

    /**
     * Returns the frame thickness
     * 
     * @return frame thickness
     */
    public double getFrameThickness() {
        return graphElementGraphicAttribute.getFrameThickness();
    }

    /**
     * Returns the frame line mode
     * 
     * @return line mode
     */
    public float[] getLineMode() {
        return graphElementGraphicAttribute.getLineMode().getDashArray();
    }

    /**
     * Returns the shape.
     * 
     * @return the shape.
     */
    public GraphElementShape getShape() {
        return shape;
    }

    public abstract double getZ();

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
