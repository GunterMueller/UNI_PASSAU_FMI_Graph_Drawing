// =============================================================================
//
//   SugiyamaData.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SugiyamaData.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugins.algorithms.sugiyama.SugiyamaAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.constraints.SugiyamaConstraint;
import org.graffiti.plugins.grids.OrthogonalGrid;
import org.graffiti.plugins.grids.RadialGrid;

/**
 * This class stores the results of each individual phase in the
 * sugiyama-algorithm:
 * <ul>
 * <li>Which edges have been reversed in the decycling-phase
 * <li>Which nodes are dummy-nodes and have to be removed
 * <li>Which node is on which layer in the graph
 * </ul>
 */
public class SugiyamaData extends AbstractAlgorithm {
    public static final String INITIAL_CROSSING_COUNT = "initialCrossingCount";
    public static final String CROSSING_COUNT = "crossingCount";
    public static final String INITIAL_LEVEL_COUNT = "initialLevelCount";

    /** Store the reversed edges */
    protected HashSet<Edge> reversedEdges;

    /** Store the deleted edges */
    protected HashSet<Edge> deletedEdges;

    /** Store the dummy-nodes */
    protected HashSet<Node> dummyNodes;

    /** Store the layout of the layers */
    protected NodeLayers layers;

    /** If a node has been selected by the user, it is stored here */
    protected Node startNode;

    /** The graph attached to the algorithms */
    // protected Graph graph;

    /** Currently available phase-algorithms */
    protected HashMap<String, String> phaseAlgorithms;

    /** binary-names of all available algorithms - to create an instance */
    protected ArrayList<String[]> algorithmBinaryNames;

    /** the selected algorithms */
    protected SugiyamaAlgorithm[] algorithms;

    /** the last algorithms that the user selected in the algorithm-chooser */
    protected String[] lastSelectedAlgorithms;

    /**
     * this toggles the use of an alternative lexicographical comparison in
     * barycenter
     */
    protected boolean alternateLex;

    /**
     * parameters of the framework itself - they cannot be stored in the
     * framework because of the layout of the configuration-dialog
     */
    // protected Parameter[] parameters;

    /** toggles animation of the first 4 phases */
    protected boolean animated;

    /** constraints in the graph */
    protected HashSet<SugiyamaConstraint> constraints;

    /** map, to map a sugiyama identifier to a node in the graph */
    protected HashMap<String, Node> nodeMap;

    /** caches instances of algorithms */
    protected HashMap<String, SugiyamaAlgorithm> algorithmMap;

    /** this hashmap stores arbitrary user-data */
    protected HashMap<String, Object> objects;

    protected HashSet<BigNode> bigNodes;

    protected String algorithmType;

    /** only used by cyclic layout: offset from the point of origin */
    private int cyclicLayoutRadiusOffset = 100;
    /** only used by cyclic layout: spacing between adjacent (dummy-)nodes */
    private int cyclicLayoutRadiusDelta = 50;

    int bigNodePolicy;

    int constraintPolicy;

    protected Collection<Edge> selfLoops;

    private Grid gridType;

    private Parameter<?>[] gridParameters;

    private Integer crossingChange;

    private boolean gridActivated;

    /**
     * Default constructor - initialize the underlying data-structures
     */
    public SugiyamaData() {
        algorithmType = SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA;
        reversedEdges = new HashSet<Edge>();
        deletedEdges = new HashSet<Edge>();
        dummyNodes = new HashSet<Node>();
        layers = new NodeLayers();
        startNode = null;
        algorithmBinaryNames = null;
        algorithms = new SugiyamaAlgorithm[4];
        lastSelectedAlgorithms = new String[4];
        alternateLex = false;
        animated = false;
        constraints = new HashSet<SugiyamaConstraint>();
        nodeMap = new HashMap<String, Node>();
        algorithmMap = new HashMap<String, SugiyamaAlgorithm>();
        buildFrameworkParameters();
        objects = new HashMap<String, Object>();
        bigNodes = new HashSet<BigNode>();
        gridActivated = false;
    }

    public SugiyamaData(boolean incremental) {
        // do nothing
    }

    public void setGridParameters(Parameter<?>[] params) {
        this.gridParameters = params;
    }

    public Parameter<?>[] getGridParameters() {
        return this.gridParameters;
    }

    public void setGridType(Grid grid) {
        this.gridType = grid;
    }

    public Grid getGridType() {
        return this.gridType;
    }

    public void setCrossingChange(Integer c) {
        this.crossingChange = c;
    }

    public Integer getCrossingChange() {
        return this.crossingChange;
    }

    /**
     * Accessor for the reversed edges
     * 
     * @return the reversed edges
     */
    public HashSet<Edge> getReversedEdges() {
        return reversedEdges;
    }

    /**
     * Setter for the reversed edges
     * 
     * @param edges
     *            The Reversed edges
     */
    public void setReversedEdges(HashSet<Edge> edges) {
        this.reversedEdges = edges;
    }

    /**
     * Accessor for the dummy-nodes
     * 
     * @return the dummy-nodes
     */
    public HashSet<Node> getDummyNodes() {
        return this.dummyNodes;
    }

    /**
     * Setter for the dummy-nodes
     * 
     * @param dummies
     *            the dummy-nodes
     */
    public void setDummyNodes(HashSet<Node> dummies) {
        this.dummyNodes = dummies;
    }

    /**
     * Accessor for the layers in the graph
     * 
     * @return the layers in the graph
     */
    public NodeLayers getLayers() {
        return this.layers;
    }

    /**
     * Setter for the layers in the graph
     * 
     * @param theLayers
     *            the layers in the graph
     */
    public void setLayers(NodeLayers theLayers) {
        this.layers = theLayers;
    }

    public void setStartNode(Node n) {
        startNode = n;
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setGraph(Graph g) {
        this.graph = g;
    }

    public Graph getGraph() {
        return this.graph;
    }

    public HashMap<String, String> getPhaseAlgorithms() {
        return this.phaseAlgorithms;
    }

    public void setPhaseAlgorithms(HashMap<String, String> algo) {
        this.phaseAlgorithms = algo;
    }

    public ArrayList<String[]> getAlgorithmBinaryNames() {
        return this.algorithmBinaryNames;
    }

    public void setAlgorithmBinaryNames(ArrayList<String[]> binaryNames) {
        this.algorithmBinaryNames = binaryNames;
    }

    public SugiyamaAlgorithm[] getSelectedAlgorithms() {
        return this.algorithms;
    }

    public void setSelectedAlgorithms(SugiyamaAlgorithm[] phases) {
        this.algorithms = phases;
    }

    public String[] getLastSelectedAlgorithms() {
        return this.lastSelectedAlgorithms;
    }

    public void setLastSelectedAlgorithms(String[] algos) {
        this.lastSelectedAlgorithms = algos;
    }

    public boolean getAlternateLex() {
        return this.alternateLex;
    }

    public void setAlternateLex(boolean b) {
        this.alternateLex = b;
    }

    public boolean gridActivated() {
        return this.gridActivated;
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return this.parameters;
    }

    @Override
    public Parameter<?>[] getDefaultParameters() {

        BooleanParameter p0 = new BooleanParameter(false, "Animate",
                "Turn on support for animations");
        String[] drawing = new String[] {
                SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA,
                SugiyamaConstants.PARAM_RADIAL_SUGIYAMA,
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA };
        StringSelectionParameter p1 = new StringSelectionParameter(drawing,
                "Drawing-method", "Please select the drawing-method");
        String[] bNodes = new String[] {
                SugiyamaConstants.PARAM_BIG_NODE_HANDLE,
                SugiyamaConstants.PARAM_BIG_NODE_SHRINK,
                SugiyamaConstants.PARAM_BIG_NODE_IGNORE };
        StringSelectionParameter p2 = new StringSelectionParameter(bNodes,
                "Big nodes", "Choose how to handle big nodes");
        p2.setSelectedValue(2);
        String[] constraintHandling = new String[] {
                SugiyamaConstants.PARAM_CONSTRAINTS_HANDLE,
                SugiyamaConstants.PARAM_CONSTRAINTS_IGNORE };
        StringSelectionParameter p3 = new StringSelectionParameter(
                constraintHandling, "Constraints",
                "Choose how to handle constraints");
        p3.setSelectedValue(1);

        BooleanParameter p4 = new BooleanParameter(false, "Grid",
                "Activate the grid");

        return new Parameter<?>[] { p0, p1, p2, p3, p4 };
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        this.animated = ((BooleanParameter) params[0]).getBoolean();
        String drawing = ((StringSelectionParameter) params[1])
                .getSelectedValue();
        algorithmType = drawing;
        String bigNodes = ((StringSelectionParameter) params[2])
                .getSelectedValue();
        if (bigNodes.equals(SugiyamaConstants.PARAM_BIG_NODE_HANDLE)) {
            bigNodePolicy = SugiyamaConstants.BIG_NODES_HANDLE;
        } else if (bigNodes.equals(SugiyamaConstants.PARAM_BIG_NODE_SHRINK)) {
            bigNodePolicy = SugiyamaConstants.BIG_NODES_SHRINK;
        } else {
            bigNodePolicy = SugiyamaConstants.BIG_NODES_IGNORE;
        }

        String constraints = ((StringSelectionParameter) params[3])
                .getSelectedValue();
        if (constraints.equals(SugiyamaConstants.PARAM_CONSTRAINTS_HANDLE)) {
            constraintPolicy = SugiyamaConstants.CONSTRAINTS_HANDLE;
        } else {
            constraintPolicy = SugiyamaConstants.CONSTRAINTS_IGNORE;
        }

        if (((BooleanParameter) params[4]).getBoolean()) {
            if (algorithmType.equals(SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
                gridType = new RadialGrid();
                gridActivated = true;
            } else if (algorithmType
                    .equals(SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)) {
                gridType = new OrthogonalGrid();
                gridActivated = true;
            } else {
                gridType = null;
            }
        }
        PreferencesUtil.saveFrameworkParameters(this);

    }

    private void buildFrameworkParameters() {
        BooleanParameter animate = new BooleanParameter(false, "Animate",
                "Turn on support for animations");
        String[] drawing = new String[] {
                SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA,
                SugiyamaConstants.PARAM_RADIAL_SUGIYAMA,
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA };
        StringSelectionParameter drawingMethod = new StringSelectionParameter(
                drawing, "Drawing-method", "Please select the drawing-method");
        String[] bNodes = new String[] {
                SugiyamaConstants.PARAM_BIG_NODE_HANDLE,
                SugiyamaConstants.PARAM_BIG_NODE_SHRINK,
                SugiyamaConstants.PARAM_BIG_NODE_IGNORE };
        StringSelectionParameter bigNodes = new StringSelectionParameter(
                bNodes, "Big nodes", "Choose how to handle big nodes");
        bigNodes.setSelectedValue(2);
        String[] constraintHandling = new String[] {
                SugiyamaConstants.PARAM_CONSTRAINTS_HANDLE,
                SugiyamaConstants.PARAM_CONSTRAINTS_IGNORE };
        StringSelectionParameter constraints = new StringSelectionParameter(
                constraintHandling, "Constraints",
                "Choose how to handle constraints");
        constraints.setSelectedValue(1);

        BooleanParameter gridParam = new BooleanParameter(false, "Grid",
                "Activate the grid");

        this.parameters = new Parameter[] { animate, drawingMethod, bigNodes,
                constraints, gridParam };
        PreferencesUtil.loadFrameworkParameters(this);
    }

    public boolean isAnimated() {
        return this.animated;
    }

    public void setAnimated(boolean b) {
        this.animated = b;
    }

    public HashSet<SugiyamaConstraint> getConstraints() {
        return this.constraints;
    }

    public void setConstraints(HashSet<SugiyamaConstraint> constraints) {
        this.constraints = constraints;
    }

    public void setNodeMap(HashMap<String, Node> map) {
        this.nodeMap = map;
    }

    public HashMap<String, Node> getNodeMap() {
        return this.nodeMap;
    }

    public void setAlgorithmMap(HashMap<String, SugiyamaAlgorithm> theMap) {
        this.algorithmMap = theMap;
    }

    public HashMap<String, SugiyamaAlgorithm> getAlgorithmMap() {
        return this.algorithmMap;
    }

    /**
     * Reset the bean to a initial state without loosing necessary information
     */
    @Override
    public void reset() {
        reversedEdges = new HashSet<Edge>();
        deletedEdges = new HashSet<Edge>();
        dummyNodes = new HashSet<Node>();
        layers = new NodeLayers();
        startNode = null;
        graph = null;
        constraints = new HashSet<SugiyamaConstraint>();
        nodeMap = new HashMap<String, Node>();
        this.gridActivated = false;
    }

    public Object putObject(String key, Object data) {
        return objects.put(key, data);
    }

    public Object getObject(String key) {
        return objects.get(key);
    }

    public boolean containsKey(String key) {
        return objects.containsKey(key);
    }

    public boolean containsObject(Object value) {
        return objects.containsValue(value);
    }

    public Object removeObject(String key) {
        return objects.remove(key);
    }

    public void setDeletedEdges(HashSet<Edge> edges) {
        this.deletedEdges = edges;
    }

    public HashSet<Edge> getDeletedEdges() {
        return this.deletedEdges;
    }

    public HashSet<BigNode> getBigNodes() {
        return this.bigNodes;
    }

    public void setBigNodes(HashSet<BigNode> bNodes) {
        this.bigNodes = bNodes;
    }

    /**
     * Returns the type of the algorithm.
     * 
     * @return <code>SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA</code>,
     *         <code>SugiyamaConstants.PARAM_RADIAL_SUGIYAMA</code> or
     *         <code>SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA</code>
     */
    public String getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(String s) {
        this.algorithmType = s;
    }

    public int getBigNodesPolicy() {
        return this.bigNodePolicy;
    }

    public int getConstraintPolicy() {
        return this.constraintPolicy;
    }

    /**
     * Returns a copy of this SugiyamaData-object
     * 
     * @return A copy of this SugiyamaData-object
     */
    public SugiyamaData copy() {
        SugiyamaData copy = new SugiyamaData();
        return copy(copy);

    }

    public SugiyamaData copy(SugiyamaData copy) {
        HashSet<Edge> e = new HashSet<Edge>();

        e.addAll(this.reversedEdges);
        copy.setReversedEdges(e);

        e = new HashSet<Edge>();
        e.addAll(this.deletedEdges);
        copy.setDeletedEdges(e);

        HashSet<Node> n = new HashSet<Node>();
        n.addAll(this.dummyNodes);
        copy.setDummyNodes(n);

        copy.setLayers(layers.clone());

        copy.setStartNode(this.startNode);

        copy.setGraph(this.graph);

        HashMap<String, String> pa = new HashMap<String, String>();
        pa.putAll(this.phaseAlgorithms);
        copy.setPhaseAlgorithms(pa);

        ArrayList<String[]> abn = new ArrayList<String[]>();
        abn.addAll(this.algorithmBinaryNames);
        copy.setAlgorithmBinaryNames(abn);

        SugiyamaAlgorithm[] alg = new SugiyamaAlgorithm[4];
        for (int i = 0; i < alg.length; i++) {
            alg[i] = this.algorithms[i];
        }
        copy.setSelectedAlgorithms(alg);

        String[] lsa = new String[this.lastSelectedAlgorithms.length];
        for (int i = 0; i < lsa.length; i++) {
            lsa[i] = this.lastSelectedAlgorithms[i];
        }
        copy.setLastSelectedAlgorithms(lsa);

        copy.setAlternateLex(this.alternateLex);

        Parameter<?>[] fp = new Parameter[this.parameters.length];
        for (int i = 0; i < fp.length; i++) {
            fp[i] = this.parameters[i];
        }

        copy.setAnimated(this.animated);

        HashSet<SugiyamaConstraint> c = new HashSet<SugiyamaConstraint>();
        c.addAll(this.constraints);
        copy.setConstraints(c);

        HashMap<String, Node> nm = new HashMap<String, Node>();
        nm.putAll(this.nodeMap);
        copy.setNodeMap(nm);

        HashMap<String, SugiyamaAlgorithm> algm = new HashMap<String, SugiyamaAlgorithm>();
        algm.putAll(this.algorithmMap);
        copy.setAlgorithmMap(algm);

        HashMap<String, Object> o = new HashMap<String, Object>();
        o.putAll(this.objects);
        copy.objects = o;

        HashSet<BigNode> bn = new HashSet<BigNode>();
        bn.addAll(this.bigNodes);
        copy.setBigNodes(bn);

        if (this.selfLoops != null) {

            copy.selfLoops = new LinkedList<Edge>(this.selfLoops);
        }

        copy.algorithmType = this.algorithmType;

        copy.bigNodePolicy = this.bigNodePolicy;

        copy.constraintPolicy = this.constraintPolicy;

        copy.gridActivated = this.gridActivated;
        copy.gridParameters = this.gridParameters;
        copy.gridType = this.gridType;

        return copy;
    }

    /**
     * Returns the selfLoops.
     * 
     * @return the selfLoops.
     */
    public Collection<Edge> getSelfLoops() {
        return selfLoops;
    }

    /**
     * Sets the selfLoops.
     * 
     * @param selfLoops
     *            the selfLoops to set.
     */
    public void setSelfLoops(Collection<Edge> selfLoops) {
        this.selfLoops = selfLoops;
    }

    public int getCyclicLayoutRadiusOffset() {
        return cyclicLayoutRadiusOffset;
    }

    public void setCyclicLayoutRadiusOffset(int cyclicLayoutRadiusOffset) {
        this.cyclicLayoutRadiusOffset = cyclicLayoutRadiusOffset;
    }

    public int getCyclicLayoutRadiusDelta() {
        return cyclicLayoutRadiusDelta;
    }

    public void setCyclicLayoutRadiusDelta(int cyclicLayoutRadiusDelta) {
        this.cyclicLayoutRadiusDelta = cyclicLayoutRadiusDelta;
    }

    public void execute() {
        throw new UnsupportedOperationException(
                "I only pretend to be an algorithm!");
    }

    public String getName() {
        throw new UnsupportedOperationException(
                "I only pretend to be an algorithm!");
    }

    public String dumpObjects() {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, Object> entry : objects.entrySet()) {
            builder.append(entry.getKey());
            builder.append(" = ");
            builder.append(entry.getValue());
            builder.append("\n");
        }
        return builder.toString();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
