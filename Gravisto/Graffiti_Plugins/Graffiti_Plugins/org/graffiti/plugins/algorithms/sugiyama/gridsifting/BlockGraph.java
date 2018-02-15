// =============================================================================
//
//   AuxGraph.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.gridsifting;

import java.awt.Color;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElement;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.tools.debug.DebugImage;
import org.graffiti.plugins.tools.debug.DebugSession;
import org.graffiti.plugins.tools.debug.DebugImage.Alignment;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public final class BlockGraph {
    public static final String NODE_INDEX_PATH = "universalSiftingNodeIndex";
    public static final String EDGE_INDEX_PATH = "universalSiftingEdgeIndex";
    // public static final String

    private AlgorithmParameters parameters;
    private Graph graph;
    protected NodeBlock[] nodeBlocks;
    protected int nodeBlockCount;
    protected EdgeBlock[] edgeBlocks;
    protected int edgeBlockCount;
    protected Block<?>[] blocks;
    protected int blockCount;

    protected int levelCount;

    private int[] cachedLevels;
    private int[] cachedCrossings;

    protected Block<?> currentPiFirst;
    protected Block<?> currentPiLast;

    protected Block<?> storedPiFirst;
    protected Block<?> storedPiLast;

    protected Block<?> bestPiFirst;
    protected Block<?> bestPiLast;

    // Phi of the block currently stepping vertically.
    protected int currentHotPhi;

    // Used by activateEdgeBlocks

    private CrossingCounter crossingCounter;

    private EdgeBlockActivator edgeBlockActivator;

    private LevelNormalizer levelNormalizer;

    private AdjacencySorter adjacencySorter;

    private BlockMover blockMover;

    private Swapper swapper;
    
    private boolean isSkippingEvenLevels;
    
    private boolean isSkippingNeighborLevels;
    
    private boolean isPreferring;

    protected DebugSession debugSession;

    public BlockGraph(NodeBlock[] nodeBlocks, EdgeBlock[] edgeBlocks,
            Block<?>[] blocks, Graph graph, AlgorithmParameters parameters) {
        this.nodeBlocks = nodeBlocks;
        nodeBlockCount = nodeBlocks.length;
        this.edgeBlocks = edgeBlocks;
        edgeBlockCount = edgeBlocks.length;
        this.blocks = blocks;
        blockCount = blocks.length;
        this.graph = graph;
        this.parameters = parameters;
        cachedCrossings = new int[2];
        cachedLevels = new int[] { -1, -1 };

        initializePi();

        crossingCounter = new CrossingCounter(this);
        edgeBlockActivator = new EdgeBlockActivator(this);
        levelNormalizer = new LevelNormalizer(this);
        adjacencySorter = new AdjacencySorter(this);
        blockMover = new BlockMover(this);
        swapper = new Swapper(this, blockMover);
        isSkippingEvenLevels = parameters.isSkippingEvenLevels();
        isSkippingNeighborLevels = parameters.isSkippingNeighborLevels();
        isPreferring = parameters.isPreferring();

        debugSession = DebugSession.create("");// USIFTING_DEBUG
    }

    private void initializePi() {
        if (blockCount > 0) {
            currentPiFirst = blocks[0];
            currentPiLast = blocks[blockCount - 1];

            for (int i = 0; i < blockCount; i++) {
                if (i > 0) {
                    blocks[i].currentPiPrev = blocks[i - 1];
                }

                if (i < blockCount - 1) {
                    blocks[i].currentPiNext = blocks[i + 1];
                }

                blocks[i].currentPiValue = i;
            }
        }
    }

    public void prepare() {
        levelNormalizer.normalizeLevels();
        edgeBlockActivator.updateEdgeBlocksActivation(-1);
    }

    public Graph getGraph() {
        return graph;
    }

    public int siftingRound() {
        int delta = 0;

        for (int i = 0; i < nodeBlockCount; i++) {
            delta += verticalStep(nodeBlocks[i]);
        }

        return delta;
    }

    private void storePi() {
        storedPiFirst = currentPiFirst;
        storedPiLast = currentPiLast;
        bestPiFirst = currentPiFirst;
        bestPiLast = currentPiLast;

        for (Block<?> block : blocks) {
            block.storePi();
        }
    }

    private void restorePi() {
        currentPiFirst = storedPiFirst;
        currentPiLast = storedPiLast;

        for (Block<?> block : blocks) {
            block.restorePi();
        }
    }

    private void storeBestPi() {
        bestPiFirst = currentPiFirst;
        bestPiLast = currentPiLast;

        for (Block<?> block : blocks) {
            block.storeBestPi();
        }
    }

    private void restoreBestPi() {
        currentPiFirst = bestPiFirst;
        currentPiLast = bestPiLast;

        for (Block<?> block : blocks) {
            block.restoreBestPi();
        }
    }

    public int verticalStep(NodeBlock nodeBlock) {
//        if (FixedRoundsCondition.DO_IT) {
//            isPreferring = true;
//        }
        
        // if (debugSession != null)//USIFTING_DEBUG
        // {
        // debugSession.addHeader("globalStep(" + nodeBlock.getDebugLabel() +
        // ")", 1);
        // debugDumpGraph("globalStep(" + nodeBlock.getDebugLabel() +
        // ") before normalizeLevels()");
        // }
        
        levelNormalizer.normalizeLevels();

        // if (debugSession != null)//USIFTING_DEBUG
        // {
        // debugDumpGraph("globalStep(" + nodeBlock.getDebugLabel() +
        // ") after normalizeLevels()");
        // }

        int initialLevel = nodeBlock.phi;

        storePi();

        int radius = parameters.getLevelRadius();

        int chi = 0;
        int bestChi = 0;

        int minLevel = Math.max(nodeBlock.calculateMinPossibleLevel(),
                nodeBlock.phi - radius);
        int maxLevel = Math.min(
                nodeBlock.calculateMaxPossibleLevel(levelCount), nodeBlock.phi
                        + radius);
        
        //int preferredLevel = (minLevel + maxLevel) / 2;
        //int preferredLevel = nodeBlock.wantsUpwards ? 0 : 10000000;
        int preferredLevel = levelCount / 2;
        
        // if (debugSession != null)//USIFTING_DEBUG
        // {
        // debugSession.addLine("minLevel = " + minLevel + "; maxLevel = " +
        // maxLevel);
        // }

        int bestLevel = initialLevel;

        // Clear cache of level crossings.
        cachedLevels[0] = -1;
        cachedLevels[1] = -1;

        int level = initialLevel;

        while (level > minLevel) {
            level--;
            
            boolean skip = isSkippingEvenLevels && (level & 1) == 0
                           || isSkippingNeighborLevels && (level + 1 == initialLevel);
            
            chi += verticalSwap(nodeBlock, level, Direction.In, skip);

            if (!skip) {
                if (chi < bestChi
                        || chi == bestChi
                            && isPreferring
                            && Math.abs(level - preferredLevel)
                                <= Math.abs(bestLevel - preferredLevel)) {
                    // if (debugSession != null)//USIFTING_DEBUG
                    // {
                    // debugDumpGraph("storing level " + level +
                    // " as best, for chi = " + chi + "; bestChi was " + bestChi);
                    // }
        
                    storeBestPi();
                    bestChi = chi;
                    bestLevel = level;
                }
            }
        }

        restorePi();
        chi = 0;

        // if (debugSession != null)//USIFTING_DEBUG
        // {
        // debugDumpGraph("globalStep(" + nodeBlock.getDebugLabel() +
        // ") after restorePi");
        // }

        // Clear cache of level crossings.
        cachedLevels[0] = -1;
        cachedLevels[1] = -1;

        level = initialLevel;

        chi += verticalSwap(nodeBlock, initialLevel, null, false);

        if (chi < bestChi
                || chi == bestChi
                    && (!isPreferring
                            || Math.abs(level - preferredLevel)
                                <= Math.abs(bestLevel - preferredLevel))) {
            // if (debugSession != null)//USIFTING_DEBUG
            // {
            // debugDumpGraph("storing (btw: old) level " + level +
            // " as best, for chi = " + chi + "; bestChi was " + bestChi);
            // }

            storeBestPi();
            bestChi = chi;
            bestLevel = level;
        }

        while (level < maxLevel) {
            level++;
            
            boolean skip = isSkippingEvenLevels && (level & 1) == 0
                           || isSkippingNeighborLevels && (level - 1 == initialLevel);
            
            chi += verticalSwap(nodeBlock, level, Direction.Out, skip);

            if (!skip) {
                if (chi < bestChi 
                        || chi == bestChi
                            && isPreferring
                            && Math.abs(level - preferredLevel)
                                <= Math.abs(bestLevel - preferredLevel)) {
                    // if (debugSession != null)//USIFTING_DEBUG
                    // {
                    // debugDumpGraph("storing level " + level +
                    // " as best, for chi = " + chi + "; bestChi was " + bestChi);
                    // }
    
                    storeBestPi();
                    bestChi = chi;
                    bestLevel = level;
                }
            }
        }

        nodeBlock.phi = bestLevel;
        currentHotPhi = bestLevel;

        restoreBestPi();

        // edgeBlockActivator.updateEdgeBlocksActivation(currentHotPhi);
        // edgeBlockActivator.updateEdgeBlocksActivation(-1);

        // if (debugSession != null)//USIFTING_DEBUG
        // {
        // debugDumpGraph("globalStep(" + nodeBlock.getDebugLabel() +
        // ") after restoreBestPi");
        // }

        return bestChi;
    }

    // skip -> do not horizontally sift
    private int verticalSwap(NodeBlock nodeBlock, int newLevel,
            Direction direction, boolean skip) {
        // if (debugSession != null)//USIFTING_DEBUG
        // {
        // debugSession.addHeader("verticalSwap(" + nodeBlock.getDebugLabel() +
        // ", " + newLevel + ", " + (direction == null ? "null" :
        // direction.name()) + ")", 2);
        // debugDumpGraph("before verticalSwap(" + nodeBlock.getDebugLabel() +
        // ", " + newLevel + ", " + (direction == null ? "null" :
        // direction.name()) + ")");
        // }

        int deltaChi = 0;

        adjacencySorter.updatePiValues();
        adjacencySorter.sortAdjacencies();

        if (direction != null) {
            int inc = direction.getInc();

            if ((newLevel & 1) != 0) {
                // AAA
                if (cachedLevels[0] == -1) {
                    deltaChi -= crossingCounter.countCrossings(newLevel - 3
                            * inc, newLevel - inc);
                    deltaChi -= crossingCounter.countCrossings(newLevel - inc,
                            newLevel + inc);
                } else {
                    /*
                     * System.out.println("Expected crossings: " +
                     * (crossingCounter.countCrossings(newLevel - 3 * inc,
                     * newLevel - inc)));
                     * System.out.println("  Cached crossings: " +
                     * cachedCrossings[0]);
                     * System.out.println("Expected crossings: " +
                     * (crossingCounter.countCrossings(newLevel - inc, newLevel
                     * + inc))); System.out.println("  Cached crossings: " +
                     * cachedCrossings[1]); System.out.println();
                     */

                    // if (debugSession != null)//USIFTING_DEBUG
                    // {
                    // DebugWriter writer = debugSession.createTextWriter(true);
                    // writer.println("AAA");
                    // writer.println("verticalSwap(" +
                    // nodeBlock.getDebugLabel() + ", " + newLevel + ", " +
                    // (direction == null ? "null" : direction.name()) + ")");
                    // writer.println("countCrossings(" + (newLevel - 3 * inc) +
                    // ", " + (newLevel - inc) + ") = " +
                    // (crossingCounter.countCrossings(newLevel - 3 * inc,
                    // newLevel - inc)));
                    // writer.println("countCrossings(" + (newLevel - inc) +
                    // ", " + (newLevel + inc) + ") = " +
                    // (crossingCounter.countCrossings(newLevel - inc, newLevel
                    // + inc)));
                    // writer.println("for level " + cachedLevels[0] +
                    // ": cachedCrossings[0] = " + cachedCrossings[0]);
                    // writer.println("for level " + cachedLevels[1] +
                    // ": cachedCrossings[1] = " + cachedCrossings[1]);
                    // writer.close();
                    // }

                    deltaChi -= cachedCrossings[0];
                    deltaChi -= cachedCrossings[1];
                }
            } else {
                // BBB
                if (cachedLevels[0] == -1) {
                    deltaChi -= crossingCounter.countCrossings(newLevel - 2
                            * inc, newLevel - inc);
                    deltaChi -= crossingCounter.countCrossings(newLevel - inc,
                            newLevel);
                } else {
                    /*
                     * System.out.println("Expected crossings: " +
                     * (crossingCounter.countCrossings(newLevel - 2 * inc,
                     * newLevel - inc)));
                     * System.out.println("  Cached crossings: " +
                     * cachedCrossings[0]);
                     * System.out.println("Expected crossings: " +
                     * (crossingCounter.countCrossings(newLevel - inc, newLevel
                     * + inc))); System.out.println("  Cached crossings: " +
                     * cachedCrossings[1]); System.out.println();
                     */

                    // if (debugSession != null)//USIFTING_DEBUG
                    // {
                    // DebugWriter writer = debugSession.createTextWriter(true);
                    // writer.println("BBB");
                    // writer.println("verticalSwap(" +
                    // nodeBlock.getDebugLabel() + ", " + newLevel + ", " +
                    // (direction == null ? "null" : direction.name()) + ")");
                    // writer.println("countCrossings(" + (newLevel - 2 * inc) +
                    // ", " + (newLevel - inc) + ") = " +
                    // (crossingCounter.countCrossings(newLevel - 2 * inc,
                    // newLevel - inc)));
                    // writer.println("countCrossings(" + (newLevel - inc) +
                    // ", " + (newLevel) + ") = " +
                    // (crossingCounter.countCrossings(newLevel - inc,
                    // newLevel)));
                    // writer.println("for level " + cachedLevels[0] +
                    // ": cachedCrossings[0] = " + cachedCrossings[0]);
                    // writer.println("for level " + cachedLevels[1] +
                    // ": cachedCrossings[1] = " + cachedCrossings[1]);
                    // writer.close();
                    // }

                    deltaChi -= cachedCrossings[0];
                    deltaChi -= cachedCrossings[1];
                }

                deltaChi -= crossingCounter.countCrossings(newLevel, newLevel
                        + 2 * inc);
            }

            nodeBlock.phi = newLevel;

            currentHotPhi = newLevel;

            edgeBlockActivator.updateEdgeBlocksActivation(newLevel);

            adjacencySorter.updatePiValues();// Maybe not necessary.
            adjacencySorter.sortAdjacencies();

            // if (debugSession != null)//USIFTING_DEBUG
            // {
            // DebugWriter writer = debugSession.createTextWriter(true);
            // writer.println("prev crossings: deltaChi = " + deltaChi);
            // writer.println("updateEdgeBlocksActivation(" + newLevel +
            // ") ...");
            // writer.close();
            // debugDumpGraph("verticalSwap after, node " +
            // nodeBlock.getDebugLabel() + " changed to level " + newLevel);
            // if (debugSession.getSectionCode().equals("5.4"))
            // {
            // System.out.println();
            // }
            // }

            if ((newLevel & 1) != 0) {
                // CCC
                deltaChi += crossingCounter.countCrossings(newLevel - 3 * inc,
                        newLevel - inc);

                int d = crossingCounter
                        .countCrossings(newLevel - inc, newLevel);
                deltaChi += d;

                cachedLevels[0] = Math.min(newLevel - inc, newLevel);
                cachedCrossings[0] = d;

                d = crossingCounter.countCrossings(newLevel, newLevel + inc);
                deltaChi += d;

                cachedLevels[1] = Math.min(newLevel, newLevel + inc);
                cachedCrossings[1] = d;
            } else {
                // DDD
                int d = crossingCounter.countCrossings(newLevel - 2 * inc,
                        newLevel);
                deltaChi += d;

                cachedLevels[0] = Math.min(newLevel - 2 * inc, newLevel);
                cachedCrossings[0] = d;

                d = crossingCounter
                        .countCrossings(newLevel, newLevel + 2 * inc);
                deltaChi += d;

                cachedLevels[1] = Math.min(newLevel, newLevel + 2 * inc);
                cachedCrossings[1] = d;
            }

            // if (debugSession != null)//USIFTING_DEBUG
            // {
            // debugSession.addLine("crossing delta(sum): deltaChi = " +
            // deltaChi);
            // }
        } else {
            nodeBlock.phi = newLevel;
            currentHotPhi = newLevel;
            edgeBlockActivator.updateEdgeBlocksActivation(newLevel);
        }
        
        if (!skip) {

            // Sift adjacent edge blocks.
            for (Direction dir : Direction.values()) {
                for (EdgeBlock edgeBlock : nodeBlock.blocks.get(dir)) {
                    if (edgeBlock.isActive) {
                        deltaChi += horizontalStep(edgeBlock);
                    }
                }
            }
        
            deltaChi += horizontalStep(nodeBlock);
        }

        return deltaChi;
    }
    
    private double calcMedian(EnumMap<Direction, EdgeBlock[]> blocks) {
        EdgeBlock[] blocks1 = blocks.get(Direction.In);
        EdgeBlock[] blocks2 = blocks.get(Direction.Out);
        int len1 = blocks1.length;
        int len2 = blocks2.length;
        int totalCount = len1 + len2;
        if (totalCount == 0) return 0.0;
        boolean isOdd = (totalCount & 1) != 0;
        int firstMed = (totalCount - 1) / 2;
        EdgeBlock[] singleList = null;
        if (len1 == 0) {
            singleList = blocks2;
        } else if (len2 == 0) {
            singleList = blocks1;
        } else {
            int i1 = 0;
            int i2 = 0;
            int ii = 0;
            int p1 = blocks1[0].currentPiValue;
            int p2 = blocks2[0].currentPiValue;
            int droppedVal;
            
            while (true) {
                if (p1 <= p2) {
                    droppedVal = p1;
                    i1++;
                    p1 = i1 == len1 ? Integer.MAX_VALUE : blocks1[i1].currentPiValue;
                    
                } else {
                    droppedVal = p2;
                    i2++;
                    p2 = i2 == len2 ? Integer.MAX_VALUE : blocks2[i2].currentPiValue;
                }
                if (ii == firstMed) {
                    if (isOdd) {
                        return droppedVal; 
                    } else {
                        return (droppedVal + (p1 <= p2 ? p1 : p2)) / 2.0;
                    }
                }
                if (i1 == len1) {
                    return isOdd ? blocks2[firstMed - len1].currentPiValue : (blocks2[firstMed - len1].currentPiValue + blocks2[firstMed - len1 + 1].currentPiValue) / 2.0; 
                } else if (i2 == len2) {
                    return isOdd ? blocks1[firstMed - len2].currentPiValue : (blocks1[firstMed - len2].currentPiValue + blocks1[firstMed - len2 + 1].currentPiValue) / 2.0;
                }
                ii++;
            }
        }
        
        return isOdd ? singleList[firstMed].currentPiValue
                : (singleList[firstMed].currentPiValue
                        + singleList[firstMed + 1].currentPiValue) / 2.0;
    }

    /**
     * 
     * @param block
     */
    private int horizontalStep(Block<?> block) {
        double median;
        if (block.isEdge()) {
            EdgeBlock edgeBlock = (EdgeBlock) block;
            median = (edgeBlock.source.currentPiValue + edgeBlock.target.currentPiValue) / 2.0;
        } else {
            NodeBlock nodeBlock = (NodeBlock) block;
            median = calcMedian(nodeBlock.sortedBlocks);
        }
        //block.get
        // if (debugSession != null)//USIFTING_DEBUG
        // {
        // debugSession.addHeader("siftingStep(" + block.getDebugLabel() + ")",
        // 3);
        // debugDumpGraph("before siftingStep(" + block.getDebugLabel() + ")");
        // }

        // TODO: special case: single block in whole graph

        // for recognizing the old position
        Block<?> oldPositionPrev = block.currentPiPrev;

        // ///// Best pi is saved by the current predecessor of the block.
        // Best pi, first position at first.
        Block<?> bestPiPrev = null;
        int bestPiValue = -1000000000;

        blockMover.putAtFirstPosition(block);

        adjacencySorter.updatePiValues();
        adjacencySorter.sortAdjacencies();

        // int bigDeltaBest = Integer.MAX_VALUE;
        int bigDeltaBest = 0;

        // cached levelwise best deltas, automatically set to 0
        int[] levelwiseDeltaBests = new int[cachedLevels.length];
        // !!!!!!!!!!!!!!!!!!!!!

        int bigDelta = 0;
        int bigDeltaNull = 0;

        // cached levelwise deltas and deltaNulls, automatically set to 0
        int[] levelwiseDeltaNulls = new int[cachedLevels.length];
        int[] levelDeltas = new int[cachedLevels.length];

        while (block.currentPiNext != null) {
            int delta = swapper.siftingSwap(block, cachedLevels, levelDeltas);
            bigDelta += delta;

            boolean isOldPosition = block.currentPiPrev == oldPositionPrev;

            if (isOldPosition) {
                // We are currently standing at the initial position of the
                // block. "Normalize" the delta as the difference of crossings
                // must ultimately be compared to the initial position of the
                // block.
                bigDeltaNull = bigDelta;

                for (int i = 0; i < cachedLevels.length; i++) {
                    levelwiseDeltaNulls[i] = levelDeltas[i];
                }
            }

            if (bigDelta < bigDeltaBest
                || bigDelta == bigDeltaBest
                    && (isOldPosition
                            || Math.abs(block.currentPiValue - median) < Math.abs(bestPiValue - median))) {
                // The current position is the best we have found so far.
                bigDeltaBest = bigDelta;
                bestPiPrev = block.currentPiPrev;
                bestPiValue = block.currentPiValue;

                for (int i = 0; i < cachedLevels.length; i++) {
                    levelwiseDeltaBests[i] = levelDeltas[i];
                }
            }
        }

        // Place the block at the best position.
        if (bestPiPrev == null) {
            blockMover.putAtFirstPosition(block);
        } else {
            blockMover.putAfter(block, bestPiPrev);
        }

        // if (debugSession != null)//USIFTING_DEBUG
        // {
        // debugSession.addLine("best yields delta: " + (bigDeltaBest -
        // bigDeltaNull));
        // debugDumpGraph("after siftingStep(" + block.getDebugLabel() +
        // "), yields delta = " + (bigDeltaBest - bigDeltaNull));
        // }

        for (int i = 0; i < cachedLevels.length; i++) {
            cachedCrossings[i] += levelwiseDeltaBests[i]
                    - levelwiseDeltaNulls[i];
        }

        return bigDeltaBest - bigDeltaNull;
    }

    public void importLevels(Graph graphCopy) {
        levelCount = 0;

        for (Node node : graphCopy.getNodes()) {
            if (node.containsAttribute(NODE_INDEX_PATH)) {
                int index = node.getInteger(NODE_INDEX_PATH);

                int level = node.getInteger(SugiyamaConstants.PATH_LEVEL);

                nodeBlocks[index].phi = level + 1;

                levelCount = (level + 1 > levelCount) ? level + 1 : levelCount;
            }
        }
    }

    public void exportLevels() {
        levelNormalizer.normalizeLevels();
        adjacencySorter.updatePiValues();
        edgeBlockActivator.updateEdgeBlocksActivation(-1);

        // if (debugSession != null)//USIFTING_DEBUG
        // {
        // debugDumpGraph("exporting");
        // }

        for (NodeBlock nodeBlock : nodeBlocks) {
            nodeBlock.exportLevels();
        }
    }

    public void exportOrdering() {
        for (Node node : graph.getNodes()) {
            if (node.containsAttribute(NODE_INDEX_PATH)) {
                // Node in node block.
                int index = node.getInteger(NODE_INDEX_PATH);

                node.setDouble(SugiyamaConstants.PATH_XPOS,
                        nodeBlocks[index].currentPiValue);
            } else {
                // Node in edge block.
                setPiFor(node);
            }
        }
    }

    public void stripOwnAttributes() {
        for (Node node : graph.getNodes()) {
            stripOwnAttributes(node);
        }
        for (Edge edge : graph.getEdges()) {
            stripOwnAttributes(edge);
        }
    }

    private void stripOwnAttributes(GraphElement element) {
        if (element.containsAttribute(BlockGraph.NODE_INDEX_PATH)) {
            element.removeAttribute(BlockGraph.NODE_INDEX_PATH);
        }

        if (element.containsAttribute(BlockGraph.EDGE_INDEX_PATH)) {
            element.removeAttribute(BlockGraph.EDGE_INDEX_PATH);
        }
    }

    private int setPiFor(Node node) {
        Edge edge = node.getAllInEdges().iterator().next();
        int pi;

        if (edge.containsAttribute(EDGE_INDEX_PATH)) {
            int index = edge.getInteger(EDGE_INDEX_PATH);

            pi = edgeBlocks[index].currentPiValue;
        } else {
            Node pred = edge.getSource();

            if (pred.containsAttribute(SugiyamaConstants.PATH_XPOS)) {
                pi = (int) pred.getDouble(SugiyamaConstants.PATH_XPOS);
            } else {
                pi = setPiFor(pred);
            }
        }

        node.setDouble(SugiyamaConstants.PATH_XPOS, pi);

        return pi;
    }

    public int incLevel(int level, Direction direction) {
        level += direction.getInc();

        if ((level & 1) != 0 && level != currentHotPhi) {
            level += direction.getInc();
        }

        return level;
    }

    public String debugToString() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < nodeBlockCount; i++) {
            builder.append(nodeBlocks[i].debugToString()).append("\n\n");
        }

        for (int i = 0; i < edgeBlockCount; i++) {
            builder.append(edgeBlocks[i].debugToString()).append("\n\n");
        }

        return builder.toString();
    }

    public void debugPrintCurrentPi() {
        StringBuilder builder = new StringBuilder();
        Set<Block<?>> set = new HashSet<Block<?>>();
        Block<?> block = currentPiFirst;
        while (block != null) {
            if (!set.contains(block)) {
                set.add(block);
                builder.append(block.toString() + "(" + block.currentPiValue
                        + ")   ");
                block = block.currentPiNext;
            } else {
                builder.append(block.toString() + "(" + block.currentPiValue
                        + ")!! ");
                break;
            }
        }
        System.out.println(builder);
    }

    public void debugDumpGraph(String caption) {
        DebugImage image = debugSession.createImageWriter();
        image.setGrid(100);

        image.drawText("[" + debugSession.getSectionCode() + "] " + caption,
                50, 50);

        // "real" pi values
        Map<Block<?>, Integer> map = new HashMap<Block<?>, Integer>();

        Block<?> block = currentPiFirst;
        int pi = 0;
        while (block != null) {
            map.put(block, pi);

            image.drawText("pi = " + block.currentPiValue, pi * 100, 0,
                    Alignment.CENTER);

            pi++;
            block = block.currentPiNext;
        }

        block = currentPiFirst;
        pi = 0;
        while (block != null) {
            if (block.isEdge()) {
                EdgeBlock eb = (EdgeBlock) block;
                int sourcePi = map.get(eb.source);
                int targetPi = map.get(eb.target);
                if (eb.isActive) {
                    int upPhi = incLevel(eb.source.phi, Direction.Out);
                    int lowPhi = incLevel(eb.target.phi, Direction.In);

                    image.drawLine(sourcePi * 100, eb.source.phi * 100,
                            pi * 100, upPhi * 100);
                    image.drawLine(pi * 100, lowPhi * 100, targetPi * 100,
                            eb.target.phi * 100);

                    image.setColor(Color.WHITE);
                    image.fillRect(pi * 100 - 12, upPhi * 100 - 12, 24,
                            (lowPhi - upPhi) * 100 + 24);
                    image.setColor(Color.BLACK);
                    image.drawRect(pi * 100 - 12, upPhi * 100 - 12, 24,
                            (lowPhi - upPhi) * 100 + 24);

                    image.drawText(block.getDebugLabel(), pi * 100,
                            (lowPhi + upPhi) * 50, Alignment.CENTER);
                } else {
                    image.setColor(Color.RED);
                    image.drawLine(pi * 100, 0, pi * 100, 50);
                    image.drawText(block.getDebugLabel(), pi * 100, 60);

                    image.setColor(Color.BLACK);
                    image.drawLine(sourcePi * 100, eb.source.phi * 100,
                            targetPi * 100, eb.target.phi * 100);
                    image.drawText(block.getDebugLabel(),
                            (sourcePi + targetPi) * 50 + 10,
                            (eb.source.phi + eb.target.phi) * 50);
                }
            }

            pi++;
            block = block.currentPiNext;
        }

        block = currentPiFirst;
        pi = 0;
        while (block != null) {
            if (block.isNode()) {
                NodeBlock nb = (NodeBlock) block;
                image.setColor(Color.WHITE);
                image.fillOval(pi * 100 - 12, nb.phi * 100 - 12, 24, 24);
                image.setColor(Color.BLACK);
                image.drawOval(pi * 100 - 12, nb.phi * 100 - 12, 24, 24);
                image.drawText(nb.getDebugLabel(), pi * 100, nb.phi * 100,
                        Alignment.CENTER);
                map.put(nb, pi);
            }

            pi++;
            block = block.currentPiNext;
        }

        image.close();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
