// =============================================================================
//
//   GraphElementComponentFacade.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.util.Iterator;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.view.AttributeComponent;
import org.graffiti.plugin.view.EdgeComponentInterface;
import org.graffiti.plugin.view.GraphElementComponent;
import org.graffiti.plugin.view.GraphElementShape;
import org.graffiti.plugin.view.NodeComponentInterface;
import org.graffiti.plugin.view.ShapeNotFoundException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Dummy preventing Gravisto crashing while the old modes are still being
 * active.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class GraphElementComponentFacade extends GraphElementComponent
        implements EdgeComponentInterface, NodeComponentInterface {
    /**
     * 
     */
    private static final long serialVersionUID = 1160743698886869240L;
    GraphElement graphElement;
    ViewAdapter viewAdapter;

    public GraphElementComponentFacade(ViewAdapter viewAdapter,
            GraphElement graphElement) {
        this.viewAdapter = viewAdapter;
        this.graphElement = graphElement;
    }

    /*
     * @see
     * org.graffiti.plugin.view.GraffitiViewComponent#attributeChanged(org.graffiti
     * .attributes.Attribute)
     */
    public void attributeChanged(Attribute attr) throws ShapeNotFoundException {
    }

    /*
     * @see org.graffiti.plugin.view.GraffitiViewComponent#createNewShape()
     */
    public void createNewShape() throws ShapeNotFoundException {
    }

    /*
     * @see
     * org.graffiti.plugin.view.GraphElementComponentInterface#addAttributeComponent
     * (org.graffiti.attributes.Attribute,
     * org.graffiti.plugin.view.AttributeComponent)
     */
    public void addAttributeComponent(Attribute attr, AttributeComponent ac) {
    }

    /*
     * @see
     * org.graffiti.plugin.view.GraphElementComponentInterface#addDependentComponent
     * (org.graffiti.plugin.view.GraphElementComponent)
     */
    public void addDependentComponent(GraphElementComponent comp) {
    }

    /*
     * @seeorg.graffiti.plugin.view.GraphElementComponentInterface#
     * clearAttributeComponentList()
     */
    public void clearAttributeComponentList() {
    }

    /*
     * @seeorg.graffiti.plugin.view.GraphElementComponentInterface#
     * clearDependentComponentList()
     */
    public void clearDependentComponentList() {
    }

    /*
     * @see
     * org.graffiti.plugin.view.GraphElementComponentInterface#createStandardShape
     * ()
     */
    public void createStandardShape() {
    }

    /*
     * @see
     * org.graffiti.plugin.view.GraphElementComponentInterface#getAttributeComponent
     * (org.graffiti.attributes.Attribute)
     */
    public AttributeComponent getAttributeComponent(Attribute attr) {
        throw new NotImplementedException();
    }

    /*
     * @seeorg.graffiti.plugin.view.GraphElementComponentInterface#
     * getAttributeComponentIterator()
     */
    public Iterator<AttributeComponent> getAttributeComponentIterator() {
        throw new NotImplementedException();
    }

    /*
     * @see
     * org.graffiti.plugin.view.GraphElementComponentInterface#getGraphElement()
     */
    public GraphElement getGraphElement() {
        return graphElement;
    }

    /*
     * @see org.graffiti.plugin.view.GraphElementComponentInterface#getShape()
     */
    public GraphElementShape getShape() {
        throw new UnsupportedOperationException("Not yet implemented.");
    }

    /*
     * @seeorg.graffiti.plugin.view.GraphElementComponentInterface#
     * graphicAttributeChanged(org.graffiti.attributes.Attribute)
     */
    public void graphicAttributeChanged(Attribute attr)
            throws ShapeNotFoundException {
    }

    /*
     * @seeorg.graffiti.plugin.view.GraphElementComponentInterface#
     * nonGraphicAttributeChanged(org.graffiti.attributes.Attribute)
     */
    public void nonGraphicAttributeChanged(Attribute attr)
            throws ShapeNotFoundException {
    }

    /*
     * @seeorg.graffiti.plugin.view.GraphElementComponentInterface#
     * removeAttributeComponent(org.graffiti.attributes.Attribute)
     */
    public void removeAttributeComponent(Attribute attr) {
    }

    /*
     * @seeorg.graffiti.plugin.view.GraphElementComponentInterface#
     * removeDependentComponent(org.graffiti.plugin.view.GraphElementComponent)
     */
    public void removeDependentComponent(GraphElementComponent comp) {
    }

    /*
     * @see
     * org.graffiti.plugin.view.EdgeComponentInterface#setSourceComponent(org
     * .graffiti.plugin.view.NodeComponentInterface)
     */
    public void setSourceComponent(NodeComponentInterface snc) {
    }

    /*
     * @see
     * org.graffiti.plugin.view.EdgeComponentInterface#setTargetComponent(org
     * .graffiti.plugin.view.NodeComponentInterface)
     */
    public void setTargetComponent(NodeComponentInterface tnc) {
    }

    /*
     * @see org.graffiti.plugin.view.EdgeComponentInterface#updateShape()
     */
    public void updateShape() {
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
