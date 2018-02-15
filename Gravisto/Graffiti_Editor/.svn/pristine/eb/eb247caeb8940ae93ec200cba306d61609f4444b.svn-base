// =============================================================================
//
//   FinishEdgeCreation.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.modes.fast.UndoUtil;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;
import org.graffiti.plugins.views.defaults.SmoothLineEdgeShape;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.session.EditorSession;
import org.graffiti.util.Reference;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("finishEdgeCreation")
public class FinishEdgeCreation extends FastViewAction {
    protected static final String ARROW_HEAD = "org.graffiti.plugins.views.defaults.StandardArrowShape";

    @InSlot
    public static final Slot<Node> nodeSlot = Slot.create("node", Node.class);
    protected LinkedList<Point2D> points;
    protected Reference<Node> startNode;

    public FinishEdgeCreation(StartEdgeCreation startEdgeCreationAction) {
        startNode = startEdgeCreationAction.getStartNode();
        points = startEdgeCreationAction.getPoints();
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        Node endNode = in.get(nodeSlot);
        Node beginNode = startNode.get();
        if (beginNode == null)
            return;
        points.removeLast();
        points.removeFirst();
        graph.getListenerManager().transactionStarted(this);
        UndoUtil undoHelper = new UndoUtil(session);
        Edge edge = undoHelper.addEdge(beginNode, endNode, true);
        undoHelper.close();
        EdgeGraphicAttribute edgeAttribute = (EdgeGraphicAttribute) edge
                .getAttribute("graphics");
        edgeAttribute.setArrowhead(ARROW_HEAD);
        if (beginNode == endNode || !points.isEmpty()) {
            SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                    "bends");

            if (points.isEmpty()) {
                edgeAttribute.setShape(SmoothLineEdgeShape.class.getName());
                Point2D pos = AttributeUtil.getPosition(beginNode);
                bends.add(new CoordinateAttribute("bend0", pos.getX() + 50, pos
                        .getY() - 50));
                bends.add(new CoordinateAttribute("bend1", pos.getX() + 50, pos
                        .getY() + 50));
                // Make loop
            } else {
                edgeAttribute.setShape(PolyLineEdgeShape.class.getName());
                int i = 0;
                for (Point2D point : points) {
                    bends.add(new CoordinateAttribute("bend" + i, point.getX(),
                            point.getY()));
                    i++;
                }
            }
            edgeAttribute.setBends(bends);
        }
        view.getGestureFeedbackProvider().setDummyEdgePoints(null, false);
        graph.getListenerManager().transactionFinished(this);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
