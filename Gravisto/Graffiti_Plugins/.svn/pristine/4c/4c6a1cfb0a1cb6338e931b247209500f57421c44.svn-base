package org.graffiti.plugins.scripting;

import java.awt.Color;
import java.awt.Cursor;
import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.WeakHashMap;

import org.graffiti.plugins.modes.fast.Sector;
import org.graffiti.plugins.scripting.delegate.DelegateFactory;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegates.BufferedCollectionDelegate;
import org.graffiti.plugins.scripting.delegates.DashDelegate;
import org.graffiti.plugins.scripting.delegates.VectorDelegate;

/**
 * Root of all scopes. Provides some basic classes and delegates. Holds a map,
 * which allows for objects to be canonically represented in the scripting
 * system.
 * 
 * @author Andreas Glei&szlig;ner
 */
public final class BasicScope extends Scope {
    /**
     * Maps from objects to delegates canonically representing them.
     * 
     * @see #getCanonicalDelegate(Object, DelegateFactory)
     */
    private WeakHashMap<Object, WeakReference<ObjectDelegate>> canonicalWrappers;

    /**
     * Constructs a new basic scope, which is wrapped by the specified scope
     * wrappers.
     * 
     * @param scopeWrappers
     *            the scope wrappers representing this scope.
     */
    BasicScope(Map<ScriptingEngine, ScopeWrapper> scopeWrappers) {
        super(scopeWrappers);

        addNativeJavaClass(Color.class);
        addNativeJavaClass(Cursor.class);
        addNativeJavaClass(Sector.class);

        addDelegateClass(VectorDelegate.class);
        addDelegateClass(BufferedCollectionDelegate.class);
        addDelegateClass(DashDelegate.class);

        seal();
        canonicalWrappers = new WeakHashMap<Object, WeakReference<ObjectDelegate>>();
    }

    /**
     * See {@link #getCanonicalDelegate(DelegateFactory, Object)}.
     */
    protected <S extends ObjectDelegate, T> S getCanonicalDelegate(
            DelegateFactory<S, T> factory, T object) {
        WeakReference<ObjectDelegate> ref = canonicalWrappers.get(object);
        if (ref != null) {
            ObjectDelegate delegate = ref.get();
            if (delegate != null)
                return factory.getDelegateClass().cast(delegate);
        }
        S delegate = factory.create(object);
        canonicalWrappers.put(object, new WeakReference<ObjectDelegate>(
                delegate));
        return delegate;
    }
}
