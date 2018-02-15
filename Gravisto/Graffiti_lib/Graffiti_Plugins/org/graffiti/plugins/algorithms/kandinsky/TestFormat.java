package org.graffiti.plugins.algorithms.kandinsky;

import java.util.LinkedList;
import java.util.StringTokenizer;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.fpp.Face;
import org.graffiti.plugins.algorithms.kandinsky.MCMFNode.Type;

/**
 * This class checks, if a constraint is valid and correct. It decides if it is
 * an angle or a bend constraint and returns this constraint.
 * 
 * @author Sonja
 * @version $Revision$ $Date$
 */
public class TestFormat {

    /** List with the nodes of the face, where the angle has to be changed. */
    private LinkedList<GraphNode> list = new LinkedList<GraphNode>();

    /** Type of the constraint (Angle, Bend). */
    private Type type;

    /** The <code>MCMFNode</code>, where the angle has to be changed. */
    private GraphNode node;

    /** The desired angle of the constraint. */
    int angle;

    /** The desired bends of the edge of the bend-constraint. */
    LinkedList<Boolean> bends = new LinkedList<Boolean>();

    /** The cost for changing the angle or not making the prescribed bend. */
    private int cost;

    /** The network which the constraints are meant for. */
    private MCMFNetwork n;

    /** Stores the edges with bends. */
    private LinkedList<Edge> edges = new LinkedList<Edge>();

    /**
     * Is true, if the input of edge which gets the bend is given in the correct
     * direction.
     */
    private boolean inDirection;

    /** The face whose angle is to be changed. */
    private FaceNode face;

    /** The edge which gets the bends. */
    private Edge edge;

    /** First leg of the angle. */
    private Edge edge1;

    /** Second leg of the angle. */
    private Edge edge2;

    /**
     * Is true, if a face is prescribed.
     */
    private boolean givenFace = false;

    /**
     * Checks the format of the constraint.
     * 
     * @param s
     *            String of the constraint
     * @return true, if the format is correct.
     */
    private boolean getConstraint(String s) {
        list = new LinkedList<GraphNode>(); // speichert alle angegebenen Knoten
        if (s.charAt(0) == '[' && s.charAt(s.length() - 1) == ']') {
            s = s.substring(1, (s.length() - 1));
            StringTokenizer st = new StringTokenizer(s, ";");

            if (st.hasMoreTokens()) {
                String t = st.nextToken().trim();
                // Bend Constraint
                if (isArc(t, true)) // Kante wurde angegeben
                {
                    if (st.hasMoreTokens()) {
                        t = st.nextToken().trim();
                        if (isBend(t)) // Knick wurde angegeben
                        {
                            if (st.hasMoreTokens()) {
                                t = st.nextToken().trim();
                                // Kosten f�rs Nicht-Erf�llen
                                cost = isCost(t);
                                if (cost >= 0) {
                                    type = Type.BEND;
                                    System.out.println("   OK.");
                                    return true;
                                }
                            }
                        }
                    }
                } else {
                    // Angle Constraint
                    if (!t.startsWith("(")) {
                        // Knoten, an dem der Winkel ge�ndert werden soll, wurde
                        // angegeben
                        if ((getNode(t, true)) && (st.hasMoreTokens())) {
                            t = st.nextToken().trim();
                            // erste Kante, die Fl�che begrenzt
                            if (isArc(t, false)) {
                                edge1 = edge;
                                if (st.hasMoreTokens()) {
                                    t = st.nextToken().trim();
                                    // zweite Kante, die Fl�che begrenzt
                                    if (isArc(t, false)) {
                                        edge2 = edge;
                                        if (st.hasMoreTokens()) {
                                            t = st.nextToken().trim();
                                            if (getFace(t) != null) {
                                                // g�ltige Fl�che wurde
                                                // angegeben
                                                if (givenFace) {
                                                    if (st.hasMoreTokens()) {
                                                        t = st.nextToken()
                                                                .trim();
                                                    }
                                                }
                                                // Winkel wurde angegeben
                                                if (isAngle(t)) {
                                                    if (st.hasMoreTokens()) {
                                                        t = st.nextToken()
                                                                .trim();
                                                        // Kosten f�rs
                                                        // Nicht-Erf�llen
                                                        cost = isCost(t);
                                                        if (cost >= 0) {
                                                            type = Type.ANGLE;
                                                            System.out
                                                                    .println("   OK.");
                                                            return true;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("   Constraint wird nicht ber�cksichtigt.");
        return false;
    }

    /**
     * Checks, if the values of the <code>MCMFArc</code> are valid.
     * 
     * @param s
     *            the constraint
     * @param isBend
     *            true, if the constraint is a bend constraint
     * @return if this Edge exists
     */
    /*
     * Die Kante mu� das Format (a, b) haben; a und b m�ssen Knoten des Graphen
     * sein und es mu� eine Kante zwischen a und b existieren.
     */
    private boolean isArc(String s, boolean isBend) {
        if (s.startsWith("(") && s.endsWith(")")) {
            boolean result = true;
            s = s.substring(1, s.length() - 1);
            StringTokenizer f = new StringTokenizer(s, ",");
            while (f.hasMoreTokens()) {
                String i = f.nextToken().trim();
                result = result && getNode(i, false);
            }
            result = result && existArc(isBend);
            return result;
        } else
            return false;
    }

    /**
     * Checks, if this Edge exists.
     * 
     * @param isBend
     *            true, if the constraint is a bend constraint
     * @return if this Edge exists.
     */
    /*
     * �berpr�ft, ob eine Kante zwischen a und b existiert. Bei einem
     * AngleConstraint ist isBend false und es wird �berpr�ft, ob die angegebene
     * Kante adjazent zum Knoten ist, an dem der Winkel ge�ndert werden soll.
     */
    private boolean existArc(boolean isBend) {
        int beforeLast = list.size() - 2;
        Node n1 = list.get(beforeLast).getElement();
        Node n2 = list.getLast().getElement();
        // ist gerichtete Kante?
        for (Edge e : n1.getDirectedOutEdges()) {
            if ((e.getSource() == n1) && (e.getTarget() == n2)) {
                edge = e;
                inDirection = true;
                if (!isBend && !isAngleLeg())
                    return false;
                return true;
            }
        }
        for (Edge e : n1.getDirectedInEdges()) {
            if ((e.getTarget() == n1) && (e.getSource() == n2)) {
                edge = e;
                inDirection = false;
                if (!isBend && !isAngleLeg())
                    return false;
                return true;
            }
        }
        // ist ungerichtete Kante?
        for (Edge e : n1.getUndirectedEdges()) {
            if ((e.getSource() == n1) && (e.getTarget() == n2)) {
                edge = e;
                inDirection = true;
                if (!isBend && !isAngleLeg())
                    return false;
                return true;
            } else {
                if ((e.getSource() == n2) && (e.getTarget() == n1)) {
                    edge = e;
                    inDirection = false;
                    if (!isBend && !isAngleLeg())
                        return false;
                    return true;
                }
            }
        }
        System.out.print("   Kante existiert nicht.");
        return false;
    }

    /**
     * Checks, if the value of the leg is correct.
     * 
     * @return if the Edge is a leg of the angle.
     */
    /*
     * �berpr�ft, ob die Kante ein Bein des zu �ndernden Winkels ist, d.h. die
     * Kante ist adjazent zum angegebenen Knoten.
     */
    private boolean isAngleLeg() {
        if ((edge.getSource() == node.getElement())
                || (edge.getTarget() == node.getElement()))
            return true;
        System.out.println("   Winkelangabe nicht korrekt.");
        return false;
    }

    /**
     * Checks, if the values of the angle are valid.
     * 
     * @param s
     *            the angle
     * @return if the angle is correct (0, 90, 180, 270, 360)
     */
    /*
     * �berpr�ft, ob der Winkel aus der Menge {0, 90, 180, 270, 360} ist.
     */
    private boolean isAngle(String s) {
        if (!s.equals("")) {
            try {
                angle = Integer.parseInt(s);
            } catch (NumberFormatException e) {
                System.out.println("   Falsche Winkelangabe " + s);
                return false;
            }
            if (angle == 0 || angle == 90 || angle == 180 || angle == 270
                    || angle == 360) {
                angle = angle / 90;
                return true;
            } else {
                System.out.println("   Falsche Winkelangabe " + s);
                return false;
            }
        } else
            return false;
    }

    /**
     * Checks, if the values of the bend angle are valid.
     * 
     * @param s
     *            the angle of the bend
     * @return if the bend is correct (contains only 0 and 1)
     */
    /*
     * �berpr�ft, ob f�r die Kante bereits ein Knick definiert wurde und ob die
     * Knickangaben nur aus 0 oder 1 bestehen. Die Knicke werden immer gem�� der
     * Kantenrichtung angegeben.
     */
    private boolean isBend(String s) {
        if (edges.contains(edge)) {
            System.out.println("   Kantenknicke wurden bereits definiert.");
            System.out.println("   Bitte zu einem Constraint zusammenfassen.");
            return false;
        }
        edges.add(edge);
        if (!s.equals("")) {
            try {
                if (!inDirection) {
                    // dreht die Reihenfolge der Knoten in list um,
                    // wenn die Kante entgegen ihrer Richtung eingegeben wurde
                    GraphNode node = list.getFirst();
                    list.removeFirst();
                    list.add(node);
                }
                bends = new LinkedList<Boolean>();
                LinkedList<Boolean> x = new LinkedList<Boolean>();
                for (int i = 0; i < s.length(); i++) {
                    String string = "" + s.charAt(i);
                    int b = Integer.parseInt(string);
                    if (b == 0 || b == 1) {
                        if (b == 0) {
                            x.add(true);
                        } else {
                            x.add(false);
                        }
                    } else {
                        System.out.println("   Falsche Winkelangabe " + s);
                        return false;
                    }
                }
                if (inDirection) {
                    bends = x;
                } else {
                    for (Boolean b : x) {
                        if (b) {
                            bends.addFirst(false);
                        } else {
                            bends.addFirst(true);
                        }
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println("   Falsche Winkelangabe " + s);
                return false;
            }
            return true;
        } else
            return false;
    }

    /**
     * Checks, if the values of the costs are valid.
     * 
     * @param s
     *            the cost
     * @return if the costs are given as a positive Integer
     */
    /*
     * �berpr�ft, ob die angegebenen Kosten f�rs Nicht-Erf�llen eine positive
     * ganzzahlige Zahl sind.
     */
    private int isCost(String s) {
        if (!s.equals("")) {
            try {
                int c = Integer.parseInt(s);
                if (c < 0) {
                    System.out.println("   Negative Kostenangabe " + c);
                    return -1;
                } else
                    return c;
            } catch (NumberFormatException e) {
                System.out.println("   Falsche Kostenangabe " + s);
                return -1;
            }
        } else
            return -1;
    }

    /**
     * Checks, if the values of the <code>MCMFNode</code> are valid.
     * 
     * @param s
     *            the node
     * @param angle
     *            true, if this node is to be changed by an angle constraint
     * @return if this Node exists
     */
    /*
     * �berpr�ft, ob ein Knoten existiert. Wenn an diesem Knoten ein Winkel
     * festgelegt werden soll, wird er zus�tzlich als node gespeichert,
     * ansonsten nur in list.
     */
    private boolean getNode(String s, boolean angle) {
        if (!s.equals("")) {
            s = s.trim();
            GraphNode k = n.searchGraphNode(s);
            if (k != null) {
                list.add(k);
                if (angle) {
                    node = k;
                }
                return true;
            } else {
                System.out.print("   Knoten existiert nicht.");
                return false;
            }
        } else
            return false;
    }

    /**
     * Tests, if the constraint is correct.
     * 
     * @param s
     *            the constraint
     * @param network
     *            The network which the constraints are meant for.
     * @return if the format of the constraint is ok
     */
    public boolean isFormatCorrect(String s, MCMFNetwork network) {
        this.n = network;
        System.out.println(s);
        if (s.length() == 0)
            return false;
        if (getConstraint(s))
            return true;
        else
            return false;
    }

    /**
     * Gets the face, where the angle is to be changed.
     * 
     * @param s
     *            the constraint
     * @return the face
     */
    /*
     * �berpr�ft, ob die zwei Schenkel zu einer Fl�che geh�ren. Bei mehreren
     * M�glichkeiten wird eine Innenfl�che gew�hlt. Es kann nur ein Constraint
     * pro Fl�che an einem Knoten angegeben werden.
     */
    private FaceNode getFace(String s) {
        // Falls eine Fl�che angegeben wurde
        if (s.startsWith("Face ")) {
            s = s.trim();
            FaceNode k = n.searchFaceNode(s);
            Face f = k.getElement();
            givenFace = true;
            if ((f.getEdgelist().contains(edge1))
                    && (f.getEdgelist().contains(edge2))) {
                if (node.hasConstraint(k)) {
                    System.out
                            .println("   Diese Bedingung kann nicht ber�cksichtigt "
                                    + "werden.");
                    System.out
                            .println("   Es wurde bereits ein Constraint f�r "
                                    + "diesen Winkel definiert.");
                    return null;
                } else {
                    node.addConstraint(k);
                }
                face = k;
                if (!isAdjacent(face, edge1, edge2)) {
                    System.out
                            .println("   Die angegebenen Kanten folgen nicht aufeinander.");
                    return null;
                }
                return k;
            } else {
                System.out.println("   Die angegebene Fl�che ist ung�ltig. "
                        + "Es wird eine andere gesucht.");
            }
        }
        LinkedList<FaceNode> faces = new LinkedList<FaceNode>();
        // keine Fl�che vorgegeben --> Suche nach einer Fl�che mit dem Winkel
        // (edge1, edge2)
        a: for (FaceNode k : n.getFaceList()) {
            Face f = k.getElement();
            if ((!f.getEdgelist().contains(edge1))
                    || (!f.getEdgelist().contains(edge2))) {
                continue a;
            }
            if (node.hasConstraint(k)) {
                System.out
                        .println("   Diese Bedingung kann nicht ber�cksichtigt "
                                + "werden.");
                System.out.println("   Es wurde bereits ein Constraint f�r "
                        + "diesen Winkel definiert.");
                return null;
            } else {
                node.addConstraint(k);
            }
            // gesucht ist grunds�tzlich ein innerer Winkel f�r ein
            // Constraint
            if (!k.getLabel().equals("Face 0")) {
                face = k;
                if (!isAdjacent(face, edge1, edge2)) {
                    System.out
                            .println("   Die angegebenen Kanten folgen nicht aufeinander.");
                    return null;
                }
                if (givenFace) {
                    System.out.println("   Gefundene Fl�che: "
                            + face.getLabel());
                }
                return face;
            } else
            // weitersuchen nach anderer Fl�che
            {
                faces.add(k);
            }
        }
        // nur Winkel von Au�enfl�che einschl�gig
        if (faces.size() != 0) {
            face = faces.getLast();
            if (!isAdjacent(face, edge1, edge2)) {
                System.out
                        .println("   Die angegebenen Kanten folgen nicht aufeinander.");
                return null;
            }
            if (givenFace) {
                System.out.println("   Gefundene Fl�che: " + face.getLabel());
            }
            return face;
        } else {
            face = null;
            return face;
        }
    }

    /**
     * Is true, if the two edges of the angle constraint are defining an angle.
     * 
     * @param face
     *            the face
     * @param edge1
     *            the first leg of the angle
     * @param edge2
     *            the second leg of the angle
     * @return if the two edges of the angle constraint are defining an angle
     */
    /*
     * �berpr�ft, ob in dieser Fl�che die eine Kante direkter Vorg�nger der
     * anderen ist.
     */
    protected boolean isAdjacent(FaceNode face, Edge edge1, Edge edge2) {
        int one = face.getEdges().indexOf(edge1);
        int two = face.getEdges().indexOf(edge2);
        int size = face.getEdges().size();
        int diff = java.lang.Math.abs((two - one + size) % size);
        return (diff == 1);
    }

    /**
     * Gets the angle constraint.
     * 
     * @return the angle constraint
     */
    public AngleConstraint getAngleConstraint() {
        int one = face.getEdges().indexOf(edge1);
        int two = face.getEdges().indexOf(edge2);
        Edge followingEdge = null;
        // Test, ob edge1 edge2 in Fl�che face nachfolgt
        // sucht vorhergehende Kante
        // two ist nachfolgende Kante
        if (two == ((one + 1) % face.getEdges().size())) {
            followingEdge = edge1;
        } else {
            followingEdge = edge2;
        }
        return new AngleConstraint(node, face, followingEdge, angle, cost);
    }

    /**
     * Gets the bend constraint.
     * 
     * @return the bend constraint
     */
    public BendConstraint getBendConstraint() {
        GraphNode n1 = list.getFirst();
        GraphNode n2 = list.get(1);
        return new BendConstraint(n1, n2, edge, bends, cost);
    }

    /**
     * Gets the <code>Type</code> of the constraint: either BEND or ANGLE.
     * 
     * @return the type of the constraint
     */
    public Type getType() {
        return type;
    }
}
