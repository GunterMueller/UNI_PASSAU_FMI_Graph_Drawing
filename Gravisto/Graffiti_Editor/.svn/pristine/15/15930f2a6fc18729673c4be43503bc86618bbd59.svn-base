package org.graffiti.plugins.algorithms.labeling.finitePositions;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import org.graffiti.attributes.Attribute;
import org.graffiti.editor.GraffitiSingleton;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.EdgeLabelAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.NodeLabelAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.view.View;
import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;
import org.graffiti.plugins.views.fast.FastView;
import org.graffiti.plugins.views.fast.label.Label;
import org.graffiti.plugins.views.fast.label.LabelManager;

public class FinitePositionsAlgorithm extends AbstractAlgorithm {

    private IntegerParameter numberOfCandidatePositionsParam;
    private int numberOfCandidatePositions = 8;

    private BooleanParameter isUseOriginalLabelPositionParam;
    private boolean isUseOriginalLabelPosition = false;

    private BooleanParameter penalizeOverlapsWithOutgoingEdgesParam;
    private boolean penalizeOverlapsWithOutgoingEdges = true;

    private BooleanParameter penalizeOverlapsWithAnyEdgesParam;
    private boolean penalizeOverlapsWithAnyEdges = true;

    private BooleanParameter penalizeOverlapsWithNodesParam;
    private boolean penalizeOverlapsWithNodes = true;

    private DoubleParameter interLabelGapParam;
    private double interLabelGap = 1d;

    public FinitePositionsAlgorithm() {
        this.isUseOriginalLabelPositionParam = new BooleanParameter(
                isUseOriginalLabelPosition,
                "consider original label positions",
                "creates a candidate position at the current position of a label");
        this.numberOfCandidatePositionsParam = new IntegerParameter(
                numberOfCandidatePositions, "number of generated candidates",
                "number of additionally created candidate positions per label",
                0, 8, 0, Integer.MAX_VALUE);
        this.penalizeOverlapsWithOutgoingEdgesParam = new BooleanParameter(
                penalizeOverlapsWithOutgoingEdges,
                "node labels: avoid overlaps with outgoing edges",
                "penalizes node label positions that overlap with outgoing edges");
        this.penalizeOverlapsWithAnyEdgesParam = new BooleanParameter(
                penalizeOverlapsWithAnyEdges, "avoid overlaps with edges",
                "penalizes label positions that overlap with edges");
        this.penalizeOverlapsWithNodesParam = new BooleanParameter(
                penalizeOverlapsWithNodes, "avoid overlaps with nodes",
                "penalizes label positions that overlap with nodes");
        this.interLabelGapParam = new DoubleParameter(interLabelGap,
                "inter label gab",
                "additional space required by each label to not be treated "
                        + "as overlapping another label", -5d, 20d);
    }

    public String getName() {
        return "Finite positions labeling algorithm";
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new Parameter[] { numberOfCandidatePositionsParam,
                isUseOriginalLabelPositionParam,
                penalizeOverlapsWithOutgoingEdgesParam,
                penalizeOverlapsWithAnyEdgesParam,
                penalizeOverlapsWithNodesParam, interLabelGapParam };
    }

    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        numberOfCandidatePositions = ((IntegerParameter) params[0])
                .getInteger().intValue();
        isUseOriginalLabelPosition = ((BooleanParameter) params[1])
                .getBoolean().booleanValue();
        penalizeOverlapsWithNodes = ((BooleanParameter) params[4]).getBoolean()
                .booleanValue();
        penalizeOverlapsWithAnyEdges = ((BooleanParameter) params[3])
                .getBoolean().booleanValue();
        penalizeOverlapsWithOutgoingEdges = ((BooleanParameter) params[2])
                .getBoolean().booleanValue()
                && !penalizeOverlapsWithAnyEdges; // if any edges is set, then
                                                  // not
        interLabelGap = ((DoubleParameter) params[5]).getDouble().doubleValue();
    }

    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        // The graph is inherited from AbstractAlgorithm.
        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    public void execute() {
        // Flush statistics (as different runs share the same algorithm)
        Statistics.reset();

        long nanoTime = System.nanoTime();

        // Step 1: extract position candidates for all labels

        // contains a label locator for every label of the graph
        ArrayList<LabelLocator> locators;

        try {
            locators = generateLabelPositionCandidates(graph,
                    numberOfCandidatePositions,
                    penalizeOverlapsWithOutgoingEdges,
                    isUseOriginalLabelPosition, penalizeOverlapsWithNodes,
                    penalizeOverlapsWithAnyEdges);
        } catch (ViewNotSupportedException e) {
            throw new RuntimeException(e.toString()
                    + " - algorithm execution stopped ");
        }

        // Step 2: add label collision information

        // contains label locators, which have candidate positions without
        // collisions.
        LinkedList<LabelLocator> placeableLocators;

        LabelCandidateCollisionStructure collisionStructure = new NaiveLabelCandidateCollisionStructure();

        placeableLocators = collisionStructure.collisionCalculation(locators,
                interLabelGap);

        // Step 3: mount labels to appropriate positions
        applyLabelPositions(locators, placeableLocators);

        // Statistics
        nanoTime = System.nanoTime() - nanoTime;
        System.out.println(Statistics.statisticsString());
        System.out.println("Algorithm running time: "
                + (nanoTime / 1000000000d) + "s");

    }

    /**
     * Creates a list of possible label positions for each label in the given
     * graph.
     * 
     * @param graph
     * @param numberOfCandidatePositions
     *            - number of position candidates to generate for each label
     * @return list of locators containing label position candidates
     * @throws ViewNotSupportedException
     */
    public static ArrayList<LabelLocator> generateLabelPositionCandidates(
            Graph graph, int numberOfCandidatePositions,
            boolean penalizeOverlapsWithOutgoingEdges,
            boolean isUseOriginalLabelPosition,
            boolean penalizeOverlapsWithNodes,
            boolean penalizeOverlapsWithAnyEdges)
            throws ViewNotSupportedException {
        // Extract label manager (used to compute label widths)
        View activeView = GraffitiSingleton.getInstance().getMainFrame()
                .getActiveSession().getActiveView();
        LabelManager<?, ?> labelManager = null;
        if (activeView instanceof FastView) {
            labelManager = ((FastView) activeView).getGraphicsEngine()
                    .getLabelManager();
        } else
            throw new ViewNotSupportedException(
                    "Label widths cannot be accessed (FastView needed).");

        // create locator list with initial capacity of #nodes
        // (assume one label per node and no edge labels)
        // (some node label candidate position will be pruned, so it is a
        // specialized list)
        ArrayList<NodeLabelLocator> nodeLabelLocators = new ArrayList<NodeLabelLocator>(
                graph.getNodes().size());
        Label<?, ?> label; // to get a label's width and height

        // node labels
        for (Node node : graph.getNodes()) {
            for (Attribute attr : node.getAttributes().getCollection().values()) {
                if (attr instanceof NodeLabelAttribute) {
                    label = labelManager.acquireLabel(node,
                            (NodeLabelAttribute) attr);
                    // add locator for the current label
                    nodeLabelLocators
                            .add(new NodeLabelLocator(
                                    (NodeLabelAttribute) attr,
                                    (NodeGraphicAttribute) node
                                            .getAttribute(GraphicAttributeConstants.GRAPHICS),
                                    new GeometricalVector(label.getWidth(),
                                            label.getHeight()),
                                    numberOfCandidatePositions,
                                    isUseOriginalLabelPosition));

                    // Penalize node label position candidates overlapping
                    // outgoing edges
                    // Needs to be done here (visibility of parent node)
                    if (penalizeOverlapsWithOutgoingEdges) {
                        NodeLabelLocator locator = nodeLabelLocators
                                .get(nodeLabelLocators.size() - 1);
                        Edge edge;
                        for (Iterator<Edge> it = node.getEdgesIterator(); it
                                .hasNext();) {
                            edge = it.next();
                            // Check overlaps between locator's positions
                            // and edge
                            for (NodeLabelPosition candidate : locator.candidatePositions) {
                                if (LabelCandidateCollisionStructure
                                        .checkOverlapWithEdge(candidate, edge)) {
                                    candidate
                                            .setQuality(candidate.getQuality() - 1d);
                                }
                            }
                        }
                    }
                }
            }
        }

        // create common locator list for both node and edge label locators
        ArrayList<LabelLocator> locators = new ArrayList<LabelLocator>(
                nodeLabelLocators);

        // edge labels
        for (Edge edge : graph.getEdges()) {
            for (Attribute attr : edge.getAttributes().getCollection().values()) {
                if (attr instanceof EdgeLabelAttribute) {
                    label = labelManager.acquireLabel(edge,
                            (EdgeLabelAttribute) attr);
                    // add locator for the current label
                    locators.add(new EdgeLabelLocator(
                            (EdgeLabelAttribute) attr, edge,
                            new GeometricalVector(label.getWidth(), label
                                    .getHeight()), numberOfCandidatePositions,
                            isUseOriginalLabelPosition));
                }
            }
        }

        // Penalize label position candidates overlapping nodes
        // TODO: drop candidates rather than penalize them
        // TODO: need parent if all candidates are dropped and new one is to be
        // generated
        if (penalizeOverlapsWithNodes) {
            LabelPosition candidate;
            for (LabelLocator locator : locators) {
                for (Node node : graph.getNodes()) {
                    // Check overlaps between locator's positions and edge
                    for (ListIterator<? extends LabelPosition> it = locator
                            .getCandidatesIterator(); it.hasNext();) {
                        candidate = it.next();
                        if (LabelCandidateCollisionStructure
                                .checkOverlapWithNode(candidate, node)) {
                            // Penalize quality
                            candidate.setQuality(candidate.getQuality() - 2d);
                        }
                    }
                }
            }
        }

        // Penalize label position candidates overlapping any edges
        if (penalizeOverlapsWithAnyEdges) {
            LabelPosition candidate;
            for (LabelLocator locator : locators) {
                for (Edge edge : graph.getEdges()) {
                    // Check overlaps between locator's positions and edge
                    for (ListIterator<? extends LabelPosition> it = locator
                            .getCandidatesIterator(); it.hasNext();) {
                        candidate = it.next();
                        if (LabelCandidateCollisionStructure
                                .checkOverlapWithEdge(candidate, edge)) {
                            // Penalize quality
                            candidate.setQuality(candidate.getQuality() - 1d);
                        }
                    }
                }
            }
        }

        return locators;
    }

    /**
     * Applies all labels to candidate positions, following a greedy heuristic.
     * <p>
     * At return, every label is placed to one of the
     * <p>
     * The effects of this routine are:
     * <ul>
     * <li>the state of the locators in the given list is changed
     * <li>the list <tt>placeableLocators</tt> will be cleared
     * <li>positions of labels referenced by the given locators are changed
     * </ul>
     * 
     * @param locators
     *            - list of locators to apply
     * @param placeableLocators
     *            - list of locators, which contain at least one position
     *            candidate without overlaps
     */
    public static void applyLabelPositions(ArrayList<LabelLocator> locators,
            LinkedList<LabelLocator> placeableLocators) {
        for (LabelLocator completeLocator : locators) {

            // first apply those locators that contain candidates
            // that don't cause problems
            while (placeableLocators.size() > 0) {
                // Remove and apply first locator
                placeableLocators.remove().applyToLabel();
            }

            if (!completeLocator.isAppliedYet()) {
                // apply to label, even though there is no
                // candidate without collision
                completeLocator.applyToLabel();
            }
        }
    }

}
