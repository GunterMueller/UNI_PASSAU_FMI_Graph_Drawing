// =============================================================================
//
//   NonConvexContoursAlgorihm.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$
package org.graffiti.plugins.algorithms.treedrawings.HexaGridPentaTree;

import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;

/**
 * Diese Klasse implementiert einen Algorithmus der die Zeichnung eines planaren
 * 5-naeren Baum auf einen triangulierten Gitter kompaktiert
 */
public class NonConvexContoursAlgorithm extends AbstractAlgorithm {
    /**
     * Constructs a new instance.
     */
    public NonConvexContoursAlgorithm() {

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "non Convex Contours Algorithm";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        GraphChecker.checkTree(this.graph, 5);
        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);

        PentaTree pentaTree = new PentaTree(graph);
        // Algorithm 1 calculates the coordinates guaranteing planarity
        pentaTree.calculateCoordinatesPlanar();

        pentaTree.copyFinalCoordinatesToOriginalNodes();
        // Jetzt muessen noch die Koordinaten von den UniformNodes ausgelesen
        // werden und richtig bei den

        pentaTree.calculateCompactedCoordinates();
        pentaTree.copyFinalCoordinatesToOriginalNodes();
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;

    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
