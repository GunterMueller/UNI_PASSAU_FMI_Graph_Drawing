package org.graffiti.util.ext;

import java.util.NoSuchElementException;

/**
 * Extension interface for maps.
 * 
 * @author Harald Frankenberger
 */
public interface Map<K, V> extends java.util.Map<K, V> {
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
    void putIf(boolean condition, K key, V value);

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
    void putIfNot(boolean condition, K key, V value);

    /**
     * Associates the specified value with the specified key in this map if the
     * specified key is not present in this map.
     * 
     * @param key
     *            key with which the specified value is to be associated
     * @param value
     *            value to be associated with the specified key
     */
    void putIfNotPresent(K key, V value);

    /**
     * Returns the value to which the specified key is mapped if the specified
     * key is present in this map.
     * 
     * @param key
     *            the key whose associated value is to be returned
     * @throws NoSuchElementException
     *             if the specified key is not present in this map.
     */
    V getIfPresent(K key);

}
