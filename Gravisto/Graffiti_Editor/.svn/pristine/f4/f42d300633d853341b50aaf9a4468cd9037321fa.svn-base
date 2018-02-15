package org.graffiti.plugins.tools.demos;

import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.gui.GraffitiComponent;
import org.graffiti.plugin.tool.Tool;
import org.graffiti.plugin.tool.ToolRegistry;
import org.graffiti.util.VoidCallback;

/**
 * Plugin providing tools for demonstration purposes.
 * 
 * @author Andreas Glei&szlig;ner
 * @see Tool
 * @see ToolRegistry
 */
public final class ToolDemosPlugin extends EditorPluginAdapter {
    /**
     * Constructs a {@code ToolRegistryPlugin}.
     */
    public ToolDemosPlugin() {
        ToolRegistry.get().executeChanges(new VoidCallback<ToolRegistry>() {
            public void call(ToolRegistry registry) {
                registry.registerTool(new ExampleTool());
                registry.registerTool(new ExampleTool2());
            }
        });

        guiComponents = new GraffitiComponent[] { new ToggleModeTool() };
    }
}
