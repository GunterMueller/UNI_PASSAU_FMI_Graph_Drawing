// =============================================================================
//
//   MoveBend.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
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

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("moveBend")
public class MoveBend extends FastViewAction {
    @InSlot
    public static final Slot<Point2D> positionSlot = Slot.create("position",
            Point2D.class);

    private Edge edge;
    private String bend;
    private Point2D mouseStartPosition;
    private Point2D bendStartPosition;
    private boolean wasUsed;

    public void setCurrentData(Edge edge, String bend, Point2D startPosition) {
        this.edge = edge;
        this.bend = bend;
        this.mouseStartPosition = startPosition;
        SortedCollectionAttribute sca = (SortedCollectionAttribute) edge
                .getAttribute(GraphicAttributeConstants.BENDS_PATH);
        Attribute a = sca.getCollection().get(bend);
        if (a instanceof CoordinateAttribute) {
            CoordinateAttribute ca = (CoordinateAttribute) a;
            bendStartPosition = ca.getCoordinate();
        }
        wasUsed = false;
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        if (edge == null || bend == null)
            return;
        FastViewGestureFeedbackProvider gfp = view.getGestureFeedbackProvider();
        Point2D toPosition = in.get(positionSlot);
        Point2D delta = new Point2D.Double(toPosition.getX()
                - mouseStartPosition.getX(), toPosition.getY()
                - mouseStartPosition.getY());
        SortedCollectionAttribute sca = (SortedCollectionAttribute) edge
                .getAttribute(GraphicAttributeConstants.BENDS_PATH);
        Attribute a = sca.getCollection().get(bend);
        if (a instanceof CoordinateAttribute) {
            CoordinateAttribute ca = (CoordinateAttribute) a;
            if (!wasUsed) {
                UndoUtil undoUtil = new UndoUtil(session);
                undoUtil.preChange(ca);
                undoUtil.close();
                wasUsed = true;
            }
            ca.setCoordinate(gfp.snapBend(new Point2D.Double(bendStartPosition
                    .getX()
                    + delta.getX(), bendStartPosition.getY() + delta.getY())));
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
