package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;
import java.util.List;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Node;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.util.CoreGraphEditing;

/**
 * Represents a &quot;select all graph elements&quot; action.
 * 
 * @version $Revision: 2821 $
 */
public class SelectConnectedComponentAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = 7120436917771524325L;

    /**
     * Constructs a new copy action.
     * 
     */
    public SelectConnectedComponentAction() {
        super(GraphEditingBundle
                .getString("menu.selectConnectedComponentAction"),
                GraffitiSingleton.getInstance().getMainFrame());
        mainFrame.addSelectionListener(new SelectGraphElementActionListener(
                this));
    }

    /**
     * @see org.graffiti.plugin.actions.GraffitiAction#getHelpContext()
     */
    @Override
    public HelpContext getHelpContext() {
        return null; // TODO
    }

    /**
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (!mainFrame.isSessionActive())
            return;
        List<Node> nodes = mainFrame.getActiveEditorSession()
                .getSelectionModel().getActiveSelection().getNodes();
        if (nodes.size() != 1)
            return;
        mainFrame
                .getActiveEditorSession()
                .getSelectionModel()
                .setActiveSelection(
                        CoreGraphEditing.selectConnectedComponent(nodes.get(0)));
    }

    /**
     * @see org.graffiti.plugin.actions.SelectionAction#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        try {
            return mainFrame.getActiveEditorSession().getSelectionModel()
                    .getActiveSelection().getNodes().size() == 1;
        } catch (NullPointerException e) {
            return false;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
