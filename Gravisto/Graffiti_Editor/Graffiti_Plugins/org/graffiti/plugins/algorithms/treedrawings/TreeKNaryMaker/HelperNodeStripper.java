// =============================================================================
//
//   HelperNodeStripper.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: HelperNodeStripper.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.TreeKNaryMaker;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.PreconditionException.Entry;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;
import org.graffiti.plugins.algorithms.treedrawings.Util;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;
import org.graffiti.selection.Selection;

/**
 * This is the algorithm responsible for removing HelperNodes.
 * 
 * @author Andreas
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */
public class HelperNodeStripper extends AbstractAlgorithm {

    /**
     * The root of the tree given in the attached Graph. This field is populated
     * using the method GraphChecker.checkTree()
     */
    private Node root = null;

    /**
     * Selection
     */
    private Selection selection = null;

    /**
     * A HelperNodeStripper can be executed in various modes (
     * <code>strategy</code>):<BR>
     * <BR>
     * 1. <code>REMOVE</code>: This will just remove the HelperNodes and draw
     * edges from non-HelperNodes to all connected non-HelperNodes.<BR>
     * 2. <code>SUBSTITUTE_BY_BENDS</code>: This will substitute the HelperNodes
     * by bends. If a HelperNode has more than one outgoing Edge the Edge is
     * duplicated.<BR>
     * 3. <code>REMOVE_UNNECESSARY</code>: This acts like <code>REMOVE</code>,
     * but only remove the HelperNodes in which the direction of the layout
     * (Attribute "layout.isHorizontal") does not change.
     */
    private int strategy;

    public static final int REMOVE = 0;

    public static final int SUBSTITUTE_BY_BENDS = 1;

    public static final int REMOVE_UNNECESSARY = 2;

    /**
     * The HelperNodeStripper can be executed on parts of the given tree:<BR>
     * <BR>
     * 1. <code>NONE_TAKE_ALL</code>: Just consider the whole graph<BR>
     * 2. <code>DOWNWARDS</code>: Only consider the subtrees of the selected
     * Nodes<BR>
     * 3. <code>ONLY_LOCAL</code>: Only consider the HelperNodes between the
     * selected Nodes and the next non-HelperNodes.
     */
    private int selectedNodesPolicy;

    public static final int NONE_TAKE_ALL = 3;

    public static final int DOWNWARDS = 4;

    public static final int ONLY_LOCAL = 5;

    /**
     * the Parameter for <code>strategy</code>
     */
    private StringSelectionParameter strategyParamter;

    /**
     * the Parameter for <code>selectedNodesPolicy</code>
     */
    private StringSelectionParameter selectedNodesPolicyParameter;

    /**
     * set the <code>strategy</code> (for non-GUI use)
     * 
     * @param strategy
     */
    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }

    /**
     * set the <code>selectedNodesPolicy</code> (for non-GUI use)
     * 
     * @param selectedNodesPolicy
     */
    public void setSelectedNodesPolicy(int selectedNodesPolicy) {
        this.selectedNodesPolicy = selectedNodesPolicy;
    }

    /**
     * Constructs a new instance.
     */
    public HelperNodeStripper() {
        this.strategyParamter = new StringSelectionParameter(new String[] {
                "REMOVE", "SUBSTITUTE_BY_BENDS", "REMOVE_UNNECESSARY" },
                "Stripping Strategy", "...");
        this.selectedNodesPolicyParameter = new StringSelectionParameter(
                new String[] { "NONE_TAKE_ALL", "DOWNWARDS", "ONLY_LOCAL" },
                "Selected Nodes Policy", "Only if Nodes selected");
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "HelperNodeStripper";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter seleParam = new SelectionParameter("Selected Node:",
                "To start the algorithm at");

        return new Parameter[] { seleParam, this.strategyParamter,
                this.selectedNodesPolicyParameter };

    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        this.selection = ((SelectionParameter) params[0]).getSelection();
        String strategyName = ((StringSelectionParameter) params[1]).getValue();
        if (strategyName.equals("REMOVE")) {
            this.strategy = HelperNodeStripper.REMOVE;
        } else if (strategyName.equals("SUBSTITUTE_BY_BENDS")) {
            this.strategy = HelperNodeStripper.SUBSTITUTE_BY_BENDS;
        } else if (strategyName.equals("REMOVE_UNNECESSARY")) {
            this.strategy = HelperNodeStripper.REMOVE_UNNECESSARY;
        }

        String selectedNodesPolicyName = ((StringSelectionParameter) params[2])
                .getValue();
        if (selectedNodesPolicyName.equals("NONE_TAKE_ALL")) {
            this.selectedNodesPolicy = HelperNodeStripper.NONE_TAKE_ALL;
        } else if (selectedNodesPolicyName.equals("DOWNWARDS")) {
            this.selectedNodesPolicy = HelperNodeStripper.DOWNWARDS;
        } else {
            this.selectedNodesPolicy = HelperNodeStripper.ONLY_LOCAL;
        }
    }

    /*
     * Executes the HelperNodeStripper considering the parameters described
     * above.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        this.graph.getListenerManager().transactionStarted(this);

        HashSet<Node> allNodesNeeded = new HashSet<Node>();

        if (this.selectedNodesPolicy == HelperNodeStripper.NONE_TAKE_ALL) {
            for (Node currentNode : this.graph.getNodes()) {
                if (!Util.isHelperNode(currentNode)) {
                    allNodesNeeded.add(currentNode);
                }
            }
        } else if (this.selectedNodesPolicy == HelperNodeStripper.DOWNWARDS) {
            setAllNodesNeededDownwards(this.root, false, allNodesNeeded);
        } else {
            for (Node currentSelectedNode : this.selection.getNodes()) {
                allNodesNeeded.add(currentSelectedNode);
            }
        }

        for (Node currentNode : allNodesNeeded) {

            // We have to do a dfs from every non HelperNode n1
            // and add an edge for every non HelperNode n2 reachable from n1
            // using only HelperNodes along the way...

            if (this.strategy == HelperNodeStripper.REMOVE) {
                this.removeAllFrom(currentNode, currentNode, false);

            } else if (this.strategy == HelperNodeStripper.SUBSTITUTE_BY_BENDS) {
                this.substituteByBends(currentNode, currentNode, false,
                        new LinkedList<Point2D>());
            } else if (this.strategy == HelperNodeStripper.REMOVE_UNNECESSARY) {
                boolean isCurrentHorizontal = currentNode
                        .getBoolean("layout.isHorizontal");
                this.removeUnnecessaryFrom(currentNode, isCurrentHorizontal,
                        true);
            }

        }
        this.graph.getListenerManager().transactionFinished(this);

    }

    /**
     * Remove the HelperNodes between the <code>sourceNode</code> and the next
     * non-HelperNode and insert Edges for all non-HelperNodes reachable (one
     * for each <code>targetNode</code>). If <code>encounteredHelperNode</code>
     * is false, then no HelperNode was discovered from <code>sourceNode</code>
     * and no new edges have to be drawn.
     * 
     * @param sourceNode
     * @param targetNode
     * @param encounteredHelperNode
     */
    protected void removeAllFrom(Node sourceNode, Node targetNode,
            boolean encounteredHelperNode) {

        LinkedList<Edge> allOutEdges = new LinkedList<Edge>();
        for (Edge currentOutEdge : targetNode.getAllOutEdges()) {
            allOutEdges.add(currentOutEdge);
        }

        for (Edge currentOutEdge : allOutEdges) {
            Node currentTargetNode = currentOutEdge.getTarget();
            if (Util.isHelperNode(currentTargetNode)) {
                this.removeAllFrom(sourceNode, currentTargetNode, true);
                this.graph.deleteNode(currentTargetNode);
            } else {
                if (encounteredHelperNode) {
                    this.graph.addEdge(sourceNode, currentTargetNode,
                            this.graph.isDirected(),
                            (CollectionAttribute) currentOutEdge
                                    .getAttributes().copy());
                }
            }
        }
    }

    /**
     * Remove the HelperNodes between the <code>sourceNode</code> and the next
     * non-HelperNode and insert Edges for all non-HelperNodes reachable that
     * are unnecessary (one for each <code>targetNode</code>). HelperNodes are
     * deemed to be unneccesary if the direction of the layout does not change
     * to the one used in their father Node. If <code>foundUnnecessary</code> is
     * false, then no unnecessary HelperNode was discovered from
     * <code>sourceNode</code> and no new edges have to be drawn.
     * 
     * @param sourceNode
     * @param isSourceHorizontal
     *            this is used to determine whether the direction has changed.
     * @param foundUnnecessary
     */
    protected void removeUnnecessaryFrom(Node sourceNode,
            boolean isSourceHorizontal, boolean foundUnnecessary) {

        LinkedList<Edge> allOutEdges = new LinkedList<Edge>();
        for (Edge currentSourceOutEdge : sourceNode.getAllOutEdges()) {
            allOutEdges.add(currentSourceOutEdge);
        }

        if (foundUnnecessary) {
            foundUnnecessary = false;
            for (Edge currentEdge : allOutEdges) {
                Node currentTargetNode = currentEdge.getTarget();
                boolean isCurrentTargetHorizontal = currentTargetNode
                        .getBoolean("layout.isHorizontal");
                if (Util.isHelperNode(currentTargetNode)
                        && isSourceHorizontal == isCurrentTargetHorizontal) {
                    foundUnnecessary = true;
                    LinkedList<Edge> allTargetOutEdges = new LinkedList<Edge>();
                    for (Edge currentTargetOutEdge : currentTargetNode
                            .getAllOutEdges()) {
                        allTargetOutEdges.add(currentTargetOutEdge);
                    }

                    for (Edge currentTargetEdge : allTargetOutEdges) {
                        currentTargetEdge.setSource(sourceNode);
                    }

                    this.graph.deleteNode(currentTargetNode);
                }

            }

            this.removeUnnecessaryFrom(sourceNode, isSourceHorizontal,
                    foundUnnecessary);
        } else {
            for (Edge currentEdge : allOutEdges) {
                Node currentTargetNode = currentEdge.getTarget();
                boolean isCurrentTargetHorizontal = currentTargetNode
                        .getBoolean("layout.isHorizontal");
                this.removeUnnecessaryFrom(currentTargetNode,
                        isCurrentTargetHorizontal, true);
            }
        }

    }

    /**
     * This will substitute the HelperNodes by bends. If a HelperNode has more
     * than one outgoing Edge the Edge is duplicated. New bends will be added
     * from the <code>sourceNode</code> to all Nodes that are
     * <code>targetNode</code>. This algorithm considers all HelperNodes that
     * are reachable between the <code>sourceNode</code> and the next
     * non-HelperNodes.
     * 
     * @param sourceNode
     * @param targetNode
     * @param encounteredHelperNode
     *            if this is false no HelperNode was encountered and nothing has
     *            to be changed/added.
     * @param allBends
     *            for accumulating the bends along the way.
     */
    protected void substituteByBends(Node sourceNode, Node targetNode,
            boolean encounteredHelperNode, LinkedList<Point2D> allBends) {
        LinkedList<Edge> allOutEdges = new LinkedList<Edge>();
        for (Edge currentOutEdge : targetNode.getAllOutEdges()) {
            allOutEdges.add(currentOutEdge);
        }

        for (Edge currentOutEdge : allOutEdges) {
            Node currentTargetNode = currentOutEdge.getTarget();

            // make a clone of all bends accumulated so far...
            @SuppressWarnings("unchecked")
            LinkedList<Point2D> allBendsClone = (LinkedList<Point2D>) allBends
                    .clone();

            // get all bends of the currentOutEdge...
            SortedCollectionAttribute ba = (SortedCollectionAttribute) currentOutEdge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.BENDS);
            Collection<Attribute> bendsOfCurrentEdge = ba.getCollection()
                    .values();

            for (Attribute currentBend : bendsOfCurrentEdge) {
                Point2D currentBendPoint = ((CoordinateAttribute) currentBend)
                        .getCoordinate();
                allBendsClone.addLast(currentBendPoint);
            }

            if (Util.isHelperNode(currentTargetNode)) {

                // add the bend of the currentTargetNode (HelperNode)...
                CoordinateAttribute ca = (CoordinateAttribute) currentTargetNode
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
                allBendsClone.addLast(ca.getCoordinate());

                this.substituteByBends(sourceNode, currentTargetNode, true,
                        allBendsClone);
                this.graph.deleteNode(currentTargetNode);
            } else {
                if (encounteredHelperNode) {
                    // add a new edge...
                    Edge newEdge = this.graph.addEdge(sourceNode,
                            currentTargetNode, this.graph.isDirected(),
                            (CollectionAttribute) currentOutEdge
                                    .getAttributes().copy());

                    // now add the bends...
                    EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) newEdge
                            .getAttribute("graphics");
                    SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                            "bends");

                    int bendIndex = 0;
                    for (Point2D currentBend : allBendsClone) {
                        bends.add(new CoordinateAttribute("bend" + bendIndex,
                                currentBend));
                        bendIndex++;
                    }

                    edgeAttr.setBends(bends);
                    edgeAttr.setShape(PolyLineEdgeShape.class.getName());
                }
            }
        }
    }

    /**
     * Check the following:<BR>
     * <BR>
     * 1. Is the given Graph a directed tree.<BR>
     * 2. If <code>selectedNodesPolicy</code> is <code>ONLY_LOCAL</code> or
     * <code>DOWNWARDS</code>: Is at least one Node selected?
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        try {
            this.root = GraphChecker.checkTree(this.graph, Integer.MAX_VALUE);
        } catch (PreconditionException p) {

            Iterator<Entry> itr = p.iterator();

            this.selection.clear();

            while (itr.hasNext()) {
                Selection selection = (Selection) itr.next().source;
                if (selection != null) {
                    this.selection.addSelection(selection);
                }
            }
            throw p;
        }

        if ((this.selectedNodesPolicy == HelperNodeStripper.ONLY_LOCAL || this.selectedNodesPolicy == HelperNodeStripper.DOWNWARDS)
                && this.selection.getNodes().size() == 0)
            throw new PreconditionException(
                    "You have to select at least one node to use the modes \"ONLY LOCAL\" or \"DOWNWARDS\".");
    }

    /**
     * This method is used by <code>selectedNodesPolicy</code>
     * <code>DOWNWARDS</code> it adds all Nodes in the subtree of the given
     * <code>root</code> to given HashSet <code>allNodesNeeded</code>
     * 
     * @param root
     * @param descendantsCleared
     *            this contains the information if the <code>root</code> or a
     *            ancestor of it were selected.
     * @param allNodesNeeded
     */
    protected void setAllNodesNeededDownwards(Node root,
            boolean descendantsCleared, HashSet<Node> allNodesNeeded) {
        if (this.selection.contains(root)) {
            allNodesNeeded.add(root);
            descendantsCleared = true;
        }

        for (Node currentChild : root.getOutNeighbors()) {
            if (descendantsCleared) {
                allNodesNeeded.add(currentChild);
            }
            this.setAllNodesNeededDownwards(currentChild, descendantsCleared,
                    allNodesNeeded);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
