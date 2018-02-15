package org.graffiti.plugins.algorithms.phyloTrees.tests;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.phyloTrees.utility.GravistoUtil;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeConstants;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeGraphData;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeUtil;

public class RerootTest implements Test {

    public List<String> test(Graph graph, PhyloTreeGraphData data) {
        List<String> errors = new LinkedList<String>();

        testRerooting(errors, graph, data);

        return errors;
    }

    private void testRerooting(List<String> failedTests, Graph graph,
            PhyloTreeGraphData incomingData) {
        String nrPath = PhyloTreeConstants.PATH_EDGE_NUMBER;

        Node c1 = createNode(graph, 1000, 0);
        Node c2 = createNode(graph, 0, 0);

        Edge ec1c2 = createEdge(graph, c1, c2, 2);

        Node c1o1 = createNode(graph, 1000, -50);
        // Edge ec1o1 =
        createEdge(graph, c1, c1o1, 3);

        Node c1o2 = createNode(graph, 1050, -50);
        Edge ec1o2 = createEdge(graph, c1, c1o2, 4);

        Node c1o3 = createNode(graph, 1050, 50);
        // Edge ec1o3 =
        createEdge(graph, c1, c1o3, 5);

        Node c1o4 = createNode(graph, 1000, 50);
        // Edge ec1o4 =
        createEdge(graph, c1, c1o4, 1);

        Node c2o1 = createNode(graph, 0, -100);
        Edge ec2o1 = createEdge(graph, c2, c2o1, 1);

        Node c2o2 = createNode(graph, 100, 0);
        // Edge ec2o2 =
        createEdge(graph, c2, c2o2, 2);

        Node c2o3 = createNode(graph, 0, 100);
        // Edge ec2o3 =
        createEdge(graph, c2, c2o3, 3);

        // first rerooting
        PhyloTreeUtil.rerootTree(c2);

        if (!PhyloTreeUtil.isRoot(c2)) {
            failedTests.add("rerooting: once: tree has not been rerooted.");
        }

        if (ec1c2.getInteger(nrPath) != 4) {
            failedTests
                    .add("rerooting: once -- source target edge has wrong number ("
                            + ec1c2.getInteger(nrPath) + " instead of 4)");
        }

        if (ec1o2.getInteger(nrPath) != 2) {
            failedTests
                    .add("rerooting: once --  edge from source has wrong number");
        }

        // second rerooting: back to initial state
        PhyloTreeUtil.rerootTree(c1);

        if (ec2o1.getInteger(nrPath) != 1) {
            failedTests
                    .add("rerooting: twice -- edge from target has wrong number");
        }

        if (ec1c2.getInteger(nrPath) != 2) {
            failedTests
                    .add("rerooting: twice -- edge source target has wrong number");
        }

        if (ec1o2.getInteger(nrPath) != 4) {
            failedTests
                    .add("rerooting: twice -- edge from source has wrong number");
        }

        if (!PhyloTreeUtil.isRoot(c1)) {
            failedTests.add("rerooting: twice: tree has not been rerooted.");
        }
    }

    private Node createNode(Graph graph, double x, double y) {
        Node node = graph.addNode();
        Point2D coords = new Point2D.Double(x, y);
        GravistoUtil.setCoords(node, coords);
        return node;
    }

    private Edge createEdge(Graph graph, Node src, Node target, int nr) {
        Edge edge = graph.addEdge(src, target, true);
        edge.setInteger(PhyloTreeConstants.PATH_EDGE_NUMBER, nr);
        return edge;
    }
}
