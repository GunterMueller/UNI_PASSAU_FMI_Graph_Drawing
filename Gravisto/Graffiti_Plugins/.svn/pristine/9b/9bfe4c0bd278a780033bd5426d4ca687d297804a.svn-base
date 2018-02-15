package org.graffiti.plugin.view;

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

import org.graffiti.graphics.grid.GridAttribute;

/**
 * This class provides default implementations for the {@code Grid} interface.
 * Standard behaviors like the get and set methods for {@code Grid} object
 * properties (origin, snap tolerance, bend snap) are defined here. The
 * developer need only subclass this abstract class and define the
 * {@link #getShapes(Rectangle2D)} and {@link #snap(Point2D, double)} methods.
 * All parameters are given in logical (i.e. attribute system) coordinates. To
 * define a parameter for a concrete grid, declare a {@code public} field in the
 * implementing class and annotate it with {@link GridParameter}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see GridAttribute
 */
public abstract class AbstractGrid implements Grid {
    /**
     * Origin of the grid.
     */
    protected Point2D origin;

    /**
     * Snap tolerance of the grid. Negative values denote a disabled snap
     * behavior.
     */
    private double snapTolerance;

    /**
     * Denotes if the snap behavior of the grid is enabled for bends.
     */
    private boolean isBendSnap;

    /**
     * Constructs an {@code AbstractGrid}.
     */
    protected AbstractGrid() {
        origin = new Point2D.Double();
        snapTolerance = 20.0;
        isBendSnap = true;
    }

    /**
     * {@inheritDoc}
     */
    public final void setOrigin(Point2D origin) {
        this.origin = origin;
    }

    /**
     * {@inheritDoc}
     */
    public final Point2D getOrigin() {
        return origin;
    }

    /**
     * {@inheritDoc}
     */
    public final void setSnapTolerance(double snapTolerance) {
        this.snapTolerance = snapTolerance;
    }

    /**
     * {@inheritDoc}
     */
    public final double getSnapTolerance() {
        return snapTolerance;
    }

    /**
     * {@inheritDoc}
     */
    public final void setBendSnap(boolean isBendSnap) {
        this.isBendSnap = isBendSnap;
    }

    /**
     * {@inheritDoc}
     */
    public final boolean isBendSnap() {
        return isBendSnap;
    }

    /**
     * {@inheritDoc}
     */
    public final Point2D snapNode(Point2D point) {
        return snap(point, snapTolerance);
    }

    /**
     * {@inheritDoc}
     */
    public final Point2D snapBend(Point2D point) {
        if (isBendSnap)
            return snap(point, snapTolerance);
        else
            return new Point2D.Double(Math.floor(point.getX()), Math
                    .floor(point.getY()));
    }

    /**
     * {@inheritDoc}
     */
    public abstract List<Shape> getShapes(Rectangle2D area);

    /**
     * Returns the new position of a node or bend that was previously located at
     * the specified point and snap in the grid.
     * 
     * @param point
     *            the previous position of the node or bend.
     * @return the new position of the node or bend.
     */
    protected abstract Point2D snap(Point2D point, double tolerance);
}
