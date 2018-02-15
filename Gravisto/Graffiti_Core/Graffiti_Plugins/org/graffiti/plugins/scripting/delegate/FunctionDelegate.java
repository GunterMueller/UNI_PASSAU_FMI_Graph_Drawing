package org.graffiti.plugins.scripting.delegate;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.graffiti.plugins.scripting.ScriptingEngine;
import org.graffiti.plugins.scripting.exceptions.ScriptingException;
import org.graffiti.plugins.scripting.exceptions.WrongSignatureException;
import org.graffiti.plugins.scripting.reflect.FunctionDesc;

public final class FunctionDelegate extends CallableDelegate {
    private List<Method> methodList;

    private Object thisObject;

    protected FunctionDelegate(String name, List<Method> methods) {
        this(name, methods, null);
    }

    protected FunctionDelegate(String name, List<Method> methods,
            Object thisObject) {
        super(name);
        this.methodList = methods;
        this.thisObject = thisObject;
    }

    public final Object call(Object[] args) throws ScriptingException {
        for (Method method : methodList) {
            Object[] newArgs = match(method.getParameterTypes(), args, 0);
            if (newArgs != null) {
                try {
                    Object result = method.invoke(thisObject, newArgs);
                    if (method.getReturnType().equals(Void.TYPE))
                        return UNDEFINED;
                    return result;
                } catch (IllegalArgumentException e) {
                    throw new ScriptingException(e);
                } catch (IllegalAccessException e) {
                    throw new ScriptingException(e);
                } catch (InvocationTargetException e) {
                    throw new ScriptingException(e);
                }
            }
        }
        throw new WrongSignatureException(name);
    }

    @Override
    public final Object createWrapper(ScriptingEngine engine) {
        return engine.createWrapper(this);
    }

    @Override
    public String getClassName() {
        return "Function";
    }

    @Override
    public FunctionDesc getMemberInfo() {
        return new FunctionDesc(this, thisObject != null);
    }

    public Class<?> getReturnType() {
        // TODO: intersect return types from all methods
        return methodList.get(0).getReturnType();
    }

    public Object getThisObject() {
        return thisObject;
    }

    @Override
    public String toString() {
        return help();
    }
}
