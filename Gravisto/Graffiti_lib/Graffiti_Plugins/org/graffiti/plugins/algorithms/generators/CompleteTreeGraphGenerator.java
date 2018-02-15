// =============================================================================
//
//   CompleteBinaryTreeGraphGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CompleteBinaryTreeGraphGenerator.java 1524 2006-10-18 02:13:56 +0200 (Wed, 18 Oct 2006) keilhaue $

package org.graffiti.plugins.algorithms.generators;

import java.util.Collection;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;

/**
 * This generator creates a graph with n nodes. The nodes and edges build a
 * complete tree of degree 5.
 */
public class CompleteTreeGraphGenerator extends AbstractGenerator {

    /** number of nodes */
    private IntegerParameter nodesParam;

    /** Should the tree be directed? */
    private BooleanParameter isDirectedParam;

    private StringSelectionParameter degree;

    /**
     * Constructs a new instance.
     */
    public CompleteTreeGraphGenerator() {
        super();
        // addNodeLabelingOption();
        // addEdgeLabelingOption();
        nodesParam = new IntegerParameter(new Integer(6), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");
        isDirectedParam = new BooleanParameter(true, "directed",
                "Should the tree be directed?");
        String[] deg = { "Ternary Tree", "Tree of Degree 4", "Tree of Degree 5" };
        degree = new StringSelectionParameter(deg,
                "Degree of the generated tree:", "<html><p>3</p>" + "<p>4</p>"
                        + "<p>5</p>" + "</html>");
        parameterList.addFirst(isDirectedParam);
        parameterList.addFirst(nodesParam);
        parameterList.addFirst(degree);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Complete Tree of Degree 3, 4 or 5";
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
     * Adds a new node and ensures all needed attributes exist.
     * 
     * @param nodes
     *            Collection to add new node to.
     */
    protected void addNode(Collection<Node> nodes) {
        Node node = graph.addNode();
        try {
            node.getAttribute(GraphicAttributeConstants.GRAPHICS);
        } catch (AttributeNotFoundException e) {
            node.addAttribute(new NodeGraphicAttribute(), "");
        }
        nodes.add(node);
    }

    // /**
    // * Adds a new edge and ensures all needed attributes exist.
    // */
    // private Edge addEdge(Node node, Node node2, boolean directed)
    // {
    // Edge newEdge = this.graph.addEdge(node, node2, directed);
    // try
    // {
    // newEdge.getAttribute(GraphicAttributeConstants.GRAPHICS);
    // }
    // catch (AttributeNotFoundException e)
    // {
    // newEdge.addAttribute(new EdgeGraphicAttribute(), "");
    // }
    //
    // return newEdge;
    // }

    /**
     * Sets the numOfNodes.
     * 
     * @param numOfNodes
     *            the numOfNodes to set.
     */
    public void setNumOfNodes(int numOfNodes) {
        nodesParam.setValue(numOfNodes);
    }

    /**
     * Sets the directed.
     * 
     * @param directed
     *            the directed to set.
     */
    public void setDirected(boolean directed) {
        isDirectedParam.setValue(directed);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        int numberOfNodes = nodesParam.getValue().intValue();
        boolean directed = (isDirectedParam.getValue()).booleanValue();
        String maxDeg = degree.getValue();

        if (maxDeg == "Ternary Tree") {
            CompleteTernaryTreeGraphGenerator terGen = new CompleteTernaryTreeGraphGenerator(
                    graph, numberOfNodes, directed);
            terGen.execute();
        } else if (maxDeg == "Tree of Degree 4") {
            Complete4TreeGraphGenerator terGen = new Complete4TreeGraphGenerator(
                    graph, numberOfNodes, directed);
            terGen.execute();
        } else if (maxDeg == "Tree of Degree 5") {
            CompletePentaTreeGraphGenerator terGen = new CompletePentaTreeGraphGenerator(
                    graph, numberOfNodes, directed);
            terGen.execute();
        }
        graph.getListenerManager().transactionFinished(this);
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
