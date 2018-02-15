// =============================================================================
//
//   GridParameterAttribute.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.graphics.grid;

import java.lang.reflect.Field;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.FieldAlreadySetException;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugin.view.GridParameter;
import org.graffiti.util.Callback;
import org.graffiti.util.VoidCallback;
import org.graffiti.util.attributes.PrimitiveAttributeFactoryFactoryManager;

/**
 * Attribute representing the parameters specific to the type of a grid.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class GridParametersAttribute extends LinkedHashMapAttribute {
    /**
     * A factory the create attributes representing the grid parameters.
     */
    private PrimitiveAttributeFactoryFactoryManager PAFFM = new PrimitiveAttributeFactoryFactoryManager();

    /**
     * The parent of this attribute.
     */
    private GridAttribute gridAttribute;

    /**
     * Construct a {@code GridParametersAttribute}.
     * 
     * @param gridAttribute
     *            the parent of the {@code GridParametersAttribute}.
     * @param grid
     *            the grid whose parameters are to be represented.
     * @param prevParameters
     *            attribute representing grid parameters to optionally copy
     *            from. May be {@code null}.
     */
    protected GridParametersAttribute(GridAttribute gridAttribute, Grid grid,
            CollectionAttribute prevParameters) {
        super(GridAttribute.GRID_PARAMETERS);
        this.gridAttribute = gridAttribute;
        for (Field field : grid.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            addAttributeForField(field, grid, prevParameters);
        }
    }

    /**
     * Adds a new attribute representing the specified field holding the
     * parameter of the specified grid.
     * 
     * @param field
     *            the field to copy from.
     * @param grid
     *            the grid the grid whose parameter is to be represented by the
     *            added attribute.
     * @param prevParameters
     *            attribute representing grid parameters to optionally copy
     *            from. May be {@code null}.
     */
    private void addAttributeForField(final Field field, Grid grid,
            CollectionAttribute prevParameters) {
        GridParameter gp = field.getAnnotation(GridParameter.class);
        if (gp == null)
            return;
        String name = field.getName();

        Attribute attribute = PAFFM.createAttribute(name, field.getType(),
                new Callback<Boolean, Object>() {
                    public Boolean call(Object t) {
                        if (gridAttribute == null)
                            return true;
                        gridAttribute.preChange();
                        try {
                            field.set(gridAttribute.getGrid(), t);
                        } catch (IllegalArgumentException e) {
                            System.out.println(e);
                        } catch (IllegalAccessException e) {
                            System.out.println(e);
                        }
                        return true;
                    }
                }, new VoidCallback<Object>() {
                    public void call(Object t) {
                        gridAttribute.postChange();
                    }
                });
        if (attribute == null)
            return;
        Object oldValue = null;
        if (prevParameters != null) {
            try {
                oldValue = prevParameters.getAttribute(name).getValue();
            } catch (AttributeNotFoundException e) {
            }
        }
        try {
            attribute.setValue(oldValue == null ? field.get(grid) : oldValue);
        } catch (IllegalArgumentException e) {
            System.out.println(e);
        } catch (IllegalAccessException e) {
            System.out.println(e);
        }
        add(attribute);
    }

    /**
     * Makes this attribute to represent the type specific parameters of the
     * specified grid.
     * 
     * @param grid
     *            the grid whose parameters are to be represented.
     */
    protected void setFromGrid(Grid grid) {
        for (Field field : grid.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            GridParameter gridParameter = field
                    .getAnnotation(GridParameter.class);
            if (gridParameter == null) {
                continue;
            }
            Attribute attribute = attributes.get(field.getName());
            try {
                attribute.setValue(field.get(grid));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
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
     * Returns the annotation of the field holding the parameter represented by
     * the attribute specified by the id.
     * 
     * @param id
     *            the id of the attribute representing the parameter held be
     *            field whose annotation is to be returned.
     * @return the annotation of the field holding the parameter represented by
     *         the attribute specified by the id or {@code null} if the
     *         attribute or the annotation is not present.
     */
    public GridParameter getGridParameter(String id) {
        if (gridAttribute == null)
            return null;
        try {
            return gridAttribute.getGrid().getClass().getField(id)
                    .getAnnotation(GridParameter.class);
        } catch (SecurityException e) {
        } catch (NoSuchFieldException e) {
        }
        return null;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
