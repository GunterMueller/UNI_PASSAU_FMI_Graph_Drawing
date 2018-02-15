// =============================================================================
//
//   AttributeListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttributeListener.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

/**
 * Interfaces an attribute events listener.
 * 
 * @version $Revision: 5767 $
 * 
 * @see AttributeEvent
 */
public interface AttributeListener extends TransactionListener {
    /**
     * Called after an attribute has been added.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    void postAttributeAdded(AttributeEvent e);

    /**
     * Called after an attribute has been changed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    void postAttributeChanged(AttributeEvent e);

    /**
     * Called after an attribute has been removed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    void postAttributeRemoved(AttributeEvent e);

    /**
     * Called just before an attribute is added.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    void preAttributeAdded(AttributeEvent e);

    /**
     * Called before a change of an attribute takes place.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    void preAttributeChanged(AttributeEvent e);

    /**
     * Called just before an attribute is removed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    void preAttributeRemoved(AttributeEvent e);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
