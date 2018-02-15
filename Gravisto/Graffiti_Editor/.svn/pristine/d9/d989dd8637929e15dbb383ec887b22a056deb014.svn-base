package org.graffiti.plugins.scripting.delegates;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.SessionScope;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;
import org.graffiti.session.Session;

/**
 * @scripted The session.
 * @author Andreas Glei&szlig;ner
 */
@DocumentedDelegate(DefaultDocumentation.class)
public class SessionDelegate extends ObjectDelegate {
    private Session session;

    public SessionDelegate(SessionScope scope, Session session) {
        super(scope);
        this.session = session;
    }

    @ScriptedMethod
    public void close() {
        GraffitiSingleton.getInstance().getMainFrame().removeSession(session);
    }

    @Override
    public String toString() {
        return "[Session]";
    }
}
