package org.graffiti.plugins.scripting.js;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.EditorPluginAdapter;
import org.graffiti.plugins.scripting.ScriptingRegistry;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class JavaScriptPlugin extends EditorPluginAdapter {
    private static final Bundle BUNDLE = Bundle
            .getBundle(JavaScriptPlugin.class);

    private JavaScriptEngine engine;

    public JavaScriptPlugin() {
        engine = new JavaScriptEngine(this);
        ScriptingRegistry.get().registerEngine(JavaScriptEngine.LANGUAGE_ID,
                engine);
    }

    public static String getString(String key) {
        return BUNDLE.getString(key);
    }

    public static String format(String key, Object... args) {
        return BUNDLE.format(key, args);
    }

    public JavaScriptEngine getEngine() {
        return engine;
    }
}
