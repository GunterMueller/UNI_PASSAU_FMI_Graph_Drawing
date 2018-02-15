/*
 * Created on 13.07.2004
 */

package org.graffiti.plugins.algorithms.clustering;

import java.util.Collection;
import java.util.logging.Logger;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringParameter;

/**
 * An implementation of a Clustering algorithm that works with a gomory-hu-tree.
 * It approximates the NP-hard problem of separating n distinct terminals in the
 * network. Therefore computes th gomory-hu-tree to the given graph and
 * generates an optimal solution for the problem on the tree. Afterwards it
 * transfers this solution to the original graph. The optimality of the result
 * can no longer be obtained.
 * 
 * @author Markus K�ser
 * @version $Revision 1.1 $
 */
public class GomoryHuTreeTerminalSeparationAlgorithm extends AbstractAlgorithm {
    private static final String NAME = "Gomory-Hu-Tree Terminal Separation";

    /** The logger of the algorithm */
    private static final Logger logger = Logger
            .getLogger(GomoryHuTreeTerminalSeparationAlgorithm.class.getName());

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

    /** the path for storing the terminal flag on nodes */
    private static final String TERMINAL_FLAG_PATH = ClusteringSupportAlgorithms.BASE
            + "terminalFlagPath";

    /** error message */
    private static final String GRAPH_NOT_SET_ERROR = "The graph must be "
            + "attached first";

    /** error message */
    private static final String TERMINALS_NOT_SET_ERROR = "The terminals are"
            + " not set";

    /** error message */
    private static final String TERMINAL_NOT_IN_GRAPH_ERROR = "The terminal is "
            + "not in the graph";

    /** error message */
    private static final String NO_TERMINALS_SET_OR_FOUND_ERROR = "No "
            + "Terminals have been set";

    /** error message */
    private static final String SET_TERMINALS_TWICE_ERROR = "Tried to set"
            + " Terminals more than once before the algorithm was reseted";

    /** error message */
    private static final String ALGORITHM_NOT_RUN_ERROR = "The algorithm has"
            + " to be executed before the results can be obtained.";

    /** error message */
    private static final String FOUND_COMPONENT_WITHOUT_TERMINAL_ERROR = "There has to be a at least one terminal in each weakly connected "
            + "component";

    /** error message! */
    private static final String CAPACITIES_SET_TWICE_ERROR = "Either trivial "
            + "or inverted betweenness can be used as capacities, but not both";

    /** error message! */
    private static final String PRECONDITIONS_OF_ICTSA_NOT_OK_ERROR = "The preconditions of the underlying isolating-cut terminal-separation"
            + "algorithm are not satisfied";

    /** error message */
    private static final String NULL_TERMINAL_SET = "Tried to set a terminal"
            + " to a value of null";

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

    /** the label of the terminals, set by the user */
    private String terminalLabel;

    /** each Collection in the array contains the nodes of exactly one cluster */
    private Collection[] clusters;

    /** The Gomory-Hu-Tree used for the computation. */
    private GomoryHuTree gomoryHuTree;

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

    /** The subalgorithm used in this algorithm */
    private BfsTreeTerminalSeparation btts;

    /**
     * 
     */
    public GomoryHuTreeTerminalSeparationAlgorithm() {
        super();
        reset();
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
        if (graph == null)
            throw new ClusteringException(GRAPH_NOT_SET_ERROR);

        for (int i = 0; i < ter.length; i++) {
            if (ter[i] == null) {
                ClusteringException ce = new ClusteringException(
                        NULL_TERMINAL_SET);
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
     * Checks if the preconditions of the algorithm are satisfied.
     * 
     * @throws PreconditionException
     *             if the preconditions are not satisfied
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
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return NAME;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        try {
            preAlgorithm();
            algorithm();
            postAlgorithm();
        } catch (Throwable t) {
            csa.removeAllClusteringData(graph, false, true);
            reset();
            if (useTrivialCapacities || useInvertedBetweennessCapacities) {
                csa.restoreCapacities(graph);
            }
            if (gomoryHuTree != null) {
                gomoryHuTree.removeGraphData();
            }
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
     * The main algorithm itself.
     */
    private void algorithm() {
        // generate Gomory-Hu-Tree to the graph
        gomoryHuTree = new GomoryHuTree(graph);
        Graph tree = gomoryHuTree.getTree();

        // convert graph terminals to tree terminals
        Node[] treeTerminals = new Node[numberOfTerminals];
        for (int i = 0; i < numberOfTerminals; i++) {
            treeTerminals[i] = gomoryHuTree
                    .convertGraphNodeToTreeNode(terminals[i]);
        }

        // apply isolating cut terminal separation algorithm to gomory hu tree
        btts = new BfsTreeTerminalSeparation(tree, treeTerminals);

        btts.execute();

        Collection treeClusters[] = btts.getClusters();
        double treeCutSize = btts.getCutSize();

        clusters = gomoryHuTree
                .convertTreeClustersToGraphClusters(treeClusters);
        terminalSeparationCut = csa.computeCutFromClusters(graph, clusters);
        cutSize = csa.computeMultiwayCutSize(terminalSeparationCut);
    }

    /**
     * Computations to be done after the run of the algorithm.
     */
    private void postAlgorithm() {
        cutSize = csa.computeMultiwayCutSize(terminalSeparationCut);

        // perhaps remove generated capacities and restore old values
        if (useTrivialCapacities || useInvertedBetweennessCapacities) {
            csa.restoreCapacities(graph);
        }

        if (colorClusters) {
            csa.colorClustersAndCutEdges(clusters, terminalSeparationCut);
        }
        csa.removeTerminalMark(terminals);
        gomoryHuTree.removeGraphData();
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
        gomoryHuTree = null;
        numberOfTerminals = 0;
        terminals = null;
        terminalSeparationCut = null;
        cutSize = 0.0;
        clusters = null;
        btts = null;
    }
}
