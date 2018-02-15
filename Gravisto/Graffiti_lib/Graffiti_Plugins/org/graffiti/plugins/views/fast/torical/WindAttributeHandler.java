// =============================================================================
//
//   ThicknessAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.torical;

import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.attributehandlers.AttributeHandler;

/**
 * @author Wolfgang Brunner
 * @version $Revision$ $Date$
 */
public class WindAttributeHandler extends AttributeHandler<IntegerAttribute> {
    public WindAttributeHandler() {
        super("windX", "windY");
    }

    /*
     * @seeorg.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#
     * acceptsAttribute(org.graffiti.graph.Edge,
     * org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, IntegerAttribute attribute) {
        return equalsPath(attribute, "graphics.windX")
                || equalsPath(attribute, "graphics.windY");
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#onChange
     * (org.graffiti.graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Edge edge, IntegerAttribute attribute,
            FastView fastView) {
        /*
         * fastView.getEdgeChangeListener().onChangeWinding( edge,
         * attribute.getInteger());
         */
        fastView
                .updateShape(edge, (EdgeGraphicAttribute) attribute.getParent());
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
