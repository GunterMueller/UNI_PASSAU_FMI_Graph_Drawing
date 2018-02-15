package org.graffiti.plugins.tools.scripted;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.graffiti.plugin.tool.Tool;
import org.graffiti.plugin.tool.ToolFactory;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.ViewFamily;
import org.graffiti.plugins.tools.toolcustomizer.CustomizableToolFactory;

/**
 * Factory creating scripted tools.
 * 
 * @author Andreas Glei&szlig;ner
 */
final class ScriptedToolFactory implements ToolFactory, CustomizableToolFactory {
    /**
     * Prefix of the factory id of this factory. It is concatenated with the id
     * of the scripting language to form the real factory id.
     */
    private static final String FACTORY_ID = "scripted.";

    /**
     * Id of the scripting language of the scripts specifying the tools to
     * create by this factory.
     */
    private String languageId;

    /**
     * Text of the menu item that makes this factory create a new tool.
     */
    private String menuText;

    /**
     * Constructs a {@code ScriptedToolFactory}.
     * 
     * @param languageId
     *            id of the scripting language of the scripts specifying the
     *            tools to create by factory to construct.
     * @param menuText
     *            text of the menu item that makes the factory to construct
     *            create a new tool.
     */
    public ScriptedToolFactory(String languageId, String menuText) {
        this.languageId = languageId;
        this.menuText = menuText;
    }

    /**
     * {@inheritDoc} This implementation always returns {@code true} as it
     * supports every view family.
     */
    public boolean acceptsViewFamily(ViewFamily<?> viewFamily) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public <T extends InteractiveView<T>> Tool<T> create(String id,
            ViewFamily<T> viewFamily) {
        return new ScriptedTool<T>(viewFamily, id, languageId);
    }

    /**
     * {@inheritDoc}
     */
    public String getId() {
        return FACTORY_ID + languageId;
    }

    /**
     * {@inheritDoc}
     */
    public Icon getAddMenuIcon() {
        return new ImageIcon(ScriptedToolPlugin.class.getResource("js.png"));
    }

    /**
     * {@inheritDoc}
     */
    public String getAddMenuText() {
        return menuText;
    }
}
