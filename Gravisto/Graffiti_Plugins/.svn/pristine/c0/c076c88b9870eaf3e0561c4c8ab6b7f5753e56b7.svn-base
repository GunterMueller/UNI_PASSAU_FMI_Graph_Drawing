package tests.graffiti.plugins.algorithms.sugiyama;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

public class CrossMinGraphGenerator {

    private int numberOfNodes;
    private double edgeDensity;
    private double dummyNodeDensity;
    private boolean cyclicLayout;
//    private boolean wide;
    private final double RATIO = (1 + Math.sqrt(5)) / (3 + Math.sqrt(5));

    private ArrayList<Node> realNodes;

    public CrossMinGraphGenerator(int numberOfNodes, double edgeDensity,
            double dummyNodeDensity, boolean wide, boolean cyclicLayout) {
        this.numberOfNodes = numberOfNodes;
        this.edgeDensity = edgeDensity;
        this.dummyNodeDensity = dummyNodeDensity;
//        this.wide = wide;
        this.cyclicLayout = cyclicLayout;
    }

    public Graph getRandomGraph(SugiyamaData data) {
        boolean failed = true;
        AdjListGraph graph = null;

        while (failed) {

            graph = new AdjListGraph();

            int nodes = 0;
            int currentLevel = 0;

            Node currentNode;

            NodeLayers layers = new NodeLayers();
            data.setLayers(layers);
            layers.addLayer();
            ArrayList<Node> currentLayer = layers.getLayer(0);

            // maxNodesPerLayer is implied by the number of nodes
            // we benchmark graphs that form a golden rectangle:
            // a b
            // +--------+----+
            // |
            // | a
            // |
            // where (a/b) = Phi = the golden ratio = (1+sqrt(5))/2
            // 
            // this gives the following formula for the number of
            // layers (k) relative to the number of nodes (n):
            // k = sqrt( n * ( (1+sqrt(5))/(3+sqrt(5)) ))
            double k = Math.sqrt(numberOfNodes * RATIO);

            int maxNodesPerLayer = (int) (Math
                    .floor((numberOfNodes / k) + 0.5d));

            // add nodes to levels
            while (nodes < numberOfNodes) {
                for (int i = 0; i < maxNodesPerLayer; i++) {
                    if (Math.random() < 0.9) {
                        nodes++;
                        currentNode = graph.addNode();
                        currentLayer.add(currentNode);
                        currentNode.setInteger(SugiyamaConstants.PATH_LEVEL,
                                currentLevel);
                    }
                    if (nodes >= numberOfNodes) {
                        break;
                    }
                }
                if (nodes < numberOfNodes) {
                    currentLevel++;
                    layers.addLayer();
                    currentLayer = layers.getLayer(currentLevel);
                }
            }

            try {
                // create dummy nodes
                LinkedList<Node> dummyNodes = addDummyNodes(graph, layers, data);
                // add edges - dummy nodes first
                int edgesLeft = addDummyNodeEdges(graph, dummyNodes, layers);
                // add edges
                addEdges(edgesLeft, graph, data);
                if (cyclicLayout) {
                    checkDummyNodes(data);
                }
                failed = false;
            } catch (Exception e) {

            }
        }
        data.setGraph(graph);
        return graph;
    }

    private void checkDummyNodes(SugiyamaData data) throws Exception {
        Iterator<Node> nodeIter = data.getDummyNodes().iterator();
        HashSet<Node> dummyNodes = data.getDummyNodes();
        Node currentNode;
        Node nextNode;

        // check for cycles that only contain dummy nodes
        while (nodeIter.hasNext()) {
            currentNode = nodeIter.next();
            nextNode = currentNode.getOutNeighbors().iterator().next();
            while (dummyNodes.contains(nextNode)) {
                if (nextNode == currentNode)
                    throw new Exception("damn...");
                nextNode = nextNode.getOutNeighbors().iterator().next();
            }
        }

    }

    private void addEdges(int edgesLeft, Graph graph, SugiyamaData data)
            throws Exception {
        // filter out dummy nodes
        NodeLayers layers = new NodeLayers();
        NodeLayers origLayers = data.getLayers();
        ArrayList<Node> rNodes = new ArrayList<Node>();

        for (int i = 0; i < origLayers.getNumberOfLayers(); i++) {
            layers.addLayer();
            for (int j = 0; j < origLayers.getLayer(i).size(); j++) {
                Node aNode = origLayers.getLayer(i).get(j);
                boolean isDummy;
                try {
                    isDummy = aNode.getBoolean(SugiyamaConstants.PATH_DUMMY);
                    if (!isDummy) {
                        layers.getLayer(i).add(origLayers.getLayer(i).get(j));
                        rNodes.add(origLayers.getLayer(i).get(j));
                    }
                } catch (AttributeNotFoundException anfe) {
                    layers.getLayer(i).add(origLayers.getLayer(i).get(j));
                    rNodes.add(origLayers.getLayer(i).get(j));
                }
            }
        }
        // add edges
        Node currentNode, neighbor;
        int level;
        int maxLevel = layers.getNumberOfLayers() - 1;
        while (edgesLeft > 0) {
            currentNode = rNodes.get((int) (Math.random() * rNodes.size()));
            level = currentNode.getInteger(SugiyamaConstants.PATH_LEVEL);

            // add edge to a node on the level above
            if (Math.random() < 0.5) {

                if (level == 0 && !cyclicLayout) {
                    continue; // not a valid edge
                } else if (level == 0 && cyclicLayout) {
                    neighbor = layers.getLayer(maxLevel).get(
                            (int) (Math.random() * layers.getLayer(maxLevel)
                                    .size()));
                    try {
                        neighbor.getAttribute(SugiyamaConstants.PATH_DUMMY);
                    } catch (AttributeNotFoundException anfe) {
                        if (!currentNode.getNeighbors().contains(neighbor)) {
                            graph.addEdge(neighbor, currentNode, true);
                            edgesLeft--;
                        }
                    }
                } else {
                    neighbor = layers.getLayer(level - 1).get(
                            (int) (Math.random() * layers.getLayer(level - 1)
                                    .size()));
                    try {
                        neighbor.getAttribute(SugiyamaConstants.PATH_DUMMY);
                    } catch (AttributeNotFoundException anfe) {
                        if (!currentNode.getNeighbors().contains(neighbor)) {
                            graph.addEdge(neighbor, currentNode, true);
                            edgesLeft--;
                        }
                    }
                }

                // add edge to a node on the level below
            } else {

                if (level == maxLevel && !cyclicLayout) {
                    continue; // not a valid edge
                } else if (level == maxLevel && cyclicLayout) {
                    neighbor = layers.getLayer(0).get(
                            (int) (Math.random() * layers.getLayer(0).size()));
                    try {
                        neighbor.getAttribute(SugiyamaConstants.PATH_DUMMY);
                    } catch (AttributeNotFoundException anfe) {
                        if (!currentNode.getNeighbors().contains(neighbor)) {
                            graph.addEdge(currentNode, neighbor, true);
                            edgesLeft--;
                        }
                    }
                } else {
                    if (layers.getLayer(level + 1) != null
                            && !layers.getLayer(level + 1).isEmpty()) {
                        neighbor = layers.getLayer(level + 1).get(
                                (int) (Math.random() * layers.getLayer(
                                        level + 1).size()));
                        try {
                            neighbor.getAttribute(SugiyamaConstants.PATH_DUMMY);
                        } catch (AttributeNotFoundException anfe) {
                            if (!currentNode.getNeighbors().contains(neighbor)) {
                                graph.addEdge(currentNode, neighbor, true);
                                edgesLeft--;
                            }
                        }
                    }
                }
            }
        }
    }

    private int addDummyNodeEdges(Graph graph, LinkedList<Node> dummyNodes,
            NodeLayers layers) throws Exception {
        Node neighbor, currentNode;
        int edgesLeft = (int) (numberOfNodes * edgeDensity);
        int level = 0;
        int maxLevel = layers.getNumberOfLayers() - 1;
        int failed = 0;

        while (!dummyNodes.isEmpty()) {

            currentNode = dummyNodes.removeFirst();
            level = currentNode.getInteger(SugiyamaConstants.PATH_LEVEL);
            boolean suc = false;
            boolean isDummy;

            // upper neighbor of the dummy node
            while (!suc) {
                if (failed > 100000)
                    throw new Exception("damn");
                if (currentNode.getInDegree() < 1) {
                    if (level == 0) {
                        neighbor = layers.getLayer(maxLevel).get(
                                (int) (Math.random() * layers
                                        .getLayer(maxLevel).size()));
                        try {
                            isDummy = neighbor
                                    .getBoolean(SugiyamaConstants.PATH_DUMMY);
                            if (isDummy) {
                                if (neighbor.getOutDegree() == 0) {
                                    graph.addEdge(neighbor, currentNode, true);
                                    edgesLeft--;
                                    suc = true;
                                }
                            } else {
                                graph.addEdge(neighbor, currentNode, true);
                                edgesLeft--;
                                suc = true;
                            }
                        } catch (AttributeNotFoundException anfe) {
                            graph.addEdge(neighbor, currentNode, true);
                            edgesLeft--;
                            suc = true;
                        }
                    } else {
                        neighbor = layers.getLayer(level - 1).get(
                                (int) (Math.random() * layers.getLayer(
                                        level - 1).size()));
                        try {
                            isDummy = neighbor
                                    .getBoolean(SugiyamaConstants.PATH_DUMMY);
                            if (isDummy) {
                                if (neighbor.getOutDegree() == 0) {
                                    graph.addEdge(neighbor, currentNode, true);
                                    edgesLeft--;
                                    suc = true;
                                }
                            } else {
                                graph.addEdge(neighbor, currentNode, true);
                                edgesLeft--;
                                suc = true;
                            }
                        } catch (AttributeNotFoundException anfe) {
                            graph.addEdge(neighbor, currentNode, true);
                            edgesLeft--;
                            suc = true;
                        }
                    }
                } else {
                    suc = true;
                }
                failed++;
            }
            suc = false;

            failed = 0;
            while (!suc) {
                if (failed > 100000)
                    throw new Exception("damn");
                // lower neighbor of the dummy node
                if (currentNode.getOutDegree() < 1) {
                    if (level == maxLevel) {
                        neighbor = layers.getLayer(0).get(
                                (int) (Math.random() * layers.getLayer(0)
                                        .size()));
                        try {
                            isDummy = neighbor
                                    .getBoolean(SugiyamaConstants.PATH_DUMMY);
                            if (isDummy) {
                                if (neighbor.getInDegree() == 0) {
                                    graph.addEdge(currentNode, neighbor, true);
                                    edgesLeft--;
                                    suc = true;
                                }
                            } else {
                                graph.addEdge(currentNode, neighbor, true);
                                edgesLeft--;
                                suc = true;
                            }
                        } catch (AttributeNotFoundException anfe) {
                            graph.addEdge(currentNode, neighbor, true);
                            edgesLeft--;
                            suc = true;
                        }
                    } else {
                        if (!layers.getLayer(level + 1).isEmpty()) {
                            neighbor = layers.getLayer(level + 1).get(
                                    (int) (Math.random() * layers.getLayer(
                                            level + 1).size()));
                            try {
                                isDummy = neighbor
                                        .getBoolean(SugiyamaConstants.PATH_DUMMY);
                                if (isDummy) {
                                    if (neighbor.getInDegree() == 0) {
                                        graph.addEdge(currentNode, neighbor,
                                                true);
                                        edgesLeft--;
                                        suc = true;
                                    }
                                } else {
                                    graph.addEdge(currentNode, neighbor, true);
                                    edgesLeft--;
                                    suc = true;
                                }
                            } catch (AttributeNotFoundException anfe) {
                                graph.addEdge(currentNode, neighbor, true);
                                edgesLeft--;
                                suc = true;
                            }
                        }
                    }
                } else {
                    suc = true;
                }
                failed++;
            }
        }
        return edgesLeft;
    }

    private LinkedList<Node> addDummyNodes(Graph graph, NodeLayers layers,
            SugiyamaData data) throws Exception {
        int numberOfDummyNodes = (int) Math.floor(numberOfNodes
                * dummyNodeDensity);
        LinkedList<Node> dummyNodes = new LinkedList<Node>();
        int level;
        int maxLevel = layers.getNumberOfLayers() - 1;
        Node currentNode;
        realNodes = new ArrayList<Node>(graph.getNodes());
        data.setDummyNodes(new HashSet<Node>());

        while (numberOfDummyNodes > 0) {

            int index = (int) (Math.random() * realNodes.size());
            currentNode = realNodes.remove(index);
            level = currentNode.getInteger(SugiyamaConstants.PATH_LEVEL);

            // there are only dummy nodes on level 0 or maxLevel, if the layout
            // is cyclic
            if (level == 0 || level == maxLevel) {
                if (!cyclicLayout) {
                    realNodes.add(currentNode);
                    continue;
                }
            }
            currentNode.setBoolean(SugiyamaConstants.PATH_DUMMY, true);
            dummyNodes.add(currentNode);
            data.getDummyNodes().add(currentNode);
            numberOfDummyNodes--;
        }

        return dummyNodes;
    }

}
