// =============================================================================
//
//   LabelData.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.exporters.pstricks;

import org.graffiti.graphics.LabelAttribute;

/**
 * @author lessenic
 * @version $Revision$ $Date$
 */
public class LabelData implements GraphicElement {

    private String label;

    private double x;

    private double y;

    /**
     * Creates a new instance of LabelData.
     * 
     * @param label
     * @param x
     * @param y
     */
    public LabelData(LabelAttribute label, double x, double y) {
        this.label = label.getLabel();
        this.x = x;
        this.y = y;
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
     * Returns the label.
     * 
     * @return the label.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns the x coordinate.
     * 
     * @return the x.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the y coordinate.
     * 
     * @return the y.
     */
    public double getY() {
        return y;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
