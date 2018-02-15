package org.graffiti.plugins.scripting.exceptions;

import org.graffiti.plugins.scripting.ScriptingPlugin;

public class FieldAccessException extends ScriptingException {
    /**
     * 
     */
    private static final long serialVersionUID = -4917340023396631233L;

    public FieldAccessException(boolean isGet) {
        super(ScriptingPlugin.format(isGet ? "error.noFieldGet"
                : "error.noFieldSet"));
    }
}
