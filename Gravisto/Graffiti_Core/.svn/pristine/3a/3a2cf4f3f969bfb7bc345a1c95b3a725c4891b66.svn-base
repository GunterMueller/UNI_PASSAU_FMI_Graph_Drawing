package org.graffiti.plugins.algorithms.dfs;

import java.util.ArrayList;

import org.graffiti.graph.Node;

/**
 * This class calculates the height
 * 
 */
public class DFSHeightLabeler extends DoNothingLabeler {
    int max = -1;

    ArrayList<Integer> maxStack = new ArrayList<Integer>();

    // TODO: copied from AlgorithmEades in order to
    // resolve dependencies
    private static final String HEIGHT = "height";

    public DFSHeightLabeler() {
        super();
    }

    @Override
    public void processNode(Node v) {
        maxStack.add(max);
        max = -1;
    }

    @Override
    public void processNeighborFinally(Node neighbour) {
        max = Math.max(neighbour.getInteger(HEIGHT), max);
    }

    @Override
    public void processNodeFinally(Node current) {
        current.setInteger(HEIGHT, max + 1);
        max = maxStack.remove(maxStack.size() - 1);
    }

}
