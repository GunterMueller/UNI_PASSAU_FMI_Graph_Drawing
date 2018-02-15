package org.graffiti.plugins.tools.scripted;

import java.util.Map;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugin.tool.ToolRegistry;
import org.graffiti.plugins.scripting.ScriptingEngine;
import org.graffiti.plugins.scripting.ScriptingRegistry;
import org.graffiti.plugins.scripting.ScriptingRegistryListener;

/**
 * Plugin providing a way to specify tools by script code.
 * 
 * @see ScriptedTool
 */
public final class ScriptedToolPlugin extends EditorPluginAdapter implements
        ScriptingRegistryListener {
    private static Bundle BUNDLE = Bundle.getBundle(ScriptedToolPlugin.class);

    public ScriptedToolPlugin() {
        ToolRegistry toolRegistry = ToolRegistry.get();
        for (Map.Entry<String, ScriptingEngine> entry : ScriptingRegistry.get()
                .getEngines()) {
            toolRegistry.registerToolFactory(new ScriptedToolFactory(entry
                    .getKey(), entry.getValue().getName()));
        }
        ScriptingRegistry.get().addListener(this);
    }

    public void engineRegistered(String id, ScriptingEngine engine) {
        ToolRegistry.get().registerToolFactory(
                new ScriptedToolFactory(id, engine.getName()));
    }

    public static String getString(String key) {
        return BUNDLE.getString(key);
    }

    public static String format(String key, Object... args) {
        return BUNDLE.format(key, args);
    }
}
