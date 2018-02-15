package org.graffiti.plugins.scripting.delegate;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import org.graffiti.plugins.scripting.Scope;
import org.graffiti.plugins.scripting.ScriptingEngine;
import org.graffiti.plugins.scripting.exceptions.IllegalConstructorDefinitionException;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.graffiti.plugins.scripting.exceptions.WrongSignatureException;
import org.graffiti.plugins.scripting.reflect.ConstructorDesc;
import org.graffiti.plugins.scripting.reflect.MemberDesc;

public final class ConstructorDelegate extends CallableDelegate {
    private List<Constructor<?>> constructors;
    private Scope scope;

    protected ConstructorDelegate(String name, Scope scope,
            List<Constructor<?>> constructors) {
        super(name);
        this.scope = scope;
        this.constructors = constructors;
    }

    public ObjectDelegate construct(Object[] args) throws ScriptingException {
        for (Constructor<?> constructor : constructors) {
            Class<?>[] types = constructor.getParameterTypes();
            if (types.length == 0 || !Scope.class.isAssignableFrom(types[0]))
                throw new IllegalConstructorDefinitionException(name);
            Object[] newArgs = match(types, args, 1);
            if (newArgs != null) {
                try {
                    newArgs[0] = scope;
                    return (ObjectDelegate) constructor.newInstance(newArgs);
                } catch (InstantiationException e) {
                    throw new ScriptingException(e);
                } catch (IllegalAccessException e) {
                    throw new ScriptingException(e);
                } catch (InvocationTargetException e) {
                    throw new ScriptingException(e.getCause());
                }
            }
        }
        throw new WrongSignatureException(name, true);
    }

    @Override
    public final Object createWrapper(ScriptingEngine engine) {
        return engine.createWrapper(this);
    }

    @Override
    public String getClassName() {
        return "Constructor";
    }

    @Override
    public MemberDesc getMemberInfo() {
        return new ConstructorDesc(this);
    }

    @Override
    public String toString() {
        return "[Constructor]";
    }
}
