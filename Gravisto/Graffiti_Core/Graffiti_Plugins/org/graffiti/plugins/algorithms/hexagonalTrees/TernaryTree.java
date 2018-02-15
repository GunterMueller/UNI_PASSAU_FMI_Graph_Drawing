package org.graffiti.plugins.algorithms.hexagonalTrees;

import java.awt.Dimension;

import org.graffiti.graph.Node;

public class TernaryTree implements Comparable<TernaryTree> {

    private Node root;

    private Subtree[] subtrees = new Subtree[3];

    private Dimension dim = new Dimension();

    public TernaryTree(Subtree a, Subtree b, Subtree c) {

        this.subtrees[0] = a;
        this.subtrees[1] = b;
        this.subtrees[2] = c;
    }

    public void setDim(int width, int height) {
        dim.setSize(width, height);
    }

    public Dimension getDim() {
        return this.dim;
    }

    public Subtree getSubtree(int i) {
        if (i > 2 || i < 0) {
            System.err.println("no subtree for this number");
            return null;
        }
        return subtrees[i];
    }

    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return this.root;
    }

    @Override
    public String toString() {
        return ("[" + this.getDim().getWidth() + ", "
                + this.getDim().getHeight() + "] ");
    }

    public int compareTo(TernaryTree objectToCompare) {

        double x1 = this.getDim().getWidth();
        double y1 = this.getDim().getHeight();
        double x2 = objectToCompare.getDim().getWidth();
        double y2 = objectToCompare.getDim().getHeight();

        if (x1 < x2)
            return -1;
        else if (x1 > x2)
            return 1;
        else {
            if (y1 > y2)
                return -1;
            else if (y1 < y2)
                return 1;
            else
                return 0;
        }
    }

    /**
     * Returns true if t1 is dominating t2, else false.
     */
    public static boolean dominating(TernaryTree t1, TernaryTree t2) {

        if (t1.getDim().getWidth() >= t2.getDim().getWidth()
                && t1.getDim().getHeight() >= t2.getDim().getHeight())
            return true;
        return false;

    }

}
