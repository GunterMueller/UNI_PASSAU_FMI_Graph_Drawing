package org.graffiti.plugins.algorithms.kandinsky;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.plugins.algorithms.kandinsky.MCMFNode.Type;

/**
 * Computes the orthogonal represenation of a graph from the MCMFNetwork.
 */
public class ComputeOrthRepresentation {

    /*
     * Format: Kante, Kantenknicke, Winkel zur n�chsten Kante der Einbettung [e;
     * 0101; 3]
     */

    /** HashMap of the graph edges and the face to their right. */
    protected Hashtable<Edge, FaceNode> rightFace;

    /** HashMap of the graph edges and the face to their left. */
    protected Hashtable<Edge, FaceNode> leftFace;

    /** The Max-Flow-Min-Cost-Network for reducing the number of bends. */
    private MCMFNetwork network;

    /** The orthogonal representation of the faces of the graph. */
    private OrthFace[] faces;

    /** The List of bend constraints. */
    private LinkedList<BendConstraint> bendList;

    /** HashMap of the graph edges and their starting points. */
    private Hashtable<Edge, GraphNode> edge_Start;

    /** HashMap of the graph edges and their target points. */
    private Hashtable<Edge, GraphNode> edge_End;

    /**
     * HashMap of the graph edges and the angle to the following edge in the
     * direction of the dart.
     */
    private Hashtable<Edge, Integer> edge_AngleInDir;

    /**
     * HashMap of the graph edges and the angle in the opposite direction of the
     * dart.
     */
    private Hashtable<Edge, Integer> edge_AngleNotDir;

    /**
     * HashMap of the graph edges and the bends in the direction of the dart.
     */
    private Hashtable<Edge, LinkedList<Boolean>> edge_BendInDir;

    /**
     * HashMap of the graph edges and the bends in the opposite direction of the
     * dart.
     */
    private Hashtable<Edge, LinkedList<Boolean>> edge_BendNotDir;

    /** Collection of edges of the graph. */
    private Collection<Edge> edges;

    /** Collection of faces of the graph. */
    private Collection<FaceNode> faceNodes;

    /** Hashtable of the devices with edges, which are in the direction. */
    private Hashtable<Edge, Device> device_InDir;

    /** Hashtable of the devices with edges, which are not in the direction. */
    private Hashtable<Edge, Device> device_NotDir;

    /**
     * True, if this orthogonal representation is constructed for a sketch
     * driven drawing.
     */
    private boolean sketch = false;

    /**
     * Computes the orthogonal represenation of a graph from the MCMFNetwork.
     * 
     * @param sketch
     *            the orthogonal representation is constructed for a sketch
     *            driven drawing.
     * @param network
     *            the MCMFNetwork which contains arcs with flow.
     * @param edgeRightFaceNode
     *            Hashtable with the right faces of the edges.
     * @param edgeLeftFaceNode
     *            Hashtable with the left faces of the edges.
     * @param bendList
     *            list of the defined bends.
     * @param edge_Start
     *            Hashtable with the starting point of the edges.
     * @param edge_End
     *            Hashtable with ending point of the edges.
     * @param edges
     *            Collection of the edges of the graph.
     */
    ComputeOrthRepresentation(boolean sketch, MCMFNetwork network,
            Hashtable<Edge, FaceNode> edgeRightFaceNode,
            Hashtable<Edge, FaceNode> edgeLeftFaceNode,
            LinkedList<BendConstraint> bendList,
            Hashtable<Edge, GraphNode> edge_Start,
            Hashtable<Edge, GraphNode> edge_End, Collection<Edge> edges) {
        this.sketch = sketch;
        this.network = network;
        this.faceNodes = network.getFaceList();
        faces = new OrthFace[faceNodes.size()];
        this.leftFace = edgeLeftFaceNode;
        this.rightFace = edgeRightFaceNode;
        this.bendList = bendList;
        this.edge_Start = edge_Start;
        this.edge_End = edge_End;
        this.edges = edges;
        edge_AngleInDir = new Hashtable<Edge, Integer>();
        edge_AngleNotDir = new Hashtable<Edge, Integer>();
        edge_BendInDir = new Hashtable<Edge, LinkedList<Boolean>>();
        edge_BendNotDir = new Hashtable<Edge, LinkedList<Boolean>>();
        device_InDir = new Hashtable<Edge, Device>();
        device_NotDir = new Hashtable<Edge, Device>();
    }

    /**
     * Computes the representation for each face.
     */
    public void computeFaceRep() {
        computeBendsAndAngles();
        int count = 0;
        for (FaceNode face : faceNodes) {
            // F�r jede Fl�che wird eine orthogonale Repr�sentation erzeugt
            OrthFace repFace = new OrthFace(face.getLabel());
            OrthEdge o = null;
            LinkedList<Edge> edgeList = face.getElement().getEdgelist();
            LinkedList<Edge> bridgeToAdd = new LinkedList<Edge>();
            int c = 0; // Position des Kante in der Liste
            // F�r jede Kante dieser Fl�che wird eine orthogonale Repr�sentation
            // davon erzeugt mit dem Winkel zur nachfolgenden Kante und den
            // Knicken darauf, so da� die Fl�che immer rechts davon liegt
            for (Edge e : edgeList) {
                FaceNode right = rightFace.get(e);
                FaceNode left = leftFace.get(e);
                if ((face == right) && (left != right))
                // Fl�che face liegt rechts der Kante
                // Keine Kante innerhalb der Fl�che
                // Kante in Pfeilrichtung einf�gen
                {
                    o = getOrthEdgeInDir(e);
                } else {
                    if ((face == left) && (left != right))
                    // Fl�che face liegt links der Kante
                    // Keine Kante innerhalb der Fl�che
                    // Kante in Pfeilrichtung einf�gen
                    {
                        o = getOrthEdgeNotDir(e);
                    }
                }
                if ((face == right) && (left == right))
                // gleiche Fl�che links und rechts von der Kante
                {
                    GraphNode node = repFace.getEndOfLastEdge();
                    if (node == null)
                    // Kante ist erste in der Kantenliste, d.h.
                    // es gibt keine Referenzrichtung einer
                    // vorhergehenden Kante --> man wei� nicht, welche Richtung
                    // man f�r die Kante w�hlen soll.
                    // Falls das mal nicht funktionieren sollte, weil die zweite
                    // Kante auch eine Br�cke ist, d.h. in die Fl�che reinragt,
                    // mu� man vor dem Durchlaufen der edgeList so lange die
                    // Kanten ans Ende setzen, bis die linke Fl�che nicht gleich
                    // der rechten ist. Trau mich jetzt nicht, das zu �ndern.
                    {
                        Edge next = edgeList.get((c + 1) % edgeList.size()); // n�chste
                        // Kante
                        if ((face == rightFace.get(next))
                                && (face != leftFace.get(next)))
                        // liegt die n�chste Kante rechts von der Fl�che und ist
                        // die linke Fl�che davon verschieden
                        {
                            if (next.getTarget() == e.getSource()) {
                                o = getOrthEdgeInDir(e);
                            } else {
                                o = getOrthEdgeNotDir(e);
                            }
                        } else {
                            bridgeToAdd.add(e);
                            o = null;
                        }
                    } else {
                        // Kante ist nicht die erste in der Kantenliste, d.h.
                        // es gibt vorher eine Referenzrichtung -->
                        // dementsprechend die Richtung der Kante w�hlen.
                        if (node.getElement() == e.getTarget()) {
                            o = getOrthEdgeInDir(e);
                        } else {
                            o = getOrthEdgeNotDir(e);
                        }
                    }
                }
                if (o != null) {
                    repFace.addEdge(o);
                }
                c++;
            }
            // Einf�gen der Br�cken vom Anfang
            while (bridgeToAdd.size() > 0) {
                Edge e = bridgeToAdd.getFirst();
                GraphNode node = repFace.getEndOfLastEdge();
                if (node == null)
                // Kante ist erste in der Kantenliste, d.h.
                // es gibt keine Referenzrichtung einer
                // vorhergehenden Kante
                {
                    o = getOrthEdgeInDir(e);
                } else {
                    if (node.getElement() == e.getTarget()) {
                        o = getOrthEdgeInDir(e);
                    } else {
                        o = getOrthEdgeNotDir(e);
                    }
                }
                repFace.addEdge(o);
                bridgeToAdd.removeFirst();
            }
            faces[count] = repFace;
            count++;
        }
    }

    /**
     * Computes the bends and angles for each edge.
     */
    private void computeBendsAndAngles() {
        // Aufteilung der Devices: mit Edges in/gegen Kantenrichtung
        for (Device device : network.deviceList) {
            if (device.getDirection()) {
                device_InDir.put(device.getEdge(), device);
            } else {
                device_NotDir.put(device.getEdge(), device);
            }
        }
        for (Edge e : edges) {
            FaceNode left = leftFace.get(e);
            FaceNode right = rightFace.get(e);
            computeAngle(right, e, true);
            computeAngle(left, e, false);
        }
        computeBends();
    }

    /**
     * Computes the angle between an edge of a face and the next edge in the
     * embedding. Given are the dart (the directed edge) and the face to its
     * right.
     * 
     * @param face
     *            the face on the right side of the dart (depends of its
     *            direction)
     * @param e
     *            the dart
     * @param direction
     *            is true if it is in the direction of the edge
     */
    /*
     * Flu� von GraphNode/AngleNode zu angrenzenden Fl�chen minus Flu� von
     * Hilfsknoten zum entsprechenden GraphNode/Winkelknoten plus 1
     */
    private void computeAngle(FaceNode face, Edge e, boolean direction) {
        int angle = 1;
        GraphNode node = null;
        // Liste, um die Kanten zu l�schen, welche die Winkel festlegen
        // wird ben�tigt, falls ein Knoten mit Grad 2 innerhalb einer Fl�che
        // liegt, damit diese Kante nur 1x gez�hlt wird
        LinkedList<MCMFArc> delete = new LinkedList<MCMFArc>();
        // GraphNode end = null;
        if (direction) {
            node = edge_Start.get(e);
            // end = edge_End.get(e);
        } else {
            node = edge_End.get(e);
            // end = edge_Start.get(e);
        }

        // bei Sketch: Flu� vom AngleNode statt GraphNode zu der Fl�che
        if (sketch) {
            for (MCMFArc a : face.getInArcs()) {
                // Startpunkt ist AngleNode,
                // Fl�che geh�rt zum AngleNode
                if ((a.getFrom().getType() == Type.ANGLE)
                        && (((AngleNode) a.getFrom()).getNode() == node)
                        && (((AngleNode) a.getFrom()).getEdge() == e)) {
                    angle += a.getFlow();
                    delete.add(a);
                }
                // bei Kandinsky: Flu� vom GraphNode zu der Fl�che
                if ((a.getFrom().getType() == Type.NODE)
                        && (a.getFrom() == node) && (a.getEdge() == e)) {
                    angle += a.getFlow();
                    delete.add(a);
                }
            }
        } else {
            // bei Kandinsky: Flu� vom GraphNode zu der Fl�che
            for (MCMFArc a : face.getInArcs()) {
                // Startpunkt ist GraphNode
                if ((a.getFrom().getType() == Type.NODE)
                        && (a.getFrom() == node) && (a.getEdge() == e)) {
                    angle += a.getFlow();
                    delete.add(a);
                }
            }
        }
        HelpNode help = node.getHelpNode(e);
        if (sketch) {
            // Flu� von Hilfsknoten zum entsprechenden AngleNode
            for (MCMFArc a : help.getOutArcs()) {
                if (((a.getTo().getType() == Type.ANGLE) || (a.getTo()
                        .getType() == Type.NODE))) {
                    angle -= a.getFlow();
                    delete.add(a);
                }
            }
        } else {
            // Flu� von Hilfsknoten zum entsprechenden GraphNode
            for (MCMFArc a : help.getOutArcs()) {
                if ((a.getTo().getType() == Type.NODE)) {
                    angle -= a.getFlow();
                    delete.add(a);
                }
            }
        }
        if (direction) {
            edge_AngleInDir.put(e, angle);
        } else {
            edge_AngleNotDir.put(e, angle);
        }
        for (MCMFArc a : delete) {
            network.removeArc(a);
        }
    }

    /*
     * 0 bzw. TRUE steht f�r Rechtsknick, 1 bzw. FALSE f�r Linksknick. Knicke
     * werden definiert durch folgende Fl�sse in folgender Reihenfolge:
     * 
     * Erstens: 0 hoch Flu� auf linkes Device, 1 hoch Flu� auf rechtes Device,
     * Flu� von Bendknoten zum entsprechenden Hilfsknoten, falls Knicke
     * vorgegeben.
     * 
     * Anschlie�end durch einen Flu� von Fl�che nach Fl�che.
     * 
     * Abschlie�end: erster Teil in entgegengesetzter Richtung der Graphkante.
     * 
     * Siehe Dissertation von Eiglsperger, S. 70 und 86
     */
    /**
     * Computes the bends for each edge. Given are the dart (the directed edge)
     * and the face to its right.
     */
    private void computeBends() {
        // vb(e, x)
        Collection<Device> devices = null;
        devices = device_InDir.values();
        MCMFArc one = null;
        MCMFArc two = null;
        Edge edge = null;
        for (Device device : devices) {
            LinkedList<Boolean> bends = new LinkedList<Boolean>();
            edge = device.getEdge();
            one = device.getOne(); // linkes Device
            two = device.getTwo(); // rechtes Device
            // vb(e, x): in Kantenrichtung von e
            for (int count = one.getFlow(); count > 0; count--) {
                bends.add(false);
            }
            for (int count = two.getFlow(); count > 0; count--) {
                bends.add(true);
            }
            // b_0 hoch x(a_e_C)
            BendConstraint constr = null;
            if (sketch && (bendList != null)) {
                for (BendConstraint c : bendList) {
                    if (c.getEdge() == edge) {
                        constr = c;
                        break; // es gibt nur einen Constraint pro Kante
                    }
                }
                if (constr != null) {
                    // Constraint bedingter Vertex-Knick
                    bends.addAll(computeVertexBends(edge, constr, true));
                }
            }

            // fb(e, x): Flu� zwischen benachbarten Fl�chen
            FaceNode left = leftFace.get(edge);
            FaceNode right = rightFace.get(edge);
            MCMFArc a1 = network.searchFFArc(right, left, edge);
            MCMFArc a2 = network.searchFFArc(left, right, edge);
            if (a1 != null) {
                for (int count = a1.getFlow(); count > 0; count--) {
                    bends.add(true);
                }
            }
            if (a2 != null) {
                for (int count = a2.getFlow(); count > 0; count--) {
                    bends.add(false);
                }
            }
            if (sketch && (constr != null)) {
                // Constraint bedingter Knick
                bends.addAll(computeFaceBends(edge, constr));
            }
            // vb(e_, x): gegen Kantenrichtung von e
            // d.h. Suche, ob auf Grund eines 0�-Winkels ein Knick am anderen
            // Ende der Kante erzeugt wurde
            device = device_NotDir.get(edge);
            if (device != null) {
                one = device.getOne();
                two = device.getTwo();
                for (int count = one.getFlow(); count > 0; count--) {
                    bends.add(true);
                }
                for (int count = two.getFlow(); count > 0; count--) {
                    bends.add(false);
                }
            }
            // b_last hoch x(a_e_C)
            if (sketch && (constr != null)) {
                // letzter VertexBend
                bends.addAll(computeVertexBends(edge, constr, false));
            }
            edge_BendInDir.put(edge, bends);

            bends = new LinkedList<Boolean>();
            // Umkehrung der Knicke f�r die Angabe der Kanten in
            // entgegengesetzter Richtung
            for (Boolean x : edge_BendInDir.get(edge)) {
                if (x) {
                    bends.addFirst(false);
                } else {
                    bends.addFirst(true);
                }
            }
            edge_BendNotDir.put(edge, bends);
        }
    }

    /**
     * Computes the bends for each edge, which are created by BendConstraints.
     * 
     * @param edge
     *            The edge of the constraint.
     * @param constr
     *            The BendConstraint.
     * @param first
     *            True, if it is the first VertexBend.
     * @return LinkedList<Boolean> The calculated bends.
     */
    /*
     * Flu� von Bendknoten zum entsprechenden Hilfsknoten, falls Vertexknicke
     * vorgegeben. Jede Kante kann an ihren Enden jeweils so einen VertexBend
     * auf Grund eines 0�-Winkels haben.
     * 
     * erster Teil in andere Richtung
     */
    private LinkedList<Boolean> computeVertexBends(Edge edge,
            BendConstraint constr, boolean first) {
        // vb(e, x)
        LinkedList<Boolean> b = new LinkedList<Boolean>();
        // b_0 hoch x(a_e_C)
        // erster VertexBend
        boolean dir = true;
        int j = 0;
        if (first) {
            if (constr.getBends().size() > 0) {
                dir = true;
                GraphNode n1 = constr.getArcNode1();
                HelpNode help_r = n1.getHelpNode(edge);
                HelpNode help_l = help_r.getOther(edge);
                for (MCMFArc arc : help_l.getInArcs()) {
                    if ((arc.getFrom().getType() == Type.BEND)
                            && (((BendNode) arc.getFrom()).getEdge() == edge)) {
                        j = arc.getFlow();
                        dir = true; // Rechtsknick
                        break;
                    }
                }
                for (MCMFArc arc : help_r.getInArcs()) {
                    if ((arc.getFrom().getType() == Type.BEND)
                            && (((BendNode) arc.getFrom()).getEdge() == edge)) {
                        j = arc.getFlow();
                        dir = false; // Linksknick
                        break;
                    }
                }
            }
        } else {
            // letzter VertexBend
            if (constr.getBends().size() > 1) {
                dir = constr.getBends().getLast();
                GraphNode n2 = constr.getArcNode2();
                HelpNode help_l = n2.getHelpNode(edge);
                HelpNode help_r = help_l.getOther(edge);
                for (MCMFArc arc : help_l.getInArcs()) {
                    if ((arc.getFrom().getType() == Type.BEND)
                            && (((BendNode) arc.getFrom()).getEdge() == edge)) {
                        j = arc.getFlow();
                        dir = true; // Rechtsknick
                        break;
                    }
                }
                for (MCMFArc arc : help_r.getInArcs()) {
                    if ((arc.getFrom().getType() == Type.BEND)
                            && (((BendNode) arc.getFrom()).getEdge() == edge)) {
                        j = arc.getFlow();
                        dir = false; // Linksknick
                        break;
                    }
                }
            }
        }
        // pro Einheit einen Knick
        // naja, es ist eigentlich nur 1 Einheit Flu� m�glich, aber ich la� das
        // mal lieber stehen
        for (int i = j; i > 0; i--) {
            b.add(dir);
        }
        return b;
    }

    /**
     * Computes the bends for each edge, which are created by flow from a
     * BendNode to a face.
     * 
     * @param edge
     *            The edge of the constraint.
     * @param constr
     *            The BendConstraint.
     * @return LinkedList<Boolean> The calculated bends.
     */
    /*
     * Flu� von BendNode nach Fl�che
     */
    private LinkedList<Boolean> computeFaceBends(Edge edge,
            BendConstraint constr) {
        // vb(e, x)
        LinkedList<Boolean> b = new LinkedList<Boolean>();
        for (int i = 0; i < constr.getBends().size(); i++) {
            boolean dir = constr.getBends().get(i);
            MCMFArc arc = null;
            String count;
            if (i == 0) {
                count = "";
            } else {
                if (i == (constr.getBends().size() - 1)) {
                    count = "_last";
                } else {
                    count = "_" + (i - 1);
                }
            }
            String label_bend = "Bend_" + constr.getArcNode1().getLabel() + "_"
                    + constr.getArcNode2().getLabel() + count;
            BendNode bend = network.searchBendNode(label_bend);
            if (bend.getOutArcs().size() != 0) {
                for (MCMFArc a : bend.getOutArcs()) {
                    if (a.getTo().getType() == Type.FACE) {
                        arc = a;
                        // pro Einheit Flu� minus 1 gibt es einen Knick
                        for (int j = (arc.getFlow() - 1); j > 0; j--) {
                            b.add(dir);
                        }
                    }
                }
            }
        }
        return b;
    }

    /**
     * Computes the orthogonal representation of an edge in its direction for
     * the face.
     * 
     * @param e
     *            The edge for which the representation is calculated.
     * @return OrthEdge The orthogonal representation of the edge.
     */
    protected OrthEdge getOrthEdgeInDir(Edge e) {
        int angle = edge_AngleInDir.get(e);
        // The bends of the edge
        LinkedList<Boolean> bends = edge_BendInDir.get(e);
        if (bends == null) {
            bends = new LinkedList<Boolean>();
        }
        GraphNode start = edge_Start.get(e);
        GraphNode end = edge_End.get(e);
        return new OrthEdge(start, end, e, bends, angle, true);
    }

    /**
     * Computes the orthogonal representation of an edge in its opposite
     * direction for the face.
     * 
     * @param e
     *            The edge for which the representation is calculated.
     * @return OrthEdge The orthogonal representation of the edge.
     */
    protected OrthEdge getOrthEdgeNotDir(Edge e) {
        int angle = edge_AngleNotDir.get(e);
        // The bends of the edge
        LinkedList<Boolean> bends = edge_BendNotDir.get(e);
        if (bends == null) {
            bends = new LinkedList<Boolean>();
        }
        GraphNode start = edge_End.get(e);
        GraphNode end = edge_Start.get(e);
        return new OrthEdge(start, end, e, bends, angle, false);
    }

    /**
     * Prints the orthogonal representation of the graph.
     */
    public void print() {
        System.out.println("Orthogonale Repr�sentation des Graphen: ");
        for (int count = 0; count < faces.length; count++) {
            OrthFace rep = faces[count];
            System.out.print("H(" + rep.getName() + ") = {");
            String string = "";
            for (OrthEdge e : rep.getEdges()) {
                string += "((" + e.getStart().getLabel() + ", ";
                string += e.getEnd().getLabel() + "), ";

                if (e.getBends().size() == 0) {
                    string += "eps";
                } else {
                    for (boolean x : e.getBends()) {
                        if (x) {
                            string += "0";
                        } else {
                            string += "1";
                        }
                    }
                }
                string += ", " + e.getAngle() + "), ";
            }
            string = string.substring(0, (string.length() - 2));
            string += "}";
            System.out.println(string);
        }
    }

    /**
     * Checks, if the orthogonal representation of the graph is correct.
     */
    public void check() {
        boolean failed = false;
        for (int count = 0; count < faces.length; count++) {
            OrthFace rep = faces[count];
            int angles = 0;
            for (OrthEdge e : rep.getEdges()) {
                int a1 = e.getAngle();
                angles += (2 - a1);
                LinkedList<Boolean> bends = e.getBends();
                for (boolean b : bends) {
                    if (b) {
                        a1 = 1;
                    } else {
                        a1 = 3;
                    }
                    angles += (2 - a1);
                }
            }
            if (rep.getName().equals("Face 0")) {
                if (angles != -4) {
                    System.out.println(rep.getName() + " falsch berechnet.");
                    failed = true;
                }
            } else {
                if (angles != 4) {
                    System.out.println(rep.getName() + " falsch berechnet.");
                    failed = true;
                }
            }
        }
        if (failed) {
            System.exit(-1);
        }
    }

    /**
     * Returns the orthogonal representation of the faces.
     * 
     * @return the array of the faces.
     */
    public OrthFace[] getFaces() {
        return faces;
    }
}
