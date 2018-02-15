// =============================================================================
//
//   Graph.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Graph.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.graph;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.graffiti.attributes.Attributable;
import org.graffiti.attributes.AttributeConsumer;
import org.graffiti.attributes.AttributeTypesManager;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.UnificationException;
import org.graffiti.core.DeepCopy;

/**
 * A <code>Graph</code> consists of a set of nodes and a set of edges. These two
 * sets are not ordered. A <code>Graph</code> is considered directed iff all its
 * edges are directed, undirected iff all its edges are undirected. A
 * <code>Graph</code> may contain directed and undirected edges. It can only be
 * directed and undirected at the same time iff it is empty. Graphs can be
 * copied. The methode <code>copy()</code> of the interface
 * <code>org.graffiti.core.DeepCopy</code> returns a copy of the
 * <code>Graph</code> which contains no references to the original
 * <Code>Graph</Code> any more. Every <code>Graph</code> implementation
 * implements this interface. <Code>Graph</Code> implementations are to be
 * observable. This is required e.g. for visualisation. For this reason every
 * <code>Graph</code> instance contains a <code>ListenerManager</code> which
 * provides the required functionality. To use this functionality all methods
 * modifying the <code>Graph</code> should inform the
 * <code>ListenerManager</code>.
 * 
 * <p>
 * <b>Implementation notes:</b> For example, the method <code>xy()</code> of any
 * <code>Graph</code> implementation will have to look as follows: <blockquote>
 * 
 * <pre>
 *            public whatever xy() {
 *                GraphEvent ge = new GraphEvent(...);
 *                listenerManager.preXy(g);
 *                // ...
 *                // here comes the functionality of method xy
 *                // ...
 *                ge = new GraphEvent(...);
 *                listenerManager.postXy(g);
 *                return ... ;
 *            }
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @version $Revision: 5767 $
 * 
 * @see Node
 * @see Edge
 * @see org.graffiti.attributes.Attributable
 */
public interface Graph extends Attributable, DeepCopy {

    /**
     * Returns the <code>AttributeTypesManager</code> of the <code>Graph</code>.
     * 
     * @return the <code>AttributeTypesManager</code> of the <code>Graph</code>.
     */
    public AttributeTypesManager getAttTypesManager();

    /**
     * Indicates whether the <code>Graph</code> is directed. A
     * <code>Graph</code> is directed if all the edges are directed.
     * 
     * @return <code>true</code> if the <code>Graph</code> is directed
     *         <code>false</code> otherwise.
     */
    public boolean isDirected();

    /**
     * Indicates whether the <code>Graph</code> has been modified. A call to
     * <code>setModified</code> can change this property to false e.g. after the
     * graph has been saved to disc. Changes to attributes and delete/add
     * commands of nodes and edges should change this property to
     * <code>true</code>.
     * 
     * @return True, if the graph has been modifed, False if not.
     */
    public boolean isModified();

    /**
     * Should be set to False after saving changes and to True after making
     * changes to the Graph. The add/delete nodes and edges commands as well as
     * the property change methods should set this value to True.
     * 
     * @param modified
     *            Indicates the new status of this field.
     */
    public void setModified(boolean modified);

    /**
     * When passing a true value, all undirected edges in the graph will be set
     * to be directed. V.v. for a false value.
     * 
     * @param directed
     */
    public void setDirected(boolean directed);

    /**
     * When passing a true value, all undirected edges in the graph will be set
     * to be directed. V.v. for a false value. A true second parameter indicates
     * that all edges shall get one arrow at their tips (i.e. close to the
     * target node).
     * 
     * @param directed
     * @param adjustArrows
     */
    public void setDirected(boolean directed, boolean adjustArrows);

    /**
     * Returns all edges of the graph. The returned collection may be
     * unmodifiable and is only valid as long as the graph is not modified.
     * 
     * @return a list of all edges of the graph.
     */
    public Collection<Edge> getEdges();

    /**
     * Returns a collection containing all the edges between n1 and n2. There
     * can be more than one <code>Edge</code> between two nodes. The edges
     * returned by this method can go from n1 to n2 or vice versa, be directed
     * or not.
     * 
     * @param n1
     *            the first <code>Node</code>
     * @param n2
     *            the second <code>Node</code>
     * 
     * @return a <code>Collection</code> containing all edges between n1 and n2,
     *         an empty collection if there is no <code>Edge</code> between the
     *         two nodes.
     * 
     * @exception GraphElementNotFoundException
     *                if one of the nodes is not contained in the graph.
     */
    public Collection<Edge> getEdges(Node n1, Node n2);

    /**
     * Returns an iterator over the edges of the <code>Graph</code>.
     * 
     * @return an iterator over the edges of the <code>Graph</code>.
     */
    public Iterator<Edge> getEdgesIterator();

    /**
     * Returns <code>true</code> if the <code>Graph</code> is empty. E.g. the
     * <code>Graph</code> is equal to a <code>Graph</code> which has been
     * cleared.
     * 
     * @return <code>true</code> if the <code>Graph</code> is empty,
     *         <code>false</code> otherwise.
     */
    public boolean isEmpty();

    /**
     * Returns all nodes and all edges contained in this graph.
     * 
     * @return Collection
     */
    public Collection<GraphElement> getGraphElements();

    /**
     * Returns all nodes of the graph. The returned list may be unmodifiable and
     * is only valid as long as the graph is not modified.
     * 
     * @return a list containing all the nodes of the graph.
     */
    public List<Node> getNodes();

    /**
     * Returns an iterator over the nodes of the graph. If the graph is empty an
     * empty iterator will be returned.
     * 
     * Note that the iterator is not guaranteed to support the
     * {@link Iterator#remove()} operation.
     * 
     * @return an iterator containing the nodes of the graph.
     */
    public Iterator<Node> getNodesIterator();

    /**
     * Returns the number of directed edges of the <code>Graph</code>.
     * 
     * @return the number of directed edges of the <code>Graph</code>.
     */
    public int getNumberOfDirectedEdges();

    /**
     * Returns the number of edges of the <code>Graph</code>.
     * 
     * @return the number of edges of the <code>Graph</code>.
     */
    public int getNumberOfEdges();

    /**
     * Returns the number of nodes in the <code>Graph</code>.
     * 
     * @return the number of nodes in the <code>Graph</code>.
     */
    public int getNumberOfNodes();

    /**
     * Returns the number of undirected edges in the <code>Graph</code>.
     * 
     * @return the number of undirected edges in the <code>Graph</code>.
     */
    public int getNumberOfUndirectedEdges();

    /**
     * Indicates whether the <code>Graph</code> is undirected. A
     * <code>Graph</code> is undirected if all the edges are undirected.
     * 
     * @return <code>true</code> if the <code>Graph</code> is undirected,
     *         <code>false</code> otherwise.
     */
    public boolean isUndirected();

    /**
     * Adds the given attribute consumer to the list of attribute consumers.
     * 
     * @param attConsumer
     *            the attribute consumer to add.
     */
    public void addAttributeConsumer(AttributeConsumer attConsumer)
            throws UnificationException;

    /**
     * Adds a new <code>Edge</code> to the current <code>Graph</code>. Informs
     * the ListenerManager about the new <code>Edge</code>.
     * 
     * @param source
     *            the source of the <code>Edge</code> to add.
     * @param target
     *            the target of the <code>Edge</code> to add.
     * @param directed
     *            <code>true</code> if the <code>Edge</code> shall be directed,
     *            <code>false</code> otherwise.
     * 
     * @return the newly generated <code>Edge</code>.
     * 
     * @exception GraphElementNotFoundException
     *                if any of the nodes cannot be found in the
     *                <code>Graph</code>.
     */
    public Edge addEdge(Node source, Node target, boolean directed)
            throws GraphElementNotFoundException;

    /**
     * Adds a new <code>Edge</code> to the current <code>Graph</code>. Informs
     * the ListenerManager about the new <code>Edge</code>.
     * 
     * @param source
     *            the source of the <code>Edge</code> to add.
     * @param target
     *            the target of the <code>Edge</code> to add.
     * @param directed
     *            <code>true</code> if the <code>Edge</code> shall be directed,
     *            <code>false</code> otherwise.
     * @param col
     *            the <code>CollectionAttribute</code> this edge is initialized
     *            with.
     * 
     * @return the newly generated <code>Edge</code>.
     * 
     * @exception GraphElementNotFoundException
     *                if any of the nodes cannot be found in the
     *                <code>Graph</code>.
     */
    public Edge addEdge(Node source, Node target, boolean directed,
            CollectionAttribute col) throws GraphElementNotFoundException;

    /**
     * Creates a new Edge without actually adding it to the Graph.
     * 
     * 
     * @param source
     *            source-node
     * @param target
     *            target-node
     * @param directed
     *            shall the new edge be directed?
     * @param col
     *            attributes for the new edge
     * @return the newly created edge
     */
    public Edge createEdge(Node source, Node target, boolean directed,
            CollectionAttribute col);

    /**
     * Adds a copy of the specified <code>Edge</code> to the <code>Graph</code>
     * as a new <code>Edge</code> between the specified source and target
     * <code>Node</code>. Informs the ListenerManager about the newly added
     * <code>Edge</code>. Also informs the ListenerManager about the copy of the
     * attributes added to the <code>Edge</code>.
     * 
     * @param edge
     *            the <code>Egde</code> which to copy and add.
     * @param source
     *            the source <code>Node</code> of the copied and added
     *            <code>Edge</code>.
     * @param target
     *            the target <code>Node</code> of the copied and added
     *            <code>Edge</code>.
     * 
     * @return DOCUMENT ME!
     */
    public Edge addEdgeCopy(Edge edge, Node source, Node target);

    /**
     * Adds a <Code>Graph</Code> g to the current <code>Graph</code>.
     * <Code>Graph</Code> g will be copied and then all its nodes and edges will
     * be added to the current <code>Graph</code>. Like this g will not be
     * destroyed.
     * 
     * @param g
     *            the <Code>Graph</Code> to be added.
     */
    public void addGraph(Graph g);

    /**
     * Adds a new <code>Node</code> to the <code>Graph</code>. Informs the
     * ListenerManager about the new <code>Node</code>.
     * 
     * @return the new <code>Node</code>.
     */
    public Node addNode();

    /**
     * Adds a new node to the graph. Informs the ListenerManager about the new
     * node.
     * 
     * @param col
     *            the <code>CollectionAttribute</code> the node is initialized
     *            with.
     * 
     * @return the new node.
     */
    public Node addNode(CollectionAttribute col);

    /**
     * Creates a new node using the given attribute-object without actually
     * adding the created Node to the graph.
     * 
     * @param col
     *            attributes for the new node
     * @return newly created Node
     */
    public Node createNode(CollectionAttribute col);

    /**
     * Adds a copy of the specified <code>Node</code> to the <code>Graph</code>.
     * Informs the ListenerManager about the newly added <code>Node</code> in
     * the same way as if a completely new <code>Node</code> was added. Also
     * informs the ListenerManager about the addition of attributes.
     * 
     * @param node
     *            the <code>Node</code> which to copy and to add.
     * 
     * @return DOCUMENT ME!
     */
    public Node addNodeCopy(Node node);

    /**
     * Deletes the current <code>Graph</code> by resetting all its attributes.
     * The <code>Graph</code> is then equal to a newly generated
     * <code>Graph</code>.
     */
    public void clear();

    /**
     * Returns <code>true</code>, if the <code>Graph</code> contains the
     * specified <code>Edge</code>, <code>false</code> otherwise.
     * 
     * @param e
     *            the <code>Edge</code> to seach for
     * 
     * @return <code>true</code>, if the <code>Graph</code> contains the
     *         specified <code>Edge</code>, <code>false</code> otherwise.
     */
    public boolean containsEdge(Edge e);

    /**
     * Returns <code>true</code>, if the <code>Graph</code> contains the
     * specified <code>Node</code>, <code>false</code> otherwise.
     * 
     * @param n
     *            the <code>Node</code> to search for.
     * 
     * @return <code>true</code>, if the <code>Graph</code> contains the
     *         <code>Node</code> n, <code>false</code> otherwise.
     */
    public boolean containsNode(Node n);

    /**
     * Deletes <code>Edge</code> e from the current <code>Graph</code>. Informs
     * the ListenerManager about the deletion.
     * 
     * @param e
     *            the <code>Edge</code> to delete.
     * 
     * @exception GraphElementNotFoundException
     *                if the <code>Edge</code> to delete cannot be found in the
     *                <code>Graph</code>.
     */
    public void deleteEdge(Edge e) throws GraphElementNotFoundException;

    /**
     * Deletes the <code>Node</code> n. All in- and out-going edges will be
     * deleted. Informs the ListenerManager about the deletion of the
     * <code>Node</code> and the concerned edges.
     * 
     * @param n
     *            the <code>Node</code> to delete.
     * 
     * @exception GraphElementNotFoundException
     *                if the <code>Node</code> to delete cannot be found in the
     *                <code>Graph</code>.
     */
    public void deleteNode(Node n) throws GraphElementNotFoundException;

    /**
     * Returns <code>true</code>, if the given attribute consumer was in the
     * list of attribute consumers and could be removed.
     * 
     * @return <code>true</code>, if the given attribute consumer was in the
     *         list of attribute consumers and could be removed.
     */
    public boolean removeAttributeConsumer(AttributeConsumer attConsumer);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
