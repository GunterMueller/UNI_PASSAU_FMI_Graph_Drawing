// =============================================================================
//
//   Incubator.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
class Incubator {
    private Map<Node, NodeBlock> map;
    private ArrayList<ArrayList<EdgeBlock>> inEdgeBlocks;
    private int nextNodeBlockIndex;
    private int nextEdgeBlockIndex;
    private int nextBlockIndex;
    private BlockGraph blockGraph;
    private NodeBlock[] nodeBlocks;
    private EdgeBlock[] edgeBlocks;
    private Block<?>[] blocks;

    public static BlockGraph create(Graph graph, AlgorithmParameters parameters) {
        return new Incubator(graph, parameters).getBlockGraph();
    }

    private Incubator(Graph graph, AlgorithmParameters parameters) {
        map = new HashMap<Node, NodeBlock>();
        inEdgeBlocks = new ArrayList<ArrayList<EdgeBlock>>();
        nextNodeBlockIndex = 0;
        nextEdgeBlockIndex = 0;
        nextBlockIndex = 0;

        List<Node> nodes = graph.getNodes();

        nodeBlocks = new NodeBlock[nodes.size()];
        edgeBlocks = new EdgeBlock[graph.getEdges().size()];
        blocks = new Block[nodeBlocks.length + edgeBlocks.length];

        for (Node node : graph.getNodes()) {
            get(node);
        }

        EdgeBlock[] emptyArray = new EdgeBlock[0];

        for (int i = 0; i < nodeBlocks.length; i++) {
            nodeBlocks[i].setInBlocks(inEdgeBlocks.get(i).toArray(emptyArray));
        }

        Collections.shuffle(Arrays.asList(blocks), parameters.getRandom());

        blockGraph = new BlockGraph(nodeBlocks, edgeBlocks, blocks, graph,
                parameters);

        for (Block<?> block : blocks) {
            block.setBlockGraph(blockGraph);
        }
    }

    public BlockGraph getBlockGraph() {
        return blockGraph;
    }

    public NodeBlock get(Node node) {
        NodeBlock auxNode = map.get(node);
        if (auxNode == null)
            // map is updated by register()
            return new NodeBlock(node, this);
        else
            return auxNode;
    }

    public int register(Block<?> block) {
        blocks[nextBlockIndex] = block;
        return nextBlockIndex++;
    }

    public int register(Node node, NodeBlock nodeBlock) {
        map.put(node, nodeBlock);
        inEdgeBlocks.add(new ArrayList<EdgeBlock>());
        nodeBlocks[nextNodeBlockIndex] = nodeBlock;
        node.setInteger(BlockGraph.NODE_INDEX_PATH, nextNodeBlockIndex);
        return nextNodeBlockIndex++;
    }

    public void addInEdgeBlock(int target, EdgeBlock edgeBlock) {
        edgeBlock.globalEdgeBlockIndex = nextEdgeBlockIndex;
        edgeBlock.edge.setInteger(BlockGraph.EDGE_INDEX_PATH,
                nextEdgeBlockIndex);
        edgeBlocks[nextEdgeBlockIndex] = edgeBlock;
        nextEdgeBlockIndex++;
        inEdgeBlocks.get(target).add(edgeBlock);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
