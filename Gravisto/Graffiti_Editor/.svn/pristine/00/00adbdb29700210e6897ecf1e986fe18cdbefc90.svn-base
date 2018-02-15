package org.graffiti.plugins.scripting.delegate;

import org.graffiti.plugins.scripting.Scope;

public abstract class DelegateFactory<S extends ObjectDelegate, T> {
    protected final Scope scope;
    private Class<S> delegateClass;

    public DelegateFactory(Scope scope, Class<S> delegateClass) {
        this.scope = scope;
        this.delegateClass = delegateClass;
    }

    public abstract S create(T object);

    public final Class<S> getDelegateClass() {
        return delegateClass;
    }
}
