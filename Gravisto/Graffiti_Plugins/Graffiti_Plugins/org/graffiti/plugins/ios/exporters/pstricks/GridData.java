// =============================================================================
//
//   GridData.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.exporters.pstricks;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.plugin.view.Grid;

/**
 * @author lessenic
 * @version $Revision$ $Date$
 */
public class GridData implements GraphicElement {

    private List<Shape> shapes = new LinkedList<Shape>();

    /**
     * Creates a new instance of GridData.
     * 
     * @param grid
     */
    public GridData(Grid grid, Rectangle2D area) {
        shapes.addAll(grid.getShapes(area));
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
     * Returns true if a grid is set for the current graph.
     * 
     * @return true if grid is enabled
     */
    public boolean isEnabled() {
        return !shapes.isEmpty();
    }

    /**
     * Returns the shapes.
     * 
     * @return the shapes.
     */
    public List<Shape> getShapes() {
        return shapes;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
