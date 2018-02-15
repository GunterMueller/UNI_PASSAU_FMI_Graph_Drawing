// =============================================================================
//
//   ToolManager.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.prefs.Preferences;

import org.graffiti.plugin.GenericPlugin;
import org.graffiti.plugin.gui.ToolButton;
import org.graffiti.plugin.gui.ToolToolbar;
import org.graffiti.plugin.view.View;
import org.graffiti.plugin.view.ViewListener;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.ToolAction;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugin.view.interactive.UserGestureDispatcher;
import org.graffiti.plugin.view.interactive.UserGestureListener;
import org.graffiti.plugin.view.interactive.ViewFamily;
import org.graffiti.session.EditorSession;
import org.graffiti.session.Session;
import org.graffiti.session.SessionListener;
import org.graffiti.util.Reference;
import org.graffiti.util.VoidCallback;

/**
 * Manager of all tools.
 * 
 * <h1>Adding Tools</h1> New {@link Tool}s can simply be constructed and then
 * registered by calling {@link #registerTool(Tool)} or
 * {@link #registerTool(Tool, GenericPlugin)}. Tools that are created by
 * {@link ToolFactory}s and are stored in the preferences tree will
 * automatically be instantiated and registered when the respective factory is
 * registered by {@link #registerToolFactory(ToolFactory)}.
 * 
 * <h2>Visiblity</h1> The availability of tools, that is, whether the tool
 * button for a tool is currently visible or not, is controlled by the default
 * mode and tool filters.
 * 
 * <h3>Default mode</h3>
 * When the default mode is active, at most those tools are visible, whose
 * {@link Tool#isDefaultMode()} method returns {@code true}. If the default mode
 * is inactive, all tools are generally visible (some may still be filtered out
 * by tool filters). To disable the default mode, add a veto against it by
 * passing an arbitrary object to {@link #addDefaultModeVeto(Object)}. The veto
 * can be withdrawn by removing that object by
 * {@link #removeDefaultModeVeto(Object)}. The default mode is considered active
 * exactly if there currently is no veto against it.
 * 
 * <h3>Tool filters</h3>
 * Tool filters decide for each tool whether it shall be visible or not. The
 * filters are added and removed to a list by {@link #addToolFilter(ToolFilter)}
 * and {@link #removeToolFilter(ToolFilter)}. A tool is available exactly if all
 * tool filters in that list vote in favor of its visibility (the default mode
 * is actually realized by a hidden member of that list).
 * 
 * <h3>Use cases</h3>
 * The previously described mechanism is exemplified by the following use cases.
 * <p>
 * <b>Task:</b> Supplement the default tools.<br>
 * <b>Solution:</b> Create and register a new tool that returns {@code true} on
 * {@link Tool#isDefaultMode()}.
 * </p>
 * <p>
 * <b>Task:</b> Hide a specific tool.<br>
 * <b>Solution:</b> Add a tool filter that lets pass all tools except the tool
 * to hide.
 * </p>
 * <p>
 * <b>Task:</b> Limit the visibility to the default tools.<br>
 * <b>Solution:</b> Add a tool filter that lets pass only the default tools.
 * </p>
 * <p>
 * <b>Task:</b> Replace the default tools by a new mode of interaction.<br>
 * <b>Solution:</b> Create and register the new tools, which return {@code
 * false} on {@link Tool#isDefaultMode()}, so that they are hidden by default.
 * In order to switch to the alternative mode, add a veto against the default
 * mode, and add a tool filter that lets pass only the new tools. To switch back
 * to normal mode, remove the veto and the tool filters.
 * </p>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class ToolRegistry implements SessionListener, ViewListener,
        UserGestureListener {
    /**
     * The singleton.
     */
    private static ToolRegistry singleton = new ToolRegistry();

    /**
     * Returns the {@code ToolRegistry} singleton.
     * 
     * @return the {@code ToolRegistry} singleton.
     */
    public static ToolRegistry get() {
        return singleton;
    }

    /**
     * Maps to tool factories from their id.
     */
    private Map<String, ToolFactory> toolFactories;

    /**
     * Maps from the view family to the entry that maintains the tools
     * supporting that family.
     */
    private Map<ViewFamily<?>, ToolEntry<?>> map;

    /**
     * Maps to common actions from their id. Common actions are those actions
     * that are compatible with every view family.
     */
    private Map<String, ToolAction<InteractiveView<?>>> commonActions;

    /**
     * The tool bar containing the tool buttons for the activation of the tools.
     */
    private ToolToolbar toolbar;

    /**
     * The dispatcher of user gestures.
     */
    private UserGestureDispatcher userGestureDispatcher;

    /**
     * The current view.
     */
    private InteractiveView<? extends InteractiveView<?>> currentView;

    /**
     * The view family of the current view.
     */
    private ViewFamily<?> currentViewFamily;

    /**
     * The entry maintaining all tool supporting the view family of the current
     * view.
     */
    private ToolEntry<?> currentToolEntry;

    /**
     * List of tool filters, which ultimately controls the visibility of the
     * tools.
     */
    private ConjunctiveToolFilter toolFilter;

    /**
     * A {@code DefaultModeFilter}, which is contained in the list of {@code
     * toolFilter}.
     */
    private DefaultModeFilter defaultModeFilter;

    /**
     * A {@code ViewFamilyToolFilter}, which is contained in the list of {@code
     * toolFilter}.
     */
    private ViewFamilyToolFilter viewFamilyToolFilter;

    /**
     * Is {@code true} during the execution of
     * {@link #executeChanges(VoidCallback)}, in order to indicate that a
     * sequence of changes is performed and the GUI should only be updated after
     * the changes.
     */
    private boolean suppressesVisibilityUpdates;

    /**
     * List of listeners interested in changes to the tool list.
     */
    private LinkedList<ToolRegistryListener> listeners;

    /**
     * List of {@code ToolPopupMenuProvider}s.
     */
    private LinkedList<ToolPopupMenuProvider> popupMenuProviders;

    /**
     * Constructs a ToolRegistry.
     */
    private ToolRegistry() {
        singleton = this;
        toolFactories = new HashMap<String, ToolFactory>();
        map = new HashMap<ViewFamily<?>, ToolEntry<?>>();
        commonActions = new HashMap<String, ToolAction<InteractiveView<?>>>();
        toolbar = new ToolToolbar();
        userGestureDispatcher = new UserGestureDispatcher();
        viewFamilyToolFilter = new ViewFamilyToolFilter();
        defaultModeFilter = new DefaultModeFilter();
        toolFilter = new ConjunctiveToolFilter(new DummyToolFilter(),
                viewFamilyToolFilter, defaultModeFilter, new HiddenToolFilter());
        listeners = new LinkedList<ToolRegistryListener>();
        popupMenuProviders = new LinkedList<ToolPopupMenuProvider>();
        suppressesVisibilityUpdates = false;
    }

    /**
     * Returns the appropriate tool entry for the specified view family. It
     * creates a new one if it does not exist yet.
     * 
     * @param <T>
     * @param viewFamily
     *            the view family.
     * @return the appropriate tool entry for the specified view family.
     */
    private <T extends InteractiveView<T>> ToolEntry<T> getEntry(
            ViewFamily<T> viewFamily) {
        ToolEntry<?> entry = map.get(viewFamily);
        if (entry == null) {
            ToolEntry<T> result = new ToolEntry<T>(viewFamily);
            map.put(viewFamily, result);

            for (ToolFactory factory : toolFactories.values()) {
                if (factory.acceptsViewFamily(viewFamily)) {
                    result.registerToolFactory(factory);
                }
            }

            return result;
        } else {
            @SuppressWarnings("unchecked")
            ToolEntry<T> result = (ToolEntry<T>) entry;
            return result;
        }
    }

    /**
     * Adds the specified listener interested in the changes to the list of
     * tools. A listener may be added multiple times. It will be notified
     * accordingly often.
     * 
     * @param listener
     *            the listener to add, which is interested in the changes to the
     *            list of tools.
     */
    public void addListener(ToolRegistryListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes one instance of the specified listener from the list.
     * 
     * @param listener
     *            the listener to remove.
     * @see #addListener(ToolRegistryListener)
     */
    public void removeListener(ToolRegistryListener listener) {
        listeners.remove(listener);
    }

    /**
     * Registers the specified tool factory. All tools that are registered in
     * the {@link Preferences} for automatic creation by this factory are
     * created. There must not be multiple tool factories with the same id.
     * 
     * @param toolFactory
     *            the tool factory to register.
     */
    public void registerToolFactory(ToolFactory toolFactory) {
        ToolFactory factory = toolFactories.get(toolFactory.getId());
        if (factory != null) {
            if (factory == toolFactory)
                return;
            throw new IllegalArgumentException("Tool factory with id "
                    + toolFactory.getId() + " is already present.");
        }
        toolFactories.put(toolFactory.getId(), toolFactory);

        for (Map.Entry<ViewFamily<?>, ToolEntry<?>> entry : map.entrySet()) {
            if (toolFactory.acceptsViewFamily(entry.getKey())) {
                entry.getValue().registerToolFactory(toolFactory);
            }
        }

        for (ToolRegistryListener listener : listeners) {
            listener.toolFactoryRegistered(toolFactory);
        }
    }

    /**
     * Registers the specified tool. There must not be multiple tools with the
     * same id.
     * 
     * @param tool
     *            the tool to register.
     */
    public <T extends InteractiveView<T>> void registerTool(Tool<T> tool) {
        // Add the tool to the appropriate tool entry.
        ToolEntry<T> entry = getEntry(tool.getViewFamily());
        entry.addTool(tool);
    }

    /**
     * Registers the specified tool. There must not be multiple tools with the
     * same id.
     * 
     * @param tool
     *            the tool to register.
     */
    public <T extends InteractiveView<T>> void registerTool(Tool<T> tool,
            GenericPlugin plugin) {
        registerTool(tool);
        if (plugin != null) {
            tool.preferences.put("plugin", plugin.getName());
        }
    }

    /**
     * Is called when the specified tool has been registered. It adds its button
     * to the tool bar and notifies the {@link ToolRegistryListener}s.
     * 
     * @param <T>
     * @param tool
     * @param entry
     * @see ToolToolbar
     */
    protected <T extends InteractiveView<T>> void toolRegistered(Tool<T> tool,
            ToolEntry<T> entry) {
        // Create the tool button.
        toolbar.add(tool, entry.getSuccessor(tool));
        tool.getToolButton().setVisible(toolFilter.isVisible(tool));

        // Notify the listeners.
        for (ToolRegistryListener listener : listeners) {
            listener.toolRegistered(tool);
        }
    }

    /**
     * Registers the specified tools. There must not be multiple tools with the
     * same id.
     * 
     * @param tools
     *            the tools to register.
     */
    public <T extends InteractiveView<T>> void registerTools(
            final Collection<Tool<T>> tools) {
        executeChanges(new VoidCallback<ToolRegistry>() {
            public void call(ToolRegistry t) {
                for (Tool<T> tool : tools) {
                    registerTool(tool);
                }
            }
        });
    }

    /**
     * Activates the specified tool. This method may only be called from
     * {@link Tool}. To activate a tool, call {@link Tool#activate()} instead.
     * 
     * @param tool
     *            the tool that calls this method to be activated.
     * @throws IllegalStateException
     *             if there is no active view or if the view family of the
     *             calling tool does not equal the current view family.
     */
    protected <T extends InteractiveView<T>> void setActiveTool(Tool<T> tool) {
        if (tool.isActive()) {
            tool.reset();
        } else if (currentViewFamily == null)
            throw new IllegalStateException("No active view");
        else if (!tool.getViewFamily().equals(currentViewFamily))
            throw new IllegalStateException(
                    "Tool is not compatible to the current view.");
        else {
            @SuppressWarnings("unchecked")
            ToolEntry<T> entry = (ToolEntry<T>) currentToolEntry;
            entry.activateTool(tool);
        }
    }

    /**
     * Moves the specified tool one position upwards.
     * 
     * @param <T>
     * @param tool
     *            the tool to move one position upwards.
     */
    protected <T extends InteractiveView<T>> void moveUp(Tool<T> tool) {
        ToolEntry<T> entry = getEntry(tool.getViewFamily());
        entry.moveUp(tool);
        toolbar.update(tool, entry.getSuccessor(tool));
    }

    /**
     * Moves the specified tool one position downwards.
     * 
     * @param <T>
     * @param tool
     *            the tool to move one position downwards.
     */
    protected <T extends InteractiveView<T>> void moveDown(Tool<T> tool) {
        ToolEntry<T> entry = getEntry(tool.getViewFamily());
        entry.moveDown(tool);
        toolbar.update(tool, entry.getSuccessor(tool));
    }

    /**
     * Resets the specified tool.
     * 
     * @param <T>
     * @param tool
     *            the tool to reset.
     */
    protected <T extends InteractiveView<T>> void resetTool(Tool<T> tool) {
        ToolEntry<T> entry = getEntry(tool.getViewFamily());
        for (ToolAction<InteractiveView<?>> action : commonActions.values()) {
            action.reset();
        }
        entry.resetTool(tool);
    }

    /**
     * Returns the tool bar.
     * 
     * @return the tool bar.
     */
    public ToolToolbar getToolbar() {
        return toolbar;
    }

    /**
     * {@inheritDoc}
     */
    public void sessionChanged(Session s) {
        if (!(s instanceof EditorSession)) {
            viewChanged(null);
        } else {
            EditorSession editorSession = (EditorSession) s;
            viewChanged(editorSession.getActiveView());
        }
    }

    /**
     * {@inheritDoc} This implementation does nothing.
     */
    public void sessionDataChanged(Session s) {
        // Nothing to do.
    }

    /**
     * {@inheritDoc}
     */
    public void viewChanged(View newView) {
        if (currentView == newView)
            return;
        if (newView instanceof InteractiveView<?>) {
            interactiveViewChanged((InteractiveView<?>) newView);
        } else {
            currentView = null;
            currentViewFamily = null;
            currentToolEntry = null;
            viewFamilyToolFilter.setCurrentViewFamily(null);
            updateVisibility();
        }
    }

    /**
     * Reacts when the newly active view is an {@code InteractiveView}.
     * 
     * @param view
     *            the newly active view.
     */
    private <T extends InteractiveView<T>> void interactiveViewChanged(
            InteractiveView<T> view) {
        currentView = view;
        currentViewFamily = view.getFamily();
        currentToolEntry = getEntry(currentViewFamily);
        viewFamilyToolFilter.setCurrentViewFamily(currentViewFamily);
        updateVisibility();
        if (currentToolEntry != null) {
            @SuppressWarnings("unchecked")
            ToolEntry<T> cte = (ToolEntry<T>) currentToolEntry;

            @SuppressWarnings("unchecked")
            T t = (T) view;
            cte.viewChanged(t);
        }
    }

    /**
     * Updates the visibility of the tools.
     */
    private void updateVisibility() {
        if (suppressesVisibilityUpdates)
            return;

        final Reference<Tool<?>> firstVisibleTool = new Reference<Tool<?>>();

        // Holds possibly a reference to the tool that was active but has to
        // be deactivated as it became invisible.
        final Reference<Boolean> toolDeactivated = new Reference<Boolean>(false);

        // Update the visibility of each tool.
        visitAll(new VoidCallback<Tool<?>>() {
            public void call(Tool<?> t) {
                boolean isVisible = toolFilter.isVisible(t);

                // Is t the first visible tool?
                if (isVisible && firstVisibleTool.get() == null) {
                    firstVisibleTool.set(t);
                }

                ToolButton button = t.getToolButton();
                if (button == null)
                    return;

                boolean buttonIsVisible = button.isVisible();

                // Change in visibility?
                if (isVisible && !buttonIsVisible) {
                    // t becomes visible.
                    button.setVisible(true);
                } else if (!isVisible && buttonIsVisible) {
                    // t becomes visible. It has to be deactivated if it is
                    // currently invisible.
                    button.setVisible(false);
                    if (t.isActive()) {
                        t.setActive(false);
                        toolDeactivated.set(true);
                    }
                }
            }
        });

        if (currentToolEntry != null) {
            // Activate the most suitable visible tool.
            activateLastAvailableTool(currentToolEntry, firstVisibleTool.get());
        }
    }

    /**
     * Returns if the specified tool should be visible according to the tool
     * filters.
     * 
     * @param tool
     *            the tool whose visibility is in questions.
     * @return if the specified tool should be visible according to the tool
     *         filters.
     */
    boolean isVisible(Tool<?> tool) {
        return toolFilter.isVisible(tool);
    }

    /**
     * Activates that tool among all visible tools, which has been active most
     * recently.
     * 
     * @param <T>
     *            Common superclass of all views belonging to the current view
     *            family.
     * @param entry
     *            the current tool entry.
     * @param firstVisibleTool
     *            the tool to activate if none of the visible tools has been
     *            active yet.
     */
    private <T extends InteractiveView<T>> void activateLastAvailableTool(
            ToolEntry<T> entry, Tool<?> firstVisibleTool) {
        @SuppressWarnings("unchecked")
        Tool<T> t = (Tool<T>) firstVisibleTool;
        entry.activeLastAvailableTool(t);
    }

    /**
     * Calls the specified visitor for all tools.
     * 
     * @param visitor
     *            the visitor to call for all tools.
     */
    private void visitAll(VoidCallback<Tool<?>> visitor) {
        for (ToolEntry<?> entry : map.values()) {
            entry.visitAll(visitor);
        }
    }

    /**
     * Adds the specified tool filter.
     * 
     * @param filter
     *            the tool filter to add.
     * @see #removeToolFilter(ToolFilter)
     */
    public void addToolFilter(ToolFilter filter) {
        toolFilter.addFilter(filter);
        updateVisibility();
    }

    /**
     * Removes the specified tool filter.
     * 
     * @param filter
     *            the tool filter to remove.
     * @see #addToolFilter(ToolFilter)
     */
    public void removeToolFilter(ToolFilter filter) {
        toolFilter.removeFilter(filter);
        updateVisibility();
    }

    /**
     * Adds a veto against the default mode.
     * 
     * @param veto
     *            the veto to add against the default mode.
     * @see #removeDefaultModeVeto(Object)
     */
    public void addDefaultModeVeto(Object veto) {
        defaultModeFilter.addVeto(veto);
        updateVisibility();
    }

    /**
     * Removes the specified veto against the default mode.
     * 
     * @param veto
     *            the veto to remove.
     * @see #addDefaultModeVeto(Object)
     */
    public void removeDefaultModeVeto(Object veto) {
        defaultModeFilter.removeVeto(veto);
        updateVisibility();
    }

    /**
     * Calls the specified callback. The visibility of the tools is only be
     * updated when the execution has finished. It is recommended to pack a
     * sequence of calls to this registry in a callback in order to eliminate
     * superfluous visibility updates. It is save to nest calls to {@code
     * executeChanges(VoidCallback)}.
     * 
     * @param callback
     *            the callback to call.
     */
    public void executeChanges(VoidCallback<ToolRegistry> callback) {
        if (suppressesVisibilityUpdates) {
            // This is a nested executeChanges() call. Simple execute the
            // changes.
            callback.call(this);
        } else {
            // Suppress the visibility changes during the update.
            try {
                suppressesVisibilityUpdates = true;
                callback.call(this);
            } finally {
                suppressesVisibilityUpdates = false;
                updateVisibility();
            }
        }
    }

    /**
     * Adds the specified popup menu provider. When the user right-clicks on a
     * {@link ToolButton}, a popup menu is shown, which contains the actions
     * provided by {@code ToolPopupMenuProvider}s.
     * 
     * @param provider
     *            the popup menu provider to add.
     * @see #removePopupMenuProvider(ToolPopupMenuProvider)
     * @see #getPopupMenuProviders()
     */
    public void addPopupMenuProvider(ToolPopupMenuProvider provider) {
        popupMenuProviders.add(provider);
    }

    /**
     * Removes the specified pop
     * 
     * @param provider
     * @see #addPopupMenuProvider(ToolPopupMenuProvider)
     */
    public void removePopupMenuProvider(ToolPopupMenuProvider provider) {
        popupMenuProviders.remove(provider);
    }

    /**
     * Returns a list of all popup menu providers.
     * 
     * @return a list of all popup menu providers.
     * 
     * @see #addPopupMenuProvider(ToolPopupMenuProvider)
     */
    public List<ToolPopupMenuProvider> getPopupMenuProviders() {
        return popupMenuProviders;
    }

    /**
     * Returns all tools supporting the specified view family.
     * 
     * @param <T>
     *            Common superclass of all views belonging to the specified view
     *            family.
     * @param viewFamily
     *            the view family, the supporting tools of which are to be
     *            returned.
     * @return all tools supporting the specified view family.
     */
    public <T extends InteractiveView<T>> SortedSet<Tool<T>> getTools(
            ViewFamily<T> viewFamily) {
        return getEntry(viewFamily).getTools();
    }

    /**
     * Returns the tool factories.
     * 
     * @return the tool factories.
     * @see #registerToolFactory(ToolFactory)
     */
    public Collection<ToolFactory> getToolFactories() {
        return toolFactories.values();
    }

    /**
     * Creates a new tool, which supports the specified view family, using the
     * factory with the specified id.
     * 
     * @param <T>
     *            Common superclass of all views belonging to the specified view
     *            family.
     * @param viewFamily
     *            the view family to support by the new tool.
     * @param factoryId
     *            the id of the factory used to create the new tool.
     * @return a new tool, which supports the specified view family, using the
     *         factory with the specified id.
     */
    public <T extends InteractiveView<T>> Tool<T> createTool(
            ViewFamily<T> viewFamily, String factoryId) {
        ToolFactory factory = toolFactories.get(factoryId);
        if (factory == null)
            throw new IllegalArgumentException("Unknown factory id \""
                    + factoryId + "\"");
        if (!factory.acceptsViewFamily(viewFamily))
            throw new IllegalArgumentException("Factory \"" + factoryId
                    + "\" does not accept view family " + viewFamily.getName());
        return getEntry(viewFamily).createTool(factory);
    }

    /**
     * {@inheritDoc} This implementation broadcasts the gesture to the current
     * tool and all listeners added by
     * {@link #addUserGestureListener(UserGestureListener)}.
     */
    public void gesturePerformed(InteractiveView<?> source, UserGesture gesture) {
        if (source != currentView) {
            interactiveViewChanged(source);
        }
        if (currentToolEntry != null) {
            currentToolEntry.gesturePerformed(gesture);
        }
        userGestureDispatcher.gesturePerformed(source, gesture);
    }

    /**
     * {@inheritDoc} This implementation broadcasts the message to the current
     * tool and all listeners added by
     * {@link #addUserGestureListener(UserGestureListener)}.
     */
    public void canceled(InteractiveView<?> source) {
        if (currentToolEntry != null) {
            currentToolEntry.canceled();
        }
        userGestureDispatcher.canceled(source);
    }

    /**
     * Adds the specified {@code UserGestureListener}, which will be notified
     * when a user gesture is performed. If a listener is added multiple times,
     * it will be notified accordingly multiple times in response to each single
     * user gesture.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addUserGestureListener(UserGestureListener listener) {
        userGestureDispatcher.addListener(listener);
    }

    /**
     * Removes the first occurrence of the specified listener.
     * 
     * @param listener
     *            the listener to remove.
     */
    public void removeUserGestureListener(UserGestureListener listener) {
        userGestureDispatcher.removeListener(listener);
    }

    /**
     * Adds the specified common action. Common actions are those actions that
     * are compatible with every view family.
     * 
     * @param commonAction
     *            the common action to add.
     */
    public void addCommonAction(ToolAction<InteractiveView<?>> commonAction) {
        commonActions.put(commonAction.getId(), commonAction);
    }

    /**
     * Returns the common action with the specified id.
     * 
     * @param id
     *            the id of the common action to return.
     * @return the common action with the specified id or {@code null} if no
     *         such action exists.
     */
    public ToolAction<InteractiveView<?>> getCommonAction(String id) {
        return commonActions.get(id);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
