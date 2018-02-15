package org.graffiti.plugins.shapes;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import org.graffiti.plugin.view.GraphElementShape;

/**
 * {@code ShapeRegistry} manages a list of publicly known shape types. Classes
 * interested in changes to the list can add listeners.
 * 
 * @author donig
 */
public final class ShapeRegistry {
    /**
     * The plugin providing this registry.
     */
    protected static ShapeRegistryPlugin plugin;

    /**
     * List of listeners interested in changes to the shape type list.
     */
    private LinkedList<ShapeRegistryListener> listeners;

    /**
     * Maps from a shape class to the count how often it was registered.
     */
    private LinkedHashMap<Class<? extends GraphElementShape>, Integer> classes;

    /**
     * Returns the {@code ShapeRegistry} singleton.
     * 
     * @return the{@code ShapeRegistry} singleton.
     * @throws IllegalStateException
     *             if the {@link ShapeRegistryPlugin} is not loaded.
     */
    public static ShapeRegistry get() {
        if (plugin != null) {
            ShapeRegistry registry = plugin.getRegistry();
            if (registry != null)
                return registry;
        }
        throw new IllegalStateException("Requires ShapeRegistry plugin.");
    }

    /**
     * Constructs a {@code ShapeRegistry}.
     * 
     * @param plugin
     *            the plugin providing this registry.
     */
    protected ShapeRegistry(ShapeRegistryPlugin plugin) {
        ShapeRegistry.plugin = plugin;
        listeners = new LinkedList<ShapeRegistryListener>();
        classes = new LinkedHashMap<Class<? extends GraphElementShape>, Integer>();
    }

    /**
     * Adds the specified listener interested in the changes to the list of
     * shape types. A listener may be added multiple times. It will be notified
     * accordingly often.
     * 
     * @param listener
     *            the listener to add, which is interested in the changes to the
     *            list of shape types.
     */
    public void addListener(ShapeRegistryListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes one instance of the specified listener from the list.
     * 
     * @param listener
     *            the listener to remove.
     * @see #addListener(ShapeRegistryListener)
     */
    public void removeListener(ShapeRegistryListener listener) {
        listeners.remove(listener);
    }

    /**
     * Returns a set of {@code Class}-objects representing the registered shape
     * types.
     * 
     * @return a set of {@code Class}-objects representing the registered shape
     *         types. It must not be modified.
     */
    public Set<Class<? extends GraphElementShape>> getShapes() {
        return classes.keySet();
    }

    /**
     * Registers the shape type represented by the specified {@code Class}
     * -object. If a shape type is registered multiple time,
     * {@link #unregisterShape(Class)} has to be called accordingly often in
     * order to completely remove the shape type from the list.
     * 
     * @param shape
     *            the {@code Class}-object representing the shape type to
     *            register.
     */
    public void registerShape(Class<? extends GraphElementShape> shape) {
        Integer c = classes.get(shape);
        if (c == null) {
            classes.put(shape, 1);
            for (ShapeRegistryListener listener : listeners) {
                listener.shapeAdded(shape);
            }
        } else {
            classes.put(shape, c + 1);
        }
    }

    /**
     * Decreases the count the shape type represented by the specified {@code
     * Class}-object is registered. If the count reaches zero, the shape type is
     * unregistered.
     * 
     * @param shape
     *            the {@code Class}-object representing the shape type to
     *            unregister.
     * @see #registerShape(Class)
     */
    public void unregisterShape(Class<? extends GraphElementShape> shape) {
        Integer c = classes.get(shape);
        if (c == null)
            return;
        if (c == 1) {
            classes.remove(shape);
            for (ShapeRegistryListener listener : listeners) {
                listener.shapeRemoved(shape);
            }
        } else {
            classes.put(shape, c - 1);
        }
    }
}
