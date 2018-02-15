// =============================================================================
//
//   Matrix.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.planarAngleGraph;

/**
 * A <code>Matrix</code> object represents a matrix with double values. A
 * <code>Matrix</code> can be an <code>AngleMatrix</code> or an
 * <code>EdgeMatrix</code>.
 * 
 * @author Mirka Kossak
 */
public abstract class Matrix {
    private int numberOfRows;

    private int numberOfColumns;

    private double[][] values;

    /**
     * Initializes the Matrix.
     * 
     * @param myNumberOfRows
     *            The number of rows.
     * @param myNumberOfColumns
     *            The number of columns.
     * @param init
     *            The value that every entry of the matrix gets as an init
     *            value.
     */
    public void init(int myNumberOfRows, int myNumberOfColumns, int init) {
        this.numberOfRows = myNumberOfRows;
        this.numberOfColumns = myNumberOfColumns;
        values = new double[numberOfRows][numberOfColumns];
        for (int i = 0; i < this.numberOfRows; i++) {
            for (int j = 0; j < this.numberOfColumns; j++) {
                this.setValue(i, j, init);
            }
        }
    }

    /**
     * Sets the value value at the row row and the column column.
     * 
     * @param row
     * @param column
     * @param value
     */
    public void setValue(int row, int column, double value) {
        if (row >= 0 && row < numberOfRows && column >= 0
                && column < numberOfColumns) {
            values[row][column] = value;
        }
    }

    /**
     * Returns the row row of the matrix as a double array.
     * 
     * @param row
     * @return The row row of the matrix as a double array.
     */
    public double[] getRowToDouble(int row) {
        double[] rowToDouble = new double[this.getNumberOfColumns() + 1];
        rowToDouble[0] = 0;
        for (int i = 1; i <= this.getNumberOfColumns(); i++) {
            rowToDouble[i] = this.getValue(row, i - 1);
        }
        return rowToDouble;
    }

    /**
     * Returns the value of the matrix at the row row and the column column.
     * 
     * @param row
     * @param column
     * @return the value of the matrix at the row row and the column column.
     */
    public double getValue(int row, int column) {
        return values[row][column];
    }

    /**
     * Sets the number of rows of the matrix.
     * 
     * @param numberOfRows
     */
    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }

    /**
     * Returns the number of rows of the matrix.
     * 
     * @return the number of rows of the matrix.
     */
    public int getNumberOfRows() {
        return numberOfRows;
    }

    /**
     * Sets the number of columns of the matrix.
     * 
     * @param numberOfColumns
     */
    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    /**
     * Returns he number of columns of the matrix.f
     * 
     * @return The number of columns of the matrix.
     */
    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    /**
     * Returns a string representation of the matrix. Used for debugging only.
     */
    @Override
    public String toString() {
        String res = "";
        for (int i = 0; i < this.getNumberOfRows(); i++) {
            for (int j = 0; j < this.getNumberOfColumns(); j++) {
                res += getValue(i, j) + "\t";
            }
            res += "\n";
        }
        return res;
    }

    public abstract void makeMatrix();

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
