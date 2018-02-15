package org.graffiti.plugins.algorithms.planarity;

import java.util.LinkedList;

/**
 * The planarity algorithm's main node class.
 * 
 * The class has two subclasses: The class <code>RealNode</code> whose objects
 * directly correspond to objects of <code>org.graffiti.graph.Node</code> and
 * the class <code>VirtualNode</code> which represents auxiliary nodes needed
 * during the planarity test.
 * 
 * @author Wolfgang Brunner
 */
public abstract class ArbitraryNode extends AdjacencyListLink {

    /**
     * These constants are used while finding a Kuratowski subgraph.
     * 
     * see "Ein effizienter Planarit�tstest nach Boyer, Myrvold: Analyse und
     * Realisierung" (my diploma thesis), pages 56, 57
     * 
     * @see #quadrant
     */
    public final static int NOT_ON_BORDER = 0;

    public final static int TOP_RIGHT = 1;

    public final static int CENTER_RIGHT = 2;

    public final static int BOTTOM_RIGHT = 3;

    public final static int TOP_LEFT = -1;

    public final static int CENTER_LEFT = -2;

    public final static int BOTTOM_LEFT = -3;

    /**
     * The degree of the node.
     */
    public int degree;

    /**
     * Used by the Walk Up to store which nodes have been visited.
     * 
     * @see ConnectedComponent#walkUp
     */
    public VirtualNode visited;

    /**
     * The <code>HalfEdge</code> connecting the node to its DFS child.
     */
    public HalfEdge edgeToChild;

    /**
     * The <code>Bicomp</code> the node belongs to.
     */
    public Bicomp bicomp;

    /**
     * The calculated ordered adjacency list is stored here.
     */
    public LinkedList<ArbitraryNode> adjacencyList;

    /**
     * Stores how often the node is on a path found during the search of a
     * Kuratoski subgraph.
     * 
     * @see Bicomp#removeDoubles(LinkedList)
     */
    public int amountOnPath;

    /**
     * Used while searching a Kuratowski subgraph to store the quadrant of the
     * bicomp the node is in.
     * 
     * see "Ein effizienter Planarit�tstest nach Boyer, Myrvold: Analyse und
     * Realisierung" (my diploma thesis), pages 56, 57
     */
    public int quadrant;

    /**
     * The lowest depth first search index in this connected component
     */
    public int DFSStartNumber;

    /**
     * Returns the <code>RealNode</code> corresponding to this node.
     * 
     * @return The corresponding <code>RealNode</code>
     * 
     * @see RealNode#getRealNode
     * @see VirtualNode#getRealNode
     */
    public abstract RealNode getRealNode();
}
