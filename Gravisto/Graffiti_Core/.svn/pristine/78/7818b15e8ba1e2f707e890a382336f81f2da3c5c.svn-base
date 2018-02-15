package org.graffiti.plugins.algorithms.kandinsky;

import java.util.LinkedList;

import org.graffiti.graph.Edge;

/**
 * @author Sonja
 * @version $Revision$ $Date$
 * 
 *          Defines a constraint for a bend of an edge of the network.
 */
public class BendConstraint {

    /*
     * Format: (Startknoten, Zielknoten), Richtung des Knicks, Kosten f�rs
     * Ignorieren des Knicks] [(3, 5); 270; 4]
     */

    /** GraphNode where edge starts. */
    private GraphNode n1;

    /** GraphNode where edge ends. */
    private GraphNode n2;

    /** The list stores the direction of each turn of the bend. */
    private LinkedList<Boolean> bends;

    /** Costs for derivations of the demanded turn. */
    private int cost;

    /** The edge where the bends are constructed. */
    private Edge edge;

    /**
     * Defines a constraint for a bend of an edge of the network. The direction
     * of the bend constraint is n1-->n2.
     * 
     * @param n1
     *            GraphNode where edge starts.
     * @param n2
     *            GraphNode where edge ends.
     * @param edge
     *            The Edge which should get the bends.
     * @param bends
     *            The direction of the turn of the bend. TRUE: right turn;
     *            FALSE: left turn.
     * @param cost
     *            Costs for derivations of the demanded turn.
     */
    BendConstraint(GraphNode n1, GraphNode n2, Edge edge,
            LinkedList<Boolean> bends, int cost) {
        this.n1 = n1;
        this.n2 = n2;
        this.bends = bends;
        this.cost = cost;
        this.edge = edge;
    }

    /**
     * Gets the start node of the arc, where the bend should be constructed.
     * 
     * @return MCMFNode start node.
     */
    public GraphNode getArcNode1() {
        return n1;
    }

    /**
     * Gets the target node of the arc, where the bend should be constructed.
     * 
     * @return MCMFNode end node.
     */
    public GraphNode getArcNode2() {
        return n2;
    }

    /**
     * Gets the cost for not constructing the bend.
     * 
     * @return int the cost charged for not changing.
     */
    public int getCost() {
        return cost;
    }

    /**
     * Gets the list of desired bends. TRUE: right turn; FALSE: left turn.
     * 
     * @return LinkedList<Boolean> the bends.
     */
    public LinkedList<Boolean> getBends() {
        return bends;
    }

    /**
     * Gets the edge for which the bends are constructed.
     * 
     * @return edge The Edge.
     */
    public Edge getEdge() {
        return edge;
    }
}
