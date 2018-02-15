// =============================================================================
//
//   StartMoveWithBendsAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StartMoveWithBendsAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.selection;

import java.awt.Point;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * To start a movement of all marked elements.
 * 
 * @author MH
 * @deprecated
 */
@Deprecated
public class StartMoveWithBendsAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 4347257002374720897L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(ChangeAttributesEdit.class.getName());

    /** Reference to the AbsractEditingTool */
    private SelectionTool selectionTool;

    /**
     * Creates a new StartMoveWithBendAction
     * 
     * @param selectionTool
     *            The given AbstractEditingTool
     */
    public StartMoveWithBendsAction(SelectionTool selectionTool) {
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
        Point pos = e.getPosition();

        if (pos == null) {
            logger.finer("Can't operate without position!");

            return;
        }

        GraphElement mouseOverElement = selectionTool.getTopGraphElement(pos);

        // checks if tool isn't in default mode
        if (!selectionTool.isDefaultMode())
            return;

        selectionTool.setMoveNodes(null);
        selectionTool.setMoveEdges(null);

        List<GraphElement> markedElements = selectionTool.getSelection()
                .getElements();
        List<Node> markedNodes = selectionTool.getSelection().getNodes();

        selectionTool.setAttributesMap(new HashMap<Attribute, Object>());

        // mouse over a node
        if (mouseOverElement instanceof Node) {
            // node under mouse-position is marked
            if (markedElements.contains(mouseOverElement)) {
                // the actual node where's the mouse over
                Node actNode = (Node) mouseOverElement;

                // list with the edges which have to be moved
                List<Edge> markedEdges = selectionTool.getSelection()
                        .getEdges();
                List<Edge> moveEdges = new LinkedList<Edge>(markedEdges);

                Iterator<Edge> edgesIt = selectionTool.getGraph()
                        .getEdgesIterator();

                while (edgesIt.hasNext()) {
                    Edge checkEdge = edgesIt.next();

                    // checkEdge is connection between two marked nodes
                    if (markedNodes.contains(checkEdge.getSource())
                            && markedNodes.contains(checkEdge.getTarget())) {
                        if (!moveEdges.contains(checkEdge)) {
                            moveEdges.add(checkEdge);
                        }
                    }
                }

                // for undo/redo
                setAttributesMap(markedNodes, moveEdges);

                // change mode to move marked elements mode
                selectionTool.setModeToMoveMarkedElements();

                CoordinateAttribute cooAttActNode = selectionTool
                        .getCooAttNode(actNode);

                // sets the actual move element
                selectionTool.setMoveElement(cooAttActNode);

                // sets the elements which has to be moved
                selectionTool.setMoveNodes(markedNodes);
                selectionTool.setMoveEdges(moveEdges);
            }

            // node under mouse-position is NOT marked
            else {
                Node actNode = (Node) mouseOverElement;

                // for undo/redo
                setAttributesMap(actNode);

                // sets mode to move only node mode
                selectionTool.setModeToMoveOnlyNode();

                // sets the graphelement to move to actNode
                selectionTool.setGraphElementToMove(actNode);

                CoordinateAttribute cooAttActNode = selectionTool
                        .getCooAttNode(actNode);
                selectionTool.setMoveElement(cooAttActNode);

                List<Node> moveNodes = new LinkedList<Node>();
                moveNodes.add(actNode);
                selectionTool.setMoveNodes(moveNodes);
                selectionTool.setMoveEdges(new LinkedList<Edge>());
            }
        }

        // mouse not over a node
        else {
            Edge edge = null;
            boolean exit = false;
            boolean foundBend = false;
            boolean selectionContainsEdge = false;
            Edge theEdge = null;
            List<Edge> moveEdges = new LinkedList<Edge>();
            List<Node> moveNodes = new LinkedList<Node>();

            // Iterator edgesIterator = selectionTool.getSelection().getEdges()
            // .iterator();
            Iterator<Edge> edgesIterator = selectionTool.getGraph()
                    .getEdgesIterator();

            while (edgesIterator.hasNext() && !exit) {
                edge = edgesIterator.next();

                Map<String, Attribute> bends = selectionTool
                        .getBendsOfEdge(edge);

                Iterator<String> checkBends = bends.keySet().iterator();

                while (checkBends.hasNext() && !exit) {
                    CoordinateAttribute moveBend = (CoordinateAttribute) bends
                            .get(checkBends.next());

                    // checks if the mouse position is "near" the actual bend
                    if ((pos.x <= (moveBend.getX() + 6))
                            && (pos.x >= (moveBend.getX() - 6))
                            && (pos.y >= (moveBend.getY() - 6))
                            && (pos.y <= (moveBend.getY() + 6))) {
                        // edge in selection
                        if (selectionTool.getSelection().contains(edge)) {
                            selectionContainsEdge = true;
                        }

                        theEdge = edge;
                        selectionTool.setMoveElement(moveBend);
                        selectionTool.setGraphElementToMove(edge);

                        moveEdges.add(edge);

                        foundBend = true;
                        exit = true;
                    }
                }
            }

            if (selectionContainsEdge) {
                selectionTool.setMoveEdges(selectionTool.getSelection()
                        .getEdges());
                selectionTool.setMoveNodes(selectionTool.getSelection()
                        .getNodes());
            } else {
                selectionTool.setMoveEdges(moveEdges);
                selectionTool.setMoveNodes(moveNodes);

                if (theEdge != null) {
                    selectionTool.unmarkAll();
                    selectionTool.mark(edge);
                }
            }

            if (foundBend && (edge != null)) {
                selectionTool.setModeToMoveMarkedElements();
                setAttributesMap(edge);
            } else {
                selectionTool.setModeToDefault();
                selectionTool
                        .setAttributesMap(new HashMap<Attribute, Object>());
            }
        }

        selectionTool.setOldPosition(pos);
    }

    /**
     * Adds the attributes of the node which has to be moved into a map for
     * undo/redo
     * 
     * @param node
     *            The node, which has to be moved
     */
    private void setAttributesMap(Node node) {
        List<Node> markedNodes = new LinkedList<Node>();
        List<Edge> markedEdges = new LinkedList<Edge>();
        markedNodes.add(node);
        setAttributesMap(markedNodes, markedEdges);
    }

    /**
     * Adds the attributes of the edge which has to be moved into a map for
     * undo/redo
     * 
     * @param edge
     *            The edge, which has to be moved
     */
    private void setAttributesMap(Edge edge) {
        List<Node> markedNodes = new LinkedList<Node>();
        List<Edge> markedEdges = new LinkedList<Edge>();
        markedEdges.add(edge);
        setAttributesMap(markedNodes, markedEdges);
    }

    /**
     * Method that sets the Map attributesMap of the AbstractEditingTool for the
     * undo/redo(!).
     * 
     * @param moveNodes
     *            The selected nodes
     * @param moveEdges
     *            The selected edges
     */
    private void setAttributesMap(List<Node> moveNodes, List<Edge> moveEdges) {
        for (Node selectedNode : moveNodes) {
            CollectionAttribute attributesSelectedNode = selectedNode
                    .getAttributes();
            NodeGraphicAttribute selectedNodeAttributes = (NodeGraphicAttribute) attributesSelectedNode
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            CoordinateAttribute selectedNodecoordinateAttribute = selectedNodeAttributes
                    .getCoordinate();
            CoordinateAttribute coordinateAttributeCopy = (CoordinateAttribute) selectedNodecoordinateAttribute
                    .copy();
            Object coordinateAttributeCopyValue = coordinateAttributeCopy
                    .getValue();

            // adds the attributesMap (AbstractEditingTool) the actual node
            // (undo/redo!)
            selectionTool.addAttributesToMap(selectedNodecoordinateAttribute,
                    coordinateAttributeCopyValue);
        }

        for (Edge selEdge : moveEdges) {
            Map<String, Attribute> bends = selectionTool
                    .getBendsOfEdge(selEdge);
            Iterator<String> checkBendsIterator = bends.keySet().iterator();

            while (checkBendsIterator.hasNext()) {
                CoordinateAttribute moveBend = (CoordinateAttribute) bends
                        .get(checkBendsIterator.next());
                CoordinateAttribute moveBendCopy = (CoordinateAttribute) moveBend
                        .copy();
                Object moveBendCopyValue = moveBendCopy.getValue();

                // adds the attributesMap (AbstractEditingTool) the actual bend
                // (undo/redo!)
                selectionTool.addAttributesToMap(moveBend, moveBendCopyValue);
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
