// =============================================================================
//
//   SetCursor.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.commonactions;

import java.awt.Cursor;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.GestureFeedbackProvider;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.MouseCursorProvider;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("setCursor")
public class SetCursor extends CommonAction {
    @InSlot
    public static final Slot<Integer> cursorSlot = Slot.create("cursor",
            Integer.class);

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            InteractiveView<?> view, EditorSession session) {
        GestureFeedbackProvider gfp = view.getGestureFeedbackProvider();
        if (!(gfp instanceof MouseCursorProvider))
            return;
        ((MouseCursorProvider) gfp).setCursor(new Cursor(in.get(cursorSlot)
                .intValue()));

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
