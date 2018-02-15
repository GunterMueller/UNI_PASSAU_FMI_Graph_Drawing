// =============================================================================
//
//   PermutationGraphAlgorithm.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.permutationgraph;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.AlgorithmResult;
import org.graffiti.plugin.algorithm.CalculatingAlgorithm;
import org.graffiti.plugin.algorithm.DefaultAlgorithmResult;
import org.graffiti.plugin.algorithm.PreconditionException;

/**
 * @author Emanuel Berndl
 * @version $Revision$ $Date$
 */
public class PermutationGraphAlgorithm extends AbstractAlgorithm implements CalculatingAlgorithm {

    private int[] permutation;
    private boolean correctPermutation;
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void check() throws PreconditionException {
        PreconditionException errors;
        errors = new PreconditionException();
        
        if(!graph.isDirected()) {
            errors.add("The graph may not be directed.");
        }
        
        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        Graph normalGraph = graph;

        // Compute the complement for the graph
        Graph complement = PermutationGraphChangings
                .complementGraph(normalGraph);

        // Transitively orient both the graph and its complement
        PermutationTransitiveOrientation.transitivelyOrientGraph(normalGraph);
        PermutationTransitiveOrientation.transitivelyOrientGraph(complement);

        // Combine the graph with its complement, and the inverse of the graph
        // with the complement
        Graph combineNormalWithComplement = PermutationGraphChangings
                .combineGraphs(normalGraph, complement);
        Graph inverseGraph = PermutationGraphChangings.invertGraph(normalGraph);
        Graph combineInverseWithComplement = PermutationGraphChangings
                .combineGraphs(inverseGraph, complement);

        // Compute topological order for both combined graphs
        List<Node> topologicalOrderOne = PermutationTopologicalSort
                .topSort(combineNormalWithComplement);
        List<Node> topologicalOrderTwo = PermutationTopologicalSort
                .topSort(combineInverseWithComplement);

        // Compute the permutation
        permutation = PermutationCalc.calculatePermutation(
                combineNormalWithComplement, combineInverseWithComplement,
                topologicalOrderOne, topologicalOrderTwo);

        correctPermutation = checkPermutation(permutation, graph);
        
        if(correctPermutation) {
            
            this.graph.getListenerManager().transactionStarted(this);
            
            // Clear the edges from the graph, they are not needed in the
            // permutation diagramm
            List<Edge> edges = new ArrayList<Edge>(graph.getEdges());
            for (Edge edge : edges) {
                graph.deleteEdge(edge);
            }
            
            List<Node> nodes = graph.getNodes();
            int amountOfNodes = nodes.size();
            Node[] bottomNodes = new Node[nodes.size()];
            
            for (int i = 0; i < amountOfNodes; i++) {
                // Place the nodes of the graph ascending in a line
                Node topNode = nodes.get(i);
                ((CoordinateAttribute) topNode
                        .getAttribute(GraphicAttributeConstants.COORD_PATH))
                        .setCoordinate(new Point2D.Double(0 + (i * 50), 0));
                NodeLabelAttribute label = new NodeLabelAttribute("permutation");
                topNode.addAttribute(label, "");
                label.setLabel("" + (i + 1));
                
                // Place a corresponding node on a line below
                bottomNodes[i] = graph.addNode();
                ((CoordinateAttribute) bottomNodes[i]
                                                   .getAttribute(GraphicAttributeConstants.COORD_PATH))
                                                   .setCoordinate(new Point2D.Double(0 + (i * 50), 100));
                NodeLabelAttribute label2 = new NodeLabelAttribute("permutation");
                bottomNodes[i].addAttribute(label2, "");
                label2.setLabel("" + (permutation[i] + 1));
            }
            
            // Change node shape to circles
            for (Node node : nodes) {
                StringAttribute attr = (StringAttribute) (node
                        .getAttribute(GraphicAttributeConstants.SHAPE_PATH));
                attr.setString(GraphicAttributeConstants.CIRCLE_CLASSNAME);
            }
            
            // Add an edge between corresponding node pairs
            for (int i = 0; i < permutation.length; i++) {
                graph.addEdge(bottomNodes[i], nodes.get(permutation[i]), false);
            }
            
            this.graph.getListenerManager().transactionFinished(this);
        } else {
//            System.out.println("Graph is no permutation Graph.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Permutation graph algorithm";
    }

    /**
     * The algorithm to get the permutation for a given undirected graph fails
     * to recognize, if a certain graph CAN NOT be presented in a permutation
     * diagram, because it is no permutation graph. So this method checks the
     * calculated permutation if the given graph can be reproduced according to
     * the permutation.
     * 
     * @param permutation
     *            The calculated permutation.
     * @param graph
     *            The underlying graph.
     * @return True, if the permutation corresponds correctly to the graph.
     */
    private boolean checkPermutation(int[] permutation, Graph graph) {
        if(permutation != null) {
            
            boolean rightPermutation = true;
            
            int amountOfEdges = 0;
            
            for(int i = 0; i < permutation.length && rightPermutation; i++) {
                for(int j = i + 1; j < permutation.length; j++) {
                    if(permutation[i] > permutation[j]) {
                        amountOfEdges++;                        
//                    if(!graph.getEdges(graph.getNodes().get(permutation[i]), graph.getNodes().get(permutation[j])).isEmpty()) {
//                    } else {
//                        rightPermutation = false;
//                        break;
//                    }
                    }
                }
            }
            
            if(amountOfEdges != graph.getEdges().size()) {
                rightPermutation = false;
            }
            
            return rightPermutation;
        } else {
            return false;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AlgorithmResult getResult() {
        AlgorithmResult result = new DefaultAlgorithmResult();
        String text;
        if(correctPermutation) {
            text = "Graph is a permutationgraph.";
        } else {
            text = "Graph is not a permutationgraph.";
        }
        result.setComponentForJDialog(text);
        return result;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
