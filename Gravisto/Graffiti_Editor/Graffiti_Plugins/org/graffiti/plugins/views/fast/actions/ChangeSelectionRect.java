// =============================================================================
//
//   ChangeRectSelectionAction.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlot;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("changeSelectionRect")
public class ChangeSelectionRect extends FastViewAction {
    @InSlot
    public static final Slot<Point2D> positionSlot = Slot.create("position",
            Point2D.class);

    @OutSlot
    public static final Slot<Rectangle2D> rectangleSlot = Slot.create(
            "rectangle", Rectangle2D.class);

    private Point2D startPosition;

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        Point2D position = in.get(positionSlot);
        Rectangle2D rect = new Rectangle2D.Double(startPosition.getX(),
                startPosition.getY(), 0, 0);
        rect.add(position);
        view.getGestureFeedbackProvider().setSelectionRectangle(rect);
        out.put(rectangleSlot, rect);
    }

    protected void setStartPosition(Point2D startPosition) {
        this.startPosition = startPosition;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
