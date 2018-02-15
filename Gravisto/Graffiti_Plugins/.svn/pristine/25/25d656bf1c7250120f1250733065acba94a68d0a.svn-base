// ==============================================================================
//
//   IsolatingCutClusteringAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: IsolatingCutClusteringAlgorithm.java,v 1.1 2004/10/03 12:27:51 kaeser
// Exp $

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
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

/**
 * An implementation of a Clustering algorithm that works with the <code>
 * IsolatingCutTerminalSeparationAlgorithm </code>
 * as subalgorithm. It approximates the NP-hard Problem of generating a
 * multiway-cut from a given graph. The complexity of the algorithm is
 * O(k*|V|^3) where k is the wanted number of clusters.
 * 
 * @author Markus K�ser
 * @version $Revision 0.1 $
 */
public class IsolatingCutClusteringAlgorithm extends AbstractAlgorithm {

    /** The name of the algorithm */
    private static final String NAME = "Isolating-Cut Clustering";

    /** The number of parameters the algorithm expects */
    private static final int NUMBER_OF_PARAMS = 5;

    /** The logger of the algorithm */
    private static final Logger logger = Logger
            .getLogger(IsolatingCutClusteringAlgorithm.class.getName());

    /** The index in the parameterlist of the number of clusters parameter */
    private static final int NUMBER_OF_CLUSTERS_INDEX = 0;

    /** The default value of the number of clusters parameter */
    private static final int NUMBER_OF_CLUSTERS_DEFAULT = 1;

    /** The name of the number of clusters parameter */
    private static final String NUMBER_OF_CLUSTERS_NAME = "Number of clusters";

    /** The description of the number of clusters parameter */
    private static final String NUMBER_OF_CLUSTERS_DESCRIPTION = "The number of clusters the algorithm will create";

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

    /** The description for the use betweenness capacities parameter */
    private static final String USE_BETWEENNEESS_CAPACITIES_DESCRIPTION = "Uses inverted betweenness values as capacities for the algorithm";

    /** The index of the compute terminals parameter */
    private static final int COMPUTE_TERMINALS_INDEX = 3;

    /** The default value for the compute terminals capacities parameter */
    private static final boolean COMPUTE_TERMINALS_DEFAULT = false;

    /** The name of the compute terminals parameter */
    private static final String COMPUTE_TERMINALS_NAME = "Compute Terminals";

    /** The description for the compute terminals parameter */
    private static final String COMPUTE_TERMINALS_DESCRIPTION = "Use a special terminal computation in stead of a random choosing";

    /** The index in the parameterlist of the color cluster parameter */
    private static final int COLOR_CLUSTERS_INDEX = 4;

    /** The default value of the color cluster parameter */
    private static final boolean COLOR_CLUSTERS_DEFAULT = true;

    /** The name of the color cluster parameter */
    private static final String COLOR_CLUSTERS_NAME = "Color Clusters";

    /** The description of the color cluster parameter */
    private static final String COLOR_CLUSTERS_DESCRIPTION = "Visualizes"
            + " different clusters in different colors";

    /** error message */
    private static final String PRECONITIONS_OF_ICTSA_NOT_SATISFIED_ERROR = "Not all preconditions of the isolationg-Cut terminal-separation-"
            + "algorithm have been satisfied";

    /** error message */
    private static final String CAPACITIES_SET_TWICE_ERROR = "Either trivial "
            + "or inverted betweenness can be used as capacities, but not both";

    /** error message */
    private static final String ALGORITHM_NOT_RUN_ERROR = "The algorithm has"
            + " to be executed before the results can be obtained.";

    /** Error message */
    private static final String GRAPH_NOT_SET_ERROR = "The graph must be "
            + "attached first.";

    /** Error message */
    private static final String NUMBER_OF_CLUSTERS_ERROR = "The number of "
            + "clusters must be greater or equal to the number of connected "
            + "components of the graph and less or equal to the number of "
            + "nodes";

    /** the singleton object of <code>clusteringSupportAlgorithms </code> */
    private ClusteringSupportAlgorithms csa = ClusteringSupportAlgorithms
            .getClusteringSupportAlgorithms();

    /** the singleton object of <code>FlowNetworkSupportAlgorithms </code> */
    private FlowNetworkSupportAlgorithms nsa = FlowNetworkSupportAlgorithms
            .getFlowNetworkSupportAlgorithms();

    /** The subalgorithm needed in this */
    private IsolatingCutTerminalSeparationAlgorithm ictsa = new IsolatingCutTerminalSeparationAlgorithm();

    /**
     * The array of weakly connected components of the graph, each given by a
     * <code> Collection </code> of nodes
     */
    private Collection[] graphComponents;

    /** The graph nodes in random order */
    private Node[] graphNodes;

    /** The terminals to be generated by this algorithm */
    private Node[] terminals;

    /**
     * Flag that indicates if the generated clusters are to be colored
     * differently
     */
    private boolean colorClusters;

    /** The number of clusters to be computed by the algorithm */
    private int numberOfClusters;

    /**
     * The number of connected components in the graph before the run of the
     * algorithm
     */
    private int numberOfOriginallyConnectedComponents;

    /**
     * Flag, that determines if capacity values of 1 will be used for every edge
     */
    private boolean useTrivialCapacities;

    /**
     * Flag, that determines if the terminals for the use of the <code>
     * IsolatingCutTerminalSeparationAlgorithm </code>
     * are not randomly chosen, but computed.
     */
    private boolean computeTerminals;

    /**
     * determines, if capacity values equal to the inverted betweenness values
     * are used for the edges
     */
    private boolean useInvertedBetweennessCapacities;

    /** The size of the cut, computed by the algorithm */
    private double cutSize;

    /** The cut edges computed by the algorithm */
    private Collection cut;

    /** The clusters computed by the algorithm */
    private Collection[] clusters;

    /** Flag that is set, if the algorithm has been executed */
    private boolean algorithmRun;

    /**
     * Constructs a new instance of the <code> IsolatingCutClusteringAlgorithm
     * </code>
     */
    public IsolatingCutClusteringAlgorithm() {
        super();
        reset();
    }

    /**
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
        numberOfClusters = ((IntegerParameter) params[NUMBER_OF_CLUSTERS_INDEX])
                .getInteger().intValue();
        useTrivialCapacities = ((BooleanParameter) params[USE_TRIVIAL_CAPACITIES_INDEX])
                .getBoolean().booleanValue();
        useInvertedBetweennessCapacities = ((BooleanParameter) params[USE_BETWEENNEESS_CAPACITIES_INDEX])
                .getBoolean().booleanValue();
        computeTerminals = ((BooleanParameter) params[COMPUTE_TERMINALS_INDEX])
                .getBoolean().booleanValue();
        colorClusters = ((BooleanParameter) params[COLOR_CLUSTERS_INDEX])
                .getBoolean().booleanValue();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
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

        numberOfOriginallyConnectedComponents = csa
                .getNumberOfConnectedComponents(graph);

        if ((numberOfClusters < numberOfOriginallyConnectedComponents)
                || (numberOfClusters > graph.getNumberOfNodes())
                || numberOfClusters == 0) {
            pe.add(NUMBER_OF_CLUSTERS_ERROR,
                    parameters[NUMBER_OF_CLUSTERS_INDEX]);
            noError = false;
        }

        if (!noError)
            throw pe;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        try {
            preAlgorithm();
            algorithm();
            postAlgortihm();
        } catch (Throwable t) {
            if (useTrivialCapacities || useInvertedBetweennessCapacities) {
                csa.restoreCapacities(graph);
            }
            csa.removeAllClusteringData(graph, false, true);
            reset();
            ClusteringException ce;
            if (t instanceof ClusteringException) {
                ce = (ClusteringException) t;
            } else {
                ce = ClusteringException.getStandardException();
            }
            ce.log(logger);
            throw ce;
            // t.printStackTrace();
        }
    }

    /**
     * Postcomputations to be done after the run of the algorithm
     */
    private void postAlgortihm() {
        // perhaps remove generated capacities and restore old values
        if (useTrivialCapacities || useInvertedBetweennessCapacities) {
            csa.restoreCapacities(graph);
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
        generateParameters();
        ictsa.reset();
        colorClusters = COLOR_CLUSTERS_DEFAULT;
        numberOfClusters = NUMBER_OF_CLUSTERS_DEFAULT;
        useTrivialCapacities = USE_TRIVIAL_CAPACITIES_DEFAULT;
        useInvertedBetweennessCapacities = USE_BETWEENNEESS_CAPACITIES_DEFAULT;
        computeTerminals = COMPUTE_TERMINALS_DEFAULT;
        graphNodes = null;
        graphComponents = null;
        terminals = null;
        cutSize = 0.0;
        cut = null;
        clusters = null;
        algorithmRun = false;
    }

    /**
     * The main algorithm
     */
    private void algorithm() {
        runTerminalSeparationAlgorithm();
        cut = ictsa.getCut();
        cutSize = ictsa.getCutSize();
        clusters = ictsa.getClusters();
        algorithmRun = true;
    }

    /**
     * Randomly chooses <code> numberOfClusters </code> terminals from the nodes
     * of the graph.
     * 
     * @return the randomly chosen terminals
     */
    private Node[] calcTerminalsRandomly() {
        Node[] calcTerminals = new Node[numberOfClusters];

        // Generate one terminal for each component
        Node tempTerminal;
        for (int i = 0; i < graphComponents.length; i++) {
            tempTerminal = csa.getRandomNodes((Node[]) graphComponents[i]
                    .toArray(new Node[0]), 1)[0];
            calcTerminals[i] = tempTerminal;
        }

        // Generate additional terminals
        Node[] additionalTerminals = csa.getRandomNodes(graphNodes,
                numberOfClusters - graphComponents.length);
        System.arraycopy(additionalTerminals, 0, calcTerminals,
                graphComponents.length, additionalTerminals.length);

        // Remove the mark
        csa.removeTemporaryTerminalMark(calcTerminals);

        return calcTerminals;
    }

    /**
     * Gets the terminals with the greatest sum of the capacities of incident
     * edges.
     * 
     * @return the calculated termials
     */
    private Node[] calcTerminalsByMaximumCapacitySum() {
        Node[] calcTerminals = new Node[numberOfClusters];

        csa.computeCapacitySum(graph);

        Node tempTerminal;
        for (int i = 0; i < graphComponents.length; i++) {
            tempTerminal = csa.getNodesWithGreatestCapacitySum(
                    (Node[]) graphComponents[i].toArray(new Node[0]), 1)[0];
            calcTerminals[i] = tempTerminal;
        }
        Node[] additionalTerminals = csa.getNodesWithGreatestCapacitySum(
                graphNodes, numberOfClusters - graphComponents.length);
        System.arraycopy(additionalTerminals, 0, calcTerminals,
                graphComponents.length, additionalTerminals.length);

        csa.removeCapacitySum(graph);
        csa.removeTemporaryTerminalMark(calcTerminals);

        return calcTerminals;
    }

    /**
     * Calculates the terminals of for the run of the interior <code>
     * IsolatingCutTerminalSeparationAlgorithm </code>
     * .
     * 
     * @return the terminals
     */
    private Node[] calculateTerminals() {
        if (computeTerminals)
            return calcTerminalsByMaximumCapacitySum();
        else
            return calcTerminalsRandomly();
    }

    /**
     * Generates the parameters asked for by this algorithm.
     */
    private void generateParameters() {
        IntegerParameter numberOfClustersParameter = new IntegerParameter(
                NUMBER_OF_CLUSTERS_DEFAULT, NUMBER_OF_CLUSTERS_NAME,
                NUMBER_OF_CLUSTERS_DESCRIPTION);
        BooleanParameter useTrivialCapParam = new BooleanParameter(
                USE_TRIVIAL_CAPACITIES_DEFAULT, USE_TRIVIAL_CAPACITIES_NAME,
                USE_TRIVIAL_CAPACITIES_DESCRIPTION);
        BooleanParameter useBetweennessCapParam = new BooleanParameter(
                USE_BETWEENNEESS_CAPACITIES_DEFAULT,
                USE_BETWEENNEESS_CAPACITIES_NAME,
                USE_BETWEENNEESS_CAPACITIES_DESCRIPTION);
        BooleanParameter computeTerminalsParam = new BooleanParameter(
                COMPUTE_TERMINALS_DEFAULT, COMPUTE_TERMINALS_NAME,
                COMPUTE_TERMINALS_DESCRIPTION);
        BooleanParameter colorClustersParameter = new BooleanParameter(
                COLOR_CLUSTERS_DEFAULT, COLOR_CLUSTERS_NAME,
                COLOR_CLUSTERS_DESCRIPTION);

        parameters = new Parameter[NUMBER_OF_PARAMS];
        parameters[NUMBER_OF_CLUSTERS_INDEX] = numberOfClustersParameter;
        parameters[USE_TRIVIAL_CAPACITIES_INDEX] = useTrivialCapParam;
        parameters[USE_BETWEENNEESS_CAPACITIES_INDEX] = useBetweennessCapParam;
        parameters[COMPUTE_TERMINALS_INDEX] = computeTerminalsParam;
        parameters[COLOR_CLUSTERS_INDEX] = colorClustersParameter;
    }

    /**
     * Initialization of internal data.
     */
    private void initGraphData() {
        graphNodes = graph.getNodes().toArray(new Node[0]);
        graphComponents = csa.getAllConnectedComponents(graph);
    }

    /**
     * Computations to be done before the run of the algorithm.
     */
    private void preAlgorithm() {
        csa.removeAllClusteringData(graph, false, true);
        csa.colorGraphToNormal(graph);

        // perhaps store old capacities and generate new ones
        if (useTrivialCapacities || useInvertedBetweennessCapacities) {
            csa.storeOldAndGenerateNewCapacities(graph, useTrivialCapacities,
                    useInvertedBetweennessCapacities);
        }

        initGraphData();
    }

    /**
     * Method that sustains the interior <code>
     * IsolatingCutTerminalSeparationAlgorithm </code>
     * with its parameters, checks if its preconditions are satisfied and runs
     * the algorithm.
     * 
     * @throws ClusteringException
     *             if the preconditions of the
     *             IsolatingCutTerminalSeparationAlgorithm have not been
     *             satisfied
     */
    private void runTerminalSeparationAlgorithm() {
        terminals = calculateTerminals();

        // set the parameters to the internal subalgorithm
        ictsa.setAll(graph, terminals, false, false, colorClusters);

        try {
            ictsa.check();
        } catch (PreconditionException pe) {
            throw new ClusteringException(
                    PRECONITIONS_OF_ICTSA_NOT_SATISFIED_ERROR, pe);
        }

        ictsa.execute();
    }

    /**
     * Sets all data needed to execute the algorithm.
     * 
     * @param graph
     *            the graph
     * @param numberClusters
     *            the number of clusters to be computed by the algorithm.
     * @param useTrivialCaps
     *            if true, a capacity of 1.0 will be used on each edge
     * @param useInvertedBetweennessCaps
     *            if true, a capacity equal to the inverted betweennesss of the
     *            edge will be used as capacity
     * @param computeTerminals
     *            if true, the terminals will be computed in stead of beeing
     *            chosen randomly
     * @param colorClusters
     *            if true, clusters and cut edges will be colored
     */
    public void setAll(Graph graph, int numberClusters, boolean useTrivialCaps,
            boolean useInvertedBetweennessCaps, boolean computeTerminals,
            boolean colorClusters) {
        this.attach(graph);
        this.setNumberOfClusters(numberClusters);
        this.setUseTrivialCapacities(useTrivialCaps);
        this.setUseInvertedBetweennessCapacities(useInvertedBetweennessCaps);
        this.setComputeTerminals(computeTerminals);
        this.setColorClusters(colorClusters);
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
        colorClusters = true;
    }

    /**
     * Sets the number of clusters to be generated by the algorithm
     * 
     * @param number
     *            the number of clusters
     * 
     * @throws ClusteringException
     *             if the graph was not set or the number of clusters is illegal
     */
    public void setNumberOfClusters(int number) {
        if (graph == null) {
            ClusteringException ce = new ClusteringException(
                    GRAPH_NOT_SET_ERROR);
            ce.log(logger);
            throw ce;
        }

        if ((number < 0) || (number > graph.getNumberOfNodes())) {
            ClusteringException ce = new ClusteringException(
                    NUMBER_OF_CLUSTERS_ERROR);
            ce.log(logger);
            throw ce;
        }

        numberOfClusters = number;
    }

    /**
     * Sets the <code> useInvertedBetweennessCapacities </code> flag, which
     * determines that betweenness values are computed for the graph. The
     * capacity values, used y the algorithm then are the inverted betweenness
     * values. If other capacity values were stored on the edges of the graph,
     * these are saved before the algorithm and restored afterward. Only one of
     * the flags <code> useInvertedBetweennessCapacities
     * </code> and <code> useTrivialCapacities </code> may be set to
     * true.
     * 
     * @param b
     *            the value for the flag
     */
    public void setUseInvertedBetweennessCapacities(boolean b) {
        useInvertedBetweennessCapacities = b;
    }

    /**
     * Sets the <code> useTrivialCapacities </code> flag, which determines that
     * capacity values of 1.0 are used for every edge for the run of the
     * algorithm. If other capacity values were stored on the edges of the
     * graph, these are saved before the algorithm and restored afterward. Only
     * one of the flags <code> useInvertedBetweennessCapacities </code> and
     * <code> useTrivialCapacities </code> may be set to true.
     * 
     * @param b
     *            the value for the flag
     */
    public void setUseTrivialCapacities(boolean b) {
        useTrivialCapacities = b;
    }

    /**
     * Sets the <code> computeTerminals </code> flag. It determines, that the
     * terminals for the use of the <code> 
     * IsolatingCutTerminalSeparationAlgorithm </code>
     * are not just randomly chosen but calculated. This may bring better
     * results in the algorithm.
     * 
     * @param computeTerminals
     *            the value for the flag
     */
    public void setComputeTerminals(boolean computeTerminals) {
        this.computeTerminals = computeTerminals;
    }

    /**
     * @return Returns the clusters.
     */
    public Collection[] getClusters() {
        if (algorithmRun)
            return clusters;
        else {
            ClusteringException ce = new ClusteringException(
                    ALGORITHM_NOT_RUN_ERROR);
            ce.log(logger);
            throw ce;
        }

    }

    /**
     * @return Returns the cut.
     */
    public Collection getCut() {
        if (algorithmRun)
            return cut;
        else {
            ClusteringException ce = new ClusteringException(
                    ALGORITHM_NOT_RUN_ERROR);
            ce.log(logger);
            throw ce;
        }
    }

    /**
     * @return Returns the cutSize.
     */
    public double getCutSize() {
        if (algorithmRun)
            return cutSize;
        else {
            ClusteringException ce = new ClusteringException(
                    ALGORITHM_NOT_RUN_ERROR);
            ce.log(logger);
            throw ce;
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
