package quoggles.auxiliary;

import java.awt.Point;
import java.util.Comparator;

import quoggles.constants.QConstants;

/**
 * Used to compare two <code>Point</code>s. Uses a small 
 * <code>EPSILON</code> environment.
 *
 */
public class PointComparator implements Comparator {

    /**
     * Never returns 0 so that no two objects are considered equal.
     * The comparison uses a small EPSILON environment.
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2) {
        Point p1 = (Point)o1;
        Point p2 = (Point)o2;
        if (p1.x < p2.x - QConstants.EPSILON) {
            return -1;
        } else {
            if (p1.x > p2.x + QConstants.EPSILON) {
                return +1;
            } else {
                // x values are "equal"
                if (p1.y < p2.y - QConstants.EPSILON) {
                    return -1;
                } else {
                    if (p1.y > p2.y + QConstants.EPSILON) {
                        return +1;
                    } else {
                        // x and y values are "equal"
                        // since we want to add all points into the map,
                        // we cannot allow two points to be equal
                        return -1;
                    }
                }
            }
        }
    }

    /**
     * The comparison uses a small EPSILON environment.
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int realcompare(Object o1, Object o2) {
        Point p1 = (Point)o1;
        Point p2 = (Point)o2;
        if (p1.x < p2.x - QConstants.EPSILON) {
            return -1;
        } else {
            if (p1.x > p2.x + QConstants.EPSILON) {
                return +1;
            } else {
                // x values are "equal"
                if (p1.y < p2.y - QConstants.EPSILON) {
                    return -1;
                } else {
                    if (p1.y > p2.y + QConstants.EPSILON) {
                        return +1;
                    } else {
                        // x and y values are "equal"
                        // since we want to add all points into the map,
                        // we cannot allow two points to be equal
                        return 0;
                    }
                }
            }
        }
    }
}
