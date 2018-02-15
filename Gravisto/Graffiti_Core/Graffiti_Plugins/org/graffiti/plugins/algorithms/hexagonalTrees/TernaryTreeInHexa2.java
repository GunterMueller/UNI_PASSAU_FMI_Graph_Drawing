// =============================================================================
//
//   PentaTreeGrid.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.hexagonalTrees;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;

/**
 * @author Tom
 * @version $Revision$ $Date$
 */
public class TernaryTreeInHexa2 extends TreeInHexa2 {

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        this.root = GraphChecker.checkTree(this.graph, 3);
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        if (root == null)
            throw new RuntimeException("Must call method \"check\" before "
                    + " calling \"execute\".");

        this.graph.getListenerManager().transactionStarted(this);
        removeBends(root);

        depth = calculateTreeDepth(root, 1) - 1;

        drawTree(root, 1);

        this.graph.getListenerManager().transactionFinished(this);
    }

    private void drawTree(Node placed, int level) {
        if (placed.equals(root)) {
            this.placeRoot();
        }
        Node[] sons = new Node[0];
        sons = placed.getOutNeighbors().toArray(sons);

        if (sons.length == 3) {
            this.placeBelowOf(sons[0], Math.pow(2, (depth - level)), placed);
            drawTree(sons[0], level + 1);
            this.placeRightBelowOf(sons[1], Math.pow(2, (depth - level)),
                    placed);
            drawTree(sons[1], level + 1);
            this.placeRightOf(sons[2], Math.pow(2, (depth - level)), placed);
            drawTree(sons[2], level + 1);
        } else if (sons.length == 2) {
            this.placeBelowOf(sons[0], Math.pow(2, (depth - level)), placed);
            drawTree(sons[0], level + 1);
            this.placeRightOf(sons[1], Math.pow(2, (depth - level)), placed);
            drawTree(sons[1], level + 1);
        } else if (sons.length == 1) {
            this.placeRightBelowOf(sons[0], Math.pow(2, (depth - level)),
                    placed);
            drawTree(sons[0], level + 1);
        }
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "TernaryTreeOnHexagonal2";
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------

