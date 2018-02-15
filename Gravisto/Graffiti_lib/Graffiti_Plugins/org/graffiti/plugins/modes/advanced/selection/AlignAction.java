// =============================================================================
//
//   AlignAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AlignAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.selection;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.swing.undo.UndoableEditSupport;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * To align a marked node to another node.
 * 
 * @author MH
 * @deprecated
 */
@Deprecated
public class AlignAction extends AbstractFunctionAction {
    /**
     * 
     */
    private static final long serialVersionUID = 208342879054650956L;

    /** Constant for top */
    private static final String TOP = "top";

    /** Constant for center horizontal */
    private static final String CENTER_HORIZONTAL = "center-horizontal";

    /** Constant for bottom */
    private static final String BOTTOM = "bottom";

    /** Constant for right */
    private static final String RIGHT = "right";

    /** Constant for center vertical */
    private static final String CENTER_VERTICAL = "center-vertical";

    /** Constant for left */
    private static final String LEFT = "left";

    /** Constant for center */
    private static final String CENTER = "center";

    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(AlignAction.class
            .getName());

    /** Reference to the SelectionTool */
    protected SelectionTool selectionTool;

    /**
     * Constructs a new AlignAction.
     * 
     * @param selectionTool
     *            the SelectionTool - instance controlling the function
     */
    public AlignAction(SelectionTool selectionTool) {
        this.selectionTool = selectionTool;
    }

    /**
     * Returns the parameters which are valid.
     * 
     * @return Map with the valid parameters
     */
    @Override
    public Map<String, Set<Object>> getValidParameters() {
        return construct1To7ParamMap("alignDirection", "top", "bottom", "left",
                "right", "center-horizontal", "center-vertical", "center");
    }

    /**
     * The action of this class.
     * 
     * @param e
     *            The given FunctinoActionEvent
     */
    @Override
    public void actionPerformed(FunctionActionEvent e) {
        // actual position of mouse
        Point position = e.getPosition();
        Node currNode = null;

        if (position == null) {
            logger.finer("Can't operate without position!");

            return;
        }

        if (selectionTool.getTopNode(position) == null) {
            logger.finer("Can't operate without a node!");

            return;
        } else {
            // Node where mouse is over
            currNode = selectionTool.getTopNode(position);

            // nodes which are selected
            List<Node> nodes = selectionTool.getSelection().getNodes();

            // 0 nodes selected
            if (nodes.size() == 0) {
                logger.finer("Can't operate without marked nodes!");

                return;
            }

            // more than 1 node selected
            else if (nodes.size() >= 1) {
                // for undo/redo(!)
                ChangeAttributesEdit edit;
                selectionTool
                        .setAttributesMap(new HashMap<Attribute, Object>());

                Map<GraphElement, GraphElement> geMap = selectionTool
                        .getGEMap();
                UndoableEditSupport undoSupport = selectionTool
                        .getUndoSupport();

                // align the marked nodes

                for (Node markedNode : nodes) {
                    CollectionAttribute attributesMarkedNode = markedNode
                            .getAttributes();
                    NodeGraphicAttribute markedNodeGraphicAttribute = (NodeGraphicAttribute) attributesMarkedNode
                            .getAttribute(GraphicAttributeConstants.GRAPHICS);

                    CoordinateAttribute oldCoordinateAttribute = markedNodeGraphicAttribute
                            .getCoordinate();

                    CoordinateAttribute oldCoordinateAttributeCopy = (CoordinateAttribute) oldCoordinateAttribute
                            .copy();
                    Object oldCoordinateAttributeCopyValue = oldCoordinateAttributeCopy
                            .getValue();

                    // add the actual nodes attributes to the attributes
                    // map for undo/redo
                    selectionTool.addAttributesToMap(oldCoordinateAttribute,
                            oldCoordinateAttributeCopyValue);

                    // aligns the marked node
                    alignNodeMarkedNode(markedNode, currNode);
                }

                // for undo/redo
                edit = new ChangeAttributesEdit(selectionTool
                        .getAttributesMap(), geMap);
                undoSupport.postEdit(edit);
            }
        }
    }

    /**
     * Returns the distance, the markedNode has to be moved (only distance, no
     * direction)
     * 
     * @param currNodeGraphicAttribute
     *            The node which does not move
     * @param markedNodeGraphicAttribute
     *            The node, which has be aligned
     * 
     * @return The distance the markedNode has to be moved (only distance, no
     *         direction)
     */
    private double getDistance(NodeGraphicAttribute currNodeGraphicAttribute,
            NodeGraphicAttribute markedNodeGraphicAttribute) {
        double currNodeCoordinate;
        double currNodeHeight;
        double markedNodeHeight;

        // top or bottom
        if (alignDirection().equals(TOP) || alignDirection().equals(BOTTOM)) {
            currNodeCoordinate = currNodeGraphicAttribute.getCoordinate()
                    .getY();
            currNodeHeight = ((currNodeGraphicAttribute.getDimension()
                    .getHeight() / 2));
            markedNodeHeight = ((markedNodeGraphicAttribute.getDimension()
                    .getHeight() / 2));

            // top
            if (alignDirection().equals(TOP))
                return currNodeCoordinate - currNodeHeight + markedNodeHeight;
            else
                return (currNodeCoordinate + currNodeHeight) - markedNodeHeight;
        }

        // left or right
        else if (alignDirection().equals(RIGHT)
                || alignDirection().equals(LEFT)) {
            currNodeCoordinate = currNodeGraphicAttribute.getCoordinate()
                    .getX();
            currNodeHeight = ((currNodeGraphicAttribute.getDimension()
                    .getWidth() / 2));
            markedNodeHeight = ((markedNodeGraphicAttribute.getDimension()
                    .getWidth() / 2));

            if (alignDirection().equals(RIGHT))
                return (currNodeCoordinate + currNodeHeight) - markedNodeHeight;
            else
                return currNodeCoordinate - currNodeHeight + markedNodeHeight;
        }

        // center horizontal
        else if (alignDirection().equals(CENTER_HORIZONTAL))
            return (currNodeGraphicAttribute.getCoordinate().getX());
        else if (alignDirection().equals(CENTER_VERTICAL))
            return (currNodeGraphicAttribute.getCoordinate().getY());
        else
            return -1;
    }

    /**
     * Aligns bottom.
     * 
     * @param currNodeGraphicAttribute
     *            of the currentNode
     * @param markedNodeGraphicAttribute
     *            of the markedNode
     * @param distance
     *            the distance the node has to be moved
     */
    private void alignBottom(NodeGraphicAttribute currNodeGraphicAttribute,
            NodeGraphicAttribute markedNodeGraphicAttribute, double distance) {
        double currNodeDistance = currNodeGraphicAttribute.getCoordinate()
                .getY();

        double currNodeBottomDistance = currNodeDistance
                + (currNodeGraphicAttribute.getDimension().getHeight() / 2);

        double markedNodeHeight = markedNodeGraphicAttribute.getDimension()
                .getHeight();

        if (markedNodeHeight > currNodeBottomDistance) {
            logger.info("Can't move more up, because of the border!");
            markedNodeGraphicAttribute.getCoordinate().setY(
                    markedNodeHeight / 2);
        } else {
            markedNodeGraphicAttribute.getCoordinate().setY(distance);
        }
    }

    /**
     * Calls the methods alignCenterHorizontal(..) and alignCenterVertical(..)
     * 
     * @param currNodeGraphicAttribute
     *            of the currentNode
     * @param markedNodeGraphicAttribute
     *            of the markedNode
     */
    private void alignCenter(NodeGraphicAttribute currNodeGraphicAttribute,
            NodeGraphicAttribute markedNodeGraphicAttribute) {
        alignCenterHorizontal(currNodeGraphicAttribute,
                markedNodeGraphicAttribute);
        alignCenterVertical(currNodeGraphicAttribute,
                markedNodeGraphicAttribute);
    }

    /**
     * Align center horizontal
     * 
     * @param currNodeGraphicAttribute
     *            of the currentNode
     * @param markedNodeGraphicAttribute
     *            of the markedNode
     */
    private void alignCenterHorizontal(
            NodeGraphicAttribute currNodeGraphicAttribute,
            NodeGraphicAttribute markedNodeGraphicAttribute) {
        // x-coordinate of the currentNode
        double x = currNodeGraphicAttribute.getCoordinate().getX();

        double markedNodeWidth = markedNodeGraphicAttribute.getDimension()
                .getWidth();

        if ((markedNodeWidth / 2) > x) {
            logger.info("Can't move more up, because of the border!");
            x = markedNodeWidth / 2;
        }

        // sets the new coordinates of the markedNode
        markedNodeGraphicAttribute.getCoordinate().setX(x);
    }

    /**
     * Align center vertical.
     * 
     * @param currNodeGraphicAttribute
     *            of the currentNode
     * @param markedNodeGraphicAttribute
     *            of the markedNode
     */
    private void alignCenterVertical(
            NodeGraphicAttribute currNodeGraphicAttribute,
            NodeGraphicAttribute markedNodeGraphicAttribute) {
        double y = currNodeGraphicAttribute.getCoordinate().getY();

        double markedNodeHeight = markedNodeGraphicAttribute.getDimension()
                .getHeight();

        if ((markedNodeHeight / 2) > y) {
            logger.info("Can't move more up, because of the border!");
            y = markedNodeHeight / 2;
        }

        markedNodeGraphicAttribute.getCoordinate().setY(y);
    }

    /**
     * Returns the direction of the alignment.
     * 
     * @return The direction of the alignment
     */
    private String alignDirection() {
        Object value = this.getValue("alignDirection");

        if (value == null) {
            value = "top";
        }

        return value.toString();
    }

    /**
     * Aligns the markedNode to the currNode.
     * 
     * @param markedNode
     *            The node, which has to be aligned
     * @param currNode
     *            The node, which hasn't to be moved
     */
    private void alignNodeMarkedNode(Node markedNode, Node currNode) {
        CollectionAttribute attributesCurrNode = currNode.getAttributes();
        NodeGraphicAttribute currNodeGraphicAttribute = (NodeGraphicAttribute) attributesCurrNode
                .getAttribute(GraphicAttributeConstants.GRAPHICS);

        CollectionAttribute attributesMarkedNode = markedNode.getAttributes();
        NodeGraphicAttribute markedNodeGraphicAttribute = (NodeGraphicAttribute) attributesMarkedNode
                .getAttribute(GraphicAttributeConstants.GRAPHICS);

        // distance to the aligned boarder
        double distance = getDistance(currNodeGraphicAttribute,
                markedNodeGraphicAttribute);

        // center
        if (alignDirection().equals(CENTER)) {
            alignCenter(currNodeGraphicAttribute, markedNodeGraphicAttribute);
        }

        // bottom
        else if (alignDirection().equals(BOTTOM)) {
            alignBottom(currNodeGraphicAttribute, markedNodeGraphicAttribute,
                    distance);
        }

        // top
        else if (alignDirection().equals(TOP)) {
            markedNodeGraphicAttribute.getCoordinate().setY(distance);
        }

        // center vertical
        else if (alignDirection().equals(CENTER_VERTICAL)) {
            alignCenterVertical(currNodeGraphicAttribute,
                    markedNodeGraphicAttribute);
        }

        // right
        else if (alignDirection().equals(RIGHT)) {
            alignRight(currNodeGraphicAttribute, markedNodeGraphicAttribute,
                    distance);
        }

        // center horizontal
        else if (alignDirection().equals(CENTER_HORIZONTAL)) {
            alignCenterHorizontal(currNodeGraphicAttribute,
                    markedNodeGraphicAttribute);
        }

        // left
        else {
            markedNodeGraphicAttribute.getCoordinate().setX(distance);
        }
    }

    /**
     * Align right.
     * 
     * @param currNodeGraphicAttribute
     *            of the currentNode
     * @param markedNodeGraphicAttribute
     *            of the markedNode
     * @param distance
     *            The distance the markedNode has to be moved
     */
    private void alignRight(NodeGraphicAttribute currNodeGraphicAttribute,
            NodeGraphicAttribute markedNodeGraphicAttribute, double distance) {
        double currNodeDistance = currNodeGraphicAttribute.getCoordinate()
                .getX();

        double currNodeLeftDistance = currNodeDistance
                + (currNodeGraphicAttribute.getDimension().getWidth() / 2);

        double markedNodeWidth = markedNodeGraphicAttribute.getDimension()
                .getWidth();

        if (markedNodeWidth > currNodeLeftDistance) {
            logger.info("Can't move more left, because of the border!");
            markedNodeGraphicAttribute.getCoordinate()
                    .setX(markedNodeWidth / 2);
        } else {
            markedNodeGraphicAttribute.getCoordinate().setX(distance);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
