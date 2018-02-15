package org.graffiti.plugins.grids;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugin.view.NoGrid;

/**
 * Plugin providing the {@code GridRegistry} and common {@code Grid} classes.
 * 
 * @author Andreas Glei&szlig;ner
 * @see Grid
 * @see GridRegistry
 */
public final class GridRegistryPlugin extends EditorPluginAdapter {
    /**
     * The grid registry.
     */
    private GridRegistry registry;

    /**
     * Constructs a {@code GridRegistryPlugin} and registers common {@code Grid}
     * classes.
     * 
     * @see Grid
     */
    public GridRegistryPlugin() {
        registry = new GridRegistry(this);

        // You should not add your own grid class to this list but rather call
        //
        // GridRegistry.get().registerGrid(...)
        //
        // from your plugin and add
        //
        // <deps>
        // <plugindesc>
        // <name>Grid Registry</name>
        // <main>org.graffiti.plugins.grids.GridRegistryPlugin</main>
        // <version>1.0.0</version>
        // <available></available>
        // </plugindesc>
        // </deps
        //
        // to your plugin.xml.
        registry.registerGrid(NoGrid.class);
        registry.registerGrid(OrthogonalGrid.class);
        registry.registerGrid(RadialGrid.class);
        registry.registerGrid(HexagonalGrid.class);
        registry.registerGrid(HexagonalGrid2.class);
        registry.registerGrid(ToricalGrid.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (registry != null) {
            if (GridRegistry.plugin == this) {
                GridRegistry.plugin = null;
            }
            registry = null;
        }
    }

    /**
     * Returns the grid registry.
     * 
     * @return the grid registry.
     */
    protected GridRegistry getRegistry() {
        return registry;
    }
}
