package org.graffiti.plugins.algorithms.labeling.forces;

import org.graffiti.plugins.algorithms.labeling.FREdgeLabelNode;
import org.graffiti.plugins.algorithms.labeling.SELabelingAlgorithmParameters;
import org.graffiti.plugins.algorithms.springembedderFR.FREdge;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

/**
 * A force between edge labels and their corresponding edge to avoid that
 * letters overlap the edge.
 * <p>
 * This force is normally smaller than an ordinary node-edge repulsion force.
 * <p>
 * Other than most forces, this one has a maximum strength. This way, the label
 * is able to cross its corresponding edge.
 */
public class EdgeLabelRepulsionForce extends AbstractSEForce {

    public EdgeLabelRepulsionForce(SELabelingAlgorithmParameters p) {
        super(p);
    }

    @Override
    public void calculateForce() {
        GeometricalVector forceVector;
        for (FREdgeLabelNode edgeLabel : this.parameters.fRGraph
                .getEdgeLabelNodes()) {
            // calculate force vector pushing overlapping from edge
            forceVector = calculateForce(edgeLabel, edgeLabel
                    .getCorrespondingFREdge());
            // set force vector (overwrite: there is only one)
            edgeLabel.setForces(EDGE_LABEL_NODE_REPULSION_FORCE, forceVector);
        }
    }

    /**
     * for full force, distance is zero
     */
    protected GeometricalVector calculateForce(FREdgeLabelNode edgeLabel,
            FREdge edge) {

        // calculate distance between label border and edge
        double centerDistance = calculateDistanceEdgePoint(
                new GeometricalVector(edgeLabel.getXPos(), edgeLabel.getYPos()),
                edge);

        GeometricalVector lowerRight = new GeometricalVector(edgeLabel
                .getXPos()
                + edgeLabel.getWidth() * 0.5d, edgeLabel.getYPos()
                + edgeLabel.getHeight() * 0.5d);
        GeometricalVector lowerLeft = new GeometricalVector(edgeLabel.getXPos()
                - edgeLabel.getWidth() * 0.5d, edgeLabel.getYPos()
                + edgeLabel.getHeight() * 0.5d);
        GeometricalVector upperLeft = new GeometricalVector(edgeLabel.getXPos()
                - edgeLabel.getWidth() * 0.5d, edgeLabel.getYPos()
                - edgeLabel.getHeight() * 0.5d);
        GeometricalVector upperRight = new GeometricalVector(edgeLabel
                .getXPos()
                + edgeLabel.getWidth() * 0.5d, edgeLabel.getYPos()
                - edgeLabel.getHeight() * 0.5d);

        double closestDistance;
        if (centerDistance >= 0) {
            closestDistance = min4(
                    calculateDistanceEdgePoint(lowerRight, edge),
                    calculateDistanceEdgePoint(lowerLeft, edge),
                    calculateDistanceEdgePoint(upperLeft, edge),
                    calculateDistanceEdgePoint(upperRight, edge));
        } else {
            closestDistance = max4(
                    calculateDistanceEdgePoint(lowerRight, edge),
                    calculateDistanceEdgePoint(lowerLeft, edge),
                    calculateDistanceEdgePoint(upperLeft, edge),
                    calculateDistanceEdgePoint(upperRight, edge));
        }

        // System.out.println("Label '"
        // + edgeLabel.getLabel() + "': center distance "
        // + centerDistance + ", closest distance "
        // + closestDistance);

        // calculate force vector
        double halfDiagonal = 0.5d * Math.hypot(edgeLabel.getWidth(), edgeLabel
                .getHeight());
        // System.out.println("max center distance: " +
        // halfDiagonal + parameters.edgeLabelForceSoftOuterBorder);

        if (Math.abs(centerDistance) <= halfDiagonal
                + parameters.edgeLabelForceSoftOuterBorder) {

            double force;
            if (centerDistance > 0) {
                force = parameters.edgeLabelForceRepulsionMax
                        * (1 - (Math.max(closestDistance
                                + parameters.edgeLabelForceSoftInnerBorder, 0d))
                                / (parameters.edgeLabelForceSoftOuterBorder + parameters.edgeLabelForceSoftInnerBorder));
            } else {
                force = -parameters.edgeLabelForceRepulsionMax
                        * (1 - (Math.max(closestDistance
                                + parameters.edgeLabelForceSoftInnerBorder, 0d))
                                / (parameters.edgeLabelForceSoftOuterBorder + parameters.edgeLabelForceSoftInnerBorder));
            }

            // System.out.println("edge label force: " +
            // force + " ("
            // + (force / parameters.edgeLabelForceRepulsionMax) + " of max)");

            GeometricalVector edgeDirection = GeometricalVector.subt(
                    edge.getTarget().getPosition(),
                    edge.getSource().getPosition()).getUnitVector();

            // force vector is perpendicular to edge vector (clockwise 90�)
            GeometricalVector forceVector = new GeometricalVector(
                    (-edgeDirection.getY()) * force, edgeDirection.getX()
                            * force);

            // System.out.println("edge label force: "
            // + edgeLabel.getLabel() + " "
            // + forceVector);
            return forceVector;
        } else
            return new GeometricalVector();

    }

    private double min4(double a, double b, double c, double d) {
        double min = a;
        if (b < min) {
            min = b;
        }
        if (c < min) {
            min = c;
        }
        if (d < min) {
            min = d;
        }
        return min;
    }

    private double max4(double a, double b, double c, double d) {
        double max = a;
        if (b > max) {
            max = b;
        }
        if (c > max) {
            max = c;
        }
        if (d > max) {
            max = d;
        }
        return max;
    }

}
