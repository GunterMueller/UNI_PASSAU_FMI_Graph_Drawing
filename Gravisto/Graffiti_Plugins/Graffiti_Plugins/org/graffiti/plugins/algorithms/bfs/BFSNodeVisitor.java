package org.graffiti.plugins.algorithms.bfs;

import org.graffiti.graph.Node;

public interface BFSNodeVisitor {

    public void processNode(Node v);

    public void processNeighbor(Node v);

    public void reset();

}
