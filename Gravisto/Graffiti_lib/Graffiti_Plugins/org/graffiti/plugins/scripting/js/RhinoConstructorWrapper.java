package org.graffiti.plugins.scripting.js;

import org.graffiti.plugins.scripting.delegate.ConstructorDelegate;
import org.graffiti.plugins.scripting.exceptions.NotConstructorException;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.Scriptable;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class RhinoConstructorWrapper extends RhinoWrapper implements Function {
    private ConstructorDelegate constructorDelegate;

    public RhinoConstructorWrapper(ConstructorDelegate delegate,
            JavaScriptEngine engine) {
        super(delegate, engine);
        constructorDelegate = delegate;
    }

    public Object call(Context cx, Scriptable sc, Scriptable thisObj,
            Object[] args) {
        throw engine.wrapException(new NotConstructorException(
                constructorDelegate.getName()));
    }

    public Scriptable construct(Context cx, Scriptable sc, final Object[] args) {
        return guard(new GuardedMethod<Scriptable>() {
            @Override
            public Scriptable execute() throws ScriptingException {
                return (Scriptable) engine.wrap(constructorDelegate
                        .construct(engine.unwrap(args)));
            }
        });
    }
}
