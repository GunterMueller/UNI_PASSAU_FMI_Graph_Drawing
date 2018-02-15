package org.graffiti.plugins.algorithms.phyloTrees;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms.Circle;
import org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms.CircularCladogram;
import org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms.CircularPhylogram;
import org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms.Cladogram;
import org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms.Phylogram;
import org.graffiti.plugins.algorithms.phyloTrees.drawingAlgorithms.Radial;
import org.graffiti.plugins.algorithms.phyloTrees.tests.TestAlgorithm;
import org.graffiti.plugins.algorithms.phyloTrees.utility.Pair;
import org.graffiti.plugins.algorithms.phyloTrees.utility.PhyloTreeGraphData;

/**
 * This class contains various algorithms for drawing of phylogenetic trees and
 * stores {@link PhyloTreeGraphData} objects for every graph, which is drawn
 * with one of its algorithms.
 */
public class PhyloTreeAlgorithm extends AbstractAlgorithm {

    /**
     * The name of this algorithm plugin.
     */
    public static final String ALGORITHM_NAME = "Phylogenetic Trees";

    /**
     * Map containing all the registered algorithms for drawing of phylogenetic
     * trees and their names.
     */
    private Map<Graph, PhyloTreeGraphData> graphData = new WeakHashMap<Graph, PhyloTreeGraphData>();

    /**
     * Map containing the name and an instance of all algorithms registered for
     * drawing of phylogenetic trees.
     */
    private Map<String, PhylogeneticTree> algorithms;

    /**
     * Cache for the algorithm and its parameters.
     */
    private Pair<PhylogeneticTree, Parameter<?>[]> chosenAlgorithmAndParameters = null;

    /**
     * Constructs a new instance.
     */
    public PhyloTreeAlgorithm() {
        algorithms = new HashMap<String, PhylogeneticTree>();

        addAlgorithm(algorithms, new Circle());
        addAlgorithm(algorithms, new CircularPhylogram());
        addAlgorithm(algorithms, new CircularCladogram());
        addAlgorithm(algorithms, new Cladogram());
        addAlgorithm(algorithms, new Phylogram());
        addAlgorithm(algorithms, new Radial());
        addAlgorithm(algorithms, new TestAlgorithm());
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return ALGORITHM_NAME;
    }

    /**
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        assert params.length >= 1;

        // get selected algorithm parameter
        StringSelectionParameter algoSelParam = (StringSelectionParameter) params[0];
        String algorithmName = algoSelParam.getValue();
        PhylogeneticTree algo = algorithms.get(algorithmName);

        // split parameter array
        List<Parameter<?>> algoParametersList = new LinkedList<Parameter<?>>();
        for (Parameter<?> param : params) {
            Parameter<?> dependencyParent = param.getDependencyParent();
            boolean isDirectlyDependend = (dependencyParent == algoSelParam)
                    && ((String) param.getDependencyValue())
                            .equals(algorithmName);

            boolean isSubDependend = false;
            if (!isDirectlyDependend) {
                isSubDependend = algoParametersList.contains(dependencyParent);
            }

            if (isDirectlyDependend || isSubDependend) {
                algoParametersList.add(param);
            }
        }

        Parameter<?>[] algorithmParameters = new Parameter<?>[algoParametersList
                .size()];

        int indexCounter = 0;
        for (Parameter<?> param : algoParametersList) {
            algorithmParameters[indexCounter] = param;
            ++indexCounter;
        }

        // update graph data
        chosenAlgorithmAndParameters = new Pair<PhylogeneticTree, Parameter<?>[]>(
                algo, algorithmParameters);
    }

    /**
     * @see org.graffiti.plugin.algorithm.AbstractAlgorithm#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        Parameter<?>[][] algoParamters = new Parameter<?>[algorithms.size()][];
        String[] treeTypes = new String[algorithms.size()];
        algorithms.keySet().toArray(treeTypes);

        int algoParameterSum = 0;
        for (int i = 0; i < treeTypes.length; ++i) {
            PhylogeneticTree algorithm = algorithms.get(treeTypes[i]);
            treeTypes[i] = algorithm.getName();
            algoParamters[i] = algorithm.getParameters();
            algoParameterSum += algoParamters[i].length;
        }

        // create the parameters needed by this algorithm
        int numberOfGenericParamters = 1;
        Parameter<?>[] resultParameters = new Parameter<?>[numberOfGenericParamters
                + algoParameterSum];

        StringSelectionParameter drawingType = new StringSelectionParameter(
                treeTypes, "Algorithm", "The drawing algorithm to be used");
        resultParameters[0] = drawingType;

        // copy parameters and set parameter dependencies
        int resultIndex = numberOfGenericParamters;
        for (int i = 0; i < treeTypes.length; ++i) {
            for (Parameter<?> param : algoParamters[i]) {
                if (param.getDependencyParent() == null) {
                    param.setDependency(drawingType, treeTypes[i]);
                }
                resultParameters[resultIndex] = param;
                ++resultIndex;
            }
        }

        return resultParameters;
    }

    /**
     * Returns the data associated with a {@link Graph} object.
     * 
     * @param g
     *            The {@link Graph} the data is to be associated with. Must not
     *            be <code>null</code>.
     * @return The {@link PhyloTreeGraphData} object associated with the
     *         {@link Graph} object.
     */
    public PhyloTreeGraphData getCorrespondingData(Graph g) {
        assert g != null;

        PhyloTreeGraphData data = graphData.get(g);
        if (data == null) {
            data = createPhyloTreeGraphData(g);
        }
        return data;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        graph = null;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        assert this.graph != null;

        graph.getListenerManager().transactionStarted(this);

        PhyloTreeGraphData graphData = getCorrespondingData(graph);

        if (chosenAlgorithmAndParameters != null) {
            graphData.setAlgorithm(chosenAlgorithmAndParameters.getFirst(),
                    chosenAlgorithmAndParameters.getSecond());
            chosenAlgorithmAndParameters = null;
        }

        PhylogeneticTree treeAlgo = graphData.getAlgorithm();
        treeAlgo.drawGraph(graph, graphData);

        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * Adds the algorithm to the map.
     * 
     * @param map
     *            The map to which the algorithm is to be added.
     * @param algo
     *            The algorithm, that is to be added to the map.
     */
    private void addAlgorithm(Map<String, PhylogeneticTree> map,
            PhylogeneticTree algo) {
        map.put(algo.getName(), algo);
    }

    /**
     * Creates and returns a new {@link PhyloTreeGraphData} object for a given
     * Graph object.
     * 
     * @param g
     *            The Graph for which a new {@link PhyloTreeGraphData} object is
     *            to be created.
     * @return The newly created {@link PhyloTreeGraphData} object for a given
     *         Graph.
     */
    private PhyloTreeGraphData createPhyloTreeGraphData(Graph g) {
        assert g != null;
        PhyloTreeGraphData data = new PhyloTreeGraphData();

        // find root nodes
        Iterator<Node> nodeIt = g.getNodesIterator();
        while (nodeIt.hasNext()) {
            Node node = nodeIt.next();

            if (node.getInDegree() == 0) {
                data.addRootNode(node);
            }
        }

        return data;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
