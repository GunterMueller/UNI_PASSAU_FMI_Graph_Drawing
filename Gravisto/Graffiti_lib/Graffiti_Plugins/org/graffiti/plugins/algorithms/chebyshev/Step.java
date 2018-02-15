package org.graffiti.plugins.algorithms.chebyshev;

import org.graffiti.plugins.algorithms.chebyshev.cores.AlgorithmCore;

public class Step {
    private int fixedLayer;
    private int activeLayer;

    public Step(int fixedLayer, int activeLayer) {
        this.fixedLayer = fixedLayer;
        this.activeLayer = activeLayer;
    }

    protected Step() {
    }

    /*
     * public int getActiveLayer() { return activeLayer; }
     * 
     * public int getFixedLayer() { return fixedLayer; }
     * 
     * public int getNeighborIndex() { return fixedLayer < activeLayer ? 1 : 0;
     * }
     */

    public void execute(AlgorithmCore core) {
        core.execute(fixedLayer, activeLayer, fixedLayer < activeLayer ? 1 : 0);
    }
}
