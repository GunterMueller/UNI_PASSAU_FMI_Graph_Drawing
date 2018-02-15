package org.graffiti.plugins.algorithms.dfs;

import org.graffiti.graph.Node;

/**
 * This class counts the number of nodes and gives each node the number in which
 * order it is beeing visited from the dfs
 * 
 */
public class DFSNodeEnumerator extends DoNothingLabeler {
    int counter = 0;

    @Override
    public void processNode(Node v) {
        v.setInteger("number", counter);
        counter++;

    }

    @Override
    public void reset() {
        counter = 0;

    }

}
