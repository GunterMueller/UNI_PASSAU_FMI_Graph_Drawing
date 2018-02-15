// =============================================================================
//
//   GridOriginAttribute.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.graphics.grid;

import static org.graffiti.graphics.GraphicAttributeConstants.X;
import static org.graffiti.graphics.GraphicAttributeConstants.Y;

import java.awt.geom.Point2D;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.FieldAlreadySetException;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.event.AttributeEvent;
import org.graffiti.plugin.view.Grid;

/**
 * Attribute representing the origin of a grid.
 * 
 * @author Kathrin Hanauer
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class GridOriginAttribute extends LinkedHashMapAttribute {
    /**
     * Attribute representing one coordinate of the origin of a grid.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    private abstract class GridOriginValueAttribute extends DoubleAttribute {
        /**
         * Constructs a {@code GridOriginValueAttribute}.
         * 
         * @param id
         *            the id of the {@code GridOriginValueAttribute}.
         */
        public GridOriginValueAttribute(String id) {
            super(id, 0.0);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setDouble(double value) {
            if (gridAttribute != null) {
                gridAttribute.preChange();
            }
            super.setDouble(value);
            if (gridAttribute != null) {
                applyOnGrid();
                gridAttribute.postChange();
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setValue(Object v) throws IllegalArgumentException {
            if (!(v instanceof Double))
                throw new IllegalArgumentException();
            setDouble((Double) v);
        }

        /**
         * Returns a new origin, whose coordinate represented by this attribute
         * is set to the specified value and whose other coordinate is copied
         * from the specified origin.
         * 
         * @param origin
         *            the origin the coordinate represented by this attribute is
         *            copied from.
         * @param value
         *            the value of the coordinate of the origin represented by
         *            this attribute.
         * @return a new origin, whose coordinate represented by this attribute
         *         is set to the specified value and whose other coordinate is
         *         copied from the specified origin.
         */
        protected abstract Point2D change(Point2D origin, double value);

        /**
         * Sets the value of this attribute to the respective coordinate of the
         * specified origin.
         * 
         * @param origin
         *            the origin to copy from.
         */
        protected abstract void setFromGrid(Point2D origin);

        /**
         * Sets the origin of the grid represented by the parent attribute to
         * the value of this attribute.
         */
        protected void applyOnGrid() {
            Grid grid = gridAttribute.getGrid();
            grid.setOrigin(change(grid.getOrigin(), getDouble()));
        }

        /**
         * Sets the value of this attribute without raising an
         * {@link AttributeEvent}.
         * 
         * @param value
         *            the value to be set.
         */
        protected void setRaw(double value) {
            super.setDouble(value);
        }
    }

    /**
     * The parent attribute.
     */
    private GridAttribute gridAttribute;

    /**
     * The attribute representing the x coordinate.
     */
    private GridOriginValueAttribute originX;

    /**
     * The attribute representing the y coordinate.
     */
    private GridOriginValueAttribute originY;

    /**
     * Constructs a {@code GridOriginAttribute} with the specified parent.
     * 
     * @param gridAttribute
     *            the parent of the constructed attribute.
     */
    protected GridOriginAttribute(GridAttribute gridAttribute) {
        super(GridAttribute.GRID_ORIGIN);
        this.gridAttribute = gridAttribute;
        add(originX = new GridOriginValueAttribute(X) {
            /**
             * {@inheritDoc}
             */
            @Override
            protected Point2D change(Point2D origin, double value) {
                return new Point2D.Double(value, origin.getY());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void setFromGrid(Point2D origin) {
                setRaw(origin.getX());
            }
        });
        add(originY = new GridOriginValueAttribute(Y) {
            /**
             * {@inheritDoc}
             */
            @Override
            protected Point2D change(Point2D origin, double value) {
                return new Point2D.Double(origin.getX(), value);
            }

            /**
             * {@inheritDoc}
             */
            @Override
            protected void setFromGrid(Point2D origin) {
                setRaw(origin.getY());
            }
        });
    }

    /**
     * Returns the x coordinate.
     * 
     * @return the x coordinate.
     */
    public double getX() {
        return originX.getDouble();
    }

    /**
     * Sets the x coordinate.
     * 
     * @param value
     *            the x coordinate.
     */
    public void setX(double value) {
        originX.setDouble(value);
    }

    /**
     * Returns the y coordinate.
     * 
     * @return the y coordinate.
     */
    public double getY() {
        return originY.getDouble();
    }

    /**
     * Sets the y coordinate.
     * 
     * @param value
     *            the y coordinate.
     */
    public void setY(double value) {
        originY.setDouble(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GridOriginAttribute copy() {
        GridOriginAttribute copiedAttributes = new GridOriginAttribute(null);
        copiedAttributes.setX(getX());
        copiedAttributes.setY(getY());
        return copiedAttributes;
    }

    /**
     * Sets the parent of this attribute. It must be a {@link GridAttribute}.
     * 
     * @param parent
     *            the parent of this attribute, which must be a {@code
     *            GridAttribute}.
     */
    @Override
    public void setParent(CollectionAttribute parent)
            throws FieldAlreadySetException {
        gridAttribute = (GridAttribute) parent;
        super.setParent(parent);
    }

    /**
     * Sets the origin of the grid represented by the parent attribute to the
     * value of this attribute.
     */
    protected void applyOnGrid() {
        originX.applyOnGrid();
        originY.applyOnGrid();
    }

    /**
     * Sets the value of this attribute to the origin of the specified grid.
     * 
     * @param grid
     *            the grid whose origin is to be copied.
     */
    protected void setFromGrid(Grid grid) {
        Point2D origin = grid.getOrigin();
        originX.setFromGrid(origin);
        originY.setFromGrid(origin);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
