// =============================================================================
//
//   CoordinateAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class CoordinateAttributeHandler extends
        AncestorAttributeHandler<CoordinateAttribute> {
    public CoordinateAttributeHandler() {
        super(X, Y);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#acceptsAttribute(org
     * .graffiti.graph.Node, org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Node node, CoordinateAttribute attribute) {
        return equalsPath(attribute, COORD_PATH);
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.AttributeHandler#onChange(org.graffiti
     * .graph.Node, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Node node, CoordinateAttribute attribute,
            FastView fastView) {
        fastView.getNodeChangeListener().onChangePosition(node,
                attribute.getCoordinate());
        NodeLabelPositionAttributeHandler.get().triggerAllAccepted(node,
                fastView, NodeLabelAttribute.class);
        fastView.updateIncidentEdgeShapes(node);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
