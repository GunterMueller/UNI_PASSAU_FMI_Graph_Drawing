// =============================================================================
//
//   InSlotMap.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

/**
 * Classes implementing {@code InSlotMap} allow to query the values associated
 * with {@link Slot}s.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see OutSlotMap
 * @see SlotMap
 */
public interface InSlotMap {
    /**
     * Returns if there is a slot with the specified id.
     * 
     * @param id
     *            the id of the slot in question.
     * @return {@code true} if there is a slot with the specified id.
     */
    public boolean containsSlot(String id);

    /**
     * Returns the value associated with the specified slot.
     * 
     * @param <T>
     *            the type of the slot.
     * @param slot
     *            the slot whose associated value is to be returned. It must be
     *            contained by this map.
     * @return the value associated with the specified slot.
     */
    public <T> T get(Slot<T> slot);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
