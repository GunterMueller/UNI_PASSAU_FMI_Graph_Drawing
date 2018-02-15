package org.graffiti.plugins.scripting.exceptions;

import org.graffiti.plugins.scripting.ScriptingPlugin;

public class WrongSignatureException extends ScriptingException {
    /**
     * 
     */
    private static final long serialVersionUID = -7860956787342095252L;

    public WrongSignatureException(String name) {
        this(name, false);
    }

    public WrongSignatureException(String name, boolean isConstructor) {
        super(ScriptingPlugin.format(
                isConstructor ? "error.wrongConstructorSignature"
                        : "error.wrongFunctionSignature", name));
    }
}
