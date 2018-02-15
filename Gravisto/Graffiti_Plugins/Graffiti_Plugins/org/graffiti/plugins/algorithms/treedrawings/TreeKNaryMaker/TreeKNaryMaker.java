// =============================================================================
//
//   TreeKNaryMaker.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: TreeKNaryMaker.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.TreeKNaryMaker;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeSet;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.algorithm.PreconditionException.Entry;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;
import org.graffiti.plugins.algorithms.treedrawings.Util;
import org.graffiti.selection.Selection;

/**
 * @author Andreas
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */
public class TreeKNaryMaker extends AbstractAlgorithm {

    /**
     * The maximum degree given.
     */
    private int degree = 2;

    /**
     * Selection
     */
    private Selection selection = null;

    /**
     * The root of the tree given in the attached Graph. This field is populated
     * using the method GraphChecker.checkTree()
     */
    private Node root;

    /**
     * The TreeKNaryMaker can run in different modes (<code>strategy</code>):<BR>
     * <BR>
     * 1. <code>ONE_SIDED</code>: The HelperNodes will be inserted one-sided to
     * reduce the degree of the given tree.<BR>
     * 2. <code>BALANCED</code>: The HelperNodes will be inserted in a way that
     * minimizes the height of the created tree and still reduces the degree of
     * the tree to the limit specified by <code>degree</code> without changing
     * any father-son-relationships.
     */
    private int strategy;

    public static final int ONE_SIDED = 0;

    public static final int BALANCED = 1;

    /**
     * The TreeKNaryMaker can be executed on parts of the given tree:<BR>
     * <BR>
     * 1. <code>NONE_TAKE_ALL</code>: Just consider the whole graph<BR>
     * 2. <code>DOWNWARDS</code>: Only consider the subtrees of the selected
     * Nodes<BR>
     * 3. <code>ONLY_LOCAL</code>: Only consider the degree at the selected
     * Nodes.
     */
    private int selectedNodesPolicy;

    public static final int NONE_TAKE_ALL = 2;

    public static final int ONLY_LOCAL = 3;

    public static final int DOWNWARDS = 4;

    /**
     * The parameter for <code>selection</code>
     */
    private SelectionParameter seleParam;

    /**
     * The parameter for <code>degree</code>
     */
    private IntegerParameter degreeParameter;

    /**
     * The parameter for <code>strategy</code>
     */
    private StringSelectionParameter strategyParamter;

    /**
     * The parameter for <code>selectedNodesPolicy<code>
     */
    private StringSelectionParameter selectedNodesPolicyParameter;

    /**
     * Set <code>strategy</code> (for non-GUI use)
     * 
     * @param strategy
     */
    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }

    /**
     * Set <code>selectedNodesPolicy</code> (for non-GUI use)
     * 
     * @param selectedNodesPolicy
     */
    public void setSelectedNodesPolicy(int selectedNodesPolicy) {
        this.selectedNodesPolicy = selectedNodesPolicy;
    }

    /**
     * Set <code>degree</code> (for non-GUI use)
     * 
     * @param degree
     */
    public void setDegree(int degree) {
        this.degree = degree;
    }

    /**
     * Constructs a new instance.
     */
    public TreeKNaryMaker() {
        this.seleParam = new SelectionParameter("Selected Node:",
                "To start the algorithm at");
        this.degreeParameter = new IntegerParameter(new Integer(2),
                new Integer(2), new Integer(10), "Degree", "...");
        this.strategyParamter = new StringSelectionParameter(new String[] {
                "ONE_SIDED", "BALANCED" }, "Splitting Strategy", "...");
        this.selectedNodesPolicyParameter = new StringSelectionParameter(
                new String[] { "NONE_TAKE_ALL", "DOWNWARDS", "ONLY_LOCAL" },
                "Selected Nodes Policy", "Only if Nodes selected");
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "TreeKNaryMaker";
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new Parameter[] { this.seleParam, this.degreeParameter,
                this.strategyParamter, this.selectedNodesPolicyParameter };
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        this.selection = ((SelectionParameter) params[0]).getSelection();
        this.degree = ((IntegerParameter) params[1]).getInteger();

        String strategyName = ((StringSelectionParameter) params[2]).getValue();
        if (strategyName.equals("ONE_SIDED")) {
            this.strategy = TreeKNaryMaker.ONE_SIDED;
        } else if (strategyName.equals("BALANCED")) {
            this.strategy = TreeKNaryMaker.BALANCED;
        }

        String selectedNodesPolicyName = ((StringSelectionParameter) params[3])
                .getValue();
        if (selectedNodesPolicyName.equals("NONE_TAKE_ALL")) {
            this.selectedNodesPolicy = TreeKNaryMaker.NONE_TAKE_ALL;
        } else if (selectedNodesPolicyName.equals("ONLY_LOCAL")) {
            this.selectedNodesPolicy = TreeKNaryMaker.ONLY_LOCAL;
        } else if (selectedNodesPolicyName.equals("DOWNWARDS")) {
            this.selectedNodesPolicy = TreeKNaryMaker.DOWNWARDS;
        }

    }

    /*
     * Executes the TreeKNaryMaker considering the parameters described above.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        this.graph.getListenerManager().transactionStarted(this);

        // Necessary for CLI use...
        if (this.selection == null) {
            this.selection = new Selection();
        }

        if (this.strategy == TreeKNaryMaker.ONE_SIDED) {
            this.expandOneSided(root, false);
        } else if (this.strategy == TreeKNaryMaker.BALANCED) {
            this.expandBalanced(root, false);
        }

        this.graph.getListenerManager().transactionFinished(this);

    }

    /**
     * The method that inserts HelperNodes, if <code>strategy</code> is
     * <code>BALANCED</code>
     * 
     * @param node
     * @param clearedDescendants
     *            this contains the information if an ancestor of
     *            <code>node</code> has been selected.
     */
    protected int expandBalanced(Node node, boolean clearedDescendants) {
        if (node.getOutDegree() == 0)
            return 1;

        TreeSet<NodeHeight> childHeights = new TreeSet<NodeHeight>(
                new NodeHeightComparator());

        if (this.selectedNodesPolicy == TreeKNaryMaker.DOWNWARDS
                && this.selection.contains(node)) {
            clearedDescendants = true;
        }

        for (Node currentNeighbour : node.getAllOutNeighbors()) {
            int currentHeight = this.expandBalanced(currentNeighbour,
                    clearedDescendants);
            childHeights.add(new NodeHeight(currentHeight, currentNeighbour));
        }

        if ((this.selection.contains(node))
                || (this.selectedNodesPolicy == TreeKNaryMaker.DOWNWARDS && clearedDescendants)
                || this.selectedNodesPolicy == TreeKNaryMaker.NONE_TAKE_ALL) {
            if (childHeights.size() > this.degree) {

                int howManyTooMuch = childHeights.size() - this.degree;

                Iterator<NodeHeight> childHeightsItr = childHeights.iterator();

                // Add helper Node...
                Node newHelperNode = Util.addHelperNode(this.graph, node);

                this.graph
                        .addEdge(node, newHelperNode, this.graph.isDirected());

                LinkedList<Node> usedToExpand = new LinkedList<Node>();
                for (int i = 0; i <= howManyTooMuch && i < this.degree; i++) {
                    NodeHeight currentNodeHeight = childHeightsItr.next();
                    Node currentNode = currentNodeHeight.getNode();
                    usedToExpand.add(currentNode);
                    LinkedList<Edge> allIncoming = new LinkedList<Edge>(
                            currentNode.getDirectedInEdges());
                    if (allIncoming.size() > 0) {
                        allIncoming.getFirst().setSource(newHelperNode);
                    }
                }

                return expandBalanced(node, clearedDescendants);

            }
        }

        return childHeights.last().getHeight() + 1;
    }

    /**
     * The method that inserts HelperNodes, if <code>strategy</code> is
     * <code>ONE_SIDED</code>
     * 
     * @param node
     * @param clearedDescendants
     *            this contains the information if an ancestor of
     *            <code>node</code> has been selected (for
     *            <code>selectedNodesPolicy</code> <code>DOWNWARDS</code>).
     */
    protected void expandOneSided(Node node, boolean clearedDescendants) {
        if (this.selectedNodesPolicy == TreeKNaryMaker.DOWNWARDS
                && this.selection.contains(node)) {
            clearedDescendants = true;
        }

        // Now let us deal with the descendants first...
        for (Node currentNode : node.getOutNeighbors()) {
            this.expandOneSided(currentNode, clearedDescendants);
        }

        if (node.getOutDegree() > this.degree
                && ((this.selectedNodesPolicy == TreeKNaryMaker.ONLY_LOCAL && this.selection
                        .contains(node))
                        || (this.selectedNodesPolicy == TreeKNaryMaker.DOWNWARDS && clearedDescendants) || this.selectedNodesPolicy == TreeKNaryMaker.NONE_TAKE_ALL)) {
            // skip the first this.degree - 1, because they are allowed...
            Iterator<Edge> outEdgeItr = node.getAllOutEdges().iterator();
            for (int i = 0; i < this.degree - 1; i++) {
                outEdgeItr.next();
            }

            // make a copy of all the edges that are too many
            LinkedList<Edge> allViolatingOutEdges = new LinkedList<Edge>();
            while (outEdgeItr.hasNext()) {
                allViolatingOutEdges.addLast(outEdgeItr.next());
            }

            // create the first HelperNode...
            Node currentHelperNode = Util.addHelperNode(this.graph, node);
            this.graph
                    .addEdge(node, currentHelperNode, this.graph.isDirected());

            int numberOfNodesLeft = allViolatingOutEdges.size();

            for (Edge currentOutEdge : allViolatingOutEdges) {
                // if we have almost filled up currentHelperNode we have to...
                if (currentHelperNode.getOutDegree() == this.degree - 1
                        && numberOfNodesLeft != 1) {
                    // add a new HelperNode...
                    Node newHelperNode = Util.addHelperNode(this.graph, node);
                    // connect the current one with the new one...
                    this.graph.addEdge(currentHelperNode, newHelperNode,
                            this.graph.isDirected());
                    // The newHelperNode becomes the currentHelperNode...
                    currentHelperNode = newHelperNode;
                }

                // now we connect the another node with the currentHelperNode
                currentOutEdge.setSource(currentHelperNode);
                numberOfNodesLeft--;
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

        if (this.degree < 2)
            throw new PreconditionException("Degree below 2 is not allowed.");
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

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
