// =============================================================================
//
//   StartMoveAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StartMoveAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.selection;

import java.awt.Point;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
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
public class StartMoveAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 6874229025271523327L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(ChangeAttributesEdit.class.getName());

    /** Reference to the AbstractEditingTool */
    private SelectionTool selectionTool;

    /**
     * Creates a new StartMoveAction
     * 
     * @param selectionTool
     *            the given AbstractEditingTool
     */
    public StartMoveAction(SelectionTool selectionTool) {
        this.selectionTool = selectionTool;
    }

    /**
     * Performes the action of this class.
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        Point pos = e.getPosition();

        if (pos == null) {
            logger.finer("Can't operate without position!");

            return;
        }

        GraphElement mouseOverElement = selectionTool.getTopGraphElement(pos);

        // check if tool is not in default mode
        if (!selectionTool.isDefaultMode())
            return;
        selectionTool.getGraph().getListenerManager().transactionStarted(this);
        selectionTool.setMoveNodes(null);
        selectionTool.setMoveEdges(null);

        List<GraphElement> markedElements = selectionTool.getSelection()
                .getElements();
        List<Node> markedNodes = selectionTool.getSelection().getNodes();
        selectionTool.setAttributesMap(new HashMap<Attribute, Object>());

        // list with the edges which have to be moved
        List<Edge> moveEdges = new LinkedList<Edge>();

        // mouse over a node
        if (mouseOverElement instanceof Node) {
            // node under mouse-position is marked
            if (!markedElements.contains(mouseOverElement)) {
                selectionTool.getSelection().clear();
                selectionTool.getSelection().add(mouseOverElement);
                GraffitiSingleton.getInstance().getMainFrame()
                        .getActiveEditorSession().getSelectionModel()
                        .selectionChanged();
            }

            // the actual node where's the mouse over
            Node actNode = (Node) mouseOverElement;

            markedNodes = selectionTool.getSelection().getNodes();

            for (Edge checkEdge : selectionTool.getGraph().getEdges()) {
                // checkEdge is connection between two marked nodes
                if (markedNodes.contains(checkEdge.getSource())
                        && markedNodes.contains(checkEdge.getTarget())) {
                    moveEdges.add(checkEdge);
                }
            }

            // change mode to move marked elements mode
            selectionTool.setModeToMoveMarkedElements();

            CoordinateAttribute cooAttActNode = selectionTool
                    .getCooAttNode(actNode);

            // sets the actual move element
            selectionTool.setMoveElement(cooAttActNode);

            int x = (int) cooAttActNode.getX();
            int y = (int) cooAttActNode.getY();
            Point startPos = new Point(x, y);

            selectionTool.setOldPosition(startPos);

            // sets the elements which has to be moved
            selectionTool.setMoveNodes(markedNodes);
            selectionTool.setMoveEdges(moveEdges);

            storeAttributes(markedNodes, moveEdges, pos);
        }
        // mouse not over a node
        else if (mouseOverElement instanceof Edge) {
            Edge edge = (Edge) mouseOverElement;
            findAndMoveBend(edge, moveEdges, pos);
        } else {
            Collection<Edge> edges = selectionTool.getGraph().getEdges();
            for (Edge edge : edges) {
                if (findAndMoveBend(edge, moveEdges, pos)) {
                    break;
                }
            }
        }
        selectionTool.setOldPosition(pos);
        selectionTool.getGraph().getListenerManager().transactionFinished(this);
    }

    private void storeAttributes(List<Node> moveNodes, List<Edge> moveEdges,
            Point mousePos) {
        CoordinateAttribute[] nodeCoords = new CoordinateAttribute[moveNodes
                .size()];
        DimensionAttribute[] nodeDims = new DimensionAttribute[moveNodes.size()];
        LinkedList<CoordinateAttribute> bendCoords = new LinkedList<CoordinateAttribute>();

        int i = 0;
        for (Node node : moveNodes) {
            NodeGraphicAttribute nga = (NodeGraphicAttribute) node
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            nodeCoords[i] = nga.getCoordinate();
            nodeDims[i] = nga.getDimension();

            // undo / redo
            CoordinateAttribute coordinateAttributeCopy = (CoordinateAttribute) nodeCoords[i]
                    .copy();
            Object coordinateAttributeCopyValue = coordinateAttributeCopy
                    .getValue();
            selectionTool.addAttributesToMap(nodeCoords[i],
                    coordinateAttributeCopyValue);

            i++;
        }

        for (Edge edge : moveEdges) {
            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            Collection<Attribute> bendsColl = ega.getBends().getCollection()
                    .values();
            for (Attribute attr : bendsColl) {
                CoordinateAttribute ca = (CoordinateAttribute) attr;
                bendCoords.add(ca);

                // undo/redo
                CoordinateAttribute moveBendCopy = (CoordinateAttribute) ca
                        .copy();
                Object moveBendCopyValue = moveBendCopy.getValue();
                selectionTool.addAttributesToMap(ca, moveBendCopyValue);
            }
        }

        selectionTool.setBendCoords(bendCoords);
        selectionTool.setNodeCoords(nodeCoords);
        selectionTool.setNodeDims(nodeDims);
    }

    private boolean findAndMoveBend(Edge edge, List<Edge> moveEdges, Point pos) {
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        Collection<Attribute> bendsColl = ega.getBends().getCollection()
                .values();
        boolean foundBend = false;
        for (Attribute attr : bendsColl) {
            CoordinateAttribute moveBend = (CoordinateAttribute) attr;
            // checks if the mouse position is "near" the actual bend
            if ((pos.x <= (moveBend.getX() + 6))
                    && (pos.x >= (moveBend.getX() - 6))
                    && (pos.y >= (moveBend.getY() - 6))
                    && (pos.y <= (moveBend.getY() + 6))) {
                // sets moveBend to the move element
                selectionTool.setMoveElement(moveBend);

                moveEdges = new LinkedList<Edge>();
                moveEdges.add(edge);
                selectionTool.setMoveEdges(moveEdges);
                selectionTool.setMoveNodes(new LinkedList<Node>());

                selectionTool.setGraphElementToMove(edge);
                foundBend = true;
            }
        }
        // a bend was found where mouse is over
        if (foundBend) {
            selectionTool.setModeToMoveOnlyBend();
        }

        // no bend near mouse position was found
        else {
            selectionTool.setModeToDefault();
            selectionTool.setAttributesMap(new HashMap<Attribute, Object>());
        }
        return foundBend;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
