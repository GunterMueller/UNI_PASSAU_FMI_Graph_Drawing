// =============================================================================
//
//   AttractiveAdjacentNodesForce.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AttractiveAdjacentNodesForce.java 1287 2006-06-09 14:53:31Z matzeder $

package org.graffiti.plugins.algorithms.labeling.forces;

import org.graffiti.plugins.algorithms.labeling.FREdgeLabelNode;
import org.graffiti.plugins.algorithms.labeling.FRLabelNode;
import org.graffiti.plugins.algorithms.labeling.SELabelingAlgorithmParameters;
import org.graffiti.plugins.algorithms.springembedderFR.FREdge;
import org.graffiti.plugins.algorithms.springembedderFR.FRNode;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

/**
 * Class for calculation the attractive force between nodes.
 * 
 * @author matzeder
 * @version $Revision: 1287 $ $Date: 2006-06-09 16:53:31 +0200 (Fri, 09 Jun
 *          2006) $
 */
public class AttractiveAdjacentNodesForce extends AbstractSEForce {
    /**
     * Creates a new attractive force for adjacent nodes.
     * 
     * @param p
     *            Parameters of the algorithms.
     */
    public AttractiveAdjacentNodesForce(SELabelingAlgorithmParameters p) {
        super(p);
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.springembedderFR.AbstractSEForces#
     * calculateForce()
     */
    @Override
    public void calculateForce() {
        calcAttractiveForces();

    }

    /**
     * Calculates the attractive forces between all edges of the graph.
     * 
     */
    protected void calcAttractiveForces() {

        // all edges
        for (FREdge actEdge : parameters.fRGraph.getFREdges()) {

            // source and target
            FRNode source = actEdge.getSource();
            FRNode target = actEdge.getTarget();

            if (source.isMovable() || target.isMovable()) {

                GeometricalVector delta;
                // in quenching phase the size of the nodes is not considered
                if (parameters.phase == QUENCHING_PHASE) {
                    delta = new GeometricalVector(source, target);
                } else {

                    delta = calculationDistanceVector(source, target);
                }

                GeometricalVector newForceVector;
                // nodes intersect
                if (delta.getX() == 0.0 && delta.getY() == 0.0) {
                    newForceVector = new GeometricalVector();
                }
                // no interection
                else {
                    // calculates the new force vector
                    newForceVector = calculateAttractiveForceVector(source,
                            target, delta);
                }

                if (source.isMovable()) {
                    // the existing force vector of source
                    GeometricalVector existingForceVectorSource = source
                            .getForce(ATT_FORCE);
                    // adding new force vector to existing force vector of
                    // source
                    source.setForces(ATT_FORCE, GeometricalVector.add(
                            existingForceVectorSource, newForceVector));
                }

                if (target.isMovable()) {
                    // the existing force vector of target
                    GeometricalVector existingForceVectorTarget = target
                            .getForce(ATT_FORCE);
                    // adding new force vector to existing force of target
                    // (subtract because of the reverse direction of the source
                    // vector)
                    target.setForces(ATT_FORCE, GeometricalVector.subt(
                            existingForceVectorTarget, newForceVector));
                }
            }
        } // end while (edgesIt)
    }

    /**
     * Calculates the attractive force vector between two nodes.
     * 
     * @param source
     *            First given node.
     * @param target
     *            Second given node.
     * @param distVector
     *            Distance vector of the two nodes
     * @return The calculated force vector.
     */
    protected GeometricalVector calculateAttractiveForceVector(FRNode source,
            FRNode target, GeometricalVector distVector) {
        double x = distVector.getX();
        double y = distVector.getY();

        double attConst;
        double optimalNodeDist;
        if ((source instanceof FRLabelNode) || (target instanceof FRLabelNode)) {
            attConst = parameters.attractionConstantToLabel;
            if ((source instanceof FREdgeLabelNode)
                    || (target instanceof FREdgeLabelNode)) {
                // special case: edge labels use this force differently
                // beware: this code is dirty
                if (source instanceof FREdgeLabelNode) {
                    optimalNodeDist = ((FREdgeLabelNode) source)
                            .getCorrespondingFREdge().getLength() * 0.5d - 10d;
                } else {
                    optimalNodeDist = ((FREdgeLabelNode) target)
                            .getCorrespondingFREdge().getLength() * 0.5d - 10d;
                }
            } else {
                optimalNodeDist = parameters.optimalNodeDistanceToLabel;
            }
        } else {
            attConst = parameters.attractionConstant;
            optimalNodeDist = parameters.optimalNodeDistance;
        }

        if (!parameters.isCalculationWangMiyamoto) {

            double attForce;
            if (!parameters.isCalculationWithForster) {
                // System.out.println("sqrt (not forster in attForce)");
                double deltaLength = GeometricalVector.getLength(distVector);

                attForce = -attConst * deltaLength * deltaLength * deltaLength
                        / (optimalNodeDist);

                distVector = GeometricalVector.div(distVector, deltaLength);

            } else {

                attForce = -attConst * (x * x + y * y) / (optimalNodeDist);

            }
            // System.out.println("attForce: " + attForce);
            return GeometricalVector.mult(distVector, attForce);

        }
        // calculation with nodes as circles (Wang, Miyamoto 1995)
        else {

            double source_in = source.getDimension();
            double target_in = target.getDimension();

            double d_in = source_in + target_in;

            double d_out = Math.sqrt(x * x + y * y);

            double attForce = -attConst * d_out * d_out * d_out
                    / (optimalNodeDist + d_in);

            distVector = GeometricalVector.div(distVector, d_out);
            return GeometricalVector.mult(distVector, attForce);
        }
    }

    /**
     * Returns the attractive constant.
     * 
     * @return The attractive constant.
     */
    protected double getAttConst() {
        return parameters.attractionConstant;
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
