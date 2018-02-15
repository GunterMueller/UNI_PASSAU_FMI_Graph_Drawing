// =============================================================================
//
//   ViewListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ViewListener.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.view;

/**
 * Interface for all who want to be noticed when a different view becomes
 * active. The events that implementors get are disjoint from those that
 * <code>SessionLister</code>s get.
 * 
 * @version $Revision: 5768 $
 * 
 * @see org.graffiti.session.Session
 */
public interface ViewListener {

    /**
     * This method is called when the view changes. This method is not called
     * when another session is activated. Implement <code>SessionListener</code>
     * if you are interested in session changed events.
     * 
     * @param newView
     *            the new View.
     */
    public void viewChanged(View newView);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
