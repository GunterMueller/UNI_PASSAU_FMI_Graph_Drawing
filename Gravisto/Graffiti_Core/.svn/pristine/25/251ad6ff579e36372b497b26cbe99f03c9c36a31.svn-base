// =============================================================================
//
//   MouseMoveOnNodeBorderTrigger.java
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
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugins.modes.fast.Sector;
import org.graffiti.plugins.modes.fast.slots.SectorSlot;
import org.graffiti.plugins.views.fast.FastViewPlugin;
import org.graffiti.plugins.views.fast.usergestures.ExtendedMouseMove;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class MouseMoveOnNodeBorderTrigger extends FastViewTrigger {
    @ParamSlot
    public static final SectorSlot horizontalSectorParam = new SectorSlot(
            "horizontalSector", FastViewPlugin
                    .getString("horizontalsector.name"), FastViewPlugin
                    .getString("horizontalsector.description"));

    @ParamSlot
    public static final SectorSlot verticalSectorParam = new SectorSlot(
            "verticalSector", FastViewPlugin.getString("verticalsector.name"),
            FastViewPlugin.getString("verticalsector.description"));

    @OutSlot
    public static final SectorSlot horizontalSectorOut = new SectorSlot(
            "horizontalSector", FastViewPlugin
                    .getString("horizontalsector.name"), FastViewPlugin
                    .getString("horizontalsector.description"));

    @OutSlot
    public static final SectorSlot verticalSectorOut = new SectorSlot(
            "verticalSector", FastViewPlugin.getString("verticalsector.name"),
            FastViewPlugin.getString("verticalsector.description"));

    public MouseMoveOnNodeBorderTrigger(MouseMoveOnNodeTrigger parent) {
        super(parent, "border");
    }

    @Override
    protected boolean matches(InSlotMap parameters, InSlotMap in,
            UserGesture userGesture) {
        ExtendedMouseMove emm = (ExtendedMouseMove) userGesture;
        if (!emm.isOnShapeBorder())
            return false;
        Sector hsp = parameters.get(horizontalSectorParam);
        Sector vsp = parameters.get(verticalSectorParam);

        return (hsp == Sector.IGNORE || hsp == emm.getHorizontalSector())
                && (vsp == Sector.IGNORE || vsp == emm.getVerticalSector());
    }

    @Override
    protected void apply(InSlotMap parameters, InSlotMap in, OutSlotMap out,
            UserGesture userGesture) {
        ExtendedMouseMove emm = (ExtendedMouseMove) userGesture;
        out.put(horizontalSectorOut, emm.getHorizontalSector());
        out.put(verticalSectorOut, emm.getVerticalSector());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
