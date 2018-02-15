package org.graffiti.plugins.scripting;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.graffiti.plugins.scripting.delegate.ConstructorDelegate;
import org.graffiti.plugins.scripting.delegate.DelegateEntry;
import org.graffiti.plugins.scripting.delegate.DelegateFactory;
import org.graffiti.plugins.scripting.delegate.FunctionDelegate;
import org.graffiti.plugins.scripting.delegate.ObjectDelegate;
import org.graffiti.plugins.scripting.delegate.ReflectiveDelegate;
import org.graffiti.plugins.scripting.delegate.ScriptingDelegate;
import org.graffiti.plugins.scripting.reflect.MemberDesc;
import org.graffiti.util.WeakHashSet;

/**
 * {@code Scope} classes manage the scope of scripts. A scope defines the global
 * variables, functions and classes accessible to the script. Scopes are
 * organized in a hierarchical manner through a parent relation. A script that
 * is executed in this scope additionally accesses the variables, functions and
 * classes of all its ancestor scopes.
 * 
 * @author Andreas Glei&szlig;ner
 */
public abstract class Scope {
    /**
     * A {@code ConsoleProvider}, which ignores the output.
     */
    private static final ConsoleProvider DEFAULT_CONSOLE_PROVIDER = new ConsoleProvider() {
        /**
         * {@inheritDoc} This implementation ignores the output.
         */
        public void print(String string, ConsoleOutput kind) {
            // Do nothing.
        }

        /**
         * {@inheritDoc} This implementation does nothing.
         */
        public void reset() {
            // Do nothing.
        }
    };

    /**
     * All existent scopes.
     */
    private static WeakHashSet<Scope> scopes = new WeakHashSet<Scope>();

    /**
     * Maps from the scripting engine to the respective wrapper wrapping this
     * scope.
     */
    private Map<ScriptingEngine, ScopeWrapper> scopeWrappers;

    /**
     * Maps to the variables of this scope from their name.
     */
    private Map<String, Object> variables;

    /**
     * Maps to constants of this scope from their name.
     */
    private Map<String, Object> constants;

    /**
     * Maps to wrapped Java classes of this scope from their name.
     */
    private Map<String, Class<?>> nativeClasses;

    /**
     * Denotes if a scope is sealed, i.e. no attributes may be set or removed
     * afterwards.
     */
    private boolean isSealed;

    /**
     * The root of the scope family tree.
     */
    private final BasicScope rootScope;

    /**
     * The parent of this scope in the scope family tree.
     */
    protected final Scope parent;

    /**
     * The console provider.
     */
    protected ConsoleProvider consoleProvider;

    /**
     * Is called by {@code ScriptingRegistry} when a new {@code ScriptingEngine}
     * has been added so the new engine can create wrappers for all existing
     * scopes.
     * 
     * @param engine
     *            the newly registered engine.
     */
    static void engineAdded(ScriptingEngine engine) {
        Set<Scope> listedScopes = new HashSet<Scope>();
        List<Scope> scopeList = new LinkedList<Scope>();

        // Create scopeList where parents are placed before their children.
        for (Scope scope : scopes) {
            engineAdded(scope, listedScopes, scopeList);
        }

        for (Scope scope : scopeList) {
            if (scope.parent == null) {
                scope.addScopeWrapper(engine, engine.createRootScope());
            } else {
                ScopeWrapper parentWrapper = scope.parent.scopeWrappers
                        .get(engine);
                // Should be safe as the parent's scope wrapper has already been
                // created.
                assert (parentWrapper != null);
                scope.addScopeWrapper(engine, parentWrapper.createChildScope());
            }
        }
    }

    /**
     * Adds the specified scope to the specified list and ensures that all
     * ancestors are placed at a lower position in the list.
     */
    private static void engineAdded(Scope scope, Set<Scope> listedScopes,
            List<Scope> scopeList) {
        if (listedScopes.contains(scope))
            return;
        Scope parent = scope.parent;
        if (parent != null) {
            engineAdded(parent, listedScopes, scopeList);
        }
        listedScopes.add(scope);
        scopeList.add(scope);
    }

    /**
     * Constructs a root scope represented by the specified scope wrappers. Must
     * only be called from the constructor of {@link BasicScope}.
     * 
     * @param scopeWrappers
     *            the scope wrappers wrapping the scope to create.
     */
    Scope(Map<ScriptingEngine, ScopeWrapper> scopeWrappers) {
        this.scopeWrappers = scopeWrappers;
        this.parent = null;
        rootScope = (BasicScope) this;
        isSealed = false;
        consoleProvider = DEFAULT_CONSOLE_PROVIDER;

        variables = new HashMap<String, Object>();
        constants = new HashMap<String, Object>();
        nativeClasses = new HashMap<String, Class<?>>();

        scopes.add(this);
    }

    /**
     * Constructs a scope, which becomes the child of the specified parent
     * scope.
     * 
     * @param parent
     *            the parent of the scope to construct.
     */
    protected Scope(final Scope parent) {
        rootScope = parent.rootScope;
        this.parent = parent;
        this.scopeWrappers = parent.createChildWrappers();

        consoleProvider = parent.consoleProvider;

        variables = new HashMap<String, Object>();
        constants = new HashMap<String, Object>();
        nativeClasses = new HashMap<String, Class<?>>();

        for (FunctionDelegate delegate : new DelegateEntry(getClass(), false)
                .createMethods(this)) {
            String name = delegate.getName();
            constants.put(name, delegate);
            for (ScopeWrapper wrapper : scopeWrappers.values()) {
                wrapper.putConst(name, delegate);
            }
        }

        scopes.add(this);
    }

    /**
     * Creates scope wrappers for a future child of this scope.
     */
    private Map<ScriptingEngine, ScopeWrapper> createChildWrappers() {
        Map<ScriptingEngine, ScopeWrapper> childWrappers = new HashMap<ScriptingEngine, ScopeWrapper>();
        for (Map.Entry<ScriptingEngine, ScopeWrapper> entry : scopeWrappers
                .entrySet()) {
            childWrappers.put(entry.getKey(), entry.getValue()
                    .createChildScope());
        }
        return childWrappers;
    }

    /**
     * Returns if this scope is sealed, i.e. no variables, constants, functions
     * or classes may be set or removed afterwards.
     */
    public final boolean isSealed() {
        return isSealed;
    }

    /**
     * Seals this scope, so that no attributes can be set or removed afterwards.
     * 
     * @see #isSealed()
     */
    protected final void seal() {
        if (isSealed)
            return;
        for (ScopeWrapper wrapper : scopeWrappers.values()) {
            wrapper.seal();
        }
        isSealed = true;
    }

    /**
     * Returns the delegate canonically representing the specified object. If no
     * such delegate exists yet, a new delegate is created by the specified
     * factory. The mapping does not depend on the particular scope, as it is
     * managed by the root scope.
     * 
     * @param object
     *            the object to wrap.
     * @param factory
     *            the factory to create a new delegate representing the
     *            specified object if no such delegate exists yet.
     * @return the delegate canonically representing the specified object.
     */
    public final <S extends ObjectDelegate, T> S getCanonicalDelegate(T object,
            DelegateFactory<S, T> factory) {
        return rootScope.getCanonicalDelegate(factory, object);
    }

    /**
     * Provides scripting access for the specified class.
     * 
     * @param clazz
     *            the class to access from scripting.
     */
    public final void addNativeJavaClass(Class<?> clazz) {
        String name = clazz.getSimpleName();
        nativeClasses.put(name, clazz);
        for (ScopeWrapper wrapper : scopeWrappers.values()) {
            wrapper.addNativeJavaClass(name, clazz);
        }
    }

    /**
     * Adds constructors that construct delegates of the specified class.
     */
    public final void addDelegateClass(Class<? extends ObjectDelegate> clazz) {
        ConstructorDelegate constructor = ObjectDelegate.createConstructor(
                clazz, this);
        String name = constructor.getName();
        constants.put(name, constructor);
        for (ScopeWrapper wrapper : scopeWrappers.values()) {
            wrapper.putConst(name, constructor);
        }
    }

    /**
     * Adds the specified scope wrapper to represent this scope for the
     * specified scripting engine.
     * 
     * @param engine
     *            the engine for which the wrapper representing this scope is
     *            added.
     * @param wrapper
     *            the wrapper representing this scope for the specified engine.
     */
    private final void addScopeWrapper(ScriptingEngine engine,
            ScopeWrapper wrapper) {
        scopeWrappers.put(engine, wrapper);

        for (Map.Entry<String, Class<?>> entry : nativeClasses.entrySet()) {
            wrapper.addNativeJavaClass(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Object> entry : constants.entrySet()) {
            wrapper.putConst(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<String, Object> entry : variables.entrySet()) {
            wrapper.put(entry.getKey(), entry.getValue());
        }

        if (isSealed) {
            wrapper.seal();
        }
    }

    /**
     * Returns the wrapper wrapping this scope for the specified engine.
     * 
     * @param engine
     *            the engine for which the wrapper of this scope is to be
     *            returned.
     * @return the wrapper wrapping this scope for the specified engine.
     */
    public final ScopeWrapper getScopeWrapper(ScriptingEngine engine) {
        return scopeWrappers.get(engine);
    }

    /**
     * Sets the variable with the specified name to the specified value. Creates
     * a new variable if no variable with that name exists yet.
     * 
     * @param name
     *            the name of the variable to set or create.
     * @param bool
     *            the value of the variable to set.
     */
    public final void put(String name, Boolean bool) {
        variables.put(name, bool);

        for (ScopeWrapper scopeWrapper : scopeWrappers.values()) {
            scopeWrapper.put(name, bool);
        }
    }

    /**
     * Sets the variable with the specified name to the specified value. Creates
     * a new variable if no variable with that name exists yet.
     * 
     * @param name
     *            the name of the variable to set or create.
     * @param number
     *            the value of the variable to set.
     */
    public final void put(String name, Number number) {
        variables.put(name, number);

        for (ScopeWrapper scopeWrapper : scopeWrappers.values()) {
            scopeWrapper.put(name, number);
        }
    }

    /**
     * Sets the variable with the specified name to the specified value. Creates
     * a new variable if no variable with that name exists yet.
     * 
     * @param name
     *            the name of the variable to set or create.
     * @param string
     *            the value of the variable to set.
     */
    public final void put(String name, String string) {
        variables.put(name, string);

        for (ScopeWrapper scopeWrapper : scopeWrappers.values()) {
            scopeWrapper.put(name, string);
        }
    }

    /**
     * Sets the variable with the specified name to the specified value. Creates
     * a new variable if no variable with that name exists yet.
     * 
     * @param name
     *            the name of the variable to set or create.
     * @param delegate
     *            the value of the variable to set.
     */
    public final void put(String name, ScriptingDelegate delegate) {
        variables.put(name, delegate);

        for (ScopeWrapper scopeWrapper : scopeWrappers.values()) {
            scopeWrapper.put(name, delegate);
        }
    }

    /**
     * Sets the constant with the specified name to the specified value. Creates
     * a new constant if no constant with that name exists yet.
     * 
     * @param name
     *            the name of the constant to set or create.
     * @param bool
     *            the value of the constant to set.
     */
    public final void putConst(String name, Boolean bool) {
        constants.put(name, bool);

        for (ScopeWrapper scopeWrapper : scopeWrappers.values()) {
            scopeWrapper.putConst(name, bool);
        }
    }

    /**
     * Sets the constant with the specified name to the specified value. Creates
     * a new constant if no constant with that name exists yet.
     * 
     * @param name
     *            the name of the constant to set or create.
     * @param number
     *            the value of the constant to set.
     */
    public final void putConst(String name, Number number) {
        constants.put(name, number);

        for (ScopeWrapper scopeWrapper : scopeWrappers.values()) {
            scopeWrapper.putConst(name, number);
        }
    }

    /**
     * Sets the constant with the specified name to the specified value. Creates
     * a new constant if no constant with that name exists yet.
     * 
     * @param name
     *            the name of the constant to set or create.
     * @param string
     *            the value of the constant to set.
     */
    public final void putConst(String name, String string) {
        constants.put(name, string);

        for (ScopeWrapper scopeWrapper : scopeWrappers.values()) {
            scopeWrapper.putConst(name, string);
        }
    }

    /**
     * Sets the constant with the specified name to the specified value. Creates
     * a new constant if no constant with that name exists yet.
     * 
     * @param name
     *            the name of the constant to set or create.
     * @param delegate
     *            the value of the constant to set.
     */
    public final void putConst(String name, ScriptingDelegate delegate) {
        constants.put(name, delegate);

        for (ScopeWrapper scopeWrapper : scopeWrappers.values()) {
            scopeWrapper.putConst(name, delegate);
        }
    }

    /**
     * Returns the descriptions of all variables, constants, functions and
     * classes provided by this scope.
     * 
     * @return a map mapping to the descriptions of all variables, constants,
     *         functions and classes provided by this scope from their name.
     */
    public SortedMap<String, MemberDesc> getMembers() {
        SortedMap<String, MemberDesc> result;
        if (parent == null) {
            result = new TreeMap<String, MemberDesc>();
        } else {
            result = parent.getMembers();
        }
        addMembers(result, variables);
        addMembers(result, constants);
        return result;
    }

    /**
     * Solely used by {@link #getMembers()}.
     */
    private void addMembers(SortedMap<String, MemberDesc> result,
            Map<String, Object> map) {
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            Object o = entry.getValue();
            if (o instanceof ReflectiveDelegate) {
                result.put(entry.getKey(), ((ReflectiveDelegate) o)
                        .getMemberInfo());
            }
        }
    }
}
