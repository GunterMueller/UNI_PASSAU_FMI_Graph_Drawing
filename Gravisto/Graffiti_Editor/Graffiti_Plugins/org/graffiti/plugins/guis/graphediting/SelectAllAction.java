package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.util.CoreGraphEditing;

/**
 * Represents a &quot;select all graph elements&quot; action.
 * 
 * @version $Revision: 5766 $
 */
public class SelectAllAction extends SelectionAction {
    /**
     * 
     */
    private static final long serialVersionUID = -4327338268253349266L;

    /**
     * Constructs a new copy action.
     * 
     */
    public SelectAllAction() {
        super(GraphEditingBundle.getString("menu.selectAllAction"),
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

        mainFrame.getActiveEditorSession().getSelectionModel()
                .setActiveSelection(
                        CoreGraphEditing.selectAll(mainFrame
                                .getActiveEditorSession().getGraph()));
    }

    /**
     * @see org.graffiti.plugin.actions.SelectionAction#isEnabled()
     */
    @Override
    public boolean isEnabled() {
        try {
            return !mainFrame.getActiveEditorSession().getGraph()
                    .getGraphElements().isEmpty();
        } catch (NullPointerException e) {
            return false;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
