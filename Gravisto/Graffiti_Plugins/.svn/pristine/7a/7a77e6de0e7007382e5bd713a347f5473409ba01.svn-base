package crossing;

import java.util.*;
import java.awt.Dimension;
import java.awt.geom.Point2D;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;

import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;

import org.graffiti.graph.*;

/**
 * Calculates intersection points within a graph using a plane sweep approach.
 * EventPoints are created when a sweep line reaches start, end or intersection-
 * points of line segments. Then EventPoints are handled using their natural
 * ordering (y- and x-coordinates).
 * 
 * @author Daniel Hanisch
 */
public class FindIntersections extends AbstractAlgorithm {

    // Event Queue, stores EventPoint using coordinates as keys
    TreeMap<Coordinate, EventPoint> events = 
        new TreeMap<Coordinate, EventPoint>();
    // Tree storing line segments
    TreeNode status = new TreeNode();

    // Stores found intersections (as EventPoints)
    LinkedList<EventPoint> foundIntersections = new LinkedList<EventPoint>();

    // Stores found intersections after duplicates are eliminated
    LinkedList<EventPoint> merged = new LinkedList<EventPoint>();

    // Stores Coordinates of found intersections
    StringBuffer positions = new StringBuffer();

    // Stores new edges of graph
    LinkedList<Segment> updatedEdges = new LinkedList<Segment>();

    // Stores number of found intersections
    private int intersectionCount;
    // parameter for result window
    private BooleanParameter counterParam;
    // parameter for editor window (if new found nodes are added) 
    private BooleanParameter displayParam;
        

    /**
     * Constructs a new instance.
     */
    public FindIntersections() {
        counterParam = new BooleanParameter(true, "count intersection",
                "display the number of found intersections");
        displayParam = new BooleanParameter(true, "show intersections",
                "create dummy nodes at intersection points");
    }

    /**
     * Returns the name of the Algorithm
     */
    public String getName() {
        return "Line Intersection Algorithm";
    }

    /**
     * Main method starts calculation of intersection points
     */
    public void execute() {
        init();                 // adds graph nodes/edges to TreeMap events
        while (!events.isEmpty()) {
            EventPoint current = events.get(events.firstKey());
            events.remove(events.firstKey());
            handleEventPoint(current);
        }

       /* for (EventPoint e : foundIntersections) {
            System.out.println("EP: " + e.getCoordinate().getXCoord() + "/"
                    + e.getCoordinate().getYCoord());
        }*/

        mergePoints();                  // eliminate duplicates
        if (displayParam.getBoolean().equals(Boolean.TRUE))
            displayIntersections();     // display intersections in editor
        if (counterParam.getBoolean().equals(Boolean.TRUE))
            displayResults();           // create window with coordinates
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    public void check() throws PreconditionException {
        if (graph.getNumberOfNodes() <= 0) {
            throw new PreconditionException(
                "The graph is empty. Cannot run Line Intersection Algorithm.");
        }
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#reset()
     */
    public void reset() {
        events = new TreeMap<Coordinate, EventPoint>();
        status = new TreeNode();
        foundIntersections = new LinkedList<EventPoint>();
        merged = new LinkedList<EventPoint>();
        positions = new StringBuffer();
        intersectionCount = 0;
        updatedEdges = new LinkedList<Segment>();
    }

    /**
     * @see org.graffiti.plugin.algorithm.Algorithm#getParameters()
     */
    public Parameter[] getParameters() {
        return new Parameter[] { counterParam, displayParam };
    }

    /**
     * Imports graph elements in data structures used for the algorithm
     */
    public void init() {
        // edges stored in original graph
        Collection<Edge> edges = graph.getEdges();

        for (Edge edge : edges) {
            boolean isBent = false;
            double x = edge.getSource().getDouble("graphics.coordinate.x");
            double y = edge.getSource().getDouble("graphics.coordinate.y");
            double a = edge.getTarget().getDouble("graphics.coordinate.x");
            double b = edge.getTarget().getDouble("graphics.coordinate.y");

            // Check if edge is bent. If so collect bend points and treat them
            // as normal start-/end-nodes
            LinkedHashMapAttribute bends = (LinkedHashMapAttribute) edge
                    .getAttribute("graphics.bends");
            LinkedList<Coordinate> bendPoints = new LinkedList<Coordinate>();
            int i = 0;
            boolean more = true;
            while (more) {
                try {
                    CoordinateAttribute ca = (CoordinateAttribute) bends
                            .getAttribute("bends" + i);
                    bendPoints.add(new Coordinate(ca.getX(), ca.getY()));
                    isBent = true;
                    i++;
                } catch (AttributeNotFoundException e) {
                    more = false;
                }
            }

            Coordinate start = new Coordinate(x, y);
            Coordinate end = new Coordinate(a, b);

            // edge is a straight line
            if (!isBent) {
                addEvents(createSegment(start, end));
                // edge contains bends
            } else {
                addEvents(createSegment(start, bendPoints.getFirst()));
                while (bendPoints.size() > 1) {
                    addEvents(createSegment(bendPoints.removeFirst(),
                            bendPoints.getFirst()));
                }
                addEvents(createSegment(bendPoints.removeLast(), end));
            }
        }
    }

    /**
     * Creates a new Segment out of two Coordinates. A Segment always has to
     * start at the Coordinate with the higher y-value, so the Coordinates have
     * to be compared first.
     * 
     * @param c1
     *            first Coordinate
     * @param c2
     *            second Coordinate
     */
    public Segment createSegment(Coordinate c1, Coordinate c2) {
        Segment seg;
        switch (c1.compareTo(c2)) {
        case -1:
            seg = new Segment(c1, c2, false);
            return seg;
        case 1:
            seg = new Segment(c2, c1, true);
            return seg;
        // loops without any bend points are not added
        default:
            return null;
        }
    }

    /**
     * Add start and end point of a segment to the priotity queue as
     * EventPoints. If EventPoint is already in the priority queue, the new
     * start and end points are added
     * 
     * @param s
     *            Segment whose endpoints are added to priority queue
     */
    public void addEvents(Segment s) {
        // self loops are not added if edge is not bent
        if (s == null)
            return;

        EventPoint e1 = new EventPoint(s.getCoordinate());
        e1.addSegmentStart(s);
        if (!events.containsKey(e1.getCoordinate())) {
            events.put(e1.getCoordinate(), e1);
        } else {
            events.get(e1.getCoordinate()).addSegmentStart(s);
        }

        EventPoint e2 = new EventPoint(s.getEnd());
        e2.addSegmentEnd(s);
        if (!events.containsKey(e2.getCoordinate())) {
            events.put(e2.getCoordinate(), e2);
        } else {
            events.get(e2.getCoordinate()).addSegmentEnd(s);
        }
    }

    /**
     * Inserts and deletes segments from the TreeNode datastructure,maintaining
     * the left to right order of segments passed by the sweep line. Checks if
     * new intersections occur below this point with left and right neighbor
     * segments of this EventPoint.
     * 
     * @param ep
     *            EventPoint
     */
    public void handleEventPoint(EventPoint ep) {

        TreeSet<Segment> del = new TreeSet<Segment>();
        TreeSet<Segment> ins = new TreeSet<Segment>();
        del.addAll(ep.getEnds());
        del.addAll(ep.getIntersects());
        ins.addAll(ep.getStarts());
        ins.addAll(ep.getIntersects());

        /*
         * check if intersection occured at this point, if so add it to
         * foundIntersections
         */
        if (!ep.getIntersects().isEmpty()) {
            foundIntersections.add(ep);

            // add new found edges to intersection points
            if (displayParam.getBoolean()) {
                for (Segment s : ep.getIntersects()) {
                    Segment temp = new Segment(s.getCoordinate(), ep
                            .getCoordinate(), s.isInverted());
                    updatedEdges.add(temp);
                }
            }
        }

        // delete all segments ending or intersecting at EventPoint
        for (Segment s : del) {
            status.delete(s);
        }

        /*
         * (re-)insert all segments starting or intersecting at EventPoint,
         * current coordinate is used as start point (reverses order of
         * segments)
         */
        for (Segment s : ins) {
            s.updateCoordinate(ep.getCoordinate());
            status.insert(s);
        }
        if (status.isEmpty()) {
            if (displayParam.getBoolean()) {
                for (Segment s : ep.getEnds()) {
                    Segment temp = new Segment(s.getCoordinate(), ep
                            .getCoordinate(), s.isInverted());
                    updatedEdges.add(temp);
                }
            }
            return;
        }

        // EventPoint only ends segments
        if (ins.isEmpty()) {
            // check if former left and right neighboring Segments can
            // intersect now
            Segment leftNext = status.findLeft(ep);
            Segment rightNext = status.findRight(ep);
            if (leftNext != null & rightNext != null)
                findEvent(leftNext, rightNext, ep);

            // add new found edges to intersection points
            if (displayParam.getBoolean()) {
                for (Segment s : ep.getEnds()) {
                    Segment temp = new Segment(s.getCoordinate(), ep
                            .getCoordinate(), s.isInverted());
                    updatedEdges.add(temp);
                }
            }
        }

        // check if new inserted segments cause intersections
        else {
            // add new found edges to intersection points
            if (displayParam.getBoolean()) {
                for (Segment s : ep.getEnds()) {
                    Segment temp = new Segment(s.getCoordinate(), ep
                            .getCoordinate(), s.isInverted());
                    updatedEdges.add(temp);
                }
            }
            // the first segment (starting at EventPoint) the sweepline passes
            // below the event point
            Segment leftmost = ins.last();
            LinkNode link = status.find(leftmost);

            // if segment is not leftmost segment
            if (link.left != null) {
                Segment neighborLeft = link.getLeft().getSeg();
                // check if leftmost segment and its left neighbor intersect
                findEvent(neighborLeft, leftmost, ep);
            }
            // the last segment (starting at EventPoint) the sweepline passes
            // below the event point
            Segment rightmost = ins.first();
            LinkNode temp = status.find(rightmost);

            // if segment is not rightmost segment
            if (temp.right != null) {
                Segment neighborRight = temp.getRight().getSeg();
                // check if rightmost segment and its right neighbor intersect
                findEvent(rightmost, neighborRight, ep);
            }
        }
    }

    /**
     * Calculates position of new EventPoint, if an intersection occurs
     * 
     * @param s1
     *            first segment
     * @param s2
     *            second segment
     * @param p
     *            EventPoint from where intersection is calculated
     */
    public void findEvent(Segment s1, Segment s2, EventPoint p) {
        EventPoint found = new EventPoint(s1.calculateIntersection(s2));

        // check if intersection occurs within Segment length
        if (found.getCoordinate() != null) {

            double foundY = found.getCoordinate().getYCoord();
            double foundX = found.getCoordinate().getXCoord();
            double eventPointY = p.getCoordinate().getYCoord();
            double eventPointX = p.getCoordinate().getXCoord();

            // check if found intersection is below sweep line
            if (foundY < eventPointY
                    || (foundY == eventPointY & foundX > eventPointX)) {
                found.addSegmentIntersects(s1);
                found.addSegmentIntersects(s2);

                // check if intersection has been found before
                if (!events.containsKey(found.getCoordinate())) {
                    events.put(found.getCoordinate(), found);

                } else {
                    // new segments are added to existing intersection list
                    EventPoint update = events.get(found.getCoordinate());
                    if (!update.getEnds().contains(s1))
                        update.addSegmentIntersects(s1);
                    if (!update.getEnds().contains(s2))
                        update.addSegmentIntersects(s2);
                }
            }
        }
    }

    /**
     * Eliminate duplicate EventPoints from foundIntersections. Duplicates can
     * be found if intersection is calculated from different EventPoints
     */
    public void mergePoints() {
        while (foundIntersections.size() > 1) {
            EventPoint e1 = foundIntersections.removeFirst();

            while (e1.getCoordinate().roundCoord(
                    foundIntersections.getFirst().getCoordinate())) {
                EventPoint e2 = foundIntersections.removeFirst();
                e1.getIntersects().addAll(e2.getIntersects());
            }
            merged.add(e1);
        }

        if (foundIntersections.size() != 0)
            merged.add(foundIntersections.removeFirst());
    }

    /**
     * Display intersections in editor window as red dots. Deletes all original
     * edges from the graph and replaces them with new ones. These edges start
     * and end at original nodes, as well as new found intersection nodes.
     */
    public void displayIntersections() {
        // number of found intersections
        intersectionCount = merged.size();
        Node[] found = new Node[intersectionCount];

        // delete old edges from graph
        for (Edge e : (Collection<Edge>) graph.getEdges()) {
            graph.deleteEdge(e);
        }

        // add newfound nodes to the graph
        graph.getListenerManager().transactionStarted(this);
        // generate nodes, assign coordinates and attributes to them
        for (int i = 0; i < intersectionCount; ++i) {
            found[i] = graph.addNode();
            CollectionAttribute collA = found[i].getAttributes();

            CoordinateAttribute ca = (CoordinateAttribute) collA
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);

            DimensionAttribute da = (DimensionAttribute) collA
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.DIMENSION);

            ColorAttribute fillColor = (ColorAttribute) collA
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.FILLCOLOR);

            ColorAttribute frameColor = (ColorAttribute) collA
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.FRAMECOLOR);

            // set coordinates of new node
            Coordinate c = merged.get(i).getCoordinate();
            double x = c.getXCoord();
            double y = c.getYCoord();
            ca.setCoordinate(new Point2D.Double(x, y));

            // set visual attributes of new node
            da.setHeight(8);
            da.setWidth(8);
            fillColor.setBlue(0);
            fillColor.setGreen(0);
            frameColor.setBlue(0);
            frameColor.setGreen(0);
        }
        graph.getListenerManager().transactionFinished(this);

        // add new edges to graph
        graph.getListenerManager().transactionStarted(this);

        LinkedList<AdjListNode> allNodes = new LinkedList(graph.getNodes());

        // create hashtable of the (real) nodes in the graph. They are hashed 
        // by their Coordinates.
        Hashtable<Integer, AdjListNode> hashedNodes = 
            new Hashtable<Integer, AdjListNode>();
        // fills Hashtable
        for (AdjListNode aln : allNodes) {
            CoordinateAttribute coord = (CoordinateAttribute) aln
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            Coordinate key = new Coordinate(coord.getX(), coord.getY());
            hashedNodes.put(key.hashCode(), aln);
        }

        /*
         * datastructures for bent edges: stores coordinates of adjacent nodes
         * in a linked list. All bend points and nodes adjacent to bend points
         * are added to this datastructure
         */
        ArrayList<LinkedList<Coordinate>> adjacent = 
            new ArrayList<LinkedList<Coordinate>>();
        // used for addressing adjacent array
        // hashvalue of coordinates are used as keys. values are positions in
        // adjacent arraylist
        Hashtable<Integer, Integer> coords = new Hashtable<Integer, Integer>();
        // current position in Arraylist adjacent
        int position = -1;
        // stores segments of original graph, with their orientation
        Hashtable<Segment, Boolean> invertedSegment = 
            new Hashtable<Segment, Boolean>();

        for (Segment seg : updatedEdges) {
            int hashStart = seg.getCoordinate().hashCode();
            int hashEnd = seg.getEnd().hashCode();
            boolean isBent = false;

            // if start point or end point is a bend point
            if ((!hashedNodes.containsKey(hashStart))
                    || (!hashedNodes.containsKey(hashEnd))) {
                isBent = true;
                invertedSegment.put(seg, seg.isInverted());
                // add end point of segment to adjacent list
                if (!coords.containsKey(hashStart)) {
                    position++;
                    coords.put(hashStart, position);
                    adjacent.add(coords.get(hashStart),
                            new LinkedList<Coordinate>());
                    adjacent.get(coords.get(hashStart)).add(seg.getEnd());
                } else {
                    adjacent.get(coords.get(hashStart)).add(seg.getEnd());
                }
                // add start point to adjacent list of end point
                if (!coords.containsKey(hashEnd)) {
                    position++;
                    coords.put(hashEnd, position);
                    adjacent.add(coords.get(hashEnd),
                            new LinkedList<Coordinate>());
                    adjacent.get(coords.get(hashEnd)).add(seg.getCoordinate());
                } else {
                    adjacent.get(coords.get(hashEnd)).add(seg.getCoordinate());
                }
            }

            // normal edges are simply added to the graph, maintaining their
            // direction
            if (!isBent) {
                if (seg.isInverted()) {
                    Edge e = graph.addEdge(hashedNodes.get(seg.getEnd()
                            .hashCode()), hashedNodes.get(seg.getCoordinate()
                            .hashCode()), true);
                    EdgeGraphicAttribute ea = (EdgeGraphicAttribute) e
                            .getAttributes().getAttribute(
                                    EdgeGraphicAttribute.GRAPHICS);
                    ea.setArrowhead("org.graffiti.plugins.views.defaults." +
                            "StandardArrowShape");
                    ea.setFillcolor(new ColorAttribute("", 0, 0, 0, 255));

                } else {
                    Edge e = graph.addEdge(hashedNodes.get(seg.getCoordinate()
                            .hashCode()), hashedNodes.get(seg.getEnd()
                            .hashCode()), true);
                    EdgeGraphicAttribute ea = (EdgeGraphicAttribute) e
                            .getAttributes().getAttribute(
                                    EdgeGraphicAttribute.GRAPHICS);
                    ea.setArrowhead("org.graffiti.plugins.views.defaults." +
                            "StandardArrowShape");
                    ea.setFillcolor(new ColorAttribute("", 0, 0, 0, 255));
                }
            }
        }

        // bent edges are now added to the graph
        // Bent edges are constructed by following a path from one normal (not
        // bend) node to another. The adjacency list is used for finding this
        // path.
        for (Integer hash : coords.keySet()) {
            // current node is a normal node in the graph (an original node or
            // new found intersection)
            // construction of bent edges starts and ends at one of these nodes
            if (hashedNodes.containsKey(hash)) {
                // as long as there are adjacent nodes to current node
                while (!adjacent.get(coords.get(hash)).isEmpty()) {
                    // stores found start-,bend- and end-coordinates
                    LinkedList<Coordinate> edge = new LinkedList<Coordinate>();
                    AdjListNode adn = hashedNodes.get(hash);
                    CoordinateAttribute coord = (CoordinateAttribute) adn
                            .getAttribute(GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.COORDINATE);
                    // coordinates of start-point
                    Coordinate last = 
                        new Coordinate(coord.getX(), coord.getY());
                    // add start node to edge-list (coordinates on bent edge)
                    edge.add(last);
                    // get next adjacent node and remove it from adjacent list
                    Coordinate temp = adjacent.get(coords.get(hash))
                            .removeFirst();
                    // as long as the adjacent node is a bend point, the node is
                    // added to edge-list
                    while (!hashedNodes.containsKey(temp.hashCode())) {
                        edge.add(temp);
                        // first the node from where this node was reached is
                        // removed
                        // from the adjacent list (to prevent loops)
                        adjacent.get(coords.get(temp.hashCode())).remove(last);
                        last = new Coordinate(temp.getXCoord(), temp
                                .getYCoord());
                        temp = adjacent.get(coords.get(last.hashCode()))
                                .removeFirst();
                    }
                    adjacent.get(coords.get(temp.hashCode())).remove(last);
                    // end point is added to edge-list
                    edge.add(temp);
                    // stores if original edge was inverted (start point below
                    // end point)
                    boolean inv;
                    // create two dummy segments. one of them was in the
                    // original graph
                    Segment tmp1 = new Segment(edge.get(0),edge.get(1),false);
                    Segment tmp2 = new Segment(edge.get(1),edge.get(0),false);
                    //check in which direction the original segment was heading
                    if (invertedSegment.get(tmp1) != null) {
                        inv = invertedSegment.get(tmp1);
                    } else {
                        inv = !invertedSegment.get(tmp2);
                    }
                    // new edge is created (maintaining its original direction)
                    Edge bEdge;
                    if (!inv) {
                        bEdge = graph.addEdge(hashedNodes.get(edge
                                .removeFirst().hashCode()), hashedNodes
                                .get(edge.removeLast().hashCode()), true);
                    } else {
                        bEdge = graph.addEdge(hashedNodes.get(edge.removeLast()
                                .hashCode()), hashedNodes.get(edge
                                .removeFirst().hashCode()), true);
                    }
                    // attributes of new edge are set
                    CollectionAttribute attributes = bEdge.getAttributes();
                    EdgeGraphicAttribute edgeAttributes =(EdgeGraphicAttribute) 
                        attributes.getAttribute(EdgeGraphicAttribute.GRAPHICS);
                    SortedCollectionAttribute sortedBends = new 
                       LinkedHashMapAttribute(GraphicAttributeConstants.BENDS);
                    int numberOfBends = 0;
                    // bendpoints attributes are created
                    for (Coordinate c : edge) {
                        sortedBends.add(new CoordinateAttribute("bends"
                                + numberOfBends, new Point2D.Double(c
                                .getXCoord(), c.getYCoord())));
                        numberOfBends++;
                    }
                    // bendpoints are added to edge
                    edgeAttributes.setBends(sortedBends);
                    // shape of the edge is adjusted to polyline
                    edgeAttributes.setShape(GraphicAttributeConstants.
                            POLYLINE_CLASSNAME);
                    // directional arrows are added to the polyline
                    edgeAttributes.setArrowhead("org.graffiti.plugins.views"+
                                               ".defaults.StandardArrowShape");
                    edgeAttributes.setFillcolor(new ColorAttribute("",0,0,0,
                            255));
                }
            }
        }
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * Creates a Window with quantity and position of found intersections
     */
    public void displayResults() {
        intersectionCount = merged.size();

        for (int i = 0; i < intersectionCount; i++) {
            Coordinate c = merged.get(i).getCoordinate();
            double x = c.getXCoord();
            double y = c.getYCoord();
            positions.append(x + " / " + y + "\n");
        }
        JFrame frame = new JFrame("Results");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        frame.setContentPane(panel);

        JLabel label = new JLabel("Found " + intersectionCount
                + " intersections at positions:");
        JTextArea text = new JTextArea(positions.toString());
        text.setEditable(false);

        JScrollPane scroll = new JScrollPane(text);
        scroll.setPreferredSize(new Dimension(100, 200));

        frame.getContentPane().add(label);
        frame.getContentPane().add(scroll);
        frame.setLocation(400, 200);
        frame.pack();
        frame.setVisible(true);
    }
}
