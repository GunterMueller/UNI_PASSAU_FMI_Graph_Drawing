package org.graffiti.plugins.tools.toolcustomizer;

import java.awt.event.ActionEvent;

import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.GraffitiAction;
import org.graffiti.plugin.tool.Tool;

public class ShowCustomizerDialogAction extends GraffitiAction {
    /**
     * 
     */
    private static final long serialVersionUID = 5805632913369678140L;
    private Tool<?> tool;

    public ShowCustomizerDialogAction() {
        super(ToolCustomizerPlugin.getString("showCustomizerDialogAction"),
                null);
    }

    @Override
    public HelpContext getHelpContext() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void actionPerformed(ActionEvent e) {
        if (tool == null)
            return;
        ToolCustomizerPlugin.get().showDialog(tool);
        tool = null;
    }

    public void setTool(Tool<?> tool) {
        this.tool = tool;
    }
}
