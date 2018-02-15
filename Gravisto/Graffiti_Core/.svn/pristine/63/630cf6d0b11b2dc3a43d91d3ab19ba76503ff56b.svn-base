package org.graffiti.plugins.algorithms.brandeskoepf;

import java.util.ArrayList;

// Andreas G.: I created this class out in the purification process, but the
// method originates from the specified author.
/**
 * {@code Matrix3Dim} for {@code double} values.
 * 
 * @author Florian Fischer
 */
public class DoubleMatrix3Dim extends Matrix3Dim<Double> {
    /**
     * The constructor, which initializes i empty lines.
     * 
     * @param i
     *            number of lines
     */
    public DoubleMatrix3Dim(int i) {
        super(i);
    }

    /**
     * The setter for Double objects, which cares also, that the matrix is big
     * enough
     * 
     * @param line
     *            the line
     * @param col
     *            the column
     * @param depth
     *            the depth
     * @param value
     *            The double object
     */
    public void set(int line, int col, int depth, Double value) {
        // Is the line wide enough?
        if (matrix.get(line).size() <= col) {
            // The needed elements
            int difference = col - matrix.get(line).size();

            // Fill the line
            for (int i = 0; i <= difference; i++) {
                matrix.get(line).add(new ArrayList<Double>());
            }
        }

        // Is the line and column deep enough?
        if (matrix.get(line).get(col).size() <= depth) {
            // The needed elements
            int difference = depth - (matrix.get(line).get(col)).size();

            // Fill the line
            for (int i = 0; i <= difference; i++) {
                matrix.get(line).get(col).add(0.0);
            }
        }

        // Assign the object
        matrix.get(line).get(col).set(depth, value);
    }
}
