// =============================================================================
//
//   MouseTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import java.awt.geom.Point2D;

import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.MouseGesture;
import org.graffiti.plugin.view.interactive.OutSlot;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.ParamSlot;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugins.modes.fast.slots.ModifierHandlingSlot;
import org.graffiti.plugins.views.fast.FastViewPlugin;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MouseTrigger extends FastViewTrigger {
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

    @OutSlot
    public static final Slot<Point2D> position = Slot.create("position",
            Point2D.class);

    public MouseTrigger(RootTrigger parent) {
        super(parent, "mouse");
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        if (!(userGesture instanceof MouseGesture))
            return false;
        MouseGesture mg = (MouseGesture) userGesture;
        if (!matches(parameters, shift, mg.isShiftDown()))
            return false;
        if (!matches(parameters, ctrl, mg.isControlDown()))
            return false;
        if (!matches(parameters, alt, mg.isAltDown()))
            return false;
        if (!matches(parameters, meta, mg.isMetaDown()))
            return false;
        return true;
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

    @Override
    protected void apply(InSlotMap parameters, InSlotMap in, OutSlotMap out,
            UserGesture userGesture) {
        MouseGesture mouseGesture = (MouseGesture) userGesture;
        out.put(position, mouseGesture.getPosition());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
