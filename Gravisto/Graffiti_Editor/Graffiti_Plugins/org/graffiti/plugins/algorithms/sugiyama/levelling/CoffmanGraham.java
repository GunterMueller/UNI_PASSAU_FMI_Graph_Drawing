// =============================================================================
//
//   CoffmanGraham.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.levelling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.TreeMap;

import org.graffiti.attributes.AttributeExistsException;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.sugiyama.constraints.SugiyamaConstraint;
import org.graffiti.plugins.algorithms.sugiyama.constraints.VerticalConstraintWithTwoNodes;
import org.graffiti.plugins.algorithms.sugiyama.decycling.TopoSort;
import org.graffiti.plugins.algorithms.sugiyama.decycling.TopologicalComparator;
import org.graffiti.plugins.algorithms.sugiyama.util.NodeLayers;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * This class implements the <code>CoffmanGraham</code> algorithm to layer a
 * graph with the result that each node is assigned to a layer.
 * <p>
 * The attached graph has to be directed and acyclic. Since
 * <code>CoffmanGraham</code> can't handle transitive edges, a
 * <code>TransitiveReduction</code> is triggered before starting the actual
 * <code>CoffmanGraham</code> algorithm.
 * 
 * @author scheu
 * @version $Revision$ $Date$
 */
public class CoffmanGraham extends AbstractLevellingAlgorithm implements
        LevellingAlgorithm {
    /** The algorithm's name */
    private final String ALGORITHM_NAME = "CoffmanGraham";

    /** String to display in case no decycling has been done */
    private final String NO_DECYCLING_DONE = "This graph has not been decycled"
            + " yet. Won't try to level a (possibly) cyclic graph!";

    /** Width at creation time, will be overwritten by setData() */
    private final int INITIAL_WIDTH = 50;

    /**
     * Boolean indicating whether the method <code>check()</code> has been
     * called
     */
    private boolean checked;

    /** Bean to store the deleted edges of this phase */
    private SugiyamaData data;

    /** The layers of the graph */
    private NodeLayers layers;

    /** Counter of current layer */
    private int currLayer;

    /** Counter to mark visited nodes */
    private int cgNum;

    /**
     * Stores all visited nodes in <code>phase1()</code> that reach an in-degree
     * level of zero. It is sorted by the ordering given by
     * <code>LexTopoComparator</code>. The <code>"key"</code> is a list of the
     * <code>cgNum</code> pattern of the predecessors of a node. The
     * <code>"value"</code> is a list of <code>Node</code>s that match that
     * pattern.
     */
    private TreeMap<ArrayList<Integer>, ArrayList<Node>> sourceMap;

    /** Stores the number of nodes that are allowed on a layer */
    private int width;

    /** Stores all transitive edges */
    private HashSet<Edge> transEdges;

    /** Stores all processed nodes of <code>phase1()</code> */
    private ArrayList<Node> pNodeList;

    /**
     * Temporary container of layers with its nodes.
     */
    private ArrayList<ArrayList<Node>> tmpLayers;

    /**
     * Stores a list of nodes as <code>"value"</code> that must not be placed on
     * the same level as a node represented by the <code>"key"</code>.
     */
    private HashMap<Node, ArrayList<Node>> notSameLevelMap;

    /**
     * Class constructor.
     */
    public CoffmanGraham() {
        cgNum = 0;
        width = INITIAL_WIDTH;
        currLayer = -1;
        checked = false;
    }

    /**
     * Accessor for the name of this algorithm
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     * @return Returns the name of this algorithm
     */
    public String getName() {
        return this.ALGORITHM_NAME;
    }

    /*
     * @see org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#getData()
     */
    public SugiyamaData getData() {
        return this.data;
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#setData(org
     * .graffiti.plugins.algorithms.sugiyama.util.SugiyamaData)
     */
    public void setData(SugiyamaData data) {
        this.data = data;
        layers = data.getLayers();
    }

    /*
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#getParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        IntegerParameter p1 = new IntegerParameter(20, "Nodes / Layer",
                "Set the max number of nodes per layer", 1, 100, 1,
                Integer.MAX_VALUE);

        this.parameters = new Parameter[] { p1 };
        return this.parameters;
    }

    /*
     * @see
     * org.graffiti.plugin.algorithm.AbstractAlgorithm#setParameters(org.graffiti
     * .plugin.parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        width = ((IntegerParameter) params[0]).getInteger();
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#supportsBigNodes
     * ()
     */
    public boolean supportsBigNodes() {
        return false;
    }

    /*
     * @seeorg.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm#
     * supportsConstraints()
     */
    public boolean supportsConstraints() {
        return false;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return algorithmType
                .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA);
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.sugiyama.levelling.AbstractLevellingAlgorithm
     * #reset()
     */
    @Override
    public void reset() {
        super.reset();
        layers = null;
    }

    /*
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        try {
            graph.getBoolean(SugiyamaConstants.PATH_HASBEENDECYCLED);
        } catch (AttributeNotFoundException anfe) {
            throw new PreconditionException(NO_DECYCLING_DONE);
        }

        checked = true;
    }

    /**
     * Executes the <code>CoffmanGraham</code> algorithm. The attached graph has
     * to be directed, acyclic and non-transitive. If there are any transitive
     * edges, the algorithm won't work, therefore a
     * <code>TransitiveReduction</code> is triggered in advance to collect all
     * transitive edges. With a transitive reduced graph the actual
     * <code>CoffmanGraham</code> algorithm starts with its two phases.
     */
    public void execute() {
        if (!checked)
            throw new RuntimeException(NO_DECYCLING_DONE);

        graph.getListenerManager().transactionStarted(this);

        init();
        new TransitiveReduction().execute();
        getInitSources();
        phase1();
        phase2();

        HashSet<Node> dummies;
        dummies = addDummies(data);
        data.setDummyNodes(dummies);

        // add level-attribute to the nodes
        ArrayList<Node> current;
        int numOfLayers = data.getLayers().getNumberOfLayers();
        for (int i = 0; i < numOfLayers; i++) {
            current = data.getLayers().getLayer(i);
            for (int j = 0; j < current.size(); j++) {
                try {
                    current.get(j).addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                            SugiyamaConstants.SUBPATH_LEVEL, i);
                } catch (AttributeExistsException aee) {
                    current.get(j).setInteger(SugiyamaConstants.PATH_LEVEL, i);
                }
            }
        }
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * Initialize the graph to start the <code>CoffmanGraham</code> algorithm.<br>
     * The constraints are checked to collect all nodes that must not be placed
     * on the same level. This is necessary, because in the
     * <code>decycling</code> phase if <code>SCCDecycling</code> was chosen,
     * <code>SCCDecycling</code> might have deleted edges. Since these edges are
     * inserted after the <code>leveling</code> phase it might happen that an
     * edge connects nodes that would be placed on the same level if the
     * constraints are not checked.<br>
     * Also the needed data structures for <code>CoffmanGraham</code> are
     * initialized. Furthermore all needed node attributes are initialized.
     */
    private void init() {
        // First layer to place nodes is on level 0.
        currLayer = -1;
        cgNum = 0;
        transEdges = new HashSet<Edge>(graph.getNumberOfEdges());
        sourceMap = new TreeMap<ArrayList<Integer>, ArrayList<Node>>(
                new LexTopoComparator());
        pNodeList = new ArrayList<Node>(graph.getNumberOfNodes());
        tmpLayers = new ArrayList<ArrayList<Node>>();

        // Build up HashMap of nodes where the VerticalConstaintWithTwoNodes
        // constraint is set.
        HashSet<SugiyamaConstraint> constraints = data.getConstraints();
        notSameLevelMap = new HashMap<Node, ArrayList<Node>>();
        ArrayList<Node> tmpValueList = new ArrayList<Node>();
        Node source;
        Node target;
        for (SugiyamaConstraint sC : constraints) {
            // Only consider those constraints that are of type
            // VerticalConstraintWithTwoNodes
            if (sC instanceof VerticalConstraintWithTwoNodes) {
                VerticalConstraintWithTwoNodes vC = (VerticalConstraintWithTwoNodes) sC;

                // Only consider those that are mandatory and don't have the
                // same y-coordinate
                if (vC.isMandatory() && vC.isNonequalY()) {
                    source = vC.getSource();
                    target = vC.getTarget();

                    // Check if SOURCE node is already in notSameLevelMap as
                    // key, add and put
                    if (notSameLevelMap.containsKey(source)) {
                        tmpValueList = notSameLevelMap.get(source);
                        tmpValueList.add(target);
                        notSameLevelMap.put(source, tmpValueList);
                    } else {
                        ArrayList<Node> nodes = new ArrayList<Node>();
                        nodes.add(target);

                        notSameLevelMap.put(source, nodes);
                    }

                    // Check if TARGET node is already in notSameLevelMap as
                    // key, add and put
                    if (notSameLevelMap.containsKey(target)) {
                        tmpValueList = notSameLevelMap.get(target);
                        tmpValueList.add(source);
                        notSameLevelMap.put(target, tmpValueList);
                    } else {
                        ArrayList<Node> nodes = new ArrayList<Node>();
                        nodes.add(source);
                        notSameLevelMap.put(target, nodes);
                    }
                }
            }
        }

        // Initialize cgNum = -1, placed = false, cgLayer = -1, cgInDegree = -1
        Iterator<Node> nIt = graph.getNodesIterator();
        Node tmp;
        while (nIt.hasNext()) {
            tmp = nIt.next();
            try {
                tmp.setInteger(SugiyamaConstants.PATH_CGNUM, -1);
            } catch (AttributeNotFoundException anfe) {
                tmp.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_CGNUM, -1);
            }

            try {
                tmp.setBoolean(SugiyamaConstants.PATH_CGPLACED, false);
            } catch (AttributeNotFoundException anfe) {
                tmp.addBoolean(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_CGPLACED, false);
            }

            try {
                tmp.setInteger(SugiyamaConstants.PATH_CGLAYER, -1);
            } catch (AttributeNotFoundException anfe) {
                tmp.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_CGLAYER, -1);
            }

            try {
                tmp.setInteger(SugiyamaConstants.PATH_CGINDEGREE, -1);
            } catch (AttributeNotFoundException anfe) {
                tmp.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                        SugiyamaConstants.SUBPATH_CGINDEGREE, -1);
            }
        }
    }

    /**
     * This method collects all sources of the graph.
     * <p>
     * At least one such node exists, since the graph is acyclic
     */
    private void getInitSources() {
        // TreeMap key: predecessors of a node
        ArrayList<Integer> preds = new ArrayList<Integer>();
        // TreeMap value: Nodes with same predecessor
        ArrayList<Node> nodes = new ArrayList<Node>();

        // Get all source's without in-edges
        for (Node n : graph.getNodes()) {
            if (n.getDirectedInEdges().isEmpty()) {
                nodes.add(n);
                sourceMap.put(preds, nodes);
            }
        }
    }

    /**
     * This method represents the first phase of the <code>CoffmanGraham</code>
     * algorithm. It calls the method <code>processNode()</code> which assigns
     * an increasing <code>CoffmanGraham</code> number <code>cgNum</code> to all
     * nodes when visited starting with value "1". The order of visiting is
     * determined by <code>LexTopoComparator</code>.
     */
    private void phase1() {
        ArrayList<Integer> tmpPreds = new ArrayList<Integer>();
        ArrayList<Node> tmpNodes = new ArrayList<Node>();

        while (!sourceMap.isEmpty()) {
            // Get firstKey
            tmpPreds = sourceMap.firstKey();

            // Remove firstKey
            tmpNodes = sourceMap.remove(tmpPreds);

            // Process node
            while (!tmpNodes.isEmpty()) {
                Node n = tmpNodes.remove(0);
                processNode(n);
            }
        }
    }// phase1

    /**
     * Processes a node by calling the <code>markNode()</code> method to assign
     * a <code>cgNum<code> to it. Also, the in-degree of all 
     * out-neighbors of this node will be decremented by one. If an 
     * out-neighbor node reaches an in-degree level of zero, it will be 
     * added to the <code>sourceMap</code>.
     * 
     * @param n
     *            node to process
     */
    private void processNode(Node n) {
        // Mark this node
        markNode(n);

        // Decrement in-degree of all non transitive out-neighbors
        for (Edge outE : n.getDirectedOutEdges()) {
            // Only consider those that are not connected by a transitive edge
            if (!transEdges.contains(outE)) {
                Node outN = outE.getTarget();
                // Current in-degree
                int inDeg = getCurrentInDeg(outN);
                // Initial decremented in degree.
                int decInDeg = -1;

                // Decrement and set in-degree to this out neighbor node
                if (inDeg > 0) {
                    // Decrement
                    inDeg--;

                    outN.setInteger(SugiyamaConstants.PATH_CGINDEGREE, inDeg);
                    decInDeg = inDeg;
                }

                // Adding out-neighbor to sourceMap if decremented in-degree==0
                if (decInDeg == 0) {
                    // Collect all predecessors that are not connected by a
                    // transitive edge
                    ArrayList<Integer> preds = new ArrayList<Integer>();
                    for (Edge e : outN.getDirectedInEdges()) {
                        if (!transEdges.contains(e)) {
                            preds.add(e.getSource().getInteger(
                                    SugiyamaConstants.PATH_CGNUM));
                        }
                    }

                    // Add node to sourceMap
                    if (sourceMap.containsKey(preds)) {
                        ArrayList<Node> nodes = sourceMap.get(preds);
                        nodes.add(outN);
                        sourceMap.put(preds, nodes);
                    } else {
                        ArrayList<Node> nodes = new ArrayList<Node>();
                        nodes.add(outN);
                        sourceMap.put(preds, nodes);
                    }
                }
            }
        }
        // Node is processed. Add node to the list for phase2
        pNodeList.add(n);
    }

    /**
     * Labels a node with its unique <code>CoffmanGraham</code> number
     * <code>cgNum</code>. The <code>cgNum</code> indicates when the node was
     * visited. It's increased before the node is labeled, so each node that is
     * visited later will have a higher <code>cgNum</code> than this node. The
     * first node will be marked with <code>cgNum<code> value of "1".
     * 
     * @param n
     *            node to mark
     */
    private void markNode(Node n) {
        // Increase for this node
        cgNum++;
        try {
            n.setInteger(SugiyamaConstants.PATH_CGNUM, cgNum);
        } catch (AttributeNotFoundException anfe) {
            n.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                    SugiyamaConstants.SUBPATH_CGNUM, cgNum);
        }
    }

    /**
     * Returns the current in-degree of this node. If it is set to "-1", the
     * node is unvisited and the in-degree reduced by the number of transitive
     * edges of this node isn't set yet. Therefore the
     * <code>getTransInDeg()</code> method is called.
     * 
     * @param n
     *            node to get the in-degree from
     * @return current in-degree
     */
    private int getCurrentInDeg(Node n) {
        int inDeg = n.getInteger(SugiyamaConstants.PATH_CGINDEGREE);

        if (inDeg == -1) {
            inDeg = getTransInDeg(n);
        }

        return inDeg;
    }

    /**
     * Returns the in-degree of this node. This is the number of directed
     * in-edges reduced by the number of transitive edges in the
     * <code>transEdges</code> set that connect this node. The possibly reduced
     * in-degree of this node will be returned.
     * 
     * @param n
     *            node to set the in-degree
     * @return in-degree of this node
     */
    private int getTransInDeg(Node n) {
        int inDeg = n.getDirectedInEdges().size();

        // Check transEdges and decrement if edge is in there
        for (Edge e : n.getDirectedInEdges()) {
            if (transEdges.contains(e)) {
                inDeg--;
            }
        }

        try {
            n.setInteger(SugiyamaConstants.PATH_CGINDEGREE, inDeg);
        } catch (AttributeNotFoundException anfe) {
            n.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                    SugiyamaConstants.SUBPATH_CGINDEGREE, inDeg);
        }

        return inDeg;
    }

    /**
     * This method represents the second phase of the <code>CoffmanGraham</code>
     * algorithm. It layers the graph by placing each node on a layer. To make
     * sure a node can be placed, the <code>check4placement</code> method is
     * called.
     * <p>
     * <code>Phase2()</code> starts with the highest <code>cgNum</code> which is
     * a target node and places it at the lowest layer with value "0". This has
     * the effect that sources will be placed at the highest numbered layer. But
     * the source has to be placed at level "0" and the target at the highest
     * numbered layer. Therefore they will be stored in a temporary data
     * structure and copied to <code>Sugiyama-Data</code> <code>layers</code>
     * when all nodes are placed.
     */
    private void phase2() {
        // Add layer, so nodes can be placed on that layer.
        tmpLayers.add(new ArrayList<Node>());
        // Increase currLayer, now currLayer = 0
        currLayer++;

        while (!pNodeList.isEmpty()) {
            for (int i = pNodeList.size() - 1; i >= 0; i--) {
                Node current = pNodeList.get(i);

                // Check if current node can be placed
                if (check4Placement(current)) {
                    // Add current node to current layer
                    tmpLayers.get(currLayer).add(current);

                    // Set boolean flag attribute CGPLACED of current node
                    // to true
                    try {
                        current.setBoolean(SugiyamaConstants.PATH_CGPLACED,
                                true);
                    } catch (AttributeNotFoundException anfe) {
                        current.addBoolean(SugiyamaConstants.PATH_SUGIYAMA,
                                SugiyamaConstants.SUBPATH_CGPLACED, true);
                    }

                    // Set layer number attribute of current node to
                    // the value currLayer
                    try {
                        current.setInteger(SugiyamaConstants.PATH_CGLAYER,
                                currLayer);
                    } catch (AttributeNotFoundException anfe) {
                        current.addInteger(SugiyamaConstants.PATH_SUGIYAMA,
                                SugiyamaConstants.SUBPATH_CGLAYER, currLayer);
                    }

                    // Remove current node from pNodeList at index i
                    pNodeList.remove(i);

                    // Start over
                    continue;
                }

                // no node can be placed at current layer (i==0)
                // -> add newLayer
                if (i == 0) {
                    tmpLayers.add(new ArrayList<Node>());
                    currLayer++;
                }
            }
        }

        // Copy nodes from tmpLayers to layers such that after coping source
        // nodes are on layer "0" and target nodes are at the highest numbered
        // layer
        int highestLayerIndex = tmpLayers.size() - 1;
        for (int i = highestLayerIndex; i >= 0; i--) {
            ArrayList<Node> tmpList = tmpLayers.get(i);

            layers.addLayer();

            for (int j = tmpList.size() - 1; j >= 0; j--) {
                layers.getLayer(highestLayerIndex - i).add(tmpList.get(j));
            }
        }
    }

    /**
     * This method checks whether a given node can be placed. <br>
     * First the possible number of nodes per layer given by <code>width</code>
     * is checked. Second, all successors of this node are checked if they are
     * already placed and if so, if they are placed in a lower level.
     * Furthermore the constraint <code>VerticalConstraintWithTwoNodes</code> is
     * checked to satisfy the precondition that adjacent nodes must not be
     * placed on the same level.
     * 
     * @param node
     *            node to check
     * @return true if node can be placed, false otherwise
     */
    private boolean check4Placement(Node node) {
        boolean result = false;

        // Check width
        if (tmpLayers.get(currLayer).size() >= width) {
            tmpLayers.add(new ArrayList<Node>());
            currLayer++;

            return false;
        }

        // Check if succ's are placed
        for (Node succ : node.getOutNeighbors()) {
            if (succ.getBoolean(SugiyamaConstants.PATH_CGPLACED) == false)
                // Non-placed succ
                return false;
            else if (succ.getInteger(SugiyamaConstants.PATH_CGLAYER) == currLayer)
                return false;
        }// all succ's are placed and are in a lower level

        // Check HashMap notSameLevel for adjacent nodes in same
        // level as this node
        if (notSameLevelMap.containsKey(node)) {
            // Check adjacent nodes if they are already placed in currLayer
            // If so return false. This adjacent node is already in currLayer
            // and therefore this node can't also be placed in this layer
            ArrayList<Node> tmpNodeList = notSameLevelMap.get(node);
            for (Node n : tmpNodeList) {
                if (n.getInteger(SugiyamaConstants.PATH_CGLAYER) == currLayer)
                    return false;
            }
        }// No adjacent node of this node is already placed in this layer

        result = true;
        return result;

    }

    /**
     * This private inner class implements a transitive reduction algorithm for
     * an acyclic graph. This is necessary because <code>CoffmanGraham</code>
     * can't handle transitive edges in a graph.
     * <p>
     * First all nodes are sorted topologically according to
     * <code>TopoSort</code>. Then the reflexive, transitive hull as well as the
     * nodes that are not connected by a transitive edge will be determined for
     * each node in descending topological order. Those nodes that are connected
     * by a transitive edge will be added to the transitive Edge set
     * <code>transEdges</code>.
     * 
     * @author scheu
     * @version $Revision$ $Date$
     */
    private class TransitiveReduction {
        /** Topological order */
        private TopoSort topo;

        /** Contains all nodes sorted in topological order */
        private ArrayList<Node> topoNodes;

        /**
         * Stores the reflexive, transitive hull of each node in reverse
         * topological order. The node with the highest topological number is at
         * index "0"
         */
        private ArrayList<HashSet<Node>> rtList;

        /**
         * Stores the neighbor nodes of each node that are connected by a non
         * transitive edge. The node with the highest topological number is at
         * index "0"
         */
        private ArrayList<HashSet<Node>> tredList;

        /**
         * Class Constructor
         */
        public TransitiveReduction() {
            topo = new TopoSort();
            rtList = new ArrayList<HashSet<Node>>(graph.getNumberOfNodes());
            tredList = new ArrayList<HashSet<Node>>(graph.getNumberOfNodes());
        }

        /**
         * Executes the transitive reduction algorithm and all transitive edges
         * of the current graph will be added to the transitive edge set
         * <code>transEdges</code>.
         */
        private void execute() {
            // Reflexive, transitive hull of a node
            HashSet<Node> rtHull;

            // Contains neighbor nodes of a node that are not connected by an
            // transitive edge
            HashSet<Node> transRedNodes;
            // Current node
            Node source;
            // Neighbor node of current node
            Node target;

            // Order the graph topologically
            topo.setData(data);
            topo.attach(graph);
            topo.execute();

            // Sort the nodes according to their topological order
            topoNodes = new ArrayList<Node>(graph.getNodes());
            Collections.sort(topoNodes, new TopologicalComparator());

            // Process all nodes in descending topological order
            for (int i = topoNodes.size() - 1; i >= 0; i--) {
                rtHull = new HashSet<Node>();
                transRedNodes = new HashSet<Node>();

                // Add source node to rtHull
                source = topoNodes.get(i);
                rtHull.add(source);

                // Process all out-neighbors in ascending topological order
                ArrayList<Node> outNs = new ArrayList<Node>(source
                        .getOutNeighbors());
                if (!outNs.isEmpty()) {
                    Collections.sort(outNs, new TopologicalComparator());

                    for (int j = 0; j <= outNs.size() - 1; j++) {
                        target = outNs.get(j);

                        // If target not in rtHull so far, the connection is
                        // non-transitive
                        if (!rtHull.contains(target)) {
                            // Index of target rtHull in rtList
                            int index = graph.getNumberOfNodes()
                                    - target
                                            .getInteger(SugiyamaConstants.PATH_TOPO);

                            // rtHull of target node
                            HashSet<Node> tmpNs = rtList.get(index);

                            // Add rtHull of target node to rtHull of source
                            rtHull.addAll(tmpNs);

                            // Add target to sources transRedNodes
                            transRedNodes.add(target);
                        }
                        // rtHull of source already contains target
                        // -> transitive connection
                        else {
                            // Locate transitive edge
                            for (Edge e : source.getDirectedOutEdges()) {
                                if (e.getTarget() == target) {
                                    transEdges.add(e);
                                }
                            }
                        }
                    }
                }
                // Adding rtHull of source to rtList
                rtList.add(rtHull);
                // Adding transRedNodes of source to tredList
                tredList.add(transRedNodes);
            }
        }
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
