package org.graffiti.plugins.algorithms.springembedderFR;

/**
 * @author matzeder
 * @version $Revision: 5766 $ $Date: 2006-06-12 07:52:18 +0200 (Mo, 12 Jun 2006)
 *          $
 */
public class PolarMagneticField implements MagneticField {

    /**
     * The center of the polar magnetic field.
     */
    GeometricalVector center;

    /**
     * Creates a new polar magnetic field.
     * 
     * @param center
     *            The specified center.
     * @throws InvalidMagneticFieldException
     */
    public PolarMagneticField(GeometricalVector center)
            throws InvalidMagneticFieldException {

        this.center = center;

    }

    /*
     * @see
     * org.graffiti.plugins.algorithms.springembedderFR.MagneticField#getDirection
     * (double, double)
     */
    public GeometricalVector getDirection(double x, double y) {

        System.out.println("(" + center.getX() + ", " + center.getY() + ")");
        return (new GeometricalVector(x - center.getX(), y - center.getY()))
                .getUnitVector();
    }

    /**
     * The output of this class.
     */
    @Override
    public String toString() {
        return "Polar Magnetic Field with Center: " + this.center;

    }

    /**
     * Sets the specified center.
     */
    public void setCenter(GeometricalVector center) {
        this.center = center;

    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
