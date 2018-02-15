package org.graffiti.plugins.algorithms.SchnyderRealizer;

import org.graffiti.graph.Graph;

/**
 * This class represents an improvement of Brehm`s algorithm to calculate one
 * Schnyder realizer by He. The grid size iof the drawing is reduced by (number
 * of cw faces + 1) compared to the "standard" algorithm. This class is more or
 * less just a wrapper class. The main work is done in the
 * <code>BarycentricRepresentation</code>.
 * 
 * @author hofmeier
 */
public class HeImprovement extends BrehmOneRealizer {

    /**
     * Creates a new instance of the class
     * 
     * @param g
     *            the graph to be drawn.
     * @param m
     *            the maximum number of realizers to be created (not used in
     *            here)
     */
    public HeImprovement(Graph g, int m) {
        super(g, m);
    }

    /**
     * Executes the algoithm by calling the algorithm from the superclass and
     * overwrite the coordinates by new ones calculated by He`s method.
     */
    @Override
    public void execute() {
        Realizer realizer = this.createRealizer();
        this.realizers.add(realizer);
        BarycentricRepresentation br = new BarycentricRepresentation(realizer,
                this.graph, this.outerNodes);
        // Overwrite the former coordinatesby the new ones calculated by He`s
        // method
        br.calculateCoordinatesByHe(this.facesByEdges);
        this.barycentricReps.add(br);
    }
}
