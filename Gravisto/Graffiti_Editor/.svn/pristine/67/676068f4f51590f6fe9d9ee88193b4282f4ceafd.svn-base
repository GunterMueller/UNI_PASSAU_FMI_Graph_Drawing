package org.graffiti.plugins.algorithms.phyloTrees.tests;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.phyloTrees.utility.EdgeCounterclockwiseComparator;
import org.graffiti.plugins.algorithms.phyloTrees.utility.GravistoUtil;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeGraphData;

public class EdgeCounterclockwiseComparatorTester implements Test {

    public List<String> test(Graph graph, PhyloTreeGraphData data) {
        List<String> failedTests = new LinkedList<String>();

        test1(failedTests, graph);
        test2(failedTests, graph);
        test3(failedTests, graph);
        test4(failedTests, graph);

        return failedTests;
    }

    private void test1(List<String> failedTests, Graph graph) {
        // create minimalistic tree
        Node n1 = createNode(graph, 0, 0);
        Node n2 = createNode(graph, 100, -100);
        Edge reference = graph.addEdge(n1, n2, true);

        Node ch1 = createNode(graph, 150, -50);
        Edge edge1 = graph.addEdge(n2, ch1, true);

        Node ch2 = createNode(graph, 100, -200);
        Edge edge2 = graph.addEdge(n2, ch2, true);

        Node ch3 = createNode(graph, 100, -300);
        Edge edge3 = graph.addEdge(n2, ch3, true);

        Node ch4 = createNode(graph, 200, -200);
        Edge edge4 = graph.addEdge(n2, ch4, true);

        // test with Comparator
        EdgeCounterclockwiseComparator comp = new EdgeCounterclockwiseComparator(
                reference);

        int edges12Compared = comp.compare(edge1, edge2);
        // edge1 is supposed to be smaller than edge2
        if (edges12Compared >= 0) {
            testFailed(failedTests, "compare method returned "
                    + edges12Compared + " instead of a value smaller than 0");
        }

        int edges21Compared = comp.compare(edge2, edge1);
        if (edges21Compared <= 0) {
            testFailed(failedTests, "compare method returned "
                    + edges21Compared + " instead of a value larger than 0");
        }

        int edges23Compared = comp.compare(edge2, edge3);
        if (edges23Compared != 0) {
            testFailed(failedTests, "compare method returned "
                    + edges23Compared + " instead of 0");
        }

        int edges14Compared = comp.compare(edge1, edge4);
        if (edges14Compared >= 0) {
            testFailed(failedTests, "compare method returned "
                    + edges12Compared + " instead of a smaller than 0");
        }
    }

    private void test2(List<String> failedTests, Graph graph) {
        // create tree
        Node n1 = createNode(graph, 300, 100);
        Node n2 = createNode(graph, 150, 300);
        Edge reference = graph.addEdge(n1, n2, true);

        Node ch1 = createNode(graph, 211, 488);
        Edge e1 = graph.addEdge(n2, ch1, true);

        Node ch2 = createNode(graph, 55, 400);
        Edge e2 = graph.addEdge(n2, ch2, true);

        Node ch3 = createNode(graph, 20, -100);
        Edge e3 = graph.addEdge(n2, ch3, true);

        Node ch4 = createNode(graph, 200, 101);
        Edge e4 = graph.addEdge(n2, ch4, true);

        Node ch5 = createNode(graph, 400, 331);
        Edge e5 = graph.addEdge(n2, ch5, true);

        // create test case
        EdgeCounterclockwiseComparator comp = new EdgeCounterclockwiseComparator(
                reference);
        Edge[] outgoingEdges = { e1, e2, e3, e4, e5 };
        Edge[] correctlySortedEdges = { e4, e3, e2, e1, e5 };

        Arrays.sort(outgoingEdges, comp);

        boolean sortingIncorrect = false;
        for (int i = 0; i < outgoingEdges.length; ++i) {
            if (outgoingEdges[i] != correctlySortedEdges[i]) {
                sortingIncorrect = true;
            }
        }

        if (sortingIncorrect) {
            testFailed(failedTests, "sorting of Edges incorrect");
        }
    }

    private void test3(List<String> failedTests, Graph graph) {
        Node n1 = createNode(graph, 300, 100);
        Node n2 = createNode(graph, 200, 100);
        Edge reference = graph.addEdge(n1, n2, true);

        Node ch1 = createNode(graph, 100, 180);
        Edge e1 = graph.addEdge(n2, ch1, true);

        Node ch2 = createNode(graph, 150, 190);
        Edge e2 = graph.addEdge(n2, ch2, true);

        EdgeCounterclockwiseComparator comp = new EdgeCounterclockwiseComparator(
                reference);

        if (comp.compare(e1, e2) >= 0) {
            testFailed(failedTests, "test3: should be smaller than 0, is "
                    + comp.compare(e1, e2));
        }

    }

    private void test4(List<String> failedTests, Graph graph) {
        Node n1 = createNode(graph, -182, 44);
        Node n2 = createNode(graph, -16, 132);
        Edge reference = graph.addEdge(n1, n2, true);

        Node ch1 = createNode(graph, 44, 147);
        Edge e1 = graph.addEdge(n2, ch1, true);

        Node ch2 = createNode(graph, 30, 173);
        Edge e2 = graph.addEdge(n2, ch2, true);

        EdgeCounterclockwiseComparator comp = new EdgeCounterclockwiseComparator(
                reference);

        if (comp.compare(e1, e2) <= 0) {
            testFailed(failedTests, "test4: should be larger than 0, is "
                    + comp.compare(e1, e2));
        }

    }

    private void testFailed(List<String> failedTests, String description) {
        failedTests.add("EdgeCounterclockwiseComparator: " + description);
    }

    private Node createNode(Graph graph, double x, double y) {
        Node node = graph.addNode();
        Point2D coords = new Point2D.Double(x, y);
        GravistoUtil.setCoords(node, coords);
        return node;
    }

}
