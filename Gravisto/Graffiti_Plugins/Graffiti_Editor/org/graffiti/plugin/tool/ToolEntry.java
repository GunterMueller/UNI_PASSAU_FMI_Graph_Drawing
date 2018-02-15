// =============================================================================
//
//   ToolEntry.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.tool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.graffiti.plugin.gui.ToolButton;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.ToolAction;
import org.graffiti.plugin.view.interactive.UserGesture;
import org.graffiti.plugin.view.interactive.ViewFamily;
import org.graffiti.util.VoidCallback;

/**
 * {@code ToolEntry}s are used by the tool registry to maintain all tools
 * supporting a specific view family.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @param <T>
 *            Superclass of all views belonging to the view family that is
 *            supported by the tools maintained by this entry.
 */
class ToolEntry<T extends InteractiveView<T>> {
    /**
     * The view family supported by all toola maintained by this entry.
     */
    private ViewFamily<T> viewFamily;

    /**
     * The set of tools supporting the view family.
     */
    private TreeSet<Tool<T>> tools;

    /**
     * Maps to the tools from their ids.
     */
    private HashMap<String, Tool<T>> map;

    /**
     * The next position that is available for new tools.
     */
    private int nextFreePosition;

    /**
     * The latest active tool of this entry.
     */
    private Tool<T> lastActiveTool;

    /**
     * The tool environment.
     */
    private ToolEnvironment<T> environment;

    /**
     * Constructs the tool entry for the specified view family.
     * 
     * @param viewFamily
     *            the view family of the new entry.
     */
    public ToolEntry(ViewFamily<T> viewFamily) {
        this.viewFamily = viewFamily;
        environment = new ToolEnvironment<T>();
        tools = new TreeSet<Tool<T>>();
        map = new HashMap<String, Tool<T>>();
        Preferences preferences = Tool.getViewFamilyPreferences(viewFamily);
        try {
            for (String childName : preferences.childrenNames()) {
                Preferences prefs = preferences.node(childName);
                if (prefs.getBoolean("deleted", false)) {
                    prefs.removeNode();
                } else {
                    tools.add(new ToolDummy<T>(viewFamily, childName));
                }
            }
        } catch (BackingStoreException e) {
            throw new RuntimeException(e);
        }

        LinkedList<Tool<T>> list = new LinkedList<Tool<T>>(tools);
        tools = new TreeSet<Tool<T>>();
        nextFreePosition = 0;
        // Assign the tools unique contiguous positions.
        for (Tool<T> tool : list) {
            tool.setPosition(nextFreePosition);
            tools.add(tool);
            map.put(tool.getId(), tool);
            nextFreePosition++;
        }
        preferences.putInt("nextFreePosition", nextFreePosition);
        try {
            preferences.flush();
        } catch (BackingStoreException e) {
        }
    }

    /**
     * Registers the specified tool factory. If their factory id matches, the
     * tool dummies are replaced by the real tool they represent, which is
     * created by the factory.
     * 
     * @param toolFactory
     *            the tool factory to create the respective tools.
     */
    public void registerToolFactory(ToolFactory toolFactory) {
        LinkedList<Tool<T>> newTools = new LinkedList<Tool<T>>();
        String factoryId = toolFactory.getId();
        for (Tool<T> tool : tools) {
            if (tool instanceof ToolDummy<?>) {
                ToolDummy<T> dummy = (ToolDummy<T>) tool;
                if (dummy.hasFactoryId(factoryId)) {
                    newTools.add(dummy.create(viewFamily, toolFactory));
                }
            }
        }

        for (Tool<T> tool : newTools) {
            addTool(tool);
        }
    }

    /**
     * Adds the specified tool.
     * 
     * @param tool
     *            the tool to add.
     */
    public void addTool(Tool<T> tool) {
        String id = tool.getId();

        Tool<T> prevTool = map.get(id);
        if (prevTool != null) {
            if (tool == prevTool)
                return;

            if (!(prevTool instanceof ToolDummy<?>))
                throw new IllegalArgumentException("Tool \"" + id
                        + "\" is already present.");
            tools.remove(prevTool);
        } else {
            tool.setPosition(nextFreePosition);
            nextFreePosition++;
        }

        tool.toolButton = new ToolButton(tool);
        tools.add(tool);
        map.put(id, tool);
        ToolRegistry.get().toolRegistered(tool, this);
    }

    /**
     * Calls the specified visitor for all tools of this entry.
     * 
     * @param visitor
     *            the visitor to call.
     */
    public void visitAll(VoidCallback<Tool<?>> visitor) {
        for (Tool<?> tool : tools) {
            visitor.call(tool);
        }
    }

    /**
     * Activates the last active tool from the set of currently visible tools of
     * this entry. If none of the visible tools has been active yet, the
     * specified tool is activated.
     * 
     * @param firstVisibleTool
     *            the tool to activate if none of the visible tools has been
     *            active yet.
     */
    public void activeLastAvailableTool(Tool<T> firstVisibleTool) {
        Tool<T> tool = lastActiveTool;

        while (tool != null) {
            if (tool.getToolButton().isVisible()) {
                activateTool(tool);
                return;
            } else {
                tool = tool.prevActiveTool;
            }
        }

        if (firstVisibleTool != null) {
            activateTool(firstVisibleTool);
        }
    }

    /**
     * Deactivates the previously active tool and activates the specified tool.
     * If the tool to activate is already active, this method does nothing.
     * 
     * @param tool
     *            the tool to activate.
     */
    public void activateTool(Tool<T> tool) {
        if (tool != lastActiveTool) {
            if (tool.prevActiveTool != null) {
                tool.prevActiveTool.nextActiveTool = tool.nextActiveTool;
            }
            if (tool.nextActiveTool != null) {
                tool.nextActiveTool.prevActiveTool = tool.prevActiveTool;
            }
            tool.prevActiveTool = lastActiveTool;
            if (lastActiveTool != null) {
                lastActiveTool.nextActiveTool = tool;
                if (lastActiveTool.isActive()) {
                    lastActiveTool.setActive(false);
                }
            }
            lastActiveTool = tool;
        }
        if (!tool.isActive()) {
            tool.setActive(true);
        }
    }

    /**
     * Is called when the active view or session has changed.
     * 
     * @param view
     *            the currently active view.
     */
    public void viewChanged(T view) {
        if (lastActiveTool != null && lastActiveTool.isActive()) {
            environment.setView(view);
            lastActiveTool.reset();
        }
    }

    /**
     * Returns the tool of this entry which comes first after the specified
     * tool.
     * 
     * @param tool
     *            the tool for which to return the first tool that comes after.
     * @return the tool of this entry which comes first after the specified tool
     *         concerning the position of their related tool buttons in the tool
     *         bar.
     */
    public Tool<T> getSuccessor(Tool<T> tool) {
        for (Tool<T> t : tools.tailSet(tool)) {
            if (t != tool && !(t instanceof ToolDummy<?>))
                return t;
        }
        return null;
    }

    /**
     * Moves the specified tool one position upwards.
     * 
     * @param tool
     *            the tool to move one position upwards.
     */
    public void moveUp(Tool<T> tool) {
        SortedSet<Tool<T>> set = tools.headSet(tool);
        if (set.isEmpty())
            return;
        Tool<T> other = set.last();
        exchange(tool, other);
    }

    /**
     * Moves the specified tool one position downwards.
     * 
     * @param tool
     *            the tool to move one position downwards.
     */
    public void moveDown(Tool<T> tool) {
        Iterator<Tool<T>> iter = tools.tailSet(tool).iterator();
        iter.next();
        if (!iter.hasNext())
            return;
        Tool<T> other = iter.next();
        exchange(tool, other);
    }

    /**
     * Exchanges the position of the specified tools.
     * 
     * @param tool1
     *            the first tool, which gets the position of the second tool.
     * @param tool2
     *            the second tool, which gets the postition of the first tool.
     */
    private void exchange(Tool<T> tool1, Tool<T> tool2) {
        int p1 = tool1.getPosition();
        int p2 = tool2.getPosition();
        tools.remove(tool1);
        tools.remove(tool2);
        tool1.setPosition(p2);
        tool2.setPosition(p1);
        tools.add(tool1);
        tools.add(tool2);
    }

    /**
     * Resets the specified tool.
     * 
     * @param tool
     *            the tool to reset.
     * @see Tool#reset()
     * @see Tool#reseted(ToolEnvironment)
     * @see ToolRegistry#resetTool(Tool)
     */
    public void resetTool(Tool<T> tool) {
        T view = environment.getView();
        if (view != null) {
            for (ToolAction<T> action : viewFamily.getActions().values()) {
                action.reset();
            }
            view.getGestureFeedbackProvider().reset();
            tool.reseted(environment);
        }
    }

    /**
     * Returns the set of tools of this entry.
     * 
     * @return the set of tools of this entry.
     */
    public SortedSet<Tool<T>> getTools() {
        return tools;
    }

    /**
     * Creates a new tool using the specified factory. The new tool is assigned
     * a random id.
     * 
     * @param factory
     *            the factory used to create the new tool.
     * @return a new tool created by the specified factory.
     */
    public Tool<T> createTool(ToolFactory factory) {
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < 32; i++) {
            buffer.append(Integer.toHexString((int) (Math.random() * 16)));
        }
        Tool<T> tool = factory.create(buffer.toString(), viewFamily);
        tool.preferences.put("factory", factory.getId());
        tool.isReadOnly = false;
        addTool(tool);
        return tool;
    }

    /**
     * Is called when the user performs a gesture while a tool of this entry is
     * active. The active tool is notified by
     * {@link Tool#gesturePerformed(ToolEnvironment)}.
     * 
     * @param gesture
     *            the gesture performed by the user.
     */
    public void gesturePerformed(UserGesture gesture) {
        if (lastActiveTool != null && lastActiveTool.isActive()) {
            environment.setUserGesture(gesture);
            lastActiveTool.gesturePerformed(environment);
        }
    }

    /**
     * Is called when the user cancels his sequence of user actions while a tool
     * of this entry is active. The active tool is reseted.
     */
    public void canceled() {
        if (lastActiveTool != null && lastActiveTool.isActive()) {
            lastActiveTool.reset();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
