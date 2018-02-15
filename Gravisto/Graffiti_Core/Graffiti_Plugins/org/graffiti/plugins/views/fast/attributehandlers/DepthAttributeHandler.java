// =============================================================================
//
//   DepthAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DepthAttributeHandler extends AttributeHandler<DoubleAttribute> {
    public DepthAttributeHandler() {
        super(Z, DEPTH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#acceptsAttribute(org
     * .graffiti.graph.Edge, org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, DoubleAttribute attribute) {
        return equalsPath(attribute, DEPTH_PATH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#acceptsAttribute(org
     * .graffiti.graph.Node, org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Node node, DoubleAttribute attribute) {
        return equalsPath(attribute, COORDZ_PATH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#onChange(org.graffiti
     * .graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Edge edge, DoubleAttribute attribute,
            FastView fastView) {
        fastView.getEdgeChangeListener().onChangeDepth(edge,
                attribute.getDouble());
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#onChange(org.graffiti
     * .graph.Node, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Node node, DoubleAttribute attribute,
            FastView fastView) {
        fastView.getNodeChangeListener().onChangeDepth(node,
                attribute.getDouble());
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
