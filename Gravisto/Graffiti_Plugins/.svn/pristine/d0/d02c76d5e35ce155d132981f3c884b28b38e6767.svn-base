// =============================================================================
//
//   LPLevelling.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.levelling;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SimpleGraph;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class LPLevelling extends AbstractLevellingAlgorithm implements
        LevellingAlgorithm {
    private static final String NAME = "LP levelling";

    private SugiyamaData data;

    private int nodeCount;

    private SimpleGraph simpleGraph;

    private boolean useThinLevels;

    @Override
    public SugiyamaData getData() {
        return data;
    }

    @Override
    public void setData(SugiyamaData data) {
        this.data = data;
    }

    @Override
    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

    @Override
    public boolean supportsBigNodes() {
        return false;
    }

    @Override
    public boolean supportsConstraints() {
        return false;
    }

    @Override
    public void execute() {
        simpleGraph = new SimpleGraph(graph);

        nodeCount = simpleGraph.getNodeCount();
        int edgeCount = simpleGraph.getEdgeCount();
        final double[] coeffs = new double[nodeCount];
        final int[] degreeDiff = new int[nodeCount];

        try {
            int[] cols = new int[nodeCount];

            LpSolve solver = LpSolve.makeLp(0, nodeCount);
            solver.setAddRowmode(true);

            for (int n = 0; n < nodeCount; n++) {
                addNodeConstraint(solver, n, cols, coeffs);
            }

            for (int e = 0; e < edgeCount; e++) {
                addEdgeConstraint(solver, e, cols, coeffs);
            }

            setObjFunc(solver, cols, coeffs, degreeDiff);

            solver.setAddRowmode(false);

            solver.setMinim();

            solver.setVerbose(LpSolve.IMPORTANT);

            solver.solve();

            solver.getVariables(coeffs);

            solver.deleteLp();
        } catch (LpSolveException e) {
            throw new RuntimeException(e);
        }

        int[] levelByNode = new int[nodeCount];
        int levelCount;

        if (useThinLevels) {
            levelCount = nodeCount;
            Integer nodes[] = new Integer[nodeCount];
            for (int n = 0; n < nodeCount; n++) {
                nodes[n] = n;
            }
            Arrays.sort(nodes, new Comparator<Integer>() {
                @Override
                public int compare(Integer node1, Integer node2) {
                    int comp = Double.valueOf(coeffs[node1]).compareTo(
                            coeffs[node2]);

                    if (comp != 0)
                        return comp;

                    return Integer.valueOf(degreeDiff[node1]).compareTo(
                            degreeDiff[node2]);
                }
            });

            for (int level = 0; level < levelCount; level++) {
                int n = nodes[level];
                levelByNode[n] = level;
            }
        } else {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;

            for (int n = 0; n < nodeCount; n++) {
                int level = (int) Math.round(coeffs[n]);
                min = Math.min(min, level);
                max = Math.max(max, level);
            }

            for (int n = 0; n < nodeCount; n++) {
                levelByNode[n] = ((int) Math.round(coeffs[n])) - min;
            }

            levelCount = max - min + 1;
        }

        debugCheckOrder(levelByNode);

        export(levelByNode, levelCount);
    }

    private void addNodeConstraint(LpSolve solver, int n, int[] cols,
            double[] coeffs) throws LpSolveException {
        cols[0] = n + 1;
        coeffs[0] = 1.0;
        solver.addConstraintex(1, coeffs, cols, LpSolve.GE, 0.0);

        cols[0] = n + 1;
        coeffs[0] = 1.0;
        solver.addConstraintex(1, coeffs, cols, LpSolve.LE, nodeCount - 1);
    }

    private void addEdgeConstraint(LpSolve solver, int e, int[] cols,
            double[] coeffs) throws LpSolveException {
        cols[0] = simpleGraph.getSource(e) + 1;
        coeffs[0] = -1.0;

        cols[1] = simpleGraph.getTarget(e) + 1;
        coeffs[1] = 1.0;

        solver.addConstraintex(2, coeffs, cols, LpSolve.GE, 1.0);
    }

    private void setObjFunc(LpSolve solver, int[] cols, double[] coeffs,
            int[] degreeDiffs) throws LpSolveException {
        for (int n = 0; n < nodeCount; n++) {
            cols[n] = n + 1;
            int degreeDiff = simpleGraph.getOutEdges(n).length
                    - simpleGraph.getInEdges(n).length;
            coeffs[n] = -degreeDiff;
            degreeDiffs[n] = degreeDiff;
        }

        solver.setObjFnex(nodeCount, coeffs, cols);
    }

    private void debugCheckOrder(int[] levelByNode) {
        for (Edge edge : graph.getEdges()) {
            int e = simpleGraph.getIndex(edge);
            int s = simpleGraph.getSource(e);
            int t = simpleGraph.getTarget(e);
            if (s != simpleGraph.getIndex(edge.getSource()))
                throw new IllegalStateException("Wrong source index assignment");
            if (t != simpleGraph.getIndex(edge.getTarget()))
                throw new IllegalStateException("Wrong target index assignment");
            if (levelByNode[s] >= levelByNode[t])
                throw new IllegalStateException("Illegal level assignment.");
        }
    }

    private void export(int[] levelByNode, int levelCount) {
        graph.getListenerManager().transactionStarted(this);

        NodeLayers layers = data.getLayers();
        for (int level = 0; level < levelCount; level++) {
            layers.addLayer();
        }

        for (int n = 0; n < nodeCount; n++) {
            layers.getLayer(levelByNode[n]).add(simpleGraph.getNode(n));
        }

        HashSet<Node> dummies;
        dummies = addDummies(data);
        data.setDummyNodes(dummies);

        for (int level = 0; level < nodeCount; level++) {
            for (Node node : layers.getLayer(level)) {
                node.setInteger(SugiyamaConstants.PATH_LEVEL, level);
            }
        }

        graph.getListenerManager().transactionFinished(this);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected Parameter<?>[] getAlgorithmParameters() {
        return new Parameter<?>[] { new IntegerParameter(1, "Nodes / Layer", "") };
    }

    @Override
    protected void setAlgorithmParameters(Parameter<?>[] params) {
        useThinLevels = ((IntegerParameter) params[0]).getInteger() == 1;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
