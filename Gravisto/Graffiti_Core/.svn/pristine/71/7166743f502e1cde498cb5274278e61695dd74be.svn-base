// =============================================================================
//
//   AddSingleToSelection.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.tools.commonactions;

import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("addSingleToSelection")
public class AddSingleToSelection extends CommonAction {
    @InSlot
    public static final Slot<GraphElement> elementSlot = Slot.create("element",
            GraphElement.class);

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            InteractiveView<?> view, EditorSession session) {
        GraphElement ge = in.get(elementSlot);
        session.getSelectionModel().getActiveSelection().add(ge);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
