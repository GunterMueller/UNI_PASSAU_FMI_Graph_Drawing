// =============================================================================
//
//   MagneticForce.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MagneticForce.java 1289 2006-06-12 05:52:18Z matzeder $

package org.graffiti.plugins.algorithms.labeling.forces;

import org.graffiti.plugins.algorithms.labeling.SELabelingAlgorithmParameters;
import org.graffiti.plugins.algorithms.springembedderFR.ConcentricMagneticField;
import org.graffiti.plugins.algorithms.springembedderFR.FREdge;
import org.graffiti.plugins.algorithms.springembedderFR.FRNode;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;
import org.graffiti.plugins.algorithms.springembedderFR.InvalidMagneticFieldException;
import org.graffiti.plugins.algorithms.springembedderFR.ParallelMagneticField;
import org.graffiti.plugins.algorithms.springembedderFR.PolarMagneticField;

/**
 * To calculate the magnetic forces of a magnetic field for the edges of a
 * graph.
 * 
 * @author matzeder
 * @version $Revision: 1289 $ $Date: 2006-06-12 07:52:18 +0200 (Mon, 12 Jun
 *          2006) $
 */
public class MagneticForce extends AbstractSEForce {

    /**
     * Creates a new magnetic force.
     * 
     * @param p
     *            Parameters of the algorithms.
     */
    public MagneticForce(SELabelingAlgorithmParameters p) {
        super(p);
        setMagneticField(p);
    }

    /**
     * Calculates and sets the magnetic spring forces.
     * 
     */
    protected void calcMagneticSpringForces() {

        if (parameters.magneticField instanceof PolarMagneticField
                || parameters.magneticField instanceof ConcentricMagneticField) {

            for (FRNode node : parameters.fRGraph.getFRNodes()) {

                if (node.getLabel().equals("wurzel")) {
                    System.out.println(node.getLabel());

                    System.out.println(parameters.root);
                    System.out.println(node);
                    parameters.root = new GeometricalVector(node);
                    parameters.magneticField.setCenter(parameters.root);

                }

            }
        }
        for (FREdge edge : parameters.fRGraph.getFREdges()) {
            GeometricalVector edgeDirection = new GeometricalVector(edge
                    .getTarget(), edge.getSource()).getUnitVector();

            // the center of an edge
            GeometricalVector edgeCenter = new GeometricalVector(
                    (edge.getSource().getXPos() + edge.getTarget().getXPos()) / 2,
                    (edge.getSource().getXPos() + edge.getTarget().getYPos()) / 2);

            // direction of the magnetic field
            GeometricalVector magneticFieldVector = parameters.magneticField
                    .getDirection(edgeCenter.getX(), edgeCenter.getY());

            // System.out.println("magneticFieldVector" + magneticFieldVector);

            double theta = Math.acos(edgeDirection.getX()
                    * magneticFieldVector.getX() + edgeDirection.getY()
                    * magneticFieldVector.getY());

            GeometricalVector orthogonalUnitVector = GeometricalVector
                    .getOrthogonalUnitVector(edgeDirection);

            double theta1 = Math.acos(orthogonalUnitVector.getX()
                    * magneticFieldVector.getX() + orthogonalUnitVector.getY()
                    * magneticFieldVector.getY());
            double theta2 = Math.acos(-orthogonalUnitVector.getX()
                    * magneticFieldVector.getX() + -orthogonalUnitVector.getY()
                    * magneticFieldVector.getY());

            double mForce = getMagneticForce(edge, theta);

            if (theta1 < theta2) {

                GeometricalVector existingForceVectorSource = edge.getSource()
                        .getForce(MAGN_FORCE);

                edge.getSource().setForces(
                        MAGN_FORCE,
                        GeometricalVector.add(existingForceVectorSource,
                                GeometricalVector.mult(orthogonalUnitVector,
                                        -mForce)));

                GeometricalVector existingForceVectorTarget = edge.getTarget()
                        .getForce(MAGN_FORCE);

                edge.getTarget().setForces(
                        MAGN_FORCE,
                        GeometricalVector.add(existingForceVectorTarget,
                                GeometricalVector.mult(orthogonalUnitVector,
                                        mForce)));

            } else {

                orthogonalUnitVector.setGeometricalVector(-orthogonalUnitVector
                        .getX(), -orthogonalUnitVector.getY());

                GeometricalVector existingForceVectorSource = edge.getSource()
                        .getForce(MAGN_FORCE);

                edge.getSource().setForces(
                        MAGN_FORCE,
                        GeometricalVector.add(existingForceVectorSource,
                                GeometricalVector.mult(orthogonalUnitVector,
                                        -mForce)));

                GeometricalVector existingForceVectorTarget = edge.getTarget()
                        .getForce(MAGN_FORCE);

                edge.getTarget().setForces(
                        MAGN_FORCE,
                        GeometricalVector.add(existingForceVectorTarget,
                                GeometricalVector.mult(orthogonalUnitVector,
                                        mForce)));
            }

        }

    }

    /**
     * Returns the magnetic force for an edge and the given angle.
     * 
     * @param edge
     *            The given edge.
     * @param angle
     *            The given angle.
     * @return The calculated magnetic force.
     */
    protected double getMagneticForce(FREdge edge, double angle) {

        double alpha = 0.9d;
        double beta = 0.75d;

        return parameters.magneticSpringConstant
                * Math.pow(edge.getLength(), alpha) * Math.pow(angle, beta);
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.springembedderFR.AbstractSEForces#
     * calculateForce()
     */
    @Override
    public void calculateForce() {
        calcMagneticSpringForces();

    }

    /**
     * Sets the magnetic field.
     * 
     * @param p
     *            To know, which magnetic field exists.
     */
    public void setMagneticField(SELabelingAlgorithmParameters p) {

        if (!p.magneticFieldParameter.getSelectedValue().equals(
                AbstractSEForce.NO_MAGNETIC_FIELD)) {

            boolean parallel = false;
            GeometricalVector dir = new GeometricalVector();
            if (p.magneticFieldParameter.getSelectedValue().equals(
                    AbstractSEForce.NORTH)) {
                dir.setGeometricalVector(0, -1);
                parallel = true;
            } else if (p.magneticFieldParameter.getSelectedValue().equals(
                    AbstractSEForce.SOUTH)) {
                dir.setGeometricalVector(0, 1);
                parallel = true;
            } else if (p.magneticFieldParameter.getSelectedValue().equals(
                    AbstractSEForce.WEST)) {
                dir.setGeometricalVector(-1, 0);
                parallel = true;
            } else if (p.magneticFieldParameter.getSelectedValue().equals(
                    AbstractSEForce.EAST)) {
                dir.setGeometricalVector(1, 0);
                parallel = true;
            }

            if (parallel) {
                try {
                    p.magneticField = new ParallelMagneticField((int) dir
                            .getX(), (int) dir.getY());
                    return;

                } catch (InvalidMagneticFieldException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

            if (p.magneticFieldParameter.getSelectedValue().equals(
                    AbstractSEForce.POLAR)) {
                // This is a hack, but it has already been when I got it...
                p.repulsionConstantNodeToLabel = (p.repulsionConstantNodeToLabel / p.repulsionConstantNodeNode) * 5d;

                p.repulsionConstantNodeNode = 50.0;
                try {

                    p.magneticField = new PolarMagneticField(
                            new GeometricalVector());
                } catch (InvalidMagneticFieldException e) {
                    System.out.println(e);
                }

            } else if (p.magneticFieldParameter.getSelectedValue().equals(
                    AbstractSEForce.CONCENTRIC_CLOCK)) {

                try {

                    p.magneticField = new ConcentricMagneticField(
                            new GeometricalVector());
                } catch (InvalidMagneticFieldException e) {
                    System.out.println(e);
                }

            }

        }

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
