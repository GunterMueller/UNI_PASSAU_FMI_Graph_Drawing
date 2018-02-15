package org.graffiti.plugins.algorithms.core;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * This class computes the betweenness centrality for each node and draws the
 * graph radial calling the Sugiyama-algorithm with the computed leveling
 * 
 * @author Matthias H�llm�ller
 * 
 */
public class BetweennessAlgorithm extends AbstractAlgorithm implements
        Centrality {

    /**
     * the constructor
     */
    public BetweennessAlgorithm() {
        super();
    }

    /**
     * returns the name of the algorithm
     */
    public String getName() {
        return "Betweenness-Zentralit�t"; //$NON-NLS-1$
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    @SuppressWarnings("nls")
    public Parameter<?>[] getAlgorithmParameters() {

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
                new Integer(2), new Integer(1), new Integer(9),
                "number of levels", "number of levels");

        return new Parameter[] { minRadiusParameter, levelDistParameter,
                qualityParameter, maxLevelParameter };
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] parameters) {

        Params p = new Params();
        p.setMinRadius(((DoubleParameter) parameters[0]).getDouble()
                .doubleValue());
        p.setLevelDist(((DoubleParameter) parameters[1]).getDouble()
                .doubleValue());
        p
                .setQuality(((IntegerParameter) parameters[2]).getInteger()
                        .intValue());
        p.setMaxLevel(((IntegerParameter) parameters[3]).getInteger()
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

        if (testedGraph.getNumberOfDoubleEdges() > 0) {
            errors.add("The graph contains double edges.");
        }

        if (testedGraph.getNumberOfLoops() > 0) {
            errors.add("The graph contains loops.");
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

        // compute betweenness centrality
        HashMap<Node, Double> betweenness = new HashMap<Node, Double>();
        betweenness = getCentrality();

        // execute sugiyama-algorithm with computed leveling
        Sugiyama s = new Sugiyama(this.graph, cluster(betweenness));
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
     * calculates the centrality values for node s
     */
    public HashMap<Node, Double> getCentrality() {

        // init centralities with 0
        HashMap<Node, Double> centralities = new HashMap<Node, Double>();
        for (Node n : this.graph.getNodes()) {
            centralities.put(n, 0.0);
        }

        // init data structures
        Stack<Node> stack = new Stack<Node>();
        LinkedList<Node> q = new LinkedList<Node>();
        HashMap<Node, LinkedList<Node>> pred = new HashMap<Node, LinkedList<Node>>();
        HashMap<Node, Double> numberOfShortestPaths = new HashMap<Node, Double>();
        HashMap<Node, Double> d = new HashMap<Node, Double>();
        HashMap<Node, Double> dependencies = new HashMap<Node, Double>();

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

        // compute centrality for each node
        for (Node s : this.graph.getNodes()) {

            // initialize data structures
            for (Node n : this.graph.getNodes()) {
                pred.put(n, new LinkedList<Node>());
                numberOfShortestPaths.put(n, 0d);
                d.put(n, -1d);
            }
            stack.clear();
            q.clear();
            numberOfShortestPaths.put(s, 1d);
            d.put(s, 0d);
            q.offer(s);

            // as long as there are nodes in the queue
            while (!q.isEmpty()) {

                // take the first node v of the queue and put it on the stack
                Node v = q.poll();
                stack.push(v);

                // for all incident edges which are not marked
                for (Edge e : adjList.get(v)) {

                    // get adjacent node w
                    Node w;
                    if (v.equals(e.getSource())) {
                        w = e.getTarget();
                    } else {
                        w = e.getSource();
                    }

                    // w found the first time?
                    if (d.get(w) < 0) {
                        q.offer(w);
                        d.put(w, d.get(v) + 1);
                    }

                    // shortest path of w via v?
                    if (d.get(w) == d.get(v) + 1) {
                        numberOfShortestPaths.put(w, numberOfShortestPaths
                                .get(w)
                                + numberOfShortestPaths.get(v));
                        LinkedList<Node> list = pred.get(w);
                        list.add(v);
                        pred.put(w, list);
                    }

                }

            }

            // set dependencies to 0
            for (Node n : this.graph.getNodes()) {
                dependencies.put(n, 0d);
            }

            // stack returns nodes in order of non-increasing distance from s
            while (!stack.isEmpty()) {
                Node w = stack.pop();

                // for each predecessor..
                for (Node v : pred.get(w)) {

                    // calculate dependency..
                    dependencies.put(v, dependencies.get(v)
                            + numberOfShortestPaths.get(v)
                            / numberOfShortestPaths.get(w)
                            * (1 + dependencies.get(w)));

                    // and add it to centrality value
                    if (!w.equals(s)) {
                        centralities.put(w, centralities.get(w)
                                + dependencies.get(w));
                    }
                }

            }
        }

        return centralities;
    }

    /**
     * clusters the nodes according to their centrality-values to k levels
     * 
     * @param c
     *            centrality values
     */
    public HashMap<Node, Integer> cluster(HashMap<Node, Double> c) {

        HashMap<Node, Integer> level = new HashMap<Node, Integer>();

        // get maximum level number
        Params p = new Params();
        int maxLevel = p.getMaxLevel() - 1;

        // get minimum and maximum value and range of centrality values
        Double range = 0.0;
        Double max = 0.0;
        Double min = Double.MAX_VALUE;
        for (Double d : c.values()) {
            if (d > max) {
                max = d;
            }
            if (d < min) {
                min = d;
            }
        }
        range = max - min;

        // set the level number
        for (Node n : c.keySet()) {
            level.put(n,
                    maxLevel
                            - Math.round((float) ((c.get(n) - min)
                                    * (maxLevel - 1) / range)));

            // only nodes with maximum value get on first level - exception just
            // one level
            if (c.get(n) == max || maxLevel == 0) {
                level.put(n, 0);
            }

        }

        return level;
    }
}
