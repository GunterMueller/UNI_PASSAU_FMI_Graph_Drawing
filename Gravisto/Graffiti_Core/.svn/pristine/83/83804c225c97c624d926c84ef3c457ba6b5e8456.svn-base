// =============================================================================
//
//   KeyboardTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import java.awt.event.KeyEvent;

import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.KeyboardGesture;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.ParamSlot;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugins.modes.fast.slots.KeyCodeSlot;
import org.graffiti.plugins.modes.fast.slots.ModifierHandlingSlot;
import org.graffiti.plugins.views.fast.FastViewPlugin;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class KeyboardTrigger extends FastViewTrigger {
    @ParamSlot
    public static final KeyCodeSlot keyCode = new KeyCodeSlot("keyCode",
            FastViewPlugin.getString("keycode.name"), FastViewPlugin
                    .getString("keycode.description"), KeyEvent.VK_A);

    @ParamSlot
    public static final ModifierHandlingSlot shift = new ModifierHandlingSlot(
            "shift", FastViewPlugin.getString("shiftkey.name"), FastViewPlugin
                    .getString("shiftkey.description"));

    @ParamSlot
    public static final ModifierHandlingSlot ctrl = new ModifierHandlingSlot(
            "ctrl", FastViewPlugin.getString("ctrlkey.name"), FastViewPlugin
                    .getString("ctrlkey.description"));

    @ParamSlot
    public static final ModifierHandlingSlot alt = new ModifierHandlingSlot(
            "alt", FastViewPlugin.getString("altkey.name"), FastViewPlugin
                    .getString("altkey.description"));

    @ParamSlot
    public static final ModifierHandlingSlot meta = new ModifierHandlingSlot(
            "meta", FastViewPlugin.getString("metakey.name"), FastViewPlugin
                    .getString("metakey.description"));

    public KeyboardTrigger(RootTrigger parent) {
        super(parent, "key");
    }

    @Override
    protected void apply(InSlotMap parameters, InSlotMap in, OutSlotMap out,
            UserGesture userGesture) {
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        if (!(userGesture instanceof KeyboardGesture))
            return false;
        KeyboardGesture kg = (KeyboardGesture) userGesture;
        if (!matches(parameters, shift, kg.isShiftDown()))
            return false;
        if (!matches(parameters, ctrl, kg.isControlDown()))
            return false;
        if (!matches(parameters, alt, kg.isAltDown()))
            return false;
        if (!matches(parameters, meta, kg.isMetaDown()))
            return false;
        return parameters.get(keyCode) == kg.getKeyCode();
    }

    private boolean matches(InSlotMap parameters, ModifierHandlingSlot modSlot,
            boolean isDown) {
        switch (parameters.get(modSlot)) {
        case IGNORE:
            return true;
        case REQUIRE_DOWN:
            return isDown;
        case REQUIRE_UP:
            return !isDown;
        }
        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
