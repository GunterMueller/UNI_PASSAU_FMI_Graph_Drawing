package org.graffiti.plugins.algorithms.kandinsky;

import org.graffiti.graph.Edge;

/**
 * @author Sonja
 * @version $Revision$ $Date$
 * 
 *          Defines a constraint for the value of an angle.
 */
public class AngleConstraint {
    /*
     * Format: Knoten n, adjazente Kante 1, adjazente Kante 2, Zielwinkel,
     * Kosten [2; (1, 2); (0, 2); 90; 3]
     */

    /** MCMFNode where the angle is prescribed. */
    private GraphNode n;

    /** The face where the angle at n is prescribed */
    private FaceNode face;

    /** The target value of the angle. */
    private int angle;

    /** Costs for derivations of the target value of the angle. */
    private int cost;

    /** The <code>Edge</code>, which angle is affected by the Constraint. */
    private Edge edge;

    /**
     * Defines a constraint for the value of an angle.
     * 
     * @param n
     *            MCMFNode, where the angle is prescribed.
     * @param face
     *            The face where the angle should be changed.
     * @param edge
     *            The edge, which is affected by the constraint.
     * @param target
     *            The target value of the angle.
     * @param cost
     *            Costs for derivations of the target value of the angle.
     */
    AngleConstraint(GraphNode n, FaceNode face, Edge edge, int target, int cost) {
        this.n = n;
        this.face = face;
        this.angle = target;
        this.cost = cost;
        this.edge = edge;
    }

    /**
     * Gets the node, where the angle is to be changed.
     * 
     * @return MCMFNode
     */
    public GraphNode getAngleNode() {
        return n;
    }

    /**
     * Gets the face, where the angle is to be changed.
     * 
     * @return FaceNode
     */
    public FaceNode getFace() {
        return face;
    }

    /**
     * Gets the cost for not changing the angle.
     * 
     * @return int the cost.
     */
    public int getCost() {
        return cost;
    }

    /**
     * Gets the desired target angle.
     * 
     * @return int the angle
     */
    public int getAngle() {
        return angle;
    }

    /**
     * Gets the edge which angle (to the next edge) is affected by the
     * constraint.
     * 
     * @return Edge the edge, which is affected
     */
    public Edge getEdge() {
        // gibt die Kante zur�ck, deren Winkel zum Nachfolger sich durch den
        // Constraint �ndert.
        return edge;
    }
}
