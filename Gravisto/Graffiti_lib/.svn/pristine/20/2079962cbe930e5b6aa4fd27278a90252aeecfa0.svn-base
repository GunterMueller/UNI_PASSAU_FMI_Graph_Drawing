// =============================================================================
//
//   GraphFixture.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package tests.graffiti.plugins.algorithms.mst;

import java.util.Collection;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * @author Harald
 * @version $Revision$ $Date$
 */
public class GraphFixture {
    private Graph graph = null;

    public GraphFixture() {

    }

    public void setUpEmptyGraph() {
        graph = new GraphStub();
    }

    public void setUpConnectedCircles() {
        graph = new GraphStub(8);
        for (int i = 0; i < 8; i++) {
            graph.addNode();
        }
        List<Node> nodes = graph.getNodes();
        Node n1 = nodes.get(0);
        for (int i = 0; i < 4; i++) {
            graph.addEdge(nodes.get(i), nodes.get((i + 1) % 4), false);
        }
        Node n2 = nodes.get(4);
        for (int i = 4; i < 8; i++) {
            graph.addEdge(nodes.get(i), nodes.get((i + 1) % 8), false);
        }
        graph.addEdge(n1, n2, false);
    }

    public Collection<Edge> getEdges() {
        return java.util.Collections.unmodifiableCollection(graph.getEdges());
    }

    public Graph getGraph() {
        return graph;
    }

    public Collection<Node> getNodes() {
        return java.util.Collections.unmodifiableList(graph.getNodes());
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
