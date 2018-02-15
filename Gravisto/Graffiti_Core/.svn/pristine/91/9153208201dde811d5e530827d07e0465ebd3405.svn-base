// =============================================================================
//
//   EdgeLabelAlignmentAttributeHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.attributehandlers;

import org.graffiti.graph.Edge;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.label.Label;
import org.graffiti.plugins.views.fast.label.LabelCommand;
import org.graffiti.plugins.views.fast.label.LabelManager;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class EdgeLabelAlignmentAttributeHandler extends
        AncestorAttributeHandler<EdgeLabelAttribute> {
    public EdgeLabelAlignmentAttributeHandler() {
        super(ALIGNMENT);
    }

    /*
     * @seeorg.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#
     * acceptsAttribute(org.graffiti.graph.Node,
     * org.graffiti.attributes.Attribute)
     */
    @Override
    protected boolean acceptsAttribute(Edge edge, EdgeLabelAttribute attribute) {
        return true;
    }

    /*
     * @see
     * org.graffiti.plugins.views.fast.attributehandlers.AttributeHandler#onChange
     * (org.graffiti.graph.Edge, org.graffiti.attributes.Attribute,
     * org.graffiti.plugins.views.fast.FastView)
     */
    @Override
    protected void onChange(Edge edge, EdgeLabelAttribute attribute,
            FastView fastView) {
        onChange(edge, attribute, fastView.getLabelManager());
    }

    private <L extends Label<L, LC>, LC extends LabelCommand> void onChange(
            Edge edge, EdgeLabelAttribute attribute, LabelManager<L, LC> manager) {
        L label = manager.acquireLabel(edge, attribute);
        manager.changePosition(label);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
