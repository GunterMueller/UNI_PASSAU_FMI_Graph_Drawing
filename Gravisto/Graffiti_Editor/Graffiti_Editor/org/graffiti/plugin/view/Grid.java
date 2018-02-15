// =============================================================================
//
//   Grid.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.graffiti.graphics.grid.GridAttribute;

/**
 * Classes implementing {@code Grid} represent grids, which can be displayed by
 * some {@code View}s. Supporting views return {@code true} on
 * {@link View#supportsGrid()}. All parameters are given in logical (i.e.
 * attribute system) coordinates. To define a parameter for a concrete grid,
 * declare a {@code public} field in the implementing class and annotate it with
 * {@link GridParameter}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see View
 * @see AbstractGrid
 * @see GridAttribute
 */
public interface Grid {
    public static final String NAME_PATTERN = "parameter.%s.name";

    public static final String DESCRIPTION_PATTERN = "parameter.%s.description";

    /**
     * Sets the origin of the grid.
     * 
     * @param origin
     *            the origin of the grid.
     */
    public void setOrigin(Point2D origin);

    /**
     * Returns the origin of the grid.
     * 
     * @return the origin of the grid.
     */
    public Point2D getOrigin();

    /**
     * Sets the snap tolerance of the grid.
     * 
     * @param snapTolerance
     *            the snap tolerance of the grid. Negative values denote a
     *            disabled snap behavior.
     */
    public void setSnapTolerance(double snapTolerance);

    /**
     * Returns the snap tolerance of the grid.
     * 
     * @return the snap tolerance of the grid. Negative values denote a disabled
     *         snap behavior.
     */
    public double getSnapTolerance();

    /**
     * Sets if the snap behavior of the grid is enabled for bends.
     * 
     * @param isBendSnap
     *            denotes if the snap behavior of the grid is enabled for bends.
     */
    public void setBendSnap(boolean isBendSnap);

    /**
     * Returns if the snap behavior of the grid is enabled for bends.
     * 
     * @return if the snap behavior of the grid is enabled for bends.
     */
    public boolean isBendSnap();

    /**
     * Returns the new position of a node that was previously located at the
     * specified point and snaps in the grid.
     * 
     * @param point
     *            the previous position of the node.
     * @return the new position of the node.
     */
    public Point2D snapNode(Point2D point);

    /**
     * Returns the new position of a bend that was previously located at the
     * specified point and snap in the grid.
     * 
     * @param point
     *            the previous position of the bend.
     * @return the new position of the bend.
     */
    public Point2D snapBend(Point2D point);

    /**
     * Returns a list of shapes to present the grid in the specified area.
     * 
     * @param area
     *            the area.
     * @return a list of shapes to present the grid in the specified area.
     */
    public List<Shape> getShapes(Rectangle2D area);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
