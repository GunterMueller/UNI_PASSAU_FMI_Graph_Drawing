package org.graffiti.plugins.algorithms.springembedderFR;

/**
 * Clockwise magnetic field for the alignment of the edges.
 * 
 * @author matzeder
 * @version $Revision: 5766 $ $Date: 2006-06-12 07:52:18 +0200 (Mo, 12 Jun 2006)
 *          $
 */
public class ConcentricMagneticField implements MagneticField {

    /**
     * The center of the magnetic field
     */
    GeometricalVector center;

    /*
     * Creates a concentric magnetic field with a specified center.
     */
    public ConcentricMagneticField(GeometricalVector center)
            throws InvalidMagneticFieldException {

        this.center = center;

    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.springembedderFR.MagneticField#getDirection
     * (double, double)
     */
    public GeometricalVector getDirection(double x, double y) {
        return (new GeometricalVector(-(y - center.getY()), x - center.getX()))
                .getUnitVector();
    }

    /*
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Polar Magnetic Field with Center: " + this.center;

    }

    /**
     * Sets the center.
     */
    public void setCenter(GeometricalVector center) {
        this.center = center;

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
