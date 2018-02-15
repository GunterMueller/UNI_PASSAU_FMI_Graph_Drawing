// =============================================================================
//
//   MouseReleaseTrigger.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.triggers;

import java.awt.geom.Point2D;

import org.graffiti.plugin.view.interactive.InSlotMap;
import org.graffiti.plugin.view.interactive.MouseButton;
import org.graffiti.plugin.view.interactive.OutSlot;
import org.graffiti.plugin.view.interactive.OutSlotMap;
import org.graffiti.plugin.view.interactive.ParamSlot;
import org.graffiti.plugin.view.interactive.Slot;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugins.modes.fast.slots.MouseButtonSlot;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.usergestures.ExtendedMouseRelease;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MouseReleaseTrigger extends FastViewTrigger {
    @ParamSlot
    public static final MouseButtonSlot button = new MouseButtonSlot("button",
            FastViewPlugin.getString("mousebutton.name"), FastViewPlugin
                    .getString("mousebutton.description"), MouseButton.LEFT);

    @OutSlot
    public static final Slot<Point2D> rawPosition = Slot.create("rawPosition",
            Point2D.class);

    public MouseReleaseTrigger(MouseTrigger parent) {
        super(parent, "release");
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        if (!(userGesture instanceof ExtendedMouseRelease))
            return false;
        MouseButton requiredButton = parameters.get(button);
        ExtendedMouseRelease emr = (ExtendedMouseRelease) userGesture;
        return requiredButton == MouseButton.IGNORE
                || emr.getButton() == requiredButton;
    }

    @Override
    protected void apply(InSlotMap parameters, InSlotMap in, OutSlotMap out,
            UserGesture userGesture) {
        ExtendedMouseRelease emr = (ExtendedMouseRelease) userGesture;
        out.put(rawPosition, emr.getRawPosition());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
