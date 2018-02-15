// =============================================================================
//
//   FrameColorAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class FrameColorAttributeHandler extends
        AncestorAttributeHandler<ColorAttribute> {
    public FrameColorAttributeHandler() {
        super(RED, GREEN, BLUE, OPAC);
    }

    /*
     * @seeorg.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#
     * acceptsAttribute(org.graffiti.graph.Edge,
     * org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, ColorAttribute attribute) {
        return equalsPath(attribute, OUTLINE_PATH);
    }

    /*
     * @seeorg.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#
     * acceptsAttribute(org.graffiti.graph.Node,
     * org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Node node, ColorAttribute attribute) {
        return equalsPath(attribute, OUTLINE_PATH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#onChange
     * (org.graffiti.graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Edge edge, ColorAttribute attribute,
            FastView fastView) {
        fastView.getEdgeChangeListener().onChangeFrameColor(edge,
                attribute.getColor());
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#onChange
     * (org.graffiti.graph.Node, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Node node, ColorAttribute attribute,
            FastView fastView) {
        fastView.getNodeChangeListener().onChangeFrameColor(node,
                attribute.getColor());
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
