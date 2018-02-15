// =============================================================================
//
//   InitialLevelling.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting.initiallevelling;

import org.graffiti.graph.Graph;
import org.graffiti.plugins.algorithms.sugiyama.Sugiyama;
import org.graffiti.plugins.algorithms.sugiyama.gridsifting.AbstractSubAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.util.Pair;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class InitialLevelling extends AbstractSubAlgorithm {
    public abstract void execute(Graph graphCopy, SugiyamaData dataCopy);

    protected int initialMaxLevelWidth;

    public void setInitialMaxLevelWidth(int initialMaxLevelWidth) {
        this.initialMaxLevelWidth = initialMaxLevelWidth;
    }

    /**
     * 
     * @return a pair containing the number of crossings and the number of
     *         levels.
     */
    public final Pair<Integer, Integer> execute() {
        Graph graphCopy = (Graph) graph.getGraph().copy();

        SugiyamaData dataCopy = sugiyamaData.copy();

        dataCopy.setGraph(graphCopy);

        execute(graphCopy, dataCopy);

        graph.importLevels(graphCopy);

        return Pair.create(Sugiyama.countCrossings(dataCopy), dataCopy
                .getLayers().getNumberOfLayers());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
