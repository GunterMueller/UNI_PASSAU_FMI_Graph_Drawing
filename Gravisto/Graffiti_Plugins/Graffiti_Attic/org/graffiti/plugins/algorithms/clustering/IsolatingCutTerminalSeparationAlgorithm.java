// ==============================================================================
//
//   IsolatingCutTerminalSeparationAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: IsolatingCutTerminalSeparationAlgorithm.java,v 1.1 2004/10/03 12:27:51
// kaeser Exp $
/*
 * Created on 17.05.2004
 */

package org.graffiti.plugins.algorithms.clustering;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.GraphElementNotFoundException;
import org.graffiti.graph.Node;
import org.graffiti.graphics.LabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

/**
 * An implementation of a Clustering algorithm that works with isolating cuts.
 * It approximates the NP-hard problem of separating n distinct terminals in the
 * network with minimum isolating cuts. Therefore it uses k runs of a max-flow
 * algorithm, where k is the number of terminals and has a complexity of
 * O(k|V|^3).
 * 
 * @author Markus K�ser
 * @version $Revision 1.0 $
 */
public class IsolatingCutTerminalSeparationAlgorithm extends AbstractAlgorithm {

    /** Name of the algorithm */
    private static final String NAME = "Isolating-Cut Terminal Separation";

    /** The logger of the algorithm */
    private static final Logger logger = Logger
            .getLogger(IsolatingCutTerminalSeparationAlgorithm.class.getName());

    /** The number of parameters the algorithm expects */
    private static final int NUMBER_OF_PARAMS = 4;

    /** The index in the parameterlist for the terminal label parameter */
    private static final int TERMINAL_LABEL_INDEX = 0;

    /** The default value for the terminal label parameter */
    private static final String TERMINAL_LABEL_DEFAULT = "T";

    /** The name of the terminal label parameter */
    private static final String TERMINAL_LABEL_NAME = "Terminal node label";

    /** The decription of the terminal label parameter */
    private static final String TERMINAL_LABEL_DESCRIPTION = "The label of the"
            + " terminal nodes, the algorithm will put in different clusters";

    /** The index of the use trivial capacities parameter */
    private static final int USE_TRIVIAL_CAPACITIES_INDEX = 1;

    /** The default value for the use trivial capacities parameter */
    private static final boolean USE_TRIVIAL_CAPACITIES_DEFAULT = false;

    /** The name of the use trivial capacities parameter */
    private static final String USE_TRIVIAL_CAPACITIES_NAME = "Use trivial "
            + "capacities";

    /** The decription of the use trivial capacities parameter */
    private static final String USE_TRIVIAL_CAPACITIES_DESCRIPTION = "Uses "
            + "capacity values of 1 on edges for the algorithm";

    /** The index of the use betweenness capacities parameter */
    private static final int USE_BETWEENNEESS_CAPACITIES_INDEX = 2;

    /** The default value for the use betweenness capacities parameter */
    private static final boolean USE_BETWEENNEESS_CAPACITIES_DEFAULT = false;

    /** The name of the use betweenness capacities parameter */
    private static final String USE_BETWEENNEESS_CAPACITIES_NAME = "Use "
            + "betweenness capacities";

    /** The default value for the use betweenness capacities parameter */
    private static final String USE_BETWEENNEESS_CAPACITIES_DESCRIPTION = "Uses inverted betweenness values as capacities for the algorithm";

    /** the index in the parameterlist of the color cluster parameter */
    private static final int COLOR_CLUSTERS_INDEX = 3;

    /** the default value of the color cluster parameter */
    private static final boolean COLOR_CLUSTERS_DEFAULT = true;

    /** the name of the color cluster parameter */
    private static final String COLOR_CLUSTERS_NAME = "Color Clusters";

    /** the description of the color cluster parameter */
    private static final String COLOR_CLUSTERS_DESCRIPTION = "Visualizes"
            + " different clusters in different colors";

    /** error message */
    private static final String GRAPH_NOT_SET_ERROR = "The graph must be "
            + "attached first";

    /** error message */
    private static final String ALGORITHM_NOT_RUN_ERROR = "The algorithm has"
            + " to be executed before the results can be obtained.";

    /** error message */
    private static final String TERMINALS_NOT_SET_ERROR = "The terminals are"
            + " not set";

    /** error message */
    private static final String TERMINAL_NOT_IN_GRAPH_ERROR = "The terminal is not in the graph";

    /** error message */
    private static final String TERMINALS_NOT_SEPARATED_ERROR = "Terminals are"
            + "not separated.";

    /** error message */
    private static final String SET_TERMINALS_TWICE_ERROR = "Tried to set"
            + " Terminals more than once before the algorithm was reseted";

    /** error message */
    private static final String CAPACITIES_SET_TWICE_ERROR = "Either trivial "
            + "or inverted betweenness can be used as capacities, but not both";

    /** error message */
    private static final String NULL_TERMINAL_SET_ERROR = "Tried to set a terminal"
            + " to a value of null";

    /** error message */
    private static final String NO_TERMINALS_SET_OR_FOUND_ERROR = "No "
            + "Terminals have been set";

    /** the singleton object of <code>clusteringSupportAlgorithms </code> */
    private ClusteringSupportAlgorithms csa = ClusteringSupportAlgorithms
            .getClusteringSupportAlgorithms();

    /**
     * the edges of the terminal sepration cut calculated by this algorithm are
     * stored in this collection after the run of the algorithm
     */
    private Collection terminalSeparationCut;

    /** the singleton object of <code>FlowNetworkSupportAlgorithms </code> */
    private FlowNetworkSupportAlgorithms nsa = FlowNetworkSupportAlgorithms
            .getFlowNetworkSupportAlgorithms();

    /** the temporary source for the calculation of isolating cuts */
    private Node temporarySource;

    /** the temporary sink for the calculation of islolating cuts. */
    private Node temporarySuperSink;

    /** the label of the terminals, set by the user */
    private String terminalLabel;

    /** each Collection in the array contains the nodes of exactly one cluster */
    private Collection[] clusters;

    /** the terminals */
    private Node[] terminals;

    /** true if the main algorithm has been executed, false otherwise */
    private boolean algorithmExecuted;

    /**
     * flag which is set by the user. It determines if the clusters found by the
     * algorithm will be colored differently
     */
    private boolean colorClusters;

    /** true, if the graph is directed. */
    private boolean directed;

    /**
     * determines, if capacity values equal to the inverted betweenness values
     * are used for the edges
     */
    private boolean useInvertedBetweennessCapacities;

    /** determines if capacity values of 1 will be used for every edge */
    private boolean useTrivialCapacities;

    /**
     * contains the cut size which is the sum of the capacities of all nodes in
     * the terminalSeparationCut
     */
    private double cutSize;

    /** the number of terminals */
    private int numberOfTerminals;

    /**
     * Constructs a new instance of the <code>
     * IsolatingCutTerminalSeparationAlgorithm.</code>
     */
    public IsolatingCutTerminalSeparationAlgorithm() {
        super();
        reset(); // inits the values and generates the parameters
    }

    /**
     * Sets all data needed by the algorithm to be executed.
     * 
     * @param g
     *            the graph
     * @param term
     *            the terminals
     * @param trivialCap
     *            uses a capacity of 1.0 on each edge
     * @param betweennessCap
     *            uses a capacity equal to the inverted betweennesss of the edge
     *            as capacity
     * @param color
     *            colors clusters and cut edges differently
     */
    public void setAll(Graph g, Node[] term, boolean trivialCap,
            boolean betweennessCap, boolean color) {
        this.attach(g);
        this.setTerminals(term);
        this.setUseTrivialCapacities(trivialCap);
        this.setUseInvertedBetweennessCapacities(betweennessCap);
        this.setColorClusters(color);
    }

    /**
     * Returns an Array of clusters. The clusters are defined by <code>
     * Collections </code>
     * of nodes. The algorithm must have been executed before trying to get the
     * clusters.
     * 
     * @return the clusters
     * 
     * @throws ClusteringException
     *             if the algorithm was not run before
     */
    public Collection[] getClusters() {
        if (algorithmExecuted)
            return clusters;
        else {
            ClusteringException ce = new ClusteringException(
                    ALGORITHM_NOT_RUN_ERROR);
            ce.log(logger);
            throw ce;
        }
    }

    /**
     * Sets the colorClusters flag. It determines if the clusters, the
     * algorithm, produces are colored in different colors.
     * 
     * @param color
     *            if true the cluster will be colored differently, if false the
     *            will keep their colors.
     */
    public void setColorClusters(boolean color) {
        colorClusters = color;
    }

    /**
     * Returns the total cutsize of the calculated terminal separation cut. The
     * algorithm must have been executed before trying to get the cutsize.
     * 
     * @return the cutsize
     * 
     * @throws ClusteringException
     *             if the algorithm was not run
     */
    public double getCutSize() {
        if (algorithmExecuted)
            return cutSize;
        else {
            ClusteringException ce = new ClusteringException(
                    ALGORITHM_NOT_RUN_ERROR);
            ce.log(logger);
            throw ce;
        }
    }

    /**
     * Returns the name of the algorithm.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return NAME;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#setAlgorithmParameters(org.graffiti.plugin.parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        super.setAlgorithmParameters(params);
        terminalLabel = ((StringParameter) params[TERMINAL_LABEL_INDEX])
                .getString();
        useTrivialCapacities = ((BooleanParameter) params[USE_TRIVIAL_CAPACITIES_INDEX])
                .getBoolean().booleanValue();
        useInvertedBetweennessCapacities = ((BooleanParameter) params[USE_BETWEENNEESS_CAPACITIES_INDEX])
                .getBoolean().booleanValue();
        colorClusters = ((BooleanParameter) params[COLOR_CLUSTERS_INDEX])
                .getBoolean().booleanValue();

        if (terminals == null) {
            // get terminals from the terminal label
            setTerminals(nsa.getNodesWithLabel(graph, terminalLabel));
        }
    }

    /**
     * Returns the edges of the terminal separtation cut (as a <code>
     * Collection </code>
     * of edges). The algorithm must have been executed before applying this
     * method.
     * 
     * @return the cut
     * 
     * @throws ClusteringException
     *             if the algorithm was not run
     */
    public Collection getCut() {
        if (algorithmExecuted)
            return terminalSeparationCut;
        else {
            ClusteringException ce = new ClusteringException(
                    ALGORITHM_NOT_RUN_ERROR);
            ce.log(logger);
            throw ce;
        }
    }

    /**
     * Sets the array of terminals to be separated by the algorithm.
     * 
     * @param ter
     *            the terminals
     * 
     * @throws ClusteringException
     *             if the graph was not set before, at least one of the given
     *             terminals is not an element of the graph, or the setTerminals
     *             method has been rum twice before reseting the algorithm.
     */
    public void setTerminals(Node[] ter) {
        if (graph == null) {
            ClusteringException ce = new ClusteringException(
                    GRAPH_NOT_SET_ERROR);
            ce.log(logger);
            throw ce;
        }

        for (int i = 0; i < ter.length; i++) {
            if (ter[i] == null) {
                ClusteringException ce = new ClusteringException(
                        NULL_TERMINAL_SET_ERROR);
                ce.log(logger);
                throw ce;
            }
            if (ter[i].getGraph() != graph) {
                ClusteringException ce = new ClusteringException(
                        TERMINAL_NOT_IN_GRAPH_ERROR);
                ce.log(logger);
                throw ce;
            }
        }

        // Set the terminals
        if (terminals == null) {
            terminals = ter;
            numberOfTerminals = terminals.length;

            for (int i = 0; i < numberOfTerminals; i++) {
                csa.setTerminalMark(terminals[i]);
            }
        } else {
            ClusteringException ce = new ClusteringException(
                    SET_TERMINALS_TWICE_ERROR);
            ce.log(logger);
            throw ce;
        }
    }

    /**
     * Returns the array of terminals.
     * 
     * @return the terminals
     * 
     * @throws ClusteringException
     *             if no terminals have been set
     */
    public Node[] getTerminals() {
        if (terminals != null)
            return terminals;
        else {
            ClusteringException ce = new ClusteringException(
                    TERMINALS_NOT_SET_ERROR);
            ce.log(logger);
            throw ce;
        }
    }

    /**
     * Sets the useInvertedBetweennessCapacities flag, which determines that
     * betweenness values are computed for the graph. The capacity values, used
     * by the algorithm then are the inverted betweenness values. If other
     * capacity values were stored on the edges of the graph, these are saved
     * before the algorithm and restored afterward. Only one of the flags
     * useInvertedBetweennessCapacities and useTrivialCapacities may be set to
     * true.
     * 
     * @param b
     *            the value for the flag
     */
    public void setUseInvertedBetweennessCapacities(boolean b) {
        useInvertedBetweennessCapacities = b;
    }

    /**
     * Sets the useTrivialCapacities flag, which determines that capacity values
     * of 1.0 are used for every edge for the run of the algorithm. If other
     * capacity values were stored on the edges of the graph, these are saved
     * before the algorithm and restored afterward. Only one of the flags
     * useInvertedBetweennessCapacities and useTrivialCapacities may be set to
     * true.
     * 
     * @param b
     *            the value for the flag
     */
    public void setUseTrivialCapacities(boolean b) {
        useTrivialCapacities = b;
    }

    /**
     * Checks if the attached graph is a directed or undirected flow network
     * with properly set terminals.
     * 
     * @throws PreconditionException
     *             if not all preconditions are satisfied
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        super.check();
        PreconditionException pe = new PreconditionException();
        boolean noError = true;

        if (!nsa.checkDirectedOrUndirected(graph, pe)) {
            noError = false;
        }

        if (useTrivialCapacities && useInvertedBetweennessCapacities) {
            noError = false;
            pe.add(CAPACITIES_SET_TWICE_ERROR);
        }

        if (!useTrivialCapacities && !useInvertedBetweennessCapacities) {
            if (!nsa.checkPositiveCapacities(graph, pe)) {
                noError = false;
            }

            if (!nsa.checkCapacityPrecision(graph, pe)) {
                noError = false;
            }
        }

        if (!csa.checkTerminals(graph, terminals, pe)) {
            noError = false;
            removeAllTerminalData();
        }

        if (!noError)
            throw pe;
    }

    /**
     * Resets terminals to null and removes all the terminal marks.
     */
    private void removeAllTerminalData() {
        csa.removeTerminalMark(terminals);
        terminals = null;
        numberOfTerminals = 0;
    }

    /**
     * Starts the execution of the isolating-cut terminal-separation-algorithm
     * and computes a Collection of edges that separates k given terminals. The
     * calculated information can now be acessed by the <code>
     * getTerminalSeparationCut() </code>
     * , <code> getClusters() </code> and <code> getCutSize() </code> methods.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        try {
            preAlgorithm();
            algorithm();
            postAlgorithm();
        } catch (Throwable t) {
            if (useTrivialCapacities || useInvertedBetweennessCapacities) {
                csa.restoreCapacities(graph);
            }
            removeTemporaryData();
            csa.removeAllClusteringData(graph, false, true);
            reset();
            // t.printStackTrace();
            ClusteringException ce;
            if (t instanceof ClusteringException) {
                ce = (ClusteringException) t;
            } else {
                ce = ClusteringException.getStandardException(t);
            }
            ce.log(logger);
            throw ce;
        }
    }

    /**
     * Resets the algorithm, so it can be applied again with new data.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
        generateParameters();
        algorithmExecuted = false;
        terminalLabel = TERMINAL_LABEL_DEFAULT;
        colorClusters = COLOR_CLUSTERS_DEFAULT;
        useTrivialCapacities = USE_TRIVIAL_CAPACITIES_DEFAULT;
        useInvertedBetweennessCapacities = USE_BETWEENNEESS_CAPACITIES_DEFAULT;
        numberOfTerminals = 0;
        directed = false;
        terminals = null;
        temporarySource = null;
        temporarySuperSink = null;
        terminalSeparationCut = null;
        cutSize = 0.0;
        clusters = null;
    }

    /**
     * The main algorithm itself.
     */
    private void algorithm() {
        Collection[] isolatingCuts = new Collection[numberOfTerminals];

        // make the graph undirected, so the isolating cuts can be computed
        // correctly
        if (directed) {
            graph.setDirected(false, false);
        }

        for (int i = 0; i < numberOfTerminals; i++) {
            temporarySource = terminals[i];
            temporarySuperSink = createSuperSink(i);

            // calculate the isolating cut
            isolatingCuts[i] = csa.get_s_t_cut(graph, temporarySource,
                    temporarySuperSink);

            // if there is no path from source to sink -> isolating cut is empty
            removeTemporaryData();
        }

        // calculate the cut out of all isolating cuts and mark the edges
        terminalSeparationCut = computeTerminalSeparationCut(isolatingCuts);
        csa.setCutEdgeMark(terminalSeparationCut, true);

        // if the graph was directed before the algorithm -> make it directed
        if (directed) {
            graph.setDirected(true, false);
        }

        algorithmExecuted = true;
    }

    /**
     * Computes the cluster of a single terminal, when the terminal separation
     * cut is already calculated. It assumes that that the edges of the cut are
     * already set to be ignored by ignoring BFS.
     * 
     * @param terminalIndex
     *            the index of the terminal.
     * 
     * @throws ClusteringException
     *             if more than one terminals are found in the cluster. In this
     *             case an error has happened in the calculation of the terminal
     *             separation cut.
     */
    private void computeClusterOfTerminal(int terminalIndex) {
        Node tempNode;
        Edge tempEdge;
        int foundTerminals = 0;

        // System.out.println(" Berechne Cluster Nummer " + terminalIndex);
        Node actTerminal = terminals[terminalIndex];

        // start a BFS from the terminal and mark all found nodes
        // the direction of the edges can be ignored because there may not
        // be any path from one of the terminals to another
        Collection cluster = csa.ignoringBFS(graph, actTerminal, true);

        for (Iterator nodeIt = cluster.iterator(); nodeIt.hasNext();) {
            tempNode = (Node) nodeIt.next();

            if (csa.isTerminalMarked(tempNode)) {
                foundTerminals++;
            }
        }

        // remove the marks
        csa.setComponentMark(cluster, false);

        // save cluster in clusters array
        clusters[terminalIndex] = cluster;
        csa.setClusterNumber(cluster, terminalIndex);

        // There has to be exactly one Terminal in this component
        if (foundTerminals > 1)
            throw new ClusteringException(TERMINALS_NOT_SEPARATED_ERROR);
    }

    /**
     * Computes all clusters from the terminal separation cut. This method
     * assumes, that all edges of the <code> terminalSeparationCut </code> are
     * set to be ignored by bfs.
     * 
     * @throws ClusteringException
     *             if the algorithm was not executed before
     */
    private void computeClustersOfAllTerminals() {
        // if the algorithm has been run
        if (algorithmExecuted) {
            // calculate a cluster for every terminal
            for (int i = 0; i < numberOfTerminals; i++) {
                computeClusterOfTerminal(i);
            }
        } else
            throw new ClusteringException(ALGORITHM_NOT_RUN_ERROR);
    }

    /**
     * Calculates a terminal-separation-cut from the given collection of
     * isolating cuts. The result is a non-reduced cut, which means that it may
     * contain edges that are not necessary for separating the terminals.
     * 
     * @param isolatingCuts
     *            the isolating cuts
     * 
     * @return the multiway cut
     */
    private Collection computeTerminalSeparationCut(Collection[] isolatingCuts) {
        // Create Cut by unifying the k-1 lightest isolating Cuts
        Collection separationCut = new LinkedList();
        double[] cutSizes = new double[isolatingCuts.length];
        double maximumCutSize = -1;
        int indexOfMaximumCutSize = -1;

        // calculate cutsizes for all isolating cuts
        for (int i = 0; i < isolatingCuts.length; i++) {
            cutSizes[i] = 0;

            for (Iterator cutIt = isolatingCuts[i].iterator(); cutIt.hasNext();) {
                Edge tempEdge = (Edge) cutIt.next();
                cutSizes[i] += nsa.getCapacity(tempEdge);
            }

            if (cutSizes[i] > maximumCutSize) {
                maximumCutSize = cutSizes[i];
                indexOfMaximumCutSize = i;
            }
        }

        // get all but the heaviest isolating Cut, avoiding dublicates
        for (int i = 0; i < isolatingCuts.length; i++) {
            if (i != indexOfMaximumCutSize) {
                for (Iterator isoIt = isolatingCuts[i].iterator(); isoIt
                        .hasNext();) {
                    Edge tempEdge = (Edge) isoIt.next();

                    // not a dublicate
                    if (!separationCut.contains(tempEdge)) {
                        separationCut.add(tempEdge);
                    }
                }
            }
        }

        return separationCut;
    }

    /**
     * Creates one super sink for all terminals except the one with the given
     * index and generates edges between these terminals and the super sink with
     * a maximum capacity.
     * 
     * @param indexOfTerminal
     *            the index of the terminal which will not be connected to the
     *            super sink
     * 
     * @return the super sink
     */
    private Node createSuperSink(int indexOfTerminal) {
        Node actTerminal = terminals[indexOfTerminal];
        LabelAttribute edgeMaxCapAttribute = null;
        Edge tempEdge = null;

        // create SuperSink
        Node superSink = graph.addNode();

        // create Edges from other Terminals to SuperSink
        for (int j = 0; j < indexOfTerminal; j++) {
            tempEdge = graph.addEdge(terminals[j], superSink, graph
                    .isDirected());
            nsa.setCapacity(tempEdge,
                    FlowNetworkSupportAlgorithms.MAXIMUM_CAPACITY);
        }

        for (int j = indexOfTerminal + 1; j < numberOfTerminals; j++) {
            tempEdge = graph.addEdge(terminals[j], superSink, graph
                    .isDirected());
            nsa.setCapacity(tempEdge,
                    FlowNetworkSupportAlgorithms.MAXIMUM_CAPACITY);
        }

        return superSink;
    }

    /**
     * Generates the parameters for the algorithm.
     */
    private void generateParameters() {
        StringParameter terminalLabelParam = new StringParameter(
                TERMINAL_LABEL_DEFAULT, TERMINAL_LABEL_NAME,
                TERMINAL_LABEL_DESCRIPTION);
        BooleanParameter useTrivialCapParam = new BooleanParameter(
                USE_TRIVIAL_CAPACITIES_DEFAULT, USE_TRIVIAL_CAPACITIES_NAME,
                USE_TRIVIAL_CAPACITIES_DESCRIPTION);
        BooleanParameter useBetweennessCapParam = new BooleanParameter(
                USE_BETWEENNEESS_CAPACITIES_DEFAULT,
                USE_BETWEENNEESS_CAPACITIES_NAME,
                USE_BETWEENNEESS_CAPACITIES_DESCRIPTION);
        BooleanParameter colorClustersParam = new BooleanParameter(
                COLOR_CLUSTERS_DEFAULT, COLOR_CLUSTERS_NAME,
                COLOR_CLUSTERS_DESCRIPTION);

        parameters = new Parameter[NUMBER_OF_PARAMS];
        parameters[TERMINAL_LABEL_INDEX] = terminalLabelParam;
        parameters[USE_TRIVIAL_CAPACITIES_INDEX] = useTrivialCapParam;
        parameters[USE_BETWEENNEESS_CAPACITIES_INDEX] = useBetweennessCapParam;
        parameters[COLOR_CLUSTERS_INDEX] = colorClustersParam;
    }

    /**
     * Checks, if all edges in the terminal-separation-cut are needed to
     * separate the terminals and removes edges from the cut that are not
     * needed. Doing this, it computes the clusters for the terminals.
     */
    private void minimizeCutAndComputeClusters() {
        Edge tempEdge;
        Node source;
        Node target;
        int sourceNumber;
        int targetNumber;
        clusters = new Collection[numberOfTerminals];

        // setting all edges of the terminal-separation-cut to be ignored
        // by the BFS.
        csa.setIgnoredByBFS(terminalSeparationCut, true);

        // compute for each terminal its cluster with ignoringBFS
        computeClustersOfAllTerminals();

        for (Iterator edgeIt = terminalSeparationCut.iterator(); edgeIt
                .hasNext();) {
            tempEdge = (Edge) edgeIt.next();
            source = tempEdge.getSource();
            target = tempEdge.getTarget();
            sourceNumber = -1;
            targetNumber = -1;

            try {
                sourceNumber = csa.getClusterNumber(source);
            } catch (ClusteringException ce) {
            }

            try {
                targetNumber = csa.getClusterNumber(target);
            } catch (ClusteringException ce) {
            }

            // check if the edge is necessary for the cut

            // if source and target are in the same cluster
            if ((sourceNumber != -1) && (targetNumber != -1)
                    && (sourceNumber == targetNumber)) {
                csa.setCutEdgeMark(tempEdge, false);
                csa.setIgnoredByBFS(tempEdge, false);
                edgeIt.remove();

                // simpy remove edge from cut
            }

            // if source is not in a cluster and target is in a cluster
            if ((sourceNumber == -1) && (targetNumber != -1)) {
                csa.setCutEdgeMark(tempEdge, false);
                csa.setIgnoredByBFS(tempEdge, false);
                edgeIt.remove();

                // recompute this cluster
                computeClusterOfTerminal(targetNumber);
            }

            // if source is in a cluster and target is not in a cluster
            if ((sourceNumber != -1) && (targetNumber == -1)) {
                csa.setCutEdgeMark(tempEdge, false);
                csa.setIgnoredByBFS(tempEdge, false);
                edgeIt.remove();

                // recompute this cluster
                computeClusterOfTerminal(sourceNumber);
            }

            // if source and target are both in no cluster
            if ((sourceNumber == -1) && (targetNumber == -1)) {
                csa.setCutEdgeMark(tempEdge, false);
                csa.setIgnoredByBFS(tempEdge, false);
                edgeIt.remove();
            }
        }

        // Removing the ignored mark from the cut edges
        csa.setIgnoredByBFS(terminalSeparationCut, false);
    }

    /**
     * Computations to be done after the run of the algorithm.
     */
    private void postAlgorithm() {
        // minimizes the cut by removing those edges from it, that are not
        // necessary for separating the terminals and computes for each terminal
        // the cluster it is in by ignoringBfs
        minimizeCutAndComputeClusters();
        cutSize = csa.computeMultiwayCutSize(terminalSeparationCut);

        // perhaps remove generated capacities and restore old values
        if (useTrivialCapacities || useInvertedBetweennessCapacities) {
            csa.restoreCapacities(graph);
        }

        if (colorClusters) {
            csa.colorClustersAndCutEdges(clusters, terminalSeparationCut);
        }
        csa.removeTerminalMark(graph);
    }

    /**
     * Computations to be done before the run of the algorithm itself.
     */
    private void preAlgorithm() {
        // remove everything, that the algorithm itself could have produced and
        // now may cause problems. The terminal marks may not be removed,
        // because
        // they are set via setAll or setParameters methods.
        csa.removeAllClusteringData(graph, false, false);
        csa.colorGraphToNormal(graph);

        // store data
        directed = graph.isDirected();

        // perhaps store old capacities and generate new ones
        if (useTrivialCapacities || useInvertedBetweennessCapacities) {
            csa.storeOldAndGenerateNewCapacities(graph, useTrivialCapacities,
                    useInvertedBetweennessCapacities);
        }
    }

    /**
     * Removes the temporary data of one iteration of the algorithm, especially
     * the superSink.
     */
    private void removeTemporaryData() {
        nsa.removeFlow(graph);
        csa.removeComponentMark(graph);

        // remove actual Super Sink
        if (temporarySuperSink != null) {
            try {
                graph.deleteNode(temporarySuperSink);
                temporarySource = null;
                temporarySuperSink = null;
            } catch (GraphElementNotFoundException genfe) {
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
