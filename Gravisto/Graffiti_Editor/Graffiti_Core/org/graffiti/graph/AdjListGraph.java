// =============================================================================
//
//   AdjListGraph.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AdjListGraph.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.graph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.event.ListenerManager;
import org.graffiti.util.logging.GlobalLoggerSetting;

//import org.graffiti.util.MultipleIterator;

/**
 * Implements the <code>Graph</code>-interface using an adjacency list
 * representation of the graph. Requires <code>AdjListNode</code> and
 * <code>AdjListEdge</code> as implementations for nodes and edges. Every method
 * modifying the graph will inform the <code>ListenerManager</code> about the
 * modification according to the description in <code>Graph</code>.
 * 
 * @version $Revision: 5779 $
 * 
 * @see Graph
 * @see AbstractGraph
 * @see AdjListNode
 * @see AdjListEdge
 * @see AbstractNode
 * @see AbstractEdge
 */
public class AdjListGraph extends AbstractGraph implements Graph {

    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(AdjListGraph.class
            .getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /** The list containing the nodes of the graph. */
    private List<Node> nodes;

    /**
     * set to True if graph has been modified.
     */
    private boolean modified;

    /**
     * Constructs a new instance of an <code>AdjListGraph</code>. Sets the
     * <code>ListenerManager</code> of the new instance to the default
     * <code>ListenerManager</code>.
     */
    public AdjListGraph() {
        super();
        this.nodes = new ArrayList<Node>();
    }

    /**
     * Constructs a new instance of an <code>AdjListGraph</code>. Sets the
     * <code>ListenerManager</code> of the new instance to the default
     * <code>ListenerManager</code>.
     * 
     * @param coll
     *            the <code>CollectionAttribute</code> of the currently created
     *            <code>AdjListGraph</code> instance.
     */
    public AdjListGraph(CollectionAttribute coll) {
        super(coll);
        this.nodes = new ArrayList<Node>();
    }

    /**
     * Constructs a new instance of an <code>AdjListGraph</code>. Sets the
     * <code>ListenerManager</code> of the new instance to the specified one.
     * 
     * @param listenerManager
     *            listener manager for the graph.
     */
    public AdjListGraph(ListenerManager listenerManager) {
        super(listenerManager);
        this.nodes = new ArrayList<Node>();
    }

    /**
     * Constructs a new instance of an <code>AdjListGraph</code>. Sets the
     * <code>ListenerManager</code> of the new instance to the specified one.
     * 
     * @param listenerManager
     *            listener manager for the graph.
     * @param coll
     *            the <code>CollectionAttribute</code> of the currently created
     *            <code>AdjListGraph</code> instance.
     */
    public AdjListGraph(ListenerManager listenerManager,
            CollectionAttribute coll) {
        super(listenerManager, coll);
        this.nodes = new ArrayList<Node>();
    }

    /**
     * Constructs a new instance of an <code>AdjListGraph</code> from an
     * instance of any <code>Graph</code> implementation. Copies all nodes and
     * edges from g into the new graph.
     * 
     * @param g
     *            any <code>Graph</code> implementation out of which an
     *            <code>AdjListGraph</code> shall be generated.
     * @param listenerManager
     *            listener manager for the graph.
     */
    public AdjListGraph(Graph g, ListenerManager listenerManager) {
        super(listenerManager);
        this.nodes = new ArrayList<Node>();
        this.addGraph(g);
    }

    /**
     * Constructs a new instance of an <code>AdjListGraph</code> from an
     * instance of any <code>Graph</code> implementation. Copies all nodes and
     * edges from g into the new graph.
     * 
     * @param g
     *            any <code>Graph</code> implementation out of which an
     *            <code>AdjListGraph</code> shall be generated.
     * @param listenerManager
     *            listener manager for the graph.
     * @param coll
     *            the <code>CollectionAttribute</code> of the currently created
     *            <code>AdjListGraph</code> instance.
     */
    public AdjListGraph(Graph g, ListenerManager listenerManager,
            CollectionAttribute coll) {
        super(listenerManager, coll);
        this.nodes = new ArrayList<Node>();
        this.addGraph(g);
    }

    /**
     * The given node is moved to the front of the node list.
     * 
     * @param node
     */
    public void setNodeFirst(Node node) {
        if (nodes.remove(node)) {
            nodes.add(0, node);
        }
    }

    /**
     * The given node is moved to the end of the node list.
     * 
     * @param node
     */
    public void setNodeLast(Node node) {
        if (nodes.remove(node)) {
            nodes.add(node);
        }
    }

    /**
     * Returns an iterator over the nodes of the graph. Note that the remove
     * operation is not supported by this iterator.
     * 
     * @return an iterator containing the nodes of the graph.
     */
    public Iterator<Node> getNodesIterator() {
        // return new MultipleIterator(nodes.iterator());
        return nodes.iterator();
    }

    /**
     * Creates and returns a copy of the graph. The attributes are copied as
     * well as all nodes and edges.
     * 
     * @return a copy of the graph.
     */
    public Object copy() {
        AdjListGraph newGraph = new AdjListGraph((CollectionAttribute) this
                .getAttributes().copy());
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

        AdjListEdge edge = (AdjListEdge) createEdge(source, target, directed);
        ((AdjListNode) source).addOutEdge(edge);
        logger
                .info("ADDING NEW EDGE TO SOURCE :outgoing edge was added to the source node");
        ((AdjListNode) target).addInEdge(edge);
        logger
                .info("ADDING NEW EDGE TO TARGET:ingoing edge was added to the target node");

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
     *            CollectionAttribute that will be added to the new Edge
     * 
     * @return the new edge.
     */
    @Override
    protected Edge doAddEdge(Node source, Node target, boolean directed,
            CollectionAttribute col) {
        assert (source != null) && (target != null) && (col != null);

        AdjListEdge edge = (AdjListEdge) createEdge(source, target, directed,
                col);
        ((AdjListNode) source).addOutEdge(edge);
        logger
                .info("ADDING NEW EDGE TO SOURCE :outgoing edge was added to the source node");
        ((AdjListNode) target).addInEdge(edge);
        logger
                .info("ADDING NEW EDGE TO TARGET:ingoing edge was added to the target node");

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
        setModified(true);
        nodes.add(node);
    }

    /**
     * Deletes the current graph by resetting all its attributes. The graph is
     * then equal to a new generated graph i.e. the list of nodes and edges will
     * be empty. A special event for clearing the graph will be passed to the
     * listener manager.
     */
    @Override
    protected void doClear() {
        setModified(true);
        this.nodes = new ArrayList<Node>();
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
        ((AdjListNode) (e.getSource())).removeOutEdge((AdjListEdge) e);
        ((AdjListNode) (e.getTarget())).removeInEdge((AdjListEdge) e);
        setModified(true);
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

        List<Edge> l = new LinkedList<Edge>();

        for (Iterator<Edge> edgeIt = n.getEdgesIterator(); edgeIt.hasNext();) {
            l.add(edgeIt.next());
        }

        for (Edge edge : l) {
            this.deleteEdge(edge);
        }

        nodes.remove(idx);

        // assert n.instanceOf(AdjListNode);
        ((AdjListNode) n).setGraphToNull();
        setModified(true);
    }

    /**
     * Creates a new <code>AdjListNode</code> that is in the current graph.
     * 
     * @return the newly created node.
     */
    @Override
    protected Node createNode() {
        setModified(true);
        return new AdjListNode(this);
    }

    /**
     * Creates a new <code>AdjListNode</code> that is in the current graph. And
     * initializes it with the given <code>CollectionAttribute</code>.
     * 
     * @param col
     *            DOCUMENT ME!
     * 
     * @return the newly created node.
     */
    @Override
    public Node createNode(CollectionAttribute col) {
        assert col != null;

        setModified(true);
        return new AdjListNode(this, col);
    }

    /**
     * Creates a new <code>AdjListEdge</code> that is in the current graph.
     * 
     * @param source
     *            the source of the edge to add.
     * @param target
     *            the target of the edge to add.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * 
     * 
     * @return the newly created edge.
     */
    protected Edge createEdge(Node source, Node target, boolean directed) {
        setModified(true);
        return new AdjListEdge(this, source, target, directed);
    }

    /**
     * Creates a new <code>AdjListEdge</code> that is in the current graph. And
     * initializes it with the given <code>CollectionAttribute</code>.
     * 
     * @param source
     *            the source of the edge to add.
     * @param target
     *            the target of the edge to add.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * @param col
     *            CollectionAttribute that will be added to the new Edge
     * 
     * @return the new edge.
     */
    public Edge createEdge(Node source, Node target, boolean directed,
            CollectionAttribute col) {
        assert col != null;

        setModified(true);
        return new AdjListEdge(this, source, target, directed, col);
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
