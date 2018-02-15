//==============================================================================
//
//   BKItem.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: BKItem.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.brandeskoepf;

import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.gui.GraffitiMenuItem;

/**
 * The class for the menue item
 * 
 * @author Florian Fischer
 */
public class BKItem extends GraffitiMenuItem {
    // ~ Constructors
    // ===========================================================

    /**
     * 
     */
    private static final long serialVersionUID = 2860985072217183897L;

    /**
     * Creates a new BKItem object.
     */
    public BKItem() {
        super(BKMenu.ID, new BKMenuShowPreferences("Options"));

        setText(((GraffitiAction) getAction()).getName()); // default
        setMnemonic('O');
        setToolTipText("Set the values for the layout");
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
