package org.graffiti.plugins.algorithms.sugiyama.crossmin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

public class Median extends AbstractAlgorithm implements CrossMinAlgorithm {

    private SugiyamaData data;
    private ArrayList<ArrayList<MedianNode>> layers;
    private HashMap<Node, MedianNode> nodeMap;

    private int iterations = 5;

    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        initialize();

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

        cleanup();
        graph.getListenerManager().transactionFinished(this);

        // Date end = new Date();
        // System.out.println("Crossmin: " + (end.getTime() - start.getTime()) +
        // "ms");
    }

    private void sortAdjacenciesTopDown(int layerNum) {
        ArrayList<MedianNode> layer, otherLayer;
        layer = layers.get(layerNum);

        if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)
                && layerNum == 0) {
            otherLayer = layers.get(layers.size() - 1);
        } else {
            otherLayer = layers.get(layerNum - 1);
        }
        LazyMedianNodeArrayList swap;

        for (MedianNode n : layer) {
            swap = n.inNeighbors;
            n.inNeighbors = n.inNeighborsOld;
            n.inNeighbors.clear();
            n.inNeighborsOld = swap;
        }
        MedianNode n;
        for (int p = 0; p < otherLayer.size(); p++) {
            n = otherLayer.get(p);
            for (int i = 0; i < n.outNeighbors.elementCount; i++) {
                n.outNeighbors.get(i).inNeighbors.add(n);
            }
        }
    }

    private void sortAdjacenciesBottomUp(int layerNum) {
        ArrayList<MedianNode> layer, otherLayer;
        layer = layers.get(layerNum);
        if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)
                && layerNum == layers.size() - 1) {
            otherLayer = layers.get(0);
        } else {
            otherLayer = layers.get(layerNum + 1);
        }
        LazyMedianNodeArrayList swap;

        for (MedianNode n : layer) {
            swap = n.outNeighbors;
            n.outNeighbors = n.outNeighborsOld;
            n.outNeighbors.clear();
            n.outNeighborsOld = swap;
        }
        MedianNode n;
        for (int p = 0; p < otherLayer.size(); p++) {
            n = otherLayer.get(p);
            for (int i = 0; i < n.inNeighbors.elementCount; i++) {
                n.inNeighbors.get(i).outNeighbors.add(n);
            }
        }
    }

    private void processLayers(int layerNum, boolean topDown) {
        if (topDown) {
            sortAdjacenciesTopDown(layerNum);
        } else {
            sortAdjacenciesBottomUp(layerNum);
        }

        Iterator<MedianNode> nodeIter;
        MedianNode tmpNode;
        MedianNodeComparator comp = new MedianNodeComparator();

        nodeIter = layers.get(layerNum).iterator();

        while (nodeIter.hasNext()) {
            tmpNode = nodeIter.next();

            if (topDown) {
                if (tmpNode.inDegree == 0) {
                    tmpNode.median = 0;
                } else if (tmpNode.inDegree % 2 == 0) {
                    tmpNode.median = tmpNode.inNeighbors
                            .get((tmpNode.inDegree / 2) - 1).xpos;
                    tmpNode.median += tmpNode.inNeighbors
                            .get((tmpNode.inDegree / 2)).xpos;
                    tmpNode.median /= 2;
                } else {
                    tmpNode.median = tmpNode.inNeighbors
                            .get((tmpNode.inDegree - 1) / 2).xpos;
                }
            } else {
                if (tmpNode.outDegree == 0) {
                    tmpNode.median = 0;
                } else if (tmpNode.outDegree % 2 == 0) {
                    tmpNode.median = tmpNode.outNeighbors
                            .get((tmpNode.outDegree / 2) - 1).xpos;
                    tmpNode.median += tmpNode.outNeighbors
                            .get((tmpNode.outDegree / 2)).xpos;
                    tmpNode.median /= 2;
                } else {
                    tmpNode.median = tmpNode.outNeighbors
                            .get((tmpNode.outDegree - 1) / 2).xpos;
                }
            }
        }

        // Sort the layer according to median
        Collections.sort(layers.get(layerNum), comp);

        // Save the order (i.e. the node's xpos) on the node
        for (int j = 0; j < layers.get(layerNum).size(); j++) {
            tmpNode = layers.get(layerNum).get(j);
            tmpNode.xpos = j;
        }
    }

    private void cleanup() {
        NodeLayers originalLayers = data.getLayers();
        ArrayList<Node> currentOriginalLayer;
        ArrayList<MedianNode> currentLayer;
        MedianNode node;

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
        layers = new ArrayList<ArrayList<MedianNode>>();
        ArrayList<MedianNode> currentLayer;
        ArrayList<Node> currentOriginalLayer;
        NodeLayers originalLayers = data.getLayers();
        MedianNode node;
        Node realNode;
        nodeMap = new HashMap<Node, MedianNode>((int) (data.getGraph()
                .getNumberOfNodes() * 1.25));

        // create barycenternodes for the real gravisto nodes
        for (int i = 0; i < originalLayers.getNumberOfLayers(); i++) {
            currentLayer = new ArrayList<MedianNode>();
            layers.add(currentLayer);
            currentOriginalLayer = originalLayers.getLayer(i);

            for (int j = 0; j < currentOriginalLayer.size(); j++) {
                realNode = currentOriginalLayer.get(j);
                node = new MedianNode(realNode);
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
                    node.inNeighbors.add(nodeMap.get(neighbor));
                }

                nodeIter = node.node.getOutNeighborsIterator();
                for (int k = 0; k < node.outDegree; k++) {
                    neighbor = nodeIter.next();
                    node.outNeighbors.add(nodeMap.get(neighbor));
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
        return "Median";
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter iterations = new IntegerParameter(10, "Iterations",
                "Iterations of the cross-min-algorithm", 1, 100, 1,
                Integer.MAX_VALUE);

        this.parameters = new Parameter[] { iterations };
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
    }

    private class MedianNode {
        private Node node;
        private float median;
        private int xpos;
        private int inDegree;
        private int outDegree;
        private LazyMedianNodeArrayList inNeighbors;
        private LazyMedianNodeArrayList outNeighbors;
        private LazyMedianNodeArrayList inNeighborsOld;
        private LazyMedianNodeArrayList outNeighborsOld;

        public MedianNode(Node n) {
            node = n;
            inDegree = n.getInDegree();
            outDegree = n.getOutDegree();

            inNeighbors = new LazyMedianNodeArrayList(inDegree);
            outNeighbors = new LazyMedianNodeArrayList(outDegree);
            inNeighborsOld = new LazyMedianNodeArrayList(inDegree);
            outNeighborsOld = new LazyMedianNodeArrayList(outDegree);
        }

    }

    private class LazyMedianNodeArrayList {
        private MedianNode[] elements;
        public int elementCount;

        public LazyMedianNodeArrayList(int size) {
            elements = new MedianNode[size];
            elementCount = 0;
        }

        public MedianNode get(int index) {
            return elements[index];
        }

        public void add(MedianNode element) {
            elements[elementCount++] = element;
        }

        public void clear() {
            elementCount = 0;
        }

        // public void set(int index, MedianNode element)
        // {
        // elements[index] = element;
        // }
    }

    private class MedianNodeComparator implements Comparator<MedianNode> {
        /*
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        @Override
        public int compare(MedianNode o1, MedianNode o2) {
            return (int) (o1.median - o2.median);
        }

    }
}