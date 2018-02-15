// =============================================================================
//
//   EditorPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EditorPlugin.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin;

import java.util.Map;

import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugin.view.GraffitiShape;

/**
 * 
 */
public interface EditorPlugin extends GenericPlugin {

    /**
     * Returns a mapping between attribute paths and attributeComponent classes.
     * 
     * @return DOCUMENT ME!
     */
    public Map<Class<?>, Class<?>> getAttributeComponents();

    /**
     * Returns the array of <code>GraffitiComponent</code>s the plugin contains.
     * 
     * @return the array of <code>GraffitiComponent</code>s the plugin contains.
     */
    public GraffitiComponent[] getGUIComponents();

    /**
     * Returns the array of <code>org.graffiti.plugin.view.GraffitiShape</code>s
     * the plugin contains.
     * 
     * @return the array of <code>org.graffiti.plugin.view.GraffitiShape</code>s
     *         the plugin contains.
     */
    public GraffitiShape[] getShapes();

    /**
     * Returns a mapping between attribute classnames and attributeComponent
     * classes.
     * 
     * @return DOCUMENT ME!
     */
    public Map<Class<?>, Class<?>> getValueEditComponents();
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
