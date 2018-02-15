// =============================================================================
//
//   DefaultView.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DefaultView.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.views.defaults;

import org.graffiti.plugin.EditorPluginAdapter;

/**
 * PlugIn for default view of graffiti graph editor
 * 
 * @version $Revision: 5766 $
 */
public class DefaultView extends EditorPluginAdapter {

    /**
     * Constructor for DefaultView.
     */
    public DefaultView() {
        super();
        this.views = new String[1];
        this.views[0] = "org.graffiti.plugins.views.defaults.GraffitiView";
    }

    // probably the method configure(Preferences pref) will be overridden.
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
