// =============================================================================
//
//   TestMenuItemAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestMenuItemAction.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.guis.test;

import java.awt.event.ActionEvent;

import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;

/**
 * DOCUMENT ME!
 * 
 * @author chris
 */
public class TestMenuItemAction extends GraffitiAction {

    /**
     * 
     */
    private static final long serialVersionUID = 3861598198173816456L;

    /**
     * Creates a new TestMenuItemAction object.
     * 
     * @param name
     *            DOCUMENT ME!
     */
    public TestMenuItemAction(String name) {
        super(name, null);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public boolean isEnabled() {
        return super.enabled;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void actionPerformed(ActionEvent e) {
        System.out.println("klicked on test menu item");
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
