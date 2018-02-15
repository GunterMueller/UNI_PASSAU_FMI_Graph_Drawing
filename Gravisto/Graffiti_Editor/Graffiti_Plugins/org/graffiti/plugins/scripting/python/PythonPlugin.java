// =============================================================================
//
//   PythonPlugin.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.scripting.python;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugins.scripting.ScriptingRegistry;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class PythonPlugin extends EditorPluginAdapter {
    private static final Bundle BUNDLE = Bundle.getBundle(PythonPlugin.class);

    private PythonEngine engine;

    public PythonPlugin() {
        engine = new PythonEngine(this);
        ScriptingRegistry.get()
                .registerEngine(PythonEngine.LANGUAGE_ID, engine);
    }

    public static String getString(String key) {
        return BUNDLE.getString(key);
    }

    public static String format(String key, Object... args) {
        return BUNDLE.format(key, args);
    }

    public PythonEngine getEngine() {
        return engine;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
