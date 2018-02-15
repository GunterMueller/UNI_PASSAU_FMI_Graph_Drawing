// =============================================================================
//
//   SlotMap.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A container, which manages the association of {@link Slot}s with values. See
 * <a href="package-summary.html#ConceptOverview"> Overview of the
 * trigger/action paradigm</a>.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class SlotMap implements InSlotMap, OutSlotMap {
    /**
     * {@code SlotMap} backed by an empty, immutable map.
     */
    private static SlotMap emptyMap = new SlotMap(Collections
            .unmodifiableMap(new HashMap<Slot<?>, Observable<?>>()));

    /**
     * Maps from the slots to their assigned values encapsulated in an {@code
     * Observable}.
     */
    private Map<Slot<?>, Observable<?>> map;

    /**
     * Returns an empty, immutable {@code SlotMap}.
     * 
     * @return an empty, immutable {@code SlotMap}.
     */
    public static SlotMap emptyMap() {
        return emptyMap;
    }

    /**
     * Constructs a new, empty {@code SlotMap}.
     */
    public SlotMap() {
        map = new HashMap<Slot<?>, Observable<?>>();
    }

    /**
     * Constructs a new {@code SlotMap} that is backed by the specified {@code
     * Map}.
     * 
     * @param map
     *            the map that maintains the associations of slots with values.
     *            Must not be {@code null}.
     */
    private SlotMap(Map<Slot<?>, Observable<?>> map) {
        this.map = map;
    }

    /**
     * Returns the value associated with the slot specified by id. The preferred
     * way to query the value of a slot is to call the type safe
     * {@link #get(Slot)} method.
     * 
     * @param id
     *            the id of the slot whose associated value is to be returned.
     * @return the value associated with the slot specified by id or {@code
     *         null} if there is no such slot.
     */
    public Object get(String id) {
        Observable<?> observable = map.get(Slot.create(id));
        if (observable == null)
            return null;
        return observable.get();
    }

    /**
     * {@inheritDoc}
     */
    public boolean containsSlot(String id) {
        return map.containsKey(Slot.create(id));
    }

    /**
     * {@inheritDoc}
     */
    public <T> T get(Slot<T> slot) {
        Observable<?> observable = map.get(slot);
        if (observable == null) {
            if (slot.acceptsNull())
                return null;
            else {
                putDefaultValue(slot);
                observable = map.get(slot);
                if (observable == null)
                    throw new SlotAssignmentException(slot.getId());
            }
        }
        Object obj = observable.get();
        if (slot.acceptsValue(obj))
            return slot.getType().cast(obj);
        else
            throw new SlotAssignmentException(slot.getId(), obj.getClass(),
                    slot.getType());
    }

    /**
     * {@inheritDoc}
     */
    public <T> void put(Slot<T> slot, T value) {
        map.put(slot, new Observable<T>(value));
    }

    /**
     * {@inheritDoc}
     */
    public void putAll(SlotMap slotMap) {
        map.putAll(slotMap.map);
    }

    /**
     * Associates the specified slot by its default value.
     * 
     * @param <T>
     *            the type of the specified slot.
     * @param slot
     *            the slot that its default value is assigned to. It must be
     *            contained by this map.
     */
    public <T> void putDefaultValue(Slot<T> slot) {
        put(slot, slot.getDefaultValue());
    }

    /**
     * Assigns the specified value to the slot with the specified id. The
     * preferred way to assign a value to a slot is to call the type safe
     * {@link #put(Slot, Object)} method.
     * 
     * @param id
     *            the id of the slot that the value is to be assigned to. This
     *            map must contain a slot with that id.
     * @param value
     *            the value to be assigned to the specified slot. May be {@code
     *            null}.
     */
    public void put(String id, Object value) {
        map.put(Slot.create(id), new Observable<Object>(value));
    }

    /**
     * Adds an observer, which is notified when the specified slot is assigned a
     * new value.
     * 
     * @param <T>
     *            the type of the specified slot.
     * @param slot
     *            the slot to be observed by {@code observer}.
     * @param observer
     *            the observer that is to be notified when {@code slot} is
     *            assigned a new value.
     * @throws SlotAssignmentException
     *             if this map does not contain {@code slot}.
     */
    @SuppressWarnings("unchecked")
    public <T> void addObserver(Slot<T> slot, Observer<T> observer) {
        Observable<?> observable = map.get(slot);
        if (observable == null)
            throw new SlotAssignmentException(slot.getId());
        Object obj = observable.get();
        Class<T> slotType = slot.getType();
        if (slotType.isInstance(obj)) {
            ((Observable<T>) observable).addObserver(observer);
        } else
            throw new SlotAssignmentException(slot.getId(), obj.getClass(),
                    slotType);
    }

    /**
     * Removes all observers for the specified slot.
     * 
     * @param slot
     *            the slot the observers are removed from.
     * @throws SlotAssignmentException
     *             if this map does not contain {@code slot}.
     */
    public void clearObservers(Slot<?> slot) {
        Observable<?> observable = map.get(slot);
        if (observable == null)
            throw new SlotAssignmentException(slot.getId());
        observable.clearObservers();
    }

    /**
     * Returns the entry set of the {@code Map} backing this {@code SlotMap}.
     * 
     * @return the entry set of the {@code Map} backing this {@code SlotMap}.
     * @see Map#entrySet()
     */
    public Set<Map.Entry<Slot<?>, Observable<?>>> getEntrySet() {
        return map.entrySet();
    }

    /**
     * Removes all associations of slots with values.
     */
    public void clear() {
        map.clear();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
