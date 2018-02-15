// =============================================================================
//
//   EditComponentManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: EditComponentManager.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.managers;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.editor.EditComponentNotFoundException;
import org.graffiti.managers.pluginmgr.PluginDescription;
import org.graffiti.managers.pluginmgr.PluginManagerListener;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.EditorPlugin;
import org.graffiti.plugin.GenericPlugin;
import org.graffiti.plugin.editcomponent.ValueEditComponent;
import org.graffiti.util.InstanceCreationException;
import org.graffiti.util.InstanceLoader;

/**
 * Contains the mapping between displayable classes and their representation as
 * <code>AttributeComponent</code> classes.
 * 
 * @author ph
 * @version $Revision: 5768 $
 */
public class EditComponentManager implements PluginManagerListener {

    /** Maps displayable classes to ValueEditComponent classes. */
    private Map<Class<?>, Class<?>> valueEditComponents;

    /**
     * Constructs an EditComponentManager.
     */
    public EditComponentManager() {
        this.valueEditComponents = new HashMap<Class<?>, Class<?>>();
    }

    /**
     * Returns the map of value edit components.
     * 
     * @return DOCUMENT ME!
     */
    public Map<Class<?>, Class<?>> getEditComponents() {
        return valueEditComponents;
    }

    /**
     * Returns an instance of the ValueEditComponent that is capable of
     * providing a possibility to alter the value of the displayable with type
     * <code>aType</code>.
     * 
     * @param aType
     *            the class of the displayable to retrieve a component for.
     * 
     * @return an instance of an ValueEditComponent.
     * 
     * @throws EditComponentNotFoundException
     *             DOCUMENT ME!
     */
    public ValueEditComponent getValueEditComponent(Class<?> aType,
            Displayable<?> displayable) throws EditComponentNotFoundException {
        if (!(valueEditComponents.containsKey(aType)))
            throw new EditComponentNotFoundException(
                    "No registered ValueEditComponent for displayable type "
                            + aType);

        Class<?> ac = valueEditComponents.get(aType);

        try {
            return (ValueEditComponent) InstanceLoader.createInstance(ac,
                    Displayable.class, displayable);
        } catch (InstanceCreationException ice) {
            throw new EditComponentNotFoundException(ice.getMessage());
        }
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
        // System.out.println("putting: " + plugin.getAttributeComponents());
        if (plugin instanceof EditorPlugin) {
            if (((EditorPlugin) plugin).getValueEditComponents() != null) {
                valueEditComponents.putAll(((EditorPlugin) plugin)
                        .getValueEditComponents());
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
