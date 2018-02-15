package org.graffiti.plugins.scripting.js;

import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.ScopeWrapper;
import org.graffiti.plugins.scripting.Script;
import org.graffiti.plugins.scripting.ScriptingContext;
import org.graffiti.plugins.scripting.ScriptingEngine;
import org.graffiti.plugins.scripting.delegate.BlackBoxDelegate;
import org.graffiti.plugins.scripting.delegate.ConstructorDelegate;
import org.graffiti.plugins.scripting.delegate.FunctionDelegate;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.Undefined;
import org.graffiti.util.Callback;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.ScriptableObject;

public final class JavaScriptEngine extends ScriptingEngine {
    public static final String LANGUAGE_ID = "JavaScript1.7";

    private static final String NAME = JavaScriptPlugin.getString("name");

    /**
     * The plugin providing this engine.
     */
    protected static JavaScriptPlugin plugin;

    private RhinoContextFactory factory;

    private ScriptableObject rootScope;

    /**
     * Returns the {@code JavaScriptEngine} singleton.
     * 
     * @return the {@code JavaScriptEngine} singleton.
     * @throws IllegalStateException
     *             if the {@link JavaScriptPlugin} is not loaded.
     */
    public static JavaScriptEngine get() {
        if (plugin != null) {
            JavaScriptEngine engine = plugin.getEngine();
            if (engine != null)
                return engine;
        }
        throw new IllegalStateException("Requires JavaScriptEngine plugin.");
    }

    protected JavaScriptEngine(JavaScriptPlugin plugin) {
        JavaScriptEngine.plugin = plugin;
        factory = new RhinoContextFactory();
    }

    @Override
    protected ScopeWrapper createRootScope() {
        return new RhinoScopeWrapper(this,
                execute(new Callback<ScriptableObject, ScriptingContext>() {
                    public ScriptableObject call(ScriptingContext context) {
                        rootScope = ((RhinoContext) context)
                                .initStandardObjects();
                        return rootScope;
                    }
                }));
    }

    @Override
    public Script createScript(String source, Scope scope) {
        return new RhinoScript(this, source, scope);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RhinoConsole createConsole(Scope scope) {
        return new RhinoConsole(this, scope);
    }

    @Override
    public <T> Object createWrapper(BlackBoxDelegate<T> delegate) {
        if (rootScope == null)
            throw new IllegalStateException("No root scope");
        return Context.javaToJS(delegate.getObject(), rootScope);
    }

    @Override
    public Object createWrapper(ConstructorDelegate delegate) {
        return new RhinoConstructorWrapper(delegate, this);
    }

    @Override
    public Object createWrapper(FunctionDelegate delegate) {
        return new RhinoFunctionWrapper(delegate, this);
    }

    @Override
    public Object createWrapper(ObjectDelegate delegate) {
        return new RhinoWrapper(delegate, this);
    }

    @Override
    protected RhinoContext enterContext() {
        return (RhinoContext) factory.enterContext();
    }

    @Override
    protected void exitContext() {
        Context.exit();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return NAME;
    }

    public boolean isValid(final String source, Scope scope) {
        return execute(new Callback<Boolean, ScriptingContext>() {
            public Boolean call(ScriptingContext context) {
                return ((RhinoContext) context).stringIsCompilableUnit(source);
            }
        });
    }

    @Override
    public Object wrap(Boolean bool) {
        return bool;
    }

    @Override
    public Object wrap(Number number) {
        return number;
    }

    @Override
    public Object wrap(String string) {
        return string;
    }

    @Override
    public Object wrap(Undefined undefined) {
        return Context.getUndefinedValue();
    }

    @Override
    protected Object wrapNull() {
        return null;
    }

    protected EvaluatorException wrapException(Throwable e) {
        return new EvaluatorException(e.getLocalizedMessage());
    }

    @Override
    public Object unwrap(Object object) {
        if ((object instanceof Boolean) || (object instanceof Number)
                || (object instanceof String) || object == null)
            return object;
        else if (object instanceof RhinoWrapper)
            return ((RhinoWrapper) object).getDelegate();
        else if (object instanceof NativeArray) {
            NativeArray na = (NativeArray) object;
            int len = (int) na.getLength();
            Object[] array = new Object[len];
            for (int i = 0; i < len; i++) {
                array[i] = unwrap(na.get(i, na));
            }
            return array;
        }
        return Context.jsToJava(object, Object.class);
    }
}
