// =============================================================================
//
//   FillColorAttributeHandler.java
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
public class FillColorAttributeHandler extends
        AncestorAttributeHandler<ColorAttribute> {
    public FillColorAttributeHandler() {
        super(RED, GREEN, BLUE, OPAC);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#acceptsAttribute(org
     * .graffiti.graph.Edge, org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, ColorAttribute attribute) {
        return equalsPath(attribute, FILLCOLOR_PATH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#acceptsAttribute(org
     * .graffiti.graph.Node, org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Node node, ColorAttribute attribute) {
        return equalsPath(attribute, FILLCOLOR_PATH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#onChange(org.graffiti
     * .graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Edge edge, ColorAttribute attribute,
            FastView fastView) {
        fastView.getEdgeChangeListener().onChangeFillColor(edge,
                attribute.getColor());
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#onChange(org.graffiti
     * .graph.Node, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Node node, ColorAttribute attribute,
            FastView fastView) {
        fastView.getNodeChangeListener().onChangeFillColor(node,
                attribute.getColor());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
