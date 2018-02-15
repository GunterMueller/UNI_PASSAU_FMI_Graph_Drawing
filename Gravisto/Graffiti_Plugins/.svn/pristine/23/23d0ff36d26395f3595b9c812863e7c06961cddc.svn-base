// ==============================================================================
//
//   GomoryHuTreeClusteringAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GomoryHuTreeClusteringAlgorithm.java,v 1.2 2004/10/04 14:24:44 kaeser
// Exp $
/*
 * Created on 06.06.2004
 */

package org.graffiti.plugins.algorithms.clustering;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;

/**
 * An implementation of a Gomory-Hu-tree clustering algorithm. It approximates
 * the NP-hard problem of dividing a graph into k clusters with a minimum
 * cutsize.
 * 
 * @author Markus K�ser
 * @version $Revision 1.0 $
 */
public class GomoryHuTreeClusteringAlgorithm extends AbstractAlgorithm {

    /** The name of the algorithm */
    private static final String NAME = "Gomory-Hu-Tree Clustering";

    /** The logger of the algorithm */
    private static final Logger logger = Logger
            .getLogger(GomoryHuTreeClusteringAlgorithm.class.getName());

    /** The number of parameters, the algorithm expects */
    private static final int NUMBER_OF_PARAMS = 4;

    /** The index for the number of clusters parameter */
    private static final int NUMBER_OF_CLUSTERS_INDEX = 0;

    /** The default value for the number of clusters parameter */
    private static final int NUMBER_OF_CLUSTERS_DEFAULT = 1;

    /** The name of the number of clusters parameter */
    private static final String NUMBER_OF_CLUSTERS_NAME = "Number of Clusters";

    /** The description of the number of clusters parameter */
    private static final String NUMBER_OF_CLUSTERS_DESCRIPTION = "The number of "
            + "Clusters the Algorithm should create";

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

    /** The index for the color clusters parameter */
    private static final int COLOR_CLUSTERS_INDEX = 3;

    /** The default value for the color clusters parameter */
    private static final boolean COLOR_CLUSTERS_DEFAULT = true;

    /** The name of the color clusters parameter */
    private static final String COLOR_CLUSTERS_NAME = "color clusters";

    /** The description of the color clusters parameter */
    private static final String COLOR_CLUSTERS_DESCRIPTION = "determines if"
            + " the resulting clusters and cut edges should be colored.";

    /** Error message */
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

    /** Error message */
    private static final String CAPACITIES_SET_TWICE_ERROR = "Either trivial "
            + "or inverted betweenness can be used as capacities, but not both";

    /** the singleton object of <code>clusteringSupportAlgorithms </code> */
    private ClusteringSupportAlgorithms csa = ClusteringSupportAlgorithms
            .getClusteringSupportAlgorithms();

    /** the cut to be computed by the algorithm */
    private Collection multiwayCut;

    /** the singleton object of <code>FlowNetworkSupportAlgorithms </code> */
    private FlowNetworkSupportAlgorithms nsa = FlowNetworkSupportAlgorithms
            .getFlowNetworkSupportAlgorithms();

    /** the gomory-hu-tree used by the algorithm */
    private GomoryHuTree gomoryHuTree;

    /** the <code>Graph</code> representation of the tree */
    private Graph tree;

    /**
     * the array of clusters to be computed by the algorithm. Each cluster is a
     * Collection of Node objects
     */
    private Collection[] clusters;

    /** the temporary cuts */
    private Collection[] cuts;

    /** the array of graph nodes */
    private Node[] graphNodes;

    /** the array of nodes of the gomory-hu-tree */
    private Node[] treeNodes;

    /** Flag which is true if the algorithm was executed, false otherwise */
    private boolean algorithmExecuted;

    /** determines if the clusters should be colored differently */
    private boolean colorClusters;

    /**
     * determines, if capacity values equal to the inverted betweenness values
     * are used for the edges
     */
    private boolean useInvertedBetweennessCapacities;

    /** determines if capacity values of 1 will be used for every edge */
    private boolean useTrivialCapacities;

    /** the total value of the capacities of all cut edges */
    private double cutSize;

    /** the number of clusters the algorithm should produce */
    private int numberOfClusters;

    /**
     * The number of connected components in the graph before the run of the
     * algorithm
     */
    private int numberOfOriginallyConnectedComponents;

    /**
     * Constructs a new instance of the minimum k-cut approximation algorithm.
     */
    public GomoryHuTreeClusteringAlgorithm() {
        super();
        reset();
    }

    /**
     * Sets all data needed to execute the algorithm.
     * 
     * @param g
     *            the graph
     * @param numberClusters
     *            the number of Clusters to be computed by the algorithm
     * @param useTrivialCap
     *            if true, a capacity of 1.0 will be used on each edge
     * @param useBetweennessCap
     *            if true, a capacity equal to the inverted betweennesss of the
     *            edge will be used as capacity
     * @param color
     *            if true, clusters and cut edges will be colored
     */
    public void setAll(Graph g, int numberClusters, boolean useTrivialCap,
            boolean useBetweennessCap, boolean color) {
        this.attach(g);
        this.setNumberOfClusters(numberClusters);
        this.setUseTrivialCapacities(useTrivialCap);
        this.setUseInvertedBetweennessCapacities(useBetweennessCap);
        this.setColorClusters(color);
    }

    /**
     * Returns the the array if clusters computed by the algorithm. Each Cluster
     * is represented as a <code> Collection </code> of nodes. The algorithm
     * must have been executed before applying this method.
     * 
     * @return the array of clusters
     * 
     * @throws ClusteringException
     *             if the algorithm was not executed before
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
        colorClusters = true;
    }

    /**
     * Returns the total cut size of the multiway cut computed by the algorithm.
     * The algorithm must have been executed before applying this method.
     * 
     * @return the cutSize
     * 
     * @throws ClusteringException
     *             if the algorithm was not executed before
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
     * Returns the multiway cut as a <code> Collection </code> of Edges. The
     * algorithm must have been executed before applying this method.
     * 
     * @return the cut
     * 
     * @throws ClusteringException
     *             if the algorithm was not executed before
     */
    public Collection getCut() {
        if (algorithmExecuted)
            return multiwayCut;
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
     * Sets the number of clusters to be generated by the algorithm
     * 
     * @param number
     *            the number of clusters
     * 
     * @throws ClusteringException
     *             if the graph was not set or the number of clusters is illegal
     */
    public void setNumberOfClusters(int number) {
        numberOfClusters = number;
    }

    /**
     * Sets the number of clusters to be calculated by the algorithm.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#setAlgorithmParameters(org.graffiti.plugin.parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        super.setAlgorithmParameters(params);
        numberOfClusters = ((IntegerParameter) parameters[NUMBER_OF_CLUSTERS_INDEX])
                .getInteger().intValue();
        useTrivialCapacities = ((BooleanParameter) params[USE_TRIVIAL_CAPACITIES_INDEX])
                .getBoolean().booleanValue();
        useInvertedBetweennessCapacities = ((BooleanParameter) params[USE_BETWEENNEESS_CAPACITIES_INDEX])
                .getBoolean().booleanValue();
        colorClusters = ((BooleanParameter) parameters[COLOR_CLUSTERS_INDEX])
                .getBoolean().booleanValue();
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
     * Checks if the graph is a flow network and if the number of clusters is
     * set correctly .
     * 
     * @throws PreconditionException
     *             if not all preconditions are satisfied
     * 
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
     * Starts the execution of the minimum k-cut approximation algorithm and
     * computes a Collection of edges that cuts the graph into k connected
     * components. For this purpose a gomory-hu-tree is used.
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
            csa.removeAllClusteringData(graph, false, true);
            if (gomoryHuTree != null) {
                gomoryHuTree.removeGraphData();
            }
            reset();
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
        generateParameters();
        algorithmExecuted = false;
        graphNodes = null;
        clusters = null;
        cutSize = 0.0;
        gomoryHuTree = null;
        treeNodes = null;
        tree = null;
        cuts = null;
        multiwayCut = null;
        numberOfOriginallyConnectedComponents = 0;
        numberOfClusters = NUMBER_OF_CLUSTERS_DEFAULT;
        colorClusters = COLOR_CLUSTERS_DEFAULT;
        useTrivialCapacities = USE_TRIVIAL_CAPACITIES_DEFAULT;
        useInvertedBetweennessCapacities = USE_BETWEENNEESS_CAPACITIES_DEFAULT;
    }

    /**
     * Returns the lightest (numberOfClusters - 1) edges from the gomory-hu-tree
     * 
     * @return the lightest edges
     */
    private Collection getLightestTreeEdges() {
        int numberOfLightestEdges = numberOfClusters - 1;
        Edge[] edges = new Edge[tree.getNumberOfEdges()];
        int index = 0;

        // put all n-1 tree edges in an array
        for (Iterator edgeIt = tree.getEdgesIterator(); edgeIt.hasNext();) {
            edges[index] = (Edge) edgeIt.next();
            index++;
        }

        Comparator edgeCapComp = new Comparator() {
            public int compare(Object o1, Object o2) {
                Edge first = (Edge) o1;
                Edge second = (Edge) o2;
                double firstCapacity = nsa.getCapacity(first);
                double secondCapacity = nsa.getCapacity(second);
                int result = 0;

                if (firstCapacity < secondCapacity) {
                    result = -1;
                }

                if (firstCapacity > secondCapacity) {
                    result = +1;
                }

                return result;
            }
        };

        // sort the array (capacities)
        Arrays.sort(edges, edgeCapComp);

        // copy the lightest edges into the list
        LinkedList lightestEdges = new LinkedList();
        for (int i = 0; i < numberOfLightestEdges; i++) {
            lightestEdges.addLast(edges[i]);
        }
        return lightestEdges;
    }

    /**
     * The main algorithm
     */
    private void algorithm() {

        // create gomory-hu-tree and get data
        gomoryHuTree = new GomoryHuTree(graph);
        tree = gomoryHuTree.getTree();
        treeNodes = gomoryHuTree.getTreeNodes();
        graphNodes = gomoryHuTree.getGraphNodes();

        // first compute the clusters of the tree removing the lightest
        // treeedges
        Collection[] treeClusters = computeTreeClusters();

        // second convert them to clusters in the graph
        clusters = gomoryHuTree
                .convertTreeClustersToGraphClusters(treeClusters);

        // third compute the cut by collecting all edges between the clusters
        multiwayCut = csa.computeCutFromClusters(graph, clusters);

        // fourth get the cutsize
        cutSize = csa.computeMultiwayCutSize(multiwayCut);

        algorithmExecuted = true;
    }

    /**
     * Computes the clusters in the tree.
     * 
     * @return the clusters in the tree.
     */
    private Collection[] computeTreeClusters() {
        // the lightest numberOfClusters-1 tree edges will be cut
        Collection lightestEdges = getLightestTreeEdges();

        double sum = 0.0;
        for (Iterator it = lightestEdges.iterator(); it.hasNext();) {
            Edge tempEdge = (Edge) it.next();
            sum += nsa.getCapacity(tempEdge);
        }

        // get the clusters of the tree
        Collection[] treeClusters = csa.computeClustersFromCut(tree,
                lightestEdges);
        return treeClusters;
    }

    /**
     * Generates the parameters needed by the algorithm
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
        BooleanParameter colorClustersParameter = new BooleanParameter(
                COLOR_CLUSTERS_DEFAULT, COLOR_CLUSTERS_NAME,
                COLOR_CLUSTERS_DESCRIPTION);

        parameters = new Parameter[NUMBER_OF_PARAMS];
        parameters[NUMBER_OF_CLUSTERS_INDEX] = numberOfClustersParameter;
        parameters[USE_TRIVIAL_CAPACITIES_INDEX] = useTrivialCapParam;
        parameters[USE_BETWEENNEESS_CAPACITIES_INDEX] = useBetweennessCapParam;
        parameters[COLOR_CLUSTERS_INDEX] = colorClustersParameter;
    }

    /**
     * Computations after the run of the algorithm
     */
    private void postAlgorithm() {
        // perhaps remove generated capacities and restore old values
        if (useTrivialCapacities || useInvertedBetweennessCapacities) {
            csa.restoreCapacities(graph);
        }

        if (colorClusters) {
            csa.colorClustersAndCutEdges(clusters, multiwayCut);
        }
        csa.setCutEdgeMark(multiwayCut, true);
        csa.setClusterNumber(clusters);

        gomoryHuTree.removeGraphData();
    }

    /**
     * Computatins before the run of the algorithm
     */
    private void preAlgorithm() {
        csa.removeAllClusteringData(graph, false, true);
        csa.colorGraphToNormal(graph);

        // perhaps store old capacities and generate new ones
        if (useTrivialCapacities || useInvertedBetweennessCapacities) {
            csa.storeOldAndGenerateNewCapacities(graph, useTrivialCapacities,
                    useInvertedBetweennessCapacities);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
