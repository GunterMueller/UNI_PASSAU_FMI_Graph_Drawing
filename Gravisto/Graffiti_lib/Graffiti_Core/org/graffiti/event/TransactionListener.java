// =============================================================================
//
//   TransactionListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TransactionListener.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.event;

import java.util.EventListener;

/**
 * Interface, that contains methods which are called when transactions are
 * started or finished.
 * 
 * @version $Revision: 5767 $
 */
public interface TransactionListener extends EventListener {
    /**
     * Called when a transaction has stopped.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void transactionFinished(TransactionEvent e);

    /**
     * Called when a transaction has started.
     * 
     * @param e
     *            the EdgeEvent detailing the changes.
     */
    public void transactionStarted(TransactionEvent e);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
