package org.graffiti.plugins.algorithms.kandinsky;

import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.DimensionAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.Port;
import org.graffiti.graphics.PortsAttribute;
import org.graffiti.plugins.algorithms.kandinsky.NormArc.Status;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;
import org.graffiti.plugins.views.defaults.StraightLineEdgeShape;

/**
 * Calculates the absolute coordinates of the nodes and bends and the ports of
 * the edges. Paints the graph.
 */
public class Drawing {

    // ~ Instance fields
    // ========================================================

    /** The network of the NormArcs and NormNodes. */
    private NormNetwork network;

    /** The grid size of the drawing. */
    private int grid = 0;

    /** The maximum number of ports on the y-side of the node. */
    private int yMax = 1;

    /** The maximum number of ports on the x-side of the node. */
    private int xMax = 1;

    /** The list of ports for each node. */
    private LinkedList<Port> ports = new LinkedList<Port>();

    /** The height of the nodes. */
    private int height = 0; // H�he des Knotens

    /** The width of the nodes. */
    private int width = 0; // Breite des Knotens

    /** The distance between the x-ports. */
    private double dX = 0; // Abstand zwischen x-Ports

    /** The distance between the y-ports. */
    private double dY = 0; // Abstand zwischen y-Ports

    /**
     * List with all of the Arcs which have been replaced in order to get
     * rectangle shaped faces. Left side: new DummyArc, right side: old NormArc.
     */
    private Hashtable<NormArc, NormArc> replacement;

    // ~ Constructors
    // ================================================================
    /**
     * Calculates the absolute coordinates of the nodes and bends and the ports
     * of the edges. Paints the graph.
     * 
     * @param network
     *            <code>NormNetwork</code>
     * @param replacement
     *            List of dummy arcs which have to be replaced.
     */
    public Drawing(NormNetwork network, Hashtable<NormArc, NormArc> replacement) {
        this.grid = 80;
        this.network = network;
        this.replacement = replacement;
        calculateDrawing();
    }

    /**
     * Calculates the drawing of the Graph.
     */
    public void calculateDrawing() {
        // Suche f�r die Knoten die maximale L�nge und Breite -->
        // Einheitsgr��e der Knoten
        computeVertexSize();
        // Berechne wie viele und wo Ports
        calculatePorts();
        // weise Knoten Koordinaten zu
        for (NormNode node : network.getNormNodes()) {
            if (!node.isDummy()) {
                setNodesCoordinates(node);
            }
        }
        // berechne die Knicke der Kanten
        for (NormArc arc : network.getNormArcs()) {
            // Alle Kanten ohne Knicke oder die erste Kante eines Kantenzugs von
            // Dummykanten --> andere Dummykanten werden nicht beachtet
            if (arc.getDirection() && (arc.getPos() < 2)) {
                setBendsCoordinates(arc);
            }
        }
        for (NormNode node : network.getNormNodes()) {
            // weise an allen Knoten des Graphs den Kanten die Ports zu
            if (!node.isDummy()) {
                setPorts(node);
            }
        }
    }

    /**
     * Computes the size of the vertices (xMax and yMax). The size depends of
     * the maximum number of edges on a side.
     */
    /*
     * Jede Seite des Knotens wird durch die mittlere Kante in zwei Bereiche
     * geteilt (one, two). Die Kanten werden je nach Knickrichtung in one oder
     * two eingef�gt.
     */
    private void computeVertexSize() {
        for (NormNode node : network.getNormNodes()) {
            // oben
            char side = 't';
            // Abknick-Richtung, nach denen die Kanten in one und two aufgeteilt
            // werden
            Status status1 = Status.LEFT;
            Status status2 = Status.RIGHT;
            sortEdges(node, side, status1, status2);

            // unten
            side = 'b';
            sortEdges(node, side, status1, status2);

            // links
            side = 'l';
            status1 = Status.UP;
            status2 = Status.DOWN;
            sortEdges(node, side, status1, status2);

            // rechts
            side = 'r';
            sortEdges(node, side, status1, status2);
        }
        // Erzeuge Rechtecke
        int fact = 10;
        width = (xMax + 1) * fact;
        if (grid < (2 * width)) {
            grid = width * 2;
        }
        height = (yMax + 1) * fact;
        if (grid < (2 * height)) {
            grid = height * 2;
        }
        // System.out.println("xMax = " + xMax);
        // System.out.println("yMax = " + yMax);
    }

    /**
     * Sorts the <code>NormArc</code>s of a side of a node according to the
     * direction they bend. Calculates the maximal number of ports.
     * 
     * @param side
     *            the side of the node (top t, bottom b, left l, right r)
     * @param status1
     *            the direction to which the bend of the edge in part one goes
     * @param status2
     *            the direction to which the bend of the edge in part two goes
     */
    /*
     * Jede Seite des Knotens wird durch die mittlere Kante in zwei Bereiche
     * geteilt (one, two). Die Kanten werden je nach Knickrichtung in one oder
     * two eingef�gt.
     * 
     * xMax = 2* max(oben_one, unten_one, oben_two, unten_two) + 1.
     * 
     * yMax = 2* max(links_one, rechts_one, links_two, rechts_two) + 1.
     */
    private void sortEdges(NormNode node, char side, Status status1,
            Status status2) {
        int number;
        LinkedList<NormArc> edges; // Kanten auf einer Seite des Knotens
        // die beiden durch die Mittelkante aufgeteilten Bereiche one/two
        ArrayList<NormArc> listOne; // Kanten auf einer Seite des Knotens
        ArrayList<NormArc> listTwo; // Kanten auf einer Seite des Knotens
        switch (side) {
        case 't':
            edges = node.getUpArcs();
            listOne = node.top_one;
            listTwo = node.top_two;
            break;
        case 'b':
            edges = node.getDownArcs();
            listOne = node.bottom_one;
            listTwo = node.bottom_two;
            break;
        case 'l':
            edges = node.getLeftArcs();
            listOne = node.left_one;
            listTwo = node.left_two;
            break;
        case 'r':
            edges = node.getRightArcs();
            listOne = node.right_one;
            listTwo = node.right_two;
            break;
        default:
            edges = node.getUpArcs();
            listOne = node.top_one;
            listTwo = node.top_two;
            break;
        }
        // Liste mit Kanten die mehrfach vorhanden sind
        LinkedList<NormArc> toDelete = new LinkedList<NormArc>();
        if (edges.size() > 1) {
            for (NormArc a : edges) {
                NormArc x = undoRefinement(a);
                resetEdge(x);
                if (x.getEdge().getBends().size() != 0) {
                    while ((x.getStatus() != status1)
                            && (x.getStatus() != status2)) {
                        x = x.getNext();
                    }
                    if (x.getStatus() == status1) {
                        if (!listOne.contains(x)) {
                            listOne.add(x);
                        } else {
                            toDelete.add(a);
                        }
                    } else {
                        if (!listTwo.contains(x)) {
                            listTwo.add(x);
                        } else {
                            toDelete.add(a);
                        }
                    }
                }
            }
            number = 1 + 2 * java.lang.Math.max(listOne.size(), listTwo.size());
            if (side == 't' || side == 'b') {
                xMax = java.lang.Math.max(xMax, number);
            } else {
                yMax = java.lang.Math.max(yMax, number);
            }
        } else {
            if (edges.size() == 1) {
                NormArc x = undoRefinement(edges.getFirst());
                resetEdge(x);
            }
        }
        for (NormArc a : toDelete) {
            node.removeStatusArc(a);
        }
    }

    /**
     * Each <code>Edge</code> is assigned to the port(0, 0).
     * 
     * @param x
     *            the <code>NormArc</code>
     */
    private void resetEdge(NormArc x) {
        if (x != null) {
            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) x.getEdge()
                    .getEdge().getAttribute(GraphicAttributeConstants.GRAPHICS);
            String portName = "m";
            ega.getDocking().setSource(portName);
            ega.getDocking().setTarget(portName);
        }
    }

    /**
     * Calculates the ports.
     */
    /*
     * Die x-Ports an der oberen/unteren Seite haben den Namen port(x, +/- 1).
     * Es gibt xMax Ports. Die y-Ports an der linken/rechten Seite haben den
     * Namen port(+/- 1, y). Es gibt davon yMax.
     */
    private void calculatePorts() {
        // Port oben
        double x1 = 0;
        double y1 = -1;
        // Port unten
        double x2 = 0;
        double y2 = 1;
        String name1 = null;
        String name2 = null;
        // Abstand zwischen den x-Ports
        dX = ((new BigDecimal(2)).divide(new BigDecimal(xMax + 1), 17,
                BigDecimal.ROUND_HALF_UP)).doubleValue();
        for (int count = 1; count <= xMax; count++) {
            // f�r die Ports oben und unten
            x1 = -1 + (count * dX);
            if (count == ((xMax + 1) / 2)) {
                x1 = 0;
            }
            name1 = "port(" + count + ", " + y1 + ")";
            name2 = "port(" + count + ", " + y2 + ")";
            ports.add(new Port(name1, x1, y1));
            ports.add(new Port(name2, x1, y2));
        }
        // Abstand zwischen den y-Ports
        dY = ((new BigDecimal(2)).divide(new BigDecimal(yMax + 1), 17,
                BigDecimal.ROUND_HALF_UP)).doubleValue();
        x1 = -1;
        x2 = 1;
        for (int count = 1; count <= yMax; count++) {
            // f�r die Ports links und rechts
            y1 = -1 + (count * dY);
            if (count == ((yMax + 1) / 2)) {
                y1 = 0;
            }
            name1 = "port(" + x1 + ", " + count + ")";
            name2 = "port(" + x2 + ", " + count + ")";
            ports.add(new Port(name1, x1, y1));
            ports.add(new Port(name2, x2, y1));
        }
        // Port f�r die mittlere Kante ohne Knicke
        ports.add(new Port("m", 0, 0));
    }

    /**
     * Sets the coordinates of the NormNode.
     * 
     * @param node
     *            the <code>NormNode</code>
     */
    /*
     * Berechne Koordinaten f�r Knoten: x-absolut = relative x-Koordinate *
     * Knotengitter + Abstand von linkem Rand; Einplanen der Einheitsl�nge des
     * Knotens
     */
    private void setNodesCoordinates(NormNode node) {
        // absolute Koordinate berechnen und setzen
        Node n = node.getElement().getElement();
        NodeGraphicAttribute nga = null;
        int x, y, brink;
        nga = (NodeGraphicAttribute) n
                .getAttribute(GraphicAttributeConstants.GRAPHICS);
        nga.setShape(GraphicAttributeConstants.RECTANGLE_CLASSNAME);
        x = node.getX();
        y = node.getY();
        brink = grid;
        if (brink < 25) {
            brink = 25;
        }
        Point2D point = new Point2D.Double((x * grid) + brink, (y * grid)
                + brink);
        nga.getCoordinate().setCoordinate(point);

        // Gr��e setzen
        try {
            n.getAttribute(GraphicAttributeConstants.GRAPHICS);
        } catch (AttributeNotFoundException e) {
            n.addAttribute(new NodeGraphicAttribute(), "");
        }
        DimensionAttribute da = (DimensionAttribute) n
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.DIMENSION);
        da.setWidth(width);
        da.setHeight(height);

        // Ports setzen
        PortsAttribute pa = nga.getPorts();
        pa.setCommonPorts(ports);
    }

    /**
     * Transforms all straight lines with bends into polylines. Sets the new
     * bends of the NormArc.
     * 
     * @param arc
     *            the <code>NormArc</code>
     */
    private void setBendsCoordinates(NormArc arc) {
        // Berechne die Knicke
        Edge edge = arc.getEdge().getEdge();
        try {
            EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) edge
                    .getAttribute("graphics");
            SortedCollectionAttribute bends = null;
            if (arc.getEdge().getBends().size() == 0) {
                bends = new LinkedHashMapAttribute("bends");
                edgeAttr.setBends(bends);
                edgeAttr.setShape(StraightLineEdgeShape.class.getName());
            } else {
                // Einf�gen der Knicke
                bends = new LinkedHashMapAttribute("bends");
                NormArc e = arc;
                for (int count = 1; count < arc.getTotal(); count++) {
                    int x, y;
                    int brink;
                    String id = "bend" + (count - 1);
                    brink = grid;
                    if (brink < 25) {
                        brink = 25;
                    }
                    // Berechnen der absoluten Koordinaten f�r den Knick
                    x = e.getTo().getX();
                    y = e.getTo().getY();
                    Point2D point = new Point2D.Double((x * grid) + brink,
                            (y * grid) + brink);
                    CoordinateAttribute ca = new CoordinateAttribute(id, point);
                    ca.setCoordinate(point);
                    bends.add(ca);
                    e = e.getNext();
                }
                edgeAttr.setBends(bends);
                edgeAttr.setShape(PolyLineEdgeShape.class.getName());
            }
        } catch (Exception e) {
            System.out.println("There are not any polylines!");
        }
    }

    /**
     * Sets the ports of each edge.
     * 
     * @param node
     *            the <code>NormNode</code> where the edge starts and ends.
     */
    /*
     * Jeder Port ist durch die mittlere Kante m, welche keinen Knick hat,
     * zweigeteilt. Auf beiden Seiten werden jetzt von m nach au�en gehend die
     * Ports verteilt.
     */
    private void setPorts(NormNode node) {
        // Berechne Ports
        NodeGraphicAttribute nga = (NodeGraphicAttribute) node.getElement()
                .getElement().getAttribute(GraphicAttributeConstants.GRAPHICS);
        Point2D point = nga.getCoordinate().getCoordinate();
        // oben
        String port = "-1.0";
        char side = 't';
        double coordPoint = point.getX();
        // false, wenn obere/untere Seite des Knotens
        boolean leftRightSide = false;
        if (node.getUpArcs().size() > 1) {
            // rechter Teil auf der oberen Seite
            assignNextPort(node, port, side, coordPoint, true, leftRightSide);
            // linker Teil
            assignNextPort(node, port, side, coordPoint, false, leftRightSide);
        }

        // unten
        side = 'b';
        port = "1.0";
        if (node.getDownArcs().size() > 1) {
            // rechter Teil auf der unteren Seite
            assignNextPort(node, port, side, coordPoint, true, leftRightSide);
            // linker Teil
            assignNextPort(node, port, side, coordPoint, false, leftRightSide);
        }

        // links
        side = 'l';
        port = "-1.0";
        coordPoint = point.getY();
        leftRightSide = true;
        if (node.getLeftArcs().size() > 1) {
            // oberer Teil auf der linken Seite
            assignNextPort(node, port, side, coordPoint, true, leftRightSide);
            // unterer Teil
            assignNextPort(node, port, side, coordPoint, false, leftRightSide);
        }

        // rechts
        side = 'r';
        port = "1.0";
        if (node.getRightArcs().size() > 1) {
            // oberer Teil auf der rechten Seite
            assignNextPort(node, port, side, coordPoint, true, leftRightSide);
            // unterer Teil
            assignNextPort(node, port, side, coordPoint, false, leftRightSide);
        }
    }

    /**
     * Sets the ports for each of the two parts of a side of a node.
     * 
     * @param node
     *            the <code>NormNode</code> where the ports are assigned
     * @param port
     *            the fixed part of the name of the port
     * @param side
     *            the side of the node (top t, bottom b, left l, right r)
     * @param coordPoint
     *            the absolute coordinate of the node
     * @param isOne
     *            if it is the first part of the ports on this side
     * @param leftOrRightSide
     *            if it is the left or right side of the node
     */
    private void assignNextPort(NormNode node, String port, char side,
            double coordPoint, boolean isOne, boolean leftOrRightSide) {
        int middle; // Nummer der mittleren Ports
        if (leftOrRightSide) {
            middle = (yMax + 1) / 2;
        } else {
            middle = (xMax + 1) / 2;
        }
        int count; // Anzahl der Ports auf dieser Seite insgesamt
        if (isOne) {
            count = middle - 1;
        } else {
            count = middle + 1;
        }
        // Kante, die dem n�chsten Port zugewiesen wird
        OrthEdge oe = node.getNextEdge(side, isOne);
        while (!(oe == null)) {
            Edge edge = oe.getEdge();
            EdgeGraphicAttribute ega = (EdgeGraphicAttribute) edge
                    .getAttribute(GraphicAttributeConstants.GRAPHICS);
            String portName = null;
            // Port mit festem x-Wert wird der Kante zugewiesen
            if (leftOrRightSide) {
                portName = "port(" + port + ", " + count + ")";
            } else
            // Port mit festem y-Wert wird der Kante zugewiesen
            {
                portName = "port(" + count + ", " + port + ")";
            }
            if (oe.getDirection()) {
                ega.getDocking().setSource(portName);
            } else {
                ega.getDocking().setTarget(portName);
            }
            // Anpassen der Knicke an die leichte Verschiebung durch den neuen
            // Port
            correctBends(edge, count, coordPoint, side, oe.getDirection());
            oe = node.getNextEdge(side, isOne); // n�chste Kante
            // n�chster Port
            if (isOne) {
                count--;
            } else {
                count++;
            }
        }
    }

    /**
     * Undoes the refinements by replacing the new DummyArcs by the former
     * NormArcs.
     * 
     * @param newArc
     *            The dummy arc which has to be replaced.
     * @return the original <code>NormArc</code>
     */
    private NormArc undoRefinement(NormArc newArc) {
        if (newArc.isDummy()) {
            NormArc n = replacement.get(newArc);
            while ((n != null) && n.isDummy()) {
                n = replacement.get(n);
            }
            return n;
        } else
            return newArc;
    }

    /**
     * Corrects the coordinates of the bends, which have to be shifted because
     * of the change of the port.
     * 
     * @param e
     *            The <code>Edge</code> of the graph.
     * @param count
     *            The position of the edge on this side.
     * @param value
     *            The coordinate of the node.
     * @param side
     *            The side of the node (t : top, b: bottom. l: left, r: right)
     * @param dir
     *            True, if the edge is in direction.
     */
    private void correctBends(Edge e, int count, double value, char side,
            boolean dir) {
        EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) e
                .getAttribute("graphics");
        SortedCollectionAttribute bends = edgeAttr.getBends();
        Iterator<Attribute> i;
        if (dir) {
            i = bends.getCollection().values().iterator();
        } else {
            i = bends.getCollection().values().iterator();
            LinkedList<Attribute> l = new LinkedList<Attribute>();
            while (i.hasNext()) {
                l.addFirst(i.next());
            }
            i = l.iterator();
        }
        Point2D point = null;
        loop: while (i.hasNext()) {
            CoordinateAttribute ca = (CoordinateAttribute) i.next();
            point = ca.getCoordinate();
            double previousX = point.getX();
            double previousY = point.getY();
            int middleX = (xMax + 1) / 2;
            int middleY = (yMax + 1) / 2;
            int diff = 0;
            diff = middleX - count;
            double newX = previousX - (diff * dX * (width / 2));
            diff = middleY - count;
            double newY = previousY - (diff * dY * (height / 2));

            switch (side) {
            case 't':
                if (value == previousX) {
                    point = new Point2D.Double(newX, previousY);
                    ca.setCoordinate(point);
                } else {
                    break loop;
                }
                break;
            case 'b':
                if (value == previousX) {
                    point = new Point2D.Double(newX, previousY);
                    ca.setCoordinate(point);
                } else {
                    break loop;
                }
                break;
            case 'l':
                if (value == previousY) {
                    point = new Point2D.Double(previousX, newY);
                    ca.setCoordinate(point);
                } else {
                    break loop;
                }
                break;
            case 'r':
                if (value == previousY) {
                    point = new Point2D.Double(previousX, newY);
                    ca.setCoordinate(point);
                } else {
                    break loop;
                }
                break;
            default:
                if (value == previousX) {
                    point = new Point2D.Double(newX, previousY);
                    ca.setCoordinate(point);
                } else {
                    break loop;
                }
                break;
            }
        }
    }
}
