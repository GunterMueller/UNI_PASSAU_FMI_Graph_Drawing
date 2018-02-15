// =============================================================================
//
//   SetHoverElement.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
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
@ActionId("setHoveredElement")
public class SetHoveredElement extends FastViewAction {
    @InSlot
    public static final Slot<GraphElement> elementSlot = Slot.create("element",
            GraphElement.class, true);

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        GraphElement element = in.get(elementSlot);
        view.getGestureFeedbackProvider().setHoveredElement(element);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
