package org.graffiti.plugins.algorithms.chebyshev.cores;

import org.graffiti.plugins.algorithms.chebyshev.AbstractSubAlgorithm;

public abstract class AlgorithmCore extends AbstractSubAlgorithm {
    /**
     * 
     * @param fixedLayer
     * @param activeLayer
     * @param neighborIndex
     *            to access the fixed nodes from the active layer.
     */
    public abstract void execute(int fixedLayer, int activeLayer,
            int neighborIndex);
}
