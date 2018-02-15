package org.graffiti.plugins.scripting.js;

import org.graffiti.plugins.scripting.ScopeWrapper;
import org.graffiti.plugins.scripting.ScriptingContext;
import org.graffiti.plugins.scripting.delegate.ConstructorDelegate;
import org.graffiti.util.Callback;
import org.mozilla.javascript.NativeJavaClass;
import org.mozilla.javascript.ScriptableObject;

class RhinoScopeWrapper extends ScopeWrapper {
    private JavaScriptEngine engine;
    private ScriptableObject scope;

    public RhinoScopeWrapper(JavaScriptEngine engine, ScriptableObject scope) {
        super(engine);
        this.engine = engine;
        this.scope = scope;
    }

    @Override
    protected void addConstructor(ConstructorDelegate constructor) {
        throw new UnsupportedOperationException("addConstructor");
    }

    @Override
    protected void addNativeJavaClass(String name, Class<?> clazz) {
        scope.putConst(clazz.getSimpleName(), scope, new NativeJavaClass(scope,
                clazz));
    }

    @Override
    protected ScopeWrapper createChildScope() {
        return new RhinoScopeWrapper(engine, engine
                .execute(new Callback<ScriptableObject, ScriptingContext>() {
                    public ScriptableObject call(ScriptingContext context) {
                        ScriptableObject childScope = (ScriptableObject) ((RhinoContext) context)
                                .newObject(scope);
                        childScope.setPrototype(scope);
                        childScope.setParentScope(null);
                        return childScope;
                    }
                }));
    }

    protected ScriptableObject getScriptable() {
        return scope;
    }

    @Override
    protected void putWrapped(String name, Object wrappedValue) {
        scope.put(name, scope, wrappedValue);
    }

    @Override
    protected void putWrappedConst(String name, Object wrappedValue) {
        scope.putConst(name, scope, wrappedValue);
    }

    @Override
    protected void seal() {
        scope.sealObject();
    }
}
