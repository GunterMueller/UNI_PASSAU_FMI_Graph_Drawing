// =============================================================================
//
//   StartRotateAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StartRotateAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.rotate;

import java.awt.Cursor;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.graffiti.attributes.Attribute;
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
 * Prepares the elements for rotation.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2008-12-31 05:02:05 +0100 (Mi, 31 Dez 2008)
 *          $
 * @deprecated
 */
@Deprecated
public class StartRotateAction extends AbstractFunctionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 471644563481635386L;

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(ChangeAttributesEdit.class.getName());

    /** Reference to the RotationTool */
    private RotationTool rotationTool;

    /**
     * Creates a new StartRotateAction.
     * 
     * @param rotationTool
     *            the given RotationTool
     */
    public StartRotateAction(RotationTool rotationTool) {
        this.rotationTool = rotationTool;
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

        GraphElement mouseOverElement = rotationTool.getTopGraphElement(pos);

        // check if tool is not in default mode
        if (!rotationTool.isDefaultMode())
            return;

        // for undo / redo
        rotationTool.setAttributesMap(new HashMap<Attribute, Object>());

        // check the top element
        if (rotationTool.getTopGraphElement(pos) == rotationTool
                .getReferenceNode()) {
            prepareReferenceNodeForMoving();
        } else if (rotationTool.getTopGraphElement(pos) instanceof Edge) {
            prepareBendForRotating(mouseOverElement, pos);
        } else if (rotationTool.getTopGraphElement(pos) instanceof Node) {
            prepareElementsForRotating((Node) mouseOverElement);
        }

    }

    /**
     * Prepares a bend for rotation.
     * 
     * @param mouseOverElement
     *            The edge under the mouse.
     * @param pos
     *            The mouse position.
     */
    private void prepareBendForRotating(GraphElement mouseOverElement, Point pos) {
        Edge edge = (Edge) mouseOverElement;
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        Collection<Attribute> bendsColl = ega.getBends().getCollection()
                .values();
        CoordinateAttribute foundBend = null;

        for (Attribute attr : bendsColl) {
            CoordinateAttribute bend = (CoordinateAttribute) attr;
            // checks if the mouse position is "near" the current bend
            if ((pos.x <= (bend.getX() + 6)) && (pos.x >= (bend.getX() - 6))
                    && (pos.y >= (bend.getY() - 6))
                    && (pos.y <= (bend.getY() + 6))) {

                foundBend = bend;
            }
        }
        // a bend was found where mouse is over
        if (foundBend != null) {
            rotationTool.setModeToRotateOnlyBend();
            rotationTool.setRotateElementCurrentCA(foundBend);
            rotationTool.setRotateElementInitialCA(foundBend);
            rotationTool.addAttributesToMap(foundBend, rotationTool
                    .getRotateElementInitialCA().getValue());
        }

        // no bend near mouse position was found
        else {
            rotationTool.setModeToDefault();
        }
    }

    /**
     * Prepares the graph elements for rotation.
     * 
     * @param topNode
     *            The node under the mouse.
     */
    private void prepareElementsForRotating(Node topNode) {
        // change mode to rotate marked elements
        rotationTool.setModeToRotateMarkedElements();

        List<Node> moveNodes = rotationTool.getSelection().getNodes();
        List<Edge> moveEdges = rotationTool.getSelection().getEdges();
        moveNodes.remove(rotationTool.getReferenceNode());

        CoordinateAttribute[] nodeCoords = new CoordinateAttribute[moveNodes
                .size()];
        CoordinateAttribute[] initialNodeCoords = new CoordinateAttribute[moveNodes
                .size()];
        DimensionAttribute[] nodeDims = new DimensionAttribute[moveNodes.size()];
        LinkedList<CoordinateAttribute> bendCoords = new LinkedList<CoordinateAttribute>();
        ArrayList<CoordinateAttribute> initialBendCoords = new ArrayList<CoordinateAttribute>();

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
            rotationTool.addAttributesToMap(nodeCoords[i],
                    coordinateAttributeCopyValue);

            initialNodeCoords[i] = coordinateAttributeCopy;
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
                rotationTool.addAttributesToMap(ca, moveBendCopyValue);

                initialBendCoords.add(moveBendCopy);
            }
        }

        NodeGraphicAttribute nga = (NodeGraphicAttribute) topNode
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        rotationTool.setRotateElementInitialCA(nga.getCoordinate());

        rotationTool.setNodeCoords(nodeCoords);
        rotationTool.setInitialNodeCoords(initialNodeCoords);
        rotationTool.setBendCoords(bendCoords);
        rotationTool.setInitialBendCoords(initialBendCoords);
        rotationTool.setNodeDims(nodeDims);

    }

    /**
     * Prepares the reference node for moving.
     */
    private void prepareReferenceNodeForMoving() {

        // change mode to move marked elements mode
        rotationTool.setModeToMoveReferenceNode();

        Cursor c = new Cursor(Cursor.MOVE_CURSOR);
        rotationTool.getActiveJComponent().setCursor(c);

        CoordinateAttribute ca = rotationTool.getReferenceNodeCoords();
        rotationTool
                .setOldPosition(new Point((int) ca.getX(), (int) ca.getY()));

        CoordinateAttribute moveBendCopy = (CoordinateAttribute) ca.copy();
        Object moveBendCopyValue = moveBendCopy.getValue();
        rotationTool.addAttributesToMap(ca, moveBendCopyValue);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
