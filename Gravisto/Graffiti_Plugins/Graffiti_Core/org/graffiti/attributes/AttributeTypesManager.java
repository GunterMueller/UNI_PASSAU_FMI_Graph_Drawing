// =============================================================================
//
//   AttributeTypesManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeTypesManager.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.attributes;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.graffiti.managers.pluginmgr.PluginDescription;
import org.graffiti.managers.pluginmgr.PluginManagerListener;
import org.graffiti.plugin.GenericPlugin;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Provides a type manager for attributes. It contains the default attributes
 * from the package <code>org.graffiti.attributes</code>. Additional classes
 * implementing the <code>org.graffiti.attributes.Attribute</code>-interface can
 * be added and then used in an arbitrary <code>Attribute</code> hierarchy
 * associated with this <code>AttributeTypesManager</code>.
 * 
 * @version $Revision: 5779 $
 */
public class AttributeTypesManager implements PluginManagerListener {
    /** The logger for this class */
    private static final Logger logger = Logger
            .getLogger(AbstractAttribute.class.getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** Maps a fully qualified class name to the appropriate class. */
    private Map<String, Class<?>> attributeTypes;

    /**
     * Constructs a new <code>AttributeTypesManager</code>. Loads the default
     * <code>Attribute</code> classes from the package
     * <code>org.graffiti.attributes</code>.
     */
    public AttributeTypesManager() {
        attributeTypes = new HashMap<String, Class<?>>();
    }

    /**
     * Returns an instance of the class that is associated with the name of the
     * attribute.
     * 
     * @param attrName
     *            the name of the attribute type.
     * @param id
     *            the id that is assigned to the new attribute.
     * 
     * @return an instance of the class that is associated with the name of the
     *         attribute.
     */
    public Object getAttributeInstance(String attrName, String id) {
        assert (attrName != null) && (id != null);

        Class<?> c = attributeTypes.get(attrName);
        assert c != null : "Attribute type " + attrName + " not registered";

        try {
            Class<?>[] argTypes = new Class[] { String.class };
            Constructor<?> constr = c.getDeclaredConstructor(argTypes);

            return constr.newInstance(new Object[] { id });

            // return ((Class)(attributeTypes.get(attrName))).newInstance();
            // } catch (InstantiationException ie) {
            // throw new RuntimeException
            // ("Class " + attrName + " could not be instantiated: "+ ie);
            // } catch (IllegalAccessException iae) {
            // throw new RuntimeException
            // ("Class " + attrName + " could not be instantiated: "+ iae);
            // } catch (NoSuchMethodException nme) {
            // throw new RuntimeException("No constructor with one String as "
            // + "parameter found: " + nme);
        } catch (Exception e) {
            assert false : "Exception occurred: " + e;

            return null;
        }
    }

    /**
     * Sets the map of known <code>Attribute</code> types.
     * 
     * @param newAttrTypes
     *            the new<code>Attribute</code> types map.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void setAttributeTypes(Map<String, Class<?>> newAttrTypes) {
        // this.attributeTypes = attributeTypes;
        assert newAttrTypes != null;
        this.attributeTypes = new HashMap<String, Class<?>>();

        for (String id : newAttrTypes.keySet()) {
            addAttributeType(newAttrTypes.get(id));
        }
    }

    /**
     * Returns a map of all known <code>Attribute</code> types.
     * 
     * @return a map of all known <code>Attribute</code> types.
     */
    public Map<String, Class<?>> getAttributeTypes() {
        return attributeTypes;
    }

    /**
     * Adds a given <code>Attribute</code> type class to the list of attribute
     * types.
     * 
     * @param c
     *            the attribute class to add.
     * 
     * @throws IllegalArgumentException
     *             DOCUMENT ME!
     */
    public void addAttributeType(Class<?> c) {
        // addAttributeType(c.getName(), c);
        boolean implementsAttribute = false;

        Class<?> superClass = c;

        while (!superClass.getName().equals("java.lang.Object")) {
            for (Class<?> iface : superClass.getInterfaces()) {
                if (iface.getName().equals("org.graffiti.attributes.Attribute")) {
                    implementsAttribute = true;

                    break;
                }
            }

            superClass = superClass.getSuperclass();
        }

        if (implementsAttribute) {
            attributeTypes.put(c.getName(), c);
            logger.info("Registered " + c.getName());
        } else
            throw new IllegalArgumentException(
                    "Only classes that implement interface "
                            + "org.graffiti.attributes.Attribute can be added.");
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
        for (Class<?> type : plugin.getAttributes()) {
            addAttributeType(type);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
