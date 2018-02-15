// =============================================================================
//
//   NodeLabelAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugins.views.fast.FastView;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class NodeLabelAttributeHandler extends
        AttributeHandler<NodeLabelAttribute> {
    public NodeLabelAttributeHandler() {
        super(NodeLabelAttribute.class);
    }

    @Override
    protected boolean acceptsAttribute(Node node, NodeLabelAttribute attribute) {
        return true;
    }

    @Override
    protected void onAdd(Node node, NodeLabelAttribute attribute,
            FastView fastView) {
        triggerAll(node, attribute, fastView, Attribute.class);
    }

    @Override
    protected void onChange(Node node, NodeLabelAttribute attribute,
            FastView fastView) {
        triggerAll(node, attribute, fastView, Attribute.class);
    }

    @Override
    protected void onDelete(Node node, NodeLabelAttribute attribute,
            FastView fastView) {
        fastView.getLabelManager().onDeleteLabel(node, attribute);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
