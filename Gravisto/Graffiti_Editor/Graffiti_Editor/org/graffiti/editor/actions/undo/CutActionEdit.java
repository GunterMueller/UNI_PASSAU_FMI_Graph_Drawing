// =============================================================================
//
//   CutActionEdit.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CutActionEdit.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.editor.actions.undo;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
 * Class performs the cut-action and is responsible for correct undo and redo
 * operations
 * 
 * @author MH
 */
public class CutActionEdit extends GraphElementsEdit implements ClipboardOwner {

    /**
     * 
     */
    private static final long serialVersionUID = -7076410293566528334L;

    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(CutActionEdit.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The mainframe from which the elements are cut */
    private MainFrame mainFrame;

    /** The graph elements, which are currently selected and will be cut */
    private Selection graphElements;

    /** If not yet executed any undo or redo operations will not be performed */
    private boolean executed = false;

    /**
     * Creates a new CutActionEdit
     * 
     * @param selection
     *            the currently selected items which should be cut
     * @param graph
     *            the graph in which cut, undo and redo operation should be
     *            performed
     * @param geMap
     *            saves relationships between cut elements and their inserted
     *            copies.
     * @param mainFrame
     *            a <code>MainFrame</code> object from which the elements are
     *            cut.
     */
    public CutActionEdit(Selection selection, Graph graph,
            Map<GraphElement, GraphElement> geMap, MainFrame mainFrame) {
        super(graph, geMap);
        this.mainFrame = mainFrame;

        // Get a copy of the currently selected items
        try {
            graphElements = (Selection) selection.clone();
        } catch (CloneNotSupportedException e) {
            logger.fine("CutActionEdit: Error while copying selection!");
        }
    }

    /**
     * Returns a name for the performed operations.
     * 
     * @return name a <code>String</code> naming the performed operations.
     */
    @Override
    public String getPresentationName() {
        Collection<GraphElement> elements = graphElements.getElements();
        return coreBundle.getString(elements.size() == 1 ? (elements.iterator()
                .next() instanceof Node ? "undo.cutNode" : "undo.cutEdge")
                : "undo.cutGraphElements");
    }

    /**
     * Performs the cut-action
     */
    @Override
    public void execute() {
        executed = true;

        // Handles edges, which are cut incorrectly
        HashSet<Edge> incorrectCutEdges = (HashSet<Edge>) removeCriticalEdges(graphElements);

        for (Edge edge : graphElements.getEdges()) {
            graph.deleteEdge((Edge) getCurrentGraphElement(edge));
        }

        for (Node node : graphElements.getNodes()) {
            graph.deleteNode((Node) getCurrentGraphElement(node));
        }

        Selection selection = new Selection();

        try {
            selection = (Selection) graphElements.clone();
        } catch (CloneNotSupportedException e) {
            logger.fine("CutActionEdit: Error while copying selection!");

            return;
        }

        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        ClipboardContents contents = new ClipboardContents(selection, 0);
        clip.setContents(contents, this);
        graphElements.addAll(incorrectCutEdges);
    }

    /**
     * Calls all selection listeners as soon as any application writes to the
     * system clipboard to check whether the paste button should still be
     * enabled
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
     * Performs the redo action
     */
    @Override
    public void redo() {
        super.redo();

        if (!executed)
            return;

        for (Edge edge : graphElements.getEdges()) {
            graph.deleteEdge((Edge) getCurrentGraphElement(edge));
        }

        for (Node node : graphElements.getNodes()) {
            graph.deleteNode((Node) getCurrentGraphElement(node));
        }
    }

    /**
     * Performs the undo operation
     */
    @Override
    public void undo() {
        super.undo();

        if (!executed)
            return;

        for (Node node : graphElements.getNodes()) {
            geMap.put(node, graph
                    .addNodeCopy((Node) getCurrentGraphElement(node)));
        }

        for (Edge edge : graphElements.getEdges()) {
            Node source = (Node) getCurrentGraphElement(edge.getSource());
            Node target = (Node) getCurrentGraphElement(edge.getTarget());
            geMap.put(edge, graph.addEdgeCopy(edge, source, target));
        }
    }

    /**
     * Checks if the edge is cut correctly, see below for the criteria, which
     * must be satisfied
     * 
     * @param markedItems
     *            all items which should be cut
     * @param edge
     *            the edge to test
     * 
     * @return true if the edge is cut correctly
     */
    private boolean edgeCutCorrectly(Selection markedItems, Edge edge) {
        Node source = (Node) getCurrentGraphElement(edge.getSource());
        Node target = (Node) getCurrentGraphElement(edge.getTarget());

        // The source of the edge will be cut but not the target
        if (markedItems.contains(source) && !markedItems.contains(target))
            return false;

        // The target of the edge will be cut but not the source
        if (markedItems.contains(target) && !markedItems.contains(source))
            return false;

        // The edge will be cut but not its target and source
        if (markedItems.contains(edge) && !markedItems.contains(source)
                && !markedItems.contains(target))
            return false;

        // The target and the source will be cut but not the edge
        if (!markedItems.contains(edge) && markedItems.contains(target)
                && markedItems.contains(source))
            return false;

        return true;
    }

    /**
     * Removes incorrectly cut edges. These edges cannot be pasted into any
     * graph, but have to be re-inserted by the undo operation
     * 
     * @param markedItems
     *            all the selected items
     * 
     * @return all edges which were cut incorrectly
     */
    private Set<Edge> removeCriticalEdges(Selection markedItems) {
        HashSet<Edge> removedEdges = new HashSet<Edge>();
        Iterator<Edge> checkAllEdges = graph.getEdgesIterator();

        while (checkAllEdges.hasNext()) {
            Edge edge = checkAllEdges.next();
            edge = (Edge) getCurrentGraphElement(edge);

            // Check if the edges were cut correctly
            if (!edgeCutCorrectly(markedItems, edge)) {
                logger.info("CutAction: Edge lost due to critical Cut-Action!");
                markedItems.remove(edge);
                removedEdges.add(edge);
            }
        }
        for (Edge edge : removedEdges) {
            graph.deleteEdge(edge);
        }

        return removedEdges;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
