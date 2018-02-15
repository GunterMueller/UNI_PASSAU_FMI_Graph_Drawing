// =============================================================================
//
//   FinishEdgeCreation.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.torical;

import java.awt.geom.Point2D;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.modes.fast.UndoUtil;
import org.graffiti.plugins.views.fast.AttributeUtil;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.actions.FinishEdgeCreation;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("finishEdgeCreation")
public class FinishEdgeCreationOnTorus extends FinishEdgeCreation {

    @InSlot
    public static final Slot<Point2D> positionSlot = Slot.create("position",
            Point2D.class);

    public FinishEdgeCreationOnTorus(
            StartEdgeCreationOnTorus startEdgeCreationAction) {
        super(startEdgeCreationAction);
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        Node endNode = in.get(nodeSlot);
        Node beginNode = startNode.get();
        Point2D beginNodePosition = AttributeUtil.getPosition(beginNode);
        Point2D endPosition = in.get(positionSlot);
        Point2D endNodePosition = AttributeUtil.getPosition(endNode);
        endPosition = ((ToricalFastView) view).roundToNearestCopy(endPosition,
                endNodePosition);
        Point2D beginPosition = points.getFirst();
        points.add(endPosition);
        if (beginNode == null)
            return;
        graph.getListenerManager().transactionStarted(this);
        UndoUtil undoHelper = new UndoUtil(session);
        Edge edge = undoHelper.addEdge(beginNode, endNode, true);
        undoHelper.close();
        EdgeGraphicAttribute edgeAttribute = (EdgeGraphicAttribute) edge
                .getAttribute("graphics");
        edgeAttribute.setArrowhead(ARROW_HEAD);
        int windX = ((ToricalFastView) view).calculateWindX(beginNodePosition
                .getX(), beginPosition.getX(), endNodePosition.getX(),
                endPosition.getX());
        int windY = ((ToricalFastView) view).calculateWindY(beginNodePosition
                .getY(), beginPosition.getY(), endNodePosition.getY(),
                endPosition.getY());
        edgeAttribute.getAttribute("windX").setValue(windX);
        edgeAttribute.getAttribute("windY").setValue(windY);
        /*
         * if (beginNode == endNode || !points.isEmpty()) {
         * SortedCollectionAttribute bends = new
         * LinkedHashMapAttribute("bends");
         * 
         * if (points.isEmpty()) {
         * edgeAttribute.setShape(SmoothLineEdgeShape.class.getName()); Point2D
         * pos = AttributeUtil.getPosition(beginNode); bends.add(new
         * CoordinateAttribute("bend0", pos.getX() + 50, pos.getY() - 50));
         * bends.add(new CoordinateAttribute("bend1", pos.getX() + 50,
         * pos.getY() + 50)); // Make loop } else {
         * edgeAttribute.setShape(PolyLineEdgeShape.class.getName()); int i = 0;
         * for (Point2D point : points) { bends.add(new
         * CoordinateAttribute("bend" + i, point.getX(), point.getY())); i++; }
         * } edgeAttribute.setBends(bends); }
         */
        view.getGestureFeedbackProvider().setDummyEdgePoints(null, false);
        graph.getListenerManager().transactionFinished(this);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
