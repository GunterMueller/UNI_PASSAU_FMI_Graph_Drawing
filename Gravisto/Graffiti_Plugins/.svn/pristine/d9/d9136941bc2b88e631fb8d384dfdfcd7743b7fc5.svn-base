// =============================================================================
//
//   OutSlotMap.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

/**
 * Classes implementing {@code OutSlotMap} allow to assign values to
 * {@link Slot}s.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see InSlotMap
 * @see SlotMap
 */
public interface OutSlotMap {
    /**
     * Assigns the specified value to the specified slot.
     * 
     * @param <T>
     *            the type of the slot.
     * @param slot
     *            the slot the value is to be assigned to. It must be contained
     *            by this map.
     * @param value
     *            the value to be assigned to the specified slot. May be {@code
     *            null}.
     */
    public <T> void put(Slot<T> slot, T value);

    /**
     * Copies all slot assignments from {@code slotMap} to this map.
     * 
     * @param slotMap
     *            the {@code SlotMap} that the assignments are taken from. Must
     *            not be {@code null}.
     */
    public void putAll(SlotMap slotMap);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
