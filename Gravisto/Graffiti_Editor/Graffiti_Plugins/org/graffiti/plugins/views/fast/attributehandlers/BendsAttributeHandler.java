// =============================================================================
//
//   BendsAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class BendsAttributeHandler extends
        AncestorAttributeHandler<CoordinateAttribute> {
    public BendsAttributeHandler() {
        super(X, Y);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#acceptsAttribute(org
     * .graffiti.graph.Edge, org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, CoordinateAttribute attribute) {
        CollectionAttribute bendsAttribute = attribute.getParent();
        return bendsAttribute != null && equalsPath(bendsAttribute, BENDS_PATH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#onAdd(org.graffiti.graph
     * .Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onAdd(Edge edge, CoordinateAttribute attribute,
            FastView fastView) {
        fastView.updateShape(edge, (EdgeGraphicAttribute) attribute.getParent()
                .getParent());
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#onChange(org.graffiti
     * .graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Edge edge, CoordinateAttribute attribute,
            FastView fastView) {
        fastView.updateShape(edge, (EdgeGraphicAttribute) attribute.getParent()
                .getParent());
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#onDelete(org.graffiti
     * .graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onDelete(Edge edge, CoordinateAttribute attribute,
            FastView fastView) {
        fastView.updateShape(edge, (EdgeGraphicAttribute) attribute.getParent()
                .getParent());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
