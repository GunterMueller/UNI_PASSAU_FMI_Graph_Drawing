// =============================================================================
//
//   DAGSplitter.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DAGSplitter.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.DAGSplitter;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.PreconditionException.Entry;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;
import org.graffiti.selection.Selection;

/**
 * @author Andreas
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */
public class DAGSplitter extends AbstractAlgorithm {

    /**
     * The duplication strategy to be used.
     */
    private int duplicationStrategy = -1;

    /**
     * In this duplication strategy only one duplicate gets the subtree.
     */
    public static final int ONLY_ONE_DUPLICATE_WITH_SUBTREE = 0;

    /**
     * In this duplication strategy all duplicates get a duplicate of the
     * subtree.
     */
    public static final int SUBTREE_FOR_EACH_DUPLICATE = 1;

    /**
     * contains the nodes in the order of a topological sort.
     */
    private LinkedList<Node> nodesInTopSortOrder = null;

    /**
     * Determines whether the duplicated nodes should be coloured.
     */
    private boolean colouring = false;

    /** Selection */
    private Selection selection;

    /**
     * Set whether the duplicated nodes should be coloured or not.
     * 
     * @param colouring
     */
    public void setColouring(boolean colouring) {
        this.colouring = colouring;
    }

    /**
     * Set the duplication Strategy. One of
     * <code>ONLY_ONE_DUPLICATE_WITH_SUBTREE</code> and
     * <code>SUBTREE_FOR_EACH_DUPLICATE</code>
     * 
     * @param duplicationStrategy
     */
    public void setDuplicationStrategy(int duplicationStrategy) {
        this.duplicationStrategy = duplicationStrategy;
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "DAGSplitter";
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {

        this.graph.getListenerManager().transactionStarted(this);
        HashMap<Node, LinkedList<Node>> nodeListForColouring = null;

        if (this.duplicationStrategy == DAGSplitter.ONLY_ONE_DUPLICATE_WITH_SUBTREE) {
            nodeListForColouring = this.duplicateViolating();
        } else if (this.duplicationStrategy == DAGSplitter.SUBTREE_FOR_EACH_DUPLICATE) {

            // duplicate the violating and remember them for colouring...
            nodeListForColouring = this
                    .duplicateIncludingDescendants(this.nodesInTopSortOrder);
        }

        // do some colouring of the duplicates if wanted...
        if (this.colouring) {
            int startColor = 6776679; // HEX: 676767
            int endColor = 16777045;// 15658734; // HEX: FFFF55

            int currentColor = startColor;

            int stepSize = (endColor - startColor)
                    / Math.max(1, nodeListForColouring.size() - 1);
            for (Node currentOriginal : nodeListForColouring.keySet()) {

                ((ColorAttribute) currentOriginal
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.FILLCOLOR))
                        .setColor(new Color(currentColor));

                for (Node currentDuplicate : nodeListForColouring
                        .get(currentOriginal)) {
                    ((ColorAttribute) currentDuplicate
                            .getAttribute(GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.FILLCOLOR))
                            .setColor(new Color(currentColor));
                }
                currentColor += stepSize;

            }
        }
        this.graph.getListenerManager().transactionFinished(this);
    }

    /**
     * This duplicates the nodes that have more than one incoming edge.
     * 
     * @return a Map between the node and its duplicates
     */
    public HashMap<Node, LinkedList<Node>> duplicateViolating() {
        // Get all the nodes with more than one incoming Edge...
        LinkedList<Node> violatingNodes = new LinkedList<Node>();
        for (Node currentNode : this.graph.getNodes()) {
            if (currentNode.getInDegree() > 1) {
                violatingNodes.addLast(currentNode);
            }
        }

        HashMap<Node, LinkedList<Node>> violatingWithDuplicates = new HashMap<Node, LinkedList<Node>>();
        for (Node currentNode : violatingNodes) {

            // Grab a copy of all inEdges - all but the first one. That is the
            // one we can keep where it is...
            LinkedList<Edge> allExcessInEdges = new LinkedList<Edge>();
            Iterator<Edge> currentInEdgesItr = currentNode.getAllInEdges()
                    .iterator();
            currentInEdgesItr.next();

            while (currentInEdgesItr.hasNext()) {
                allExcessInEdges.addLast(currentInEdgesItr.next());
            }

            // remember the copies...
            LinkedList<Node> currentCopies = new LinkedList<Node>();

            // add a copy of the currentNode for each excess edge and
            // reassign a copied currentNode to each excess edge as the new
            // target...
            for (Edge currentExcessInEdge : allExcessInEdges) {
                Node newCopy = this.graph
                        .addNode((CollectionAttribute) currentNode
                                .getAttributes().copy());
                currentCopies.addLast(newCopy);
                currentExcessInEdge.setTarget(newCopy);
            }

            violatingWithDuplicates.put(currentNode, currentCopies);

        }

        return violatingWithDuplicates;
    }

    /**
     * This duplicates the nodes that have more than one incoming edge and
     * attaches a copy of the subtrees to each one of them.
     * 
     * @return a Map between the node and its duplicates
     */
    public HashMap<Node, LinkedList<Node>> duplicateIncludingDescendants(
            LinkedList<Node> nodeCandidates) {

        HashMap<Node, LinkedList<Node>> violatingWithDuplicates = new HashMap<Node, LinkedList<Node>>();
        for (Node currentNode : nodeCandidates) {
            if (currentNode.getInDegree() > 1) {
                // Grab a copy of all inEdges - all but the first one. That is
                // the one we can keep where it is...
                LinkedList<Edge> allExcessInEdges = new LinkedList<Edge>();
                Iterator<Edge> currentInEdgesItr = currentNode.getAllInEdges()
                        .iterator();
                currentInEdgesItr.next();

                while (currentInEdgesItr.hasNext()) {
                    allExcessInEdges.addLast(currentInEdgesItr.next());
                }

                // Because we want to capture the descendants we have to copy
                // the outEdges as well. So, get a copy of all the outgoing
                // edges of all outgoing Edges of the currentNode...
                LinkedList<Edge> currentOutEdges = new LinkedList<Edge>();
                for (Edge currentOutEdge : currentNode.getAllOutEdges()) {
                    currentOutEdges.addLast(currentOutEdge);
                }

                // remember the copies...
                LinkedList<Node> currentCopies = new LinkedList<Node>();

                // add a copy of the currentNode for each excess edge and
                // reassign a copied currentNode to each excess edge as the new
                // target...
                for (Edge currentExcessInEdge : allExcessInEdges) {
                    Node newCopy = this.graph
                            .addNode((CollectionAttribute) currentNode
                                    .getAttributes().copy());
                    currentCopies.addLast(newCopy);
                    currentExcessInEdge.setTarget(newCopy);

                    // Now add a copy of all outgoing Edges...
                    for (Edge currentOutEdge : currentOutEdges) {
                        this.graph.addEdge(newCopy, currentOutEdge.getTarget(),
                                this.graph.isDirected(),
                                (CollectionAttribute) currentOutEdge
                                        .getAttributes().copy());
                    }
                }

                // save the mapping in case we have to do colouring
                violatingWithDuplicates.put(currentNode, currentCopies);
            }
        }

        return violatingWithDuplicates;
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter seleParam = new SelectionParameter("Selection", "");

        StringSelectionParameter duplicationStrategyParam = new StringSelectionParameter(
                new String[] { "ONLY_ONE_DUPLICATE_WITH_SUBTREE",
                        "SUBTREE_FOR_EACH_DUPLICATE" }, "Duplication Strategy",
                "...");
        BooleanParameter colouringParameter = new BooleanParameter(false,
                "Colour Duplicated Nodes:", "");
        return new Parameter[] { seleParam, duplicationStrategyParam,
                colouringParameter };
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;

        this.selection = ((SelectionParameter) params[0]).getSelection();

        String duplicationStrategyName = ((StringSelectionParameter) params[1])
                .getSelectedValue();
        if (duplicationStrategyName.equals("ONLY_ONE_DUPLICATE_WITH_SUBTREE")) {
            this.duplicationStrategy = DAGSplitter.ONLY_ONE_DUPLICATE_WITH_SUBTREE;
        } else if (duplicationStrategyName.equals("SUBTREE_FOR_EACH_DUPLICATE")) {
            this.duplicationStrategy = DAGSplitter.SUBTREE_FOR_EACH_DUPLICATE;
        }

        this.colouring = ((BooleanParameter) params[2]).getValue();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        try {
            this.nodesInTopSortOrder = GraphChecker.checkDAG(this.graph);
        } catch (PreconditionException p) {

            this.selection.clear();

            Iterator<Entry> itr = p.iterator();
            while (itr.hasNext()) {
                Selection selection = (Selection) itr.next().source;
                if (selection != null) {
                    this.selection.addSelection(selection);
                }
            }
            throw p;
        }
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
