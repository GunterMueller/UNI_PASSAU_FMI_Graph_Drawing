// =============================================================================
//
//   RootChanger.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: RootChanger.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.RootChanger;

import java.util.HashSet;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.treedrawings.Util;
import org.graffiti.selection.Selection;

/**
 * @author Andreas
 * @version $Revision: 5766 $ $Date: 2010-05-07 19:21:40 +0200 (Fr, 07 Mai 2010)
 *          $
 */
public class RootChanger extends AbstractAlgorithm {

    /** Selection */
    private Selection selection = null;

    private Node newRoot = null;

    protected StringSelectionParameter appearanceParameter = null;

    private int edgeAppearance;

    private final static int CHANGE_APPEARANCE = 0;

    private final static int KEEP_APPEARANCE = 1;

    public RootChanger() {
        this.appearanceParameter = new StringSelectionParameter(new String[] {
                "CHANGE_APPEARANCE", "KEEP_APPEARANCE" },
                "Appearance of edges", "");
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "RootChanger";
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        this.graph.getListenerManager().transactionStarted(this);

        // This is pretty straight forward. Just do a bfs from newRoot and turn
        // around all edges that point in the "wrong" direction...
        this.bfsFlood(newRoot);
        this.graph.getListenerManager().transactionFinished(this);
    }

    /**
     * This algorithm can serve two different purposes:<BR>
     * <BR>
     * 1. Change the root of a given tree.<BR>
     * 2. Make the selected node the first node in any topological sorting of
     * the nodes.<BR>
     * <BR>
     * This is achieved by a modified BFS-decycler that turns around all edges
     * to point away from the currentNode.
     * 
     * @param startNode
     */
    protected void bfsFlood(Node startNode) {
        HashSet<Node> visited = new HashSet<Node>();
        LinkedList<Node> queue = new LinkedList<Node>();
        queue.addLast(startNode);

        while (!queue.isEmpty()) {

            Node currentStartNode = queue.removeFirst();
            visited.add(currentStartNode);

            for (Edge currentOutEdge : currentStartNode.getAllOutEdges()) {
                Node nextNode = currentOutEdge.getTarget();
                if (!visited.contains(nextNode)) {
                    queue.addLast(nextNode);
                }
            }

            LinkedList<Edge> allInEdges = new LinkedList<Edge>();

            for (Edge currentInEdge : currentStartNode.getAllInEdges()) {
                allInEdges.addLast(currentInEdge);
            }

            for (Edge currentEdge : allInEdges) {
                Node nextNode = currentEdge.getSource();
                if (!visited.contains(nextNode)) {

                    // if we want to keep the appearance of the reversed edges,
                    // we have to swap the corresponding arrowtail and arrowhead
                    // attributes.

                    // TODO: Fix this:

                    if (this.edgeAppearance == RootChanger.KEEP_APPEARANCE) {
                        Attribute arrowHead = Util.getAttribute(currentEdge,
                                "graphics.arrowhead");
                        Attribute arrowTail = Util.getAttribute(currentEdge,
                                "graphics.arrowtail");

                        if (arrowHead != null && arrowTail != null) {
                            Attribute arrowHeadCopy = (Attribute) arrowHead
                                    .copy();
                            arrowHead.setValue(arrowTail.getValue());
                            arrowTail.setValue(arrowHeadCopy.getValue());

                        } else
                            throw new IllegalStateException(
                                    "All edges must have arrowhead and arrowtail attributes");

                    }

                    currentEdge.setSource(currentStartNode);
                    currentEdge.setTarget(nextNode);

                    queue.addLast(nextNode);
                }

            }

        }
    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#getAlgorithmParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter seleParam = new SelectionParameter("Selected Node:",
                "To start the algorithm at");
        return new Parameter[] { seleParam, this.appearanceParameter };

    }

    /**
     * @see org.graffiti.plugin.AbstractParametrizable#setAlgorithmParameters(Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        this.selection = ((SelectionParameter) params[0]).getSelection();

        String appearanceString = ((StringSelectionParameter) params[1])
                .getSelectedValue();

        if (appearanceString.equals("KEEP_APPEARANCE")) {
            this.edgeAppearance = RootChanger.KEEP_APPEARANCE;
        } else {
            this.edgeAppearance = RootChanger.CHANGE_APPEARANCE;
        }
    }

    /**
     * Check if there is exactly one node selected.
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {

        if (this.selection.getNodes().size() != 1)
            throw new PreconditionException(
                    "Please select exactly one Node to become the new root.");

        this.newRoot = this.selection.getNodes().get(0);

    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
