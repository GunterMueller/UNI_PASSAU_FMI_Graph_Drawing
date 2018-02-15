package org.graffiti.plugins.algorithms.sugiyama.crossmin;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

import lpsolve.LpSolve;
import lpsolve.LpSolveException;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

public class ILPCrossMin extends AbstractAlgorithm implements CrossMinAlgorithm {

    private SugiyamaData data;

    private HashMap<String, Boolean> result;

    private boolean forbidType2;

    private static boolean lpsolve55Loaded = false;

    public ILPCrossMin() {
        result = new HashMap<String, Boolean>();
        if (!lpsolve55Loaded) {
            System.loadLibrary("lpsolve55");
            lpsolve55Loaded = true;
        }
    }

    public SugiyamaData getData() {
        return data;
    }

    public void setData(SugiyamaData data) {
        this.data = data;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)
                || algorithmType
                        .equals(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
    }

    public boolean supportsBigNodes() {
        return false;
    }

    public boolean supportsConstraints() {
        return false;
    }

    private String x(int a, int b, int c) {
        return "x_" + a + "_" + b + "_" + c;
    }

    private String c(int a, int b, int c, int d, int e) {
        return "c_" + a + "_" + b + "_" + c + "_" + d + "_" + e;
    }

    private String c(int a, int[] edge1, int[] edge2) {
        if (edge1[0] < edge2[0])
            return c(a, edge1[0], edge1[1], edge2[0], edge2[1]);
        else
            return c(a, edge2[0], edge2[1], edge1[0], edge1[1]);

    }

    public void execute() {
        PrintStream ps = null;
        ps = System.out;
        try {
            ps = new PrintStream("ILPCrossmin.lp");
        } catch (Exception e) {
            e.printStackTrace();
        }
        StringBuffer minString = new StringBuffer("min: ");
        StringBuffer intString = new StringBuffer();
        StringBuffer consString = new StringBuffer();
        StringBuffer optionalString = new StringBuffer();
        boolean type2Possible = true;
        for (int r = 0; r < data.getLayers().getNumberOfLayers(); r++) {
            ArrayList<Node> layer = data.getLayers().getLayer(r);
            for (int i = 0; i < layer.size(); i++) {
                Node iNode = layer.get(i);
                type2Possible = data.getDummyNodes().contains(iNode);
                for (int k = i + 1; k < layer.size(); k++) {
                    consString.append(x(r, i, k) + " <= 1;\n");
                    intString.append("int " + x(r, i, k) + ";\n");
                    for (int v = k + 1; v < layer.size(); v++) {
                        consString.append("0 <= " + x(r, i, k) + " + "
                                + x(r, k, v) + " - " + x(r, i, v) + " <= 1;\n");
                    }
                    Node kNode = layer.get(k);
                    if (!data.getDummyNodes().contains(kNode)) {
                        type2Possible = false;
                    }
                    int nextLayerIndex;
                    if (r == data.getLayers().getNumberOfLayers() - 1) {
                        nextLayerIndex = 0;
                    } else {
                        nextLayerIndex = r + 1;
                    }
                    for (Node jNode : iNode.getOutNeighbors()) {
                        for (Node lNode : kNode.getOutNeighbors()) {
                            if (jNode == lNode) {
                                continue;
                            }
                            if (!data.getDummyNodes().contains(jNode)) {
                                type2Possible = false;
                            }
                            if (!data.getDummyNodes().contains(lNode)) {
                                type2Possible = false;
                            }
                            int j = data.getLayers().getLayer(nextLayerIndex)
                                    .indexOf(jNode);
                            int l = data.getLayers().getLayer(nextLayerIndex)
                                    .indexOf(lNode);
                            if (forbidType2 && type2Possible) {
                                if (j < l) {
                                    consString.append(x(r, i, k) + " = "
                                            + x(nextLayerIndex, j, l) + ";\n");
                                } else {
                                    consString.append(x(r, i, k) + " = 1 - "
                                            + x(nextLayerIndex, l, j) + ";\n");
                                }
                            } else {
                                consString
                                        .append(c(r, i, j, k, l) + " <= 1;\n");
                                intString.append("int " + c(r, i, j, k, l)
                                        + ";\n");
                                minString.append(" + " + c(r, i, j, k, l));
                                if (j < l) {
                                    consString.append("-" + c(r, i, j, k, l)
                                            + " <= " + x(nextLayerIndex, j, l)
                                            + " - " + x(r, i, k) + ";\n");
                                    consString.append(x(nextLayerIndex, j, l)
                                            + " - " + x(r, i, k) + " <= "
                                            + c(r, i, j, k, l) + ";\n");
                                } else {
                                    consString.append("1 - " + c(r, i, j, k, l)
                                            + " <= " + x(nextLayerIndex, l, j)
                                            + " + " + x(r, i, k) + ";\n");
                                    consString.append(x(nextLayerIndex, l, j)
                                            + " + " + x(r, i, k) + " <= 1 + "
                                            + c(r, i, j, k, l) + ";\n");
                                }
                            }
                        }
                    }
                }
            }
        }
        minString.append(";\n");

        for (int layer = 0; layer < data.getLayers().getNumberOfLayers() - 1; layer++) {
            // System.out.println("upper layer "+layer);
            LinkedList<LinkedList<Node>> cycles = findCycles(layer);
            for (LinkedList<Node> c : cycles) {

                // System.out.println("next cycle " + cycleToString(c, layer));
                for (int pos = 0; pos < c.size() - 1; pos++) {
                    int[] edge = getEdge(c, pos, layer);
                    // System.out.println("layer "+layer+"   edge "+edge[0]+" "+edge[1]);
                    StringBuffer condition = new StringBuffer();
                    for (int other = 0; other < c.size() - 1; other++) {
                        int[] otherEdge = getEdge(c, other, layer);
                        if (edge[0] != otherEdge[0] && edge[1] != otherEdge[1]) {
                            condition.append(" + " + c(layer, edge, otherEdge));
                        }
                    }
                    int prevPos = pos - 1;
                    if (prevPos == -1) {
                        prevPos = c.size() - 2;
                    }
                    int nextPos = pos + 1;
                    if (nextPos == c.size() - 1) {
                        nextPos = 0;
                    }
                    int[] prevEdge = getEdge(c, prevPos, layer);
                    int[] nextEdge = getEdge(c, nextPos, layer);
                    condition.append(" + " + c(layer, prevEdge, nextEdge)
                            + " >= 1;\n");
                    optionalString.append(condition);
                    // System.out.println(condition);
                }
                StringBuffer condition2 = new StringBuffer();
                for (int pos1 = 0; pos1 < c.size() - 1; pos1++) {
                    for (int pos2 = pos1 + 1; pos2 < c.size() - 1; pos2++) {
                        if ((pos2 - pos1) % 2 == 0) {
                            condition2.append(" + "
                                    + c(layer, getEdge(c, pos1, layer),
                                            getEdge(c, pos2, layer)));
                        }
                    }
                }
                int cr = (c.size() - 3) / 2;
                if (cr == 1) {
                    condition2.append(" = " + cr + ";\n");
                } else {
                    condition2.append(" >= " + cr + ";\n");
                }
                // System.out.println(condition2);
                optionalString.append(condition2);
            }
        }

        // System.out.println(minString);
        // System.out.println(consString);
        // System.out.println(optionalString);
        // System.out.println(intString);

        ps.println(minString);
        ps.println(consString);
        ps.println(optionalString);
        ps.println(intString);
        ps.flush();
        ps.close();
        // System.out.println("start");
        try {
            // long start = System.currentTimeMillis();
            LpSolve lp = LpSolve.readLp("ILPCrossmin.lp", 1, "lp");
            lp.setPresolve(7, 5);
            lp.solve();
            // System.out.println("time: "+(System.currentTimeMillis() -
            // start));
            // System.out.println(lp.getObjective()+"\n");
            double[] variables = new double[lp.getNorigColumns()];
            lp.getVariables(variables);
            for (int i = 0; i < variables.length; i++) {
                String var = lp.getOrigcolName(i + 1);
                // System.out.println(var+" "+variables[i]);
                if (var.startsWith("x")) {
                    result.put(var, variables[i] == 1);
                }
            }
        } catch (LpSolveException e) {
            e.printStackTrace();
        }
        // System.out.println(result);
        for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
            ArrayList<Node> currentLayer = data.getLayers().getLayer(i);
            @SuppressWarnings("unchecked")
            ArrayList<Node> copy = (ArrayList<Node>) currentLayer.clone();
            Collections.sort(copy, new NodeComparator());
            currentLayer.clear();
            currentLayer.addAll(copy);
            for (int j = 0; j < currentLayer.size(); j++) {
                currentLayer.get(j).setDouble(SugiyamaConstants.PATH_XPOS, j);
            }
        }
        /*
         * int crossingsAfter = 0; for (int l = 0; l <
         * data.getLayers().getNumberOfLayers(); l++) { crossingsAfter += new
         * BilayerCrossCounter(graph, l, data).getNumberOfCrossings(); }
         * System.out.println("crossings "+crossingsAfter);
         */

    }

    public String getName() {
        return "ILP Crossmin";
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        BooleanParameter t2Param = new BooleanParameter(false,
                "Forbid type 2 conflicts",
                "If set, the optimal solution with no crossings of inner segments is computed");
        this.parameters = new Parameter[] { t2Param };
        return this.parameters;
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        forbidType2 = ((BooleanParameter) params[0]).getValue();
    }

    public class NodeComparator implements Comparator<Node> {

        public int compare(Node node0, Node node1) {
            if (node0 == node1)
                return 0;
            for (int r = 0; r < data.getLayers().getNumberOfLayers(); r++) {
                ArrayList<Node> l = data.getLayers().getLayer(r);
                if (!l.contains(node0)) {
                    continue;
                }
                if (!l.contains(node1)) {
                    continue;
                }
                int a0 = data.getLayers().getLayer(r).indexOf(node0);
                int a1 = data.getLayers().getLayer(r).indexOf(node1);
                if (a0 < a1)
                    return (result.get(x(r, a0, a1)) == true) ? -1 : 1;
                else
                    return (result.get(x(r, a1, a0)) == true) ? 1 : -1;

            }
            throw new IllegalStateException();
        }

    }

    private int[] getEdge(LinkedList<Node> cycle, int pos, int upperLayer) {
        int lowerLayer = upperLayer + 1;
        if (lowerLayer == data.getLayers().getNumberOfLayers()) {
            lowerLayer = 0;
        }

        Node firstNode = cycle.get(pos);
        Node secondNode = cycle.get(pos + 1);
        if (pos % 2 == 0)
            return new int[] { getIndex(firstNode, upperLayer),
                    getIndex(secondNode, lowerLayer) };
        else
            return new int[] { getIndex(secondNode, upperLayer),
                    getIndex(firstNode, lowerLayer) };
    }

    private Node getNode(int layer, int pos) {
        return data.getLayers().getLayer(layer).get(pos);
    }

    private LinkedList<LinkedList<Node>> appendCycle(
            LinkedList<Node> partialCycle, boolean down,
            LinkedList<Node> forbiddenNodes) {
        Node firstNode = partialCycle.getFirst();
        Node lastNode = partialCycle.getLast();
        LinkedList<LinkedList<Node>> result = new LinkedList<LinkedList<Node>>();
        Collection<Node> neighbors;
        if (down) {
            neighbors = lastNode.getOutNeighbors();
        } else {
            neighbors = lastNode.getInNeighbors();
        }
        for (Node n : neighbors) {
            if (n == firstNode && partialCycle.size() > 2) {
                LinkedList<Node> cycle = new LinkedList<Node>(partialCycle);
                cycle.add(n);
                result.add(cycle);
            } else if (!partialCycle.contains(n) && !forbiddenNodes.contains(n)) {
                LinkedList<Node> longerPartialCycle = new LinkedList<Node>(
                        partialCycle);
                longerPartialCycle.add(n);
                result.addAll(appendCycle(longerPartialCycle, !down,
                        forbiddenNodes));
            }
        }
        return result;
    }

    private LinkedList<LinkedList<Node>> findCycles(int upperLayer) {
        int lowerLayer = upperLayer + 1;
        if (lowerLayer == data.getLayers().getNumberOfLayers()) {
            lowerLayer = 0;
        }
        LinkedList<LinkedList<Node>> result = new LinkedList<LinkedList<Node>>();
        LinkedList<Node> forbiddenNodes = new LinkedList<Node>();
        for (int start = 0; start < data.getLayers().getLayer(upperLayer)
                .size(); start++) {
            Node startNode = getNode(upperLayer, start);
            LinkedList<Node> partialCycle = new LinkedList<Node>();
            partialCycle.add(startNode);
            result.addAll(appendCycle(partialCycle, true, forbiddenNodes));
            forbiddenNodes.add(startNode);
        }
        return result;
    }

    private int getIndex(Node n, int layer) {
        return data.getLayers().getLayer(layer).indexOf(n);
    }

    // private String cycleToString(LinkedList<Node> cycle, int upperLayer) {
    // int lowerLayer = upperLayer + 1;
    // if (lowerLayer == data.getLayers().getNumberOfLayers()) {
    // lowerLayer = 0;
    // }
    // String result = "";
    // boolean upper = true;
    // for(Node n : cycle) {
    // if (upper) {
    // result += getIndex(n, upperLayer)+" ";
    // } else {
    // result += getIndex(n, lowerLayer)+" ";
    // }
    // upper = !upper;
    // }
    // return result;
    // }

}
