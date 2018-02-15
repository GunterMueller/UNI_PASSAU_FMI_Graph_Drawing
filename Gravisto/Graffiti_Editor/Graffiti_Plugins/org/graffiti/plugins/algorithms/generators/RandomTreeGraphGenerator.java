// =============================================================================
//
//   CompleteBinaryTreeGraphGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RandomTreeGraphGenerator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.awt.geom.Point2D;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugins.algorithms.reingoldtilford.ReingoldTilfordAlgorithm;

/**
 * This generator creates a graph with n nodes. In each node the number of child
 * nodes are determined randomly.
 */
public class RandomTreeGraphGenerator extends AbstractGenerator {

    /** number of nodes */
    private IntegerParameter nodesParam;

    /** branch factor */
    private IntegerParameter maxBranchParam;

    /** Should the tree be directed? */
    private BooleanParameter isDirectedParam;

    /**
     * Constructs a new instance.
     */
    public RandomTreeGraphGenerator() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        nodesParam = new IntegerParameter(new Integer(60), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");
        this.maxBranchParam = new IntegerParameter(new Integer(20),
                new Integer(0), new Integer(100), "maximum branch factor",
                "...");
        isDirectedParam = new BooleanParameter(true, "directed",
                "Should the tree be directed?");
        parameterList.addFirst(isDirectedParam);
        parameterList.addFirst(nodesParam);
        parameterList.addFirst(maxBranchParam);

    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Random Tree";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (nodesParam.getValue().compareTo(new Integer(1)) < 0) {
            errors.add("The number of nodes may not be smaller than two.");
        }

        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        // add nodes
        int numberOfNodes = nodesParam.getValue().intValue();
        int maxBranchFactor = maxBranchParam.getValue().intValue();
        boolean directed = (isDirectedParam.getValue()).booleanValue();
        graph.setDirected(directed);

        graph.getListenerManager().transactionStarted(this);

        LinkedList<Node> nodesInLowestLevel = new LinkedList<Node>();

        // add root node
        Node root = graph.addNode();
        nodesInLowestLevel.add(root);
        CoordinateAttribute ca = (CoordinateAttribute) root
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        ca.setCoordinate(new Point2D.Double(100.0, 50.0));

        int numberOfNodesLeft = numberOfNodes - 1;

        while (numberOfNodesLeft > 0) {
            int lowestLevelIndex = (int) (Math.random() * nodesInLowestLevel
                    .size());
            int numberOfNodesToAdd = (int) (Math.random() * Math.min(
                    maxBranchFactor, numberOfNodesLeft)) + 1;

            Node chosenNode = nodesInLowestLevel.remove(lowestLevelIndex);
            for (int i = 0; i < numberOfNodesToAdd; i++) {
                Node newChildNode = graph.addNode();
                graph.addEdge(chosenNode, newChildNode, directed);
                nodesInLowestLevel.add(newChildNode);
            }

            numberOfNodesLeft -= numberOfNodesToAdd;
        }

        if (directed) {
            setEdgeArrows(graph);
        }

        graph.getListenerManager().transactionFinished(this);

        // label the nodes
        if (nodeLabelParam.getBoolean().booleanValue()) {
            labelNodes(graph.getNodes(), startNumberParam.getValue().intValue());
        }

        // label the edges
        if (edgeLabelParam.getBoolean().booleanValue()) {
            labelEdges(graph.getEdges(), edgeLabelNameParam.getString(),
                    edgeMin.getValue().intValue(), edgeMax.getValue()
                            .intValue());
        }

        ReingoldTilfordAlgorithm rt = new ReingoldTilfordAlgorithm();

        boolean okSelected = GraffitiSingleton.showParameterDialog(rt);
        if (!okSelected)
            return;

        rt.attach(graph);
        rt.setRoot(root);
        rt.execute();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
        nodesParam.setValue(new Integer(5));
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
