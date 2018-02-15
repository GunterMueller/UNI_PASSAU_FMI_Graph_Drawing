// =============================================================================
//
//   ThicknessAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.attributes.DoubleAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class ThicknessAttributeHandler extends
        AttributeHandler<DoubleAttribute> {
    public ThicknessAttributeHandler() {
        super(THICKNESS);
    }

    /*
     * @seeorg.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#
     * acceptsAttribute(org.graffiti.graph.Edge,
     * org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, DoubleAttribute attribute) {
        return equalsPath(attribute, THICKNESS_PATH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#onChange
     * (org.graffiti.graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Edge edge, DoubleAttribute attribute,
            FastView fastView) {
        fastView.getEdgeChangeListener().onChangeThickness(edge,
                attribute.getDouble());
        fastView
                .updateShape(edge, (EdgeGraphicAttribute) attribute.getParent());
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
