package org.graffiti.plugins.algorithms.labeling;

import org.graffiti.plugins.algorithms.springembedderFR.GeometricalVector;

/**
 * direction angle module
 * <p>
 * Directions embody a circular axis, hiding implementation related over- and
 * underflow issues. They provide simple mathematic operators and routines for
 * conversion from <code>GeometricalVector</code>.
 * 
 * @author scholz
 */
public final class Direction implements Comparable<Direction> {

    /**
     * Angle in radians in the interval [0;2*Pi[.
     */
    public final double angle;

    /**
     * The supplied value is treated as a radian angle and normalised to the
     * interval [0;2*Pi[.
     * 
     * @return normalised double
     */
    private double normalize(double r) {
        return ((r % (2 * Math.PI)) + (2 * Math.PI)) % (2 * Math.PI);
    }

    /**
     * creates a direction angle facing at 0�
     */
    public Direction() {
        angle = 0;
    }

    /**
     * creates a direction angle
     * 
     * @param radians
     *            [0;2*Pi[; will be normalized if not in interval
     */
    public Direction(double radians) {
        angle = normalize(radians);
    }

    /**
     * creates a direction angle of a <code>GeometricalVector</code>.
     * <p>
     * Reference: be x > 0,
     * <ul>
     * <li>( x | 0 ) results in a direction of 0�
     * <li>( 0 | x ) results in a direction of 90�
     * <li>( -x | 0 ) results in a direction of 180�
     * <li>( 0 | -x ) results in a direction of 270�
     * <li>( x | x ) results in a direction of 45�
     * <li>( 0 | 0 ) results in a direction of 0�
     * </ul>
     */
    public Direction(GeometricalVector v) {
        // special cases
        if (v.getY() == 0) {
            if (v.getX() >= 0) {
                angle = 0;
            } else {
                angle = Math.PI;
            }
        } else if (v.getX() == 0) {
            if (v.getY() >= 0) {
                angle = Math.PI * 0.5;
            } else {
                angle = Math.PI * 1.5;
            }
        } else
        // arcus tangens
        if (v.getX() > 0) {
            angle = normalize(Math.atan(v.getY() / v.getX()));
        } else {
            angle = normalize(Math.atan(v.getY() / v.getX()) + Math.PI);
        }
        assert ((angle >= 0) && (angle < 2 * Math.PI)) : ("out of bounds: "
                + (angle * 180d / Math.PI) + "�");
    }

    /**
     * returns the opposite direction
     * 
     * @param direction
     * @return the direction on the exact other side of the unit circle.
     */
    public static Direction getOpposite(Direction direction) {
        return new Direction(direction.angle + Math.PI);
    }

    /**
     * calculates the not commutative cone angle between <tt>d1</tt> and
     * <tt>d2</tt>
     * <p>
     * The cone is assumed to emanate from <tt>d1</tt> in the direction where
     * <tt>d2</tt> lies closest. A clockwise direction results in a positive
     * angle, a anticlockwise direction in a negative angle. Opposite directions
     * result in an Angle of 180�.
     * 
     * @param d1
     * @param d2
     * @return degree angle of the cone spanned by the two given directions in
     *         ]-180; 180];
     */
    public static double coneAngle(Direction d1, Direction d2) {
        double dir = (new Direction(d2.angle - d1.angle)).getValueAsDegrees();
        if (dir <= 180d)
            return dir;
        else
            return -(360d - dir);
    }

    /**
     * calculates the clockwise distance from <tt>d1</tt> to <tt>d2</tt>
     * 
     * @param d1
     * @param d2
     * @return angle from <tt>d1</tt> to <tt>d2</tt> [0; 360�[;
     */
    public static double clockwiseDistance(Direction d1, Direction d2) {
        double d1ToD2 = d2.getValueAsDegrees() - d1.getValueAsDegrees();
        double d2ToD1 = d1.getValueAsDegrees() - d2.getValueAsDegrees();
        if (d2.angle >= d1.angle)
            return d1ToD2;
        else
            return 360d - d2ToD1;
    }

    /**
     * @return degree value in the interval [0;360[
     */
    public double getValueAsDegrees() {
        assert (((angle / Math.PI) * 180 < 360.0) && ((angle / Math.PI) * 180 >= 0.0)) : ("out of bounds: " + (angle * 180d / Math.PI));
        return (angle / Math.PI) * 180;
    }

    @Override
    public String toString() {
        return getValueAsDegrees() + "�";
    }

    public int compareTo(Direction d) {
        return (int) Math.signum(angle - d.angle);
    }

    @Override
    public boolean equals(Object o) {
        /*
         * For use in <tt>SortedSet</tt>s, this method is called (I don't know
         * why <tt>SortedSet<Direction>.contains</tt> expects a parameter of
         * type <tt>Object</tt> instead of <tt>Direction</tt>). As the
         * overloaded specialized <tt>equals</tt> is statically binded, this
         * function has to access runtime type information.
         */
        if (o instanceof Direction)
            return angle == ((Direction) o).angle;
        else
            return super.equals(o);
    }

    public boolean equals(Direction d) {
        return angle == d.angle;
    }

}
