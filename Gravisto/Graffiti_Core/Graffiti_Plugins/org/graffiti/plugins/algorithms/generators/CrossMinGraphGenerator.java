package org.graffiti.plugins.algorithms.generators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.util.CoordinatesUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements a graph generator that creates a random, levelled
 * graph.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class CrossMinGraphGenerator extends AbstractAlgorithm {

    /** number of nodes in the graph */
    private int numberOfNodes;
    /** edge density */
    private double edgeDensity;
    /** density of dummy nodes */
    private double dummyNodeDensity;
    /** cyclic or horizontal layout */
    private boolean cyclicLayout;
    /** wide layout or not */
    // private boolean wide;
    /** ratio required to create a golden rectangle */
    private final double RATIO = (1 + Math.sqrt(5)) / (3 + Math.sqrt(5));

    /** list of real nodes */
    private ArrayList<Node> realNodes;

    private LinkedList<Node> myNodes;
    private LinkedList<Edge> myEdges;

    public CrossMinGraphGenerator() {

    }

    @Override
    public void reset() {

    }

    @Override
    public void check() {

    }

    /**
     * Returns a random graph
     * 
     * @param data
     *            SugiyamaData bean
     * @return Returns a random graph
     */
    public Graph getRandomGraph(SugiyamaData data) {
        boolean failed = true;
        myNodes = new LinkedList<Node>();
        myEdges = new LinkedList<Edge>();

        while (failed) {

            for (Edge e : myEdges) {
                e.remove();
            }
            for (Node n : myNodes) {
                n.remove();
            }

            myEdges.clear();
            myNodes.clear();

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
                        myNodes.add(currentNode);
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
                // fix invalid long edges
                int fixedEdges = -1;
                while (fixedEdges != 0) {
                    fixedEdges = fixLongEdges(data);
                    // System.out.println("Fixed " + fixedEdges +
                    // " invalid edges.");
                }
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

    private int fixLongEdges(SugiyamaData data) {
        int fixed = 0;
        HashSet<Node> dummyNodes = data.getDummyNodes();
        HashSet<Node> checkedDummyNodes = new HashSet<Node>();

        Iterator<Node> nodeIter = dummyNodes.iterator();
        Node current, inNeighbor, outNeighbor;

        while (nodeIter.hasNext()) {
            current = nodeIter.next();

            if (checkedDummyNodes.contains(current)) {
                continue;
            } else {
                checkedDummyNodes.add(current);
            }

            inNeighbor = current.getInNeighborsIterator().next();
            outNeighbor = current.getOutNeighborsIterator().next();

            // this dummy node is not part of a problematic edge
            if (!dummyNodes.contains(inNeighbor)
                    && !dummyNodes.contains(outNeighbor)) {
                continue;
            }
            LinkedList<Node> inNeighbors, outNeighbors, dummyChain;
            dummyChain = new LinkedList<Node>();

            if (dummyNodes.contains(inNeighbor)) {
                inNeighbors = getUpperDummyNodes(current, data);
            } else {
                inNeighbors = new LinkedList<Node>();
            }

            dummyChain.addAll(0, inNeighbors);
            dummyChain.add(current);

            if (dummyNodes.contains(outNeighbor)) {
                outNeighbors = getLowerDummyNodes(current, data);
            } else {
                outNeighbors = new LinkedList<Node>();
            }

            dummyChain.addAll(outNeighbors);
            checkedDummyNodes.addAll(dummyChain);

            if (dummyChain.size() < data.getLayers().getNumberOfLayers()) {
                continue;
            }

            // at this point, the long edge is too long and has to be split
            // into two long edges
            int split = data.getLayers().getNumberOfLayers() - 1;
            Node splitNode = dummyChain.get(split);
            inNeighbor = splitNode.getInNeighborsIterator().next();

            Edge invalidEdge = splitNode.getAllInEdges().iterator().next();
            data.getGraph().deleteEdge(invalidEdge);
            myEdges.remove(invalidEdge);

            int splitLevel = splitNode.getInteger(SugiyamaConstants.PATH_LEVEL);

            int otherLevel;
            if (splitLevel == 0) {
                otherLevel = data.getLayers().getNumberOfLayers() - 1;
            } else {
                otherLevel = splitLevel - 1;
            }

            ArrayList<Node> sLevel = data.getLayers().getLayer(splitLevel);
            ArrayList<Node> oLevel = data.getLayers().getLayer(otherLevel);

            // get a real node on level sLevel; this node will be the new
            // out-neighbor for "inNeighbor"
            Node newNeighbor = sLevel
                    .get((int) (Math.random() * sLevel.size()));
            while (dummyNodes.contains(newNeighbor)) {
                newNeighbor = sLevel.get((int) (Math.random() * sLevel.size()));
            }
            Edge theEdge = data.getGraph().addEdge(inNeighbor, newNeighbor,
                    true);
            myEdges.add(theEdge);

            // get a real node on the level oLevel; this node will be the
            // new out-neighbor for "splitNode"
            newNeighbor = oLevel.get((int) (Math.random() * oLevel.size()));
            while (dummyNodes.contains(newNeighbor)) {
                newNeighbor = oLevel.get((int) (Math.random() * oLevel.size()));
            }
            theEdge = data.getGraph().addEdge(newNeighbor, splitNode, true);
            myEdges.add(theEdge);

            ++fixed;

        }
        return fixed;
    }

    /**
     * Private helper method to get a <code>LinkedList</code> of all dummy nodes
     * on the long edge that are above the current <code>Node n</code>.
     * 
     * @return Returns a <code>LinkedList</code> of dummy nodes on the long edge
     *         that are above <code>Node n</code>.
     */
    private LinkedList<Node> getUpperDummyNodes(Node n, SugiyamaData data) {
        LinkedList<Node> upperDummies = new LinkedList<Node>();
        Node next;
        HashSet<Node> dummyNodes = data.getDummyNodes();

        next = n.getAllInNeighbors().iterator().next();
        while (dummyNodes.contains(next)) {
            upperDummies.addFirst(next);
            next = next.getAllInNeighbors().iterator().next();
        }

        return upperDummies;
    }

    /**
     * Private helper method to get a <code>LinkedList</code> of all dummy nodes
     * on the long edge that are below the current <code>Node n</code>.
     * 
     * @return Returns a <code>LinkedList</code> of dummy nodes on the long edge
     *         that are below <code>Node n</code>.
     */
    private LinkedList<Node> getLowerDummyNodes(Node n, SugiyamaData data) {
        LinkedList<Node> lowerDummies = new LinkedList<Node>();
        Node next;
        HashSet<Node> dummyNodes = data.getDummyNodes();

        next = n.getAllOutNeighbors().iterator().next();
        while (dummyNodes.contains(next)) {
            lowerDummies.addLast(next);
            next = next.getAllOutNeighbors().iterator().next();
        }

        return lowerDummies;
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
                    throw new Exception(
                            "Detected a cycle that contains only dummy nodes!");
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
        int fail = 0;
        Edge theEdge;

        while (edgesLeft > 0 && (fail < 10000)) {
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
                            theEdge = graph
                                    .addEdge(neighbor, currentNode, true);
                            myEdges.add(theEdge);
                            edgesLeft--;
                        } else {
                            fail++;
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
                            theEdge = graph
                                    .addEdge(neighbor, currentNode, true);
                            myEdges.add(theEdge);
                            edgesLeft--;
                        } else {
                            fail++;
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
                            theEdge = graph
                                    .addEdge(currentNode, neighbor, true);
                            myEdges.add(theEdge);
                            edgesLeft--;
                        } else {
                            fail++;
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
                                theEdge = graph.addEdge(currentNode, neighbor,
                                        true);
                                myEdges.add(theEdge);
                                edgesLeft--;
                            } else {
                                fail++;
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
            Edge theEdge;

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
                                    theEdge = graph.addEdge(neighbor,
                                            currentNode, true);
                                    myEdges.add(theEdge);
                                    edgesLeft--;
                                    suc = true;
                                }
                            } else {
                                theEdge = graph.addEdge(neighbor, currentNode,
                                        true);
                                myEdges.add(theEdge);
                                edgesLeft--;
                                suc = true;
                            }
                        } catch (AttributeNotFoundException anfe) {
                            theEdge = graph
                                    .addEdge(neighbor, currentNode, true);
                            myEdges.add(theEdge);
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
                                    theEdge = graph.addEdge(neighbor,
                                            currentNode, true);
                                    myEdges.add(theEdge);
                                    edgesLeft--;
                                    suc = true;
                                }
                            } else {
                                theEdge = graph.addEdge(neighbor, currentNode,
                                        true);
                                myEdges.add(theEdge);
                                edgesLeft--;
                                suc = true;
                            }
                        } catch (AttributeNotFoundException anfe) {
                            theEdge = graph
                                    .addEdge(neighbor, currentNode, true);
                            myEdges.add(theEdge);
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
                                    theEdge = graph.addEdge(currentNode,
                                            neighbor, true);
                                    myEdges.add(theEdge);
                                    edgesLeft--;
                                    suc = true;
                                }
                            } else {
                                theEdge = graph.addEdge(currentNode, neighbor,
                                        true);
                                myEdges.add(theEdge);
                                edgesLeft--;
                                suc = true;
                            }
                        } catch (AttributeNotFoundException anfe) {
                            theEdge = graph
                                    .addEdge(currentNode, neighbor, true);
                            myEdges.add(theEdge);
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
                                        theEdge = graph.addEdge(currentNode,
                                                neighbor, true);
                                        myEdges.add(theEdge);
                                        edgesLeft--;
                                        suc = true;
                                    }
                                } else {
                                    theEdge = graph.addEdge(currentNode,
                                            neighbor, true);
                                    myEdges.add(theEdge);
                                    edgesLeft--;
                                    suc = true;
                                }
                            } catch (AttributeNotFoundException anfe) {
                                theEdge = graph.addEdge(currentNode, neighbor,
                                        true);
                                myEdges.add(theEdge);
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

    public void execute() {
        SugiyamaData data = new SugiyamaData();
        graph.getListenerManager().transactionStarted(this);

        if (cyclicLayout) {
            data.setAlgorithmType(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA);
        }

        getRandomGraph(data);

        for (Edge edge : graph.getEdges()) {
            // edge.addAttribute(new HashMapAttribute("graphics"), "");
            // edge.addAttribute(new StringAttribute("arrowhead"), "graphics");
            ((StringAttribute) edge.getAttribute("graphics.arrowhead"))
                    .setString("org.graffiti.plugins.views.defaults.StandardArrowShape");
            // edge.addAttribute(new
            // StringAttribute("org.graffiti.plugins.views.defaults.StandardArrowShape"),
            // "graphics.arrowhead");
        }
        for (Node d : data.getDummyNodes()) {
            d.setString(GraphicAttributeConstants.SHAPE_PATH,
                    GraphicAttributeConstants.CIRCLE_CLASSNAME);
            d.setDouble(GraphicAttributeConstants.DIMW_PATH, 10);
            d.setDouble(GraphicAttributeConstants.DIMH_PATH, 10);
            d.setInteger(GraphicAttributeConstants.FILLCOLOR_PATH
                    + Attribute.SEPARATOR + GraphicAttributeConstants.RED, 0);
            d.setInteger(GraphicAttributeConstants.FILLCOLOR_PATH
                    + Attribute.SEPARATOR + GraphicAttributeConstants.GREEN, 0);
            d.setInteger(GraphicAttributeConstants.FILLCOLOR_PATH
                    + Attribute.SEPARATOR + GraphicAttributeConstants.BLUE, 0);
        }

        CoordinatesUtil.updateGraph(graph, data);
        CoordinatesUtil.updateRealCoordinates(graph);
        graph.getListenerManager().transactionFinished(this);
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter numberOfNodesParam = new IntegerParameter(100, 10,
                Integer.MAX_VALUE, "Number of nodes",
                "Number of nodes in the graph");
        DoubleParameter edgeDensityParam = new DoubleParameter(2d,
                "Edge density", "Edge density in the graph");
        DoubleParameter dummyNodeDensityParam = new DoubleParameter(0.3d,
                "Dummy node density", "Dummy node density in the graph", 0d, 1d);
        BooleanParameter wideLayoutParam = new BooleanParameter(true,
                "Wide layout", "Wide layout");
        BooleanParameter cyclicLayout = new BooleanParameter(false,
                "Cyclic layout", "Cyclic layout");

        return new Parameter<?>[] { numberOfNodesParam, edgeDensityParam,
                dummyNodeDensityParam, wideLayoutParam, cyclicLayout };
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        numberOfNodes = ((IntegerParameter) params[0]).getValue();
        edgeDensity = ((DoubleParameter) params[1]).getValue();
        dummyNodeDensity = ((DoubleParameter) params[2]).getValue();
        // wide = ((BooleanParameter)params[3]).getValue();
        cyclicLayout = ((BooleanParameter) params[4]).getValue();
    }

    public String getName() {
        return "Sugiyama: CrossMin graph generator";
    }

}
