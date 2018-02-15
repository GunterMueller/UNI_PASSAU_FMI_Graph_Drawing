// =============================================================================
//
//   ShapeAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.view.EdgeShape;
import org.graffiti.plugin.view.NodeShape;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ShapeAttributeHandler extends AttributeHandler<StringAttribute> {
    public ShapeAttributeHandler() {
        super(SHAPE);
    }

    /*
     * @seeorg.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#
     * acceptsAttribute(org.graffiti.graph.Edge,
     * org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, StringAttribute attribute) {
        return equalsPath(attribute, SHAPE_PATH);
    }

    /*
     * @seeorg.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#
     * acceptsAttribute(org.graffiti.graph.Node,
     * org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Node node, StringAttribute attribute) {
        return equalsPath(attribute, SHAPE_PATH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#onChange
     * (org.graffiti.graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Edge edge, StringAttribute attribute,
            FastView fastView) {
        try {
            EdgeShape shape = (EdgeShape) InstanceLoader
                    .createInstance(attribute.getString());
            fastView.getEdgeChangeListener().onSetShape(edge, shape);
            fastView.updateShape(edge, (EdgeGraphicAttribute) attribute
                    .getParent());
        } catch (InstanceCreationException e) {
            // TODO:
            assert (false);
        }
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#onChange
     * (org.graffiti.graph.Node, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Node node, StringAttribute attribute,
            FastView fastView) {
        try {
            NodeShape shape = (NodeShape) InstanceLoader
                    .createInstance(attribute.getString());
            fastView.getNodeChangeListener().onSetShape(node, shape);
            fastView.updateShape(node, (NodeGraphicAttribute) attribute
                    .getParent());
        } catch (InstanceCreationException e) {
            // TODO:
            assert (false);
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
