// =============================================================================
//
//   MagneticField.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: MagneticField.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.springembedderFR;

/**
 * Interface for magnetic fields.
 * 
 * @author matzeder
 * @version $Revision: 5766 $ $Date: 2006-06-09 16:53:31 +0200 (Fr, 09 Jun 2006)
 *          $
 */
public interface MagneticField {
    /**
     * Returns, the direction at the point (x, y).
     * 
     * @param x
     *            x-part of the point, where direction of the magnetic field is
     *            searched
     * @param y
     *            y-part of the point, where direction of the magnetic field is
     *            searched
     * @return A GeometricalVector, with the direction of the magnetic field.
     */
    public GeometricalVector getDirection(double x, double y);

    /**
     * For magnetic fields with a center point.
     * 
     * @param center
     *            The given center point.
     */
    public void setCenter(GeometricalVector center);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
