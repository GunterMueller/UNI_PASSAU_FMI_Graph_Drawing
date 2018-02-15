package org.graffiti.util.ext;

import java.util.NoSuchElementException;

/**
 * Extension class for hash-maps.
 * 
 * @author Harald Frankenberger
 */
public class HashMap<K, V> extends java.util.HashMap<K, V> implements Map<K, V> {

    /**
     * 
     */
    private static final long serialVersionUID = 3290748258335178984L;

    /**
     * Default-constructor.
     */
    public HashMap() {
        super();
    }

    /**
     * Associates the specified value with the specified key in this map if the
     * specified condition holds.
     * 
     * @param condition
     *            the condition that must hold, so that the specified key is
     *            associated with the specified value.
     * @param key
     *            key with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified key
     */
    public void putIf(boolean condition, K key, V value) {
        if (condition) {
            put(key, value);
        }
    }

    /**
     * Associates the specified value with the specified key in this map if the
     * specified condition does not hold.
     * 
     * @param condition
     *            the condition that must not hold, so that the specified key is
     *            associated with the specified value.
     * @param key
     *            key with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified key
     */
    public void putIfNot(boolean condition, K key, V value) {
        putIf(!condition, key, value);
    }

    /**
     * Associates the specified value with the specified key in this map if the
     * specified key is not present in this map.
     * 
     * @param key
     *            key with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified key
     */
    public void putIfNotPresent(K key, V value) {
        putIfNot(containsKey(key), key, value);
    }

    /**
     * Returns the value to which the specified key is mapped if the specified
     * key is present in this map.
     * 
     * @param key
     *            the key whose associated value is to be returned
     * @throws NoSuchElementException
     *             if the specified key is not present in this map.
     */
    public V getIfPresent(K key) {
        V value = get(key);
        if (value == null)
            throw new NoSuchElementException();
        return value;
    }

}
