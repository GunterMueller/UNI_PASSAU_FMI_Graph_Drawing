// =============================================================================
//
//   GraphElementComponentInterface.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphElementComponentInterface.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

import java.util.Iterator;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.GraphElement;

/**
 * 
 */
public interface GraphElementComponentInterface {

    // /**
    // * Draws the shape of the node contained in this component according to
    // the
    // * graphic attributes of the node.
    // *
    // * @param g the graphics context in which to draw.
    // */
    // public void drawShape(Graphics g);
    //    
    // /**
    // * Used when the shape changed in the datastructure.
    // *
    // * @throws ShapeNotFoundException DOCUMENT ME!
    // */
    // public void recreate()
    // throws ShapeNotFoundException;

    /**
     * Returns the attributeComponents of given attribute.
     * 
     * @param attr
     * 
     * @return Map
     */
    public AttributeComponent getAttributeComponent(Attribute attr);

    /**
     * Returns the attributeComponents of given attribute.
     * 
     * @return Map
     */
    public Iterator<AttributeComponent> getAttributeComponentIterator();

    /**
     * Returns the graphElement.
     * 
     * @return GraphElement
     */
    public GraphElement getGraphElement();

    /**
     * Returns GraphElementShape object
     * 
     * @return DOCUMENT ME!
     */
    public GraphElementShape getShape();

    /**
     * Adds an <code>Attribute</code> and its <code>GraffitiViewComponent</code>
     * to the list of registered attributes that can be displayed. This
     * attribute is then treated as dependent on the position, size etc. of this
     * <code>GraphElement</code>.
     * 
     * @param attr
     *            the attribute that is registered as being able to be
     *            displayed.
     * @param ac
     *            the component that will be used to display the attribute.
     */
    public void addAttributeComponent(Attribute attr, AttributeComponent ac);

    /**
     * Adds a <code>GraphElementComponent</code> to the list of dependent
     * <code>GraphElementComponent</code>s. These will nearly always be
     * <code>EdgeComponent</code>s that are dependent on their source or target
     * nodes.
     * 
     * @param comp
     *            the <code>GraphElementComponent</code> that is added to the
     *            list of dependent components.
     */
    public void addDependentComponent(GraphElementComponent comp);

    /**
     * Called when an attribute of the GraphElement represented by this
     * component has changed.
     * 
     * @param attr
     *            the attribute that has triggered the event.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    public void attributeChanged(Attribute attr) throws ShapeNotFoundException;

    /**
     * Removes all entries in the attributeComponent list.
     */
    public void clearAttributeComponentList();

    /**
     * Removes a <code>GraphElementComponent</code> from the list of dependent
     * <code>GraphElementComponent</code>s.
     */
    public void clearDependentComponentList();

    /**
     * Called to initialise the shape of the NodeComponent correctly. Also calls
     * <code>repaint()</code>.
     * 
     * @exception ShapeNotFoundException
     *                thrown when the shapeclass couldn't be resolved.
     */
    public void createNewShape() throws ShapeNotFoundException;

    /**
     * Called to initialise and draw a standard shape, if the specified
     * shapeclass could not be found.
     */
    public void createStandardShape();

    /**
     * Called when a graphic attribute of the GraphElement represented by this
     * component has changed.
     * 
     * @param attr
     *            the graphic attribute that has triggered the event.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    public void graphicAttributeChanged(Attribute attr)
            throws ShapeNotFoundException;

    /**
     * Called when a non-graphic attribute of the GraphElement represented by
     * this component has changed.
     * 
     * @param attr
     *            the attribute that has triggered the event.
     * 
     * @throws ShapeNotFoundException
     *             DOCUMENT ME!
     */
    public void nonGraphicAttributeChanged(Attribute attr)
            throws ShapeNotFoundException;

    /**
     * Removes a <code>GraffitiViewComponent</code> of an <code>Attribute</code>
     * from collection of attribute components.
     * 
     * @param attr
     *            the attribute that has to be removed
     */
    public void removeAttributeComponent(Attribute attr);

    /**
     * Removes a <code>GraphElementComponent</code> from the list of dependent
     * <code>GraphElementComponent</code>s.
     * 
     * @param comp
     *            the <code>GraphElementComponent</code> that is removed from
     *            the list of dependent components.
     */
    public void removeDependentComponent(GraphElementComponent comp);

    // /**
    // * Retrieve the zoom value from the view this component is displayed in.
    // *
    // * @return DOCUMENT ME!
    // */
    // public AffineTransform getZoom();
    //
    // /**
    // * Draws the shape of the graph element contained in this component
    // * according to its graphic attributes.
    // *
    // * @param g the graphics context in which to draw.
    // */
    // public void drawShape(Graphics g);
    //    
    // /**
    // * Used when the shape changed in the datastructure. Makes the painter
    // * create a new shape.
    // */
    // public void recreate()
    // throws ShapeNotFoundException;
    //
    // /**
    // * Called whenever the size of the shape within this component has
    // changed.
    // */
    // public void adjustComponentSize();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
