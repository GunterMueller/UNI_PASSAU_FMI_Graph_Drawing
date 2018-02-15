// =============================================================================
//
//   EditorPluginAdapter.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EditorPluginAdapter.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugin.view.GraffitiShape;

/**
 * Plugin for editor. Returns null everywhere.
 */
public class EditorPluginAdapter extends GenericPluginAdapter implements
        EditorPlugin {

    /**
     * Maps from an attribute class to an AttributeComponent class. old comment:
     * A <code>java.util.Map</code> from <code>Attribute</code> to the
     * corresponding <code>LabelValueRow</code>-instance.
     */
    protected Map<Class<?>, Class<?>> attributeComponents;

    /** The mapping between attribute classes and attributeComponent classes. */
    protected Map<Class<?>, Class<?>> valueEditComponents;

    /** The gui components the plugin provides. */
    protected GraffitiComponent[] guiComponents;

    /** The shapes the plugin provides. */
    protected GraffitiShape[] shapes;

    /**
     * Constructor for EditorPluginAdapter.
     */
    public EditorPluginAdapter() {
        super();
        this.guiComponents = new GraffitiComponent[0];
        this.shapes = new GraffitiShape[0];
        this.valueEditComponents = new HashMap<Class<?>, Class<?>>();
        this.attributeComponents = new HashMap<Class<?>, Class<?>>();
    }

    /**
     * Returns a mapping between attribute classnames and attributeComponent
     * classnames.
     * 
     * @return a mapping between attribute classnames and attributeComponent
     *         classnames.
     */
    public Map<Class<?>, Class<?>> getAttributeComponents() {
        return this.attributeComponents;
    }

    /**
     * Returns the array of <code>GraffitiComponent</code>s the plugin contains.
     * 
     * @return the array of <code>GraffitiComponent</code>s the plugin contains.
     */
    public GraffitiComponent[] getGUIComponents() {
        return this.guiComponents;
    }

    /**
     * Returns the array of <code>org.graffiti.plugin.view.GraffitiShape</code>s
     * the plugin contains.
     * 
     * @return the array of <code>org.graffiti.plugin.view.GraffitiShape</code>s
     *         the plugin contains.
     */
    public GraffitiShape[] getShapes() {
        return this.shapes;
    }

    /**
     * Returns a mapping from attribute classes to attributeComponent classes.
     * 
     * @return DOCUMENT ME!
     */
    public Map<Class<?>, Class<?>> getValueEditComponents() {
        return this.valueEditComponents;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
