package org.graffiti.plugins.scripting.delegate;

import java.util.HashMap;
import java.util.Map;

import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.ScriptingEngine;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.graffiti.plugins.scripting.reflect.MemberDesc;
import org.graffiti.plugins.scripting.reflect.ObjectDesc;

public class ObjectDelegate extends ReflectiveDelegate {
    protected class CanonicalDelegate<S extends ObjectDelegate, T> extends
            DelegateFactory<S, T> {
        private DelegateFactory<S, T> factory;

        public CanonicalDelegate(DelegateFactory<S, T> factory,
                Class<S> delegateClass) {
            super(factory.scope, delegateClass);
            this.factory = factory;
        }

        @Override
        public S create(T object) {
            return scope.getCanonicalDelegate(object, factory);
        }
    };

    protected final Scope scope;

    protected Map<String, FieldDelegate<?>> fields;

    protected ObjectDelegate(Scope scope) {
        this.scope = scope;
        fields = new HashMap<String, FieldDelegate<?>>();
    }

    public static ConstructorDelegate createConstructor(
            Class<? extends ObjectDelegate> wrapperClass, Scope scope) {
        return DelegateManager.getEntry(wrapperClass).createConstructor(scope);
    }

    protected FieldDelegate<?> createField(String name) {
        return null;
    }

    @Override
    public final Object createWrapper(ScriptingEngine engine) {
        return engine.createWrapper(this);
    }

    @Override
    public Object get(String name) throws ScriptingException {
        FieldDelegate<?> fieldDelegate = entry.getField(name, fields, this);
        if (fieldDelegate != null)
            return fieldDelegate.get();
        return super.get(name);
    }

    @Override
    public boolean has(String name) {
        return fields.containsKey(name) || super.has(name);
    }

    @Override
    public MemberDesc getMemberInfo() {
        return new ObjectDesc(this);
    }

    @Override
    public void put(String name, Object value) throws ScriptingException {
        FieldDelegate<?> fieldWrapper = entry.getField(name, fields, this);
        if (fieldWrapper != null) {
            @SuppressWarnings("unchecked")
            FieldDelegate<Object> fw = (FieldDelegate<Object>) fieldWrapper;
            fw.set(value);
        }
    }

    public Scope getScope() {
        return scope;
    }
}
