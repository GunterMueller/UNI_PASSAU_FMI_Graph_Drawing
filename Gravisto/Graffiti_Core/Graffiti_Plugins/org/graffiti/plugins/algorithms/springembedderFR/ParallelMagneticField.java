// =============================================================================
//
//   ParallelMagneticField.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ParallelMagneticField.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedderFR;

/**
 * Parallel magnetic field for the alignment of the edges.
 * 
 * @author matzeder
 * @version $Revision: 5766 $ $Date: 2006-06-12 07:52:18 +0200 (Mo, 12 Jun 2006)
 *          $
 */
public class ParallelMagneticField implements MagneticField {

    /**
     * The direction of the magnetic field in x - coordinate
     */
    private int x;

    /**
     * The direction of the magnetic field in y - coordinate
     */
    private int y;

    /**
     * A parallel magnetic field, which only can have the 4 directions, north:
     * (0, -1), south: (0, 1), west: (-1, 0), east: (1, 0). If another direction
     * is tried to create, then InvalidMagneticFieldException.
     * 
     * @param x
     *            Direction in x-coordinate (west, east)
     * @param y
     *            Direction in y-coorinate (north, south)
     * @throws InvalidMagneticFieldException
     *             If a magnetic field should be created, which has not the
     *             allowed direction
     */
    public ParallelMagneticField(int x, int y)
            throws InvalidMagneticFieldException {
        // if the direction is north, south, west or east, with appropriate
        // length
        if (x == 1 && y == 0 || x == -1 && y == 0 || x == 0 && y == 1 || x == 0
                && y == -1) {
            this.x = x;
            this.y = y;
        } else
            throw new InvalidMagneticFieldException(
                    "a parallel magnetic field can only instanciated with a direction (0,1), (0,-1), (1,0), (-1,0)");
    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.springembedderFR.MagneticField#getDirection
     * (double, double)
     */
    public GeometricalVector getDirection(double x, double y) {
        return new GeometricalVector(this.x, this.y);

    }

    /**
     * Output of this parallel magnetic field.
     */
    @Override
    public String toString() {
        return "Parallel Magnetic Field Direction: (" + this.x + ", " + this.y
                + ")";

    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.springembedderFR.MagneticField#setCenter
     * (org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector)
     */
    public void setCenter(GeometricalVector center) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented.");
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
