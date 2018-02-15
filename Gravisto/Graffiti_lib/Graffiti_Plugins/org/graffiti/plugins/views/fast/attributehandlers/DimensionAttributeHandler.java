// =============================================================================
//
//   DimensionAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import java.awt.geom.Point2D;

import org.graffiti.graph.Node;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DimensionAttributeHandler extends
        AncestorAttributeHandler<DimensionAttribute> {
    public DimensionAttributeHandler() {
        super(WIDTH, HEIGHT);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#acceptsAttribute(org
     * .graffiti.graph.Node, org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Node node, DimensionAttribute attribute) {
        return equalsPath(attribute, DIM_PATH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#onChange(org.graffiti
     * .graph.Node, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Node node, DimensionAttribute attribute,
            FastView fastView) {
        fastView.getNodeChangeListener()
                .onChangeSize(
                        node,
                        new Point2D.Double(attribute.getWidth(), attribute
                                .getHeight()));
        fastView
                .updateShape(node, (NodeGraphicAttribute) attribute.getParent());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
