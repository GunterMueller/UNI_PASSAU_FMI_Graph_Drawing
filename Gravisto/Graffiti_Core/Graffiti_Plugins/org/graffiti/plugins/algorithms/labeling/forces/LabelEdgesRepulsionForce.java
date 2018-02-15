// =============================================================================
//
//   RepulsiveNodeEdgeForce.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RepulsiveNodeEdgeForce.java 1289 2006-06-12 05:52:18Z matzeder $

package org.graffiti.plugins.algorithms.labeling.forces;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.graffiti.plugins.algorithms.labeling.FREdgeLabelNode;
import org.graffiti.plugins.algorithms.labeling.FRNodeLabelNode;
import org.graffiti.plugins.algorithms.labeling.SELabelingAlgorithmParameters;
import org.graffiti.plugins.algorithms.springembedderFR.FREdge;
import org.graffiti.plugins.algorithms.springembedderFR.FRNode;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

/**
 * This version repulses only label nodes from <i>not-own</i> edges.
 * <p>
 * Considered as <i>own</i> are the following edges:
 * <ul>
 * <li>for <tt>EdgeLabelNode</tt>s: the corresponding edge
 * <li>for <tt>NodeLabelNode</tt>s: all edges of the corresponding node
 * </ul>
 * There exist separate forces for <i>own</i> edges. They can be parameterized
 * independently.
 * <p>
 * TODO: implement
 * 
 * @author scholz
 * @see RepulsiveNodeEdgeForce
 */
public class LabelEdgesRepulsionForce extends AbstractSEForce {

    /**
     * Creates a force for node-edge repulsion.
     * 
     * @param p
     *            Parameters of the algorithms.
     */
    public LabelEdgesRepulsionForce(SELabelingAlgorithmParameters p) {
        super(p);
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.springembedderFR.AbstractSEForces#
     * calculateForce() <p>...this is a green comment to not overwrite the
     * beautiful generic JavaDoc ;) *ironic* <p>In fact, this routine Runs
     * through the node-edge pairs of the graph. And forces of the pairs are
     * updated respectively. Own edges are excluded here, while other
     * restrictions still apply at the subordinate routine.
     * 
     * @see RepulsiveNodeNodeForce
     */
    @Override
    public void calculateForce() {
        // node label nodes: O(#nodeLabelNodes * (#edges + 2*#edges)) = O(n�)
        List<FREdge> ownEdges;
        for (FRNodeLabelNode node : parameters.fRGraph.getNodeLabelNodes()) {
            ownEdges = parameters.fRGraph.getEmergingEdges(node
                    .getCorrespondingFRNode());
            for (FREdge edge : parameters.fRGraph.getNotLabelEdges()) {
                // don't collide with own edges
                if (ownEdges.contains(edge)) {
                    continue;
                }
                calcNodeEdgeRepulsiveForces(node, edge);
            }
        }
        // edge label nodes: O (#edgeLabelNodes * #edges) = O(n�)
        for (FREdgeLabelNode node : parameters.fRGraph.getEdgeLabelNodes()) {
            for (FREdge edge : parameters.fRGraph.getFREdges()) {
                // don't collide with own edge
                if (edge != node.getCorrespondingFREdge()) {
                    calcNodeEdgeRepulsiveForces(node, edge);
                }
            }
        }
    }

    /**
     * Runs through the node-edge pairs of the graph. And the forces of the
     * pairs are updated respectively.
     * 
     */
    protected void calcNodeEdgeRepulsiveForces(FRNode node, FREdge edge) {
        // don't move source or target nodes of edges
        if (edge.getSource() != node && edge.getTarget() != node
                && edge.getSource() != edge.getTarget()) {
            // don't start calculation if neither is movable
            if (node.isMovable() || edge.getSource().isMovable()
                    || edge.getTarget().isMovable()) {
                GeometricalVector repForce;
                // variant with node sizes
                if (parameters.isCalculationWithNodeSizes) {
                    repForce = calcRepForceNodeEdgeWithNodeSizes(node, edge);
                } else {
                    // calculates the GeometricalVector between edge and
                    // node (without node sizes)
                    repForce = calcRepForceNodeEdge(node, edge);

                }
                if (node.isMovable()) {
                    // existing node-edge force at node
                    GeometricalVector nodeForceVector = node
                            .getForce(LABEL_NODE_EDGE_REPULSION_FORCE);
                    // add the repForce GeometricalVector to the
                    // GeometricalVector of node
                    node.setForces(LABEL_NODE_EDGE_REPULSION_FORCE,
                            GeometricalVector.add(repForce, nodeForceVector));
                }
                if (edge.getSource().isMovable()) {
                    FRNode source = edge.getSource();
                    GeometricalVector sourceForceVector = source
                            .getForce(LABEL_NODE_EDGE_REPULSION_FORCE);
                    // subtract the repForce GeometricalVector of the
                    // GeometricalVector of
                    // source
                    source
                            .setForces(LABEL_NODE_EDGE_REPULSION_FORCE,
                                    GeometricalVector.subt(sourceForceVector,
                                            repForce));
                }
                if (edge.getTarget().isMovable()) {
                    FRNode target = edge.getTarget();
                    GeometricalVector targetForceVector = target
                            .getForce(LABEL_NODE_EDGE_REPULSION_FORCE);
                    // subtract the repForce GeometricalVector of the
                    // GeometricalVector of
                    // target
                    target
                            .setForces(LABEL_NODE_EDGE_REPULSION_FORCE,
                                    GeometricalVector.subt(targetForceVector,
                                            repForce));
                }
            }
        }
    }

    /**
     * Calculates the force vector of the node-edge repulsive force,
     * incorperating node sizes.
     * 
     * @param node
     *            The given node.
     * @param edge
     *            The given edge
     * @return The force vector of the node-edge repulsive force.
     */
    protected GeometricalVector calcRepForceNodeEdgeWithNodeSizes(FRNode node,
            FREdge edge) {

        GeometricalVector sourceVector = new GeometricalVector(edge.getSource());
        GeometricalVector targetVector = new GeometricalVector(edge.getTarget());
        GeometricalVector nodeVector = new GeometricalVector(node);

        // node intersects edge
        if (nodeIntersectsEdge(node, edge)) {
            GeometricalVector i_node = getOrthogonalIntersectionPoint(
                    sourceVector, targetVector, nodeVector);

            // node over edge
            return calcRepNodeEdgeForceVector(edge, i_node, nodeVector,
                    REPULSIVE_INTERSECTION_STRENGTH_CONSTANT);
        }

        // node does not intersect edge, but can also be located onto the
        // straight line of edge
        else {
            // the corners of the node
            GeometricalVector nodeVectorLeftDown = new GeometricalVector(node
                    .getXPos()
                    - node.getWidth() / 2, node.getYPos() + node.getHeight()
                    / 2);

            GeometricalVector nodeVectorLeftUp = new GeometricalVector(node
                    .getXPos()
                    - node.getWidth() / 2, node.getYPos() - node.getHeight()
                    / 2);

            GeometricalVector nodeVectorRightUp = new GeometricalVector(node
                    .getXPos()
                    + node.getWidth() / 2, node.getYPos() - node.getHeight()
                    / 2);

            GeometricalVector nodeVectorRightDown = new GeometricalVector(node
                    .getXPos()
                    + node.getWidth() / 2, node.getYPos() + node.getHeight()
                    / 2);

            // the different orthogonal projections points of the corners
            GeometricalVector i_nodeLeftDown = getOrthogonalIntersectionPoint(
                    sourceVector, targetVector, nodeVectorLeftDown);

            GeometricalVector i_nodeLeftUp = getOrthogonalIntersectionPoint(
                    sourceVector, targetVector, nodeVectorLeftUp);

            GeometricalVector i_nodeRightDown = getOrthogonalIntersectionPoint(
                    sourceVector, targetVector, nodeVectorRightDown);

            GeometricalVector i_nodeRightUp = getOrthogonalIntersectionPoint(
                    sourceVector, targetVector, nodeVectorRightUp);

            // to save the i_nodes onto the edge
            HashMap<GeometricalVector, GeometricalVector> i_nodesOnEdge = new HashMap<GeometricalVector, GeometricalVector>();

            // add the i_nodes, which are located onto the edge
            if (GeometricalVector.isPointBetweenSourceAndTarget(i_nodeLeftDown,
                    sourceVector, targetVector)) {
                i_nodesOnEdge.put(nodeVectorLeftDown, i_nodeLeftDown);
            }
            if (GeometricalVector.isPointBetweenSourceAndTarget(i_nodeLeftUp,
                    sourceVector, targetVector)) {
                i_nodesOnEdge.put(nodeVectorLeftUp, i_nodeLeftUp);
            }
            if (GeometricalVector.isPointBetweenSourceAndTarget(
                    i_nodeRightDown, sourceVector, targetVector)) {
                i_nodesOnEdge.put(nodeVectorRightDown, i_nodeRightDown);
            }
            if (GeometricalVector.isPointBetweenSourceAndTarget(i_nodeRightUp,
                    sourceVector, targetVector)) {
                i_nodesOnEdge.put(nodeVectorRightUp, i_nodeRightUp);
            }

            // search the corner (where the i_node is onto the edge) with
            // minimal distance
            Set<GeometricalVector> nodeCorners = i_nodesOnEdge.keySet();

            // no i_node is located on edge
            if (nodeCorners.size() == 0)
                // the force is the zero vector
                return new GeometricalVector();
            else {
                double xDist = Double.MAX_VALUE;
                double yDist = Double.MAX_VALUE;

                GeometricalVector i_node = new GeometricalVector();
                GeometricalVector cornerOfNode = new GeometricalVector();

                GeometricalVector e = new GeometricalVector(edge.getSource(),
                        edge.getTarget());

                if (e.getY() == 0) {

                    // search the corner with the minimum distance to the edge
                    // (only these ones, where the i_node is located on the
                    // edge)
                    for (GeometricalVector corner : nodeCorners) {
                        GeometricalVector i_nodeCorner = i_nodesOnEdge
                                .get(corner);
                        // because all projections of the corners onto the edge
                        // are parallel only the x component can be used
                        double tempXDistance = Math.abs(corner.getX()
                                - i_nodeCorner.getX());

                        // GeometricalVector.getDistance(
                        // corner, i_nodesOnEdge.get(corner));

                        if (tempXDistance < xDist) {

                            xDist = tempXDistance;
                            cornerOfNode = corner;
                            i_node = i_nodeCorner;
                        }
                    }

                } else {
                    // search the corner with the minimum distance to the edge
                    // (only these ones, where the i_node is located on the
                    // edge)
                    for (GeometricalVector corner : nodeCorners) {

                        GeometricalVector i_nodeCorner = i_nodesOnEdge
                                .get(corner);
                        // because all projections of the corners onto the edge
                        // ar
                        // parallel only the y component can be used
                        double tempYDistance = Math.abs(corner.getX()
                                - i_nodeCorner.getX());

                        if (tempYDistance < yDist) {

                            yDist = tempYDistance;
                            cornerOfNode = corner;
                            i_node = i_nodeCorner;
                        }
                    }
                }

                double distanceI_nodeToNodeVector = GeometricalVector
                        .getDistance(cornerOfNode, i_node);

                return calcRepNodeEdgeForceVector(edge, i_node, cornerOfNode,
                        distanceI_nodeToNodeVector);
            }
        }
    }

    /**
     * Calculates the node-edge force vector with the formula of Bertault.
     * 
     * @param edge
     * @param i_node
     * @param nodeVector
     * @param distanceI_nodeToNodeVector
     * @return force vector of node-edge repulsive force
     */
    protected GeometricalVector calcRepNodeEdgeForceVector(FREdge edge,
            GeometricalVector i_node, GeometricalVector nodeVector,
            double distanceI_nodeToNodeVector) {
        GeometricalVector sourceVector = new GeometricalVector(edge.getSource());
        GeometricalVector targetVector = new GeometricalVector(edge.getTarget());

        // length of edge
        double delta = edge.getLength();

        // this choice is recommended by Bertault
        double gamma = 4 * delta;

        // true, if i_node is located onto the edge
        boolean i_nodeIsOnEdge = GeometricalVector
                .isPointBetweenSourceAndTarget(i_node, sourceVector,
                        targetVector);

        // i_node is located on edge
        if (i_nodeIsOnEdge) {
            // vector from i_node to nodeVector
            GeometricalVector diff = GeometricalVector.subtract(i_node,
                    nodeVector);

            // distance between node and i_node is smaller than gamma
            if (distanceI_nodeToNodeVector < gamma) {
                double factor1 = (-Math.pow(
                        (gamma - distanceI_nodeToNodeVector), 2))
                        / (distanceI_nodeToNodeVector * distanceI_nodeToNodeVector);

                GeometricalVector vec = GeometricalVector.mult(diff, factor1);

                return new GeometricalVector(vec.getX()
                        * parameters.repulsionConstantLabelNodeEdge, vec.getY()
                        * parameters.repulsionConstantLabelNodeEdge);
            }
        }
        // zero force vector is returned
        return new GeometricalVector();
    }

    /**
     * Method tests if a (big) node intersects an edge. True is returned, if
     * node intersects. Else false.
     * 
     * @param node
     *            The given node.
     * @param edge
     *            The given edge
     * @return True, if node intersects, else false.
     */
    protected boolean nodeIntersectsEdge(FRNode node, FREdge edge) {

        // Vectors of the position of node, source and target of edge
        GeometricalVector sourceVector = new GeometricalVector(edge.getSource());
        GeometricalVector targetVector = new GeometricalVector(edge.getTarget());

        // the corners of node
        GeometricalVector nodeVectorLeftDown = new GeometricalVector(node
                .getXPos()
                - node.getWidth() / 2, node.getYPos() + node.getHeight() / 2);
        GeometricalVector nodeVectorLeftUp = new GeometricalVector(node
                .getXPos()
                - node.getWidth() / 2, node.getYPos() - node.getHeight() / 2);
        GeometricalVector nodeVectorRightUp = new GeometricalVector(node
                .getXPos()
                + node.getWidth() / 2, node.getYPos() - node.getHeight() / 2);
        GeometricalVector nodeVectorRightDown = new GeometricalVector(node
                .getXPos()
                + node.getWidth() / 2, node.getYPos() + node.getHeight() / 2);

        // to know the corner positions of the node dependent on edge
        int position1 = rightOf(sourceVector, targetVector, nodeVectorLeftDown);
        int position2 = rightOf(sourceVector, targetVector, nodeVectorLeftUp);
        int position3 = rightOf(sourceVector, targetVector, nodeVectorRightUp);
        int position4 = rightOf(sourceVector, targetVector, nodeVectorRightDown);

        // all corners have the same position ==> node does not intersect the
        // (straight) line ==> not intersecting edge
        if (position1 != 0 && position1 == position2 && position2 == position3
                && position3 == position4)
            return false;

        // to know the rectanlge the edge includes
        double leftOfEdge = Math.min(sourceVector.getX(), targetVector.getX());
        double rightOfEdge = Math.max(sourceVector.getX(), targetVector.getX());
        double topOfEdge = Math.min(sourceVector.getY(), targetVector.getY());
        double bottomOfEdge = Math
                .max(sourceVector.getY(), targetVector.getY());

        // borders of the node
        double rightOfNode = nodeVectorRightUp.getX();
        double leftOfNode = nodeVectorLeftUp.getX();
        double topOfNode = nodeVectorLeftUp.getY();
        double bottomOfNode = nodeVectorRightDown.getY();

        // true, if the node intersects the produced rectangle of edge
        // ==> there exist corners left and right of edge ==> intersects edge
        boolean isIntersecting = Math.max(leftOfNode, leftOfEdge) <= Math.min(
                rightOfNode, rightOfEdge)
                && Math.min(bottomOfNode, bottomOfEdge) >= Math.max(topOfNode,
                        topOfEdge);
        return isIntersecting;
    }

    /**
     * Calculates the repulsive force between node and edge.
     * 
     * @param node
     *            The given node.
     * @param edge
     *            The given edge
     * @return GeometricalVector, which is the repulsive force
     */
    protected GeometricalVector calcRepForceNodeEdge(FRNode node, FREdge edge) {

        // Vectors of the position of node, source and target of edge
        GeometricalVector nodeVector = new GeometricalVector(node.getXPos(),
                node.getYPos());

        // vector of source
        GeometricalVector sourceVector = new GeometricalVector(edge.getSource()
                .getXPos(), edge.getSource().getYPos());

        // vector of target
        GeometricalVector targetVector = new GeometricalVector(edge.getTarget()
                .getXPos(), edge.getTarget().getYPos());

        // point onto the edge, which is the orthogonal projection of node onto
        // edge
        GeometricalVector i_node = getOrthogonalIntersectionPoint(sourceVector,
                targetVector, nodeVector);

        // distance node and i_node
        double distanceI_nodeToNodeVector = GeometricalVector.getDistance(
                i_node, nodeVector);

        return calcRepNodeEdgeForceVector(edge, i_node, nodeVector,
                distanceI_nodeToNodeVector);
    }

    /**
     * If r is onto the line (p, q) then method returns 0. Returns -1, if r is
     * right of (p, q), 1 if r is left of (p, q) (comparable "Algorithmische
     * Geometrie" -> liegtRechtsVon)
     * 
     * @param p
     *            First point to specify the line
     * @param q
     *            Second point to specify the line
     * @param r
     *            Point to test the position
     * @return 0, if r is onto line (p, q). -1 if r is right of line (p, q), 1
     *         else.
     */
    protected int rightOf(GeometricalVector p, GeometricalVector q,
            GeometricalVector r) {

        double w = (r.getY() - p.getY()) * (q.getX() - p.getX())
                - (r.getX() - p.getX()) * (q.getY() - p.getY());

        if (w == 0.0)
            return 0;
        else if (w < 0)
            return 1;
        else
            return -1;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
