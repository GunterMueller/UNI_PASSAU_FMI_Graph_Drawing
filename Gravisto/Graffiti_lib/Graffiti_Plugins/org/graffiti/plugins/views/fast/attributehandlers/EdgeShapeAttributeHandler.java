// =============================================================================
//
//   EdgeShapeAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class EdgeShapeAttributeHandler extends
        AncestorAttributeHandler<EdgeGraphicAttribute> {
    public EdgeShapeAttributeHandler() {
        super(ARROWHEAD, ARROWTAIL, LINETYPE);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#acceptsAttribute(org
     * .graffiti.graph.Edge, org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, EdgeGraphicAttribute attribute) {
        return true;
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#onChange(org.graffiti
     * .graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Edge edge, EdgeGraphicAttribute attribute,
            FastView fastView) {
        fastView.updateShape(edge, attribute);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
