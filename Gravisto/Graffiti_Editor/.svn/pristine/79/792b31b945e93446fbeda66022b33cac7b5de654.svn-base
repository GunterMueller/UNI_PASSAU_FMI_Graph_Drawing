// =============================================================================
//
//   StartSelectionAction.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.geom.Point2D;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("startSelectionRect")
public class StartSelectionRect extends FastViewAction {
    @InSlot
    public static final Slot<Point2D> positionSlot = Slot.create("position",
            Point2D.class);

    private ChangeSelectionRect changeAction;
    private FinishSelectionRect finishAction;

    public StartSelectionRect(ChangeSelectionRect changeAction,
            FinishSelectionRect finishAction) {
        this.changeAction = changeAction;
        this.finishAction = finishAction;
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        Point2D startPosition = in.get(positionSlot);
        changeAction.setStartPosition(startPosition);
        finishAction.setStartPosition(startPosition);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
