// =============================================================================
//
//   CrossMinObject.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CrossMinObject.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.crossmin.global;

import java.util.LinkedList;

import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.LazyCrossMinObjectArrayList;
import org.graffiti.plugins.algorithms.sugiyama.util.LazyIntArrayList;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * <p>
 * This class acts as a container for either nodes, isolated dummy nodes or
 * inner segments in a graph.
 * </p>
 * <p>
 * These objects are being used by cross minimization algorithms that minimize
 * the number of edge intersections on a global scale - in contrast to ordinary
 * algorithms solving one-sided two layer straightline crossing minimization.
 * </p>
 * <p>
 * A <code>CrossMinObject</code> consists of either exactly one
 * <code>Node</code>, or a <code>LinkedList</code> of <code>Nodes</code>. The
 * former is true for the following <code>Nodes</code>:
 * </p>
 * <ul>
 * <li>"Real" <code>Nodes</code></li>
 * <li>Dummy nodes that are not part of a long edge, i.e. its upper and lower
 * neighbor is no dummy node</li>
 * </ul>
 * <p>
 * The other class of <code>CrossMinObjects</code> contains dummy nodes that are
 * part of an edge spanning over more than two levels. Those objects are called
 * <i>innerSegments</i>.
 * 
 * @author Ferdinand Huebner
 */
public class CrossMinObject implements Comparable<CrossMinObject> {
    /** flag to classify this object as an inner segment. */
    private boolean isInnerSegment;

    /**
     * LinkedList containing all inner nodes if the object is an inner segment
     */
    private LinkedList<Node> innerNodes;

    /**
     * Reference to the <code>Node</code> this object represents if the object
     * is not an inner segment.
     */
    private Node node;

    /**
     * The level the <code>Node</code> is on. This parameter is meaningless, if
     * the object is an inner segment.
     */
    private int level;

    /**
     * If the object is an inner segment, this parameter is the level of the
     * last node in the chain of dummy nodes. Please note that this level might
     * be less than the level of the first dummy node in the chain if a cyclic
     * layout of the graph is being used.
     */
    private int maxLevel;

    /**
     * If the object is an inner segment, this parameter is the level of the
     * first node in the chain of dummy nodes. Please note that this level might
     * be greater than the level of the last dummy node in the chain if a cyclic
     * layout of the graph is being used.
     */
    private int minLevel;

    /** The relative x coordinate of this object */
    protected int xPos;

    /** unique id of this object */
    protected int id;

    /** nodes of this object */
    private LinkedList<Node> theNode;

    /** list containing the in-neighbors of this object */
    protected LazyCrossMinObjectArrayList inNeighbors;

    /** list containing the out-neighbors of this object */
    protected LazyCrossMinObjectArrayList outNeighbors;

    protected LazyCrossMinObjectArrayList inNeighborsOld;
    protected LazyCrossMinObjectArrayList outNeighborsOld;

    /** position of this object in the adjacency list of its in-neighbors */
    protected LazyIntArrayList inNeighborPositions;

    /** position of this object in the adjacency list of its out-neighbors */
    protected LazyIntArrayList outNeighborPositions;

    /** the barycenter value of this object */
    protected double barycenter;

    protected CrossMinObject next;
    protected CrossMinObject previous;

    /**
     * Default constructor for a <code>CrossMinObject</code>.
     * 
     * @param xPos
     *            The relative x coordinate of this object
     * @param isInnerSegment
     *            Classifies this object as an inner segment or not
     */
    public CrossMinObject(int xPos, boolean isInnerSegment, int id) {
        this.xPos = xPos;
        this.isInnerSegment = isInnerSegment;
        this.id = id;
    }

    /**
     * Accessor to access the <code>Node</code> represented by this object.
     * 
     * @return Returns <code>null</code> if this object is an inner segment, the
     *         <code>Node</code> represented by this object otherwise.
     */
    public Node getNode() {
        if (isInnerSegment)
            return null;
        else
            return this.node;

    }

    /**
     * Stores the <code>Node</code> represented by this object, if the object is
     * an inner segment.<br />
     * Additionally, the Sugiyama attribute <i>xpos</i> of the <code>Node</code>
     * will get changed to the <i>xPos</i> of this <code>CrossMinObject</code>.
     * 
     * @param n
     *            The <code>Node</code> to reference in this object
     * @return Returns <code>false</code> if this object is an inner segment. In
     *         that case, <code>Node n</code> will not be stored in this object.
     *         Returns <code>true</code> otherwise.
     */
    public boolean setNode(Node n) {
        if (isInnerSegment)
            return false;
        else {
            this.node = n;
        }

        theNode = new LinkedList<Node>();
        theNode.add(n);
        level = n.getInteger(SugiyamaConstants.PATH_LEVEL);
        // n.setDouble(SugiyamaConstants.PATH_XPOS, this.xPos);

        inNeighbors = new LazyCrossMinObjectArrayList(n.getInDegree());
        outNeighbors = new LazyCrossMinObjectArrayList(n.getOutDegree());
        inNeighborPositions = new LazyIntArrayList(n.getInDegree());
        outNeighborPositions = new LazyIntArrayList(n.getOutDegree());

        return true;
    }

    /**
     * Stores the <code>LinkedList</code> of <code>Nodes</code> represented by
     * this inner segment.<br />
     * Additionally, the Sugiyama attribute <i>xpos</i> of all
     * <code>Nodes</code> in this inner segment will get changed to the
     * <i>xPos</i> of this <code>CrossMinObject</code>.
     * 
     * @param nodes
     *            <code>LinkedList</code> of <code>Nodes</code> representing
     *            this inner segment.
     * @return Returns <code>false</code> if this object is not an inner
     *         segment. In that case, it makes no sense to store a
     *         <code>LinkedList</code> of <code>Nodes</code>. Returns
     *         <code>true</code> otherwise.
     */
    public boolean setInnerNodes(LinkedList<Node> nodes) {
        if (!isInnerSegment)
            return false;
        else {
            this.innerNodes = nodes;
        }

        minLevel = nodes.getFirst().getInteger(SugiyamaConstants.PATH_LEVEL);
        maxLevel = nodes.getLast().getInteger(SugiyamaConstants.PATH_LEVEL);

        // for (Node n: nodes)
        // n.setDouble(SugiyamaConstants.PATH_XPOS, xPos);

        inNeighbors = new LazyCrossMinObjectArrayList(innerNodes.getFirst()
                .getInDegree());
        inNeighborPositions = new LazyIntArrayList(innerNodes.getFirst()
                .getInDegree());
        outNeighbors = new LazyCrossMinObjectArrayList(innerNodes.getLast()
                .getOutDegree());
        outNeighborPositions = new LazyIntArrayList(innerNodes.getLast()
                .getOutDegree());
        return true;
    }

    public LinkedList<Node> getInnerNodes() {
        if (!isInnerSegment)
            return null;
        else
            return this.innerNodes;
    }

    /**
     * Modifies the <i>xPos</i> of this <code>CrossMinObject</code>. If it is an
     * inner segment, the <i>xPos</i> of all inner nodes will get changed.
     * 
     * @param pos
     *            New <i>xPos</i> of this object.
     */
    public void setXPos(int pos) {
        this.xPos = pos;
    }

    /**
     * Accessor for the attribute <i>xPos</i>.
     * 
     * @return Returns the <i>xPos</i> of this object.
     */
    public int getXPos() {
        return xPos;
    }

    /**
     * Returns the level this object is on.<br />
     * <b>WARNING:</b> If this object is an inner segment, this method will
     * return -1. Use <code>getMinLevel</code> or <code>getMaxLevel</code> to
     * access the minimal or maximal level of the participating
     * <code>Nodes</code>.
     * 
     * @return Returns the level this object is on or <code>-1</code> if this
     *         object is an inner segment.
     */
    public int getLevel() {
        if (isInnerSegment)
            return -1;
        else
            return level;
    }

    /**
     * Returns the level on which the lowest <code>Node</code> of this inner
     * segment is on.
     * 
     * @return The level on which the lowest <code>Node</code> is on or
     *         <code>-1</code> if this object is not an inner segment.
     */
    public int getMinLevel() {
        if (!isInnerSegment)
            return level;
        else
            return minLevel;
    }

    /**
     * Returns the level on which the highest <code>Node</code> of this inner
     * segment is on.
     * 
     * @return The level on which the highest <code>Node</code> is on or
     *         <code>-1</code> if this object is not an inner segment.
     */
    public int getMaxLevel() {
        if (!isInnerSegment)
            return level;
        else
            return maxLevel;
    }

    /**
     * Accessor to check if this object is an inner segment or not.
     * 
     * @return Returns <code>true</code> if this object is an inner segment,
     *         <code>false</code> otherwise.
     */
    public boolean isInnerSegment() {
        return isInnerSegment;
    }

    /**
     * Accessor to check if this object is a <code>Node</code> or not.
     * 
     * @return Returns <code>true</code> if this object is a <code>Node</code>,
     *         <code>false</code> if it is an inner segment.
     */
    public boolean isNode() {
        return !isInnerSegment;
    }

    /**
     * Returns a list of the nodes representing this object
     */
    public LinkedList<Node> getNodes() {
        if (isInnerSegment)
            return innerNodes;
        else
            return theNode;
    }

    /**
     * <code>CrossMinObject</code> are comparable by their <i>xPos</i>. This
     * method returns a negative integer if the <i>xPos</i> of this object is
     * smaller than the <i>xPos</i> of the <code>CrossMinObject o</code>, zero
     * if both objects have the same <i>xPos</i>, a positive integer otherwise.
     * 
     * @return Returns a negative integer, zero, or a positive integer if this
     *         object's <i>xPos</i> is less than, equal to or greater than the
     *         <i>xPos</i> of the <code>CrossMinObject o</code>.
     */
    public int compareTo(CrossMinObject o) {
        return this.xPos - o.getXPos();
    }

}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
