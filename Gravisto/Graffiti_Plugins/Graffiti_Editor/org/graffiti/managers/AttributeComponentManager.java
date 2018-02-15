// =============================================================================
//
//   AttributeComponentManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeComponentManager.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.managers;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.editor.AttributeComponentNotFoundException;
import org.graffiti.managers.pluginmgr.PluginDescription;
import org.graffiti.managers.pluginmgr.PluginManagerListener;
import org.graffiti.plugin.EditorPlugin;
import org.graffiti.plugin.GenericPlugin;
import org.graffiti.plugin.view.AttributeComponent;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;

/**
 * Contains the mapping between attribute classes and their representation as
 * <code>AttributeComponent</code> classes.
 * 
 * @author ph
 * @version $Revision: 5768 $
 */
public class AttributeComponentManager implements PluginManagerListener {

    /** Maps attribute classes to attributeComponent classes. */
    private Map<Class<?>, Class<?>> attributeComponents;

    /**
     * Constructs an AttributeComponentManager.
     */
    public AttributeComponentManager() {
        this.attributeComponents = new HashMap<Class<?>, Class<?>>();
    }

    /**
     * Returns an instance of the AttributeComponent that is capable of drawing
     * the attribute with type <code>aType</code>.
     * 
     * @param aType
     *            the class of the attribute to retrieve a component for.
     * 
     * @return an instance of an AttributeComponent.
     * 
     * @throws AttributeComponentNotFoundException
     *             DOCUMENT ME!
     */
    public AttributeComponent getAttributeComponent(Class<?> aType)
            throws AttributeComponentNotFoundException {
        if (!(attributeComponents.containsKey(aType)))
            throw new AttributeComponentNotFoundException(
                    "No registered GraffitiViewComponent for AttributeType "
                            + aType);

        Class<?> ac = attributeComponents.get(aType);

        try {
            AttributeComponent component = (AttributeComponent) InstanceLoader
                    .createInstance(ac);

            return component;
        } catch (InstanceCreationException ice) {
            throw new AttributeComponentNotFoundException(ice.getMessage());
        }
    }

    /**
     * Returns the map of attribute components.
     * 
     * @return DOCUMENT ME!
     */
    public Map<Class<?>, Class<?>> getAttributeComponents() {
        return attributeComponents;
    }

    /**
     * Called by the plugin manager, iff a plugin has been added.
     * 
     * @param plugin
     *            the added plugin.
     * @param desc
     *            the description of the new plugin.
     */
    public void pluginAdded(GenericPlugin plugin, PluginDescription desc) {
        // System.out.println("puting: " + plugin.getAttributeComponents());
        if (plugin instanceof EditorPlugin) {
            if (((EditorPlugin) plugin).getAttributeComponents() != null) {
                attributeComponents.putAll(((EditorPlugin) plugin)
                        .getAttributeComponents());
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
