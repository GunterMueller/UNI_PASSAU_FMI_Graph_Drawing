// =============================================================================
//
//   ViewFamily.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.graffiti.plugin.tool.ToolRegistry;

/**
 * If different view classes want to share the same tools, triggers and actions,
 * they return the same {@code ViewFamily} instance.
 * 
 * @param <T>
 *            common superclass of all views belonging to this {@code
 *            ViewFamily}.
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class ViewFamily<T extends InteractiveView<T>> {
    private Trigger rootTrigger;

    private Map<String, ToolAction<T>> actions;

    /**
     * Constructs a new {@code ViewFamily}. Note that {@link #getId()} must
     * return a different for each simultaneous instance of {@code ViewFamily}.
     */
    protected ViewFamily() {
        actions = new HashMap<String, ToolAction<T>>();
        rootTrigger = createRootTrigger();
    }

    protected abstract Trigger createRootTrigger();

    /**
     * Adds an action.
     * 
     * @param action
     *            an action.
     * @see #getActions()
     */
    protected void add(ToolAction<T> action) {
        actions.put(action.getId(), action);
    }

    /**
     * Gets the universal trigger root of this view family. All triggers
     * supported by this view family are children of the returned trigger.
     * 
     * @return the trigger root of this view family.
     */
    public Trigger getRootTrigger() {
        return rootTrigger;
    }

    public Trigger getTrigger(String id) {
        return rootTrigger.getById(id);
    }

    /**
     * Returns a map of {@code ToolAction}s compatible with this view family.
     * That actions are provided innately by this view family. There may be
     * other actions compatible with this view family but not contained in the
     * map.
     * 
     * @return a map of {@code ToolAction}s compatible with this view family.
     */
    public Map<String, ToolAction<T>> getActions() {
        return actions;
    }

    /**
     * Returns the action with the specified id.
     * 
     * @param id
     *            the id of the action to return.
     * @return the action with the specified id.
     * @throws NoSuchElementException
     *             if no such action exists.
     */
    public ToolAction<T> getAction(String id) {
        ToolAction<T> action = actions.get(id);
        if (action == null) {
            // Common actions accept any InteractiveView and never return an
            // InteractiveView so we can happily treat them as an ordinary
            // action belonging to this view family.
            @SuppressWarnings("unchecked")
            ToolAction<T> ta = (ToolAction<T>) ToolRegistry.get()
                    .getCommonAction(id);
            action = ta;
        }
        if (action == null)
            throw new NoSuchElementException(
                    "The view family does not contain "
                            + "an action with the specified id.");
        return action;
    }

    /**
     * Returns the action implementing the specified action class, which must be
     * annotated with the id of the action.
     * 
     * @param toolClass
     *            the class of the action to return.
     * @return the action implementing the specified action class.
     * @throws IllegalArgumentException
     *             if the specified action class is not annotated with
     *             {@link ActionId}.
     * @throws NoSuchElementException
     *             if no such action exists.
     */
    public ToolAction<T> getAction(Class<? extends ToolAction<?>> toolClass) {
        ActionId id = toolClass.getAnnotation(ActionId.class);
        if (id == null)
            throw new IllegalArgumentException(
                    "The specified class is not annotated with an action id.");
        return getAction(id.value());
    }

    /**
     * Returns the name of the view family as seen by the user. Result must not
     * contain slashes ('/').
     * 
     * @return the name of the view family as seen by the user.
     */
    public abstract String getName();

    /**
     * Returns a common super class of all views sharing this view family.
     * 
     * @return a common super class of all views sharing this view family.
     */
    public abstract Class<T> getCommonSuperClass();

    /**
     * Returns an id unique to each view family. The default implementation
     * returns the canonical name of the common super class.
     * 
     * @return an id unique to each view family.
     */
    public String getId() {
        return getCommonSuperClass().getCanonicalName();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
