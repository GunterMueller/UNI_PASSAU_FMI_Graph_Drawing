// =============================================================================
//
//   OpenGLPlugin.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast.opengl;

import java.util.prefs.Preferences;

import org.graffiti.plugin.EditorPluginAdapter;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class OpenGLPlugin extends EditorPluginAdapter {
    private static OpenGLPlugin plugin;

    protected static Preferences getPreferences() {
        return plugin.prefs;
    }

    public OpenGLPlugin() {
        plugin = this;
        views = new String[] { "org.graffiti.plugins.views.fast.opengl.OpenGLFastView" };
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
