// =============================================================================
//
//   SetResizingCursor.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.Cursor;
import java.util.HashMap;

import org.graffiti.graph.Graph;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.modes.fast.Sector;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.FastViewGestureFeedbackProvider;
import org.graffiti.session.EditorSession;
import org.graffiti.util.Pair;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("setResizingCursor")
public class SetResizingCursor extends FastViewAction {
    @InSlot
    public static final Slot<Sector> horizontalSectorSlot = Slot.create(
            "horizontalSector", Sector.class);

    @InSlot
    public static final Slot<Sector> verticalSectorSlot = Slot.create(
            "verticalSector", Sector.class);

    private HashMap<Pair<Sector, Sector>, Cursor> cursorMap;

    public SetResizingCursor() {
        cursorMap = new HashMap<Pair<Sector, Sector>, Cursor>();
        put(Sector.LOW, Sector.LOW, Cursor.NW_RESIZE_CURSOR);
        put(Sector.LOW, Sector.CENTER, Cursor.W_RESIZE_CURSOR);
        put(Sector.LOW, Sector.HIGH, Cursor.SW_RESIZE_CURSOR);
        put(Sector.CENTER, Sector.LOW, Cursor.N_RESIZE_CURSOR);
        put(Sector.CENTER, Sector.CENTER, Cursor.DEFAULT_CURSOR);
        put(Sector.CENTER, Sector.HIGH, Cursor.S_RESIZE_CURSOR);
        put(Sector.HIGH, Sector.LOW, Cursor.NE_RESIZE_CURSOR);
        put(Sector.HIGH, Sector.CENTER, Cursor.E_RESIZE_CURSOR);
        put(Sector.HIGH, Sector.HIGH, Cursor.SE_RESIZE_CURSOR);
    }

    private void put(Sector hs, Sector vs, int id) {
        cursorMap.put(Pair.create(hs, vs), Cursor.getPredefinedCursor(id));
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        FastViewGestureFeedbackProvider gfp = view.getGestureFeedbackProvider();
        Sector hs = in.get(horizontalSectorSlot);
        Sector vs = in.get(verticalSectorSlot);
        Cursor cursor = cursorMap.get(Pair.create(hs, vs));
        if (cursor == null) {
            cursor = Cursor.getDefaultCursor();
        }
        gfp.setCursor(cursor);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
