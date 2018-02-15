// =============================================================================
//
//   AddBendToEdgeAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AddBendToEdgeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.modes.advanced;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.editor.MainFrame;
import org.graffiti.editor.actions.SelectGraphElementActionListener;
import org.graffiti.event.ListenerManager;
import org.graffiti.graph.Edge;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;

/**
 * This is a special action for the popup menu. It creates a new bend and adds
 * it to one or more selected edges. If the mouse points on a selected edge, a
 * new bend is added to this edge at the mouse pointed position. If the mouse
 * does not point on an edge, a new bend is added to all selected edges at the
 * mouse pointed position.
 * 
 * @author Marek Piorkowski
 * @version $Revision: 5766 $ $Date: 2008-12-31 05:02:05 +0100 (Mi, 31 Dez 2008)
 *          $
 * @deprecated
 */
@Deprecated
public class AddBendToEdgeAction extends SelectionAction {

    /**
     * 
     */
    private static final long serialVersionUID = -610904464912618992L;

    private AbstractEditingTool editingTool;

    private static final double tolerance = 3.0;

    /**
     * Creates a new AddBendToEdgeAction.
     * 
     * @param mainFrame
     *            The mainframe.
     * @param editingTool
     *            The editing tool.
     */
    public AddBendToEdgeAction(MainFrame mainFrame,
            AbstractEditingTool editingTool) {
        super("Add Bend", mainFrame);
        this.editingTool = editingTool;
        mainFrame.addSelectionListener(new SelectGraphElementActionListener(
                this));
    }

    /*
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * Checks the enabled flag, which tells if the Add Bend button in the popup
     * menu should be enabled
     * 
     * @return the enabled flag
     */
    @Override
    public boolean isEnabled() {
        try {
            return !mainFrame.getActiveEditorSession().getSelectionModel()
                    .getActiveSelection().getEdges().isEmpty();
        } catch (NullPointerException e) {
            return false;
        }
    }

    /*
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        Point mousePos = editingTool.getPositionInfo().getLastPopupPosition();
        Edge topEdge = editingTool.getTopEdge(mousePos);

        // the mouse is not above an edge
        if (topEdge == null) {
            // take all selected edges
            List<Edge> edges = mainFrame.getActiveEditorSession()
                    .getSelectionModel().getActiveSelection().getEdges();
            for (Edge edge : edges) {
                EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                        .getAttribute(GraphicAttributeConstants.GRAPHICS);
                SortedCollectionAttribute bends = ega.getBends();
                Collection<Attribute> bendsColl = ega.getBends()
                        .getCollection().values();
                LinkedList<CoordinateAttribute> coordinateList = new LinkedList<CoordinateAttribute>();
                for (Attribute attr : bendsColl) {
                    coordinateList.add((CoordinateAttribute) attr);
                }
                String newId = GraphicAttributeConstants.BEND + "0";
                // find out the id if the bend
                if (!coordinateList.isEmpty()) {
                    CoordinateAttribute ca = coordinateList.getLast();
                    int maxIdNumber = Integer.parseInt(ca.getId().substring(
                            GraphicAttributeConstants.BEND.length()));
                    newId = GraphicAttributeConstants.BEND + (maxIdNumber + 1);
                }
                // add the new bend at the mouse position
                bends.add(new CoordinateAttribute(newId, mousePos.getX(),
                        mousePos.getY()));

                ListenerManager lm = edge.getGraph().getListenerManager();
                lm.transactionStarted(this);
                ega.setBends(bends);
                if (edge
                        .getAttribute(GraphicAttributeConstants.SHAPE_PATH)
                        .getValue()
                        .equals(
                                GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME)) {
                    // change line shape to "polyline" if current shape
                    // is "straight line"
                    edge.changeString(GraphicAttributeConstants.SHAPE_PATH,
                            GraphicAttributeConstants.POLYLINE_CLASSNAME);
                }
                lm.transactionFinished(this);
            }
        } else {
            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) topEdge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            SortedCollectionAttribute bends = ega.getBends();
            SortedCollectionAttribute newBends = new LinkedHashMapAttribute(
                    bends.getId());
            ;
            Collection<Attribute> bendsColl = ega.getBends().getCollection()
                    .values();

            // source node coordinates + all bends coordinates + target node
            // corrdinates
            CoordinateAttribute[] caArray = new CoordinateAttribute[bendsColl
                    .size() + 2];

            // source node coordinates
            caArray[0] = ((NodeGraphicAttribute) topEdge.getSource()
                    .getAttribute(GraphicAttributeConstants.GRAPHICS))
                    .getCoordinate();

            // target node coordinates
            caArray[caArray.length - 1] = ((NodeGraphicAttribute) topEdge
                    .getTarget().getAttribute(
                            GraphicAttributeConstants.GRAPHICS))
                    .getCoordinate();

            int k = 1;
            for (Attribute attr : bendsColl) {
                caArray[k] = (CoordinateAttribute) attr;
                k++;
            }

            double[] distances = new double[caArray.length];

            // compute the position of the new bend
            double minDistance = 100;
            int position = -1;
            for (int i = 0; i < caArray.length - 1; i++) {
                CoordinateAttribute caA = caArray[i];
                CoordinateAttribute caB = caArray[i + 1];
                double m = (caB.getY() - caA.getY())
                        / (caB.getX() - caA.getX());
                double d = (m * (mousePos.getX() - caA.getX())
                        - mousePos.getY() + caA.getY())
                        / Math.sqrt(Math.pow(m, 2) + 1);

                if (Math.abs(d) < minDistance
                        && rectangleContainsPoint(caArray[i], caArray[i + 1],
                                mousePos)) {
                    minDistance = Math.abs(d);
                    position = i;
                }
                distances[i] = Math.abs(d);

            }

            if (position == -1)
                return;

            boolean added = false;
            int index = 0;
            for (Attribute bend : bendsColl) {
                String id = GraphicAttributeConstants.BEND + index;
                if (index == position) {
                    newBends.add(new CoordinateAttribute(id, mousePos.getX(),
                            mousePos.getY()));
                    index++;
                    id = GraphicAttributeConstants.BEND + index;
                    added = true;
                }
                newBends.add(new CoordinateAttribute(id,
                        ((CoordinateAttribute) bend).getX(),
                        ((CoordinateAttribute) bend).getY()));

                index++;
            }

            // if the bend has to be added at the last position or no bends
            // existed yet
            if (!added) {
                newBends.add(new CoordinateAttribute(
                        GraphicAttributeConstants.BEND + index,
                        mousePos.getX(), mousePos.getY()));
            }

            ListenerManager lm = topEdge.getGraph().getListenerManager();
            lm.transactionStarted(this);
            ega.setBends(newBends);
            if (topEdge.getAttribute(GraphicAttributeConstants.SHAPE_PATH)
                    .getValue().equals(
                            GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME)) {
                // change line shape to "polyline" if current shape
                // is "straight line"
                topEdge.changeString(GraphicAttributeConstants.SHAPE_PATH,
                        GraphicAttributeConstants.POLYLINE_CLASSNAME);
            }
            lm.transactionFinished(this);
        }
    }

    private boolean rectangleContainsPoint(CoordinateAttribute a,
            CoordinateAttribute b, Point pointToCheck) {
        double rightBound = Math.max(a.getX(), b.getX());
        double leftBound = Math.min(a.getX(), b.getX());
        double upperBound = Math.max(a.getY(), b.getY());
        double lowerBound = Math.min(a.getY(), b.getY());

        if (Math.max(a.getX(), b.getX()) - Math.min(a.getX(), b.getX()) < tolerance) {
            rightBound = Math.max(a.getX(), b.getX()) + tolerance / 2;
            leftBound = Math.min(a.getX(), b.getX()) - tolerance / 2;
        }

        if (Math.max(a.getY(), b.getY()) - Math.min(a.getY(), b.getY()) < tolerance) {
            upperBound = Math.max(a.getY(), b.getY()) + tolerance / 2;
            lowerBound = Math.min(a.getY(), b.getY()) - tolerance / 2;
        }

        if (pointToCheck.getX() < rightBound && pointToCheck.getX() > leftBound
                && pointToCheck.getY() < upperBound
                && pointToCheck.getY() > lowerBound)
            return true;
        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
