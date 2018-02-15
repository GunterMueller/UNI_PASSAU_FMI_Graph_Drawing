package org.graffiti.plugins.tools.scripted;

import javax.swing.SwingUtilities;

import org.graffiti.plugin.tool.Tool;
import org.graffiti.plugin.tool.ToolEnvironment;
import org.graffiti.plugin.view.interactive.GestureFeedbackProvider;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.ViewFamily;
import org.graffiti.plugins.scripting.ConsoleOutput;
import org.graffiti.plugins.scripting.ConsoleProvider;
import org.graffiti.plugins.scripting.ResultCallbackAdapter;
import org.graffiti.plugins.scripting.Script;
import org.graffiti.plugins.scripting.ScriptingEngine;
import org.graffiti.plugins.scripting.ScriptingRegistry;
import org.graffiti.plugins.tools.scripted.dialog.ScriptedToolEditor;
import org.graffiti.plugins.tools.toolcustomizer.CustomizableTool;
import org.graffiti.plugins.tools.toolcustomizer.ToolEditor;

/**
 * Tool specified by script code.
 * 
 * @author Andreas Glei&szlig;ner
 */
public class ScriptedTool<T extends InteractiveView<T>> extends Tool<T>
        implements CustomizableTool {
    /**
     * The source code of the script.
     */
    private String source;

    /**
     * Identifies the scripting language.
     * 
     * @see ScriptingRegistry#registerEngine(String, ScriptingEngine)
     */
    private String language;

    /**
     * The scope in which the script is executed.
     */
    private ToolScope<T> scope;

    /**
     * Is {@code true} while there is a tool reset command enqueued in the Swing
     * event queue.
     */
    private boolean isResetting;

    /**
     * The script specifying the tool.
     */
    private Script script;

    /**
     * Constructs a scripted tool.
     * 
     * @param viewFamily
     *            the view family to support by the tool to construct.
     * @param id
     *            the id of the tool to construct.
     * @param language
     *            the language of the script code that specifies the tool to
     *            construct.
     */
    public ScriptedTool(ViewFamily<T> viewFamily, String id, String language) {
        super(viewFamily, id);
        this.language = language;
        source = preferences.get("source", "");
        isResetting = false;
    }

    /**
     * Constructs a scripted tool.
     * 
     * @param viewFamily
     *            the view family to support by the tool to construct.
     * @param id
     *            the id of the tool to construct.
     * @param source
     *            the source code of the script specifying the tool to
     *            construct.
     * @param language
     *            the language of the script code that specifies the tool to
     *            construct.
     */
    public ScriptedTool(ViewFamily<T> viewFamily, String id, String source,
            String language) {
        super(viewFamily, id);
        this.source = source;
        this.language = language;
        preferences.put("source", source);
        preferences.put("language", language);
        isResetting = true;
    }

    /**
     * {@inheritDoc}
     */
    public String getCustomizingDescription() {
        return ScriptedToolPlugin.format("customizingDescription", language);
    }

    /**
     * {@inheritDoc}
     */
    public ToolEditor createEditor() {
        return new ScriptedToolEditor<T>(this);
    }

    /**
     * Returns the source code of the script.
     * 
     * @return the source code of the script.
     */
    public String getSource() {
        return source;
    }

    /**
     * Sets the source code of the script.
     * 
     * @param source
     *            the source code to set.
     */
    public void setSource(String source) {
        if (isReadOnly())
            return;
        this.source = source;
        preferences.put("source", source);
        createScript();
    }

    /**
     * {@inheritDoc} This implementation does not react immediately but enqueues
     * the changes in the Swing event queue.
     */
    @Override
    protected void reseted(final ToolEnvironment<T> env) {
        isResetting = true;
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                isResetting = false;
                scope = new ToolScope<T>(ScriptingRegistry.get().getViewScope(
                        env.getView()), env, ScriptedTool.this);
                scope.setActivated(true);
                env.setUserGesture(null);
                createScript();
                executeScript(env);
                scope.setActivated(false);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void gesturePerformed(ToolEnvironment<T> env) {
        if (isResetting)
            return;
        executeScript(env);
    }

    /**
     * Executes the script code specifying this tool
     * 
     * @param env
     *            the environment of this tool.
     */
    private void executeScript(final ToolEnvironment<T> env) {
        scope.setEnvironment(env);
        if (script == null) {
            createScript();
        }
        if (script == null)
            return;
        script.execute(new ResultCallbackAdapter() {
            @Override
            public void reportError(String message) {
                GestureFeedbackProvider gfp = env.getView()
                        .getGestureFeedbackProvider();
                if (gfp instanceof ConsoleProvider) {
                    ((ConsoleProvider) gfp).print(message + "\n",
                            ConsoleOutput.Error);
                }
            }
        });
    }

    /**
     * Recreates the script after the source code or scripting language has
     * changed.
     */
    private void createScript() {
        ScriptingEngine engine = ScriptingRegistry.get().getEngine(language);
        if (engine == null) {
            script = null;
        } else {
            script = engine.createScript(source, scope);
        }
    }

    /**
     * Returns the language of the script specifying this tool.
     * 
     * @return the language of the script specifying this tool.
     */
    public String getLanguage() {
        ScriptingEngine engine = ScriptingRegistry.get().getEngine(language);
        return engine == null ? language : engine.getName();
    }
}
