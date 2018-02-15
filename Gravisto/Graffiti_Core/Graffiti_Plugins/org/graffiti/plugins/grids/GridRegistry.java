package org.graffiti.plugins.grids;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import org.graffiti.plugin.view.Grid;

/**
 * {@code GridRegistry} manages a list of publicly known grid types. Classes
 * interested in changes to the list can add listeners.
 * 
 * @author Andreas Glei&szlig;ner
 */
public final class GridRegistry {
    /**
     * The plugin providing this registry.
     */
    protected static GridRegistryPlugin plugin;

    /**
     * List of listeners interested in changes to the grid type list.
     */
    private LinkedList<GridRegistryListener> listeners;

    /**
     * Maps from a grid class to the count how often it was registered.
     */
    private LinkedHashMap<Class<? extends Grid>, Integer> classes;

    /**
     * Returns the {@code GridRegistry} singleton.
     * 
     * @return the{@code GridRegistry} singleton.
     * @throws IllegalStateException
     *             if the {@link GridRegistryPlugin} is not loaded.
     */
    public static GridRegistry get() {
        if (plugin != null) {
            GridRegistry registry = plugin.getRegistry();
            if (registry != null)
                return registry;
        }
        throw new IllegalStateException("Requires GridRegistry plugin.");
    }

    /**
     * Constructs a {@code GridRegistry}.
     * 
     * @param plugin
     *            the plugin providing this registry.
     */
    protected GridRegistry(GridRegistryPlugin plugin) {
        GridRegistry.plugin = plugin;
        listeners = new LinkedList<GridRegistryListener>();
        classes = new LinkedHashMap<Class<? extends Grid>, Integer>();
    }

    /**
     * Adds the specified listener interested in the changes to the list of grid
     * types. A listener may be added multiple times. It will be notified
     * accordingly often.
     * 
     * @param listener
     *            the listener to add, which is interested in the changes to the
     *            list of grid types.
     */
    public void addListener(GridRegistryListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes one instance of the specified listener from the list.
     * 
     * @param listener
     *            the listener to remove.
     * @see #addListener(GridRegistryListener)
     */
    public void removeListener(GridRegistryListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns a set of {@code Class}-objects representing the registered grid
     * types.
     * 
     * @return a set of {@code Class}-objects representing the registered grid
     *         types. It must not be modified.
     */
    public Set<Class<? extends Grid>> getGrids() {
        return classes.keySet();
    }

    /**
     * Registers the grid type represented by the specified {@code Class}
     * -object. If a grid type is registered multiple time,
     * {@link #unregisterGrid(Class)} has to be called accordingly often in
     * order to completely remove the grid type from the list.
     * 
     * @param grid
     *            the {@code Class}-object representing the grid type to
     *            register.
     */
    public void registerGrid(Class<? extends Grid> grid) {
        Integer c = classes.get(grid);
        if (c == null) {
            classes.put(grid, 1);
            for (GridRegistryListener listener : listeners) {
                listener.gridAdded(grid);
            }
        } else {
            classes.put(grid, c + 1);
        }
    }

    /**
     * Decreases the count the grid type represented by the specified {@code
     * Class}-object is registered. If the count reaches zero, the grid type is
     * unregistered.
     * 
     * @param grid
     *            the {@code Class}-object representing the grid type to
     *            unregister.
     * @see #registerGrid(Class)
     */
    public void unregisterGrid(Class<? extends Grid> grid) {
        Integer c = classes.get(grid);
        if (c == null)
            return;
        if (c == 1) {
            classes.remove(grid);
            for (GridRegistryListener listener : listeners) {
                listener.gridRemoved(grid);
            }
        } else {
            classes.put(grid, c - 1);
        }
    }
}
