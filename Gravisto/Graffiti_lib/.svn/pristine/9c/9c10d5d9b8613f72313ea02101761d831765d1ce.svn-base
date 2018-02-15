package org.graffiti.plugins.scripting.delegate;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.plugins.scripting.Scope;

class FunctionDelegateFactory {
    private String name;

    private List<Method> methods;

    public FunctionDelegateFactory(String name) {
        this.name = name;
        methods = new LinkedList<Method>();
    }

    public void add(Method method) {
        method.setAccessible(true);
        methods.add(method);
    }

    public FunctionDelegate create(ReflectiveDelegate thisObject) {
        return new FunctionDelegate(name, methods, thisObject);
    }

    public FunctionDelegate create(Scope thisObject) {
        return new FunctionDelegate(name, methods, thisObject);
    }

    public List<Method> getMethods() {
        return methods;
    }
}
