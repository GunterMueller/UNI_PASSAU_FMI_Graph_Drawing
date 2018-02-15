// =============================================================================
//
//   StartNodeResizing.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.modes.fast.Sector;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("startNodeResizing")
public class StartNodeResizing extends FastViewAction {
    @InSlot
    public static final Slot<Sector> horizontalSectorSlot = Slot.create(
            "horizontalSector", Sector.class);

    @InSlot
    public static final Slot<Sector> verticalSectorSlot = Slot.create(
            "verticalSector", Sector.class);

    @InSlot
    public static final Slot<Node> nodeSlot = Slot.create("node", Node.class);

    private ResizeNode resizeNodeAction;

    public StartNodeResizing(ResizeNode resizeNodeAction) {
        this.resizeNodeAction = resizeNodeAction;
    }

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        resizeNodeAction.setCurrentData(in.get(horizontalSectorSlot), in
                .get(verticalSectorSlot), in.get(nodeSlot));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
