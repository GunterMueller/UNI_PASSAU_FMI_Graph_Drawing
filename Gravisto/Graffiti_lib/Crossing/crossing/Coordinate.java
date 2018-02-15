package crossing;

/**
 * Coordinate class for EventPoints and Segments. A Coordinate consists of two
 * double values (x and y).
 * 
 * @author Daniel Hanisch
 * 
 */
public class Coordinate implements Comparable<Coordinate> {
    
    private double xCoord;
    private double yCoord;

    /**
     * Constructor
     * 
     * @param x
     *            x-value
     * @param y
     *            y-value
     */
    public Coordinate(double x, double y) {
        xCoord = Math.round(x * Segment.factor) / Segment.factor;
        yCoord = Math.round(y * Segment.factor) / Segment.factor;
    }

    /**
     * Compares this coordinate with coordinate c, using lexicographical order.
     * Coordinates are first ordered by their y-values.
     * 
     * @param c
     *            compared Coordinate
     * @return -1: if compared Coordinate c lies below this Coordinate (or on
     *         the same level and right of this Coordinate.). 1: if compared
     *         Coordinate c lies above this Coordinate (or to the left). 0: if
     *         both are equal (rounded)
     */
    public int compareTo(Coordinate c) {
        if (this.roundCoord(c))
            return 0;
        if ((this.yCoord > c.yCoord)
                || (this.yCoord == c.yCoord & this.xCoord < c.xCoord))
            return -1;
        if ((this.yCoord < c.yCoord)
                || (this.yCoord == c.yCoord & this.xCoord > c.xCoord))
            return 1;

        // should not be reached
        System.out.println("ERROR: -2 returned (Coord)");
        return -2;
    }

    /**
     * Compares two coordinates.
     * 
     * @param c
     *            compared coordinate
     * @return true, if after rounding both coordinates are equal
     */
    public boolean roundCoord(Coordinate c) {
        if ((Math.abs(this.xCoord - c.xCoord) < 10 / Segment.factor)
                & (Math.abs(this.yCoord - c.yCoord) < 10 / Segment.factor))
            return true;
        else
            return false;
    }

    /**
     * Compares two coordinates. If they possess the same x- and y-values, they
     * are equal.
     * 
     * @param c
     *            compared coordinate
     * @return true, if equal
     */
    public boolean equals(Coordinate c) {
        if (this.compareTo(c) == 0)
            return true;
        else
            return false;
    }

    /**
     * Overrides java.lang.Object#equals(). Returns true if compared object is
     * a Coordinate with equal x- and y-values.
     * 
     * @param o
     *            compared Object
     */
    public boolean equals(Object o) {
        try {
            Coordinate tmp = (Coordinate) o;
            return (this.hashCode() == tmp.hashCode());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Overrides java.lang.Object#hashCode(). Assures that Coordinates with
     * equal x- and y-values are hashed the same way.
     * 
     * @return hashvalue of concatenated String consisting of xCoord and yCoord
     */
    public int hashCode() {
        String hash = "" + xCoord + yCoord;
        return hash.hashCode();
    }

    /**
     * Returns x-coordinate of this Coordinate
     * 
     * @return xCoord
     */
    public double getXCoord() {
        return xCoord;
    }

    /**
     * Returns y-coordinate of this Coordinate
     * 
     * @return yCoord
     */
    public double getYCoord() {
        return yCoord;
    }

    /**
     * Sets x-coordinate of this Coordinate
     * 
     * @param d
     *            xCoord
     */
    public void setXCoord(double d) {
        xCoord = d;
    }

    /**
     * Sets y-coordinate of this Coordinate
     * 
     * @param d
     *            yCoord
     */
    public void setYCoord(double d) {
        yCoord = d;
    }

}
