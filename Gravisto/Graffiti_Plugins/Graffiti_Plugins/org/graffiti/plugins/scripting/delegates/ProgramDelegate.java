package org.graffiti.plugins.scripting.delegates;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.plugins.scripting.DefaultDocumentation;
import org.graffiti.plugins.scripting.ProgramScope;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptedMethod;
import org.graffiti.plugins.scripting.reflect.DocumentedDelegate;

/**
 * @scripted The program.
 * @author Andreas Glei&szlig;ner
 */
@DocumentedDelegate(DefaultDocumentation.class)
public class ProgramDelegate extends ObjectDelegate {
    public ProgramDelegate(ProgramScope scope) {
        super(scope);
    }

    /**
     * Closes the program.
     * 
     * @scripted Closes the program.
     */
    @ScriptedMethod
    public void exit() {
        GraffitiSingleton.getInstance().getMainFrame().dispose();
    }

    @Override
    public String toString() {
        return "[Program]";
    }
}
