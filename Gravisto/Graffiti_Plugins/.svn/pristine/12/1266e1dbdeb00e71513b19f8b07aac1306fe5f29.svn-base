package org.graffiti.plugins.tools.toolcustomizer;

import javax.swing.ImageIcon;

import org.graffiti.core.Bundle;
import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.tool.Tool;
import org.graffiti.plugin.tool.ToolPopupMenuProvider;
import org.graffiti.plugin.tool.ToolRegistry;
import org.graffiti.plugin.view.interactive.InteractiveView;
import org.graffiti.plugins.tools.commonactions.AddRectSelection;
import org.graffiti.plugins.tools.commonactions.AddSingleToSelection;
import org.graffiti.plugins.tools.commonactions.CreateUnsnappedNode;
import org.graffiti.plugins.tools.commonactions.SetCursor;
import org.graffiti.plugins.tools.commonactions.SetRectSelection;
import org.graffiti.plugins.tools.commonactions.SetSingleSelection;
import org.graffiti.plugins.tools.commonactions.ShowPopupMenu;
import org.graffiti.plugins.tools.commonactions.StartAddRectSelection;
import org.graffiti.plugins.tools.toolcustomizer.dialog.ConfigurationDialog;
import org.graffiti.util.VoidCallback;

public final class ToolCustomizerPlugin extends GenericPluginAdapter {
    private static ToolCustomizerPlugin singleton;

    private static Bundle bundle = Bundle.getBundle(ToolCustomizerPlugin.class);

    protected static ToolCustomizerPlugin get() {
        return singleton;
    }

    public static String getString(String key) {
        return bundle.getString(key);
    }

    public static ImageIcon createIcon(String path) {
        return new ImageIcon(ToolCustomizerPlugin.class.getResource(path));
    }

    private ShowCustomizerDialogAction showCustomizerDialogAction;

    public ToolCustomizerPlugin() {
        singleton = this;
        showCustomizerDialogAction = new ShowCustomizerDialogAction();
        ToolRegistry.get().executeChanges(new VoidCallback<ToolRegistry>() {
            public void call(ToolRegistry registry) {
                registry.addPopupMenuProvider(new ToolPopupMenuProvider() {
                    public GraffitiAction provideActionForTool(Tool<?> tool) {
                        showCustomizerDialogAction.setTool(tool);
                        return showCustomizerDialogAction;
                    }
                });

                registry.addCommonAction(new AddSingleToSelection());
                registry.addCommonAction(new SetSingleSelection());
                registry.addCommonAction(new SetRectSelection());
                StartAddRectSelection sars = new StartAddRectSelection();
                registry.addCommonAction(sars);
                registry.addCommonAction(new AddRectSelection(sars));
                registry.addCommonAction(new CreateUnsnappedNode());
                registry.addCommonAction(new SetCursor());
                registry.addCommonAction(new ShowPopupMenu());
            }
        });
    }

    protected <T extends InteractiveView<T>> void showDialog(Tool<T> tool) {
        (new ConfigurationDialog<T>(tool.getViewFamily())).show(tool);
    }
}
