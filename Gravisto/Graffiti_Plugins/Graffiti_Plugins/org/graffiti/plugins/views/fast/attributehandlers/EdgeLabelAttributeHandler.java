// =============================================================================
//
//   EdgeLabelAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class EdgeLabelAttributeHandler extends
        AttributeHandler<EdgeLabelAttribute> {
    public EdgeLabelAttributeHandler() {
        super(EdgeLabelAttribute.class);
    }

    @Override
    protected boolean acceptsAttribute(Edge edge, EdgeLabelAttribute attribute) {
        return true;
    }

    @Override
    protected void onAdd(Edge edge, EdgeLabelAttribute attribute,
            FastView fastView) {
        triggerAll(edge, attribute, fastView, Attribute.class);
    }

    @Override
    protected void onChange(Edge edge, EdgeLabelAttribute attribute,
            FastView fastView) {
        triggerAll(edge, attribute, fastView, Attribute.class);
    }

    @Override
    protected void onDelete(Edge edge, EdgeLabelAttribute attribute,
            FastView fastView) {
        fastView.getLabelManager().onDeleteLabel(edge, attribute);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
