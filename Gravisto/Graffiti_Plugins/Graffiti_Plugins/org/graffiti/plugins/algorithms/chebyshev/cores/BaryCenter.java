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
public class BaryCenter extends AlgorithmCore {
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute(int fixedLayer, int activeLayer, int neighborIndex) {
        neighborIndex = 1 - neighborIndex;
        AuxLayer layer = graph.getLayer(activeLayer);
        Double[] xs = new Double[layer.getLength()];
        for (AuxNode node : layer.getIdNodes()) {
            AuxNode[] fixed = node.getNeighbors(neighborIndex);
            if (fixed.length == 0) {
                xs[node.getLocalId()] = Double.valueOf(node.getX());
            } else {
                int sum = 0;
                for (AuxNode n : fixed) {
                    sum += n.getX();
                }
                xs[node.getLocalId()] = sum / (double) fixed.length;
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
