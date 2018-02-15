//==============================================================================
//
//   Matrix3Dim.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: Matrix3Dim.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.brandeskoepf;

import java.util.ArrayList;

/**
 * This data structure is designed for the Brandes/Koepf algorithm. Its a 3
 * dimension Matrix
 * 
 * @author Florian Fischer
 */
public class Matrix3Dim<T> {
    // ~ Instance fields
    // ========================================================

    // Andreas G.: Let's replace the whole thing by a flat array and a mapping
    // function (x,y,z) -> index.
    /** The main data container */
    protected ArrayList<ArrayList<ArrayList<T>>> matrix;

    // ~ Constructors
    // ===========================================================

    /**
     * The constructor which initializes i empty lines
     * 
     * @param i
     *            number of lines
     */
    public Matrix3Dim(int i) {
        matrix = new ArrayList<ArrayList<ArrayList<T>>>();

        for (int j = 0; j <= i; j++) {
            matrix.add(new ArrayList<ArrayList<T>>());
        }
    }

    // ~ Methods
    // ================================================================

    /**
     * Returns the element in given line, column and depth
     * 
     * @param line
     *            the line
     * @param col
     *            the colum
     * @param depth
     *            the depth
     * 
     * @return the element
     */
    public T get(int line, int col, int depth) {
        if (line < matrix.size()) {
            if (col < matrix.get(line).size())
                return matrix.get(line).get(col).get(depth);
        }

        return null;
    }

    /**
     * The adder, which cares also, that the matrix is big enough
     * 
     * @param line
     *            the line
     * @param col
     *            the colum
     * @param value
     *            the object
     */
    public void add(int line, int col, T value) {
        // Is the line wide enough?
        if (matrix.get(line).size() <= col) {
            // The needed elements
            int difference = col - matrix.get(line).size();

            // Fill the line
            for (int i = 0; i <= difference; i++) {
                matrix.get(line).add(new ArrayList<T>());
            }
        }

        // Assign the object
        matrix.get(line).get(col).add(value);
    }

    /**
     * Returns the number of elements in the line i
     * 
     * @param i
     *            the line
     * 
     * @return the number of elements in the line i
     */
    public int elementsOfLine(int i) {
        if (i < matrix.size())
            return matrix.get(i).size();
        return 0;
    }

    /**
     * Returns the number of elements in the line i and column j
     * 
     * @param i
     *            the line
     * @param j
     *            the column
     * 
     * @return the number of elements in the line i and column j
     */
    public int elementsOfLineAndColumn(int i, int j) {
        if (i < matrix.size()) {
            if (j < matrix.get(i).size())
                return matrix.get(i).get(j).size();
        }

        return 0;
    }

    /**
     * Returns the number of lines
     * 
     * @return The number of lines
     */
    public int lines() {
        return matrix.size();
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
