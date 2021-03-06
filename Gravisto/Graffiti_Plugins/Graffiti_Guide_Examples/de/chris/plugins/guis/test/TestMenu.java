// =============================================================================
//
//   TestMenu.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestMenu.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.guis.test;

import org.graffiti.plugin.gui.GraffitiContainer;
import org.graffiti.plugin.gui.GraffitiMenu;

/**
 * DOCUMENT ME!
 * 
 * @author chris
 */
public class TestMenu extends GraffitiMenu implements GraffitiContainer {

    // to avoid collisions let ID be package name + ".menus." + name of the menu

    /**
     * 
     */
    private static final long serialVersionUID = -8760042510268001763L;
    /** DOCUMENT ME! */
    public static final String ID = "de.chris.plugins.guis.test.menus.Test";

    /**
     * Create Menu.
     */
    public TestMenu() {
        super();
        setName("Test");
        setText("Test");
        setMnemonic('T');
        setEnabled(true);

        /*
         * TestMenuItemAction testMenuItemAction = new TestMenuItemAction("Test
         * Action"); testMenuItemAction.setEnabled(true);
         * testMenuItemAction.putValue(Action.NAME, "Test Action");
         * GraffitiMenuItem testItem = new GraffitiMenuItem("Test",
         * testMenuItemAction); testItem.setToolTipText("This is a test tooltip
         * for a menu item."); insert(testItem, 1);
         */
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public String getId() {
        return ID;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
