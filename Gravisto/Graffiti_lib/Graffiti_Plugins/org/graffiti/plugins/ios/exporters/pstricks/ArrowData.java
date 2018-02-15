// =============================================================================
//
//   ArrowData.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.exporters.pstricks;

import java.awt.Shape;

/**
 * @author lessenic
 * @version $Revision$ $Date$
 */
public class ArrowData implements GraphicElement {
    private Shape shape;

    private EdgeData edgeData;

    /**
     * Creates a new instance of ArrowData.
     * 
     * @param shape
     * @param edgeData
     */
    public ArrowData(Shape shape, EdgeData edgeData) {
        this.shape = shape;
        this.edgeData = edgeData;
    }

    /**
     * Returns the shape.
     * 
     * @return the shape.
     */
    public Shape getShape() {
        return shape;
    }

    /*
     * @see
     * org.graffiti.plugins.ios.exporters.pstricks.GraphicElement#accept(org
     * .graffiti.plugins.ios.exporters.pstricks.ExportVisitor)
     */
    @Override
    public void accept(ExportVisitor visitor) {
        visitor.visit(this);
    }

    /**
     * Returns the edgeData.
     * 
     * @return the edgeData.
     */
    public EdgeData getEdgeData() {
        return edgeData;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
