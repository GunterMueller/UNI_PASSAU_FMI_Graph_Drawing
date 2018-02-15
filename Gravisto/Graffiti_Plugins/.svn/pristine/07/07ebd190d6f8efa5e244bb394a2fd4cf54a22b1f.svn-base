// =============================================================================
//
//   GridClassAttribute.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.graphics.grid;

import org.graffiti.attributes.StringAttribute;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugin.view.NoGrid;

/**
 * Attribute representing the type of a grid.
 * 
 * @author Kathrin Hanauer
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class GridClassAttribute extends StringAttribute {
    /**
     * The parent of this attribute.
     */
    private GridAttribute gridAttribute;

    /**
     * Constructs a {@code GridClassAttribute} with the specified parent.
     * 
     * @param gridAttribute
     */
    protected GridClassAttribute(GridAttribute gridAttribute) {
        super(GridAttribute.GRID_CLASS);
        this.gridAttribute = gridAttribute;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setString(String value) {
        if (gridAttribute != null) {
            gridAttribute.preChange();
            try {
                gridAttribute.setClass(Class.forName(value).asSubclass(
                        Grid.class));
            } catch (ClassNotFoundException e) {
                gridAttribute.setClass(NoGrid.class);
            }
            gridAttribute.postChange();
        } else {
            super.setString(value);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(Object v) throws IllegalArgumentException {
        if (!(v instanceof String))
            throw new IllegalArgumentException();
        setString((String) v);
    }

    /**
     * Makes this attribute to represent the type of the specified grid.
     * 
     * @param grid
     *            the grid whose type is to be represented.
     */
    protected void setFromGrid(Grid grid) {
        super.setString(grid.getClass().getCanonicalName());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public GridClassAttribute copy() {
        GridClassAttribute copiedAttributes = new GridClassAttribute(null);
        copiedAttributes.setString(getString());
        return copiedAttributes;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
