// ==============================================================================
//
//   RemoveNetworkDataAlgorithm.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RemoveNetworkDataAlgorithm.java 5766 2010-05-07 18:39:06Z gleissner $

/*
 * Created on 17.07.2004
 */

package org.graffiti.plugins.algorithms.networkFlow;

import java.util.Iterator;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;

/**
 * Provides a variety of methods to remove special network data from networks.
 * 
 * @author Markus K�ser
 * @version $Revision 1.0 $
 */
public class RemoveNetworkDataAlgorithm extends AbstractAlgorithm {

    /** The NAME of the algorithm */
    private static final String NAME = "Remove Network Data";

    /** The number of parameters for this algorithm */
    private static final int NUMBER_OF_PARAMS = 5;

    /** The index for the parameter 'delete flow' */
    private static final int DELETE_FLOW_INDEX = 0;

    /** default value of delete flow */
    private static final boolean DELETE_FLOW_DEFAULT = false;

    /** NAME of the delete flow parameter */
    private static final String DELETE_FLOW_NAME = "Remove flow";

    /** description of the delete flow parameter */
    private static final String DELETE_FLOW_DESCRIPTION = "removes all "
            + "flow an all flow source numbers from the graph";

    /** The index for the 'delete cyclic flow' parameter */
    private static final int DELETE_CYCLIC_FLOW_INDEX = 1;

    /** default value of delete cyclic flow */
    private static final boolean DELETE_CYCLIC_FLOW_DEFAULT = false;

    /** NAME of the delete cyclic flow parameter */
    private static final String DELETE_CYCLIC_FLOW_NAME = "Removes cylcic flow";

    /** description of the delete cyclic flow parameter */
    private static final String DELETE_CYCLIC_FLOW_DESCRIPTION = "Removes "
            + "redundant cyclic flows, that do not add to the total network flow.";

    /** The index for the 'recolor edges' parameter */
    private static final int ADJUST_COLOR_AND_LABELS_INDEX = 2;

    /** default value of recolor edges */
    private static final boolean ADJUST_COLOR_AND_LABELS_DEFAULT = true;

    /** NAME of the recolor edges parameter */
    private static final String ADJUST_COLOR_AND_LABELS_NAME = "Adjust edge"
            + "color and labels";

    /** description of the recolor edges parameter */
    private static final String ADJUST_COLOR_AND_LABELS_DESCRIPTION = " recolors "
            + "the edges to new flow values and adjusts flow labels";

    /** The index for the 'delete capacities' parameter */
    private static final int DELETE_CAPACITIES_INDEX = 3;

    /** default value of delete capacities */
    private static final boolean DELETE_CAPACITIES_DEFAULT = false;

    /** NAME of the delete capacities parameter */
    private static final String DELETE_CAPACITIES_NAME = "Remove capacities";

    /** description of the delete capacities parameter */
    private static final String DELETE_CAPACITIES_DESCRIPTION = "removes all "
            + "capacities from the flow network, leaving an ordinary graph";

    /** The index for the 'delete source and sink labels' parameter */
    private static final int DELETE_SOURCE_AND_SINK_LABELS_INDEX = 4;

    /** default value of delete source and sink labels */
    private static final boolean DELETE_SOURCE_AND_SINK_LABELS_DEFAULT = false;

    /** NAME of the delete source and sink labels parameter */
    private static final String DELETE_SOURCE_AND_SINK_LABELS_NAME = "Delete"
            + " standard source and sink labels";

    /** description of the delete source and sink labels parameter */
    private static final String DELETE_SOURCE_AND_SINK_LABELS_DESCRIPTION = "deletes the standard source and sink labels";

    /** The source label */
    private static String sourceLabel = PreflowPushAlgorithm.SOURCE_LABEL_DEFAULT;

    /** the sink label */
    private static String sinkLabel = PreflowPushAlgorithm.SINK_LABEL_DEFAULT;

    /** The flow network support algorithms */
    private FlowNetworkSupportAlgorithms nsa = FlowNetworkSupportAlgorithms
            .getFlowNetworkSupportAlgorithms();

    /** true, if edges should be recolored */
    private boolean adjustColorsAndLabels = ADJUST_COLOR_AND_LABELS_DEFAULT;

    /** true, if capacities should be deleted */
    private boolean deleteCapacities = DELETE_CAPACITIES_DEFAULT;

    /** true, if cyclic flows should be deleted */
    private boolean deleteCyclicFlow = DELETE_CYCLIC_FLOW_DEFAULT;

    /** true, if flow should be deleted */
    private boolean deleteFlow = DELETE_FLOW_DEFAULT;

    /** true, if standard source and sink labels should be deleted */
    private boolean deleteSourceAndSinkLabel = DELETE_SOURCE_AND_SINK_LABELS_DEFAULT;

    /**
     * Constructs a new instance of the delete flow algorithm
     */
    public RemoveNetworkDataAlgorithm() {
        super();
        generateParameters();
    }

    /**
     * Sets the value of <code> adjustColorsAndLabels </code>, which determines,
     * if the algorithm adjusts the color of edges with changed flow. It also
     * changes the flow labels of edges with changed flow.
     * 
     * @param b
     *            adjustColorsAndLabels
     */
    public void setAdjustColorsAndLabels(boolean b) {
        adjustColorsAndLabels = b;
    }

    /**
     * Returns the value of <code> adjustColorsAndLabels </code>, which
     * determines, if if the algorithm adjusts the color of edges with changed
     * flow. It also changes the flow labels of edges with changed flow.
     * 
     * @return adjustColorsAndLabels
     */
    public boolean getAdjustColorsAndLabels() {
        return adjustColorsAndLabels;
    }

    /**
     * Sets the Parameters of the algorithm.
     * 
     * @param delFlow
     *            sets, if edges will be colored black, while the deleting the
     *            flow.
     * @param delCyclicFlow
     *            if true, cyclic flows will be removed
     * @param delCaps
     *            if true, capacities will be removed
     * @param adjustColorAndLabels
     *            if true, edges are recolored relativ to flow capacity and the
     *            flow labels are adjusted to the new values.
     * @param deleteStandardLabels
     *            sets, if the standard labels for source and sink nodes should
     *            be deleted.
     */
    public void setAll(boolean delFlow, boolean delCyclicFlow, boolean delCaps,
            boolean adjustColorAndLabels, boolean deleteStandardLabels) {
        setDeleteFlow(delFlow);
        setDeleteCyclicFlow(delCyclicFlow);
        setAdjustColorsAndLabels(adjustColorAndLabels);
        setDeleteCapacities(delCaps);
        setDeleteSourceAndSinkLabel(deleteStandardLabels);
    }

    /**
     * Sets the value of of <code> deleteCapacities </code>, which determines,
     * if the algorithm will delete the capacities of all edges.
     * 
     * @param b
     *            deleteCapacities
     */
    public void setDeleteCapacities(boolean b) {
        deleteCapacities = b;
    }

    /**
     * Returns the value of <code> deleteCapacities </code>, which determines,
     * if the algorithm will delete the capacities of all edges.
     * 
     * @return delete capacities
     */
    public boolean getDeleteCapacities() {
        return deleteCapacities;
    }

    /**
     * Sets the value of <code> deleteCyclicFlows </code>, which determines, if
     * the algorithm will remove redundant cyclic flows, from the network, that
     * do not add to the total network flow.
     * 
     * @param del
     *            deleteCyclicFlows
     */
    public void setDeleteCyclicFlow(boolean del) {
        deleteCyclicFlow = del;
    }

    /**
     * Returns the value of <code> deleteCyclicFlows </code>, which determines,
     * if the algorithm will remove redundant cyclic flows, from the network,
     * that do not add to the total network flow.
     * 
     * @return deleteCyclicFlows
     */
    public boolean getDeleteCyclicFlow() {
        return deleteCyclicFlow;
    }

    /**
     * Sets the value of <code> deleteFlow </code>, which determines, if the
     * algorithm will delete the flow and flow source of all edges.
     * 
     * @param del
     *            deleteFlow
     */
    public void setDeleteFlow(boolean del) {
        deleteFlow = del;
    }

    /**
     * Returns the value of <code> deleteFlow </code>, which determines, if the
     * algorithm will delete the flow and flow source of all edges.
     * 
     * @return deleteFlow
     */
    public boolean getDeleteFlow() {
        return deleteFlow;
    }

    /**
     * Sets the value of <code> deleteSourceAndSinkLabel </code>, which
     * determines, if the algorithm removes all labels from the nodes which are
     * equal to the set source and sink labels.
     * 
     * @param b
     *            deleteSourceAndSinkLabel
     */
    public void setDeleteSourceAndSinkLabel(boolean b) {
        deleteSourceAndSinkLabel = b;
    }

    /**
     * Returns the value of <code> deleteSourceAndSinkLabel </code>, which
     * determines, if the algorithm removes all labels from the nodes which are
     * equal to the set source and sink labels.
     * 
     * @return deleteSourceAndSinkLabel
     */
    public boolean getDeleteSourceAndSinkLabel() {
        return deleteSourceAndSinkLabel;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return NAME;
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(org.graffiti.plugin.parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        super.setAlgorithmParameters(params);
        deleteFlow = ((BooleanParameter) params[DELETE_FLOW_INDEX])
                .getBoolean().booleanValue();
        deleteCyclicFlow = ((BooleanParameter) params[DELETE_CYCLIC_FLOW_INDEX])
                .getBoolean().booleanValue();
        adjustColorsAndLabels = ((BooleanParameter) params[ADJUST_COLOR_AND_LABELS_INDEX])
                .getBoolean().booleanValue();
        deleteCapacities = ((BooleanParameter) params[DELETE_CAPACITIES_INDEX])
                .getBoolean().booleanValue();
        deleteSourceAndSinkLabel = ((BooleanParameter) params[DELETE_SOURCE_AND_SINK_LABELS_INDEX])
                .getBoolean().booleanValue();
    }

    /**
     * Sets a new <code> sinkLabel </code> to the algorithm
     * 
     * @param string
     */
    public static void setSinkLabel(String string) {
        sinkLabel = string;
    }

    /**
     * Returns the <code> sinkLabel </code> assumed by the algorithm.
     * 
     * @return the sink label
     */
    public static String getSinkLabel() {
        return sinkLabel;
    }

    /**
     * Sets a new <code> sourceLabel </code> to the algorithm
     * 
     * @param string
     */
    public static void setSourceLabel(String string) {
        sourceLabel = string;
    }

    /**
     * Returns the <code> sourceLabel </code> set to the algorithm.
     * 
     * @return the source label
     */
    public static String getSourceLabel() {
        return sourceLabel;
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() {
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        if (deleteFlow) {
            nsa.removeFlow(graph);
        }

        if (deleteCyclicFlow && !deleteFlow) {
            nsa.removeCyclicFlows(graph);
        }

        if (deleteCapacities) {
            nsa.removeCapacities(graph);
        }

        Node tempNode;

        if (deleteSourceAndSinkLabel) {
            for (Iterator<Node> nodeIt = graph.getNodesIterator(); nodeIt
                    .hasNext();) {
                tempNode = nodeIt.next();

                if ((nsa.getLabel(tempNode).equals(sourceLabel))
                        || (nsa.getLabel(tempNode).equals(sinkLabel))) {
                    nsa.removeLabel(tempNode);
                }
            }
        }

        if (adjustColorsAndLabels) {
            nsa.colorFlowEdges(graph);
            nsa.generateVisibleFlowLabels(graph);
        }
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    @Override
    public void reset() {
        super.reset();
        generateParameters();
        deleteFlow = DELETE_FLOW_DEFAULT;
        deleteCyclicFlow = DELETE_CYCLIC_FLOW_DEFAULT;
        adjustColorsAndLabels = ADJUST_COLOR_AND_LABELS_DEFAULT;
        deleteCapacities = DELETE_CAPACITIES_DEFAULT;
        deleteSourceAndSinkLabel = DELETE_SOURCE_AND_SINK_LABELS_DEFAULT;
        sourceLabel = PreflowPushAlgorithm.SOURCE_LABEL_DEFAULT;
        sinkLabel = PreflowPushAlgorithm.SINK_LABEL_DEFAULT;
    }

    /**
     * Generates the Paremeters.
     */
    private void generateParameters() {
        BooleanParameter deleteFlowParam = new BooleanParameter(
                DELETE_FLOW_DEFAULT, DELETE_FLOW_NAME, DELETE_FLOW_DESCRIPTION);
        BooleanParameter deleteCyclicFlowParam = new BooleanParameter(
                DELETE_CYCLIC_FLOW_DEFAULT, DELETE_CYCLIC_FLOW_NAME,
                DELETE_CYCLIC_FLOW_DESCRIPTION);
        BooleanParameter adjustColorLabelParam = new BooleanParameter(
                ADJUST_COLOR_AND_LABELS_DEFAULT, ADJUST_COLOR_AND_LABELS_NAME,
                ADJUST_COLOR_AND_LABELS_DESCRIPTION);
        BooleanParameter deleteCapacitiesParam = new BooleanParameter(
                DELETE_CAPACITIES_DEFAULT, DELETE_CAPACITIES_NAME,
                DELETE_CAPACITIES_DESCRIPTION);
        BooleanParameter deleteSourceAndSinkLabelsParam = new BooleanParameter(
                DELETE_SOURCE_AND_SINK_LABELS_DEFAULT,
                DELETE_SOURCE_AND_SINK_LABELS_NAME,
                DELETE_SOURCE_AND_SINK_LABELS_DESCRIPTION);
        parameters = new Parameter[NUMBER_OF_PARAMS];
        parameters[DELETE_FLOW_INDEX] = deleteFlowParam;
        parameters[DELETE_CYCLIC_FLOW_INDEX] = deleteCyclicFlowParam;
        parameters[ADJUST_COLOR_AND_LABELS_INDEX] = adjustColorLabelParam;
        parameters[DELETE_CAPACITIES_INDEX] = deleteCapacitiesParam;
        parameters[DELETE_SOURCE_AND_SINK_LABELS_INDEX] = deleteSourceAndSinkLabelsParam;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
