package org.graffiti.plugins.algorithms.hexagonalTrees;

import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;

public class FournaryTreesInHexa2 extends TreeInHexa2 {

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        this.root = GraphChecker.checkTree(this.graph, 4);
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

        depth = calculateTreeDepth(root, 1);
        placeRoot();
        this.drawTree(root, 2, 1);

        this.graph.getListenerManager().transactionFinished(this);
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "FournaryTreeInHexa2";
    }

    private void drawTree(Node placed, int level, int grandfather) {
        Node[] sons = new Node[0];
        sons = placed.getOutNeighbors().toArray(sons);
        if (sons.length == 1) {
            placeAtPosition(sons[0], Math.pow(3, (depth - level)), placed,
                    grandfather);
            drawTree(sons[0], level + 1, grandfather);
        } else if (sons.length == 2) {
            placeAtPosition(sons[0], Math.pow(3, (depth - level)), placed,
                    (grandfather + 5) % 6);
            drawTree(sons[0], level + 1, (grandfather + 5) % 6);
            placeAtPosition(sons[1], Math.pow(3, (depth - level)), placed,
                    (grandfather + 1) % 6);
            drawTree(sons[1], level + 1, (grandfather + 1) % 6);
        } else if (sons.length == 3) {
            placeAtPosition(sons[0], Math.pow(3, (depth - level)), placed,
                    (grandfather + 5) % 6);
            drawTree(sons[0], level + 1, (grandfather + 5) % 6);
            placeAtPosition(sons[1], Math.pow(3, (depth - level)), placed,
                    grandfather % 6);
            drawTree(sons[1], level + 1, grandfather % 6);
            placeAtPosition(sons[2], Math.pow(3, (depth - level)), placed,
                    (grandfather + 1) % 6);
            drawTree(sons[2], level + 1, (grandfather + 1) % 6);
        } else if (sons.length == 4) {
            placeAtPosition(sons[0], Math.pow(3, (depth - level)), placed,
                    (grandfather + 4) % 6);
            drawTree(sons[0], level + 1, (grandfather + 4) % 6);
            placeAtPosition(sons[1], Math.pow(3, (depth - level)), placed,
                    (grandfather + 5) % 6);
            drawTree(sons[1], level + 1, (grandfather + 5) % 6);
            placeAtPosition(sons[2], Math.pow(3, (depth - level)), placed,
                    (grandfather + 1) % 6);
            drawTree(sons[2], level + 1, (grandfather + 1) % 6);
            placeAtPosition(sons[3], Math.pow(3, (depth - level)), placed,
                    (grandfather + 2) % 6);
            drawTree(sons[3], level + 1, (grandfather + 2) % 6);
        }
    }
}
