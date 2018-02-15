// =============================================================================
//
//   NodeLabelPositionAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.graph.Node;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.label.Label;
import org.graffiti.plugins.views.fast.label.LabelCommand;
import org.graffiti.plugins.views.fast.label.LabelManager;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class NodeLabelPositionAttributeHandler extends
        AncestorAttributeHandler<NodeLabelAttribute> {
    private static NodeLabelPositionAttributeHandler singleton;

    public static NodeLabelPositionAttributeHandler get() {
        return singleton;
    }

    public NodeLabelPositionAttributeHandler() {
        super(2, ABSOLUTE_X_OFFSET, ABSOLUTE_Y_OFFSET, RELATIVE_X_OFFSET,
                RELATIVE_Y_OFFSET, ALIGNMENT_X, ALIGNMENT_Y, FRAMETHICKNESS,
                ROTATION);
        singleton = this;
    }

    /*
     * @seeorg.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#
     * acceptsAttribute(org.graffiti.graph.Node,
     * org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Node node, NodeLabelAttribute attribute) {
        return true;
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#onChange
     * (org.graffiti.graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Node node, NodeLabelAttribute attribute,
            FastView fastView) {
        onChange(node, attribute, fastView.getLabelManager());
    }

    private <L extends Label<L, LC>, LC extends LabelCommand> void onChange(
            Node node, NodeLabelAttribute attribute, LabelManager<L, LC> manager) {
        L label = manager.acquireLabel(node, attribute);
        manager.changePosition(label);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
