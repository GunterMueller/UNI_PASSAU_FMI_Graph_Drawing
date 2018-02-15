package org.graffiti.plugins.scripting.exceptions;

import org.graffiti.plugins.scripting.ScriptingPlugin;

public class IncompatibleFieldTypeException extends ScriptingException {
    /**
     * 
     */
    private static final long serialVersionUID = -6491708075536241925L;

    public IncompatibleFieldTypeException() {
        super(ScriptingPlugin.format("error.fieldType"));
    }
}
