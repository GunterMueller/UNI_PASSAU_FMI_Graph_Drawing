package org.graffiti.plugins.scripting;

/**
 * Classes implementing {@code ScriptingRegistryListener} are interested in the
 * registration of new scripting engines.
 * 
 * @see ScriptingRegistry#addListener(ScriptingRegistryListener)
 * @see ScriptingEngine
 */
public interface ScriptingRegistryListener {
    public void engineRegistered(String id, ScriptingEngine engine);
}
