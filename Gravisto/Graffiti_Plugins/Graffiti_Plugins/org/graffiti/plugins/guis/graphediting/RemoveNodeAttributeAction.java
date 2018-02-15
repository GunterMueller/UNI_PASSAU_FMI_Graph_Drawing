// =============================================================================
//
//   RemoveNodeAttributeAction.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RemoveNodeAttributeAction.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.guis.graphediting;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;
import org.graffiti.util.CoreGraphEditing;

/**
 * Represents a &quot;select all graph elements&quot; action.
 * 
 * @version $Revision: 5766 $
 */
public class RemoveNodeAttributeAction extends SelectionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 4238994433064511681L;

    /**
     * Constructs a new copy action.
     * 
     */
    public RemoveNodeAttributeAction() {
        super(GraphEditingBundle.getString("menu.removeNodeAttributeAction"),
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
        String attribute = JOptionPane.showInputDialog(mainFrame,
                GraphEditingBundle.getString("menu.removeEdgeAttributeAction."
                        + "inputDialog"), GraphEditingBundle
                        .getString("menu.removeNodeAttributeAction"),
                JOptionPane.QUESTION_MESSAGE);

        Selection selection = mainFrame.getActiveEditorSession()
                .getSelectionModel().getActiveSelection();

        try {

            if (selection.getNodes().isEmpty()) {
                Graph graph = mainFrame.getActiveEditorSession().getGraph();
                CoreGraphEditing.removeAttribute(graph.getNodes(), attribute);
            } else {
                CoreGraphEditing.removeAttribute(selection.getNodes(),
                        attribute);
            }
        } catch (AttributeNotFoundException ex) {
            JOptionPane
                    .showMessageDialog(
                            mainFrame,
                            ex.getMessage(),
                            GraphEditingBundle
                                    .getString("menu.removeEdgeAttributeAction.messageDialog"),
                            JOptionPane.ERROR_MESSAGE);
        }
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
