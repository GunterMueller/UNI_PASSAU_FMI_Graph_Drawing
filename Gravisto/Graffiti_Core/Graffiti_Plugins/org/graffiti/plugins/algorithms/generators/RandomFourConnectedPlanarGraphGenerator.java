// =============================================================================
//
//   RandomGraphGenerator.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RandomFourConnectedPlanarGraphGenerator.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.generators;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.connectivity.Fourconnect;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedBicomp;
import org.graffiti.plugins.algorithms.planarity.TestedComponent;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * This generator creates a 4-connected internally triangulated planar graph
 * with n nodes and 4 nodes on the exterior face.
 */
public class RandomFourConnectedPlanarGraphGenerator extends AbstractGenerator {
    /** number of edges nodes to delete */
    private IntegerParameter edgesToDeleteParam;

    /** number of nodes */
    private IntegerParameter nodesParam;

    private BooleanParameter fourNodesParam;

    private int numOfNodes;
    private int numOfedgesToDelete;
    private boolean fourNodes;

    /**
     * Constructs a new instance.
     */
    public RandomFourConnectedPlanarGraphGenerator() {
        super();

        nodesParam = new IntegerParameter(new Integer(8), new Integer(0),
                new Integer(100), "number of nodes",
                "the number of nodes to generate");

        parameterList.addLast(nodesParam);

        edgesToDeleteParam = new IntegerParameter(
                new Integer(0),
                new Integer(0),
                new Integer(100),
                "edges to delete randomly",
                "The number of edges to delete from the maximal graph/map. If this number is negative, a random number of edges will be deleted.");
        parameterList.addLast(edgesToDeleteParam);

        fourNodesParam = new BooleanParameter(true, "One Face with 4 nodes",
                "At least 4 nodes on the exterior face (nessecary for MNN algorithm)");

        parameterList.addLast(fourNodesParam);

        addNodeLabelingOption();
        // addEdgeLabelingOption();
        addFormSelOption();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "Graph Generator: Random planar 4-connected Graph";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        numOfNodes = nodesParam.getInteger().intValue();
        numOfedgesToDelete = edgesToDeleteParam.getInteger().intValue();
        fourNodes = fourNodesParam.getBoolean().booleanValue();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        long start = System.currentTimeMillis();

        if (numOfNodes < 6) {
            numOfNodes = 6;

            if (fourNodes) {
                numOfNodes = 8;
            }
        }

        Node[] nodes = new Node[numOfNodes];
        ArrayList<Edge> edges = new ArrayList<Edge>();

        for (int i = 0; i < 6; i++) {

            nodes[i] = graph.addNode();
        }

        // create the initial graph
        edges.add(graph.addEdge(nodes[0], nodes[1], false));
        edges.add(graph.addEdge(nodes[0], nodes[2], false));
        edges.add(graph.addEdge(nodes[0], nodes[3], false));
        edges.add(graph.addEdge(nodes[0], nodes[5], false));

        edges.add(graph.addEdge(nodes[1], nodes[2], false));
        edges.add(graph.addEdge(nodes[1], nodes[3], false));
        edges.add(graph.addEdge(nodes[1], nodes[4], false));

        edges.add(graph.addEdge(nodes[2], nodes[4], false));
        edges.add(graph.addEdge(nodes[2], nodes[5], false));

        edges.add(graph.addEdge(nodes[3], nodes[4], false));
        edges.add(graph.addEdge(nodes[3], nodes[5], false));

        edges.add(graph.addEdge(nodes[4], nodes[5], false));

        for (int i = 6; i < numOfNodes; i++) {

            // choose a random edge
            int rand = getRandom(edges.size());
            Edge current = edges.get(rand);

            PlanarityAlgorithm planar = new PlanarityAlgorithm();

            planar.attach(graph);
            TestedGraph testedGraph = planar.getTestedGraph();
            TestedComponent tc = testedGraph.getTestedComponents().get(0);
            TestedBicomp tb = tc.getTestedBicomps().get(0);

            Node source = current.getSource();
            Node target = current.getTarget();

            ArrayList<Node> arr = new ArrayList<Node>();
            arr.addAll(tb.getAdjacencyList(source));
            int index = arr.indexOf(target);

            Node next1 = arr.get((index + 1) % arr.size());
            Node next2 = null;
            if (index > 0) {
                next2 = arr.get((index - 1));
            } else {
                next2 = arr.get(arr.size() - 1);
            }

            // add a new node instead of the chosen edge and connect it with
            // all the (neighbour) nodes in the face
            nodes[i] = graph.addNode();

            edges.add(graph.addEdge(nodes[i], source, false));
            edges.add(graph.addEdge(nodes[i], target, false));
            edges.add(graph.addEdge(nodes[i], next1, false));
            edges.add(graph.addEdge(nodes[i], next2, false));

            edges.remove(current);
            graph.deleteEdge(current);

        }

        if (fourNodes && numOfedgesToDelete == 0) {
            numOfedgesToDelete = 1;
        }

        deleteEdges(numOfedgesToDelete);

        long finish = System.currentTimeMillis();

        // Zeitausgabe
        String s = "Algorithm-Time: " + ((double) (finish - start) / 1000);

        System.out.println(s);

        // label the nodes
        if (nodeLabelParam.getBoolean().booleanValue()) {
            Collection<Node> nodeList = new LinkedList<Node>();
            Collections.addAll(nodeList, nodes);
            labelNodes(nodeList, startNumberParam.getInteger().intValue());
            formGraph(nodeList, form.getSelectedValue());
        }

    }

    /**
     * Deletes a specified number of nodes from the graph in that way, that the
     * graph still is 4-connected
     * 
     * @param num
     *            the number of edges that should be removed
     */
    private void deleteEdges(int num) {
        boolean stop = false;
        int numOfRemovedEdges = 0;

        for (int i = 0; i < num;) {

            Collection<Edge> allowedEdges = calculateAllowedEdges(graph
                    .getEdges());

            if (allowedEdges.size() <= 0) {
                stop = true;
            }

            System.out.println("Allowed Edges: " + allowedEdges.size() + "/"
                    + graph.getNumberOfEdges());

            int counter = 0;
            for (Edge e : allowedEdges) {
                counter++;

                if (counter >= allowedEdges.size()) {
                    stop = true;
                }
                graph.deleteEdge(e);
                Fourconnect fc = new Fourconnect();
                fc.attach(graph);
                fc.testFourconnect();
                if (!fc.isFourconnected()) {
                    graph.addEdgeCopy(e, e.getSource(), e.getTarget());
                } else {
                    i++;
                    numOfRemovedEdges++;
                    break;
                }

                if (stop) {
                    break;
                }
            }

            if (stop) {
                break;
            }
        }
        System.out.println(numOfRemovedEdges + " edges were removed");

    }

    /**
     * returns a collection of the edges that are allowed to be removed
     * 
     * @param allowed
     * @return a collection of the edges that are allowed to be removed
     */
    private Collection<Edge> calculateAllowedEdges(Collection<Edge> allowed) {

        Collection<Edge> allowedEdges = new LinkedList<Edge>();

        for (Edge e : allowed) {

            if (e.getSource().getNeighbors().size() > 4
                    && e.getTarget().getNeighbors().size() > 4) {
                allowedEdges.add(e);
            }
        }
        return allowedEdges;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
    }

    /**
     * Returns a random number between zero and the specified upper bound.
     * 
     * @param upperBound
     *            The upper bound of the random number.
     * 
     * @return A random number between zero and the specified upper bound.
     */
    private int getRandom(int upperBound) {
        return (int) (Math.random() * upperBound);
    }
}