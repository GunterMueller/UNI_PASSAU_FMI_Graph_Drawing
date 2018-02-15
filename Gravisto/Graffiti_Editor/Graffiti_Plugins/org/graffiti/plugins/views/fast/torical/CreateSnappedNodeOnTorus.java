// =============================================================================
//
//   CreateSnappedNode.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.torical;

import java.awt.geom.Point2D;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugins.modes.fast.UndoUtil;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.FastViewGestureFeedbackProvider;
import org.graffiti.plugins.views.fast.actions.CreateSnappedNode;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("createSnappedNodeOnTorus")
public class CreateSnappedNodeOnTorus extends CreateSnappedNode {

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        FastViewGestureFeedbackProvider gfp = view.getGestureFeedbackProvider();
        Point2D position = gfp.snapNode(in.get(positionSlot));
        position = ((ToricalFastView) view).transformOnTorus(position);
        UndoUtil undoUtil = new UndoUtil(session);
        Node node = undoUtil.addNode();
        undoUtil.close();
        ((CoordinateAttribute) node
                .getAttribute(GraphicAttributeConstants.COORD_PATH))
                .setCoordinate(position);
        out.put(nodeSlot, node);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
