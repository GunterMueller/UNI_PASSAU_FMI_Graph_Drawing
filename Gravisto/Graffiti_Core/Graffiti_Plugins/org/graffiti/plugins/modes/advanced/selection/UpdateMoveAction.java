// =============================================================================
//
//   UpdateMoveAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: UpdateMoveAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.selection;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.undo.UndoableEditSupport;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2008-12-31 05:02:05 +0100 (Mi, 31 Dez 2008)
 *          $
 * @deprecated
 */
@Deprecated
public class UpdateMoveAction extends AbstractFunctionAction {
    /**
     * 
     */
    private static final long serialVersionUID = 6979362413110770611L;

    /** The marked Nodes */
    private List<Node> markedNodes;

    private CoordinateAttribute[] movedNodesCoords;

    private LinkedList<CoordinateAttribute> movedBendsCoords;

    private DimensionAttribute[] movedNodesDims;

    /** Reference to the AbstractEditingTool */
    private SelectionTool selectionTool;

    /**
     * Creates a new UpdateMoveAction
     * 
     * @param selectionTool
     *            The given SelectionTool
     */
    public UpdateMoveAction(SelectionTool selectionTool) {
        this.selectionTool = selectionTool;
    }

    /**
     * The action of this class
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        // the given mouse position of the FunctionActionEvent e
        Point newPosition = e.getPosition();

        // checks if the position exists
        if (newPosition == null)
            return;

        markedNodes = selectionTool.getSelection().getNodes();
        movedBendsCoords = selectionTool.getBendCoords();
        movedNodesCoords = selectionTool.getNodeCoords();
        movedNodesDims = selectionTool.getNodeDims();

        Point oldPosition = selectionTool.getOldPosition();

        // moves the marked elements
        if (selectionTool.getMode() == SelectionTool.MOVE_MARKED_ELEMENTS) {
            // for undo-redo
            if (!selectionTool.isDuring_update_move()) {
                ChangeAttributesEdit edit;
                Map<GraphElement, GraphElement> geMap = selectionTool
                        .getGEMap();
                UndoableEditSupport undoSupport = selectionTool
                        .getUndoSupport();
                edit = new ChangeAttributesEdit(selectionTool
                        .getAttributesMap(), geMap);

                // sends the information (the old values) to the undo/redo -
                // support
                undoSupport.postEdit(edit);
                selectionTool.setDuring_update_move(true);
            }

            Point2D move = getMove(newPosition, oldPosition);
            moveNodes(move.getX(), move.getY());
            moveEdges(move.getX(), move.getY());
        }

        // only one node to move (not selected)
        else if (selectionTool.getMode() == SelectionTool.MOVE_ONLY_NODE) {
            Node moveNode = (Node) selectionTool.getGraphElementToMove();
            markedNodes = selectionTool.getSelection().getNodes();

            if (!markedNodes.contains(moveNode)) {
                markedNodes = new LinkedList<Node>();
                markedNodes.add(moveNode);
            }

            // is only 1x (at one movement) called for undo/redo (!)
            if (!selectionTool.isDuring_update_move_only_node()) {
                ChangeAttributesEdit edit;
                Map<GraphElement, GraphElement> geMap = selectionTool
                        .getGEMap();
                UndoableEditSupport undoSupport = selectionTool
                        .getUndoSupport();
                edit = new ChangeAttributesEdit(selectionTool
                        .getAttributesMap(), geMap);
                undoSupport.postEdit(edit);
                selectionTool.setDuring_update_move_only_node(true);
                selectionTool.unmarkAll();
                // markMoveElements(moveNodes, moveEdges);
            }

            Point2D move = getMove(newPosition, oldPosition);
            moveNodes(move.getX(), move.getY());
            moveEdges(move.getX(), move.getY());
        }

        // Only one bend move (no one is selected)
        else if (selectionTool.getMode() == SelectionTool.MOVE_ONLY_BEND) {
            Edge edge = null;

            if (selectionTool.getGraphElementToMove() instanceof Edge) {
                edge = (Edge) selectionTool.getGraphElementToMove();
            }

            CoordinateAttribute bend = selectionTool.getMoveElement();

            // is only 1x (at one movement) called for undo/redo (!)
            if (!selectionTool.isDuring_update_move_bend()) {
                ChangeAttributesEdit edit;

                Map<GraphElement, GraphElement> geMap = selectionTool
                        .getGEMap();
                UndoableEditSupport undoSupport = selectionTool
                        .getUndoSupport();

                edit = new ChangeAttributesEdit(selectionTool
                        .getAttributesMap(), geMap);

                undoSupport.postEdit(edit);
                selectionTool.setDuring_update_move_bend(true);

                if (!selectionTool.getSelection().contains(edge)) {
                    selectionTool.unmarkAll();
                    selectionTool.mark(edge);
                }
            }

            // sets the new position of the bend to move
            if (newPosition.x > 0) {
                bend.setX(newPosition.x);
            } else {
                bend.setX(0);
            }

            if (newPosition.y > 0) {
                bend.setY(newPosition.y);
            } else {
                bend.setY(0);
            }
        }

        if (newPosition.getX() < 0) {
            newPosition.setLocation(0, newPosition.getY());
        }

        if (newPosition.getY() < 0) {
            newPosition.setLocation(newPosition.getX(), 0);
        }

        selectionTool.setOldPosition(newPosition);
    }

    private Point2D getSmallestDistance() {
        double minX = Integer.MAX_VALUE;
        double minY = Integer.MAX_VALUE;

        for (int i = 0; i < movedNodesCoords.length; i++) {
            minX = Math.min(minX, movedNodesCoords[i].getX()
                    - movedNodesDims[i].getWidth() / 2d);
            minY = Math.min(minY, movedNodesCoords[i].getY()
                    - movedNodesDims[i].getHeight() / 2d);
        }

        for (CoordinateAttribute ca : movedBendsCoords) {
            minX = Math.min(minX, ca.getX());
            minY = Math.min(minY, ca.getY());
        }
        return new Point2D.Double(minX, minY);
    }

    private Point2D getMove(Point newPosition, Point oldPosition) {
        Point2D smallestDistance = getSmallestDistance();
        double dx = oldPosition.getX() - newPosition.getX();
        double smallestXCoordinate = smallestDistance.getX();
        // checks left border
        if (dx > smallestXCoordinate) {
            dx = smallestXCoordinate;
        }

        // the moved way between the mouse position and the moveElement
        double dy = oldPosition.getY() - newPosition.getY();

        // smallest distance to the top and left border (so that the elements
        // don't
        // disappear, includes the height and the width of the nodes)
        double smallestYCoordinate = smallestDistance.getY();

        // checks if the element is at the border (top)
        if (dy > smallestYCoordinate) {
            dy = smallestYCoordinate;
        }
        return new Point2D.Double(dx, dy);
    }

    private void moveNodes(double moveX, double moveY) {
        CoordinateAttribute[] moveCoords = selectionTool.getNodeCoords();
        // selectionTool.getGraph().getListenerManager().transactionStarted(this);
        for (int i = 0; i < moveCoords.length; i++) {
            moveCoords[i].setX(moveCoords[i].getX() - moveX);
            moveCoords[i].setY(moveCoords[i].getY() - moveY);
        }
        // selectionTool.getGraph().getListenerManager().transactionFinished(this);
    }

    private void moveEdges(double moveX, double moveY) {
        for (CoordinateAttribute ca : selectionTool.getBendCoords()) {
            ca.setX(ca.getX() - moveX);
            ca.setY(ca.getY() - moveY);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
