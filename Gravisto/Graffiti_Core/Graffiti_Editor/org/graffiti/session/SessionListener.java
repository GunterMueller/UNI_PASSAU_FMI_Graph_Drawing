// =============================================================================
//
//   SessionListener.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SessionListener.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.session;

/**
 * Interface for all who want to be noticed when the session changes.
 * 
 * @version $Revision: 5768 $
 * 
 * @see org.graffiti.session.Session
 */
public interface SessionListener {

    /**
     * This method is called when the session changes.
     * 
     * @param s
     *            the new Session.
     */
    public void sessionChanged(Session s);

    /**
     * This method is called when the data (except the graph data) are changed.
     */
    public void sessionDataChanged(Session s);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
