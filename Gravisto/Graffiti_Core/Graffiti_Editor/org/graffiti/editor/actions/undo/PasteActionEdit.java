// =============================================================================
//
//   PasteActionEdit.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: PasteActionEdit.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.editor.actions.undo;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import org.graffiti.editor.MainFrame;
import org.graffiti.editor.actions.cutcopypaste.ClipboardContents;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.selection.Selection;
import org.graffiti.undo.GraphElementsEdit;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Class performes the paste-action and is responsible for correct undo and redo
 * operations.
 * 
 * @author MH
 */
public class PasteActionEdit extends GraphElementsEdit implements
        ClipboardOwner {

    /**
     * 
     */
    private static final long serialVersionUID = -9001686383444990862L;

    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(PasteActionEdit.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The mainframe in which the pasted Items will be marked */
    private MainFrame mainFrame;

    /** DOCUMENT ME! */
    private Selection copiedItems;

    /** The elements from the clipboard */
    private Selection selection;

    /**
     * Creates a new PasteActionEdit
     * 
     * @param selection
     *            items that will be pasted
     * @param graph
     *            the graph in which paste, undo and redo operation should be
     *            performed
     * @param geMap
     *            saves relationships between elements and their inserted copies
     * @param mainFrame
     *            the mainframe in which the pastedItems will be marked
     */
    public PasteActionEdit(Selection selection, Graph graph,
            Map<GraphElement, GraphElement> geMap, MainFrame mainFrame) {
        super(graph, geMap);
        this.selection = selection;
        this.mainFrame = mainFrame;
    }

    /**
     * Returns a name for the performed operations
     * 
     * @return name
     */
    @Override
    public String getPresentationName() {
        return "Paste";
    }

    /**
     * Performes the paste-action
     */
    @Override
    public void execute() {
        this.addItems(selection);
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
     * Performes the redo action
     */
    @Override
    public void redo() {
        super.redo();
        this.addItems(selection);
    }

    /**
     * Performes the undo action
     */
    @Override
    public void undo() {
        super.undo();
        this.removeItems(selection);
    }

    /**
     * Adds items to the graph
     * 
     * @param itemsToAdd
     *            items that will be added
     */
    private void addItems(Selection itemsToAdd) {
        try {
            mainFrame.getActiveEditorSession().getSelectionModel()
                    .getActiveSelection().clear();

            // Gets the system Clipboard and creates a Transferable
            // that will be written into the Clipboard
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            ClipboardContents contents = (ClipboardContents) clip.getContents(
                    null).getTransferData(ClipboardContents.selectionFlavor);
            Selection elementsRemovedFromGe = new Selection();

            Iterator<Node> insertNodes = itemsToAdd.getNodes().iterator();
            Iterator<Edge> insertEdges = itemsToAdd.getEdges().iterator();

            if (copiedItems != null) {
                insertNodes = copiedItems.getNodes().iterator();
                insertEdges = copiedItems.getEdges().iterator();
            }

            // Add the nodes first
            while (insertNodes.hasNext()) {
                Node node = insertNodes.next();
                node = (Node) getCurrentGraphElement(node);
                elementsRemovedFromGe.add(node);

                Node newNode = graph.addNodeCopy(node);
                geMap.put(node, newNode);
                mainFrame.getActiveEditorSession().getSelectionModel()
                        .getActiveSelection().add(newNode);
            }

            // Then add the edges
            while (insertEdges.hasNext()) {
                Edge edge = insertEdges.next();
                edge = (Edge) getCurrentGraphElement(edge);
                elementsRemovedFromGe.add(edge);

                Node source = (Node) getCurrentGraphElement(edge.getSource());
                Node target = (Node) getCurrentGraphElement(edge.getTarget());
                Edge newEdge = graph.addEdgeCopy(edge, source, target);
                geMap.put(edge, newEdge);
                mainFrame.getActiveEditorSession().getSelectionModel()
                        .getActiveSelection().add(newEdge);
            }

            // If there is already a copy of the elements in the graph, there
            // should not
            // be an entry in the GEMap
            if (contents.getNumOfCopiesInGraph() > 0) {
                copiedItems = new Selection();

                for (GraphElement ge : elementsRemovedFromGe.getElements()) {
                    GraphElement newGe = getCurrentGraphElement(ge);
                    copiedItems.add(newGe);
                    geMap.remove(ge);
                }
            }

            contents.increaseNumOfCopies();

            mainFrame.getActiveEditorSession().getSelectionModel()
                    .selectionChanged();
        }

        // Both catch cases should not happen. The paste button should be
        // disabled if there are no convenient contens in the Clipboard
        catch (UnsupportedFlavorException e) {
            logger.fine("PasteActionEdit: Error while reading from Clipboard");
        } catch (IOException e) {
            logger.fine("PasteActionEdit: Error while reading from Clipboard");
        }
    }

    /**
     * Removes items from the graph
     * 
     * @param itemsToRemove
     *            the items that should be removed
     */
    private void removeItems(Selection itemsToRemove) {
        try {
            // Gets the system Clipboard and creates a Transferable
            // that will be written into the Clipboard
            Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
            ClipboardContents contents = (ClipboardContents) clip.getContents(
                    null).getTransferData(ClipboardContents.selectionFlavor);

            Iterator<Node> removeNodes = itemsToRemove.getNodes().iterator();
            Iterator<Edge> removeEdges = itemsToRemove.getEdges().iterator();

            if (copiedItems != null) {
                removeNodes = copiedItems.getNodes().iterator();
                removeEdges = copiedItems.getEdges().iterator();
            }

            // Remove the edges first
            while (removeEdges.hasNext()) {
                Edge edge = removeEdges.next();
                edge = (Edge) getCurrentGraphElement(edge);
                graph.deleteEdge(edge);
            }

            // Remove the nodes
            while (removeNodes.hasNext()) {
                Node node = removeNodes.next();
                node = (Node) getCurrentGraphElement(node);
                graph.deleteNode(node);
            }

            contents.decreaseNumOfCopies();
        }

        // Both catch cases should not happen. The paste button shpuld be
        // disabled if there are no convenient contens in the Clipboard
        catch (UnsupportedFlavorException e) {
            logger.fine("PasteActionEdit: Error while reading from Clipboard");
        } catch (IOException e) {
            logger.fine("PasteActionEdit: Error while reading from Clipboard");
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
