package org.graffiti.plugins.algorithms.hexagonalTrees;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;

/**
 * The rectangle dimension of a subtree
 */
public class Subtree implements Comparable<Subtree> {

    /**
     * sets the coordinate of the root-node of the subtree
     */
    private Point root;

    /**
     * represents the height and the width of a subtree
     */
    private Dimension dim;

    /**
     * coloring of the subtree (for a better readability)
     */
    private Color color;

    /**
     * Constructor for the class (with color)
     */
    public Subtree(Point root, int width, int height, Color color) {
        this.root = root;
        this.dim = new Dimension(width, height);
        this.color = color;
    }

    /**
     * Constructor for the class (with color)
     */
    public Subtree(int width, int height, Color color) {
        this.root = new Point(0, 0);
        this.dim = new Dimension(width, height);
        this.color = color;
    }

    /**
     * Constructor for the class (without color)
     */
    public Subtree(Point root, int x, int y) {
        this.root = root;
        this.dim = new Dimension(0, 0);
    }

    /**
     * Sets the root-coordinates onto the given coordinate values
     */
    public void moveRoot(int x, int y) {
        root.setLocation(x, y);
    }

    public Point getRoot() {
        return root;
    }

    public void setRoot(Point root) {
        this.root = root;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Dimension getDim() {
        return dim;
    }

    public void setDim(Dimension dim) {
        this.dim = dim;
    }

    public int getWidth() {
        return dim.width;
    }

    public int getHeight() {
        return dim.height;
    }

    /**
     * this object is smaller than objectToCompare, if width is smaller than
     * width of objectToCompare, and if equal then if height is bigger than
     * height of objectToCompare
     */
    public int compareTo(Subtree objectToCompare) {

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

    @Override
    public String toString() {
        return ("[" + this.getDim().getWidth() + ", "
                + this.getDim().getHeight() + "]");
    }
}
