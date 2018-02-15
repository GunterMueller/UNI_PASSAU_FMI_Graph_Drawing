// =============================================================================
//
//   SelectionHandler.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.views.fast;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.selection.Selection;
import org.graffiti.selection.SelectionEvent;
import org.graffiti.selection.SelectionListener;
import org.graffiti.selection.SelectionModel;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class SelectionHandler implements SelectionListener {
    private FastView fastView;
    private SelectionModel selectionModel;
    GraphicsEngine<?, ?> engine;
    private NodeChangeListener<?> nodeChangeListener;
    private EdgeChangeListener<?> edgeChangeListener;
    private Set<Node> selectedNodes;
    private Set<Edge> selectedEdges;

    protected SelectionHandler(FastView fastView) {
        this.fastView = fastView;
        engine = fastView.getGraphicsEngine();
        nodeChangeListener = engine.getNodeChangeListener();
        edgeChangeListener = engine.getEdgeChangeListener();
        selectedNodes = new HashSet<Node>();
        selectedEdges = new HashSet<Edge>();
    }

    protected void setSelectionModel(SelectionModel selectionModel) {

        Selection selection = null;
        if (selectionModel != this.selectionModel) {
            if (this.selectionModel != null) {
                this.selectionModel.removeSelectionListener(this);
            }
            this.selectionModel = selectionModel;
            if (selectionModel != null) {
                selectionModel.addSelectionListener(this);
                selection = selectionModel.getActiveSelection();
            }
        }

        Set<Node> oldSelectedNodes = selectedNodes;
        selectedNodes = new HashSet<Node>();
        Set<Edge> oldSelectedEdges = selectedEdges;
        selectedEdges = new HashSet<Edge>();
        if (selection != null) {

            for (Node node : selection.getNodes()) {
                if (engine.knows(node)) {
                    selectedNodes.add(node);
                    oldSelectedEdges.remove(node);
                    nodeChangeListener.onChangeSelection(node, true);
                }
            }

            for (Edge edge : selection.getEdges()) {
                if (engine.knows(edge)) {
                    selectedEdges.add(edge);
                    oldSelectedEdges.remove(edge);
                    edgeChangeListener.onChangeSelection(edge, true);
                }
            }
        }
        for (Node node : oldSelectedNodes) {
            nodeChangeListener.onChangeSelection(node, false);
        }
        for (Edge edge : oldSelectedEdges) {
            edgeChangeListener.onChangeSelection(edge, false);
        }
    }

    protected void close() {
        if (selectionModel != null) {
            selectionModel.removeSelectionListener(this);
        }
    }

    /**
     * @{inheritdoc
     */
    public void selectionChanged(SelectionEvent e) {
        processChange(e);
    }

    /**
     * @{inheritdoc
     */
    public void selectionListChanged(SelectionEvent e) {
        processChange(e);
    }

    private void processChange(SelectionEvent e) {
        Selection selection = e.getSelection();
        for (Map.Entry<GraphElement, Object> entry : selection.getNewUnmarked()
                .entrySet()) {
            GraphElement element = entry.getKey();
            if (element instanceof Node) {
                Node node = (Node) element;
                if (engine.knows(node)) {
                    selectedNodes.remove(node);
                    nodeChangeListener.onChangeSelection(node, false);
                }
            } else if (element instanceof Edge) {
                Edge edge = (Edge) element;
                if (engine.knows(edge)) {
                    selectedEdges.remove(edge);
                    edgeChangeListener.onChangeSelection(edge, false);
                }
            }
        }
        for (Map.Entry<GraphElement, Object> entry : selection.getNewMarked()
                .entrySet()) {
            GraphElement element = entry.getKey();
            if (element instanceof Node) {
                Node node = (Node) element;
                if (engine.knows(node)) {
                    selectedNodes.add(node);
                    nodeChangeListener.onChangeSelection(node, true);
                }
            } else if (element instanceof Edge) {
                Edge edge = (Edge) element;
                if (engine.knows(edge)) {
                    selectedEdges.add(edge);
                    edgeChangeListener.onChangeSelection(edge, true);
                }
            }
        }
        fastView.refresh();
    }

    public void sendSelectionStatus(Node node) {
        nodeChangeListener.onChangeSelection(node,
                selectionModel != null ? selectionModel.getActiveSelection()
                        .contains(node) : false);
    }

    public void sendSelectionStatus(Edge edge) {
        edgeChangeListener.onChangeSelection(edge,
                selectionModel != null ? selectionModel.getActiveSelection()
                        .contains(edge) : false);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
