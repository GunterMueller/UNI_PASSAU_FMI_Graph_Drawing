// =============================================================================
//
//   SessionManager.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SessionManager.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.session;

import java.util.Iterator;

/**
 * Manages the session objects.
 * 
 * @see org.graffiti.session.Session
 */
public interface SessionManager {

    /**
     * Returns the current active session.
     * 
     * @return the current active session.
     */
    public Session getActiveSession();

    /**
     * Returns <code>true</code>, if a session is active.
     * 
     * @return <code>true</code>, if a session is active.
     */
    public boolean isSessionActive();

    /**
     * Returns an iterator over all sessions.
     * 
     * @return an iterator over all sessions.
     */
    public Iterator<Session> getSessionsIterator();

    /**
     * Adds the given session to the list of sessions.
     * 
     * @param es
     *            the new session to add.
     */
    public void addSession(Session es);

    /**
     * Adds a <code>SelectionListener</code>.
     */
    public void addSessionListener(SessionListener sl);

    /**
     * Called, if the session or data (except graph data) in the session have
     * been changed.
     */
    public void fireSessionDataChanged(Session session);

    /**
     * Removes the given session from the list of sessions.
     * 
     * @param es
     *            the session to remove from the list.
     * @return <code>true</code> if the remove operation was successful
     */
    public boolean removeSession(Session es);

    /**
     * Removes a <code>SelectionListener</code>.
     */
    public void removeSessionListener(SessionListener sl);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
