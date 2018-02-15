package org.graffiti.plugins.algorithms.sugiyama.layout.cyclic;

import static java.lang.Math.max;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

public class Toolkit {
    /** The logger */
    private static final Logger logger = Logger.getLogger(Toolkit.class
            .getName());

    static int level(Node node) {
        return node.getInteger(SugiyamaConstants.PATH_LEVEL);
    }

    static int xpos(Node node) {
        return (int) node.getDouble(SugiyamaConstants.PATH_XPOS);
    }

    static boolean isDummy(Node node) {
        try {
            return node.getBoolean(SugiyamaConstants.PATH_DUMMY);
        } catch (AttributeNotFoundException ex) {
            return false;
        }
    }

    static boolean isMarked(Edge edge) {
        return edge.getBoolean(SugiyamaConstants.PATH_BK_MARKED);
    }

    /**
     * Extracts a two-dimensional array of nodes from the SugiyamaData object.
     */
    public static Node[][] collectLayers(SugiyamaData data) {
        String prefix = "CyclicBrandesKoepf: The input data is corrupt: ";

        // check the NodeLayers structure for consistency with all nodes' "xpos"
        for (int layerNum = 0; layerNum < data.getLayers().getNumberOfLayers(); layerNum++) {
            ArrayList<Node> layer = data.getLayers().getLayer(layerNum);
            for (int nodeNum = 0; nodeNum < layer.size(); nodeNum++) {
                int xpos = (int) layer.get(nodeNum).getDouble(
                        SugiyamaConstants.PATH_XPOS);
                if (xpos != nodeNum)
                    throw new IllegalStateException(prefix
                            + "The attribute \"sugiyama.xpos\" of some"
                            + " nodes doesn't match their array index in the"
                            + " NodeLayers structure -> Bug!");
            }
        }

        // copy references to all nodes into a 2-dimensional array
        Node[][] layers = new Node[data.getLayers().getNumberOfLayers()][];
        for (int layerNr = 0; layerNr < layers.length; layerNr++) {
            ArrayList<Node> nodeLayer = data.getLayers().getLayer(layerNr);
            layers[layerNr] = new Node[nodeLayer.size()];
            nodeLayer.toArray(layers[layerNr]);
        }

        // more checks on our data structures
        int nodes = 0;
        for (Node[] layer : layers) {
            nodes += layer.length;
        }
        if (nodes != data.getGraph().getNumberOfNodes())
            throw new IllegalStateException(prefix
                    + "Node# in layers[][] != graph.getNumberOfNodes()");

        for (int level = 0; level < layers.length; level++) {
            Node[] layer = layers[level];
            for (int xpos = 0; xpos < layer.length; xpos++) {
                Node node = layers[level][xpos];
                if (xpos != xpos(node))
                    throw new IllegalStateException(
                            "The arribute sugiyama.xpos of the node "
                                    + DebugToolkit.getNodeLabel(node)
                                    + " doesn't match that node's position"
                                    + " in the NodeLayers structure!");
                if (level != level(node))
                    throw new IllegalStateException(
                            "The arribute sugiyama.level of the node "
                                    + DebugToolkit.getNodeLabel(node)
                                    + " doesn't match that node's position"
                                    + " in the NodeLayers structure!");
            }
        }

        return layers;
    }

    /**
     * Extracts a two-dimensional array of nodes from the SugiyamaData object.
     */
    static Node[][] collectLayers(Graph graph) {
        // count the nodes in each layer
        Map<Integer, Integer> count = new HashMap<Integer, Integer>();
        for (Node node : graph.getNodes()) {
            int level = node.getInteger(SugiyamaConstants.PATH_LEVEL);
            int xpos = (int) node.getDouble(SugiyamaConstants.PATH_XPOS);
            if (count.containsKey(level)) {
                xpos = max(xpos, count.get(level));
            }
            count.put(level, xpos);
        }

        // find the number of layers
        int maxLevel = 0;
        for (int level : count.keySet()) {
            maxLevel = max(maxLevel, level);
        }

        // consistency check
        for (int i = 0; i <= maxLevel; i++)
            if (!count.containsKey(i))
                throw new IllegalArgumentException(
                        "There are no nodes on layer " + i + "!");

        // create the array for the nodes
        Node[][] layers = new Node[maxLevel + 1][];
        for (int level = 0; level <= maxLevel; level++) {
            layers[level] = new Node[count.get(level) + 1];
        }

        // copy the nodes into the layers-array
        for (Node node : graph.getNodes()) {
            int level = node.getInteger(SugiyamaConstants.PATH_LEVEL);
            int xpos = (int) node.getDouble(SugiyamaConstants.PATH_XPOS);
            layers[level][xpos] = node;
        }

        // do another check on our data structure
        String prefix = VerticalAlignment.class.getSimpleName()
                + ": input data is corrupt: ";

        int nodes = 0;
        for (Node[] layer : layers) {
            nodes += layer.length;
        }
        if (nodes != graph.getNumberOfNodes())
            throw new IllegalStateException(prefix
                    + "Node# in layers[][] != graph.getNumberOfNodes()");

        return layers;
    }

    /**
     * Marks type 1 conflicts. See Algorithm 1 of the Brandes/Koepf paper.
     */
    public static int markType1Conflicts(Node[][] layers) {

        int typeTwoConflicts = 0;

        // un-mark all edges by default
        for (Node[] layer : layers) {
            for (Node node : layer) {
                for (Edge edge : node.getAllOutEdges()) {
                    edge.setBoolean(SugiyamaConstants.PATH_BK_MARKED, false);
                }
            }
        }

        // for each pair of layers
        for (int upperLayer = 0; upperLayer < layers.length; upperLayer++) {
            int lowerLayer = (upperLayer + 1) % layers.length;

            // - inspect all non-inner segments between two inner segments
            // - start from the left

            // left inner segment
            int innerSegmentUL = -1;
            int innerSegmentLL = -1;
            // right inner segment
            int innerSegmentUR = 0;
            int innerSegmentLR = 0;

            do {

                // advance to next inner segment (or end of lower layer)
                do {
                    // next node in lower layer
                    innerSegmentLR++;

                    // end of layer?
                    if (innerSegmentLR >= layers[lowerLayer].length) {
                        // move the right inner segment fully to the right
                        innerSegmentUR = layers[upperLayer].length;
                        // innerSegmentUR = layers[upperLayer].length - 1;
                        // innerSegmentLR--;
                        break;
                    }

                    // found a new dummy node?
                    Node maybeDummy = layers[lowerLayer][innerSegmentLR];
                    if (!isDummy(maybeDummy)) {
                        continue;
                    }

                    // it's upper neighbor must be a dummy, too
                    Node n = maybeDummy.getInNeighbors().iterator().next();
                    if (!isDummy(n)) {
                        continue;
                    }

                    // we found a new inner segment

                    assert n.getInDegree() == 1;
                    assert n.getOutDegree() == 1;
                    assert maybeDummy.getInDegree() == 1;
                    assert maybeDummy.getOutDegree() == 1;

                    // get the pos of the upper neighbor
                    innerSegmentUR = xpos(n);

                    if (innerSegmentUL >= innerSegmentUR) {
                        ++typeTwoConflicts;
                        logger.log(Level.INFO, "Found a Type 2 conflict!");
                    }

                    break;

                } while (true);

                // inspect all edges whose target nodes lie between the
                // currently selected inner segments

                // for all nodes between innerSegmentLL and innerSegmentLR
                for (int n = innerSegmentLL + 1; n < innerSegmentLR; n++) {
                    Node inspectedNodeL = layers[lowerLayer][n];

                    // for all in-edges of the current node
                    for (Edge inspectedEdge : inspectedNodeL
                            .getDirectedInEdges()) {
                        Node inspectedNodeU = inspectedEdge.getSource();
                        int posU = xpos(inspectedNodeU);

                        // conflict?
                        if (posU < innerSegmentUL || innerSegmentUR < posU) {
                            if (isDummy(inspectedNodeU)
                                    && isDummy(inspectedNodeL)) {
                                ++typeTwoConflicts;
                                logger.log(Level.INFO,
                                        "Found a Type 2 conflict!");
                            }

                            // handle the type 1 conflict by marking the
                            // non-inner edge
                            inspectedEdge.setBoolean(
                                    SugiyamaConstants.PATH_BK_MARKED, true);
                        }
                    }
                }

                // move the left inner segment to the right inner segment
                // (the edges in between have just been tested)
                innerSegmentUL = innerSegmentUR;
                innerSegmentLL = innerSegmentLR;

                // stop at the end of the lower layer
            } while (!(innerSegmentLR >= layers[lowerLayer].length - 1));
            // finished with this pair of layers
        }
        return typeTwoConflicts;
    }

    /**
     * flips all x values horizontally
     */
    static void flipX(Map<Node, Double> x) {
        // find the rightmost x-coordinate ...
        double maxX = Double.NEGATIVE_INFINITY;
        for (double val : x.values()) {
            maxX = Math.max(maxX, val);
        }

        // ... and flip all x-coordinates
        for (Map.Entry<Node, Double> entry : x.entrySet()) {
            entry.setValue(maxX - entry.getValue());
        }
    }

    /**
     * flips the whole graph horizontally (i.e. flips the Node[][] layers and
     * each node's sugiyama.xpos attribute)
     */
    static void flipHorizontal(Node[][] layers) {

        // flip layers and XPOS:

        // for all layers
        for (int layer = 0; layer < layers.length; layer++) {

            // flip this layer horizontally
            Node[] oldArray = layers[layer];
            Node[] newArray = new Node[oldArray.length];
            layers[layer] = newArray;
            for (int oldIndex = 0; oldIndex < oldArray.length; oldIndex++) {
                // copy one node from oldArray to newArray
                int newIndex = oldArray.length - 1 - oldIndex;
                Node node = oldArray[oldIndex];
                newArray[newIndex] = node;

                // update the node's x-coordinate
                node.setDouble(SugiyamaConstants.PATH_XPOS, newIndex);
            }
        }
    }

    /**
     * flips the whole graph vertically (i.e. reverses all edges, changes each
     * node's sugiyama.level attribute and flips the Node[][] layers)
     */
    static void flipVertical(Node[][] layers) {

        // flip all layers vertically
        Node[][] result = new Node[layers.length][];
        for (int layer = 0; layer < layers.length; layer++) {
            result[layer] = layers[layers.length - 1 - layer];
            // flip each node's layer number
            for (Node node : result[layer]) {
                node.setInteger(SugiyamaConstants.PATH_LEVEL, layer);
            }
        }
        System.arraycopy(result, 0, layers, 0, result.length);

        // reverse all edges
        for (Edge edge : layers[0][0].getGraph().getEdges()) {
            edge.reverse();
        }
    }
}
