package org.graffiti.plugins.algorithms.core;

import java.util.HashMap;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;
import org.graffiti.selection.Selection;

/**
 * This class computes the closeness centrality for each node and draws the
 * graph radial calling the Sugiyama-algorithm with the computed leveling.
 * 
 * @author Matthias H�llm�ller
 * 
 */
public class ClosenessAlgorithm extends AbstractAlgorithm implements Centrality {

    /**
     * the selected nodes
     */
    private Selection selection = new Selection();

    /**
     * centrality values
     */
    private HashMap<Node, Double> centralities = new HashMap<Node, Double>();

    /**
     * the constructor
     */
    public ClosenessAlgorithm() {
        super();
    }

    /**
     * returns the name of the algorithm
     */
    @SuppressWarnings("nls")
    public String getName() {
        return "Closeness-Zentralit�t";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    @SuppressWarnings("nls")
    public Parameter<?>[] getAlgorithmParameters() {

        SelectionParameter selParameter = new SelectionParameter("Selection",
                "selected node");
        selParameter.setSelection(new Selection("_temp_"));

        DoubleParameter minRadiusParameter = new DoubleParameter(
                new Double(1d), "minimum radius", "radius of the inner circle",
                new Double(0.5), new Double(5.0));

        DoubleParameter levelDistParameter = new DoubleParameter(new Double(1),
                "distance between levels", "distance between levels",
                new Double(0.5), new Double(5.0));

        IntegerParameter qualityParameter = new IntegerParameter(new Integer(
                500), new Integer(100), new Integer(2000), "quality factor",
                "quality of the spiral");

        IntegerParameter maxLevelParameter = new IntegerParameter(
                new Integer(9), new Integer(1), new Integer(9),
                "maximum number of levels", "maximum number of levels");

        return new Parameter[] { selParameter, minRadiusParameter,
                levelDistParameter, qualityParameter, maxLevelParameter };
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] parameters) {

        Params p = new Params();
        this.selection = ((SelectionParameter) parameters[0]).getSelection();
        p.setMinRadius(((DoubleParameter) parameters[1]).getDouble()
                .doubleValue());
        p.setLevelDist(((DoubleParameter) parameters[2]).getDouble()
                .doubleValue());
        p
                .setQuality(((IntegerParameter) parameters[3]).getInteger()
                        .intValue());
        p.setMaxLevel(((IntegerParameter) parameters[4]).getInteger()
                .intValue());
        p.setDirection(false);
        p.setCore(false);

    }

    /**
     * Checks the preconditions for the algorithm.
     */
    @Override
    @SuppressWarnings("nls")
    public void check() throws PreconditionException {
        PlanarityAlgorithm planar = new PlanarityAlgorithm();
        planar.attach(this.graph);
        TestedGraph testedGraph = planar.getTestedGraph();

        PreconditionException errors = new PreconditionException();

        if (this.graph == null) {
            errors.add("The graph instance may not be null.");

        } else if (this.graph.getNumberOfNodes() == 0) {

            errors.add("The graph is empty.");
        }

        if (testedGraph.getNumberOfComponents() > 1) {
            errors.add("The graph has to be connected.");
        }

        if (testedGraph.getNumberOfDoubleEdges() > 0) {
            errors.add("The graph contains double edges.");
        }

        if (testedGraph.getNumberOfLoops() > 0) {
            errors.add("The graph contains loops.");
        }

        if (this.selection.getNodes().size() > 1) {
            errors.add("The maximum number of selected nodes is one.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /**
     * executes the algorithm
     */
    public void execute() {

        // set shape to straight line to avoid an exception in SpiralShape.java
        for (Edge e : this.graph.getEdges()) {
            e.setString(GraphicAttributeConstants.SHAPE_PATH,
                    GraphicAttributeConstants.STRAIGHTLINE_CLASSNAME);
        }

        HashMap<Node, Double> closeness = new HashMap<Node, Double>();

        // get selected nodes
        LinkedList<Node> nodes = (LinkedList<Node>) this.selection.getNodes();

        // just one selected node - compute centrality according to it
        if (nodes.size() == 1) {
            closeness = getCentrality(nodes.getFirst());

            // nothing selected - compute the node with highest centrality value
            // and compute centrality according to it
        } else {
            HashMap<Node, Double> centralities = getCentrality();
            Node center = null;
            Double highestValue = 0d;
            for (Node n : centralities.keySet()) {
                if (centralities.get(n) > highestValue) {
                    highestValue = centralities.get(n);
                    center = n;
                }
            }
            closeness = getCentrality(center);
        }

        // execute sugiyama-algorithm with computed leveling
        Sugiyama s = new Sugiyama(this.graph, cluster(closeness));
        s.execute();
    }

    /**
     * resets static params
     */
    @Override
    public void reset() {
        Params p = new Params();
        p.setDefault();
    }

    /**
     * get the centrality values of each node
     */
    public HashMap<Node, Double> getCentrality() {
        for (Node n : this.graph.getNodes()) {
            getCentrality(n);
        }
        return this.centralities;
    }

    /**
     * compute the closeness centrality with an extension of bfs
     */
    public HashMap<Node, Double> getCentrality(Node s) {

        // queue
        LinkedList<Node> q = new LinkedList<Node>();

        // store if nodes and edges are marked - init as not marked
        HashMap<Node, Boolean> markedNodes = new HashMap<Node, Boolean>();
        HashMap<Edge, Boolean> markedEdges = new HashMap<Edge, Boolean>();
        for (Edge e : this.graph.getEdges()) {
            markedEdges.put(e, false);
        }
        for (Node n : this.graph.getNodes()) {
            markedNodes.put(n, false);
        }

        // bfs number
        HashMap<Node, Double> bfs = new HashMap<Node, Double>();

        // get adacency lists
        HashMap<Node, LinkedList<Edge>> adjList = new HashMap<Node, LinkedList<Edge>>();
        for (Node n : this.graph.getNodes()) {
            adjList.put(n, new LinkedList<Edge>());
        }
        for (Edge e : this.graph.getEdges()) {
            LinkedList<Edge> list = adjList.get(e.getSource());
            list.add(e);
            adjList.put(e.getSource(), list);

            list = adjList.get(e.getTarget());
            list.add(e);
            adjList.put(e.getTarget(), list);
        }

        // mark start node, init bfs number and put it in the queue
        markedNodes.put(s, true);
        bfs.put(s, 0.0);
        q.offer(s);

        // closeness centrality
        Double c = 0.0;

        // as long as there are nodes in the queue
        while (!q.isEmpty()) {

            // take the first node v of the queue
            Node v = q.poll();

            // for all incident edges which are not marked
            for (Edge e : adjList.get(v)) {
                if (!markedEdges.get(e)) {

                    // mark the edge
                    markedEdges.put(e, true);

                    // get adjacent node w
                    Node w;
                    if (v.equals(e.getSource())) {
                        w = e.getTarget();
                    } else {
                        w = e.getSource();
                    }

                    // if it is not marked - mark it, set bfs number, put it in
                    // the queue, and update centrality value
                    if (!markedNodes.get(w)) {
                        markedNodes.put(w, true);
                        bfs.put(w, bfs.get(v) + 1);
                        q.offer(w);
                        c += bfs.get(w);
                    }

                }

            }
        }

        // store centrality
        if (c != 0) {
            this.centralities.put(s, 1 / c);
        }

        return bfs;
    }

    /**
     * clusters the nodes according to their centrality-values to k clusters
     * 
     * @param c
     *            centrality values
     */
    public HashMap<Node, Integer> cluster(HashMap<Node, Double> c) {

        HashMap<Node, Integer> level = new HashMap<Node, Integer>();

        // get maximum level number
        Params p = new Params();
        int maxLevel = p.getMaxLevel() - 1;

        for (Node n : c.keySet()) {

            if (c.get(n) > maxLevel) {
                level.put(n, maxLevel);
            } else {
                level.put(n, c.get(n).intValue());
            }
        }

        return level;
    }

}
