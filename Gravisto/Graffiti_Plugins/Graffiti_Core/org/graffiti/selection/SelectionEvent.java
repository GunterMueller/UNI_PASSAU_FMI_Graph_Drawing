// =============================================================================
//
//   SelectionEvent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SelectionEvent.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.selection;

import org.graffiti.event.AbstractEvent;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 5767 $
 */
public class SelectionEvent extends AbstractEvent {

    /**
     * 
     */
    private static final long serialVersionUID = 1549429891485378799L;
    /** DOCUMENT ME! */
    private boolean added;

    /**
     * Constructs a new <code>SelectionEvent</code>.
     * 
     * @param selection
     *            the (new / updated) selection.
     */
    public SelectionEvent(Selection selection) {
        super(selection);
    }

    /**
     * Sets the added.
     * 
     * @param added
     *            The added to set
     */
    public void setAdded(boolean added) {
        this.added = added;
    }

    /**
     * Returns the selection contained in the event.
     * 
     * @return the selection contained in the event.
     */
    public Selection getSelection() {
        return (Selection) getSource();
    }

    /**
     * Returns the added.
     * 
     * @return boolean
     */
    public boolean toBeAdded() {
        return added;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
