package org.graffiti.plugins.scripting.exceptions;

import org.graffiti.plugins.scripting.ScriptingPlugin;

public class IllegalConstructorDefinitionException extends ScriptingException {
    /**
     * 
     */
    private static final long serialVersionUID = -5668045045501151592L;

    public IllegalConstructorDefinitionException(String name) {
        super(ScriptingPlugin.format("error.illegalConstructor", name));
    }
}
