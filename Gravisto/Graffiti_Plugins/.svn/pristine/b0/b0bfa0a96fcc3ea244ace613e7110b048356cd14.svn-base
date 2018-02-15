// =============================================================================
//
//   GridAttribute2.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.graphics.grid;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.event.AttributeEvent;
import org.graffiti.event.ListenerManager;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugin.view.NoGrid;

/**
 * Attribute representing a grid.
 * 
 * @author Kathrin Hanauer
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Grid
 */
public class GridAttribute extends LinkedHashMapAttribute {
    /**
     * Id of the contained {@link GridClassAttribute}.
     */
    public static final String GRID_CLASS = "className";

    /**
     * Id of the contained {@link SnapOnGridAttribute}.
     */
    public static final String GRID_SNAP = "snap";

    /**
     * Id of the contained {@link GridOriginAttribute}.
     */
    public static final String GRID_ORIGIN = "origin";

    /**
     * Id of the contained {@link GridParametersAttribute}.
     */
    public static final String GRID_PARAMETERS = "parameters";

    /**
     * Attribute representing the type of the grid.
     */
    private GridClassAttribute gridClassAttribute;

    /**
     * Attribute representing the snap behavior of the grid.
     */
    private SnapOnGridAttribute snapOnGridAttribute;

    /**
     * Attribute representing the origin of the grid.
     */
    private GridOriginAttribute originAttribute;

    /**
     * Attribute representing the parameters specific to the type of the grid.
     */
    private GridParametersAttribute parametersAttribute;

    /**
     * The grid represented by this attribute.
     */
    private Grid grid;

    /**
     * Used to automatically raise {@link AttributeEvent}s when one of the
     * subattributes changes.
     */
    private AttributeEventCounter attributeEventCounter;

    /**
     * Used to automatically raise {@code AttributeEvent}s when one of the
     * subattributes changes. It contains a counter in order to create exactly
     * one {@link AttributeEvent} in the course of contiguous changes of
     * different subattributes.
     * 
     * @author Andreas Glei&szlig;ner
     * @version $Revision$ $Date$
     */
    private class AttributeEventCounter {
        /**
         * The counter.
         */
        private int counter;

        /**
         * The {@code AttributeEvent} to raise when the counter reaches zero.
         */
        private AttributeEvent attributeEvent;

        /**
         * The {@code ListenerManager} managing the transaction wrapping the
         * changes.
         */
        private ListenerManager listenerManager;

        /**
         * Constructs a {@code AttributeEventCounter} and initializes the
         * counter to zero.
         */
        private AttributeEventCounter() {
            counter = 0;
        }

        /**
         * Increases the counter. If the counter reaches one, a new
         * {@link AttributeEvent} is created.
         */
        private void inc() {
            counter++;
            if (counter == 1) {
                Attributable attributable = getAttributable();
                if (attributable != null) {
                    listenerManager = attributable.getListenerManager();
                    listenerManager.transactionStarted(GridAttribute.this);
                }
                attributeEvent = new AttributeEvent(GridAttribute.this);
                callPreAttributeChanged(attributeEvent);
            }
        }

        /**
         * Decreases the counter. If the counter reaches zero, the previously
         * created {@link AttributeEvent} is raised.
         */
        private void dec() {
            counter--;
            if (counter == 0) {
                callPostAttributeChanged(attributeEvent);
                attributeEvent = null;
                if (listenerManager != null) {
                    listenerManager.transactionFinished(GridAttribute.this);
                }
            }
        }

    }

    /**
     * Constructs a {@code GridAttribute} with the specified id.
     * 
     * @param id
     *            the id of the {@code GridAttribute}.
     */
    public GridAttribute(String id) {
        super(id);
        attributeEventCounter = new AttributeEventCounter();
        add(gridClassAttribute = new GridClassAttribute(this));
        add(snapOnGridAttribute = new SnapOnGridAttribute(this));
        add(originAttribute = new GridOriginAttribute(this));
        setClass(NoGrid.class);
    }

    /**
     * Constructs a {@code GridAttribute} that is the copy of the specified
     * other {@code GridAttribute}.
     * 
     * @param other
     *            the {@code GridAttribute} to clone.
     */
    private GridAttribute(GridAttribute other) {
        super(other.id);
        attributeEventCounter = new AttributeEventCounter();
        add(gridClassAttribute = other.gridClassAttribute.copy());
        add(snapOnGridAttribute = other.snapOnGridAttribute.copy());
        add(originAttribute = other.originAttribute.copy());
        setGrid(other.grid);
    }

    /**
     * Sets the type of the grid.
     * 
     * @param newClass
     *            the {@code Class} object representing the type of the grid.
     */
    public void setClass(Class<? extends Grid> newClass) {
        if (newClass.isInstance(grid))
            return;
        preChange();
        try {
            grid = newClass.newInstance();
            gridClassAttribute.setFromGrid(grid);
        } catch (InstantiationException e) {
            throw new IllegalArgumentException(e);
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
        snapOnGridAttribute.applyOnGrid();
        originAttribute.applyOnGrid();
        CollectionAttribute prevParametersAttribute = parametersAttribute;
        if (prevParametersAttribute == null) {
            try {
                prevParametersAttribute = (CollectionAttribute) getAttribute(GRID_PARAMETERS);
            } catch (AttributeNotFoundException e) {
            }
        }
        if (prevParametersAttribute != null) {
            remove(prevParametersAttribute);
        }
        add(parametersAttribute = new GridParametersAttribute(this, grid,
                prevParametersAttribute));
        postChange();
    }

    /**
     * Increases the counter. If the counter reaches one, a new
     * {@link AttributeEvent} is created.
     */
    protected void preChange() {
        attributeEventCounter.inc();
    }

    /**
     * Decreases the counter. If the counter reaches zero, the previously
     * created {@link AttributeEvent} is raised.
     */
    protected void postChange() {
        attributeEventCounter.dec();
    }

    /**
     * Returns the grid represented by this attribute. The same object is
     * returned while the grid type remains the same.
     * 
     * @return the grid represented by this attribute.
     */
    public Grid getGrid() {
        return grid;
    }

    /**
     * Makes this attribute to represent the specified grid. The grid returned
     * by {@link #getGrid()} will be a clone of the specified grid.
     * 
     * @param grid
     *            the grid to be represented by this attribute.
     */
    public void setGrid(Grid grid) {
        preChange();
        setClass(grid.getClass());
        gridClassAttribute.setFromGrid(grid);
        snapOnGridAttribute.setFromGrid(grid);
        originAttribute.setFromGrid(grid);
        parametersAttribute.setFromGrid(grid);
        postChange();
    }

    /**
     * Returns a clone of this attribute.
     * 
     * @return a clone of this attribute.
     */
    @Override
    public GridAttribute copy() {
        return new GridAttribute(this);
    }

    /**
     * Returns an attribute representing the snap behavior of the grid.
     * 
     * @return an attribute representing the snap behavior of the grid.
     */
    public SnapOnGridAttribute getSnapAttribute() {
        return snapOnGridAttribute;
    }

    /**
     * Returns an attribute representing the origin of the grid.
     * 
     * @return an attribute representing the origin of the grid.
     */
    public GridOriginAttribute getOriginAttribute() {
        return originAttribute;
    }

    /**
     * Returns an attribute representing the parameters specific to the type of
     * the grid.
     * 
     * @return an attribute representing the parameters specific to the type of
     *         the grid.
     */
    public GridParametersAttribute getParametersAttribute() {
        return parametersAttribute;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
