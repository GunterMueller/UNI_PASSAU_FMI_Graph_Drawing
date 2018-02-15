// =============================================================================
//
//   TestButton.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestButton.java 5769 2010-05-07 18:42:56Z gleissner $

package de.chris.plugins.guis.test;

import javax.swing.ImageIcon;

import org.graffiti.plugin.gui.GraffitiButton;

/**
 * This is an example button on a toolbar.
 * 
 * @author chris
 */
public class TestButton extends GraffitiButton {

    /**
     * 
     */
    private static final long serialVersionUID = 1974531595393310272L;

    /**
     * Creates a new TestButton object.
     */
    public TestButton() {
        super(TestToolbar.ID, new TestToolbarButtonAction());
        setToolTipText("This is a test tooltip for the test button.");
        setIcon(new ImageIcon(TestButton.class.getResource("smiley.gif")));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
