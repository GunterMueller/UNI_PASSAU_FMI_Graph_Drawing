// =============================================================================
//
//   UpdateRotateAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: UpdateRotateAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced.rotate;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.undo.UndoableEditSupport;

import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.plugins.modes.advanced.AbstractFunctionAction;
import org.graffiti.plugins.modes.advanced.FunctionActionEvent;
import org.graffiti.undo.ChangeAttributesEdit;

/**
 * Updates the rotation while the mouse button is pressed.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2008-12-31 05:02:05 +0100 (Mi, 31 Dez 2008)
 *          $
 * @deprecated
 */
@Deprecated
public class UpdateRotateAction extends AbstractFunctionAction {
    /**
     * 
     */
    private static final long serialVersionUID = 6898404097100741599L;
    /** Reference to the AbstractEditingTool */
    private RotationTool rotationTool;

    /**
     * Creates a new UpdateMoveAction
     * 
     * @param rotationTool
     *            The given RotationTool
     */
    public UpdateRotateAction(RotationTool rotationTool) {
        this.rotationTool = rotationTool;
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

        // check the current mode
        if (rotationTool.getMode() == RotationTool.MOVE_REFERENCE_NODE) {
            moveReferenceNode(newPosition);
        } else if (rotationTool.getMode() == RotationTool.ROTATE_ONLY_BEND) {
            rotateTheBend(newPosition);
        } else if (rotationTool.getMode() == RotationTool.ROTATE_MARKED_ELEMENTS) {
            rotateMarkedElements(newPosition);
        } else {
            rotationTool.setOldPosition(newPosition);
            return;
        }
        rotationTool.setOldPosition(newPosition);
    }

    /**
     * Rotates the marked graph elements.
     * 
     * @param mousePos
     *            The current mouse position.
     */
    private void rotateMarkedElements(Point mousePos) {
        // for undo-redo
        if (!rotationTool.isDuring_update_rotation()) {
            ChangeAttributesEdit edit;
            Map<GraphElement, GraphElement> geMap = rotationTool.getGEMap();
            UndoableEditSupport undoSupport = rotationTool.getUndoSupport();
            edit = new ChangeAttributesEdit(rotationTool.getAttributesMap(),
                    geMap);

            // sends the information (the old values) to the undo/redo -
            // support
            undoSupport.postEdit(edit);
            rotationTool.setDuring_update_rotation(true);
        }

        CoordinateAttribute[] nodesToRotate = rotationTool.getNodeCoords();
        CoordinateAttribute[] initialNodeCoords = rotationTool
                .getInitialNodeCoords();

        double angle = computeRotationAngle(mousePos, rotationTool
                .getRotateElementInitialCA());

        boolean clockwise = moveClockwise(mousePos, rotationTool
                .getRotateElementInitialCA());

        for (int i = 0; i < nodesToRotate.length; i++) {

            Point newPos = null;

            if (clockwise) {
                newPos = computeNewPosition(angle, initialNodeCoords[i]);
            } else {
                newPos = computeNewPosition(-angle, initialNodeCoords[i]);
            }

            nodesToRotate[i].setX(newPos.getX());
            nodesToRotate[i].setY(newPos.getY());

        }

        ArrayList<CoordinateAttribute> initialBendCoords = rotationTool
                .getInitialBendCoords();
        int i = 0;
        for (CoordinateAttribute bendCA : rotationTool.getBendCoords()) {

            Point newPos = null;

            if (clockwise) {
                newPos = computeNewPosition(angle, initialBendCoords.get(i));
            } else {
                newPos = computeNewPosition(-angle, initialBendCoords.get(i));
            }

            bendCA.setX(newPos.getX());
            bendCA.setY(newPos.getY());
            i++;
        }

    }

    /**
     * Rotates the bend.
     * 
     * @param mousePos
     *            The current mouse position.
     */
    private void rotateTheBend(Point mousePos) {
        // for undo-redo
        if (!rotationTool.isDuring_update_bend_rotation()) {
            ChangeAttributesEdit edit;
            Map<GraphElement, GraphElement> geMap = rotationTool.getGEMap();
            UndoableEditSupport undoSupport = rotationTool.getUndoSupport();
            edit = new ChangeAttributesEdit(rotationTool.getAttributesMap(),
                    geMap);

            // sends the information (the old values) to the undo/redo -
            // support
            undoSupport.postEdit(edit);
            rotationTool.setDuring_update_bend_rotation(true);
        }

        CoordinateAttribute bendCA = rotationTool.getRotateElementCurrentCA();
        CoordinateAttribute initialBendCA = rotationTool
                .getRotateElementInitialCA();

        double angle = computeRotationAngle(mousePos, initialBendCA);

        Point newPos = null;

        if (moveClockwise(mousePos, initialBendCA)) {
            newPos = computeNewPosition(angle, initialBendCA);
        } else {
            newPos = computeNewPosition(-angle, initialBendCA);
        }

        bendCA.setX(newPos.getX());
        bendCA.setY(newPos.getY());
    }

    /**
     * Moves the reference node.
     * 
     * @param mousePos
     *            The current mouse position.
     */
    private void moveReferenceNode(Point mousePos) {
        // for undo-redo
        if (!rotationTool.isDuring_update_move()) {
            ChangeAttributesEdit edit;
            Map<GraphElement, GraphElement> geMap = rotationTool.getGEMap();
            UndoableEditSupport undoSupport = rotationTool.getUndoSupport();
            edit = new ChangeAttributesEdit(rotationTool.getAttributesMap(),
                    geMap);

            // sends the information (the old values) to the undo/redo -
            // support
            undoSupport.postEdit(edit);
            rotationTool.setDuring_update_move(true);
        }

        CoordinateAttribute ca = rotationTool.getReferenceNodeCoords();
        Dimension dim = rotationTool.getReferenceNodeDim();
        Point oldPostion = rotationTool.getOldPosition();

        double moveX = oldPostion.getX() - mousePos.getX();
        double moveY = oldPostion.getY() - mousePos.getY();
        if (moveX > ca.getX() - dim.getWidth() / 2d) {
            moveX = ca.getX() - dim.getWidth() / 2d;
        }
        if (moveY > ca.getY() - dim.getHeight() / 2d) {
            moveY = ca.getY() - dim.getHeight() / 2d;
        }
        ca.setX(ca.getX() - moveX);
        ca.setY(ca.getY() - moveY);

        if (mousePos.getX() < 0) {
            mousePos.setLocation(0, mousePos.getY());
        }

        if (mousePos.getY() < 0) {
            mousePos.setLocation(mousePos.getX(), 0);
        }
    }

    /**
     * Computes the new position to which the element has to be moved.
     * 
     * @param angle
     *            The angle.
     * @param initialCoordinate
     *            The initial coordinate.
     * @return The new position of this element.
     */
    private Point computeNewPosition(double angle,
            CoordinateAttribute initialCoordinate) {
        CoordinateAttribute refCA = rotationTool.getReferenceNodeCoords();
        double x = (initialCoordinate.getX() - refCA.getX()) * Math.cos(angle)
                - (initialCoordinate.getY() - refCA.getY()) * Math.sin(angle)
                + refCA.getX();
        double y = (initialCoordinate.getY() - refCA.getY()) * Math.cos(angle)
                + (initialCoordinate.getX() - refCA.getX()) * Math.sin(angle)
                + refCA.getY();

        return new Point((int) x, (int) y);
    }

    /**
     * Computes the rotating angle.
     * 
     * @param mousePos
     *            The current mouse position.
     * @param rotObjectPos
     *            the object that has to be rotated.
     * @return the rotating angle.
     */
    private double computeRotationAngle(Point mousePos,
            CoordinateAttribute rotObjectPos) {
        CoordinateAttribute refCA = rotationTool.getReferenceNodeCoords();
        double xcxa = rotObjectPos.getX() - refCA.getX();
        double xbxa = mousePos.getX() - refCA.getX();
        double ycya = rotObjectPos.getY() - refCA.getY();
        double ybya = mousePos.getY() - refCA.getY();

        double cosAlpha = (xcxa * xbxa + ycya * ybya)
                / (Math.sqrt(Math.pow(xcxa, 2) + Math.pow(ycya, 2)) * Math
                        .sqrt(Math.pow(xbxa, 2) + Math.pow(ybya, 2)));

        return Math.acos(cosAlpha);
    }

    /**
     * Checks, if the rotation is clockwise.
     * 
     * @param mousePos
     *            The current mouse position.
     * @param initialCoordinate
     *            The initial coordinates.
     * @return true, if the rotation is clockwise, false otherwise.
     */
    private boolean moveClockwise(Point mousePos,
            CoordinateAttribute initialCoordinate) {
        CoordinateAttribute refCA = rotationTool.getReferenceNodeCoords();

        double tempW = (mousePos.getY() - refCA.getY())
                * (initialCoordinate.getX() - refCA.getX())
                - (mousePos.getX() - refCA.getX())
                * (initialCoordinate.getY() - refCA.getY());
        if (tempW < 0)
            return false;
        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
