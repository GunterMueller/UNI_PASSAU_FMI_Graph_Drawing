package org.graffiti.plugins.scripting.js;

import org.graffiti.plugins.scripting.ResultCallback;
import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.Script;
import org.graffiti.plugins.scripting.ScriptingContext;
import org.graffiti.plugins.scripting.ScriptingTimeout;
import org.graffiti.util.Callback;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.Undefined;

public class RhinoScript implements Script {
    private JavaScriptEngine engine;
    private String source;
    private Scope scope;

    protected RhinoScript(JavaScriptEngine engine, String source, Scope scope) {
        this.engine = engine;
        this.source = source;
        this.scope = scope;
    }

    public void execute(final ResultCallback callback) {
        engine.execute(new Callback<Object, ScriptingContext>() {
            public Object call(ScriptingContext c) {
                try {
                    RhinoContext context = (RhinoContext) c;
                    RhinoScopeWrapper wrapper = (RhinoScopeWrapper) scope
                            .getScopeWrapper(engine);
                    Scriptable scriptable = wrapper.getScriptable();
                    Object result = context.evaluateString(scriptable, source,
                            "<cmd>", 1, null);
                    if (!scope.isSealed()) {
                        scriptable.put("$", scriptable, result);
                    }
                    if (result instanceof Undefined) {
                        callback.reportResult();
                    } else {
                        callback.reportResult(Context.toString(result));
                    }
                } catch (Exception e) {
                    callback.reportError(e.getLocalizedMessage());
                } catch (ScriptingTimeout e) {
                    callback.reportError(e.getLocalizedMessage());
                }
                return null;
            }
        });
    }
}
