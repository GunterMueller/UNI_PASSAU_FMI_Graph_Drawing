// =============================================================================
//
//   GravityForce.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GravityForce.java 1289 2006-06-12 05:52:18Z matzeder $

package org.graffiti.plugins.algorithms.labeling.forces;

import org.graffiti.plugins.algorithms.labeling.SELabelingAlgorithmParameters;
import org.graffiti.plugins.algorithms.springembedderFR.FRNode;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

/**
 * The gravity force of a spring embedder.
 * 
 * @author matzeder
 * @version $Revision: 1289 $ $Date: 2006-06-12 07:52:18 +0200 (Mon, 12 Jun
 *          2006) $
 */
public class GravityForce extends AbstractSEForce {

    /**
     * Creates a new gravity force for all nodes of the graph.
     * 
     * @param p
     *            Parameters of the algorithms.
     */
    public GravityForce(SELabelingAlgorithmParameters p) {
        super(p);
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.springembedderFR.AbstractSEForces#
     * calculateForce()
     */
    @Override
    public void calculateForce() {
        for (FRNode node : parameters.fRGraph.getFRNodes()) {
            setGravitiyForce(node);
        }
    }

    /**
     * Sets the gravity force for the given node
     * 
     * @param currNode
     *            The given node.
     */
    protected void setGravitiyForce(FRNode currNode) {

        GeometricalVector newForceVector = new GeometricalVector(currNode,
                parameters.barycenter);

        double gravity = ((double) -parameters.gravityConstant) / 100;

        newForceVector = GeometricalVector.mult(newForceVector, gravity);

        GeometricalVector forceVector = currNode.getForce(GRAV_FORCE);
        currNode.setForces(GRAV_FORCE, GeometricalVector.add(forceVector,
                newForceVector));
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
