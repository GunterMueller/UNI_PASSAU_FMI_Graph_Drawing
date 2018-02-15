// =============================================================================
//
//   DockingAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.DockingAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DockingAttributeHandler extends
        AncestorAttributeHandler<DockingAttribute> {
    public DockingAttributeHandler() {
        super(SOURCE, TARGET);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#acceptsAttribute(org
     * .graffiti.graph.Edge, org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, DockingAttribute attribute) {
        return equalsPath(attribute, DOCKING_PATH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#onChange(org.graffiti
     * .graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Edge edge, DockingAttribute attribute,
            FastView fastView) {
        fastView
                .updateShape(edge, (EdgeGraphicAttribute) attribute.getParent());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
