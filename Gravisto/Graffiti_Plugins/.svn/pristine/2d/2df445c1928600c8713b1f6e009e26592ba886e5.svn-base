package org.graffiti.plugins.algorithms.sugiyama.crossmin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.gridsifting.benchmark.SugiyamaBenchmarkAdapter;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

public class BaryCenter extends AbstractAlgorithm implements CrossMinAlgorithm {

    private SugiyamaData data;
    private ArrayList<ArrayList<BarycenterNode>> layers;
    private HashMap<Node, BarycenterNode> nodeMap;

    private int iterations = 5;

    public void execute() {
        // Date start = new Date();

        graph.getListenerManager().transactionStarted(this);
        initialize();

        long startTime = System.nanoTime();

        if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
            // if the graph is cyclic, we iterate top-down, starting from a
            // random
            // level of the graph; afterwards we iterate bottom-up, starting
            // from another random layer of the graph
            for (int i = 0; i < iterations; i++) {
                // top-down
                int startingLayer = (int) (Math.floor((Math.random() * (layers
                        .size() - 1)) + 0.5d));
                for (int j = startingLayer; j < layers.size(); j++) {
                    processLayers(j, true);
                }
                if (startingLayer != 0) {
                    for (int j = 0; j < startingLayer; j++) {
                        processLayers(j, true);
                    }
                }
                // bottom up
                startingLayer = (int) (Math.floor((Math.random() * (layers
                        .size() - 1)) + 0.5d));

                for (int j = startingLayer; j >= 0; j--) {
                    processLayers(j, false);
                }
                if (startingLayer != layers.size() - 1) {
                    for (int j = layers.size() - 1; j > startingLayer; j--) {
                        processLayers(j, false);
                    }
                }
            }
        } else {
            for (int i = 0; i < iterations; i++) {
                // top down
                for (int j = 1; j < layers.size(); j++) {
                    processLayers(j, true);
                }
                // bottom up
                for (int j = layers.size() - 2; j >= 0; j--) {
                    processLayers(j, false);
                }
            }
        }

        long stopTime = System.nanoTime();
        data.putObject(SugiyamaBenchmarkAdapter.CROSSMIN_TIME_KEY, stopTime
                - startTime);

        cleanup();
        graph.getListenerManager().transactionFinished(this);

        // Date end = new Date();
        // System.out.println("Crossmin: " + (end.getTime() - start.getTime()) +
        // "ms");
    }

    private void processLayers(int layerNum, boolean topDown) {
        Iterator<BarycenterNode> nodeIter;
        BarycenterNode[] neighbors;
        HashSet<BarycenterNode> fixedPosition = new HashSet<BarycenterNode>();
        BarycenterNode tmpNode;
        int xpos;
        float barycenter;
        int[] lex;
        int counter;
        int degree;
        int oldPos;
        String lexString;
        BaryCenterNodeComparator comp = new BaryCenterNodeComparator(data);

        nodeIter = layers.get(layerNum).iterator();

        while (nodeIter.hasNext()) {

            tmpNode = nodeIter.next();
            barycenter = 0;
            lexString = new String();

            if (topDown) {
                lex = new int[tmpNode.inDegree];
                neighbors = tmpNode.inNeighbors;
            } else {
                lex = new int[tmpNode.outDegree];
                neighbors = tmpNode.outNeighbors;
            }

            counter = -1;

            // Calculate the barycenter-value for each node on this layer. The
            // barycenter is defined as:
            // (sum of the xpos' of all incoming neighbors) / indegree
            // And write a string at the node used to compare two nodes that
            // have the same barycenter-value
            for (int i = 0; i < neighbors.length; i++) {

                counter++;
                xpos = neighbors[i].xpos;
                lex[counter] = xpos;
                barycenter += xpos;

            }
            if (topDown) {
                degree = tmpNode.inDegree;
            } else {
                degree = tmpNode.outDegree;
            }

            // if the in/out-degree is 0, keep the old position of this node
            if (degree == 0) {
                fixedPosition.add(tmpNode);

                oldPos = tmpNode.xpos;
                tmpNode.oldPos = oldPos;

                barycenter = Float.POSITIVE_INFINITY;
            } else {
                barycenter /= degree;
            }

            tmpNode.barycenter = barycenter;

            // Sort the to-be String lex, as it has to be sorted in an
            // ascending order
            Arrays.sort(lex);
            for (int j = 0; j < lex.length; j++) {
                lexString += lex[j] + ",";
            }

            tmpNode.lex = lexString;
        }

        // Sort the layer according to barycenter
        Collections.sort(layers.get(layerNum), comp);

        if (!fixedPosition.isEmpty()) {
            nodeIter = fixedPosition.iterator();
            while (nodeIter.hasNext()) {
                tmpNode = nodeIter.next();
                layers.get(layerNum).remove(tmpNode);
                layers.get(layerNum).add(tmpNode.oldPos, tmpNode);
            }
        }

        // Save the order (i.e. the node's xpos) on the node
        for (int j = 0; j < layers.get(layerNum).size(); j++) {
            tmpNode = layers.get(layerNum).get(j);
            tmpNode.xpos = j;
        }
    }

    private void cleanup() {
        NodeLayers originalLayers = data.getLayers();
        ArrayList<Node> currentOriginalLayer;
        ArrayList<BarycenterNode> currentLayer;
        BarycenterNode node;

        for (int i = 0; i < originalLayers.getNumberOfLayers(); i++) {
            currentOriginalLayer = originalLayers.getLayer(i);
            currentLayer = layers.get(i);

            for (int j = 0; j < currentLayer.size(); j++) {
                node = currentLayer.get(j);
                currentOriginalLayer.set(j, node.node);

                node.node.setDouble(SugiyamaConstants.PATH_XPOS, j);
            }
        }
    }

    private void initialize() {
        layers = new ArrayList<ArrayList<BarycenterNode>>();
        ArrayList<BarycenterNode> currentLayer;
        ArrayList<Node> currentOriginalLayer;
        NodeLayers originalLayers = data.getLayers();
        BarycenterNode node;
        Node realNode;
        nodeMap = new HashMap<Node, BarycenterNode>((int) (data.getGraph()
                .getNumberOfNodes() * 1.25));

        // create barycenternodes for the real gravisto nodes
        for (int i = 0; i < originalLayers.getNumberOfLayers(); i++) {
            currentLayer = new ArrayList<BarycenterNode>();
            layers.add(currentLayer);
            currentOriginalLayer = originalLayers.getLayer(i);

            for (int j = 0; j < currentOriginalLayer.size(); j++) {
                realNode = currentOriginalLayer.get(j);
                node = new BarycenterNode(realNode);
                node.xpos = j;
                nodeMap.put(realNode, node);
                currentLayer.add(node);
            }
        }

        // initialize the neighbors of each barycenternode
        for (int i = 0; i < layers.size(); i++) {
            currentLayer = layers.get(i);
            for (int j = 0; j < currentLayer.size(); j++) {
                node = currentLayer.get(j);
                Iterator<Node> nodeIter = node.node.getInNeighborsIterator();
                Node neighbor;

                for (int k = 0; k < node.inDegree; k++) {
                    neighbor = nodeIter.next();
                    node.inNeighbors[k] = nodeMap.get(neighbor);
                }

                nodeIter = node.node.getOutNeighborsIterator();
                for (int k = 0; k < node.outDegree; k++) {
                    neighbor = nodeIter.next();
                    node.outNeighbors[k] = nodeMap.get(neighbor);
                }
            }
        }
    }

    public SugiyamaData getData() {
        return this.data;
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

    public String getName() {
        return "Barycenter";
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter iterations = new IntegerParameter(10, "Iterations",
                "Iterations of the cross-min-algorithm", 1, 100, 1,
                Integer.MAX_VALUE);
        BooleanParameter altLex = new BooleanParameter(false, "Use alternative"
                + " lex",
                "Use alternative method to compare Strings lexicographically");

        this.parameters = new Parameter[] { iterations, altLex };
        return this.parameters;
    }

    /**
     * Setter-method to store an array of parameters
     * 
     * @param params
     *            The parameters to be stored
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.iterations = ((IntegerParameter) params[0]).getValue();
        if (data != null) {
            data.setAlternateLex(((BooleanParameter) params[1]).getValue());
        }

    }

    private class BarycenterNode {
        private Node node;
        private float barycenter;
        private int xpos;
        private int oldPos;
        private String lex;
        private int inDegree;
        private int outDegree;
        private BarycenterNode[] inNeighbors;
        private BarycenterNode[] outNeighbors;

        public BarycenterNode(Node n) {
            node = n;
            inDegree = n.getInDegree();
            outDegree = n.getOutDegree();

            inNeighbors = new BarycenterNode[inDegree];
            outNeighbors = new BarycenterNode[outDegree];
            lex = "";
        }

    }

    public class BaryCenterNodeComparator implements Comparator<BarycenterNode> {

        private float n1_barycenter;

        private float n2_barycenter;

        private String n1_lex;

        private String n2_lex;

        private SugiyamaData data;

        public BaryCenterNodeComparator(SugiyamaData d) {
            this.data = d;
        }

        /**
         * Compares two nodes
         */
        public int compare(BarycenterNode n1, BarycenterNode n2) {
            int lex1_size = 0;
            int lex2_size = 0;

            n1_barycenter = n1.barycenter;
            n2_barycenter = n2.barycenter;

            if (n1_barycenter < n2_barycenter)
                return -1;
            else if (n1_barycenter > n2_barycenter)
                return 1;
            else {
                n1_lex = n1.lex;
                n2_lex = n2.lex;

                if (n1_lex == null || n2_lex == null)
                    return 0;
                else {
                    if (data.getAlternateLex()) {
                        for (int i = 0; i < n1_lex.length(); i++) {
                            if (n1_lex.charAt(i) == ',') {
                                lex1_size++;
                            }
                        }
                        for (int i = 0; i < n2_lex.length(); i++) {
                            if (n2_lex.charAt(i) == ',') {
                                lex2_size++;
                            }
                        }
                        if (lex1_size < lex2_size)
                            return -1;
                        else if (lex1_size > lex2_size)
                            return 1;
                        else
                            return n1_lex.compareTo(n2_lex);
                    } else
                        return n1_lex.compareTo(n2_lex);
                }
            }
        }

    }
}