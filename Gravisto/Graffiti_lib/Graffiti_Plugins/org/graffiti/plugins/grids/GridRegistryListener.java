package org.graffiti.plugins.grids;

import org.graffiti.plugin.view.Grid;

/**
 * Classes implementing {@code GridRegistryListener} are interested in being
 * notified when {@code Grid} types are registered or deregistered at the grid
 * registry.
 * 
 * @see Grid
 * @see GridRegistry
 */
public interface GridRegistryListener {
    /**
     * Is called when the specified grid type is registered at the grid
     * registry.
     * 
     * @param gridClass
     *            the {@code Class}-object representing the grid type being
     *            registered at the grid registry.
     * @see GridRegistry
     */
    public void gridAdded(Class<? extends Grid> gridClass);

    /**
     * Is called when the specified grid type is deregistered at the grid
     * registry.
     * 
     * @param gridClass
     *            the {@code Class}-object representing the grid type being
     *            deregistered at the grid registry.
     * @see GridRegistry
     */
    public void gridRemoved(Class<? extends Grid> gridClass);
}
