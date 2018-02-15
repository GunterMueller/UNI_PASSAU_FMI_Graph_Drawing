// =============================================================================
//
//   MoveAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MoveAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.selection;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.undo.UndoableEditSupport;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.modes.advanced.AbstractEditingTool;
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
public class MoveAction extends AbstractFunctionAction {
    /**
     * 
     */
    private static final long serialVersionUID = 2236532511654524423L;

    /** Constant for a very fast speed movement (very large steps) */
    private static final double VERY_LARGE_STEP = 20;

    /** Constant for a fast speed movement (large steps) */
    private static final double LARGE_STEP = 10;

    /** Reference to the SelectionTool */
    private SelectionTool selectionTool;

    /**
     * Creates a new OldMoveAction.
     * 
     * @param selectionTool
     *            The given SelectionTool
     */
    public MoveAction(SelectionTool selectionTool) {
        this.selectionTool = selectionTool;
    }

    /**
     * Returns the parameters which are valid.
     * 
     * @return Map with the valid parameters
     */
    @Override
    public Map<String, Set<Object>> getValidParameters() {
        return construct2To43ParamMap("direction", "up", "down", "left",
                "right", "moveSteps", "smallSteps", "largeSteps",
                "veryLargeSteps");
    }

    /**
     * The action of this class.
     * 
     * @param e
     *            The given FunctionActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        // the selected nodes
        List<Node> markedNodes = selectionTool.getSelection().getNodes();

        // the selected edges
        List<Edge> markedEdges = selectionTool.getSelection().getEdges();

        // sets the size of the move steps
        double moveStep = 1;

        String steps = largeSteps();
        if ("largeSteps".equals(steps)) {
            moveStep = LARGE_STEP;
        } else if ("veryLargeSteps".equals(steps)) {
            moveStep = VERY_LARGE_STEP;
        }

        // for undo/redo(!)
        ChangeAttributesEdit edit;
        selectionTool.setAttributesMap(new HashMap<Attribute, Object>());

        Map<GraphElement, GraphElement> geMap = selectionTool.getGEMap();
        UndoableEditSupport undoSupport = selectionTool.getUndoSupport();

        for (Node selectedNode : markedNodes) {
            CoordinateAttribute cooAtt = selectionTool
                    .getCooAttNode(selectedNode);

            CoordinateAttribute cooAttCopy = (CoordinateAttribute) cooAtt
                    .copy();
            Object cooAttCopyValue = cooAttCopy.getValue();
            selectionTool.addAttributesToMap(cooAtt, cooAttCopyValue);
        }

        for (Edge selEdge : markedEdges) {
            CollectionAttribute attributesSelEdge = selEdge.getAttributes();
            EdgeGraphicAttribute selEdgeAttributes = (EdgeGraphicAttribute) attributesSelEdge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            Map<String, Attribute> bends = selEdgeAttributes.getBends()
                    .getCollection();

            for (String string : bends.keySet()) {
                CoordinateAttribute moveBend = (CoordinateAttribute) bends
                        .get(string);

                CoordinateAttribute moveBendCopy = (CoordinateAttribute) moveBend
                        .copy();
                Object moveBendCopyValue = moveBendCopy.getValue();

                selectionTool.addAttributesToMap(moveBend, moveBendCopyValue);
            }
        }

        // the maximal possible distance to move to left border and to top
        // border
        double smallestYDistance = getSmallestYDistance(markedNodes,
                markedEdges);
        double smallestXDistance = getSmallestXDistance(markedNodes,
                markedEdges);

        // check if movement bigger than smallestYDistance
        if ((smallestYDistance < moveStep)
                && direction().equals(AbstractEditingTool.UP)) {
            moveStep = smallestYDistance;
        }

        // check if movement bigger than smallestXDistance
        if ((smallestXDistance < moveStep)
                && direction().equals(AbstractEditingTool.LEFT)) {
            moveStep = smallestXDistance;
        }

        for (Node node : markedNodes) {
            CollectionAttribute attributesNode = node.getAttributes();
            NodeGraphicAttribute nodeGraphicAttribute = (NodeGraphicAttribute) attributesNode
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);

            // top
            if (direction().equals(AbstractEditingTool.UP)) {
                nodeGraphicAttribute.getCoordinate().setY(
                        nodeGraphicAttribute.getCoordinate().getY() - moveStep);

                // down
            } else if (direction().equals(AbstractEditingTool.DOWN)) {
                nodeGraphicAttribute.getCoordinate().setY(
                        nodeGraphicAttribute.getCoordinate().getY() + moveStep);

                // right
            } else if (direction().equals(AbstractEditingTool.RIGHT)) {
                nodeGraphicAttribute.getCoordinate().setX(
                        nodeGraphicAttribute.getCoordinate().getX() + moveStep);

                // left
            } else {
                nodeGraphicAttribute.getCoordinate().setX(
                        nodeGraphicAttribute.getCoordinate().getX() - moveStep);
            }
        }

        for (Edge edge : markedEdges) {
            // the bends of the edge edge
            Map<String, Attribute> bends = selectionTool.getBendsOfEdge(edge);

            // moves the bends
            for (String s : bends.keySet()) {
                CoordinateAttribute moveBend = (CoordinateAttribute) bends
                        .get(s);

                // up
                if (direction().equals(AbstractEditingTool.UP)) {
                    moveBend.setY(moveBend.getY() - moveStep);

                    // down
                } else if (direction().equals(AbstractEditingTool.DOWN)) {
                    moveBend.setY(moveBend.getY() + moveStep);

                    // right
                } else if (direction().equals(AbstractEditingTool.RIGHT)) {
                    moveBend.setX(moveBend.getX() + moveStep);

                    // left
                } else if (direction().equals(AbstractEditingTool.LEFT)) {
                    moveBend.setX(moveBend.getX() - moveStep);
                }
            }
        }

        // for undo/redo
        edit = new ChangeAttributesEdit(selectionTool.getAttributesMap(), geMap);
        undoSupport.postEdit(edit);
    }

    /**
     * Returns the smallest distance of the given graph elements to the left
     * border.
     * 
     * @param moveNodes
     *            the given nodes
     * @param moveEdges
     *            the given edges
     * 
     * @return smallest distance to top border
     */
    private double getSmallestXDistance(List<Node> moveNodes,
            List<Edge> moveEdges) {
        // initialize
        double smallestXDistance = -1;

        // calculates the smallest distance to left border (nodes)
        for (Node currentMoveNode : moveNodes) {
            CollectionAttribute attributesCurrentMoveNode = currentMoveNode
                    .getAttributes();
            NodeGraphicAttribute currentMoveNodeGraphicAttribute = (NodeGraphicAttribute) attributesCurrentMoveNode
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);

            double currentXDistance = currentMoveNodeGraphicAttribute
                    .getCoordinate().getX()
                    - (currentMoveNodeGraphicAttribute.getDimension()
                            .getWidth() / 2);

            if (smallestXDistance == -1) {
                smallestXDistance = currentXDistance;
            } else {
                if (currentXDistance < smallestXDistance) {
                    smallestXDistance = currentXDistance;
                }
            }
        }

        // calculates the smallest distance of the bends (including smallest
        // distance of the nodes

        for (Edge currentMoveEdge : moveEdges) {
            Map<String, Attribute> bends = selectionTool
                    .getBendsOfEdge(currentMoveEdge);

            for (String s : bends.keySet()) {
                CoordinateAttribute moveBend = (CoordinateAttribute) bends
                        .get(s);

                double currentXDistance = moveBend.getX();

                if (smallestXDistance == -1) {
                    smallestXDistance = currentXDistance;
                } else {
                    if (currentXDistance < smallestXDistance) {
                        smallestXDistance = currentXDistance;
                    }
                }
            }
        }

        return smallestXDistance;
    }

    /**
     * Returns the smallest distance of the given graph elements to the top
     * border.
     * 
     * @param moveNodes
     *            the given nodes
     * @param moveEdges
     *            the given edges
     * 
     * @return smallest distance to top border
     */
    private double getSmallestYDistance(List<Node> moveNodes,
            List<Edge> moveEdges) {
        // initialize
        double smallestYDistance = -1;

        // calculates the smallest distance (beginning with nodes)

        for (Node currentMoveNode : moveNodes) {
            CollectionAttribute attributesCurrentMoveNode = currentMoveNode
                    .getAttributes();
            NodeGraphicAttribute currentMoveNodeGraphicAttribute = (NodeGraphicAttribute) attributesCurrentMoveNode
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);

            double currentYDistance = currentMoveNodeGraphicAttribute
                    .getCoordinate().getY()
                    - (currentMoveNodeGraphicAttribute.getDimension()
                            .getHeight() / 2);

            if (smallestYDistance == -1) {
                smallestYDistance = currentYDistance;
            } else {
                if (currentYDistance < smallestYDistance) {
                    smallestYDistance = currentYDistance;
                }
            }
        }

        for (Edge currentMoveEdge : moveEdges) {
            // bends of currentMoveEdge
            Map<String, Attribute> bends = selectionTool
                    .getBendsOfEdge(currentMoveEdge);

            for (String s : bends.keySet()) {
                CoordinateAttribute moveBend = (CoordinateAttribute) bends
                        .get(s);

                double currentYDistance = moveBend.getY();

                if (smallestYDistance == -1) {
                    smallestYDistance = currentYDistance;
                } else {
                    if (currentYDistance < smallestYDistance) {
                        smallestYDistance = currentYDistance;
                    }
                }
            }
        }

        return smallestYDistance;
    }

    /**
     * Returns the direction.
     * 
     * @return direction
     */
    private String direction() {
        Object value = this.getValue("direction");

        return value.toString();
    }

    /**
     * Returns the String which moveStep is set.
     * 
     * @return Value of moveStep.
     */
    private String largeSteps() {
        Object value = this.getValue("moveSteps");
        return (String) value;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
