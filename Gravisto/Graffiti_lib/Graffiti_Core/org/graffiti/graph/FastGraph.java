// =============================================================================
//
//   FastGraph.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: FastGraph.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.util.MultiLinkedList;

/**
 * An adjacency list implementation of the {@link Graph} interface tuned for
 * speed (not for size!).
 * 
 * {@link #addNode()}, {@link #addEdge(Node, Node, boolean)}, and
 * {@link #deleteEdge(Edge)} run in constant time. {@link #deleteNode(Node)}
 * needs linear time in the node degree. Any iteration step in any of the
 * supported iterations has constant complexity.
 * 
 * @see FastNode
 * @see FastEdge
 * 
 * @author forster
 * @version $Revision: 5767 $ $Date: 2006-01-26 10:09:40 +0100 (Do, 26 Jan 2006)
 *          $
 */
public class FastGraph extends AbstractGraph {
    /** The nodes of the graph. */
    private List<FastNode> nodes;

    /** The edges of the graph. */
    private List<FastEdge> edges;

    /** The number of directed edges contained in the graph. */
    int numberOfDirectedEdges;

    /** Flag that is set to true each time the graph is modified */
    private boolean modified;

    /**
     * Creates a new graph.
     */
    public FastGraph() {
        this(null);
    }

    /**
     * Creates a new graph with a given set of attributes.
     * 
     * @param attributes
     *            The initial attributes of the graph
     */
    public FastGraph(CollectionAttribute attributes) {
        super(attributes);
        doClear();
        modified = false;
    }

    /*
     * @see org.graffiti.core.DeepCopy#copy()
     */
    public Object copy() {
        CollectionAttribute attr = (CollectionAttribute) getAttributes().copy();

        FastGraph g = new FastGraph(attr);
        g.addGraph(this);

        return g;
    }

    /*
     * @see org.graffiti.graph.Graph#createEdge(org.graffiti.graph.Node,
     * org.graffiti.graph.Node, boolean,
     * org.graffiti.attributes.CollectionAttribute)
     */
    public Edge createEdge(Node source, Node target, boolean directed,
            CollectionAttribute attribute) {
        return new FastEdge(this, (FastNode) source, (FastNode) target,
                directed, attribute);
    }

    /*
     * @see org.graffiti.graph.AbstractGraph#createNode()
     */
    @Override
    Node createNode() {
        return new FastNode(this);
    }

    /*
     * @seeorg.graffiti.graph.AbstractGraph#createNode(org.graffiti.attributes.
     * CollectionAttribute)
     */
    @Override
    public Node createNode(CollectionAttribute attribute) {
        return new FastNode(this, attribute);
    }

    /*
     * @see org.graffiti.graph.AbstractGraph#doAddEdge(org.graffiti.graph.Node,
     * org.graffiti.graph.Node, boolean,
     * org.graffiti.attributes.CollectionAttribute)
     */
    @Override
    protected Edge doAddEdge(Node source, Node target, boolean directed,
            CollectionAttribute col) throws GraphElementNotFoundException {
        FastEdge edge = (FastEdge) createEdge(source, target, directed, col);

        edge.link();
        edges.add(edge);

        modified = true;
        return edge;
    }

    /*
     * @see org.graffiti.graph.AbstractGraph#doAddNode(org.graffiti.graph.Node)
     */
    @Override
    protected void doAddNode(Node node) {
        nodes.add((FastNode) node);
        modified = true;
    }

    /*
     * @see org.graffiti.graph.AbstractGraph#doClear()
     */
    @Override
    protected void doClear() {
        nodes = new MultiLinkedList<FastNode, Void>(null);
        edges = new MultiLinkedList<FastEdge, FastEdge.End>(null);
        numberOfDirectedEdges = 0;
        modified = true;
    }

    /*
     * @see
     * org.graffiti.graph.AbstractGraph#doDeleteEdge(org.graffiti.graph.Edge)
     */
    @Override
    protected void doDeleteEdge(Edge e) throws GraphElementNotFoundException {
        ((FastEdge) e).unlink();
        edges.remove(e);
        modified = true;
    }

    /*
     * @see
     * org.graffiti.graph.AbstractGraph#doDeleteNode(org.graffiti.graph.Node)
     */
    @Override
    protected void doDeleteNode(Node n) throws GraphElementNotFoundException {
        Collection<Edge> edges = new ArrayList<Edge>(n.getEdges());
        for (Edge edge : edges) {
            deleteEdge(edge);
        }

        nodes.remove(n);
        modified = true;
    }

    /*
     * @see org.graffiti.graph.AbstractGraph#getEdges()
     */
    @Override
    public Collection<Edge> getEdges() {
        return Collections.<Edge> unmodifiableCollection(edges);
    }

    /*
     * @see org.graffiti.graph.AbstractGraph#getNodes()
     */
    @Override
    public List<Node> getNodes() {
        return Collections.<Node> unmodifiableList(nodes);
    }

    /*
     * @see org.graffiti.graph.Graph#getNodesIterator()
     */
    public Iterator<Node> getNodesIterator() {
        return Collections.<Node> unmodifiableCollection(nodes).iterator();
    }

    /*
     * @see org.graffiti.graph.AbstractGraph#getNumberOfDirectedEdges()
     */
    @Override
    public int getNumberOfDirectedEdges() {
        return numberOfDirectedEdges;
    }

    /*
     * @see org.graffiti.graph.Graph#isModified()
     */
    public boolean isModified() {
        return modified;
    }

    /*
     * @see org.graffiti.graph.Graph#setModified(boolean)
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
