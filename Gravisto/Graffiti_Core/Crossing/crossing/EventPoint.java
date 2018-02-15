package crossing;

import java.util.*;

/**
 * Stores events, such as start-, end- and intersection-points.
 * 
 * @author Daniel Hanisch
 */
public class EventPoint implements Comparable<EventPoint> {
    
    private Coordinate c;
    // stores segments that start at this EventPoint
    private TreeSet<Segment> starts = new TreeSet<Segment>();
    // stores segments that end at this EventPoint
    private TreeSet<Segment> ends = new TreeSet<Segment>();
    // stores segments that intersect at this EventPoint
    private TreeSet<Segment> intersects = new TreeSet<Segment>();

    /**
     * Constructor
     * 
     * @param crd
     *            coordinate of the EventPoint
     */
    public EventPoint(Coordinate crd) {
        setCoordinate(crd);
        starts.clear();
        ends.clear();
        intersects.clear();
    }

    /**
     * Compares this EventPoint with EventPoint e, using their Coordinates.
     * 
     * @param e
     *            compared EventPoint
     * @return -1: if compared EventPoint e lies below this EventPoint (or on
     *         the same level and right of this EventPoint). 1: if compared
     *         EventPoint e lies above this EventPoint (or to the left). 0: if
     *         both EventPoints are equal
     */
    public int compareTo(EventPoint e) {
        return this.getCoordinate().compareTo(e.getCoordinate());
    }

    /**
     * Adds a Segment to the TreeSet starts
     * 
     * @param s
     *            Segment that starts at this EventPoint
     */
    public void addSegmentStart(Segment s) {
        starts.add(s);
    }

    /**
     * Adds a Segment to the TreeSet ends
     * 
     * @param s
     *            Segment that ends at this EventPoint
     */
    public void addSegmentEnd(Segment s) {
        ends.add(s);
    }

    /**
     * Adds a Segment to the TreeSet intersects
     * 
     * @param s
     *            Segment that intersects at this EventPoint
     */
    public void addSegmentIntersects(Segment s) {
        intersects.add(s);
    }

    /**
     * Returns the Coordinate of this EventPoint
     * 
     * @return coordinate
     */
    public Coordinate getCoordinate() {
        return c;
    }

    /**
     * Sets the Coordinate of this EventPoint
     * 
     * @param coordinate
     */
    public void setCoordinate(Coordinate coordinate) {
        c = coordinate;
    }

    /**
     * Returns all Segments ending at this EventPoint
     * 
     * @return Segments ending at EventPoint
     */
    public TreeSet<Segment> getEnds() {
        return ends;
    }

    /**
     * Returns all Segments intersecting at this EventPoint
     * 
     * @return Segments intersecting at EventPoint
     */
    public TreeSet<Segment> getIntersects() {
        return intersects;
    }

    /**
     * Returns all Segments starting at this EventPoint
     * 
     * @return Segments starting at EventPoint
     */
    public TreeSet<Segment> getStarts() {
        return starts;
    }
}
