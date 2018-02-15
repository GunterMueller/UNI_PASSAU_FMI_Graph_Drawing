// =============================================================================
//
//   SnapOnGridAttribute.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.graphics.grid;

import org.graffiti.attributes.BooleanAttribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.attributes.FieldAlreadySetException;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.plugin.view.Grid;

/**
 * Attribute representing the snap behavior of a grid.
 * 
 * @author Kathrin Hanauer
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class SnapOnGridAttribute extends LinkedHashMapAttribute {
    /**
     * Id of the contained {@link SnapEnabledAttribute}.
     */
    public static final String ENABLED = "enabled";

    /**
     * Id of the contained {@link ToleranceAttribute}.
     */
    public static final String TOLERANCE = "tolerance";

    /**
     * Id of the contained {@code SnapBendsAttribute}.
     */
    public static final String SNAP_BENDS = "snapBends";

    /**
     * Attribute representing if the snap behavior of a grid is enabled.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    public class SnapEnabledAttribute extends BooleanAttribute {
        /**
         * Constructs a {@code SnapEnabledAttribute}.
         */
        private SnapEnabledAttribute() {
            super(ENABLED, true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setBoolean(boolean value) {
            if (gridAttribute != null) {
                gridAttribute.preChange();
            }
            super.setBoolean(value);
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
            if (!(v instanceof Boolean))
                throw new IllegalArgumentException();
            setBoolean((Boolean) v);
        }

        /**
         * Sets if the snap behavior is enabled for the grid represented by the
         * parent attribute.
         */
        protected void applyOnGrid() {
            gridAttribute.getGrid().setSnapTolerance(
                    toleranceAttribute.getDouble() * (getBoolean() ? 1 : -1));

        }

        /**
         * Sets the value of this attribute according to the specified grid.
         * 
         * @param grid
         *            the grid.
         */
        protected void setFromGrid(Grid grid) {
            super.setBoolean(grid.getSnapTolerance() >= 0);
        }
    }

    /**
     * Attribute representing the snap tolerance of a grid.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    public class ToleranceAttribute extends DoubleAttribute {
        /**
         * Constructs a {@code ToleranceAttribute}.
         */
        private ToleranceAttribute() {
            super(TOLERANCE, 20.0);
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
         * Sets the snap tolerance of the grid represented by the parent
         * attribute.
         */
        protected void applyOnGrid() {
            gridAttribute.getGrid().setSnapTolerance(
                    getDouble() * (snapEnabledAttribute.getBoolean() ? 1 : -1));
        }

        /**
         * Sets the value of this attribute to the snap tolerance of the
         * specified grid.
         * 
         * @param grid
         *            the grid.
         */
        protected void setFromGrid(Grid grid) {
            super.setDouble(Math.abs(grid.getSnapTolerance()));
        }
    }

    /**
     * Attribute representing if the snap behavior of a grid is enabled for
     * bends.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    private class SnapBendsAttribute extends BooleanAttribute {
        /**
         * Constructs a {@code SnapBendsAttribute}.
         */
        private SnapBendsAttribute() {
            super(SNAP_BENDS, true);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void setBoolean(boolean value) {
            if (gridAttribute != null) {
                gridAttribute.preChange();
            }
            super.setBoolean(value);
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
            if (!(v instanceof Boolean))
                throw new IllegalArgumentException();
            setBoolean((Boolean) v);
        }

        /**
         * Sets if the snap behavior for bends is enabled for the grid
         * represented by the parent attribute.
         */
        protected void applyOnGrid() {
            gridAttribute.getGrid().setBendSnap(getBoolean());
        }

        /**
         * Sets the value of this attribute according to the specified grid.
         * 
         * @param grid
         *            the grid.
         */
        protected void setFromGrid(Grid grid) {
            super.setBoolean(grid.isBendSnap());
        }
    }

    /**
     * The parent attribute.
     */
    private GridAttribute gridAttribute;

    /**
     * Attribute representing if the snap behavior of the grid is enabled.
     */
    private SnapEnabledAttribute snapEnabledAttribute;

    /**
     * Attribute representing the snap tolerance of the grid.
     */
    private ToleranceAttribute toleranceAttribute;

    /**
     * Attribute representing if the snap behavior of the grid is enabled for
     * bends.
     */
    private SnapBendsAttribute snapBendsAttribute;

    /**
     * Constructs a {@code SnapOnGridAttribute} with the specified parent.
     * 
     * @param gridAttribute
     *            the parent of the constructed attribute.
     */
    protected SnapOnGridAttribute(GridAttribute gridAttribute) {
        super(GridAttribute.GRID_SNAP);
        this.gridAttribute = gridAttribute;
        add(snapEnabledAttribute = new SnapEnabledAttribute());
        add(toleranceAttribute = new ToleranceAttribute());
        add(snapBendsAttribute = new SnapBendsAttribute());
    }

    /**
     * Returns if the snap behavior of the grid is enabled.
     * 
     * @return if the snap behavior of the grid is enabled.
     */
    public boolean isEnabled() {
        return snapEnabledAttribute.getBoolean();
    }

    /**
     * Sets if the snap behavior of the grid is enabled.
     * 
     * @param value
     *            denotes if the snap behavior of the grid is enabled.
     */
    public void setEnabled(boolean value) {
        snapEnabledAttribute.setBoolean(value);
    }

    /**
     * Returns the snap tolerance of the grid.
     * 
     * @return the snap tolerance of the grid.
     */
    public double getTolerance() {
        return toleranceAttribute.getDouble();
    }

    /**
     * Sets the snap tolerance of the grid.
     * 
     * @param value
     *            denotes the snap tolerance of the grid.
     */
    public void setTolerance(double value) {
        toleranceAttribute.setDouble(value);
    }

    /**
     * Returns if the snap behavior of the grid is enabled for bends.
     * 
     * @return if the snap behavior of the grid is enabled for bends.
     */
    public boolean isSnapBends() {
        return snapBendsAttribute.getBoolean();
    }

    /**
     * Sets if the snap behavior of the grid is enabled for bends.
     * 
     * @param value
     *            denotes if the snap behavior of the grid is enabled for bends.
     */
    public void setSnapBends(boolean value) {
        snapBendsAttribute.setBoolean(value);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SnapOnGridAttribute copy() {
        SnapOnGridAttribute copiedAttributes = new SnapOnGridAttribute(null);
        copiedAttributes.setEnabled(isEnabled());
        copiedAttributes.setTolerance(getTolerance());
        copiedAttributes.setSnapBends(isSnapBends());
        return copiedAttributes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParent(CollectionAttribute parent)
            throws FieldAlreadySetException {
        gridAttribute = (GridAttribute) parent;
        super.setParent(parent);
    }

    /**
     * Sets the snap behavior of the grid represented by the parent attribute to
     * the value of this attribute.
     */
    protected void applyOnGrid() {
        snapEnabledAttribute.applyOnGrid();
        toleranceAttribute.applyOnGrid();
        snapBendsAttribute.applyOnGrid();
    }

    /**
     * Sets the value of this attribute to the snap behavior of the specified
     * grid.
     * 
     * @param grid
     *            the grid whose snap behavior is to be copied.
     */
    protected void setFromGrid(Grid grid) {
        snapBendsAttribute.setFromGrid(grid);
        toleranceAttribute.setFromGrid(grid);
        snapBendsAttribute.setFromGrid(grid);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
