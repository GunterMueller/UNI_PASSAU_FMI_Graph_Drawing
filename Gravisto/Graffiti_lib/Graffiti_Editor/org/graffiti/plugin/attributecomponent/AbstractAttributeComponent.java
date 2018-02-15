// =============================================================================
//
//   AbstractAttributeComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractAttributeComponent.java 5768 2010-05-07 18:42:39Z gleissner $

/*
 * $$Id: AbstractAttributeComponent.java 5768 2010-05-07 18:42:39Z gleissner $$
 */

package org.graffiti.plugin.attributecomponent;

import java.awt.Point;

import org.graffiti.attributes.Attribute;
import org.graffiti.plugin.view.AttributeComponent;
import org.graffiti.plugin.view.GraffitiViewComponent;
import org.graffiti.plugin.view.GraphElementShape;
import org.graffiti.plugin.view.ShapeNotFoundException;

/**
 * This component represents a <code>org.graffiti.attributes.Attribute</code>.
 * 
 * @version $Revision: 5768 $
 */
public abstract class AbstractAttributeComponent extends AttributeComponent
        implements GraffitiViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = -6977512867011882045L;

    /** The attribute that this component displays. */
    protected Attribute attr;

    /** The shape of the node or edge to which this attribute belongs. */
    protected GraphElementShape geShape;

    /** DOCUMENT ME! */
    protected Point shift;

    /**
     * Instantiates an <code>AttributeComponent</code>
     */
    public AbstractAttributeComponent() {
        super();
    }

    /**
     * Sets an instance of attribute which this component displays.
     * 
     * @param attr
     */
    @Override
    public void setAttribute(Attribute attr) {
        this.attr = attr;
    }

    /**
     * Returns the attribute that is displayed by this component.
     * 
     * @return the attribute that is displayed by this component.
     */
    @Override
    public Attribute getAttribute() {
        return this.attr;
    }

    /**
     * Sets shape of graph element to which the attribute of this component
     * belongs.
     * 
     * @param geShape
     */
    @Override
    public void setGraphElementShape(GraphElementShape geShape) {
        this.geShape = geShape;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param shift
     *            DOCUMENT ME!
     */
    @Override
    public void setShift(Point shift) {
        this.shift = shift;
    }

    /**
     * Called when a graphics attribute of the attribute represented by this
     * component has changed.
     * 
     * @param attr
     *            the attribute that has triggered the event.
     */
    @Override
    public abstract void attributeChanged(Attribute attr)
            throws ShapeNotFoundException;

    /**
     * Called to initialise the component of this attribute correctly. Also
     * calls <code>repaint()</code>.
     * 
     * @exception ShapeNotFoundException
     *                thrown when the shapeclass couldn't be resolved.
     */
    public void createNewShape() throws ShapeNotFoundException {
        this.recreate();

        // System.out.println("AC1");
        // this.repaint();
    }

    /**
     * Used when the shape changed in the datastructure. Makes the painter to
     * create a new shape.
     */
    @Override
    public abstract void recreate() throws ShapeNotFoundException;

    // /**
    // * Paints the attribute contained in this component.
    // *
    // * @see javax.swing.JComponent#paintComponent(Graphics)
    // */
    // public void paintComponent(Graphics g) {
    // super.paintComponent(g);
    // }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
