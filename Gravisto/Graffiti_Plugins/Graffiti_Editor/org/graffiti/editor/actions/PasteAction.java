// =============================================================================
//
//   PasteAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PasteAction.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.Map;

import javax.swing.undo.UndoableEditSupport;

import org.graffiti.editor.MainFrame;
import org.graffiti.editor.actions.cutcopypaste.ClipboardContents;
import org.graffiti.editor.actions.undo.PasteActionEdit;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;

/**
 * Implementation of an action, which pastes nodes and edges into a graph.
 * 
 * @author MH
 */
public class PasteAction extends SelectionAction implements ClipboardOwner {

    /**
     * 
     */
    private static final long serialVersionUID = -3910603322858272039L;

    /**
     * Creates a new PasteAction
     * 
     * @param mainFrame
     *            the MainFrame where the PasteAction takes place
     */
    public PasteAction(MainFrame mainFrame) {
        super("edit.paste", mainFrame);

        // Used for better description in the context menu
        putValue(NAME, coreBundle.getString("menu." + getName()));
        putValue(SHORT_DESCRIPTION, getName());
        putValue(SMALL_ICON, coreBundle.getIcon("toolbar." + getName()
                + ".icon"));
    }

    /**
     * Checks the enabled flag, which tells if the PasteButton in the mainFrame
     * should be enabled
     * 
     * @return the enabled flag
     */
    @Override
    public boolean isEnabled() {
        // There is no editor session active
        if (mainFrame.getActiveEditorSession() == null)
            return false;

        // Nothing or wrong data (from other applications) is saved in
        // SystemClipboard
        try {
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();

            /* fixes bug #0000026 */
            if (!clip.isDataFlavorAvailable(ClipboardContents.selectionFlavor))
                return false;

            ClipboardContents contents = (ClipboardContents) clip.getContents(
                    null).getTransferData(ClipboardContents.selectionFlavor);

            // Check if there are any cut items
            return contents != null && !contents.isEmpty();
        } catch (UnsupportedFlavorException e) {
            return false;
        } catch (IOException e) {
            return false;
        } catch (IllegalStateException e) {
            return false;
        }
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
     * Performes the action of this class by getting the currently cut items
     * from CutActionEdit.cutItems and then creates an instance of the
     * PasteActionEdit class, which will perform the paste action
     * 
     * @param e
     *            not used
     */
    public void actionPerformed(ActionEvent e) {
        // The else-case should not happen: if not enabled, the
        // Paste-Button cannot be used
        if (this.isEnabled()) {
            try {
                Clipboard clip = Toolkit.getDefaultToolkit()
                        .getSystemClipboard();
                Graph graph = mainFrame.getActiveEditorSession().getGraph();
                ClipboardContents contents = (ClipboardContents) clip
                        .getContents(null).getTransferData(
                                ClipboardContents.selectionFlavor);

                Map<GraphElement, GraphElement> geMap = mainFrame
                        .getActiveEditorSession().getGraphElementsMap();
                UndoableEditSupport undoSupport = mainFrame.getUndoSupport();
                PasteActionEdit change = new PasteActionEdit(contents, graph,
                        geMap, mainFrame);

                graph.getListenerManager().transactionStarted(this);
                // Performs the pasting of the graph-elements
                change.execute();
                graph.getListenerManager().transactionFinished(this);
                undoSupport.postEdit(change);
            } catch (UnsupportedFlavorException e2) {
                return;
            } catch (IOException e2) {
                return;
            }
        }
    }

    /**
     * Calls all selection listeners as soon as any application writes to the
     * system clipboard to check wether the paste button should still be enabled
     * 
     * @param arg0
     *            not used
     * @param arg1
     *            not used
     */
    public void lostOwnership(Clipboard arg0, Transferable arg1) {
        mainFrame.getActiveEditorSession().getSelectionModel()
                .selectionChanged();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
