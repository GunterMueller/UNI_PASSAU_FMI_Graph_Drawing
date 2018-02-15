// =============================================================================
//
//   EditLabel.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlot;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.dialog.LabelDialog;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("editLabel")
public class EditLabel extends FastViewAction {
    @InSlot
    public static final Slot<GraphElement> elementSlot = Slot.create("element",
            GraphElement.class);

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        GraphElement element = in.get(elementSlot);
        Set<GraphElement> set = Collections.singleton(element);
        Map<String, Attribute> map = element.getAttributes().getCollection();
        for (Map.Entry<String, Attribute> entry : map.entrySet()) {
            if (entry.getValue() instanceof LabelAttribute) {
                LabelDialog.get().showEdit(set, entry.getKey());
                return;
            }
        }
        LabelDialog.get().showCreate(set);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
