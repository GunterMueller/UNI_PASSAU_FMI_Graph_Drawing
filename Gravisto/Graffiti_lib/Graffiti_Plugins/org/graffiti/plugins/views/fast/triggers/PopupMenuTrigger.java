// =============================================================================
//
//   PopupTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.OutSlot;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.ParamSlot;
import org.graffiti.plugin.view.interactive.PopupMenuSelectionGesture;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugin.view.interactive.slots.StringSlot;
import org.graffiti.plugins.views.fast.FastViewPlugin;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class PopupMenuTrigger extends FastViewTrigger {
    private static final String NOCANCEL_PATTERN = PopupMenuSelectionGesture.CANCEL_STRING;

    @ParamSlot
    public static final StringSlot idMatch = new StringSlot("id",
            FastViewPlugin.getString("popupmenuid.name"), FastViewPlugin
                    .getString("popupmenuid.description"), "");

    @OutSlot
    public static final Slot<String> idOut = Slot.create("id", String.class);

    public PopupMenuTrigger(RootTrigger parent) {
        super(parent, "popupmenu");
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        if (userGesture instanceof PopupMenuSelectionGesture) {
            String pattern = parameters.get(idMatch);
            String id = ((PopupMenuSelectionGesture) userGesture).getId();
            return pattern.length() == 0
                    || (pattern.equals(NOCANCEL_PATTERN) && !id
                            .equals(PopupMenuSelectionGesture.CANCEL_STRING))
                    || pattern.equals(id);
        }
        return false;
    }

    @Override
    protected void apply(InSlotMap parameters, InSlotMap in, OutSlotMap out,
            UserGesture userGesture) {
        PopupMenuSelectionGesture pmsg = (PopupMenuSelectionGesture) userGesture;
        out.put(idOut, pmsg.getId());
        out.putAll(pmsg.getSlots());
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
