// =============================================================================
//
//   TransactionEvent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TransactionEvent.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

import java.util.Set;

/**
 * Contains a transaction event. A <code>TransactionEvent</code> object is
 * passed to every <code>TransactionListener</code> object which is registered
 * to receive a transaction event.
 * 
 * @version $Revision: 5767 $
 * 
 * @see TransactionListener
 */
public class TransactionEvent extends AbstractEvent {
    /**
     * 
     */
    private static final long serialVersionUID = -4527980119030361674L;
    /**
     * Contains the objects that have been changed during the lifetime of a
     * transaction.
     */
    private Set<Object> changedObjects;

    /**
     * Constructs a transaction event object with the specified source
     * component.
     * 
     * @param source
     *            the source component of the transaction.
     * @param changedObjects
     *            DOCUMENT ME!
     */
    public TransactionEvent(Object source, Set<Object> changedObjects) {
        this(source);
        this.changedObjects = changedObjects;
    }

    /**
     * Constructs a transaction event object with the specified source
     * component.
     * 
     * @param source
     *            the graph that originated the event.
     */
    public TransactionEvent(Object source) {
        super(source);
        changedObjects = null;
    }

    /**
     * Returns the <code>Set</code> of objects that have been changed during the
     * transaction.
     * 
     * @return the graph that originated this event.
     */
    public Set<Object> getChangedObjects() {
        return changedObjects;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
