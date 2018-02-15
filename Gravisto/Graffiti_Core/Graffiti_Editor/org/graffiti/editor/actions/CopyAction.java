// =============================================================================
//
//   CopyAction.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CopyAction.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.editor.actions;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import org.graffiti.editor.MainFrame;
import org.graffiti.editor.actions.cutcopypaste.ClipboardContents;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.help.HelpContext;
import org.graffiti.plugin.actions.SelectionAction;
import org.graffiti.selection.Selection;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Implementation of an action, which copies nodes and edges from the graph in
 * order to paste it to the same or another graph.
 * 
 * @author MH
 */
public class CopyAction extends SelectionAction implements ClipboardOwner {

    /**
     * 
     */
    private static final long serialVersionUID = 2033558992474980223L;
    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(CopyAction.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /**
     * Creates a new CopyAction
     * 
     * @param mainFrame
     *            the MainFrame where the CopyAction takes place
     */
    public CopyAction(MainFrame mainFrame) {
        super("edit.copy", mainFrame);

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
     * the graph and creating an instance of the CopyActionEdit class, which
     * will perform the copy action
     * 
     * @param e
     *            not used
     */
    public void actionPerformed(ActionEvent e) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();

        // The else-case should not happen: if not enabled, the
        // Cut-Button cannot be used
        if (this.isEnabled()) {
            Selection selection = new Selection();

            // Gets the current selection from the graph
            try {
                selection = (Selection) mainFrame.getActiveEditorSession()
                        .getSelectionModel().getActiveSelection().clone();
            } catch (CloneNotSupportedException e1) {
                logger.fine("CopyAction: Error while copying selection");

                return;
            }

            removeCriticalEdges(selection);

            ClipboardContents contents = new ClipboardContents(selection, 1);
            clip.setContents(contents, this);

            // By calling this method the SelectionListener is
            // called to enable the Paste-Button
            mainFrame.getActiveEditorSession().getSelectionModel()
                    .selectionChanged();
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

    /**
     * Checks if the edge is copied correctly, see below for the criteria, which
     * must be satisfied
     * 
     * @param markedItems
     *            all items which should be copied
     * @param edge
     *            the edge to test
     * 
     * @return true if the edge is copied correctly
     */
    private boolean edgeCopiedCorrectly(Selection markedItems, Edge edge) {
        Node source = edge.getSource();
        Node target = edge.getTarget();

        // The source of the edge will be copied but not the target
        if (markedItems.contains(source) && !markedItems.contains(target))
            return false;

        // The target of the edge will be copied but not the source
        if (markedItems.contains(target) && !markedItems.contains(source))
            return false;

        // The edge will be copied but not its target and source
        if (markedItems.contains(edge) && !markedItems.contains(source)
                && !markedItems.contains(target))
            return false;

        return true;
    }

    /**
     * Removes incorrectly cut edges, this means edges whose target is cut but
     * not their source or whose source is cut but not their target. These edges
     * obviously cannot be pasted into any graph, but have to be re-inserted by
     * the undo operation
     * 
     * @param markedItems
     *            all the selected items
     * 
     * @return all edges which were cut incorrectly
     */
    private Set<Edge> removeCriticalEdges(Selection markedItems) {
        HashSet<Edge> removedEdges = new HashSet<Edge>();

        for (Edge edge : markedItems.getEdges()) {
            // Check if the edges were cut correctly
            if (!edgeCopiedCorrectly(markedItems, edge)) {
                logger
                        .info("CopyAction: Edge not copied due to critical Copy-Action!");
                removedEdges.add(edge);
            }
        }

        for (Edge edge : removedEdges) {
            markedItems.remove(edge);
        }

        return removedEdges;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
