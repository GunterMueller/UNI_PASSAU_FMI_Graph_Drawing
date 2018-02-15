// =============================================================================
//
//   Sugiyama.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Sugiyama.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.editor.dialog.ParameterDialog;
import org.graffiti.graph.Node;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.EditorAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.animation.Animation;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.sugiyama.constraints.ConstraintBuilder;
import org.graffiti.plugins.algorithms.sugiyama.decycling.DecyclingAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.dialog.AlgorithmConfigurationDialog;
import org.graffiti.plugins.algorithms.sugiyama.layout.LayoutAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.BigNode;
import org.graffiti.plugins.algorithms.sugiyama.util.ComparableClassParameter;
import org.graffiti.plugins.algorithms.sugiyama.util.ConstraintsUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.CoordinatesUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.DummyNodeUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.EdgeUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.FindPhaseAlgorithms;
import org.graffiti.plugins.algorithms.sugiyama.util.PreferencesUtil;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;
import org.graffiti.selection.Selection;

/**
 * This class is the main wrapper for the individual algorithms in the
 * sugiyama-framework.
 * 
 * It creates an array of algorithms for each phase in the sugiyama-algorithm
 * and attaches a <code>SugiyamaData</code>-bean to each algorithm.
 * 
 * The framework supports <tt>Animation</tt>s and <tt>Animation</tt>s in a
 * phase.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class Sugiyama extends AbstractAlgorithm implements SugiyamaAlgorithm,
        EditorAlgorithm {

    /** The name of this algorithm */
    private final String ALGORITHM_NAME = "Sugiyama";

    /** The number of phases included in the Sugiyama-algorithm */
    protected final int NUMBER_OF_PHASES = 4;

    /** Array that stores the algorithm for each phase */
    protected SugiyamaAlgorithm[] algorithms;

    /** Bean that stores necessary information for each phase */
    protected SugiyamaData data;

    /**
     * This boolean controls animation of the framework. If enabled, each phase
     * acts as one step in the animation, i.e. the animation has four steps.
     * Optionally, a phase-algorithm can support animations, too.
     */
    private boolean animated = true;

    /** The logger */
    private static final Logger logger = Logger.getLogger(Sugiyama.class
            .getName());

    /**
     * Default constructor.
     * 
     * Creates a new <code>SugiyamaData</code>-bean and loads saved preferences
     */
    public Sugiyama() {
        data = new SugiyamaData();
        initData();
    }

    /**
     * This constructor should only be used by the sub class
     * IncrementalSugiyama.
     * 
     * @param incremental
     */
    public Sugiyama(boolean incremental) {
        // do nothing

    }

    protected void initData() {

        PreferencesUtil.loadPreferences(data);
        HashSet<String> messages = PreferencesUtil.validateAlgorithms(data);
        if (messages != null) {
            logger.log(Level.WARNING, "Please search for algorithms. The "
                    + "following algorithms could not be loaded:");
            Iterator<String> algoIter = messages.iterator();
            while (algoIter.hasNext()) {
                logger.log(Level.WARNING, "  - " + algoIter.next());
            }
        }
        data.setGraph(this.graph);
        algorithms = new SugiyamaAlgorithm[NUMBER_OF_PHASES];
    }

    /**
     * Getter-method to access the <code>SugiyamaData</code>-bean
     * 
     * @return Returns the <code>SugiyamaData</code>-bean
     */
    public SugiyamaData getData() {
        return this.data;
    }

    /**
     * Setter-method to store a <code>SugiyamaData</code>-bean
     * 
     * @param theData
     *            The <code>SugiyamaData</code>-bean that gets stored
     */
    public void setData(SugiyamaData theData) {
        this.data = theData;
    }

    /**
     * Getter-method to access this algorithm's name
     * 
     * @return Returns the name of this algorithm
     */
    public String getName() {
        return ALGORITHM_NAME;
    }

    /**
     * This method checks if all neccessary preconditions for executing the
     * sugiyama-algorithms are met.
     * 
     * It performs basic checks on the graph itself:
     * <ul>
     * <li>The attached graph may not be <code>null</code>
     * <li>The graph has to be directed
     * </ul>
     * 
     * After checking the general preconditions, it executes the check()-method
     * from the first phase's algorithm. The other phases cannot be checked, as
     * their preconditions may depend on the execution of other phases.
     */
    @Override
    public void check() throws PreconditionException {
        if (this.graph == null)
            throw new PreconditionException(
                    SugiyamaConstants.ERROR_GRAPH_IS_NULL);

        if (this.graph.getNumberOfNodes() == 0)
            throw new PreconditionException(
                    SugiyamaConstants.ERROR_GRAPH_IS_EMPTY);

        for (int i = 0; i < NUMBER_OF_PHASES; i++) {
            algorithms[i].attach(graph);
        }
        algorithms[0].check();

        SugiyamaAttributesCreator c = new SugiyamaAttributesCreator();
        c.attach(this.graph);
        c.check();
        c.execute();
    }

    /**
     * This method executes the individual algorithms responsible for each
     * phase.
     * 
     * Before executing a phase, the algorithm's check-method is called.
     */
    public void execute() {
        data.setGraph(this.graph);
        DummyNodeUtil.collectDummies(data, graph);
        ConstraintBuilder constraintBuilder = new ConstraintBuilder(graph, data);
        constraintBuilder.buildConstraints();

        if (data.getBigNodesPolicy() == SugiyamaConstants.BIG_NODES_SHRINK) {
            shrinkBigNodes();
        }

        // don't go any further - an animation-object will be returned that
        // does all the work
        if (animated)
            return;

        graph.getListenerManager().transactionStarted(this);
        EdgeUtil.removeBends(data);
        EdgeUtil.removeSelfLoops(data);
        graph.getListenerManager().transactionFinished(this);

        for (int i = 0; i < NUMBER_OF_PHASES; i++) {
            try {
                algorithms[i].check();
            } catch (PreconditionException pce) {
                throw new RuntimeException(pce);
            }
            // layout phase - normalize xpos attribute if the algorithm does not
            // support arbitrary xpos attributes
            if (i == 3)
                if (!((LayoutAlgorithm) algorithms[i]).supportsArbitraryXPos()) {
                    graph.getListenerManager().transactionStarted(this);
                    data.getLayers().normalizeLayers();
                    graph.getListenerManager().transactionFinished(this);
                }

            // crossing reduction: count the number of crossings before the
            // algorithm did his work
            if (i == 2) {
                /*
                 * int crossings = 0; for (int l = 0; l <
                 * data.getLayers().getNumberOfLayers(); l++) { crossings += new
                 * BilayerCrossCounter(graph, l, data).getNumberOfCrossings(); }
                 * logger.log(Level.INFO,
                 * "Edge crossings before crossing reduction: " + crossings);
                 */
                int crss = countCrossings(data);

                if (!data.containsKey(SugiyamaData.INITIAL_CROSSING_COUNT)) {
                    data.putObject(SugiyamaData.INITIAL_CROSSING_COUNT, crss);
                }
            }

            algorithms[i].execute();

            // activate the grid if the user requested one
            if (i == 1) {
                graph.getListenerManager().transactionStarted(this);
                if (data.gridActivated()) {
                    CoordinatesUtil.addGrid(graph, data);
                }
                graph.getListenerManager().transactionFinished(this);

                int levelCount = data.getLayers().getNumberOfLayers();

                if (!data.containsKey(SugiyamaData.INITIAL_LEVEL_COUNT)) {
                    data
                            .putObject(SugiyamaData.INITIAL_LEVEL_COUNT,
                                    levelCount);
                }
            }

            // crossing reduction: count the number of crossings after the
            // algorithm did his work
            if (i == 2) {
                /*
                 * int crossings = 0; for (int l = 0; l <
                 * data.getLayers().getNumberOfLayers(); l++) { crossings += new
                 * BilayerCrossCounter(graph, l, data).getNumberOfCrossings(); }
                 * logger.log(Level.INFO,
                 * "Edge crossings after crossing reduction: " + crossings);
                 * Integer cInt = data.getCrossingChange(); if (cInt != null) {
                 * logger.log(Level.INFO,
                 * "Crossing reduction claimed change of " + cInt); }
                 * 
                 * System.out.println("BilayerCrossCounter#crossings: " +
                 * crossings);
                 */
                int crss = countCrossings(data);
                // data.getC
                // System.out.println("countCrossings(data)#crossings: " +
                // crss);

                data.putObject(SugiyamaData.CROSSING_COUNT, crss);
            }

            // update the graph after levelling and crossmin, so the user
            // can see the results
            if (i == 0 || i == 1 || i == 2) {
                graph.getListenerManager().transactionStarted(this);
                if (i == 1) {
                    EdgeUtil.insertDeletedEdges(data);
                }
                if (i == 0) {
                    EdgeUtil.insertConstraintsForDeletedEdges(data);
                }
                if (i > 0) {
                    CoordinatesUtil.updateGraph(graph, data);
                }
                // after updating, check vertical constraints and remove
                // constraint from nodes
                if (i == 1) {
                    // Check necessary in case there are deleted edges
                    // Only in this case SCC was chosen in first phase
                    ConstraintsUtil.checkVerticalConstraints(data);
                    if (data.getDeletedEdges().size() > 0) {
                        ConstraintsUtil.removeDeletedEdgesConstraints(graph);
                    }
                }
                graph.getListenerManager().transactionFinished(this);
            }
        }
        graph.getListenerManager().transactionStarted(this);
        // collect coordinate-attributes in the sugiyama-attribute-tree and
        // set the "real" coordinates in the graphics-tree
        CoordinatesUtil.updateRealCoordinates(graph);
        // remove all big nodes
        Iterator<BigNode> bNodes = data.getBigNodes().iterator();
        while (bNodes.hasNext()) {
            bNodes.next().removeDummyElements();
        }
        // remove all dummies that had been inserted into the graph
        DummyNodeUtil.removeDummies(data, graph);
        // reverse or undelete edges that would have created cycles in the graph
        ((DecyclingAlgorithm) algorithms[0]).undo();
        // Iterator<Node> nodeIterator = graph.getNodesIterator();
        // while (nodeIterator.hasNext()) {
        // nodeIterator.next().removeAttribute("sugiyama");
        // }
        EdgeUtil.addPorts(data);
        EdgeUtil.insertSelfLoops(data);
        graph.getListenerManager().transactionFinished(this);
        // check the constraints
        ConstraintsUtil.checkConstraints(data);

        // System.out.println(data.dumpObjects());
    }

    /**
     * This method returns parts of the available framework-parameters. In this
     * method, four <tt>StringSelectionParameter</tt>s are returned, that
     * represent a selection of available phase-algorithms. The user can select
     * available algorithms on the first "page" of the
     * <tt>AlgorithmConfigurationDialog</tt>. The second page of this dialog
     * depends on the user's selection, so these <tt>Parameter</tt>s cannot be
     * set here - the configuration dialog will take care of that. As a
     * side-effect of the multi-page-layout of the configuration-dialog,
     * parameters for the framework itself are stored in <tt>SugiyamaData</tt>.
     * If you want to add additional configuration-parameters to the framework,
     * you have to edit <tt>SugiyamaData</tt>'s
     * <tt>buildFrameworkParameters</tt>-method.
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("startNode",
                "DFS will start with the selected node");

        // some general initialization
        ArrayList<String[]> phaseAlgorithms;
        String algoName;
        ArrayList<String[]> algBinaryNames = new ArrayList<String[]>();
        ComparableClassParameter[] sortMe;
        String[] phaseNames = new String[] { "Decycling-algorithm",
                "Levelling-algorithm", "CrossMin-algorithm", "Layout-algorithm" };
        String[] phaseDescr = new String[] { "Select the decycling-algorithm",
                "Select the levelling-algorithm",
                "Select the crossmin-algorithm", "Select the layout-algorithm" };
        String[] selection;

        // get the available phase-algorithms
        phaseAlgorithms = FindPhaseAlgorithms.getPhaseAlgorithms(data
                .getPhaseAlgorithms());
        // The StringSelections that will be returned in the Parameter[]
        StringSelectionParameter[] selectableAlgorithms;
        selectableAlgorithms = new StringSelectionParameter[NUMBER_OF_PHASES];

        // Build the StringSelectionParameter for each phase
        for (int i = 0; i < phaseAlgorithms.size(); i++) {
            // Create an array of ComparableClassParameters to build the
            // StringSelectionParameter for each phase in alphabetical order
            sortMe = new ComparableClassParameter[phaseAlgorithms.get(i).length];
            // Iterate through the available algorithms in phase i
            for (int j = 0; j < phaseAlgorithms.get(i).length; j++) {
                algoName = phaseAlgorithms.get(i)[j];
                try {
                    // Access the algorithm's getName()-Method (used for
                    // description of the algorithm
                    algoName = ((SugiyamaAlgorithm) Class.forName(algoName)
                            .newInstance()).getName();
                    sortMe[j] = new ComparableClassParameter(phaseAlgorithms
                            .get(i)[j], algoName);
                } catch (Exception ex) {
                    // This should not happen
                }
            }
            // check if an entry of the array is null (could happen, if an
            // algorithm has been removed but was still loaded from preferences
            int nullalgos = 0;
            ComparableClassParameter[] sortMe2;
            for (int j = 0; j < sortMe.length; j++) {
                if (sortMe[j] == null) {
                    nullalgos++;
                }
            }
            // rebuild array if there's a null-value in it
            if (nullalgos != 0) {
                sortMe2 = new ComparableClassParameter[sortMe.length
                        - nullalgos];
                int counter = 0;
                for (int j = 0; j < sortMe.length; j++) {
                    if (sortMe[j] != null) {
                        sortMe2[counter] = sortMe[j];
                        counter++;
                    }
                }
                sortMe = sortMe2;
            }
            // An array of ComparableClassParameters has been build now, sort
            // it and build a StringSelectionParameter
            Arrays.sort(sortMe);
            algBinaryNames.add(i, new String[sortMe.length]);
            selection = new String[sortMe.length];
            for (int j = 0; j < sortMe.length; j++) {
                selection[j] = sortMe[j].description;
                algBinaryNames.get(i)[j] = sortMe[j].binaryName;
            }
            selectableAlgorithms[i] = new StringSelectionParameter(selection,
                    phaseNames[i], phaseDescr[i]);
        }

        // save the binary-names of the algorithms in the bean
        data.setAlgorithmBinaryNames(algBinaryNames);

        // check if there had been any algorithms pre-selected by the user
        // and set this to the selected value
        String[] para;
        for (int i = 0; i < 4; i++) {
            if (data.getLastSelectedAlgorithms()[i] != null) {
                para = selectableAlgorithms[i].getParams();
                for (int j = 0; j < para.length; j++) {
                    if (data.getLastSelectedAlgorithms()[i].equals(para[j])) {
                        selectableAlgorithms[i].setSelectedValue(j);
                    }
                }
            }
        }

        this.parameters = new Parameter[] { selParam, selectableAlgorithms[0],
                selectableAlgorithms[1], selectableAlgorithms[2],
                selectableAlgorithms[3] };
        return this.parameters;
    }

    /**
     * Set the parameters of the framework. This has to be done using
     * <tt>SugiyamaData</tt>.
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        // access the user-selected algorithms
        this.algorithms = data.getSelectedAlgorithms();
        this.parameters = params;
        if (params != null) {
            // save the selected node (only the first one makes sense)
            try {
                data.setStartNode(((SelectionParameter) params[0])
                        .getSelection().getNodes().get(0));
            } catch (Exception e) {
                logger.log(Level.FINE, "No Selection-Parameter at index 0"
                        + ": " + e.getMessage());
            }
        }
        // does the user want animation-support
        this.animated = ((BooleanParameter) data.getAlgorithmParameters()[0])
                .getBoolean();
        data.setAnimated(this.animated);
    }

    /**
     * Reset all algorithms in the framwork. This method calls reset() on all
     * used algorithms and creates a new SugiyamaData-Bean
     */
    @Override
    public void reset() {
        graph = null;
        data.reset();
        for (int i = 0; i < algorithms.length; i++) {
            algorithms[i].reset();
            algorithms[i].setData(data);
        }
    }

    /**
     * Returns the frameworks parameter-dialog.
     */
    public ParameterDialog getParameterDialog(Selection sel) {
        GraffitiSingleton gSingleton = GraffitiSingleton.getInstance();
        ParameterDialog paramDialog;

        paramDialog = new AlgorithmConfigurationDialog(gSingleton
                .getMainFrame().getEditComponentManager(), gSingleton
                .getMainFrame(), this, sel, this.data);

        return paramDialog;
    }

    /**
     * Support for animations can be turned on or off by the user
     */
    @Override
    public boolean supportsAnimation() {
        return animated;
    }

    /**
     * A <tt>SugiyamaAnimation</tt> is returned, if the user wants an "animated"
     * (i.e. each phase is one step) version of the sugiyama-algorithm.
     */
    @Override
    public Animation getAnimation() {
        SugiyamaAlgorithm[] copiedAlgorithms = new SugiyamaAlgorithm[NUMBER_OF_PHASES];
        SugiyamaData copiedData = data.copy();
        copiedData.setGraph(this.graph);

        for (int i = 0; i < NUMBER_OF_PHASES; i++) {
            try {
                SugiyamaAlgorithm a = algorithms[i].getClass().newInstance();
                a.attach(this.graph);
                a.setData(copiedData);
                if (algorithms[i].getParameters() != null) {
                    a.setParameters(algorithms[i].getParameters());
                }
                copiedAlgorithms[i] = a;
            } catch (IllegalAccessException iae) {
                copiedAlgorithms[i] = algorithms[i];
                logger
                        .log(Level.SEVERE, "Cannot copy algorithm for phase "
                                + i);
            } catch (InstantiationException ie) {
                copiedAlgorithms[i] = algorithms[i];
                logger
                        .log(Level.SEVERE, "Cannot copy algorithm for phase "
                                + i);
            }
        }
        copiedData.setSelectedAlgorithms(copiedAlgorithms);
        return new SugiyamaAnimation(copiedAlgorithms, copiedData, this.graph);
    }

    public boolean supportsBigNodes() {
        return true;
    }

    public boolean supportsConstraints() {
        return true;
    }

    public boolean supportsAlgorithmType(String algorithmType) {
        return true;
    }

    /**
     * This method shrinks big nodes to the default size
     */
    public void shrinkBigNodes() {
        Iterator<Node> nodeIter = graph.getNodesIterator();
        Node current;
        DimensionAttribute dim;
        while (nodeIter.hasNext()) {
            current = nodeIter.next();
            try {
                dim = (DimensionAttribute) current
                        .getAttribute(GraphicAttributeConstants.DIM_PATH);
                if (dim.getWidth() != SugiyamaConstants.DEFAULT_NODE_WIDTH) {
                    dim.setWidth(SugiyamaConstants.DEFAULT_NODE_WIDTH);
                }
                if (dim.getHeight() != SugiyamaConstants.DEFAULT_NODE_HEIGHT) {
                    dim.setHeight(SugiyamaConstants.DEFAULT_NODE_HEIGHT);
                }
            } catch (Exception e) {

            }
        }
    }

    public SugiyamaData getSugiyamaData() {
        return data;
    }

    // private void countCrossings() {
    // // checkXPosVSLayers(data);
    // int crossingsAfter = 0;
    // for (int l = 0; l < data.getLayers().getNumberOfLayers(); l++) {
    // crossingsAfter += new BilayerCrossCounter(graph, l,
    // data).getNumberOfCrossings();
    // }
    // // logger.log(Level.INFO,
    // // "Crossings(1): "+countCrossings(data));*/
    // logger.log(Level.INFO,
    // "Crossings(2): "+crossingsAfter);
    // /*logger.log(Level.INFO,
    // "Type 2 conflicts(1): "+countType2Conflicts(data));
    // data.getLayers().normalizeLayers();
    // logger.log(Level.INFO,
    // "Type 2 conflicts(2): "+Toolkit.markType1Conflicts(Toolkit.collectLayers(data)));
    // */
    // }
    //    
    // private void checkXPosVSLayers(SugiyamaData data) {
    // for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++)
    // {
    // ArrayList<Node> currentLayer = data.getLayers().getLayer(i);
    // for (int j = 0; j < currentLayer.size()-1; j++) {
    // double leftXPos =
    // currentLayer.get(j).getDouble(SugiyamaConstants.PATH_XPOS);
    // double rightXPos =
    // currentLayer.get(j+1).getDouble(SugiyamaConstants.PATH_XPOS);
    // if (leftXPos >= rightXPos) {
    // throw new IllegalStateException();
    // }
    // }
    // }
    //        
    // }

    public static int countCrossings(SugiyamaData data) {
        int crossings = 0;
        for (int r = 0; r < data.getLayers().getNumberOfLayers(); r++) {
            ArrayList<Node> layer = data.getLayers().getLayer(r);
            ArrayList<Node> nextLayer = null;
            if (r < data.getLayers().getNumberOfLayers() - 1) {
                nextLayer = data.getLayers().getLayer(r + 1);
            } else {
                nextLayer = data.getLayers().getLayer(0);
            }
            for (int i = 0; i < layer.size(); i++) {
                Node iNode = layer.get(i);
                for (int k = i + 1; k < layer.size(); k++) {
                    Node kNode = layer.get(k);
                    for (Node jNode : iNode.getOutNeighbors()) {
                        int j = nextLayer.indexOf(jNode);
                        if (j == -1)
                            throw new IllegalStateException();
                        for (Node lNode : kNode.getOutNeighbors()) {
                            if (jNode == lNode) {
                                continue;
                            }
                            int l = nextLayer.indexOf(lNode);
                            if (l == -1)
                                throw new IllegalStateException();
                            if (j > l) {
                                crossings++;
                            }
                        }
                    }
                }
            }
        }
        return crossings;

    }

    // private static int countType2Conflicts(SugiyamaData data) {
    // int type2 = 0;
    // HashSet<Node> dummies = data.getDummyNodes();
    // for (int r = 0; r < data.getLayers().getNumberOfLayers(); r++) {
    // ArrayList<Node> layer = data.getLayers().getLayer(r);
    // ArrayList<Node> nextLayer = null;
    // if (r < data.getLayers().getNumberOfLayers()-1) {
    // nextLayer = data.getLayers().getLayer(r+1);
    // } else {
    // nextLayer = data.getLayers().getLayer(0);
    // }
    // for (int i = 0; i < layer.size(); i++) {
    // Node iNode = layer.get(i);
    // if (!dummies.contains(iNode)) {
    // continue;
    // }
    // for (int k = i+1; k < layer.size(); k++) {
    // Node kNode = layer.get(k);
    // if (!dummies.contains(kNode)) {
    // continue;
    // }
    // for (Node jNode: iNode.getOutNeighbors()) {
    // if (!dummies.contains(jNode)) {
    // continue;
    // }
    // int j = nextLayer.indexOf(jNode);
    // if (j == -1) {
    // throw new IllegalStateException();
    // }
    // for (Node lNode: kNode.getOutNeighbors()) {
    // if (jNode == lNode) {
    // continue;
    // }
    // if (!dummies.contains(lNode)) {
    // continue;
    // }
    // int l = nextLayer.indexOf(lNode);
    // if (l == -1) {
    // throw new IllegalStateException();
    // }
    // if (j > l) {
    // type2++;
    // }
    // }
    // }
    // }
    // }
    // }
    // return type2;
    //
    // }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
