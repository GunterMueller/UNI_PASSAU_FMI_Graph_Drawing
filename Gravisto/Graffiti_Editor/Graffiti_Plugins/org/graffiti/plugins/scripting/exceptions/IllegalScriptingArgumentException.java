package org.graffiti.plugins.scripting.exceptions;

import org.graffiti.plugins.scripting.ScriptingPlugin;

public class IllegalScriptingArgumentException extends ScriptingException {
    /**
     * 
     */
    private static final long serialVersionUID = 377339945110000295L;

    public IllegalScriptingArgumentException(String name) {
        super(ScriptingPlugin.format("error.argument", name));
    }

    public IllegalScriptingArgumentException(String name, String message) {
        super(ScriptingPlugin.format("error.argumentExt", name, message));
    }
}
