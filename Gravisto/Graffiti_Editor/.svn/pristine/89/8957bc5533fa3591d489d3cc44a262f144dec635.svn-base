package org.graffiti.plugins.scripting.delegate;

import java.lang.reflect.Constructor;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.plugins.scripting.Scope;

class ConstructorDelegateFactory {
    private String name;
    private List<Constructor<?>> constructors;

    public ConstructorDelegateFactory() {
        name = "";
        constructors = new LinkedList<Constructor<?>>();
    }

    public void add(Constructor<?> constructor) {
        constructors.add(constructor);
    }

    public void setName(String name) {
        this.name = name;
    }

    public ConstructorDelegate create(Scope scope) {
        return new ConstructorDelegate(name, scope, constructors);
    }
}
