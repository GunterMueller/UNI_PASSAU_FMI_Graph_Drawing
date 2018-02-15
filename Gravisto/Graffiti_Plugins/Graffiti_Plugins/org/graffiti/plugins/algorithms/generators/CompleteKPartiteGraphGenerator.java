// =============================================================================
//
//   CompleteKPartiteGraphGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CompleteKPartiteGraphGenerator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.StringParameter;

/**
 * This generator creates a k-partite graph. That means: There are k sets of
 * nodes and the generator creates edges between all edges except the edges in
 * the sets themselves.
 */
public class CompleteKPartiteGraphGenerator extends AbstractGenerator {

    /** list with the number of nodes */
    LinkedList<Integer> numOfNodesList;

    /** number of nodes in every part */
    private StringParameter kPartParam;

    /**
     * Constructs a new instance.
     */
    public CompleteKPartiteGraphGenerator() {
        super();
        addNodeLabelingOption();
        addEdgeLabelingOption();
        kPartParam = new StringParameter("2.3.2.", "number of nodes",
                "number of nodes in every k-part, format: <x.>*");
        parameterList.addFirst(kPartParam);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "K-Partite Graph Generator";
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        try {
            numOfNodesList = parseInput(kPartParam.getString());
        } catch (Exception e) {
            errors
                    .add("The input format of the nodes is not correct, format is: <x.>*");
        }

        for (Iterator<Integer> it = numOfNodesList.iterator(); it.hasNext();) {
            Integer temp = it.next();

            if (temp.compareTo(new Integer(0)) <= 0) {
                errors.add("Negative parameters are not allowed");
            }
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
        double xStart = 100.0;
        double yStart = 50.0;

        double xSpace = 100.0;
        double ySpace = 100.0;

        double x = xStart;

        // search the maximum number of nodes in a set
        int maxNodes = 0;

        for (Integer temp : numOfNodesList) {
            if (temp.intValue() > maxNodes) {
                maxNodes = temp.intValue();
            }
        }

        double yOffset = (maxNodes * ySpace) / 2.0;

        graph.getListenerManager().transactionStarted(this);

        LinkedList<Node[]> nodeArrays = new LinkedList<Node[]>();
        Collection<Edge> edges = new LinkedList<Edge>();

        for (Integer temp : numOfNodesList) {
            Node[] nodes = new Node[temp.intValue()];
            nodeArrays.add(nodes);

            double y;

            if (nodes.length == maxNodes) {
                y = yStart;
            } else {
                y = (yStart + yOffset) - ((ySpace * nodes.length) / 2.0);
            }

            for (int i = 0; i < nodes.length; i++) {
                nodes[i] = graph.addNode();

                CoordinateAttribute ca = (CoordinateAttribute) nodes[i]
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
                ca.setCoordinate(new Point2D.Double(x, y));
                y += ySpace;
            }

            x += xSpace;
        }

        for (int i = 0; i < nodeArrays.size(); i++) {
            Node[] nodes = nodeArrays.get(i);

            for (int j = i + 1; j < nodeArrays.size(); j++) {
                Node[] nodes2 = nodeArrays.get(j);

                if (nodes.length > nodes2.length) {
                    for (int k = 0; k < nodes.length; k++) {
                        for (int m = 0; m < nodes2.length; m++) {
                            edges
                                    .add(graph.addEdge(nodes[k], nodes2[m],
                                            false));
                        }
                    }
                } else {
                    for (int k = 0; k < nodes2.length; k++) {
                        for (int m = 0; m < nodes.length; m++) {
                            edges
                                    .add(graph.addEdge(nodes2[k], nodes[m],
                                            false));
                        }
                    }
                }
            }
        }

        graph.getListenerManager().transactionFinished(this);

        // label the nodes
        if (nodeLabelParam.getBoolean().booleanValue()) {
            Collection<Node> nodeList = new LinkedList<Node>();
            for (Iterator<Node[]> it = nodeArrays.iterator(); it.hasNext();) {
                Node[] nodes = it.next();
                Collections.addAll(nodeList, nodes);
            }
            labelNodes(nodeList, startNumberParam.getValue().intValue());
        }

        // label the edges
        if (edgeLabelParam.getBoolean().booleanValue()) {
            labelEdges(edges, edgeLabelNameParam.getString(), edgeMin
                    .getValue().intValue(), edgeMax.getValue().intValue());
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
        kPartParam.setValue("2.3.2.");
    }

    /**
     * Parses the input.
     * 
     * @param input
     *            The input to parse.
     * 
     * @return The parsed parameters.
     */
    private LinkedList<Integer> parseInput(String input) {
        LinkedList<Integer> numbersOfNodes = new LinkedList<Integer>();

        while (input.length() > 0) {
            int i = 0;
            int splitter = input.indexOf(".");

            if (splitter == -1) {
                splitter = input.length();
            }

            String arg = input.substring(i, splitter);
            numbersOfNodes.add(new Integer(Integer.parseInt(arg)));

            if (splitter <= input.length()) {
                input = input.substring(splitter + 1);
            } else {
                break;
            }
        }

        return numbersOfNodes;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
