package org.graffiti.plugins.shapes;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.view.GraphElementShape;
import org.graffiti.plugins.shapes.edges.circleLineSegmentationShape.CircleLineSegmentationShape;
import org.graffiti.plugins.shapes.edges.toricalEdgeShape.ToricalEdgeShape;
import org.graffiti.plugins.shapes.nodes.dfa.EndNodeShape;
import org.graffiti.plugins.shapes.nodes.dfa.StartNodeShape;
import org.graffiti.plugins.shapes.nodes.polygon.PolygonalNodeShape;

/**
 * Plugin providing the {@code ShapeRegistry} and common {@code
 * GraphElementShape} classes.
 * 
 * @author donig
 * @see GraphElementShape
 * @see ShapeRegistry
 */
public final class ShapeRegistryPlugin extends EditorPluginAdapter {
    /**
     * The shape registry.
     */
    private ShapeRegistry registry;

    /**
     * Constructs a {@code ShapeRegistryPlugin} and registers common {@code
     * GraphElementShape} classes.
     * 
     * @see GraphElementShape
     */
    public ShapeRegistryPlugin() {
        registry = new ShapeRegistry(this);

        // You should not add your own shape class to this list but rather call
        //
        // ShapeRegistry.get().registerShape(...)
        //
        // from your plugin and add
        //
        // <deps>
        // <plugindesc>
        // <name>Shape Registry</name>
        // <main>org.graffiti.plugins.shapes.ShapeRegistryPlugin</main>
        // <version>1.0.0</version>
        // <available></available>
        // </plugindesc>
        // </deps
        //
        // to your plugin.xml.
        registry.registerShape(CircleLineSegmentationShape.class);
        registry.registerShape(EndNodeShape.class);
        registry.registerShape(PolygonalNodeShape.class);
        registry.registerShape(StartNodeShape.class);
        registry.registerShape(ToricalEdgeShape.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (registry != null) {
            if (ShapeRegistry.plugin == this) {
                ShapeRegistry.plugin = null;
            }
            registry = null;
        }
    }

    /**
     * Returns the shape registry.
     * 
     * @return the shape registry.
     */
    protected ShapeRegistry getRegistry() {
        return registry;
    }
}
