package org.graffiti.plugins.algorithms.mst.adapters;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Stack;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * Proxy object for a {@link org.graffiti.graph.Graph}; Provides customized
 * methods for minimum spanning tree algorithms and their animations.
 * 
 * @author Harald
 * @version $Revision$ $Date$
 */
public class GraphAdapter {
    /**
     * The graph this proxy stands for.
     */
    private Graph graph = null;

    private EdgeAdapterFactory edgeAdapterFactory = null;

    private NodeAdapterFactory nodeAdapterFactory = null;

    /**
     * Creates a new graph adapter with a default edge adapter factory an a
     * default node adapter factory.
     * 
     * @see NodeAdapterFactory
     * @see EdgeAdapterFactory
     */
    public GraphAdapter(Graph g) {
        this(g, new EdgeAdapterFactory(), new NodeAdapterFactory());
    }

    /**
     * Creates a new graph proxy for the specified graph and with the specified
     * proxy builder.
     * 
     * @param g
     *            the graph this proxy stands for.
     */
    public GraphAdapter(Graph g, EdgeAdapterFactory ef, NodeAdapterFactory nf) {
        graph = g;
        edgeAdapterFactory = ef;
        nodeAdapterFactory = nf;
    }

    /**
     * Returns the nodes of this graph as an unmodifiable collection.
     * 
     * @return the nodes of this graph as an unmodifiable collection.
     */
    public Collection<NodeAdapter> nodes() {
        return new java.util.AbstractCollection<NodeAdapter>() {
            @Override
            public int size() {
                return graph.getNodes().size();
            }

            @Override
            public Iterator<NodeAdapter> iterator() {
                return new Iterator<NodeAdapter>() {
                    private Iterator<Node> i = graph.getNodes().iterator();

                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    public NodeAdapter next() {
                        return nodeAdapterFactory.createNodeAdapter(i.next(),
                                edgeAdapterFactory);
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * Returns the start node of this graph.
     * 
     * @return the start node of this graph.
     * @throws java.util.NoSuchElementException
     *             if the graph is empty.
     */
    public NodeAdapter startNode() {
        if (graph.isEmpty())
            throw new java.util.NoSuchElementException();
        return nodeAdapterFactory.createNodeAdapter(graph.getNodes().iterator()
                .next(), edgeAdapterFactory);
    }

    /**
     * Returns <tt>true</tt> if this graph is empty; i.e. contains no nodes and
     * edges.
     * 
     * @return <tt>true</tt> if this graph is empty; i.e. contains no nodes and
     *         edges.
     */
    public boolean isEmpty() {
        return graph.isEmpty();
    }

    /**
     * Returns the edges of this graph.
     * 
     * @return the edges of this graph.
     */
    public Collection<EdgeAdapter> edges() {
        return new java.util.AbstractCollection<EdgeAdapter>() {
            @Override
            public int size() {
                return graph.getEdges().size();
            }

            @Override
            public Iterator<EdgeAdapter> iterator() {
                return new Iterator<EdgeAdapter>() {
                    Iterator<Edge> i = graph.getEdges().iterator();

                    public boolean hasNext() {
                        return i.hasNext();
                    }

                    public EdgeAdapter next() {
                        return edgeAdapterFactory.createAdapter(i.next());
                    }

                    public void remove() {
                        throw new UnsupportedOperationException();
                    }
                };
            }
        };
    }

    /**
     * Returns <tt>true</tt> if this graph is connected.
     * 
     * @return <tt>true</tt> if this graph is connected.
     */
    public boolean isConnected() {
        if (graph.isEmpty())
            return true;
        Set<Node> visited = new java.util.HashSet<Node>(graph.getNodes().size());
        Stack<Node> stack = new Stack<Node>();
        stack.push(graph.getNodes().iterator().next());
        while (!stack.isEmpty()) {
            Node curr = stack.pop();
            visited.add(curr);
            for (Node n : curr.getAllOutNeighbors()) {
                if (!visited.contains(n)) {
                    stack.push(n);
                }
            }
        }
        return visited.size() == graph.getNodes().size();
    }

    /**
     * Returns <tt>true</tt> if this graph is undirected.
     * 
     * @return <tt>true</tt> if this graph is undirected.
     */
    public boolean isUndirected() {
        return graph.isUndirected();
    }

    /**
     * Clears this graph; i.e. clears every node and every edge of this graph.
     * 
     * @see NodeAdapter#clear()
     * @see EdgeAdapter#clear()
     * 
     */
    public void clear() {
        clearNodes();
        clearEdges();
    }

    /**
     * Clears all nodes of this graph.
     * 
     * @see NodeAdapter#clear()
     */
    private void clearNodes() {
        for (NodeAdapter n : nodes()) {
            n.clear();
        }
    }

    /**
     * Clears all edges of this graph.
     * 
     * @see EdgeAdapter#clear()
     */
    private void clearEdges() {
        for (EdgeAdapter e : edges()) {
            e.clear();
        }
    }

    /**
     * Initializes this graph; i.e. initializes every node and every edge of
     * this graph.
     * 
     * @see NodeAdapter#init(HeapAdapter)
     * @see EdgeAdapter#init()
     * 
     * @param h
     *            the heap to be used for initializing this graph's nodes.
     */
    public void init(HeapAdapter h) {
        h.setEdgeAdapterFactory(edgeAdapterFactory);
        h.setNodeAdapterFactory(nodeAdapterFactory);
        initNodes(h);
        initEdges();
    }

    /**
     * Initializes all nodes of this graph.
     * 
     * @see NodeAdapter#init(HeapAdapter)
     * 
     * @param h
     *            the heap used to initialize the nodes of this graph.
     */
    public void initNodes(HeapAdapter h) {
        for (NodeAdapter n : nodes()) {
            n.init(h);
        }
    }

    /**
     * Initializes the edges of this graph.
     * 
     * @see EdgeAdapter#init()
     */
    public void initEdges() {
        for (EdgeAdapter e : edges()) {
            e.init();
        }
    }

    /**
     * Cleans this graph; i.e. removes unnecessary attributes.
     * <p>
     * This implementation clears all nodes and cleans all edges.
     * 
     * @see NodeAdapter#clear()
     * @see EdgeAdapter#clean()
     * 
     */
    public void clean() {
        clearNodes();
        cleanUpEdges();
    }

    /**
     * Cleans all edges of this graph.
     * 
     * @see EdgeAdapter#clean()
     */
    private void cleanUpEdges() {
        for (EdgeAdapter e : edges()) {
            e.clean();
        }
    }

    /**
     * Returns <tt>true</tt> if this graph contains only one node.
     * 
     * @return <tt>true</tt> if this graph contains only one node.
     */
    public boolean isSingleton() {
        return nodes().size() == 1;
    }

    public boolean isMultiGraph() {
        for (Node n : graph.getNodes()) {
            for (Node m : n.getAllOutNeighbors())
                if (graph.getEdges(n, m).size() > 1)
                    return true;
        }
        return false;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
