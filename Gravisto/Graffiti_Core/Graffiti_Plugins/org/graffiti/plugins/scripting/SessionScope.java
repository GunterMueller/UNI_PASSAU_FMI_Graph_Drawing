package org.graffiti.plugins.scripting;

import org.graffiti.plugins.scripting.delegates.GraphDelegate;
import org.graffiti.plugins.scripting.delegates.GridDelegate;
import org.graffiti.plugins.scripting.delegates.SelectionDelegate;
import org.graffiti.plugins.scripting.delegates.SessionDelegate;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;

/**
 * Scope providing access to a session, its graph and selection.
 */
public class SessionScope extends Scope {
    /**
     * The session.
     */
    private Session session;

    /**
     * Constructs a {@code Session} providing access to the specified session
     * and with the specified parent.
     * 
     * @param programScope
     *            the parent of the scope to construct.
     * @param session
     *            the session accessed from the scope to construct.
     */
    SessionScope(ProgramScope programScope, Session session) {
        super(programScope);
        this.session = session;

        addDelegateClass(GridDelegate.class);
        putConst("graph", getCanonicalDelegate(session.getGraph(),
                new GraphDelegate.Factory(this)));
        putConst("session", new SessionDelegate(this, session));

        if (session instanceof EditorSession) {
            putConst("selection", new SelectionDelegate(this,
                    (EditorSession) session));
        }

    }

    /**
     * Returns the session.
     * 
     * @return the sessopn.
     */
    public Session getSession() {
        return session;
    }
}
