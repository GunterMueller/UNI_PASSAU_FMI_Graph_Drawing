// =============================================================================
//
//   Selection.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Selection.java 6093 2012-01-12 16:13:00Z hanauer $

package org.graffiti.selection;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graffiti.attributes.FieldAlreadySetException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;

/**
 * Contains selected nodes and edges.
 * 
 * <p>
 * </p>
 * 
 * <p>
 * Even if there are fundamental changes to the selection, don't use something
 * like that:
 * </p>
 * 
 * <p>
 * <code>Selection newSel = new Selection(SelectionModel.ACTIVE);
 * editorSession. getSelectionModel().add(SelectionModel.ACTIVE);
 * editorSession.
 * getSelectionModel().setActiveSelection(SelectionModel.ACTIVE); </code>
 * </p>
 * 
 * <p>
 * Instead, remove all entries within the selection by calling
 * <code>clear()</code> on the active selection (<code>editorSession.
 * getSelectionModel().getActiveSelection()</code>) and add the new selection
 * elements to it. After all changes have been made and the system should be
 * updated, call <code>editorSession.getSelectionmodel().
 * selectionChanged()</code>
 * </p>
 * 
 * @version $Revision: 6093 $
 */
public class Selection {

    /**
     * The list of selected edges.
     * 
     * @see org.graffiti.graph.Edge
     */
    private List<Edge> edges;

    /**
     * The list of selected nodes.
     * 
     * @see org.graffiti.graph.Node
     */
    private List<Node> nodes;

    /**
     * Map of graph elements that changed state from unmarked to marked. This
     * map is cleared after a selectionChanged event has been fired.
     */
    private Map<GraphElement, Object> newMarked;

    /**
     * Map of graph elements that changed state from marked to unmarked. This
     * map is cleared after a selectionChanged event has been fired.
     */
    private Map<GraphElement, Object> newUnmarked;

    /** The name of this selection. */
    private String name;

    /**
     * Constructs a new <code>Selection</code> instance with the given name.
     */
    public Selection() {
        this.nodes = new LinkedList<Node>();
        this.edges = new LinkedList<Edge>();
        this.newMarked = new HashMap<GraphElement, Object>();
        this.newUnmarked = new HashMap<GraphElement, Object>();
    }

    /**
     * Constructs a new <code>Selection</code> instance with the given name.
     * 
     * @param name
     *            the name of this selection.
     */
    public Selection(String name) {
        this.name = name;
        this.nodes = new LinkedList<Node>();
        this.edges = new LinkedList<Edge>();
        this.newMarked = new HashMap<GraphElement, Object>();
        this.newUnmarked = new HashMap<GraphElement, Object>();
    }

    /**
     * Returns the list of selected edges.
     * 
     * @see org.graffiti.graph.Edge
     */
    public List<Edge> getEdges() {
        return this.edges;
    }

    /**
     * Returns a list containing all edges and nodes in this selection.
     * 
     * @return a list containing all edges and nodes in this selection.
     */
    public List<GraphElement> getElements() {
        List<GraphElement> all = new LinkedList<GraphElement>();
        all.addAll(this.edges);
        all.addAll(this.nodes);

        return all;
    }

    /**
     * Returns <code>true</code> if no nodes or edges are selected.
     * 
     * @return <code>true</code> if no nodes or edges are selected.
     */
    public boolean isEmpty() {
        return (this.nodes.isEmpty() && this.edges.isEmpty());
    }

    /**
     * Sets the name.
     * 
     * @param name
     *            The name to set
     * 
     * @throws FieldAlreadySetException
     *             DOCUMENT ME!
     */
    public void setName(String name) throws FieldAlreadySetException {
        if (this.name == null) {
            this.name = name;
        } else
            throw new FieldAlreadySetException(
                    "Name of a selection may not be changed. Create new one.");
    }

    /**
     * Returns the name of this selection.
     * 
     * @return the name of this selection.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Sets the list of graph elements that have been marked but the selection
     * listeners have not yet been notified.
     * 
     * @param newMarked
     *            list of newly marked graph elements
     */
    public void setNewMarked(Map<GraphElement, Object> newMarked) {
        this.newMarked = newMarked;
    }

    /**
     * Returns the map holding graph elements that have been marked since the
     * last selectionChanged event.
     * 
     * @return map holding graph elements that have been marked since the last
     *         selectionChanged event.
     */
    public Map<GraphElement, Object> getNewMarked() {
        return newMarked;
    }

    /**
     * Sets the list of graph elements that have been unmarked but the selection
     * listeners have not yet been notified.
     * 
     * @param newUnmarked
     *            list of newly unmarked graph elements
     */
    public void setNewUnmarked(Map<GraphElement, Object> newUnmarked) {
        this.newUnmarked = newUnmarked;
    }

    /**
     * Returns the map holding graph elements that have been unmarked since the
     * last selectionChanged event.
     * 
     * @return the map holding graph elements that have been unmarked since the
     *         last selectionChanged event.
     */
    public Map<GraphElement, Object> getNewUnmarked() {
        return newUnmarked;
    }

    /**
     * Returns the list of selected nodes.
     * 
     * @see org.graffiti.graph.Node
     */
    public List<Node> getNodes() {
        return this.nodes;
    }

    /**
     * Adds the given node or edge to the selection.
     * 
     * @param ge
     *            the node or edge to add to the selection.
     */
    public void add(GraphElement ge) {
        if (ge instanceof Node) {
            this.add((Node) ge);
        } else {
            this.add((Edge) ge);
        }
    }

    /**
     * Adds the given node to the list of selected nodes.
     * 
     * @param node
     *            the node to add to the list of selected nodes.
     */
    public void add(Node node) {
        if (nodes.contains(node))
            return;
        
        this.nodes.add(node);

        newUnmarked.remove(node);
        this.newMarked.put(node, null);
    }

    /**
     * Adds the given edge to the list of selected edges.
     * 
     * @param edge
     *            the edge to add to the list of selected edges.
     */
    public void add(Edge edge) {
        if (edges.contains(edge))
            return;
        
        this.edges.add(edge);

        newUnmarked.remove(edge);
        this.newMarked.put(edge, null);
    }

    /**
     * Adds all (graph)elements of the given collection to this selection.
     * 
     * @param newElements
     */
    public void addAll(Collection<? extends GraphElement> newElements) {
        for (GraphElement element : newElements) {
            add(element);
        }
    }

    /**
     * Adds all elements from the given selection to this selection.
     * 
     * @param sel
     */
    public void addSelection(Selection sel) {
        for (GraphElement e : sel.getElements()) {
            add(e);
        }
    }

    /**
     * Remove all elements from this selection.
     */
    public void clear() {
        for (Node node : nodes) {
            newUnmarked.put(node, null);
        }

        for (Edge edge : edges) {
            newUnmarked.put(edge, null);
        }

        nodes = new LinkedList<Node>();
        edges = new LinkedList<Edge>();
        newMarked = new HashMap<GraphElement, Object>();
    }

    /**
     * @see java.lang.Object#clone()
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        Selection newSel = new Selection();

        for (GraphElement ge : getElements()) {
            newSel.add(ge);
        }

        return newSel;
    }

    /**
     * Removes the given node or edge from the selection.
     * 
     * @param ge
     *            the node or edge to remove from the selection.
     */
    public void remove(GraphElement ge) {
        if (ge instanceof Node) {
            this.nodes.remove(ge);
        } else {
            this.edges.remove(ge);
        }

        newMarked.remove(ge);
        this.newUnmarked.put(ge, null);
    }

    public boolean contains(GraphElement graphElement) {
        return (nodes.contains(graphElement) || edges.contains(graphElement));
    }

    /**
     * Gets a string describing the selection. Default: number of selected nodes
     * and edges in a sentence.
     * 
     * @return a string describing the selection. Default: number of selected
     *         nodes and edges in a sentence.
     */
    @Override
    public String toString() {
        return nodes.size() + " nodes and " + edges.size() + " edges selected";
    }

    /**
     * Clears the maps holding any changes since the last selectionChanged
     * event. Should be called whenever a selectionChanged event has been
     * generated with this selection.
     */
    protected void committedChanges() {
        this.newMarked = new HashMap<GraphElement, Object>();
        this.newUnmarked = new HashMap<GraphElement, Object>();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
