package org.graffiti.plugins.algorithms.core;

import java.util.HashMap;
import java.util.Iterator;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * computes the core degrees of each node and draws the graph radial placing
 * each node on the level according to its core degree.
 * 
 * @author Matthias H�llm�ller
 */
public class CoreAlgorithm extends AbstractAlgorithm {

    /**
     * store the core-degrees
     */
    private HashMap<Node, Integer> core = new HashMap<Node, Integer>();

    /**
     * the constructor
     */
    public CoreAlgorithm() {
        super();
    }

    /**
     * returns the Name of the Algorithm
     */
    @SuppressWarnings("nls")
    public String getName() {
        return "Core";
    }

    /**
     * checks the preconditions for the algorithm
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
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    @SuppressWarnings("nls")
    public Parameter<?>[] getAlgorithmParameters() {

        IntegerParameter maxLevelParameter = new IntegerParameter(
                new Integer(9), new Integer(1), new Integer(9),
                "maximum level number", "maximum number of levels");

        BooleanParameter directionParameter = new BooleanParameter(true,
                "direction", "highest level in center");

        BooleanParameter colorParameter = new BooleanParameter(false, "color",
                "just color the nodes");

        DoubleParameter minRadiusParameter = new DoubleParameter(
                new Double(1d), "minimum radius", "radius of the inner circle",
                new Double(0.5), new Double(5.0));

        DoubleParameter levelDistParameter = new DoubleParameter(new Double(1),
                "distance between levels", "distance between levels",
                new Double(0.5), new Double(5.0));

        IntegerParameter qualityParameter = new IntegerParameter(new Integer(
                500), new Integer(100), new Integer(5000), "quality factor",
                "quality of the spiral");

        return new Parameter[] { minRadiusParameter, levelDistParameter,
                qualityParameter, maxLevelParameter, directionParameter,
                colorParameter };
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(org.graffiti.plugin.parameter.Parameter[])
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
        p.setDirection(((BooleanParameter) parameters[4]).getBoolean()
                .booleanValue());
        p.setColor(((BooleanParameter) parameters[5]).getBoolean()
                .booleanValue());
        p.setCore(true);
    }

    /**
     * resets the algorithm
     */
    @Override
    public void reset() {
        super.reset();
    }

    /**
     * executes the algorithm
     */
    public void execute() {

        // computes the core numbers of the graph
        computeCores();

        Sugiyama s = new Sugiyama(this.graph, this.core);
        Params p = new Params();

        // if color parameter is set - just color the nodes according to their
        // core degree - else execute sugiyama algorithm for a radial drawing
        if (p.getColor()) {
            s.justColor();
        } else {
            s.execute();
        }

    }

    /**
     * calculates the core-degrees
     */
    private void computeCores() {
        int size = this.graph.getNodes().size(); // size of the graph

        int[] deg = new int[size]; // array with core-degrees

        int num = 0; // counter

        int maxDeg = 0; // maximum degree

        Node[] order = new Node[size];

        HashMap<Node, Integer> number = new HashMap<Node, Integer>(size);

        // compute degrees of each vertex in deg[]
        Iterator<Node> nodesIterator = this.graph.getNodesIterator();
        while (nodesIterator.hasNext()) {
            Node n = nodesIterator.next();
            deg[num] = n.getNeighbors().size();

            // remember order of vertices
            number.put(n, num);
            order[num] = n;

            // remember maximum degree
            if (deg[num] > maxDeg) {
                maxDeg = deg[num];
            }

            num++;
        }

        // sort the vertices in increasing order of their degree
        int[] bin = new int[maxDeg + 1]; // position of the first vertex of
        // each degree
        int[] vert = new int[size]; // vertices sorted by their degrees
        int[] pos = new int[size]; // positions in vert[]

        // count how many vertices are in each bin (of each degree)
        for (int i = 0; i < size; i++) {
            bin[deg[i]] = bin[deg[i]] + 1;
        }

        // determine starting positions of each bin
        int start = 0;
        for (int i = 0; i <= maxDeg; i++) {
            num = bin[i];
            bin[i] = start;
            start = start + num;
        }

        // put vertices sorted by their degrees into array vert
        for (int i = 0; i < size; i++) {
            pos[i] = bin[deg[i]];
            vert[pos[i]] = i;
            bin[deg[i]] = bin[deg[i]] + 1;
        }

        // recover the starting positions of the bins
        for (int i = maxDeg; i > 0; i--) {
            bin[i] = bin[i - 1];
        }
        bin[0] = 0;

        // calculate core-numbers
        for (int i = 0; i < size; i++) {
            // order of vert[] (increasing degrees)
            Node currentNode = order[vert[i]];

            // core-number of current vertex is current degree
            this.core.put(currentNode, deg[vert[i]]);

            // decrease degree of all neighbours with higher degree
            // and move it one bin to the left
            Iterator<Node> neighboursIterator = currentNode
                    .getNeighborsIterator();
            while (neighboursIterator.hasNext()) {
                Node neighbour = neighboursIterator.next();
                int u = number.get(neighbour);

                if (deg[u] > deg[vert[i]]) {
                    int pu = pos[u];
                    int pw = bin[deg[u]];

                    // first vertex of bin with degree of u
                    int w = vert[pw];

                    // swap positions
                    if (u != w) {
                        pos[u] = pw;
                        pos[w] = pu;
                        vert[pu] = w;
                        vert[pw] = u;

                    }

                    // increase starting position of the bin
                    bin[deg[u]]++;
                    deg[u]--;
                }
            }
        }
    }

}
