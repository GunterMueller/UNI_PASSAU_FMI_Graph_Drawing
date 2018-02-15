// =============================================================================
//
//   TestAttrListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TestAttrListener.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.event;

import java.util.LinkedList;
import java.util.logging.Logger;

import org.graffiti.event.AbstractEvent;
import org.graffiti.event.AttributeEvent;
import org.graffiti.event.AttributeListener;
import org.graffiti.event.TransactionEvent;

/**
 * Auxiliary test class to examine the functionality of ListenerManager.
 * 
 * @version $Revision: 5771 $
 */
public class TestAttrListener implements AttributeListener {

    /** The logger for the current class. */
    private static final Logger logger = Logger
            .getLogger(TestAttrListener.class.getName());

    /** Contains a list of events in the same order as the methodCalled list */
    public LinkedList<AbstractEvent> events = new LinkedList<AbstractEvent>();

    /** Contains a list of methods, which have been called. */
    public LinkedList<String> methodsCalled = new LinkedList<String>();

    /**
     * Contains the name of the method, that has been called by the
     * <code>ListenerManager</code>.
     */
    public String lastMethodCalled = "";

    /** Contains the number of times, a listener method has been called. */
    public int called = 0;

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postAttributeAdded(AttributeEvent e) {
        methodCalled("postAttributeAdded", e);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postAttributeChanged(AttributeEvent e) {
        methodCalled("postAttributeChanged", e);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void postAttributeRemoved(AttributeEvent e) {
        methodCalled("postAttributeRemoved", e);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preAttributeAdded(AttributeEvent e) {
        methodCalled("preAttributeAdded", e);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preAttributeChanged(AttributeEvent e) {
        methodCalled("preAttributeChanged", e);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void preAttributeRemoved(AttributeEvent e) {
        methodCalled("preAttributeRemoved", e);
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    @Override
    public String toString() {
        return methodsCalled.toString();
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void transactionFinished(TransactionEvent e) {
        methodCalled("transactionFinished", e);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param e
     *            DOCUMENT ME!
     */
    public void transactionStarted(TransactionEvent e) {
        methodCalled("transactionStarted", e);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param method
     *            DOCUMENT ME!
     * @param e
     *            DOCUMENT ME!
     */
    private void methodCalled(String method, AbstractEvent e) {
        called++;
        lastMethodCalled = method;
        methodsCalled.add(method);
        events.add(e);
        logger.info(method + " called " + called + " times.");
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
