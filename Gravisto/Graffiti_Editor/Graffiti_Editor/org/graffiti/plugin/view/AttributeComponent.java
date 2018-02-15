// =============================================================================
//
//   AttributeComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeComponent.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

import java.awt.Point;

import javax.swing.JPanel;

import org.graffiti.attributes.Attribute;

/**
 * This component represents a <code>org.graffiti.attributes.Attribute</code>.
 * 
 * @version $Revision: 5768 $
 */
public abstract class AttributeComponent extends JPanel implements
        GraffitiViewComponent {

    /**
     * 
     */
    private static final long serialVersionUID = 7010154560829369643L;

    /**
     * Sets an instance of attribute which this component displays.
     * 
     * @param attr
     */
    public abstract void setAttribute(Attribute attr);

    /**
     * Returns the attribute that is displayed by this component.
     * 
     * @return the attribute that is displayed by this component.
     */
    public abstract Attribute getAttribute();

    /**
     * Sets shape of graph element to which the attribute of this component
     * belongs.
     * 
     * @param geShape
     */
    public abstract void setGraphElementShape(GraphElementShape geShape);

    /**
     * DOCUMENT ME!
     * 
     * @param shift
     *            DOCUMENT ME!
     */
    public abstract void setShift(Point shift);

    /**
     * Called when a graphics attribute of the attribute represented by this
     * component has changed.
     * 
     * @param attr
     *            the attribute that has triggered the event.
     */
    public abstract void attributeChanged(Attribute attr)
            throws ShapeNotFoundException;

    // /**
    // * Called to initialise the component of this attribute correctly. Also
    // * calls <code>repaint()</code>.
    // *
    // * @exception ShapeNotFoundException thrown when the shapeclass couldn't
    // be
    // * resolved.
    // */
    // public abstract void createNewShape()
    // throws ShapeNotFoundException;
    //    

    /**
     * Used when the shape changed in the datastructure. Makes the painter to
     * create a new shape.
     */
    public abstract void recreate() throws ShapeNotFoundException;
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
