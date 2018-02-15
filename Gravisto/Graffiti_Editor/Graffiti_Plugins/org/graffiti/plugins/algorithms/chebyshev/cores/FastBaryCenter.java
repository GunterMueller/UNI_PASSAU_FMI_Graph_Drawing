// =============================================================================
//
//   BaryCenter.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.chebyshev.cores;

import java.util.Arrays;

import org.graffiti.plugins.algorithms.chebyshev.ArrayComparator;
import org.graffiti.plugins.algorithms.chebyshev.AuxLayer;
import org.graffiti.plugins.algorithms.chebyshev.AuxNode;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class FastBaryCenter extends AlgorithmCore {
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(int fixedLayer, int activeLayer, int neighborIndex) {
        neighborIndex = 1 - neighborIndex;
        AuxLayer layer = graph.getLayer(activeLayer);
        Double[] xs = new Double[layer.getLength()];
        for (AuxNode node : layer.getIdNodes()) {
            AuxNode[] fixed1 = node.getNeighbors(neighborIndex);
            AuxNode[] fixed2 = node.getNeighbors(1 - neighborIndex);
            if (fixed1.length == 0 && fixed2.length == 0) {
                xs[node.getLocalId()] = Double.valueOf(node.getX());
            } else {
                int sum1 = 0;
                int sum2 = 0;
                for (AuxNode n : fixed1) {
                    sum1 += n.getX();
                }
                for (AuxNode n : fixed2) {
                    sum2 += n.getX();
                }
                xs[node.getLocalId()] = (sum1 / (double) fixed1.length) + (sum2 / (double) fixed2.length);
            }
        }
        AuxNode[] xNodes = layer.getXNodes();
        Arrays.sort(xNodes, new ArrayComparator<Double>(xs));
        layer.updateIndices();
        xNodes[0].setX(0);
        layer.repairDistances();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
