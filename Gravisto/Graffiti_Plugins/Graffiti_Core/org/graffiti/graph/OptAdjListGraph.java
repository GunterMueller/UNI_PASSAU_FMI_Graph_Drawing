// =============================================================================
//
//   OptAdjListGraph.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: OptAdjListGraph.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.event.ListenerManager;
import org.graffiti.util.MultipleIterator;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Implements the <code>Graph</code>-interface using an adjacency list
 * representation of the graph. Requires <code>AdjListNode</code> and
 * <code>AdjListEdge</code> as implementations for nodes and edges. Every method
 * modifying the graph will inform the <code>ListenerManager</code> about the
 * modification according to the description in <code>Graph</code>.
 * 
 * @version $Revision: 5779 $
 * 
 * @see org.graffiti.graph.Graph
 * @see org.graffiti.graph.AbstractGraph
 * @see org.graffiti.graph.AdjListNode
 * @see org.graffiti.graph.AdjListEdge
 * @see org.graffiti.graph.AbstractNode
 * @see org.graffiti.graph.AbstractEdge
 */
public class OptAdjListGraph extends AdjListGraph implements Graph {

    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(OptAdjListGraph.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The list containing the edges of the graph. */
    private List<Edge> edges;

    /** The list containing the nodes of the graph. */
    private List<Node> nodes;

    /** The number of directed edges. */
    private int noDirectedEdges;

    /** The number of undirected edges. */
    private int noUndirectedEdges;

    /**
     * Constructs a new instance of an <code>OptAdjListGraph</code>. Sets the
     * <code>ListenerManager</code> of the new instance to the default
     * <code>ListenerManager</code>.
     */
    public OptAdjListGraph() {
        super();
        this.nodes = new ArrayList<Node>();
        this.edges = new ArrayList<Edge>();
        noUndirectedEdges = 0;
        noDirectedEdges = 0;
    }

    /**
     * Constructs a new instance of an <code>OptAdjListGraph</code>. Sets the
     * <code>ListenerManager</code> of the new instance to the default
     * <code>ListenerManager</code>.
     * 
     * @param coll
     *            the <code>CollectionAttribute</code> of the currently created
     *            <code>OptAdjListGraph</code> instance.
     */
    public OptAdjListGraph(CollectionAttribute coll) {
        super(coll);
        this.nodes = new ArrayList<Node>();
        this.edges = new ArrayList<Edge>();
        noUndirectedEdges = 0;
        noDirectedEdges = 0;
    }

    /**
     * Constructs a new instance of an <code>OptAdjListGraph</code>. Sets the
     * <code>ListenerManager</code> of the new instance to the specified one.
     * 
     * @param listenerManager
     *            listener manager for the graph.
     */
    public OptAdjListGraph(ListenerManager listenerManager) {
        super(listenerManager);
        this.nodes = new ArrayList<Node>();
        this.edges = new ArrayList<Edge>();
        noUndirectedEdges = 0;
        noDirectedEdges = 0;
    }

    /**
     * Constructs a new instance of an <code>OptAdjListGraph</code>. Sets the
     * <code>ListenerManager</code> of the new instance to the specified one.
     * 
     * @param listenerManager
     *            listener manager for the graph.
     * @param coll
     *            the <code>CollectionAttribute</code> of the currently created
     *            <code>OptAdjListGraph</code> instance.
     */
    public OptAdjListGraph(ListenerManager listenerManager,
            CollectionAttribute coll) {
        super(listenerManager, coll);
        this.nodes = new ArrayList<Node>();
        this.edges = new ArrayList<Edge>();
        noUndirectedEdges = 0;
        noDirectedEdges = 0;
    }

    /**
     * Constructs a new instance of an <code>OptAdjListGraph</code> from an
     * instance of any <code>Graph</code> implementation. Copies all nodes and
     * edges from g into the new graph.
     * 
     * @param g
     *            any <code>Graph</code> implementation out of which an
     *            <code>OptAdjListGraph</code> shall be generated.
     * @param listenerManager
     *            listener manager for the graph.
     */
    public OptAdjListGraph(Graph g, ListenerManager listenerManager) {
        super(listenerManager);
        this.nodes = new ArrayList<Node>();
        this.edges = new ArrayList<Edge>();
        this.addGraph(g);
        noUndirectedEdges = 0;
        noDirectedEdges = 0;
    }

    /**
     * Constructs a new instance of an <code>OptAdjListGraph</code> from an
     * instance of any <code>Graph</code> implementation. Copies all nodes and
     * edges from g into the new graph.
     * 
     * @param g
     *            any <code>Graph</code> implementation out of which an
     *            <code>OptAdjListGraph</code> shall be generated.
     * @param listenerManager
     *            listener manager for the graph.
     * @param coll
     *            the <code>CollectionAttribute</code> of the currently created
     *            <code>OptAdjListGraph</code> instance.
     */
    public OptAdjListGraph(Graph g, ListenerManager listenerManager,
            CollectionAttribute coll) {
        super(listenerManager, coll);
        this.nodes = new ArrayList<Node>();
        this.edges = new ArrayList<Edge>();
        noUndirectedEdges = 0;
        noDirectedEdges = 0;
        this.addGraph(g);
    }

    /**
     * Indicates whether the graph is directed. A graph is directed if all the
     * edges are directed.
     * 
     * @return a boolean indicating whether the graph is directed.
     */
    @Override
    public boolean isDirected() {
        return noDirectedEdges == edges.size();
    }

    /**
     * Sets all edges to be <code>directed</code>.
     * <p>
     * If <code>directed</code> is <code>true</code>, standard arrows are set,
     * if it is <code>false</code>, all arrows of all edges are removed.
     * 
     * @see org.graffiti.graph.Graph#setDirected(boolean)
     */
    @Override
    public void setDirected(boolean directed) {
        for (Iterator<Edge> it = getEdgesIterator(); it.hasNext();) {
            Edge edge = it.next();
            if (directed != edge.isDirected()) {
                edge.setDirected(directed);
                if (directed) {
                    noUndirectedEdges--;
                    noDirectedEdges++;
                } else {
                    noUndirectedEdges++;
                    noDirectedEdges--;
                }
            }
        }
    }

    /**
     * When passing a true value, all undirected edges in the graph will be set
     * to be directed. V.v. for a false value. A true second parameter indicates
     * that all edges shall get one arrow at their tips (i.e. close to the
     * target node).
     * 
     * @param directed
     * @param adjustArrows
     */
    @Override
    public void setDirected(boolean directed, boolean adjustArrows) {
        if (adjustArrows) {
            for (Iterator<Edge> it = getEdgesIterator(); it.hasNext();) {
                Edge edge = it.next();
                if (directed != edge.isDirected()) {
                    edge.setDirected(directed);
                    if (directed) {
                        noUndirectedEdges--;
                        noDirectedEdges++;
                    } else {
                        noUndirectedEdges++;
                        noDirectedEdges--;
                    }
                }
                // TODO think if we really want those attribute to be ADDED
                if (directed) {
                    edge.setString("graphics.arrowtail", "");
                    edge
                            .setString("graphics.arrowhead",
                                    "org.graffiti.plugins.views.defaults.StandardArrowShape");
                } else {
                    edge.setString("graphics.arrowtail", "");
                    edge.setString("graphics.arrowhead", "");
                }
            }
        }
    }

    /**
     * Returns a <code>java.util.Collection</code> containing all the edges of
     * the current graph.
     * 
     * @return a <code>java.util.Collection</code> containing all the edges of
     *         the current graph.
     */
    @Override
    public Collection<Edge> getEdges() {
        return new ArrayList<Edge>(edges);
    }

    /**
     * Returns an iterator over the edges of the graph. Note that the remove
     * operation is not supported by this iterator.
     * 
     * @return an iterator containing the edges of the graph.
     */
    @Override
    public Iterator<Edge> getEdgesIterator() {
        return new MultipleIterator<Edge>(edges.iterator());
    }

    /**
     * Returns <code>true</code> if the graph is empty. The graph is equal to a
     * graph which has been cleared.
     * 
     * @return <code>true</code> if the graph is empty, <code>false</code>
     *         otherwise.
     */
    @Override
    public boolean isEmpty() {
        return nodes.size() == 0;
    }

    /**
     * Returns a list containing a copy of the node list of the graph. Removing
     * elements from this collection will have no effect on the graph whereas
     * nodes can be modified.
     * 
     * @return a new <code>java.util.List</code> containing all the nodes of the
     *         graph.
     */
    @Override
    public List<Node> getNodes() {
        return new ArrayList<Node>(nodes);
    }

    /**
     * Returns an iterator over the nodes of the graph. Note that the remove
     * operation is not supported by this iterator.
     * 
     * @return an iterator containing the nodes of the graph.
     */
    @Override
    public Iterator<Node> getNodesIterator() {
        return new MultipleIterator<Node>(nodes.iterator());
    }

    /**
     * Returns the number of directed edges of the graph.
     * 
     * @return the number of directed edges of the graph.
     */
    @Override
    public int getNumberOfDirectedEdges() {
        return noDirectedEdges;
    }

    /**
     * Returns the number of edges of the graph.
     * 
     * @return the number of edges of the graph.
     */
    @Override
    public int getNumberOfEdges() {
        return edges.size();
    }

    /**
     * Returns the number of nodes in the graph.
     * 
     * @return the number of nodes of the graph.
     */
    @Override
    public int getNumberOfNodes() {
        return nodes.size();
    }

    /**
     * Returns the number of undirected edges in the graph.
     * 
     * @return the number of undirected edges in the graph.
     */
    @Override
    public int getNumberOfUndirectedEdges() {
        return noUndirectedEdges;
    }

    /**
     * Indicates whether the graph is undirected. A graph is undirected if all
     * the edges are undirected.
     * 
     * @return A boolean indicating whether the graph is undirected.
     */
    @Override
    public boolean isUndirected() {
        return edges.size() == noUndirectedEdges;
    }

    /**
     * Returns <code>true</code>, if the graph contains the specified edge,
     * <code>false</code> otherwise.
     * 
     * @param e
     *            the edge to search for.
     * 
     * @return <code>true</code>, if the graph contains the edge e,
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean containsEdge(Edge e) {
        assert e != null;

        return edges.contains(e);
    }

    /**
     * Returns <code>true</code>, if the graph contains the specified node,
     * <code>false</code> otherwise.
     * 
     * @param n
     *            the node to search for.
     * 
     * @return <code>true</code>, if the graph contains the node n,
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean containsNode(Node n) {
        assert n != null;

        return nodes.contains(n);
    }

    /**
     * Adds a copy of the specified edge to the graph as a new edge between the
     * specified source and target node. Informs the ListenerManager about the
     * newly added edge through the call to <code>addEdge()</code>. Also informs
     * the ListenerManager about the copy of the attributes added to the edge by
     * adding them separatly throug
     * <code>CollectionAttribute.add(Attribute)</code>.
     * 
     * @return DOCUMENT ME!
     */

    // public Edge addEdgeCopy(Edge edge, Node source, Node target) {
    //	
    // assert edge != null && source != null && target != null;
    // CollectionAttribute col =
    // (CollectionAttribute) edge.getAttributes().copy();
    // Edge newEdge = this.addEdge(source, target, edge.isDirected(), col);
    // assert noDirectedEdges + noUndirectedEdges == edges.size();
    // return newEdge;
    // }
    /**
     * Creates and returns a copy of the graph. The attributes are copied as
     * well as all nodes and edges.
     * 
     * @return a copy of the graph.
     */
    @Override
    public Object copy() {
        OptAdjListGraph newGraph = new OptAdjListGraph(
                (CollectionAttribute) this.getAttributes().copy());
        newGraph.addGraph(this);

        return newGraph;
    }

    /**
     * Adds a new edge to the current graph. Informs the ListenerManager about
     * the new node.
     * 
     * @param source
     *            the source of the edge to add.
     * @param target
     *            the target of the edge to add.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * 
     * @return the new edge.
     */
    @Override
    protected Edge doAddEdge(Node source, Node target, boolean directed) {
        assert (source != null) && (target != null);
        assert (noDirectedEdges + noUndirectedEdges) == edges.size();

        AdjListEdge edge = new AdjListEdge(this, source, target, directed);
        ((AdjListNode) source).addOutEdge(edge);
        ((AdjListNode) target).addInEdge(edge);
        edges.add(edge);

        if (edge.isDirected()) {
            ++noDirectedEdges;
        } else {
            ++noUndirectedEdges;
        }

        assert (noDirectedEdges + noUndirectedEdges) == edges.size();

        return edge;
    }

    /**
     * Adds a new edge to the current graph. Informs the ListenerManager about
     * the new node.
     * 
     * @param source
     *            the source of the edge to add.
     * @param target
     *            the target of the edge to add.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * @param col
     *            DOCUMENT ME!
     * 
     * @return the new edge.
     */
    @Override
    protected Edge doAddEdge(Node source, Node target, boolean directed,
            CollectionAttribute col) {
        assert (source != null) && (target != null) && (col != null);
        assert (noDirectedEdges + noUndirectedEdges) == edges.size();

        AdjListEdge edge = new AdjListEdge(this, source, target, directed, col);
        ((AdjListNode) source).addOutEdge(edge);
        ((AdjListNode) target).addInEdge(edge);
        edges.add(edge);

        if (edge.isDirected()) {
            ++noDirectedEdges;
        } else {
            ++noUndirectedEdges;
        }

        assert (noDirectedEdges + noUndirectedEdges) == edges.size();

        return edge;
    }

    /**
     * Adds a new node to the graph. Informs the ListenerManager about the new
     * node.
     * 
     * @param node
     *            DOCUMENT ME!
     */
    @Override
    protected void doAddNode(Node node) {
        assert node != null;
        nodes.add(node);
    }

    /**
     * void setDirectedEdge(boolean b) { if (b == Edge.DIRECTED) {
     * ++noDirectedEdges; --noUndirectedEdges; } else { --noDirectedEdges;
     * ++noUndirectedEdges; } assert noDirectedEdges + noUndirectedEdges ==
     * edges.size(); }
     */
    /**
     * Deletes the current graph by resetting all its attributes. The graph is
     * then equal to a new generated graph i.e. the list of nodes and edges will
     * be empty. A special event for clearing the graph will be passed to the
     * listener manager.
     */
    @Override
    protected void doClear() {
        assert (noDirectedEdges + noUndirectedEdges) == edges.size();
        this.nodes = new ArrayList<Node>();
        this.edges = new ArrayList<Edge>();
        noUndirectedEdges = 0;
        noDirectedEdges = 0;
        assert (noDirectedEdges + noUndirectedEdges) == edges.size();
    }

    /**
     * Deletes the given edge from the current graph. Implicitly calls the
     * ListenerManager by calling <code>AdjListNode.removeEdge()</code> in the
     * source and target node of the edge.
     * 
     * @param e
     *            the edge to delete.
     */
    @Override
    protected void doDeleteEdge(Edge e) {
        assert e != null;
        assert (noDirectedEdges + noUndirectedEdges) == edges.size();

        ((AdjListNode) (e.getSource())).removeOutEdge((AdjListEdge) e);
        ((AdjListNode) (e.getTarget())).removeInEdge((AdjListEdge) e);
        edges.remove(e);

        if (e.isDirected()) {
            --noDirectedEdges;
        } else {
            --noUndirectedEdges;
        }

        assert (noDirectedEdges + noUndirectedEdges) == edges.size();
    }

    /**
     * Deletes the given node. First all in- and out-going edges will be deleted
     * using <code>deleteEdge()</code> and thereby informs the ListenerManager
     * implicitly.
     * 
     * @param n
     *            the node to delete.
     * 
     * @throws GraphElementNotFoundException
     *             DOCUMENT ME!
     */
    @Override
    protected void doDeleteNode(Node n) throws GraphElementNotFoundException {
        assert n != null;

        int idx = nodes.indexOf(n);
        logger.fine("removing all edges adjacent to this node");

        for (Iterator<Edge> it = n.getEdgesIterator(); it.hasNext();) {
            this.deleteEdge(it.next());
        }

        nodes.remove(idx);
        ((AdjListNode) n).setGraphToNull();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
