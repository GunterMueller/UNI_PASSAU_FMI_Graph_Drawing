// =============================================================================
//
//   SlotTypeException.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import org.graffiti.core.Bundle;

/**
 * {@code RuntimeException} that is thrown to indicate that a value and a slot
 * are not compatible.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see Slot#acceptsValue(Object)
 */
public class SlotAssignmentException extends RuntimeException {
    /**
     * 
     */
    private static final long serialVersionUID = 4534094596376331251L;

    /**
     * Constructs a {@code SlotAssignmentException}.
     * 
     * @param id
     *            the id of the incompatible slot.
     * @param actualClass
     *            the {@code Class} object representing the type of the
     *            incompatible value.
     * @param targetClass
     *            the {@code Class} object representing the type of the
     *            incompatible slot.
     */
    public SlotAssignmentException(String id, Class<?> actualClass,
            Class<?> targetClass) {
        super(String.format(Bundle.getBundle(SlotAssignmentException.class)
                .getString("type"), id, actualClass.getName(), targetClass
                .getName()));
    }

    /**
     * Constructs a new {@code SlotAssignmentException}.
     * 
     * @param id
     *            the id of the incompatible slot.
     */
    public SlotAssignmentException(String id) {
        super(String.format(Bundle.getBundle(SlotAssignmentException.class)
                .getString("null"), id));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
