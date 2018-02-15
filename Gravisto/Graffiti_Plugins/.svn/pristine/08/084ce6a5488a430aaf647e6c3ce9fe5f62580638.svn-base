package org.graffiti.plugins.algorithms.springembedderFR;

import java.awt.Point;
import java.util.HashMap;

/**
 * The grid for the fast calculation of the nearest nodes in drawing of the
 * graph.
 * 
 * @author matzeder
 * 
 */
public class Grid {

    /**
     * Length of a gridQuadrat
     */
    private int gridLength;

    /**
     * speichert linken oberen Point als key, und ein Grid als Value
     */
    private HashMap<Point, GridSquare> gridSquares;

    /**
     * Creates a grid, with a gridLength
     * 
     * @param length
     */
    public Grid(int length, FRGraph graph) {

        // height and width of an grid square
        this.gridLength = length;

        // here all grid squares are saved
        this.gridSquares = new HashMap<Point, GridSquare>();

        // initialize the grid, all nodes are assigned to
        initGrid(gridLength, graph, gridSquares);
    }

    /**
     * Initializes the grid.
     * 
     * @param gridLength
     *            Length of a grid square
     * @param graph
     *            The given graph
     * @param gridSquares
     *            The HashMap with the grid squares
     */
    private void initGrid(int gridLength, FRGraph graph,
            HashMap<Point, GridSquare> gridSquares) {

        // save every node in the grid square it has to be assigned to
        for (FRNode node : graph.getFRNodes()) {
            // node position
            double x = node.getXPos();
            double y = node.getYPos();

            // determine the grid square
            int leftGridBorder;
            int topGridBorder;

            // calculate the left grid square border
            if (x < 0) {
                // a int value for the grid border
                leftGridBorder = ((int) x - (int) (x % this.gridLength))
                        - this.gridLength;
            } else {
                leftGridBorder = ((int) x - (int) (x % this.gridLength));
            }

            // calculate the top grid square border
            if (y < 0) {
                topGridBorder = ((int) y - (int) (y % this.gridLength))
                        - this.gridLength;
            } else {
                topGridBorder = ((int) y - (int) (y % this.gridLength));
            }

            // left top point defines the grid square
            Point leftTopPoint = new Point(leftGridBorder, topGridBorder);

            // if gridSquares doesn't contain a grid square at point
            // leftTopPoint, then create a new grid square with the node
            if (!gridSquares.containsKey(leftTopPoint)) {

                // the new grid square
                GridSquare tmpGridSquare = new GridSquare(leftGridBorder,
                        topGridBorder, gridLength);

                // add the node to the new grid square
                tmpGridSquare.addNode(node);

                // add the new grid square to HashMap gridsquares
                gridSquares.put(leftTopPoint, tmpGridSquare);
            }
            // GridSquare des Knotens existiert bereits
            else {
                // the grid square, where the node is in
                GridSquare tmpGridSquare = gridSquares.get(leftTopPoint);

                // add the node to the grid square
                tmpGridSquare.addNode(node);
            }
        } // end while (nodesIt)
    }

    /**
     * Returns the 8 neighbours and the grid square of a grid and the grid
     * square the node is in.
     */
    public HashMap<Point, GridSquare> getNeighbourGrids(FRNode node) {

        // HashMap where all "neighbours" are saved
        HashMap<Point, GridSquare> neighbours = new HashMap<Point, GridSquare>();

        // the (left top) point of the gridSquare, where the node is in
        Point gridSquarePoint = calculateGridSquareOfNode(node);

        int squareX = (int) gridSquarePoint.getX();
        int squareY = (int) gridSquarePoint.getY();

        // upper grid square
        Point upperPoint = new Point(squareX, squareY - gridLength);

        // left upper grid square
        Point leftUpperPoint = new Point(squareX - gridLength, squareY
                - gridLength);

        // left grid square
        Point leftPoint = new Point(squareX - gridLength, squareY);

        // left down grid square
        Point leftLowerPoint = new Point(squareX - gridLength, squareY
                + gridLength);

        // down grid square
        Point lowerPoint = new Point(squareX, squareY + gridLength);

        // right down grid square
        Point rightLowerPoint = new Point(squareX + gridLength, squareY
                + gridLength);

        // right grid square
        Point rightPoint = new Point(squareX + gridLength, squareY);

        // right upper grid square
        Point rightUpperPoint = new Point(squareX + gridLength, squareY
                - gridLength);

        // add the grid squares, if exist
        if (gridSquares.get(gridSquarePoint) != null) {
            neighbours.put(gridSquarePoint, gridSquares.get(gridSquarePoint));
        }

        if (gridSquares.get(leftUpperPoint) != null) {
            neighbours.put(leftUpperPoint, gridSquares.get(leftUpperPoint));
        }

        if (gridSquares.get(leftPoint) != null) {
            neighbours.put(leftPoint, gridSquares.get(leftPoint));
        }

        if (gridSquares.get(leftLowerPoint) != null) {
            neighbours.put(leftLowerPoint, gridSquares.get(leftLowerPoint));
        }

        if (gridSquares.get(lowerPoint) != null) {
            neighbours.put(lowerPoint, gridSquares.get(lowerPoint));
        }

        if (gridSquares.get(rightUpperPoint) != null) {
            neighbours.put(rightUpperPoint, gridSquares.get(rightUpperPoint));
        }

        if (gridSquares.get(rightLowerPoint) != null) {
            neighbours.put(rightLowerPoint, gridSquares.get(rightLowerPoint));
        }

        if (gridSquares.get(rightPoint) != null) {
            neighbours.put(rightPoint, gridSquares.get(rightPoint));
        }
        if (gridSquares.get(upperPoint) != null) {
            neighbours.put(upperPoint, gridSquares.get(upperPoint));
        }

        return neighbours;

    }

    /**
     * Returns the point of the gridsquare (key of the gridSquares HashMap)
     * 
     * @param node
     *            The node inside the searched grid square
     * @return Point The point of the grid square to identify.
     */
    private Point calculateGridSquareOfNode(FRNode node) {

        double x = node.getXPos();
        double y = node.getYPos();

        return calculateGridSquareOfPoint(x, y);

    }

    /**
     * Returns the point of the gridsquare (key of the gridSquares HashMap)
     * 
     * @return Point
     */
    private Point calculateGridSquareOfPoint(double x, double y) {

        Point p;

        int left;
        int top;

        // left of the grid square
        if (x < 0) {

            left = ((int) x - (int) (x % this.gridLength)) - this.gridLength;

        } else {

            left = ((int) x - (int) (x % this.gridLength));

        }

        // top of the grid square
        if (y < 0) {

            top = ((int) y - (int) (y % this.gridLength)) - this.gridLength;

        } else {

            top = ((int) y - (int) (y % this.gridLength));

        }
        p = new Point(left, top);

        return p;
    }

    /**
     * Returns the length of the grid
     * 
     * @return gridLength
     */
    public int getGridLength() {
        return this.gridLength;
    }

    /**
     * Returns the number of gridSquares in this Grid
     * 
     * @return number of gridSquares
     */
    public int getNrOfGridSquares() {

        return gridSquares.size();

    }

    /**
     * Returns the grid square where the node is in
     * 
     * @param node
     * @return GridSquare, where node is in
     */
    public GridSquare getGridSquare(FRNode node) {

        Point pointOfGridSquare = this.calculateGridSquareOfNode(node);

        if (gridSquares.containsKey(pointOfGridSquare)) {

            GridSquare gridSquare = gridSquares.get(pointOfGridSquare);

            return gridSquare;

        } else
            return null;
    }

    /**
     * Methode die nach dem Verschieben eines Knotens aufgerufen wird, um dann
     * den Knoten auch dem richtigen GridSquare wieder zuzuordnen
     * 
     * @param node
     */
    public void movedNodeInGrid(FRNode node, double x, double y) {

        Point oldGridSquareKey = calculateGridSquareOfPoint(x, y);
        GridSquare oldGridSquare = gridSquares.get(oldGridSquareKey);

        Point newGridSquareKey = calculateGridSquareOfNode(node);

        if (oldGridSquare == null) {

            System.out.println("ERROR");
            return;
        }

        // deletes the node in the old grid
        oldGridSquare.removeNode(node);

        // if new grid square does not exist, then create one
        if (gridSquares.get(newGridSquareKey) == null) {

            // the new grid square
            GridSquare newGridSquare = new GridSquare(newGridSquareKey.x,
                    newGridSquareKey.x, gridLength);
            newGridSquare.addNode(node);

            // adds the new grid square to the HashMap
            gridSquares.put(newGridSquareKey, newGridSquare);
        }
        // grid square exists
        else {
            GridSquare existingGridSquare = gridSquares.get(newGridSquareKey);

            existingGridSquare.addNode(node);
        }
    }

    /**
     * Output of the grid, with its grid squares
     */
    @Override
    public String toString() {
        String s = "{ ";

        for (GridSquare gridSquare : gridSquares.values()) {
            s = s + "\n - " + gridSquare;
        }
        s = s + " }";
        return s;
    }
}
