// =============================================================================
//
//   AttributeFactoryFactoryManager.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util.attributes;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.util.Callback;
import org.graffiti.util.VoidCallback;

/**
 * Class for creating attributes employing a double factory pattern. It contains
 * a map that determines the kind of attribute to be created dependent on the
 * {@code Class} object passed to the {@code createAttribute} method. The
 * construction process is triple-stage. First, the appropriate
 * {@link AttributeFactoryFactory} is determined for a given {@code Class}
 * object by a look up in the map. Then the {@code AttributeFactoryFactory} is
 * used to create an {@link AttributeFactory}, which finally creates the desired
 * {@link Attribute}. One may optionally specify callbacks to be called by the
 * setter methods of the created {@code Attribute}s.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see AttributeFactory#setCallback(Callback, VoidCallback)
 */
public class AttributeFactoryFactoryManager {
    /**
     * Callback object whose method always returns {@code true}.
     */
    private static Callback<Boolean, Object> DUMMY_PRE_CALLBACK = new Callback<Boolean, Object>() {
        /**
         * {@inheritDoc}. This implementation always returns {@code true}.
         * 
         * @return {@code true}.
         */
        public Boolean call(Object t) {
            return true;
        }
    };

    /**
     * Callback object whose method does nothing.
     */
    private static VoidCallback<Object> DUMMY_POST_CALLBACK = new VoidCallback<Object>() {
        /**
         * {@inheritDoc}. This implementation does nothing.
         */
        public void call(Object t) {
        }
    };

    /**
     * A map that determines the kind of attribute to be created dependent on
     * the {@code Class} object passed to the {@code createAttribute} method.
     */
    private Map<Class<?>, AttributeFactoryFactory> map;

    /**
     * Constructs a {@code AttributeFactoryFactoryManager}.
     */
    public AttributeFactoryFactoryManager() {
        map = new HashMap<Class<?>, AttributeFactoryFactory>();
    }

    /**
     * Creates a new {@code Attribute} for the specified {@code Class} object
     * and with the specified id.
     * 
     * @param id
     *            the id of the attribute to be created.
     * @param clazz
     *            the {@code Class} object to determine the attribute to be
     *            created. Note that {@code clazz} does not represent the type
     *            of the returned attribute but is rather associated with it
     *            using a map.
     * @return a new {@code Attribute} for the specified {@code Class} object
     *         and with the specified id.
     * @see #map
     * @see #createAttribute(String, Class, Callback, VoidCallback)
     */
    public Attribute createAttribute(String id, Class<?> clazz) {
        return createAttribute(id, clazz, DUMMY_PRE_CALLBACK,
                DUMMY_POST_CALLBACK);
    }

    /**
     * Creates a new {@code Attribute} for the specified {@code Class} object
     * and with the specified id. All setter methods of the created {code
     * Attribute} feature the following structure:
     * <ol>
     * <li>Call {@code preCallback}.</li>
     * <li>Iff {@code preCallback} returns true, actually set the value.</li>
     * <li>Call {@code postCallback}.</li>
     * </ol>
     * 
     * @param id
     *            the id of the attribute to be created.
     * @param clazz
     *            the {@code Class} object to determine the attribute to be
     *            created. Note that {@code clazz} does not represent the type
     *            of the returned attribute but is rather associated with it
     *            using a map.
     * @param preCallback
     *            the callback to be called before the value of the returned
     *            attribute is actually set.
     * @param postCallback
     *            the callback to be called after the value of the returned
     *            attribute has actually been set.
     * @return a new {@code Attribute} for the specified {@code Class} object
     *         and with the specified id.
     * @see #map
     * @see #createAttribute(String, Class)
     */
    public Attribute createAttribute(String id, Class<?> clazz,
            Callback<Boolean, Object> preCallback,
            VoidCallback<Object> postCallback) {
        AttributeFactoryFactory aff = map.get(clazz);
        if (aff == null)
            return null;
        AttributeFactory af = aff.createAttributeFactory();
        af.setCallback(preCallback, postCallback);
        return af.createAttribute(id);
    }

    /**
     * Associates the specified types with the specified {@code
     * AttributeFactoryFactory}.
     * 
     * @param factory
     *            the {@code AttributeFactoryFactory} to create the
     *            {@link AttributeFactory} to create the desired {@code
     *            Attribute} if one of the specified {@code Class} objects are
     *            passed to {@code createAttribute}.
     * @param types
     *            the {@code Class} objects to be associated with the specified
     *            {@code AttributeFactoryFactory}.
     */
    public void add(AttributeFactoryFactory factory, Class<?>... types) {
        for (Class<?> type : types) {
            map.put(type, factory);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
