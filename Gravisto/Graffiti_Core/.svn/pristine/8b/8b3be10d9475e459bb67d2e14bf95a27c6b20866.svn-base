// =============================================================================
//
//   PSTricksExporter.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.ios.exporters.pstricks;

import java.awt.geom.PathIterator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.graffiti.graph.Graph;

/**
 * @author lessenic
 * @version $Revision$ $Date$
 */
public abstract class PSTricksExporter {

    /**
     * Default accuracy used for coordinates
     */
    protected static final int COORDINATES_ACCURACY = 4;

    protected static final double DEFAULT_EDGE_LINEWIDTH = 0.1;

    protected PrintStream stream;

    /**
     * Generates a PSTricks path from a Java path
     * 
     * @param stream
     *            output stream to write to
     * @param pi
     *            PathIterator
     */
    protected static void describeCurrentSegment(PrintStream stream,
            PathIterator pi) {
        double[] coordinates = new double[6];

        int type = pi.currentSegment(coordinates);

        for (int i = 0; i < coordinates.length; i++) {
            coordinates[i] = round(coordinates[i], COORDINATES_ACCURACY);
        }

        switch (type) {
        case PathIterator.SEG_MOVETO:
            stream.println("  \\moveto(" + coordinates[0] + ","
                    + coordinates[1] + ")");
            break;
        case PathIterator.SEG_LINETO:
            stream.println("  \\lineto(" + coordinates[0] + ","
                    + coordinates[1] + ")");
            break;
        case PathIterator.SEG_QUADTO:
            stream.println("  \\curveto(" + coordinates[0] + ","
                    + coordinates[1] + ")(" + coordinates[0] + ","
                    + coordinates[1] + ")(" + coordinates[2] + ","
                    + coordinates[3] + ")");
            break;
        case PathIterator.SEG_CUBICTO:
            stream.println("  \\curveto(" + coordinates[0] + ","
                    + coordinates[1] + ")(" + coordinates[2] + ","
                    + coordinates[3] + ")(" + coordinates[4] + ","
                    + coordinates[5] + ")");
            break;
        case PathIterator.SEG_CLOSE:
            stream.println("  \\closepath");
            break;
        default:
            break;
        }
    }

    /**
     * Calculates a label from an index number.
     * 
     * @param index
     *            incrementing index number
     * @return label
     */
    protected static String generateLabel(int index) {
        // generates some label
        StringBuilder label = new StringBuilder();
        char c;

        do {
            c = (char) ('A' + index % 26);
            label.append(c);
            index = index / 26 - 1;
        } while (index + 1 > 0);

        return label.toString();
    }

    /**
     * Rounds floating-point values according to a specified accuracy.
     * 
     * @param value
     *            floating-point value
     * @param accuracy
     *            accuracy
     * @return rounded floating-point value
     */
    protected static double round(double value, int accuracy) {
        return Math.rint(Math.pow(10, accuracy) * value)
                / Math.pow(10, accuracy);
    }

    public void write(OutputStream stream, Graph g) throws IOException {
        this.stream = new PrintStream(stream);
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
