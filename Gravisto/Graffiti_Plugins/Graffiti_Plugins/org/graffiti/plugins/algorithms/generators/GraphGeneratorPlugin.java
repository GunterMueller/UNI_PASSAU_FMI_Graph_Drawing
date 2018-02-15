// =============================================================================
//
//   GraphGeneratorPlugin.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GraphGeneratorPlugin.java 6280 2014-08-25 16:19:06Z ehrlingc $

package org.graffiti.plugins.algorithms.generators;

import org.graffiti.plugin.GenericPluginAdapter;
import org.graffiti.plugin.PluginPathNode;
import org.graffiti.plugin.algorithm.Algorithm;

/**
 * Provides some graph generator algorithms.
 * 
 * @version $Revision: 6280 $
 */
public class GraphGeneratorPlugin extends GenericPluginAdapter {

    /**
     * Creates a new GraphGeneratorPlugin object.
     */
    public GraphGeneratorPlugin() {
        this.algorithms = new Algorithm[] { new CirculantGraphGenerator(),
                new CompleteGraphGeneratorAlgorithm(),
                new CompleteKPartiteGraphGenerator(), new PlanarMapGenerator(),
                new GridGeneratorAlgorithm(), new ThreeDimGridGraphGenerator(),
                new CycleGraphGenerator(), new WheelGraphGenerator(),
                new GraphGeneratorHypercube(), new RandomTreeGraphGenerator(),
                new CompleteBinaryTreeGraphGenerator(),
                new RandomGraphGeneratorNodeDensityBased(),
                new RandomGraphGeneratorNeighborConnecting(),
                new GraphGeneratorPetersen(), new GraphGeneratorHarary(),
                new RandomTournamentGraphGenerator(),
                new RandomGraphGeneratorGilbert(),
                new RandomGraphGeneratorPreference(),
                new RandomGraphGeneratorSmallWorld(),
                new RandomGraphGenerator(), new RandomCoordinatesGenerator(),
                new RandomFourConnectedPlanarGraphGenerator(),
                new CompleteTreeGraphGenerator(), new CrossMinGraphGenerator(),
                new PlanarTriconnectedGraphGenerator()};
    }

    @Override
    public PluginPathNode getPathInformation() {

        return new PluginPathNode("Generators", algorithms, null);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
