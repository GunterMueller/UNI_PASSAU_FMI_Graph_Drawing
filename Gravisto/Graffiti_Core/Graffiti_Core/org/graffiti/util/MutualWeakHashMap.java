// =============================================================================
//
//   MutualWeakHashMap.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util;

import java.lang.ref.WeakReference;
import java.util.WeakHashMap;

/**
 * A hashtable-based {@code Map} implementation with both weak keys and values.
 * This map extends {@code WeakHashMap} and additional wraps the values in
 * {@link WeakReference}s.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see WeakHashSet
 */
public class MutualWeakHashMap<K extends MutuallyReferable, V> extends
        WeakHashMap<K, WeakReference<V>> {
    /**
     * Associates the specified value with the specified key in this map. If the
     * map previously contained a mapping for this key, the old value is
     * replaced.
     */
    public void put(K key, V value) {
        key.addReference(value);
        put(key, new WeakReference<V>(value));
    }

    /**
     * Returns the value to which the specified key is mapped, or {@code null}
     * if this map contains no mapping for the key.
     * 
     * @return the value to which the specified key is mapped, or {@code null}
     *         if this map contains no mapping for the key.
     */
    public V get(K key) {
        WeakReference<V> ref = get((Object) key);
        if (ref == null)
            return null;
        return ref.get();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
