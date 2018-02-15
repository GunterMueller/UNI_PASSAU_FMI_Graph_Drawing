// =============================================================================
//
//   MoveNodes.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.modes.fast.UndoUtil;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.FastViewGestureFeedbackProvider;
import org.graffiti.session.EditorSession;
import org.graffiti.util.Pair;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("moveNodes")
public class MoveNodes extends FastViewAction {
    @InSlot
    public static final Slot<Point2D> positionSlot = Slot.create("position",
            Point2D.class);

    private LinkedList<Pair<CoordinateAttribute, Point2D>> nodeCoordinateList;
    private LinkedList<Pair<CoordinateAttribute, Point2D>> bendCoordinateList;
    private Point2D startPosition;
    private boolean wasUsed;

    public MoveNodes() {
        nodeCoordinateList = new LinkedList<Pair<CoordinateAttribute, Point2D>>();
        bendCoordinateList = new LinkedList<Pair<CoordinateAttribute, Point2D>>();
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        FastViewGestureFeedbackProvider gfp = view.getGestureFeedbackProvider();
        Point2D newPosition = in.get(positionSlot);
        double dx = newPosition.getX() - startPosition.getX();
        double dy = newPosition.getY() - startPosition.getY();
        UndoUtil undoUtil = null;
        if (!wasUsed) {
            undoUtil = new UndoUtil(session);
        }
        for (Pair<CoordinateAttribute, Point2D> pair : nodeCoordinateList) {
            Point2D point = pair.getSecond();
            CoordinateAttribute ca = pair.getFirst();
            if (!wasUsed) {
                undoUtil.preChange(ca);
            }
            ca.setCoordinate(gfp.snapNode(new Point2D.Double(point.getX() + dx,
                    point.getY() + dy)));
        }
        for (Pair<CoordinateAttribute, Point2D> pair : bendCoordinateList) {
            Point2D point = pair.getSecond();
            CoordinateAttribute ca = pair.getFirst();
            if (!wasUsed) {
                undoUtil.preChange(ca);
            }
            ca.setCoordinate(gfp.snapBend(new Point2D.Double(point.getX() + dx,
                    point.getY() + dy)));
        }
        if (!wasUsed) {
            undoUtil.close();
            wasUsed = true;
        }
    }

    public void setNodes(Set<Node> nodes) {
        nodeCoordinateList.clear();
        bendCoordinateList.clear();
        Set<Edge> edges = new HashSet<Edge>();
        for (Node node : nodes) {
            for (Edge edge : node.getEdges()) {
                if (edge.getSource() == edge.getTarget()
                        || (nodes.contains(edge.getSource()) && nodes
                                .contains(edge.getTarget()))) {
                    edges.add(edge);
                }
            }
            CoordinateAttribute ca = (CoordinateAttribute) node
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);
            nodeCoordinateList.add(Pair.create(ca, ca.getCoordinate()));
        }
        for (Edge edge : edges) {
            SortedCollectionAttribute sca = (SortedCollectionAttribute) edge
                    .getAttribute(GraphicAttributeConstants.BENDS_PATH);
            for (Attribute a : sca.getCollection().values()) {
                if (a instanceof CoordinateAttribute) {
                    CoordinateAttribute ca = (CoordinateAttribute) a;
                    bendCoordinateList.add(Pair.create(ca, ca.getCoordinate()));
                }
            }
        }
        wasUsed = false;
    }

    public void setStartPosition(Point2D startPosition) {
        this.startPosition = startPosition;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
