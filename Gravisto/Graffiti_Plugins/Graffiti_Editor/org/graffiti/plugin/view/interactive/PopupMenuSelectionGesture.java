// =============================================================================
//
//   PopupMenuSelectionGesture.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

/**
 * {@code UserGesture} representing the selection of an item in or the
 * cancellation of a popup menu. It is provided for convenience and its use by
 * {@link InteractiveView}s is not prescribed.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see PopupMenuItem
 */
public class PopupMenuSelectionGesture implements UserGesture {
    /**
     * The id of {@code PopupMenuSelectionGesture}s representing the
     * cancellation of a popup menu.
     * 
     * @see #getId()
     * @see #PopupMenuSelectionGesture()
     */
    public static final String CANCEL_STRING = "cancel";

    /**
     * The id of this {@code PopupMenuSelectionGesture}. It is the id of the
     * {@link PopupMenuItem} whose selection is represented by this or
     * {@link #CANCEL_STRING} if this represents the cancellation of a popup
     * menu.
     */
    private String id;

    /**
     * The slots attached to the {@link PopupMenuItem} whose selection is
     * represented by this.
     * 
     * @see PopupMenuItem#getSlots()
     */
    private SlotMap slots;

    /**
     * Constructs a {@code PopupMenuItem} representing the selection of the
     * specified popup menu item.
     * 
     * @param item
     *            the popup menu item whose selection is represented by the
     *            {@code PopupMenuSelectionGesture} to construct.
     */
    public PopupMenuSelectionGesture(PopupMenuItem item) {
        id = item.getId();
        slots = item.getSlots();
    }

    /**
     * Constructs a {@code PopupMenuItem} representing the cancellation of a
     * popup menu. Its id will be {@link #CANCEL_STRING}.
     */
    public PopupMenuSelectionGesture() {
        id = CANCEL_STRING;
        slots = SlotMap.emptyMap();
    }

    /**
     * Returns the id of the {@code PopupMenuItem} whose selection is
     * represented by this.
     * 
     * @return the id of the {@link PopupMenuItem} whose selection is
     *         represented by this or {@link #CANCEL_STRING} if this represents
     *         the cancellation of a popup menu.
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the slots attached to the {@code PopupMenuItem} whose selection
     * is represented by this.
     * 
     * @return the slots attached to the {@link PopupMenuItem} whose selection
     *         is represented by this or an empty {@link SlotMap} if this
     *         represents the cancellation of a popup menu.
     */
    public SlotMap getSlots() {
        return slots;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
