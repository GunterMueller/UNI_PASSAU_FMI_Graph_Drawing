package org.graffiti.plugins.scripting;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.EditorPluginAdapter;

/**
 * Plugin providing the {@code ScriptingRegistry}.
 * 
 * @author Andreas Glei&szlig;ner
 * @see ScriptingRegistry
 */
public final class ScriptingPlugin extends EditorPluginAdapter {
    private static final Bundle BUNDLE = Bundle
            .getBundle(ScriptingPlugin.class);

    public static String getString(String key) {
        return BUNDLE.getString(key);
    }

    public static String format(String key, Object... args) {
        return BUNDLE.format(key, args);
    }

    /**
     * The scripting registry.
     */
    private ScriptingRegistry registry;

    /**
     * Constructs a {@code ScriptingPlugin}.
     */
    public ScriptingPlugin() {
        registry = new ScriptingRegistry(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void stop() {
        if (registry != null) {
            if (ScriptingRegistry.plugin == this) {
                ScriptingRegistry.plugin = null;
            }
            registry = null;
        }
    }

    /**
     * Returns the scripting registry.
     * 
     * @return the scripting registry.
     */
    protected ScriptingRegistry getRegistry() {
        return registry;
    }
}
