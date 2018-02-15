// =============================================================================
//
//   CutAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CutAction.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.editor.actions;

import java.awt.event.ActionEvent;
import java.util.Map;
import java.util.logging.Logger;

import javax.swing.undo.UndoableEditSupport;

import org.graffiti.editor.MainFrame;
import org.graffiti.editor.actions.undo.CutActionEdit;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Implementation of an action, which cuts nodes and edges from the graph in
 * order to paste it to the same or another graph.
 * 
 * @author MH
 */
public class CutAction extends SelectionAction {

    /**
     * 
     */
    private static final long serialVersionUID = 1423570042047784016L;
    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(CutAction.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /**
     * Creates a new CutAction
     * 
     * @param mainFrame
     *            the MainFrame where the CutAction takes place
     */
    public CutAction(MainFrame mainFrame) {
        super("edit.cut", mainFrame);

        // Used for better description in the context menu
        putValue(NAME, coreBundle.getString("menu." + getName()));
        putValue(SHORT_DESCRIPTION, getName());
        putValue(SMALL_ICON, coreBundle.getIcon("toolbar." + getName()
                + ".icon"));
    }

    /**
     * Checks the enabled flag, which tells if the CutButton in the mainFrame
     * should be enabled
     * 
     * @return the enabled flag
     */
    @Override
    public boolean isEnabled() {
        // No editor is opened: Button disabled
        if (mainFrame.getActiveEditorSession() == null)
            return false;
        else if (mainFrame.getActiveEditorSession().getSelectionModel()
                .getActiveSelection() == null)
            return false;
        else
            return (!mainFrame.getActiveEditorSession().getSelectionModel()
                    .getActiveSelection().isEmpty());
    }

    /**
     * Not yet implemented
     * 
     * @return not yet implemented
     */
    @Override
    public HelpContext getHelpContext() {
        return null; // TODO
    }

    /**
     * Performes the action of this class by getting the current selection of
     * the graph and creating an instance of the CutActionEdit class, which will
     * perform the cut action
     * 
     * @param e
     *            not used
     */
    public void actionPerformed(ActionEvent e) {
        // The else-case should not happen: if not enabled, the
        // Cut-Button cannot be used
        if (this.isEnabled()) {
            Graph graph = mainFrame.getActiveEditorSession().getGraph();
            Selection selection = new Selection();
            Map<GraphElement, GraphElement> geMap = mainFrame
                    .getActiveEditorSession().getGraphElementsMap();
            UndoableEditSupport undoSupport = mainFrame.getUndoSupport();

            // Gets the current selection from the graph
            try {
                selection = (Selection) mainFrame.getActiveEditorSession()
                        .getSelectionModel().getActiveSelection().clone();
            } catch (CloneNotSupportedException e1) {
                logger.fine("CutAction: Error while copying selection!");

                return;
            }

            CutActionEdit change = new CutActionEdit(selection, graph, geMap,
                    mainFrame);

            // Performs the cutting of the graph-elements
            change.execute();
            undoSupport.postEdit(change);
            mainFrame.getActiveEditorSession().getSelectionModel()
                    .getActiveSelection().clear();

            // By calling this method the SelectionListener is
            // called to enable the Paste-Button
            mainFrame.getActiveEditorSession().getSelectionModel()
                    .selectionChanged();
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
