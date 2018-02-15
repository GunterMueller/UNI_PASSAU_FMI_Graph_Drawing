package org.graffiti.plugins.algorithms.phyloTrees.tests;

import java.util.LinkedList;
import java.util.List;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Graph;
import org.graffiti.plugins.algorithms.phyloTrees.PhyloTreeAlgorithm;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeGraphData;

public class GraphDataTests implements Test {

    public List<String> test(Graph graph, PhyloTreeGraphData data) {
        List<String> failedTests = new LinkedList<String>();

        getGraphData(failedTests, graph, data);
        testAlgorithm(failedTests, graph, data);

        return failedTests;
    }

    private void getGraphData(List<String> failedTests, Graph graph,
            PhyloTreeGraphData incomingData) {

        PhyloTreeGraphData savedData = null;

        try {
            PhyloTreeAlgorithm phyloTreeAlgo = (PhyloTreeAlgorithm) GraffitiSingleton
                    .getInstance().getAlgorithmInstanceFromFriendlyName(
                            PhyloTreeAlgorithm.ALGORITHM_NAME);

            savedData = phyloTreeAlgo.getCorrespondingData(graph);
        } catch (ClassCastException e) {
            failedTests.add("GraphDataTests: " + e.toString());
        }

        if (savedData != incomingData) {
            failedTests.add("GraphDataTests: Graph data given != graph "
                    + "data fetched for specific graph");
        }
    }

    private void testAlgorithm(List<String> failedTests, Graph graph,
            PhyloTreeGraphData data) {
        if (data.getAlgorithm().getName() != (new TestAlgorithm()).getName()) {
            failedTests.add("GraphDataTests: wrong algorithm set in data");
        }
    }
}
