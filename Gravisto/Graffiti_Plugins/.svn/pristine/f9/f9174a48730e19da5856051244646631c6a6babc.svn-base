package org.graffiti.plugins.algorithms.kandinsky;

import org.graffiti.graph.Edge;

/**
 * The arc of the MCMF-Network.
 */
class MCMFArc {

    /**
     * The id for the new label attributes on edges which arrise as result of
     * this algorithm
     */
    private String label = "";

    /** Capacity of the edge. */
    private int capacity;

    /** Cost for one unit of flow. */
    private int cost;

    /** Adjusted cost for one unit flow. */
    private int reducedCost;

    /** Flow of edge. */
    private int flow;

    /** The node where the edge starts. */
    private MCMFNode from;

    /** The node where the edge ends. */
    private MCMFNode to;

    /** The related Edge. */
    private Edge edge = null;

    /** Arc in the opposite direction */
    private MCMFArc restArc = null;

    /**
     * Constructor for an arc of the MCMF-network.
     * 
     * @param label
     *            The label of the arc.
     * @param start
     *            The starting point of the edge, which is a face.
     * @param end
     *            The target point of the edge, which is a face.
     * @param cap
     *            The capacity of the arc.
     * @param cost
     *            The cost for sending one unit through the arc.
     */
    public MCMFArc(String label, MCMFNode start, MCMFNode end, int cap, int cost) {
        this.capacity = 0;
        this.label = label;
        this.from = start;
        this.to = end;
        this.flow = 0;
        this.capacity = cap;
        this.cost = cost;
        this.reducedCost = cost;
    }

    /**
     * Returns the label of the arc.
     * 
     * @return The label of the arc.
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of the arc.
     * 
     * @param label
     *            The label to set.
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the capacity.
     * 
     * @return The capacity of the arc.
     */
    public int getCap() {
        return capacity;
    }

    /**
     * Sets the capacity.
     * 
     * @param capacity
     *            The capacity to set.
     */
    public void setCap(int capacity) {
        this.capacity = capacity;
    }

    /**
     * Returns the starting node of the edge.
     * 
     * @return the from as MCMFNode.
     */
    public MCMFNode getFrom() {
        return from;
    }

    /**
     * Returns the target node of the edge.
     * 
     * @return The target node of the edge.
     */
    public MCMFNode getTo() {
        return to;
    }

    /**
     * Returns the units of flow which are sent via this arc.
     * 
     * @return The units of flow.
     */
    public int getFlow() {
        return flow;
    }

    /**
     * Sents some units of flow via this arc.
     * 
     * @param flow
     *            The flow running through the edge.
     */
    public void setFlow(int flow) {
        this.flow = flow;
    }

    /**
     * Returns the cost for using this arc.
     * 
     * @return the cost.
     */
    public int getCost() {
        return cost;
    }

    /**
     * Sets the cost and the reduced costs.
     * 
     * @param cost
     *            the cost and the reduced costs to set.
     */
    public void setCost(int cost) {
        this.cost = cost;
        this.reducedCost = cost;
    }

    /**
     * Returns the adjusted costs for one unit of flow.
     * 
     * @return the reducedCost.
     */
    public int getReducedCost() {
        return reducedCost;
    }

    /**
     * Sets the adjusted costs for one unit of flow.
     * 
     * @param reducedCost
     *            the reducedCost to set.
     */
    public void setReducedCost(int reducedCost) {
        this.reducedCost = reducedCost;
    }

    /**
     * Returns the edge.
     * 
     * @return the edge.
     */
    public Edge getEdge() {
        return edge;
    }

    /**
     * Sets the edge.
     * 
     * @param edge
     *            the edge to set.
     */
    public void setEdge(Edge edge) {
        this.edge = edge;
    }

    /**
     * Prints the data of this arc.
     */
    @Override
    public String toString() {
        String data = "(" + label + ", Kap. " + capacity + ", Flu� " + flow
                + ")";
        return data;
    }

    /**
     * Returns the corresponding arc of the residual network.
     * 
     * @return the <codeMCMFArc</code> of the residual network.
     */
    public MCMFArc getRestArc() {
        return restArc;
    }

    /**
     * Sets the corresponding arc of the residual network which undoes the flow
     * on this arc.
     * 
     * @param restArc
     *            the restArc to set.
     */
    public void setRestArc(MCMFArc restArc) {
        this.restArc = restArc;
    }
}
