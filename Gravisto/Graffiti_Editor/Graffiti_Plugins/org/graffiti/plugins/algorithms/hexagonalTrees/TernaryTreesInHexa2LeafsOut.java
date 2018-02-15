package org.graffiti.plugins.algorithms.hexagonalTrees;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;

public class TernaryTreesInHexa2LeafsOut extends TreeInHexa2 {

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

        drawTree(root, 1, 1);

        this.graph.getListenerManager().transactionFinished(this);
    }

    private void drawTree(Node placed, int level, int side) {
        if (placed.equals(root)) {
            this.placeRoot();
        }
        Node[] sons = new Node[0];
        sons = placed.getOutNeighbors().toArray(sons);

        if (sons.length == 3) {
            if (side == 2) {
                this
                        .placeBelowOf(sons[0], Math.pow(3, (depth - level)),
                                placed);
                drawTree(sons[0], level + 1, side);
                this.placeRightBelowOf(sons[1], Math.pow(3, (depth - level)),
                        placed);
                drawTree(sons[1], level + 1, side);
                this.placeDoubleRightBelowOf(sons[2], Math.pow(3,
                        (depth - level)), placed);
                drawTree(sons[2], level + 1, side);
            } else if (side == 1) {
                this
                        .placeRightOf(sons[0], Math.pow(3, (depth - level)),
                                placed);
                drawTree(sons[0], level + 1, 0);
                this.placeRightBelowOf(sons[1], Math.pow(3, (depth - level)),
                        placed);
                drawTree(sons[1], level + 1, 1);
                this
                        .placeBelowOf(sons[2], Math.pow(3, (depth - level)),
                                placed);
                drawTree(sons[2], level + 1, 2);
            } else if (side == 0) {
                this
                        .placeRightOf(sons[0], Math.pow(3, (depth - level)),
                                placed);
                drawTree(sons[0], level + 1, side);
                this.placeRightBelowOf(sons[1], Math.pow(3, (depth - level)),
                        placed);
                drawTree(sons[1], level + 1, side);
                this.placeRightDoubleBelowOf(sons[2], Math.pow(3,
                        (depth - level)), placed);
                drawTree(sons[2], level + 1, side);
            }
        } else if (sons.length == 2) {
            if (side == 2) {
                this
                        .placeBelowOf(sons[0], Math.pow(3, (depth - level)),
                                placed);
                drawTree(sons[0], level + 1, side);
                this.placeDoubleRightBelowOf(sons[1], Math.pow(3,
                        (depth - level)), placed);
                drawTree(sons[1], level + 1, side);
            } else if (side == 1) {
                this
                        .placeRightOf(sons[0], Math.pow(3, (depth - level)),
                                placed);
                drawTree(sons[0], level + 1, 0);
                this
                        .placeBelowOf(sons[1], Math.pow(3, (depth - level)),
                                placed);
                drawTree(sons[1], level + 1, 2);
            } else if (side == 0) {
                this
                        .placeRightOf(sons[0], Math.pow(3, (depth - level)),
                                placed);
                drawTree(sons[0], level + 1, side);
                this.placeRightDoubleBelowOf(sons[1], Math.pow(3,
                        (depth - level)), placed);
                drawTree(sons[1], level + 1, side);
            }
        } else if (sons.length == 1) {
            if (side == 2) {
                this.placeRightBelowOf(sons[0], Math.pow(3, (depth - level)),
                        placed);
                drawTree(sons[0], level + 1, side);
            } else if (side == 1) {
                this.placeRightBelowOf(sons[0], Math.pow(3, (depth - level)),
                        placed);
                drawTree(sons[0], level + 1, 1);
            } else if (side == 0) {
                this.placeRightBelowOf(sons[0], Math.pow(3, (depth - level)),
                        placed);
                drawTree(sons[0], level + 1, side);
            }
        }
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "TernaryTreeOnHexagonal2LeafsOut";
    }
}
