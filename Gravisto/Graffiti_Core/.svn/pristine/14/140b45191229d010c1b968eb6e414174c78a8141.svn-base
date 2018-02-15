package org.graffiti.plugins.tools.toolcustomizer.dialog;

import java.awt.BorderLayout;

import javax.swing.JDialog;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.plugin.tool.Tool;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugin.view.interactive.ViewFamily;
import org.graffiti.plugins.tools.toolcustomizer.ToolCustomizerPlugin;

public final class ConfigurationDialog<T extends InteractiveView<T>> extends
        JDialog {
    /**
     * 
     */
    private static final long serialVersionUID = 3998059112139378899L;
    public static final int GAP = 5;

    static BorderLayout createBorderLayout() {
        return new BorderLayout(GAP, GAP);
    }

    private ToolListPanel<T> toolListPanel;
    private ToolPanel<T> toolPanel;

    public ConfigurationDialog(ViewFamily<T> viewFamily) {
        super(GraffitiSingleton.getInstance().getMainFrame(),
                ToolCustomizerPlugin.getString("window.title"), true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        setSize(700, 350);
        setLocationByPlatform(true);
        setLayout(createBorderLayout());

        toolListPanel = new ToolListPanel<T>(viewFamily, this);
        add(toolListPanel, BorderLayout.WEST);
        toolPanel = new ToolPanel<T>(this);
        add(toolPanel, BorderLayout.CENTER);
    }

    public void show(Tool<T> tool) {
        setTool(tool);
        setVisible(true);
    }

    protected void setTool(Tool<T> tool) {
        toolListPanel.setTool(tool);
        toolPanel.setTool(tool);
    }

    protected void toolAppearanceChanged() {
        toolListPanel.toolAppearanceChanged();
        toolPanel.toolAppearanceChanged();
    }
}
