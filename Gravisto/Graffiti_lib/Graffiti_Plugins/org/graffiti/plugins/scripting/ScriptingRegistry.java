package org.graffiti.plugins.scripting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.session.Session;
import org.graffiti.util.Callback;
import org.graffiti.util.MutualWeakHashMap;

/**
 * Manager of all scripting engines and scopes. A {@link ScriptingEngine}
 * provides access to a scripting language in a uniform way. A {@link Scope}
 * defines the variables, functions and classes accessible to the script.
 * 
 * <h2>Structure</h2>
 * <p>
 * The scripting system can roughly be divided into two major parts, which
 * relate to an two-staged process of wrapping objects for script access. In the
 * first step, objects of the main program like {@code Node} or {@code Session}
 * are wrapped by <em>delegates</em> for the use in the scripting system. The
 * delegates are then in a second step wrapped by <em>wrappers</em> to be used
 * by the concrete scripting languages. The term 'to wrap' is used in both
 * contexts.
 * </p>
 * <p>
 * This mechanism allows for the easy extension of the scripting system by new
 * features in a high-level manner, as the delegates added by the programmer
 * become automatically available in every scripting language without having him
 * to worry about the details of each language. New scripting languages can in
 * turn be supported through the addition of a rather thin layer of wrappers.
 * Diagram 1 exemplifies this structure in the case of using {@code Graph},
 * {@code Node} and {@code Session} from JavaScript and Python. <center> <img
 * src="doc-files/ScriptingRegistry-1.png"></img><br />
 * <b>Diagram 1: Accessing Gravisto objects from JavaScript and Python.</b>
 * </center>
 * </p>
 * <h2>Manipulating the scopes</h2> All changes to the scopes, like the addition
 * of a new variable, must be wrapped within a callback passed to
 * {@link #cascadeContexts(Object, Callback)}. <h2>Executing scripts</h2> To
 * execute a piece of script code, create a script by calling
 * {@link ScriptingEngine#createScript(String, Scope)} for the respective
 * engine, passing in the source code and desired scope. The script can then be
 * executed by {@link Script#execute(ResultCallback)}.
 */
public final class ScriptingRegistry {
    /**
     * The plugin providing this registry.
     */
    protected static ScriptingPlugin plugin;

    /**
     * Maps to scripting engines from their id.
     */
    private Map<String, ScriptingEngine> engines;

    /**
     * The basic scope.
     */
    private BasicScope basicScope;

    /**
     * The program scope.
     */
    private ProgramScope programScope;

    /**
     * Maps from a session to its representing scope.
     */
    private MutualWeakHashMap<Session, SessionScope> sessionScopes;

    /**
     * Maps from a view to its representing scoope.
     */
    private MutualWeakHashMap<InteractiveView<?>, ViewScope<?>> viewScopes;

    /**
     * The listeners interested in the registration of new scripting engines.
     */
    private List<ScriptingRegistryListener> listeners;

    /**
     * Returns the {@code ScriptingRegistry} singleton.
     * 
     * @return the {@code ScriptingRegistry} singleton.
     * @throws IllegalStateException
     *             if the {@link ScriptingPlugin} is not loaded.
     */
    public static ScriptingRegistry get() {
        if (plugin != null) {
            ScriptingRegistry registry = plugin.getRegistry();
            if (registry != null)
                return registry;
        }
        throw new IllegalStateException("Requires GridRegistry plugin.");
    }

    /**
     * Constructs the {@code ScriptingRegistry} for the specified plugin.
     * 
     * @param plugin
     *            the plugin providing to registry to construct.
     */
    protected ScriptingRegistry(ScriptingPlugin plugin) {
        ScriptingRegistry.plugin = plugin;
        engines = new HashMap<String, ScriptingEngine>();
        listeners = new LinkedList<ScriptingRegistryListener>();
        cascadeContexts(null, new Callback<Object, Object>() {
            public Object call(Object t) {
                Map<ScriptingEngine, ScopeWrapper> scopeWrappers = new HashMap<ScriptingEngine, ScopeWrapper>();
                for (ScriptingEngine engine : engines.values()) {
                    scopeWrappers.put(engine, engine.createRootScope());
                }
                basicScope = new BasicScope(scopeWrappers);
                programScope = new ProgramScope(basicScope);
                sessionScopes = new MutualWeakHashMap<Session, SessionScope>();
                viewScopes = new MutualWeakHashMap<InteractiveView<?>, ViewScope<?>>();
                return null;
            }
        });
    }

    /**
     * Returns the basic scope.
     * 
     * @return the basic scope.
     */
    public BasicScope getBasicScope() {
        return basicScope;
    }

    /**
     * Returns the program scope.
     * 
     * @return the program scope.
     */
    public ProgramScope getProgramScope() {
        return programScope;
    }

    /**
     * Returns the scope representing the specified session.
     * 
     * @param session
     *            the session represented by the scope to return.
     * @return the scope representing the specified session.
     */
    public SessionScope getSessionScope(Session session) {
        SessionScope sessionScope = sessionScopes.get(session);
        if (sessionScope == null) {
            sessionScope = new SessionScope(programScope, session);
            sessionScopes.put(session, sessionScope);
        }
        return sessionScope;
    }

    /**
     * Returns the scope representing the specified view.
     * 
     * @param view
     *            the view represented by the scope to return.
     * @return the scope representing the specified view.
     */
    public <T extends InteractiveView<T>> ViewScope<T> getViewScope(
            InteractiveView<T> view) {
        @SuppressWarnings("unchecked")
        ViewScope<T> viewScope = (ViewScope<T>) viewScopes.get(view);
        if (viewScope == null) {
            viewScope = new ViewScope<T>(getSessionScope(view
                    .getEditorSession()), view);
            viewScopes.put(view, viewScope);
        }
        return viewScope;
    }

    /**
     * Registers the specified engine with the specified id. The same engine may
     * be registered with multiple ids for backwards compatibility.
     * 
     * @param id
     *            the id of the engine to register.
     * @param engine
     *            the engine to register.
     */
    public void registerEngine(String id, final ScriptingEngine engine) {
        engines.put(id, engine);
        engine.execute(new Callback<Object, ScriptingContext>() {
            public Object call(ScriptingContext t) {
                Scope.engineAdded(engine);
                return null;
            }
        });

        for (ScriptingRegistryListener listener : listeners) {
            listener.engineRegistered(id, engine);
        }
    }

    /**
     * Executes the specified callback. All changes to scopes must be wrapped
     * within such callbacks. It is safe to nest calls to {@code
     * cascadeContexts}.
     * 
     * @param parameter
     *            arbitrary parameter passed to the specified callback.
     * @param callback
     *            the callback to execute.
     * @param <S>
     *            the return type of the callback.
     * @param <T>
     *            type of the parameter passed to the callback.
     */
    public <S, T> S cascadeContexts(T parameter, Callback<S, T> callback) {
        return cascadeContexts(engines.values().iterator(), callback, parameter);
    }

    /**
     * Used to execute the specified callback once but nested within the context
     * of every scripting engine.
     */
    private <S, T> S cascadeContexts(final Iterator<ScriptingEngine> iter,
            final Callback<S, T> callback, final T parameter) {
        if (iter.hasNext()) {
            ScriptingEngine engine = iter.next();
            return engine.execute(new Callback<S, ScriptingContext>() {
                public S call(ScriptingContext context) {
                    return cascadeContexts(iter, callback, parameter);
                }
            });
        } else
            return callback.call(parameter);
    }

    /**
     * Returns the scripting engine with the specified id.
     * 
     * @return the scripting engine with the specified id or {@code null} if no
     *         such engine exists.
     */
    public ScriptingEngine getEngine(String id) {
        return engines.get(id);
    }

    /**
     * Returns all scripting engines.
     * 
     * @return all scripting engines.
     */
    public Set<Map.Entry<String, ScriptingEngine>> getEngines() {
        return engines.entrySet();
    }

    /**
     * Adds the specified {@code ScriptingRegistryListener}, which will be
     * notified when a scripting engine is registered. If a listener is added
     * multiple times, it will be notified accordingly multiple times.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addListener(ScriptingRegistryListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes the specified listener.
     * 
     * @param listener
     *            the listener to remove.
     */
    public void removeListener(ScriptingRegistryListener listener) {
        listeners.remove(listener);
    }
}
