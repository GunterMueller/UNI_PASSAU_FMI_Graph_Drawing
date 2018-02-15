package org.graffiti.plugins.algorithms.phyloTrees.tests;

import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;
import org.graffiti.plugins.algorithms.phyloTrees.PhylogeneticTree;
import org.graffiti.plugins.algorithms.phyloTrees.utility.GravistoUtil;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeGraphData;

public class TestAlgorithm implements PhylogeneticTree {

    private LinkedList<String> failedTests;

    private List<Test> registeredTests;

    public TestAlgorithm() {
        registeredTests = new LinkedList<Test>();

        registeredTests.add(new EdgeCounterclockwiseComparatorTester());
        registeredTests.add(new GraphDataTests());
        registeredTests.add(new RerootTest());
    }

    public void drawGraph(Graph graph, PhyloTreeGraphData data) {
        failedTests = new LinkedList<String>();

        if (graph == null) {
            failedTests.add("graph parameter is null reference");
        }

        if (data == null) {
            failedTests.add("data parameter is null reference");
        }

        testDataAlgorithm(data);
        testParameters(data.getAlgorithmParameters());

        testRegisteredTests(failedTests, graph, data);

        testContainsAttributeFunctionality(failedTests, graph, data);
        testCircleLineSegmentationShape(failedTests, graph, data);

        printResults(failedTests);
    }

    private void testCircleLineSegmentationShape(List<String> failedTests,
            Graph graph, PhyloTreeGraphData data) {
        Node source = graph.addNode();
        GravistoUtil.setCoords(source, new Point2D.Float(400f, 300f));

        Node target = graph.addNode();
        GravistoUtil.setCoords(target, new Point2D.Float(700f, 100f));

        Edge edge = graph.addEdge(source, target, true);

        Point2D center = new Point2D.Float(400, 100);
        CoordinateAttribute attr = new CoordinateAttribute(
                GraphicAttributeConstants.CIRCLE_CENTER);
        attr.setCoordinate(center);
        edge.addAttribute(attr, GraphicAttributeConstants.GRAPHICS);

        GravistoUtil.setEdgeShape(edge,
                GraphicAttributeConstants.CIRCLE_LINE_SEGMENTATION_CLASSNAME);
    }

    private void testContainsAttributeFunctionality(List<String> failedTests,
            Graph graph, PhyloTreeGraphData data) {
        Node node = graph.addNode();

        if (!node.containsAttribute(GraphicAttributeConstants.GRAPHICS)) {
            failedTests
                    .add("path to graphics does not exist in node according to method containsPath");
        }

        if (!node.containsAttribute(GraphicAttributeConstants.GRAPHICS)) {
            failedTests
                    .add("path to graphics does not exist in node according to method containsPath");
        }

        String testPath = "test";

        if (node.containsAttribute(testPath)) {
            failedTests.add("path exists according to method containsPath");
        }

        node.addBoolean("", testPath, true);

        if (!node.containsAttribute(testPath)) {
            failedTests
                    .add("path does not exist according to method containsPath");
        }
    }

    private void testRegisteredTests(List<String> failedTests, Graph graph,
            PhyloTreeGraphData data) {
        for (Test test : registeredTests) {
            failedTests.addAll(test.test(graph, data));
        }
    }

    public String getName() {
        return "TestAlgorithm";
    }

    public Parameter<?>[] getParameters() {
        BooleanParameter bp = new BooleanParameter(true, "Testbool", "");
        StringParameter sp = new StringParameter("foo", "String Parameter", "");
        IntegerParameter ip = new IntegerParameter(0, "Integer Parameter",
                "desc");
        BooleanParameter bp2 = new BooleanParameter(false, "Testbool 2", "");

        Parameter<?>[] parameters = { bp, sp, ip, bp2 };
        return parameters;
    }

    public void redrawParts(Graph graph, Node tainted, PhyloTreeGraphData data) {
        throw new UnsupportedOperationException("redrawParts");
    }

    /**
     * Prints the errors found in failedTests.
     * 
     * @param failedTests
     *            The error messages to be printed.
     */
    private void printResults(List<String> failedTests) {
        if (failedTests.isEmpty()) {
            System.out.println("All tests have been completed successfully");
        } else {
            System.out.println("The following tests have failed:\n");

            for (String errorDescription : failedTests) {
                System.out.println("- " + errorDescription);
            }
        }
    }

    private void testDataAlgorithm(PhyloTreeGraphData data) {
        if (data.getAlgorithm() == null) {
            failedTests.add("algorithm in data object is null reference");
            return;
        }

        if (data.getAlgorithm().getClass() != this.getClass()) {
            failedTests.add("wrong algorithm class in data");
        }
    }

    private void testParameters(Parameter<?>[] optionsParameters) {
        if (optionsParameters == null) {
            failedTests.add("parameters in data object is null reference");
            return;
        }

        Parameter<?>[] referenceParameters = getParameters();

        if (referenceParameters.length != optionsParameters.length) {
            failedTests.add("parameter array given in data has "
                    + "incorrect length: " + optionsParameters.length
                    + " instead of " + referenceParameters.length);
            return;
        }

        for (int i = 0; i < optionsParameters.length; ++i) {
            if (!optionsParameters[i].getName().equals(
                    referenceParameters[i].getName())) {
                failedTests.add("parameter is in incorrect order at index " + i
                        + ": " + optionsParameters[i].getName()
                        + " instead of " + referenceParameters[i].getName());
            }
        }
    }
}
