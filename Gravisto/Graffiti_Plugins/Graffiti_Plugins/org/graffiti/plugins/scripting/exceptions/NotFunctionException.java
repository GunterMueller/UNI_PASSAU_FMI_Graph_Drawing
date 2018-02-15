package org.graffiti.plugins.scripting.exceptions;

import org.graffiti.plugins.scripting.ScriptingPlugin;

public class NotFunctionException extends ScriptingException {
    /**
     * 
     */
    private static final long serialVersionUID = -205194095072700830L;

    public NotFunctionException(String name) {
        super(ScriptingPlugin.format("error.noFunction", name));
    }
}
