// =============================================================================
//
//   OrientationStrategy.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.reingoldtilford;

import java.awt.geom.Point2D;

import org.graffiti.graph.Node;
import org.graffiti.graphics.Port;

/**
 * Subclasses of <code>OrientationStrategy</code> determine the orientation of a
 * tree. See {@link Orientation} for details.
 * <p>
 * To provide a new orientation, create a class extending
 * <code>OrientationStrategy</code> and add a new member to the
 * <code>Orientation</code> enumeration.
 * <p>
 * In order to align the left of the tree layout to the absolute x-coordinate 0
 * and the top of the tree layout to the absolute y-coordinate 0, the width and
 * height of the whole tree is passed to an <code>OrientationStrategy</code> by
 * {@link #setHeight(double)} and {@link #setWidth(double)} before any calls to
 * {@link #transformWritingNodePosition(java.awt.geom.Point2D.Double)}.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class OrientationStrategy implements Cloneable {
    /**
     * The width of the tree layout. This field is set before any call to
     * {@link #transformWritingNodePosition(java.awt.geom.Point2D.Double)}.
     * 
     * @see #setWidth(double)
     */
    protected double width;

    /**
     * The height of the tree layout. This field is set before any call to
     * {@link #transformWritingNodePosition(java.awt.geom.Point2D.Double)}.
     * 
     * @see #setWidth(double)
     */
    protected double height;

    /**
     * Sets the width of the tree layout. Must only be called by
     * {@link ReingoldTilfordAlgorithm}.
     * 
     * @param width
     *            the width of the tree layout.
     * @see #transformWritingNodePosition(java.awt.geom.Point2D.Double)
     */
    void setWidth(double width) {
        this.width = width;
    }

    /**
     * Sets the height of the tree layout. Must only be called by
     * {@link ReingoldTilfordAlgorithm}.
     * 
     * @param height
     *            the height of the tree layout.
     * @see #transformWritingNodePosition(java.awt.geom.Point2D.Double)
     */
    void setHeight(double height) {
        this.height = height;
    }

    /**
     * Transforms the dimensions of {@link Node}s. Is implemented by subclasses
     * of <code>OrientationStrategy</code> to yield the specific orientation.
     * Must only be called by {@link ReingoldTilfordAlgorithm}.
     * 
     * @param dimension
     *            the real dimension of a node.
     * @return the pretended dimension of the node.
     */
    abstract Point2D.Double transformDimension(Point2D.Double dimension);

    /**
     * Transforms the positions of {@link Node}s and bends when they are set. Is
     * implemented by subclasses of <code>OrientationStrategy</code> to yield
     * the specific orientation. Must only be called by
     * {@link ReingoldTilfordAlgorithm}.
     * <p>
     * <b>Preconditions:</b><br>
     * The <code>width</code> and <code>height</code> must have been set by
     * {@link #setWidth(double)} and {@link #setHeight(double)}.
     * 
     * @param position
     *            the position where a node or bend should be placed at if the
     *            tree grew from top to bottom.
     * @return the position where the node or bend will be placed at,
     *         considering the real orientation of the tree.
     */
    abstract Point2D.Double transformWritingNodePosition(Point2D.Double position);

    /**
     * Transforms the position of {@link Port}s. Is implemented by subclasses of
     * <code>OrientationStrategy</code> to yield the specific orientation. Must
     * only be called by {@link ReingoldTilfordAlgorithm}.
     * 
     * @param position
     *            the position where a port should be placed if the tree grew
     *            from top to bottom.
     * @return the position where the port will be placed at, considering the
     *         real orientation of the tree.
     */
    abstract Point2D.Double transformPortPosition(Point2D.Double position);

    /**
     * Transforms the positions of {@link Node}s and bends when they are
     * queried. Is implemented by subclasses of <code>OrientationStrategy</code>
     * to yield the specific orientation. Must only be called by
     * {@link ReingoldTilfordAlgorithm}.
     * 
     * @param position
     *            the real position of a node or bend.
     * @return the pretended position of the node or bend where it would be if
     *         the tree grew from top to bottom.
     */
    Point2D.Double transformReadingNodePosition(Point2D.Double position) {
        return transformWritingNodePosition(position);
    }
}

/**
 * An <code>OrientationStrategy</code>, which makes the tree grow from top to
 * bottom.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Orientation#TOP_TO_BOTTOM
 */
class TopToBottomOrientation extends OrientationStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformDimension(Point2D.Double dimension) {
        return dimension;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformWritingNodePosition(Point2D.Double position) {
        return position;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformPortPosition(Point2D.Double position) {
        return position;
    }
}

/**
 * An <code>OrientationStrategy</code>, which makes the tree grow from bottom to
 * top.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Orientation#BOTTOM_TO_TOP
 */
class BottomToTopOrientation extends OrientationStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformDimension(Point2D.Double dimension) {
        return dimension;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformWritingNodePosition(Point2D.Double position) {
        return new Point2D.Double(position.getX(), height - position.getY());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformPortPosition(Point2D.Double position) {
        return new Point2D.Double(position.getX(), -position.getY());
    }
}

/**
 * An <code>OrientationStrategy</code>, which makes the tree grow from left to
 * right.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Orientation#LEFT_TO_RIGHT
 */
class LeftToRightOrientation extends OrientationStrategy {
    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformDimension(Point2D.Double dimension) {
        return new Point2D.Double(dimension.getY(), dimension.getX());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformWritingNodePosition(Point2D.Double position) {
        return new Point2D.Double(position.getY(), position.getX());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformPortPosition(Point2D.Double position) {
        return new Point2D.Double(position.getY(), position.getX());
    }

}

/**
 * An <code>OrientationStrategy</code>, which makes the tree grow from right to
 * left.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Orientation#RIGHT_TO_LEFT
 */
class RightToLeftOrientation extends OrientationStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformDimension(Point2D.Double dimension) {
        return new Point2D.Double(dimension.getY(), dimension.getX());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformWritingNodePosition(Point2D.Double position) {
        return new Point2D.Double(height - position.getY(), position.getX());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformReadingNodePosition(Point2D.Double position) {
        return new Point2D.Double(position.getY(), -position.getX());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    Point2D.Double transformPortPosition(Point2D.Double position) {
        return new Point2D.Double(-position.getY(), position.getX());
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
