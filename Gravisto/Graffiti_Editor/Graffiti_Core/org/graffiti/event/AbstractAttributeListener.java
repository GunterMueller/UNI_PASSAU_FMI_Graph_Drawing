// =============================================================================
//
//   AbstractAttributeListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractAttributeListener.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

/**
 * An abstract adapter class for receiving attribute events. The methods in this
 * class are empty. This class exists as convenience for creating listener
 * objects.
 * 
 * <p>
 * Extend this class to create a <code>AttributeEvent</code> listener and
 * override the methods for the events of interest. (If you implement the
 * <code>AttributeListener</code> interface, you have to define all of the
 * methods in it. This abstract class defines <code>null</code> methods for them
 * all, so you can only have to define methods for events you care about.)
 * </p>
 * 
 * <p>
 * Create a listener object using the extended class and then register it with a
 * component using the component's <code>addAttributeEventListener</code>
 * method. When an attribute is added, removed or changed or a transaction of
 * attribute changes is started or finished, the relevant method in the listener
 * object is invoked and the <code>AttributeEvent</code> is passed to it.
 * </p>
 * 
 * @version $Revision: 5767 $
 * 
 * @see ListenerManager
 * @see AttributeEvent
 */
public abstract class AbstractAttributeListener implements AttributeListener {
    /**
     * Called after an attribute has been added.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void postAttributeAdded(AttributeEvent e) {
    }

    /**
     * Called after an attribute has been changed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void postAttributeChanged(AttributeEvent e) {
    }

    /**
     * Called after an attribute has been removed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void postAttributeRemoved(AttributeEvent e) {
    }

    /**
     * Called just before an attribute is added.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void preAttributeAdded(AttributeEvent e) {
    }

    /**
     * Called before a change of an attribute takes place.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void preAttributeChanged(AttributeEvent e) {
    }

    /**
     * Called just before an attribute is removed.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    public void preAttributeRemoved(AttributeEvent e) {
    }

    /**
     * Called after a transaction has been finished.
     * 
     * @param e
     *            gives details about the transaction.
     */
    public void transactionFinished(TransactionEvent e) {
    }

    /**
     * Called after a transaction has been started.
     * 
     * @param e
     *            gives details about the transaction.
     */
    public void transactionStarted(TransactionEvent e) {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
