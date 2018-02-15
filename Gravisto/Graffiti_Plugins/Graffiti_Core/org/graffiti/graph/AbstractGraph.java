// =============================================================================
//
//   AbstractGraph.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractGraph.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.attributes.AbstractAttributable;
import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeConsumer;
import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeTypesManager;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.UnificationException;
import org.graffiti.event.GraphEvent;
import org.graffiti.event.ListenerManager;

/**
 * Provides further functionality for graphs.
 * 
 * @version $Revision: 5779 $
 * 
 * @see Graph
 * @see AdjListGraph
 */
public abstract class AbstractGraph extends AbstractAttributable implements
        Graph {
    /** The logger for the current class. */
    private static final Logger logger = Logger.getLogger(AbstractGraph.class
            .getName());
    
    static {
        logger.setLevel(Level.SEVERE);
    }

    /** The <code> AttributeTypesManager</code> for handling attribute types. */
    protected AttributeTypesManager attTypesManager;

    /**
     * The <code>ListenerManager</code> for handling events modifying the graph.
     */
    protected ListenerManager listenerManager;

    /**
     * The attribute, which will be (deep-)copied and added to every new
     * undirected edge. This attribute is extended by the
     * <code>addAttributeConsumer</code> method.
     */
    protected CollectionAttribute defaultUndirectedEdgeAttribute;

    /**
     * The attribute, which will be (deep-)copied and added to every new
     * directed edge. This attribute is extended by the
     * <code>addAttributeConsumer</code> method.
     */
    protected CollectionAttribute defaultDirectedEdgeAttribute;

    /**
     * The attribute, which will be (deep-)copied and added to every new node.
     * This attribute is extended by the <code>addAttributeConsumer</code>
     * method.
     */
    protected CollectionAttribute defaultNodeAttribute;

    /**
     * The attribute, which will be (deep-)copied and added to every new graph.
     * This attribute is extended by the <code>addAttributeConsumer</code>
     * method.
     */
    protected CollectionAttribute defaultGraphAttribute;

    /** Contains a set of attribute consumers. */
    private Set<AttributeConsumer> attributeConsumers;

    /** Number of current Node Adding Operation */
    private int CurrentNumberOfNode = 0;

    /** Number of current Edge Adding Operation */
    private int CurrentNumberOfEdge = 0;

    /**
     * Constructs a new instance of an <code>AbstractGraph</code>. Sets the
     * <code>ListenerManager</code> of the new instance to the default
     * <code>ListenerManager</code>.
     */
    public AbstractGraph() {
        this(null, null);
    }

    /**
     * Constructs a new instance of an <code>AbstractGraph</code>. Sets the
     * <code>ListenerManager</code> of the new instance to the default
     * <code>ListenerManager</code>.
     * 
     * @param coll
     *            the <code>CollectionAttribute</code> of the currently created
     *            <code>AbstractGraph</code> instance.
     */
    public AbstractGraph(CollectionAttribute coll) {
        this(null, coll);
    }

    /**
     * Constructs a new instance of an <code>AdjListGraph</code>. Sets the
     * <code>ListenerManager</code> of the new instance to the specified one.
     * 
     * @param listenerManager
     *            listener manager for the graph.
     */
    public AbstractGraph(ListenerManager listenerManager) {
        this(listenerManager, null);
    }

    /**
     * Constructs a new instance of an <code>AdjListGraph</code>. Sets the
     * <code>ListenerManager</code> of the new instance to the specified one.
     * 
     * @param listenerManager
     *            listener manager for the graph.
     * @param coll
     *            the <code>CollectionAttribute</code> of the currently created
     *            <code>AbstractGraph</code> instance.
     */
    public AbstractGraph(ListenerManager listenerManager,
            CollectionAttribute coll) {
        super(coll);

        if (listenerManager != null) {
            this.listenerManager = listenerManager;
        } else {
            this.listenerManager = new ListenerManager();
        }

        this.attributeConsumers = new HashSet<AttributeConsumer>();

        setBoolean("directed", true);
    }

    /**
     * Returns the <code>AttributeTypesManager</code> of the graph.
     * 
     * @return the <code>AttributeTypesManager</code> of the graph.
     */
    public AttributeTypesManager getAttTypesManager() {
        return this.attTypesManager;
    }

    /**
     * Indicates whether the graph is directed. A graph is directed if all the
     * edges are directed.
     * 
     * @return a boolean indicating whether the graph is directed.
     */
    public boolean isDirected() {
        return getEdges().size() == getNumberOfDirectedEdges();
    }

    /**
     * Sets all edges to be <code>directed</code>.
     * <p>
     * If <code>directed</code> is <code>true</code>, standard arrows are set,
     * if it is <code>false</code>, all arrows of all edges are removed.
     * 
     * @see org.graffiti.graph.Graph#setDirected(boolean)
     */
    public void setDirected(boolean directed) {
        for (Iterator<Edge> it = getEdgesIterator(); it.hasNext();) {
            Edge edge = it.next();
            if (directed != edge.isDirected()) {
                edge.setDirected(directed);
            }
        }
        // setBoolean("directed", directed);
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
    public void setDirected(boolean directed, boolean adjustArrows) {
        if (adjustArrows) {
            for (Iterator<Edge> it = getEdgesIterator(); it.hasNext();) {
                Edge edge = it.next();
                if (directed != edge.isDirected()) {
                    edge.setDirected(directed);
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

    /*
     * @see org.graffiti.graph.Graph#getEdges()
     */
    public Collection<Edge> getEdges() {
        Set<Edge> h = new LinkedHashSet<Edge>();

        for (Iterator<Node> nodeIt = getNodesIterator(); nodeIt.hasNext();) {
            for (Iterator<Edge> edgeIt = (nodeIt.next()).getEdgesIterator(); edgeIt
                    .hasNext();) {
                h.add(edgeIt.next());
            }
        }

        return h;
    }

    /**
     * Returns a collection containing all the edges between n1 and n2. There
     * can be more than one edge between two nodes. The edges returned by this
     * method can go from n1 to n2 or vice versa, be directed or not.
     * 
     * @param n1
     *            the first node.
     * @param n2
     *            the second node.
     * 
     * @return a <code>Collection</code> containing all edges between n1 and n2,
     *         an empty collection if there is no edge between the two nodes.
     * 
     * @exception GraphElementNotFoundException
     *                if one of the nodes is not contained in the graph.
     */
    public Collection<Edge> getEdges(Node n1, Node n2)
            throws GraphElementNotFoundException {
        assert (n1 != null) && (n2 != null);

        if ((this != n1.getGraph()) || (this != n2.getGraph()))
            throw new GraphElementNotFoundException(
                    "one of the nodes is not in the graph");

        Collection<Edge> col = new LinkedList<Edge>();

        for (Iterator<Edge> it = n1.getEdgesIterator(); it.hasNext();) {
            Edge e = it.next();

            if (n1 != n2) {
                if ((n2 == e.getSource()) || (n2 == e.getTarget())) {
                    col.add(e);
                }
            } else if ((n1 == e.getSource()) && (n1 == e.getTarget())) {
                col.add(e);
            }
        }

        return col;
    }

    /**
     * Returns an iterator over the edges of the graph.
     * 
     * @return an iterator over the edges of the graph.
     */
    public Iterator<Edge> getEdgesIterator() {
        return getEdges().iterator();
    }

    /**
     * Returns <code>true</code> if the graph is empty. The graph is equal to a
     * graph which has been cleared.
     * 
     * @return <code>true</code> if the graph is empty, <code>false</code>
     *         otherwise.
     */
    public boolean isEmpty() {
        return getNumberOfNodes() == 0;
    }

    /**
     * Returns all nodes and all edges contained in this graph.
     * 
     * @return Collection
     */
    public Collection<GraphElement> getGraphElements() {
        Collection<Node> nodes = getNodes();
        Collection<Edge> edges = getEdges();
        Collection<GraphElement> ges = new ArrayList<GraphElement>(nodes.size()
                + edges.size());
        ges.addAll(nodes);
        ges.addAll(edges);

        return ges;
    }

    /**
     * Returns the ListenerManager of the current graph.
     * 
     * @return the ListenerManager of the current graph.
     */
    public ListenerManager getListenerManager() {
        return this.listenerManager;
    }

    /**
     * Returns a list containing a copy of the node list of the graph. Removing
     * elements from this collection will have no effect on the graph whereas
     * nodes can be modified.
     * 
     * @return a new <code>java.util.List</code> containing all the nodes of the
     *         graph.
     */
    public List<Node> getNodes() {
        List<Node> l = new LinkedList<Node>();

        for (Iterator<Node> i = getNodesIterator(); i.hasNext();) {
            l.add(i.next());
        }

        return l;
    }

    /**
     * Returns the number of directed edges of the graph.
     * 
     * @return the number of directed edges of the graph.
     */
    public int getNumberOfDirectedEdges() {
        int numberOfDirectedEdges = 0;

        for (Iterator<Edge> edgeIt = getEdgesIterator(); edgeIt.hasNext();)
            if (edgeIt.next().isDirected()) {
                numberOfDirectedEdges++;
            }

        logger.fine("this graph contains " + numberOfDirectedEdges
                + " directed edge(s)");

        return numberOfDirectedEdges;
    }

    /**
     * Returns the number of edges of the graph.
     * 
     * @return the number of edges of the graph.
     */
    public int getNumberOfEdges() {
        return getEdges().size();
    }

    /**
     * Returns the number of nodes in the graph.
     * 
     * @return the number of nodes of the graph.
     */
    public int getNumberOfNodes() {
        return getNodes().size();
    }

    /**
     * Returns the number of undirected edges in the graph.
     * 
     * @return the number of undirected edges in the graph.
     */
    public int getNumberOfUndirectedEdges() {
        int numberOfUndirectedEdges = getEdges().size()
                - getNumberOfDirectedEdges();

        logger.fine("this graph contains " + numberOfUndirectedEdges
                + " undirected edge(s)");

        return numberOfUndirectedEdges;
    }

    /**
     * Indicates whether the graph is undirected. A graph is undirected if all
     * the edges are undirected.
     * 
     * @return A boolean indicating whether the graph is undirected.
     */
    public boolean isUndirected() {
        return getEdges().size() == getNumberOfUndirectedEdges();
    }

    /**
     * Adds the given attribute consumer to the list of attribute consumers.
     * 
     * @param attConsumer
     *            the attribute consumer to add.
     * 
     * @throws UnificationException
     *             in the context of unification failures.
     */
    public void addAttributeConsumer(AttributeConsumer attConsumer)
            throws UnificationException {
        defaultGraphAttribute = unifyWithDefaultAttribute(
                defaultGraphAttribute, attConsumer.getGraphAttribute());
        defaultNodeAttribute = unifyWithDefaultAttribute(defaultNodeAttribute,
                attConsumer.getNodeAttribute());
        defaultUndirectedEdgeAttribute = unifyWithDefaultAttribute(
                defaultUndirectedEdgeAttribute, attConsumer
                        .getUndirectedEdgeAttribute());
        defaultDirectedEdgeAttribute = unifyWithDefaultAttribute(
                defaultDirectedEdgeAttribute, attConsumer
                        .getDirectedEdgeAttribute());
        addAttributeToGraph(attConsumer.getGraphAttribute());
        addAttributeToExistingNodes(attConsumer.getNodeAttribute());
        addAttributesToExistingEdges(attConsumer.getDirectedEdgeAttribute(),
                attConsumer.getUndirectedEdgeAttribute());
        attributeConsumers.add(attConsumer);
    }

    /**
     * Adds a new edge to the current graph. Informs the ListenerManager about
     * the new node. This method adds a copy of the
     * <code>defaultEdgeAttributes</code> after the <code>preEdgeAdded</code>
     * and before the <code>postEdgeAdded</code> event.
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
     * 
     * @exception GraphElementNotFoundException
     *                if any of the nodes cannot be found in the graph.
     */
    public Edge addEdge(Node source, Node target, boolean directed)
            throws GraphElementNotFoundException {
        assert (source != null) && (target != null);

        if (logger.isLoggable(Level.INFO)) {
            logger.info("adding a new edge to the graph (Nr. "
                    + CurrentNumberOfEdge + ")");
        }

        CurrentNumberOfEdge++;

        ListenerManager listMan = this.getListenerManager();

        if (this != source.getGraph()) {
            logger.severe("throwing GENFException, because the given source "
                    + "was not in the same graph");
            throw new GraphElementNotFoundException(
                    "source is not in the same graph as the edge");
        }

        if (this != target.getGraph()) {
            logger.severe("throwing GENFException, because the given target "
                    + "was not in the same graph");
            throw new GraphElementNotFoundException(
                    "target is not in the same graph as the edge");
        }

        listMan.preEdgeAdded(new GraphEvent(source, target));

        Edge edge = doAddEdge(source, target, directed);

        // add the edge's default attribute
        if (directed) {
            if (defaultDirectedEdgeAttribute != null) {
                edge.addAttribute((Attribute) defaultDirectedEdgeAttribute
                        .copy(), "");
            }
        } else {
            if (defaultUndirectedEdgeAttribute != null) {
                edge.addAttribute((Attribute) defaultUndirectedEdgeAttribute
                        .copy(), "");
            }
        }

        listMan.postEdgeAdded(new GraphEvent(edge));

        return edge;
    }

    /**
     * Adds a new edge to the current graph. Informs the ListenerManager about
     * the new node. This method does not add any
     * <code>defaultEdgeAttributes</code>.
     * 
     * @param source
     *            the source of the edge to add.
     * @param target
     *            the target of the edge to add.
     * @param directed
     *            <code>true</code> if the edge shall be directed,
     *            <code>false</code> otherwise.
     * @param col
     *            the <code>CollectionAttribute</code> with which the edge is
     *            initialized.
     * 
     * @return the new edge.
     * 
     * @exception GraphElementNotFoundException
     *                if any of the nodes cannot be found in the graph.
     */
    public Edge addEdge(Node source, Node target, boolean directed,
            CollectionAttribute col) throws GraphElementNotFoundException {
        assert (source != null) && (target != null) && (col != null);
        logger
                .info("adding a new edge with collection attributes to the graph (Nr."
                        + CurrentNumberOfEdge + ")");
        CurrentNumberOfEdge++;

        if (this != source.getGraph()) {
            logger.severe("throwing GENFException, because the given source "
                    + "was not in the same graph");
            throw new GraphElementNotFoundException(
                    "source is not in the same graph as the edge");
        }

        if (this != target.getGraph()) {
            logger.severe("throwing GENFException, because the given target "
                    + "was not in the same graph");
            throw new GraphElementNotFoundException(
                    "target is not in the same graph as the edge");
        }

        // logger.info("adding a new edge to the graph");
        ListenerManager listMan = this.getListenerManager();

        listMan.preEdgeAdded(new GraphEvent(source, target));

        Edge edge = doAddEdge(source, target, directed, col);
        listMan.postEdgeAdded(new GraphEvent(edge));

        return edge;
    }

    /**
     * Adds a copy of the specified edge to the graph as a new edge between the
     * specified source and target node. Informs the ListenerManager about the
     * newly added edge through the call to <code>addEdge()</code>. Also informs
     * the ListenerManager about the copy of the attributes added to the edge by
     * adding them separatly throug
     * <code>CollectionAttribute.add(Attribute)</code>.
     * 
     * @param edge
     *            the <code>Egde</code> which to copy and add.
     * @param source
     *            the source <code>Node</code> of the copied and added edge.
     * @param target
     *            the target <code>Node</code> of the copied and added edge.
     * 
     * @return DOCUMENT ME!
     */
    public Edge addEdgeCopy(Edge edge, Node source, Node target) {
        assert (edge != null) && (source != null) && (target != null);

        CollectionAttribute col = (CollectionAttribute) edge.getAttributes()
                .copy();
        Edge newEdge = this.addEdge(source, target, edge.isDirected(), col);

        return newEdge;
    }

    /**
     * Adds a Graph g to the current graph. Graph g will be copied and then all
     * its nodes and edges will be added to the current graph. Like this g will
     * not be destroyed.
     * 
     * @param g
     *            the Graph to be added.
     */
    public void addGraph(Graph g) {
        assert g != null;

        Map<Node, Node> hm = new HashMap<Node, Node>();

        for (Iterator<Node> nodeIt = g.getNodesIterator(); nodeIt.hasNext();) {
            Node oldNode = nodeIt.next();
            Node newNode = addNodeCopy(oldNode);
            hm.put(oldNode, newNode);
        }

        for (Iterator<Edge> edgeIt = g.getEdgesIterator(); edgeIt.hasNext();) {
            Edge oldEdge = (edgeIt.next());

            CollectionAttribute col = (CollectionAttribute) oldEdge
                    .getAttributes().copy();

            Node source = hm.get(oldEdge.getSource());
            Node target = hm.get(oldEdge.getTarget());
            this.addEdge(source, target, oldEdge.isDirected(), col);
        }
    }

    /**
     * Adds a new node to the graph. Informs the ListenerManager about the new
     * node. This method adds a copy of the <code>defaultNodeAttribute</code> to
     * the newly created node (after the <code>preNodeAdded</code> event and
     * before the <code>postNodeAdded</code> event).
     * 
     * @return the new node.
     */
    public Node addNode() {
        if (logger.isLoggable(Level.INFO)) {
            logger.info("adding a new node to the graph (Nr. "
                    + CurrentNumberOfNode + ")");
        }

        CurrentNumberOfNode++;

        Node node = createNode();
        GraphEvent ga = new GraphEvent(node);

        listenerManager.preNodeAdded(ga);
        doAddNode(node);

        // add the node's default attribute
        if (defaultNodeAttribute != null) {
            node.addAttribute((Attribute) defaultNodeAttribute.copy(), "");
        }

        listenerManager.postNodeAdded(ga);

        logger.fine("returning the created node and exiting addNode()");

        return node;
    }

    /**
     * Adds a new node to the graph. Informs the ListenerManager about the new
     * node. Default node attributes (<code>defaultNodeAttribute</code>) are not
     * added by this method.
     * 
     * @param col
     *            the <code>CollectionAttribute</code> the node is initialized
     *            with.
     * 
     * @return the new node.
     */
    public Node addNode(CollectionAttribute col) {
        assert col != null;
        logger.info("adding a new node to the graph (Nr." + CurrentNumberOfNode
                + ")");
        CurrentNumberOfNode++;

        Node node = createNode(col);

        GraphEvent ga = new GraphEvent(node);

        listenerManager.preNodeAdded(ga);
        doAddNode(node);
        listenerManager.postNodeAdded(ga);

        logger.fine("returning the created node and exiting addNode()");

        return node;
    }

    /**
     * Adds a copy of the specified node to the graph and returns the copy.
     * Informs the ListenerManager about the newly added node in the same way as
     * if a completely new node was added. Also informs the ListenerManager
     * about the addition of attributes by using the <code>add(Attribute)</code>
     * method of <code>CollectionAttribute</code>.
     * 
     * @param node
     *            the <code>Node</code> which to copy and to add.
     * 
     * @return the newly created node.
     */
    public Node addNodeCopy(Node node) {
        assert node != null;

        CollectionAttribute col = (CollectionAttribute) node.getAttributes()
                .copy();
        Node newNode = this.addNode(col);

        return newNode;
    }

    /**
     * Returns <code>true</code>, if the graph contains an edge between the
     * nodes n1 and n2, <code>false</code> otherwise.
     * 
     * @param n1
     *            first node of the edge to search for.
     * @param n2
     *            second node of the edge to search for.
     * 
     * @return <code>true</code>, if the graph contains an edge between the
     *         nodes n1 and n2 <code>false</code> otherwise.
     * 
     * @exception GraphElementNotFoundException
     *                if any of the nodes cannot be found in the graph.
     */
    public boolean areConnected(Node n1, Node n2)
            throws GraphElementNotFoundException {
        assert (n1 != null) && (n2 != null);

        return getEdges(n1, n2).size() > 0;
    }

    /**
     * Deletes the current graph by resetting all its attributes. The graph is
     * then equal to a new generated graph i.e. the list of nodes and edges will
     * be empty. A special event for clearing the graph will be passed to the
     * listener manager.
     */
    public void clear() {
        ListenerManager listMan = getListenerManager();
        listMan.preGraphCleared(new GraphEvent(this));
        doClear();
        listMan.postGraphCleared(new GraphEvent(this));
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
    public boolean containsEdge(Edge e) {
        assert e != null;

        return getEdges().contains(e);
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
    public boolean containsNode(Node n) {
        assert n != null;

        return getNodes().contains(n);
    }

    /**
     * Deletes the given edge from the current graph. Informs the
     * ListenerManager about the deletion.
     * 
     * @param e
     *            the edge to delete.
     * 
     * @exception GraphElementNotFoundException
     *                if the edge to delete cannot be found in the graph.
     */
    public void deleteEdge(Edge e) throws GraphElementNotFoundException {
        assert e != null;

        if (this != e.getGraph())
            throw new GraphElementNotFoundException(
                    "the edge was not found in this graph");

        logger.info("deleting edge e from this graph");

        ListenerManager listMan = this.getListenerManager();
        GraphEvent ga = new GraphEvent(e);

        listMan.preEdgeRemoved(ga);
        doDeleteEdge(e);
        e.remove();
        listMan.postEdgeRemoved(ga);
    }

    /**
     * Deletes the given node. First all in- and out-going edges will be deleted
     * using <code>deleteEdge()</code> and thereby informs the ListenerManager
     * implicitly. Then deletes the node and informs the ListenerManager about
     * the deletion.
     * 
     * @param n
     *            the node to delete.
     * 
     * @exception GraphElementNotFoundException
     *                if the node to delete cannot be found in the graph.
     */
    public void deleteNode(Node n) throws GraphElementNotFoundException {
        assert n != null;

        if (n.getGraph() != this)
            throw new GraphElementNotFoundException(
                    "the node was not found in this graph");

        logger.info("deleting a node from the graph");

        ListenerManager listMan = this.getListenerManager();
        GraphEvent ga = new GraphEvent(n);

        listMan.preNodeRemoved(ga);
        doDeleteNode(n);
        n.remove();
        listMan.postNodeRemoved(ga);
        n = null;
    }

    /**
     * Returns <code>true</code>, if the given attribute consumer was in the
     * list of attribute consumers and could be removed.
     * 
     * @param attConsumer
     *            DOCUMENT ME!
     * 
     * @return <code>true</code>, if the given attribute consumer was in the
     *         list of attribute consumers and could be removed.
     */
    public boolean removeAttributeConsumer(AttributeConsumer attConsumer) {
        return attributeConsumers.remove(attConsumer);
    }

    /**
     * Adds a new edge to the current graph.
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
     * 
     * @exception GraphElementNotFoundException
     *                if any of the nodes cannot be found in the graph.
     */
    protected Edge doAddEdge(Node source, Node target, boolean directed)
            throws GraphElementNotFoundException {
        return doAddEdge(source, target, directed, null);
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
     *            the <code>CollectionAttribute</code> with which the edge is
     *            initialized.
     * 
     * @return the new edge.
     * 
     * @exception GraphElementNotFoundException
     *                if any of the nodes cannot be found in the graph.
     */
    protected abstract Edge doAddEdge(Node source, Node target,
            boolean directed, CollectionAttribute col)
            throws GraphElementNotFoundException;

    /**
     * Adds the node to the graph.
     * 
     * @param node
     *            the node to add
     */
    protected abstract void doAddNode(Node node);

    /**
     * Deletes the current graph by resetting all its attributes. The graph is
     * then equal to a new generated graph i.e. the list of nodes and edges will
     * be empty.
     */
    protected abstract void doClear();

    /**
     * Deletes the given edge from the current graph.
     * 
     * @param e
     *            the edge to delete.
     * 
     * @exception GraphElementNotFoundException
     *                if the edge to delete cannot be found in the graph.
     */
    protected abstract void doDeleteEdge(Edge e)
            throws GraphElementNotFoundException;

    /**
     * Deletes the given node. First all in- and out-going edges will be deleted
     * using <code>deleteEdge()</code> and thereby informs the ListenerManager
     * implicitly.
     * 
     * @param n
     *            the node to delete.
     * 
     * @exception GraphElementNotFoundException
     *                if the node to delete cannot be found in the graph.
     */
    protected abstract void doDeleteNode(Node n)
            throws GraphElementNotFoundException;

    /**
     * Creates a new <code>Node</code>.
     * 
     * @return the newly created node.
     */
    abstract Node createNode();

    /**
     * Creates a new <code>Node</code> that is initialize with the given
     * <code>CollectionAttribute</code>.
     * 
     * @return the newly created node.
     */
    public abstract Node createNode(CollectionAttribute col);

    /**
     * Tries to add the given attribute to every edge in this graph.
     * 
     * @param directed
     *            the attribute to add to every directed edge.
     * @param undirected
     *            the attribute to add to every undirected edge.
     */
    private void addAttributesToExistingEdges(CollectionAttribute directed,
            CollectionAttribute undirected) {
        if (directed == null && undirected == null)
            return;

        for (Iterator<Edge> i = getEdgesIterator(); i.hasNext();) {
            try {
                Edge e = i.next();
                if (e.isDirected()) {
                    if (directed != null) {
                        e.addAttribute((Attribute) directed.copy(), "");
                    }
                } else {
                    if (undirected != null) {
                        e.addAttribute((Attribute) undirected.copy(), "");
                    }
                }
            } catch (AttributeExistsException aee) {
                // TODO: check
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tries to add the given attribute to every node in this graph.
     * 
     * @param att
     *            the attribute to add to every node.
     */
    private void addAttributeToExistingNodes(CollectionAttribute att) {
        if (att == null)
            return;

        for (Iterator<Node> i = getNodesIterator(); i.hasNext();) {
            try {
                Node n = i.next();
                n.addAttribute((Attribute) att.copy(), "");
            } catch (AttributeExistsException aee) {
                // TODO: check
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tries to add the given attribute to every node in this graph.
     * 
     * @param att
     *            the attribute to add to every node.
     */
    private void addAttributeToGraph(CollectionAttribute att) {
        if (att == null)
            return;

        try {
            addAttribute((Attribute) att.copy(), "");
        } catch (AttributeExistsException aee) {
            // TODO: check
        }

    }

    /**
     * Unifies the given default attribute with the given collection attribute.
     * 
     * @param defaultAttribute
     *            the default attribute
     * @param newAttribute
     *            the new collection attribute to add
     * 
     * @return the unified defaultAttribute
     * 
     * @throws UnificationException
     *             DOCUMENT ME!
     */
    private CollectionAttribute unifyWithDefaultAttribute(
            CollectionAttribute defaultAttribute,
            CollectionAttribute newAttribute) throws UnificationException {
        if (newAttribute == null)
            return defaultAttribute;
        else if (defaultAttribute == null)
            return newAttribute;
        else {
            for (String id : newAttribute.getCollection().keySet()) {
                try {
                    defaultAttribute.add((Attribute) newAttribute.getAttribute(
                            id).copy());
                } catch (AttributeExistsException aee) {
                    // TODO: check
                }
            }
            return defaultAttribute;
        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
