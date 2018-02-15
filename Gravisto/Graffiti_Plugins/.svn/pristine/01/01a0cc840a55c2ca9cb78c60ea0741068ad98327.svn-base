// =============================================================================
//
//   LineModeAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LineModeAttribute;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class LineModeAttributeHandler extends
        AttributeHandler<LineModeAttribute> {
    public LineModeAttributeHandler() {
        super(LINEMODE);
    }

    /*
     * @seeorg.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#
     * acceptsAttribute(org.graffiti.graph.Edge,
     * org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, LineModeAttribute attribute) {
        return equalsPath(attribute, LINEMODE_PATH);
    }

    /*
     * @seeorg.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#
     * acceptsAttribute(org.graffiti.graph.Node,
     * org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Node node, LineModeAttribute attribute) {
        return equalsPath(attribute, LINEMODE_PATH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#onChange
     * (org.graffiti.graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Edge edge, LineModeAttribute attribute,
            FastView fastView) {
        fastView.getEdgeChangeListener().onChangeDash(edge,
                attribute.getValue());
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#onChange
     * (org.graffiti.graph.Node, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Node node, LineModeAttribute attribute,
            FastView fastView) {
        fastView.getNodeChangeListener().onChangeDash(node,
                attribute.getValue());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
