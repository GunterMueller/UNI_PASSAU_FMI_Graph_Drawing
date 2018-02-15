package org.graffiti.plugins.scripting.exceptions;

import org.graffiti.plugins.scripting.ScriptingPlugin;

public class FieldTypeException extends ScriptingException {
    /**
     * 
     */
    private static final long serialVersionUID = -4262785210267309036L;

    public FieldTypeException(String name) {
        super(ScriptingPlugin.format("error.fieldType", name));
    }
}
