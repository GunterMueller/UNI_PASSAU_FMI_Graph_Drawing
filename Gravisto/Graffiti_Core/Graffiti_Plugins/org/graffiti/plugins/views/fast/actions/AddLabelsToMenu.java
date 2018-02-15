// =============================================================================
//
//   AddLabelsToMenu.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.view.interactive.ActionId;
import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.PopupMenuItem;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.dialog.LabelDialog;
import org.graffiti.session.EditorSession;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
@ActionId("addLabelsToMenu")
public class AddLabelsToMenu extends FastViewAction {
    private Slot<Set<GraphElement>> elementsSlot = Slot.createSetSlot(
            "elements", GraphElement.class);

    private Slot<PopupMenuItem> menuSlot = Slot.create("menu",
            PopupMenuItem.class);

    @Override
    public void perform(InSlotMap in, OutSlotMap out, Graph graph,
            FastView view, EditorSession session) {
        PopupMenuItem menu = in.get(menuSlot);
        final Set<GraphElement> set = in.get(elementsSlot);
        menu.addLast(PopupMenuItem.createSeparator());
        Set<String> ids = null;
        for (GraphElement element : set) {
            Map<String, Attribute> map = element.getAttributes()
                    .getCollection();
            Set<String> ids2 = new HashSet<String>();
            for (Map.Entry<String, Attribute> entry : map.entrySet()) {
                if (entry.getValue() instanceof LabelAttribute) {
                    ids2.add(entry.getKey());
                }
            }
            if (ids == null) {
                ids = ids2;
            } else {
                ids.retainAll(ids2);
            }
            if (ids.isEmpty()) {
                break;
            }
        }
        if (ids != null) {
            for (final String id : ids) {
                String displayId = id;
                if (displayId.length() > 50) {
                    displayId = displayId.substring(0, 47) + "...";
                }
                menu.addLast(new PopupMenuItem(String.format(FastViewPlugin
                        .getString("actions.addLabelsToMenu.editlabel"),
                        displayId), new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        LabelDialog.get().showEdit(set, id);
                    }
                }));
            }
        }
        menu.addLast(new PopupMenuItem(FastViewPlugin
                .getString("actions.addLabelsToMenu.newlabel"),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        LabelDialog.get().showCreate(set);
                    }
                }));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
