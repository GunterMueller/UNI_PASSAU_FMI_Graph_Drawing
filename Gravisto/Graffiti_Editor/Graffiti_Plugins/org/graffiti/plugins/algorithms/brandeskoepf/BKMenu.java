//==============================================================================
//
//   BKMenu.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: BKMenu.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.brandeskoepf;

import org.graffiti.plugin.gui.GraffitiContainer;
import org.graffiti.plugin.gui.GraffitiMenu;

/**
 * The class for the new menue item
 * 
 * @author Florian Fischer
 */
public class BKMenu extends GraffitiMenu implements GraffitiContainer {
    // ~ Static fields/initializers
    // =============================================

    /**
     * 
     */
    private static final long serialVersionUID = 2614486399747270349L;
    /** The ID of the menue entry */
    public static final String ID = "org.graffiti.plugins.algorithms.brandeskoepf.menus.BKMenu";

    // ~ Constructors
    // ===========================================================

    /**
     * Creates a new BKMenu object.
     */
    public BKMenu() {
        super();
        setName("Coordiante Assignement (BK)");
        setText("Coordiante Assignement (BK)");

        // setMnemonic('T');
        setEnabled(true);
    }

    // ~ Methods
    // ================================================================

    /**
     * Returns the ID
     * 
     * @return The ID
     */
    public String getId() {
        return ID;
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
