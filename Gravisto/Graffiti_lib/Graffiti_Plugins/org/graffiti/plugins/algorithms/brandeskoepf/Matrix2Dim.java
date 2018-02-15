//==============================================================================
//
//   Matrix2Dim.java
//
//   Copyright (c) 2001-2003 Graffiti Team, Uni Passau
//
//==============================================================================
// $Id: Matrix2Dim.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.brandeskoepf;

import java.util.ArrayList;

/**
 * This data structure is designed for the Brandes/Koepf algorithm. Its a 2
 * dimension Matrix
 * 
 * @author Florian Fischer
 */
public class Matrix2Dim<T> {
    // ~ Instance fields
    // ========================================================

    // Andreas G.: Let's replace the whole thing by a flat array and a mapping
    // function (x,y,z) -> index.
    /** The main data container */
    private ArrayList<ArrayList<T>> matrix;

    // ~ Constructors
    // ===========================================================

    /**
     * The constructor which initializes i empty lines
     * 
     * @param i
     *            The number of lines
     */
    public Matrix2Dim(int i) {
        matrix = new ArrayList<ArrayList<T>>();

        for (int j = 0; j <= i; j++) {
            matrix.add(new ArrayList<T>());
        }
    }

    // ~ Methods
    // ================================================================

    /**
     * The setter for objects, which cares also, that the matrix is big enough
     * 
     * @param line
     *            the line
     * @param col
     *            the column
     * @param o
     *            the object
     */
    public void set(int line, int col, T o) {
        // Is the line wide enough?
        if (matrix.get(line).size() <= col) {
            // The needed elements
            int difference = col - matrix.get(line).size();

            // Fill the line
            for (int i = 0; i <= difference; i++) {
                matrix.get(line).add(null);
            }
        }

        // Assign the object
        matrix.get(line).set(col, o);
    }

    /**
     * Returns the element in given line and column
     * 
     * @param line
     *            the line
     * @param col
     *            the column
     * 
     * @return The object as a Double.
     */
    public T get(int line, int col) {
        return matrix.get(line).get(col);
    }

    /**
     * The constructor which initializes i empty lines
     * 
     * @param toCopy
     *            The Matrix2Dim which should be copied
     */
    public void cloneMatrix(Matrix2Dim<T> toCopy) {
        // The matrix has to be constructed new
        matrix = new ArrayList<ArrayList<T>>();

        // For every line of the matrix, which should be copyed
        for (int i = 0; i < toCopy.lines(); i++) {
            // Add a new line
            matrix.add(new ArrayList<T>());

            // Fill the lines with the objects of the incoming matrix
            for (int j = 0; j < toCopy.elementsOfLine(i); j++) {
                matrix.get(i).add(toCopy.get(i, j));
            }
        }
    }

    /**
     * The constructor which initializes i empty lines
     * 
     * @param toCopy
     *            The Matrix2Dim which should be copied
     */
    public void assignMatrixSize(Matrix2Dim<?> toCopy) {
        // The matrix has to be constructed new
        matrix = new ArrayList<ArrayList<T>>();

        // For every line of the matrix, which should be copyed
        for (int i = 0; i < toCopy.lines(); i++) {
            // Add a new line
            matrix.add(new ArrayList<T>());

            // Fill the lines with the objects of the incoming matrix
            for (int j = 0; j < toCopy.elementsOfLine(i); j++) {
                matrix.get(i).add(null);
            }
        }
    }

    /**
     * Returns the number of elements in the line i
     * 
     * @param i
     *            The number of the line
     * 
     * @return The number of object in this line
     */
    public int elementsOfLine(int i) {
        return matrix.get(i).size();
    }

    /**
     * Returns the number of lines
     * 
     * @return the number of lines
     */
    public int lines() {
        return matrix.size();
    }
}

// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------
