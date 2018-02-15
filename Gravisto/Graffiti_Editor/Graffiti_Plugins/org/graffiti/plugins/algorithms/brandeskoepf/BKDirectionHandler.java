//==============================================================================
//
//   BKDirectionHandler.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: BKDirectionHandler.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.brandeskoepf;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * This class handles the neccessary functions for the different directions of
 * the Brandes/Koepf algorithm
 * 
 * @author Florian Fischer
 */
public class BKDirectionHandler {
    // ~ Methods
    // ================================================================

    /**
     * Sets the barrier variable for the vertical alignment
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param order
     *            The barrier variable
     * 
     * @return The in- or decreased barrier variable
     */
    int setBarrierVariable(int direction, int order) {
        if ((direction == 0) || (direction == 2))
            return order + 1;
        else
            return order - 1;
    }

    /**
     * This function returns true when he specified edge is marked as a typ1
     * conflict edge
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param edgesToUpperNeighbours
     *            The incomming edges of the upper neighbours
     * @param edgesToLowerNeighbours
     *            The outgoing edges of the lower neighbours
     * @param line
     *            the line of the node
     * @param col
     *            the column of the node
     * @param m
     *            the column of the last aligned node
     * 
     * @return true if edge is marked, false else
     */
    boolean isEdgeMarked(int direction,
            Matrix3Dim<Edge> edgesToUpperNeighbours,
            Matrix3Dim<Edge> edgesToLowerNeighbours, int line, int col, int m) {
        if ((direction == 0) || (direction == 1))
            return GraffitiGraph.getEdgeIsMarked(edgesToUpperNeighbours.get(
                    line, col, m));
        else
            return GraffitiGraph.getEdgeIsMarked(edgesToLowerNeighbours.get(
                    line, col, m));
    }

    /**
     * This function controls the finding according to the correct horizontal
     * neighbour
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param level
     *            The Matrix2Dim with all the nodes
     * @param n
     *            The node form which the neighbour should be found
     * 
     * @return The neighbour node
     */
    Node getNeighbourHorizontal(int direction, Matrix2Dim<Node> level, Node n) {
        // From left to right is the neighbour on the left side
        if ((direction == 0) || (direction == 2))
            return level.get(GraffitiGraph.getNodeLevel(n), GraffitiGraph
                    .getNodeOrder(n) - 1);
        else
            return level.get(GraffitiGraph.getNodeLevel(n), GraffitiGraph
                    .getNodeOrder(n) + 1);
    }

    /**
     * This function returns the upper or lower neighbour, denpending on the
     * direction
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param upperNeighbours
     *            The Matrix3Dim with the upper neighbours
     * @param lowerNeighbours
     *            The Matrix3Dim with the lower neighbour
     * @param line
     *            The line of the node
     * @param col
     *            The column of the node
     * @param m
     *            The order number of the upper or lower neighbour
     * 
     * @return The neighbour
     */
    Node getNeighbourVertical(int direction, Matrix3Dim<Node> upperNeighbours,
            Matrix3Dim<Node> lowerNeighbours, int line, int col, int m) {
        if ((direction == 0) || (direction == 1))
            return upperNeighbours.get(line, col, m);
        else
            return lowerNeighbours.get(line, col, m);
    }

    /**
     * This function returns the number of upper or lower neighbours, denpending
     * on the direction
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param upperNeighbours
     *            The Matrix3Dim with the upper neighbours
     * @param lowerNeighbours
     *            The Matrix3Dim with the lower neighbour
     * @param line
     *            The line of the node
     * @param col
     *            The column of the node
     * 
     * @return The number of upper or lower neighbours
     */
    int getNumberOfNeighbours(int direction, Matrix3Dim<Node> upperNeighbours,
            Matrix3Dim<Node> lowerNeighbours, int line, int col) {
        if ((direction == 0) || (direction == 1))
            return upperNeighbours.elementsOfLineAndColumn(line, col);
        else
            return lowerNeighbours.elementsOfLineAndColumn(line, col);
    }

    /*
     * Everything for the vertical alignment ----END
     */
    /*
     * Everything for the horizontal compaction ----BEGIN
     */

    /**
     * This function returns true if the node is a candidate for a sink
     * 
     * @param direction
     *            The direction
     * @param level
     *            The Matrix2Dim with all the nodes
     * @param candidateOrder
     *            The order number of the candidate
     * @param candidateLevel
     *            The level number of the candidate
     * 
     * @return true if the node is a candidate, false else
     */
    boolean isSinkCandidate(int direction, Matrix2Dim<Node> level,
            int candidateOrder, int candidateLevel) {
        // From left to right is a sink candidate a node with order number 0
        if ((direction == 0) || (direction == 2))
            return candidateOrder == 0;
        else
            return candidateOrder == (level.elementsOfLine(candidateLevel) - 1);
    }

    /**
     * This function compares the order numbers of the node with the barrier
     * variable
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param order
     *            The order number of the node
     * @param barrier
     *            The barrier variable
     * 
     * @return Returns true if the comparison is valid, depending on the
     *         direction
     */
    boolean compareBarrierVariable(int direction, int order, int barrier) {
        if ((direction == 0) || (direction == 2))
            return barrier <= order;
        else
            return barrier >= order;
    }

    /*
     * Everything for the horizontal direction ----END
     */
    /*
     * Everything for the vertical alignment ----BEGIN
     */

    /**
     * This function compares the barrier variable with its initial value
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param level
     *            The Matrix2Dim with all the nodes
     * @param counter
     *            The value
     * 
     * @return The result of the comparison
     */
    boolean compareForInitialiseBarrierVariable(int direction,
            Matrix2Dim<Node> level, int counter) {
        // don't assign a value to the barrier variable in the first level -- no
        // upper neighbours!
        if ((direction == 0) || (direction == 1))
            return counter > 0;
        else
            return counter < (level.lines() - 1);
    }

    /**
     * The function for the 'for' loop comparison
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param level
     *            The Matrix2Dim with all the nodes
     * @param counter
     *            The value
     * @param line
     *            The actual line in the level matrix
     * 
     * @return The result of the comparison
     */
    boolean horizontalDirectionCompare(int direction, Matrix2Dim<Node> level,
            int counter, int line) {
        if ((direction == 0) || (direction == 2))
            return counter < level.elementsOfLine(line);
        else
            return counter >= 0;
    }

    /**
     * The function for in- or decreasing the control variable of a 'for' loop
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param counter
     *            The value to be in- or decreased
     * 
     * @return The in- or decreased value
     */
    int horizontalDirectionGoThrough(int direction, int counter) {
        if ((direction == 0) || (direction == 2))
            return counter + 1;
        else
            return counter - 1;
    }

    /*
     * Everything for the vertical direction ----END
     */
    /*
     * Everything for the horizontal direction ----BEGIN
     */

    /**
     * The function for the initial values of the control variable of the 'for'
     * loops
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param level
     *            The Matrix2Dim with all the nodes
     * @param line
     *            The actual line in the level matrix
     * 
     * @return The initial value
     */
    int horizontalDirectionInitial(int direction, Matrix2Dim<Node> level,
            int line) {
        if ((direction == 0) || (direction == 2))
            return 0;
        else
            return level.elementsOfLine(line) - 1;
    }

    /**
     * The function for the comparison of the control variable of the 'for'
     * loops with the number of upper or lower neighbours
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param numberOfNeighbours
     *            The number of upper or lower neighbours, depending on the
     *            direction
     * @param counter
     *            The control variable
     * 
     * @return The result of the comparison
     */
    boolean horizontalDirectionNeigboursCompare(int direction,
            int numberOfNeighbours, int counter) {
        if ((direction == 0) || (direction == 2))
            return counter <= (numberOfNeighbours / 2);
        else
            return counter >= ((numberOfNeighbours / 2) - 1);
    }

    /**
     * The function for in- or decreasing the control variable of a 'for' loop
     * with upper or lower neighbours
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param counter
     *            The control variable
     * 
     * @return The in- or decreased value
     */
    int horizontalDirectionNeigboursGoThrough(int direction, int counter) {
        if ((direction == 0) || (direction == 2))
            return counter + 1;
        else
            return counter - 1;
    }

    /**
     * The function which returns the initial value for the control variable of
     * a 'for' loop with upper or lower neighbours
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param numberOfNeighbours
     *            The number of upper or lower neighbours, depending on the
     *            direction
     * 
     * @return The initial value
     */
    int horizontalDirectionNeigboursInitial(int direction,
            int numberOfNeighbours) {
        if ((direction == 0) || (direction == 2))
            return (numberOfNeighbours / 2) - 1;
        else
            return (numberOfNeighbours / 2);
    }

    /**
     * This function returns the initial value for the barrier varriable,
     * depending on the direction
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param level
     *            The Matrix2Dim with all the nodes
     * @param line
     *            The actual line in the level matrix
     * 
     * @return The initail value
     */
    int initialiseBarrierVariable(int direction, Matrix2Dim<Node> level,
            int line) {
        // l->r always 0 is the first order number of the nodes one level up or
        // down
        if ((direction == 0) || (direction == 2))
            return 0;
        else {
            // t->b we search nodes one level up
            if (direction == 1)
                return level.elementsOfLine(line - 1) - 1;
            else
                return level.elementsOfLine(line + 1) - 1;
        }
    }

    /**
     * This function realises the min or max function for double values,
     * depending on the direction. Is the X-coordinate of the nodes initialised
     * with 0 then this function has to realise the max function
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param val1
     *            double value 1
     * @param val2
     *            double value2
     * 
     * @return The min or max value, depending on the direction
     */
    double maxMin(int direction, double val1, double val2) {
        // From left to right return the maximum
        // if(direction == 0 || direction == 2){
        return Math.max(val1, val2);

        // }
        // From right to left return the minimum
        // else{
        // return Math.min(val1,val2);
        // }
    }

    /**
     * This function realises the min or max function for double values,
     * depending on the direction. Is the X-coordinate of the nodes initialised
     * with 0 then this function has to realise the min function
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param val1
     *            double value 1
     * @param val2
     *            double value2
     * 
     * @return The min or max value, depending on the direction
     */
    double minMax(int direction, double val1, double val2) {
        // From left to right return the minimum
        // if(direction == 0 || direction == 2){
        return Math.min(val1, val2);

        // }
        // From right to left return the maximum
        // else{
        // return Math.max(val1,val2);
        // }
    }

    /**
     * This function realises the comparison for the veritical direction through
     * the graph
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param level
     *            The Matrix2Dim with all the nodes
     * @param counter
     *            The value to be compared
     * 
     * @return The result of the comparison
     */
    boolean verticalDirectionCompare(int direction, Matrix2Dim<Node> level,
            int counter) {
        if ((direction == 0) || (direction == 1))
            return counter < level.lines();
        else
            return counter >= 0;
    }

    /**
     * This function realises the iteration of the control variable
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param counter
     *            The control variable
     * 
     * @return The in- or decreased control variable
     */
    int verticalDirectionGoThrough(int direction, int counter) {
        if ((direction == 0) || (direction == 1))
            return counter + 1;
        else
            return counter - 1;
    }

    /*
     * Everything for the vertical direction ----BEGIN
     */

    /**
     * This function returns the initial value for the vertical direction
     * 
     * @param direction
     *            the direction 0=top->down,left->right;
     *            1=top->down,right->left; 2=botton->up,left->right;
     *            3=botton->up,right->left;
     * @param level
     *            The Matrix2Dim with all the nodes
     * 
     * @return the initial value
     */
    int verticalDirectionInitial(int direction, Matrix2Dim<Node> level) {
        if ((direction == 0) || (direction == 1))
            return 0;
        else
            return level.lines() - 1;
    }

    /*
     * Everything for the horizontal compaction ----END
     */
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
