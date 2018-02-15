package org.graffiti.plugins.scripting.js;

import org.graffiti.plugins.scripting.delegate.FunctionDelegate;
import org.graffiti.plugins.scripting.exceptions.NotConstructorException;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

public class RhinoFunctionWrapper extends RhinoWrapper implements Function {
    private FunctionDelegate functionDelegate;

    public RhinoFunctionWrapper(FunctionDelegate delegate,
            JavaScriptEngine engine) {
        super(delegate, engine);
        functionDelegate = delegate;
    }

    public Object call(Context cx, Scriptable sc, final Scriptable thisObj,
            final Object[] args) {
        return guard(new GuardedMethod<Object>() {
            @Override
            public Object execute() throws ScriptingException {
                return engine.wrap(functionDelegate.call(engine.unwrap(args)));
            }
        });
    }

    public Scriptable construct(Context cx, Scriptable sc, Object[] args) {
        throw engine.wrapException(new NotConstructorException(functionDelegate
                .getName()));
    }
}
