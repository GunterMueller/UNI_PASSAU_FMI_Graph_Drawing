// =============================================================================
//
//   RemoveBends.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.util.Set;

import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.modes.fast.UndoUtil;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("removeBends")
public class RemoveBends extends FastViewAction {
    @InSlot
    public static final Slot<Set<Edge>> edgesSlot = Slot.createSetSlot("edges",
            Edge.class);

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        Set<Edge> edges = in.get(edgesSlot);
        graph.getListenerManager().transactionStarted(this);
        UndoUtil undoUtil = new UndoUtil(session);
        for (Edge edge : edges) {
            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            undoUtil.preChange(ega);
            ega.setBends(new LinkedHashMapAttribute(ega.getBends().getId()));
            StringAttribute sa = (StringAttribute) edge
                    .getAttribute(GraphicAttributeConstants.SHAPE_PATH);
            undoUtil.preChange(sa);
            sa.setString(GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME);
        }
        undoUtil.close();
        graph.getListenerManager().transactionFinished(this);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
