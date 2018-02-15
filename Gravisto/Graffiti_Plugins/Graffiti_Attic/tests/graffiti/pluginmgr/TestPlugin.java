// =============================================================================
//
//   TestPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestPlugin.java 5773 2010-05-07 18:50:34Z gleissner $

package tests.graffiti.pluginmgr;

import javax.swing.ImageIcon;

import org.graffiti.plugin.GenericPlugin;
import org.graffiti.plugin.GenericPluginAdapter;

/**
 * Represents a plugin for the plugin manager test cases.
 * 
 * @version $Revision: 5773 $
 */
public class TestPlugin extends GenericPluginAdapter implements GenericPlugin {

    /**
     * Constructor for TestPlugin.
     */
    public TestPlugin() {
        super();
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public ImageIcon getIcon() {
        return iBundle.getIcon("icon.plugin.test");
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
