package org.graffiti.plugins.shapes;

import org.graffiti.plugin.view.GraphElementShape;

/**
 * Classes implementing {@code ShapeRegistryListener} are interested in being
 * notified when {@code GraphElementShape} types are registered or deregistered
 * at the shape registry.
 * 
 * @see GraphElementShape
 * @see ShapeRegistry
 */
public interface ShapeRegistryListener {
    /**
     * Is called when the specified shape type is registered at the shape
     * registry.
     * 
     * @param shapeClass
     *            the {@code Class}-object representing the shape type being
     *            registered at the shape registry.
     * @see ShapeRegistry
     */
    public void shapeAdded(Class<? extends GraphElementShape> shapeClass);

    /**
     * Is called when the specified shape type is deregistered at the shape
     * registry.
     * 
     * @param shapeClass
     *            the {@code Class}-object representing the shape type being
     *            deregistered at the shape registry.
     * @see ShapeRegistry
     */
    public void shapeRemoved(Class<? extends GraphElementShape> shapeClass);
}
