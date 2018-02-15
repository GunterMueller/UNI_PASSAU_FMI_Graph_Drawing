package org.graffiti.plugins.algorithms.springembedderFR;

import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashSet;

/**
 * Grid Square is an square of a grid, where nodes are saved, which are inside
 * the grid square.
 * 
 * @author matzeder
 */
public class GridSquare {

    /**
     * grid quadrat as <code>Rectangle</code>
     */
    Rectangle gridQuadrat;

    /**
     * Nodes within the GridQuadrat
     */
    Collection<FRNode> nodes;

    /**
     * Creates a new GridQuadrat with a grid
     * 
     * @param left
     *            Left border of the grid quadrat
     * @param top
     *            Top border of the grid quadrat
     * @param gridQuadratLength
     *            Width of the grid quadrat
     */
    public GridSquare(int left, int top, int gridQuadratLength) {
        this.gridQuadrat = new Rectangle(left, top, gridQuadratLength,
                gridQuadratLength);
        nodes = new HashSet<FRNode>();
    }

    /**
     * Adds a node to the grid quadrat
     * 
     * @param n
     *            The node, which should be added
     */
    public void addNode(FRNode n) {
        nodes.add(n);
    }

    /**
     * Removes a node of the grid quadrat
     * 
     * @param n
     *            The node, which should be removed
     */
    public void removeNode(FRNode n) {
        nodes.remove(n);
    }

    /**
     * Returns true, if the node is within the grid.
     * 
     * @param n
     * @return if node is inside this GridSquare, then true
     */
    public boolean containsNode(FRNode n) {
        return nodes.contains(n);
    }

    /**
     * For output of a grid square
     */
    @Override
    public String toString() {
        String point = "(" + this.gridQuadrat.x + ", " + this.gridQuadrat.y
                + ")";
        String nrNodes = ", #Knoten im Grid: " + nodes.size();
        String coords = " mit Koordinaten: {";

        for (FRNode node : nodes) {
            double xCoord = node.getXPos();
            double yCoord = node.getYPos();

            coords = coords + "(" + xCoord + ", " + yCoord + ") ";
        }
        coords = coords + "}";
        return point + nrNodes + coords;
    }

    /**
     * Returns the nodes inside this grid square
     * 
     * @return Nodes of this gridSquare
     */
    public Collection<FRNode> getNodes() {
        return nodes;
    }

}
