package org.graffiti.plugins.algorithms.labeling.forces;

import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.graffiti.plugins.algorithms.labeling.Direction;
import org.graffiti.plugins.algorithms.labeling.FRNodeLabelNode;
import org.graffiti.plugins.algorithms.labeling.SELabelingAlgorithmParameters;
import org.graffiti.plugins.algorithms.springembedderFR.FREdge;
import org.graffiti.plugins.algorithms.springembedderFR.FRNode;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

/**
 * A force between a node label and the edges of its corresponding node to avoid
 * letters overlapping the edge.
 * <p>
 * The force's direction is tangential to a circle around the corresponding
 * node.
 * <p>
 * <i>Note</i>: The force is designed to try to jump over edges, if the space
 * available for the label is not sufficient. It will do this in a clockwise
 * manner.
 * <p>
 * <i>Known bugs</i>:
 * <ul>
 * <li>If the nearest edges in clockwise and anticlockwise direction share an
 * angle > 180, the force behaves badly (it does not treat edges as line
 * segments)
 * </ul>
 */
public class NodeLabelAdjacentEdgesRepulsionForce extends AbstractSEForce {

    public NodeLabelAdjacentEdgesRepulsionForce(SELabelingAlgorithmParameters p) {
        super(p);
    }

    @Override
    public void calculateForce() {
        GeometricalVector forceVector;
        for (FRNodeLabelNode labelNode : this.parameters.fRGraph
                .getNodeLabelNodes()) {

            // calculate force vector pushing overlapping from edge
            forceVector = calculateForce(labelNode);
            // set force vector (overwrite: there is only one)
            labelNode.setForces(NODE_LABEL_NODE_EDGES_REPULSION_FORCE,
                    forceVector);
        }
    }

    protected GeometricalVector calculateForce(FRNodeLabelNode labelNode) {

        FRNode parentNode = labelNode.getCorrespondingFRNode();
        List<FREdge> emergingEdges = this.parameters.fRGraph
                .getEmergingEdges(parentNode);

        // if there are no emerging edges, the resulting force is zero
        if (emergingEdges.size() == 0)
            return new GeometricalVector();

        // determine angle parentNode -> labelNode
        GeometricalVector parentToLabelVector = GeometricalVector.subtract(
                labelNode.getActualPosition(), parentNode.getActualPosition());
        Direction labelDir = new Direction(parentToLabelVector);

        if (parameters.isLabelingForcesVerboseMode) {
            System.out.println("Node label: " + labelNode.getActualPosition()
                    + "\n - parent: " + parentNode.getActualPosition()
                    + "\n - parentToLabel: " + parentToLabelVector);
        }

        // determine angles parentNode -> all edges
        // TODO: eventually change to SortedSet
        TreeMap<Direction, FREdge> emergingDirections = new TreeMap<Direction, FREdge>();
        GeometricalVector edgeVector; // edge vector from parentNode
        for (FREdge edge : emergingEdges) {
            if (edge.getSource() == parentNode) {
                edgeVector = GeometricalVector.subtract(edge.getTarget()
                        .getActualPosition(), edge.getSource()
                        .getActualPosition());
            } else {
                assert (edge.getTarget() == parentNode);
                edgeVector = GeometricalVector.subtract(edge.getSource()
                        .getActualPosition(), edge.getTarget()
                        .getActualPosition());
            }
            emergingDirections.put(new Direction(edgeVector), edge);
        }

        // find nearest two emerging edges (left and right)
        Entry<Direction, FREdge> nearestEdgeCounterClockwise;
        Entry<Direction, FREdge> nearestEdgeClockwise;

        nearestEdgeCounterClockwise = emergingDirections.floorEntry(labelDir);
        if (nearestEdgeCounterClockwise == null) {
            nearestEdgeCounterClockwise = emergingDirections.lastEntry();
        }

        nearestEdgeClockwise = emergingDirections.ceilingEntry(labelDir);
        if (nearestEdgeClockwise == null) {
            nearestEdgeClockwise = emergingDirections.firstEntry();
        }

        // Calculate circular distance from label center to left and right
        // angles
        // Note: This is a bit of a hack - angles are in degrees ]-180;180]
        double distanceLeft = Direction.clockwiseDistance(
                nearestEdgeCounterClockwise.getKey(), labelDir);
        double distanceRight = Direction.clockwiseDistance(labelDir,
                nearestEdgeClockwise.getKey());

        if (parameters.isLabelingForcesVerboseMode) {
            System.out.println("NodeLabelRep.: " + labelNode.getLabel() + "("
                    + labelDir + ") - leftEdge "
                    + nearestEdgeCounterClockwise.getKey() + " - rightEdge "
                    + nearestEdgeClockwise.getKey());
        }

        // determine label border distance to edges:
        // Note: a circular distance would be nice here, but is not implemented
        double effectiveDistanceLeft = this.calculateDistanceNodeEdge(
                labelNode, nearestEdgeCounterClockwise.getValue(),
                parameters.NodeLabelAdjacentEdgesRepulsionForceSoftInnerBorder);
        double effectiveDistanceRight = this.calculateDistanceNodeEdge(
                labelNode, nearestEdgeClockwise.getValue(),
                parameters.NodeLabelAdjacentEdgesRepulsionForceSoftInnerBorder);

        // construct force strength
        double force;
        // special case: edges have angle distance from label > 180�
        /*
         * If the nearest edges in clockwise and anticlockwise direction share
         * an angle > 90�, the original force calculation behaves badly (it does
         * not treat edges as line segments). The raise in badness is
         * remarkable, if the angle is > 180�, as the label won't cross that
         * border. If this is the case, the label is treated as point and only
         * angles determine the resulting force.
         */
        if (distanceLeft > 90 || distanceRight > 90) {
            // effective distance will not be computed appropriately
            force = (distanceRight - distanceLeft)
                    / (distanceRight + distanceLeft)
                    * parameters.NodeLabelAdjacentEdgesRepulsionMaxForce;
        } else if (effectiveDistanceLeft == 0d && effectiveDistanceRight == 0d) {
            // special case: not enough space for label
            // repulse clockwise, so that label might jump clockwise over
            // edges if there is not enough space
            // TODO: determine strength by distance to parent node,
            // so that, say, half a quadrant (45�) is jumped
            force = parameters.NodeLabelAdjacentEdgesRepulsionJumpForce;
        } else {
            // normal case:
            force = (0.5 - (effectiveDistanceLeft / (effectiveDistanceLeft + effectiveDistanceRight)))
                    * 2d * parameters.NodeLabelAdjacentEdgesRepulsionMaxForce;
        }

        if (parameters.isLabelingForcesVerboseMode) {
            System.out
                    .println("node label - emerging edges repulsion force: "
                            + force
                            + " ("
                            + ((int) (force * 100 / parameters.NodeLabelAdjacentEdgesRepulsionMaxForce))
                            + "%) " + "\n d�_left " + distanceLeft
                            + " - d�_right " + distanceRight + "\n d_left "
                            + effectiveDistanceLeft + " - d_right "
                            + effectiveDistanceRight);
        }

        // force vector is perpendicular to parent->labelnode vector (clockwise)
        GeometricalVector forceDirection = GeometricalVector
                .getClockwiseOrthogonalUnitVector(parentToLabelVector);

        if (parameters.isLabelingForcesVerboseMode) {
            System.out.println("orthogonal unit vector: " + parentToLabelVector
                    + "\n-> " + forceDirection);
        }

        // apply force strength
        GeometricalVector forceVector = GeometricalVector.mult(forceDirection,
                force);

        if (parameters.isLabelingForcesVerboseMode) {
            System.out.println("NLEE force: " + labelNode.getLabel() + " "
                    + forceVector);
        }
        return forceVector;
    }

}
