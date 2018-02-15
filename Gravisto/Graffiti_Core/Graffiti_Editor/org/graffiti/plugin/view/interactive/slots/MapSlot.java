// =============================================================================
//
//   MapSlot.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive.slots;

import java.util.Map;

import org.graffiti.plugin.view.interactive.Slot;

/**
 * Slot of type {@code Map}. The intention of this class is to provide an
 * {@link #acceptsValue(Object)} method that is strictly type safe despite of
 * generic type erasure.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @param <K>
 *            the type of keys to be maintained by accepted maps.
 * @param <V>
 *            the type of values to be mapped by accepted maps.
 */
public class MapSlot<K, V> extends Slot<Map<K, V>> {
    /**
     * {@code Class} object modeling the type of keys to be maintained by
     * accepted maps.
     */
    private Class<K> keyType;

    /**
     * {@code Class} object modeling the type of keys to be maintained by
     * accepted maps.
     */
    private Class<V> valueType;

    /**
     * Constructs a {@code MapSlot}.
     * 
     * @param id
     *            the id of the slot.
     * @param keyType
     *            the {@code Class} object modeling the type of keys to be
     *            maintained by accepted maps.
     * @param valueType
     *            the {@code Class} object modeling the type of keys to be
     *            maintained by accepted maps.
     */
    @SuppressWarnings("unchecked")
    public MapSlot(String id, Class<K> keyType, Class<V> valueType) {
        super(id, (Class<Map<K, V>>) Map.class.asSubclass(Map.class), false);
    }

    /**
     * {@inheritDoc}
     * 
     * This implementation checks both if the specified value is a {@link Map}
     * and if each of the keys and values contained in the specified map is an
     * instance of the desired type.
     */
    @Override
    public boolean acceptsValue(Object value) {
        if (!(value instanceof Map<?, ?>))
            return false;
        Map<?, ?> map = (Map<?, ?>) value;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!keyType.isInstance(entry.getKey())
                    || !valueType.isInstance(entry.getValue()))
                return false;
        }
        return true;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
