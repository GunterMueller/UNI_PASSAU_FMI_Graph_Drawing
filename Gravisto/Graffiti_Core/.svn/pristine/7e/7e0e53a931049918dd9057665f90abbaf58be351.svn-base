package org.graffiti.plugins.algorithms.kandinsky;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.fpp.Face;
import org.graffiti.plugins.algorithms.kandinsky.MCMFNode.Type;
import org.graffiti.plugins.algorithms.planarity.PlanarityAlgorithm;
import org.graffiti.plugins.algorithms.planarity.TestedBicomp;
import org.graffiti.plugins.algorithms.planarity.TestedComponent;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * The interface to the Gravisto plugin mechanism. Creates an orthogonal planar
 * drawing by invoking a new <code>KandinskyAlgorithm</code>.
 * 
 * @author Sonja Zur
 */
public class KandinskyAlgorithm extends AbstractAlgorithm {

    /**
     * Stores the calculated embedding.
     */
    protected TestedGraph testedGraph = null;

    /**
     * Algorithm tests the planarity.
     */
    protected PlanarityAlgorithm planar = new PlanarityAlgorithm();

    /**
     * Algorithm computes the network flow.
     */
    protected ComputeFlow flow = new ComputeFlow();

    /**
     * Planarity is checked.
     */
    protected boolean isChecked = false;

    /**
     * If set to <code>true</code> the result of the planarity test is printed
     * as a text and the nodes and edges of the Kuratowski subgraph get colored
     */
    protected boolean GUIMode = true;

    /**
     * Creates a new network for calculating the minimum number of bends.
     */
    protected MCMFNetwork network;

    /**
     * Stores the faces of the graph.
     */
    protected Face[] faces;

    /**
     * Index of the outer face.
     */
    protected int outerfaceIndex;

    /**
     * The outer face.
     */
    protected FaceNode outerFace;

    /**
     * Stores the TestComponent of the calculated embedding.
     */
    protected TestedComponent comp = null;

    /**
     * Stores the TestBicomp of the calculated embedding.
     */
    protected TestedBicomp testBicomp;

    /**
     * The labels of a face start with 1, 0 is reserved for the outer face.
     */
    private int faceLabel = 1;

    /** HashMap of the graph edges and the face to their right. */
    private Hashtable<Edge, Face> edge_r_Face;

    /** HashMap of the graph edges and the face to their left. */
    private Hashtable<Edge, Face> edge_l_Face;

    /** HashMap of the graph edges and the face to their right. */
    protected Hashtable<Edge, FaceNode> edgeRightFaceNode;

    /** HashMap of the graph edges and the face to their left. */
    protected Hashtable<Edge, FaceNode> edgeLeftFaceNode;

    /** HashMap of the graph edges and their starting points. */
    protected Hashtable<Edge, GraphNode> edge_Start;

    /** HashMap of the graph edges and their target points. */
    protected Hashtable<Edge, GraphNode> edge_End;

    /** Sores the calculated shortest paths, if they contain a device arc. */
    private HashMap<HelpNode, LinkedList<MCMFArc>> shortestPaths = null;

    /**
     * Creates a new <code>KandinskyAlgorithm</code>
     */
    public KandinskyAlgorithm() {
        parameters = new Parameter[0];
    }

    /**
     * Returns the name of the algorithm.
     * 
     * @return The name of the algorithm.
     */
    public String getName() {
        return "Construct Kandinsky Network";
    }

    /**
     * The default value of GUIMode is true. Define GUIMode = true in order to
     * use, otherwise false.
     * 
     * @param GUIMode
     */
    public void setGUIMode(boolean GUIMode) {
        this.GUIMode = GUIMode;
    }

    /**
     * Checks the preconditions for the construction of the network. In this
     * case the only possibility of failure is an empty graph.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void check() throws PreconditionException {
        if (!isChecked) {
            planar.attach(graph);
            planar.check();
            if (!planar.isPlanar())
                throw new PreconditionException("The graph is not planar. "
                        + "Cannot construct a Kandinsky network.");

            if (GUIMode) {
                planar.check();
                planar.execute();
                testedGraph = planar.getTestedGraph();
            } else {
                testedGraph = planar.getTestedGraph();
            }
            if (graph.getNumberOfNodes() <= 0)
                throw new PreconditionException("The graph is empty. "
                        + "Cannot construct a Kandinsky network.");
            if (graph.getNumberOfNodes() == 1)
                throw new PreconditionException(
                        "The graph contains only one node. "
                                + "Do not need to construct a Kandinsky network.");
            List<TestedComponent> componentList = testedGraph
                    .getTestedComponents();
            if (componentList.size() != 1)
                throw new PreconditionException("The graph is not connected. "
                        + "Cannot calculate the faces correctly.");
            else {
                comp = componentList.get(0);
                List bicompList = comp.getTestedBicomps();
                for (Iterator j = bicompList.iterator(); j.hasNext();) {
                    // einzelne Bikomponente
                    testBicomp = (TestedBicomp) j.next();
                }
            }
        }
        isChecked = true;
    }

    /**
     * Executes the planar test in GUI mode.
     */
    public void execute() {
        if (!isChecked)
            throw new IllegalStateException("Method check() was not invoked!");
        startKandinsky();
        isChecked = false;
        computeFlow();
        // printDevice();
        transformOversaturatedDevices();
        removeObsoleteArcs();
        // printFlowArcs();
        ComputeOrthRepresentation orthRep = new ComputeOrthRepresentation(
                false, network, edgeRightFaceNode, edgeLeftFaceNode, null,
                edge_Start, edge_End, graph.getEdges());
        orthRep.computeFaceRep();
        orthRep.print();
        orthRep.check();
        CompactRepresentation compact = new CompactRepresentation(orthRep);
        compact.compact();
    }

    /**
     * Resets the algorithm.
     */
    @Override
    public void reset() {
        super.reset();
        planar.reset();
        testedGraph = null;
        flow = new ComputeFlow();
        network = null;
        edge_r_Face = null;
        edge_l_Face = null;
        edgeRightFaceNode = null;
        edgeLeftFaceNode = null;
        edge_Start = null;
        edge_End = null;
        shortestPaths = null;
        faces = null;
        java.lang.System.gc();
        faceLabel = 1;
    }

    /**
     * Initializes the network.
     */
    protected void startKandinsky() {
        graph.getListenerManager().transactionStarted(this);
        calculateFaces();
        constructNetwork();
        // printNodes();
        printFaces();
        // printArcs();
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * This method invokes <code>CalculateFace</code> in order to calculates the
     * number of faces and the outerface.
     */
    protected void calculateFaces() {
        CalculateFace calculatefaces = new CalculateFace(graph, testedGraph);
        faces = calculatefaces.getFaces();
        outerfaceIndex = calculatefaces.getOutIndex();
        edge_l_Face = calculatefaces.getEdgeLeftFace();
        edge_r_Face = calculatefaces.getEdgeRightFace();
    }

    /**
     * Executes the construction of the network.
     */
    protected void constructNetwork() {
        network = new MCMFNetwork();
        constructMCMFNodes(); // MCMFNode f�r MCMFNode und Fl�chen
        edgeLeftFaceNode = transform(edge_l_Face);
        edgeRightFaceNode = transform(edge_r_Face);
        constructMCMFArcs(); // Kanten einf�gen
        // Kandinsky-Hilfsknoten und -kanten einf�gen
        // printNodes();
        constructKandinskyDevices();
    }

    /**
     * Prints every node with its adjacent faces.
     */
    protected void printNodes() {
        System.out.println("Test: MCMFNode");
        for (GraphNode i : network.getNodeList()) {
            System.out.print("Nachbarn von " + i.getLabel() + ": ");
            GraphNode f;
            for (Edge e : i.getEdges()) {
                if (e.getSource() == i.getElement()) {
                    f = edge_End.get(e);
                } else {
                    f = edge_Start.get(e);
                }
                System.out.print(f.getLabel() + ", ");
            }
            System.out.println("Mit den Nachbarfl�chen: ");
            for (FaceNode x : i.getNeighbourFaces()) {
                System.out.print(x.getLabel() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Prints every face with its edge lists and their nodes.
     */
    protected void printFaces() {
        System.out.println();
        System.out.println("Au�enfl�che: " + getOuterFace().getLabel());
        for (FaceNode k : network.getFaceList()) {
            System.out.println(toString(k));
        }
        System.out.println();
    }

    /**
     * Prints every arc of the network.
     */
    protected void printArcs() {
        for (MCMFNode k : network.elementList) {
            for (MCMFArc e : k.getOutArcs()) {
                if (e.getCap() > 0) {
                    System.out.println(e.getLabel() + ", Kap. " + e.getCap()
                            + ", Flu� " + e.getFlow());
                }
            }
        }
        System.out.println();
    }

    /**
     * Prints every arc with positive flow of the network.
     */
    protected void printFlowArcs() {
        for (MCMFArc arc : network.getArcs()) {
            if (arc.getFlow() > 0) {
                System.out.println(arc.getLabel() + ", Kosten " + arc.getCost()
                        + ", Flu� " + arc.getFlow());
            }
        }
        System.out.println();
    }

    /**
     * Prints every arc of a device of the network.
     */
    protected void printDevice() {
        for (GraphNode node : network.getNodeList()) {
            System.out.println("Node " + node.getLabel() + ":");
            for (Device device : node.getDeviceList()) {
                System.out.println("    Kante 1: " + device.getOne().getLabel()
                        + ", Flu� " + device.getOne().getFlow() + ", Kap. "
                        + device.getOne().getCap() + ", Kosten "
                        + device.getOne().getCost());
                System.out.println("    Kante 2: " + device.getTwo().getLabel()
                        + ", Flu� " + device.getTwo().getFlow() + ", Kap. "
                        + device.getTwo().getCap() + ", Kosten "
                        + device.getTwo().getCost());
                if (device.hasThirdArc()) {
                    System.out.println("   Kante 3: "
                            + device.getThree().getLabel() + ", Flu� "
                            + device.getThree().getFlow() + ", Kap. "
                            + device.getThree().getCap() + ", Kosten "
                            + device.getThree().getCost());
                }
            }
            if (node.getDeviceList().size() == 0) {
                System.out.println("   Keine Devices, da nur Grad = 1.");
            }
        }
        System.out.println();
    }

    /**
     * Prints a face with its edge list and its nodes.
     * 
     * @param k
     *            The face node k.
     * @return description of a face as String
     */
    protected String toString(FaceNode k) {
        Face face = k.getElement();
        String description = k.getLabel() + ": ";
        description += "Knotenliste: ";
        for (Node node : face.getNodelist()) {
            description += (testBicomp.toString(node)) + ", ";
        }
        // Entfernen des letzten �berfl�ssigen Kommas
        description = description.substring(0, (description.length() - 2));
        description += " --> Kantenliste: ";
        for (Edge edge : face.getEdgelist()) {
            String label = testBicomp.toString(edge.getSource()) + " - "
                    + testBicomp.toString(edge.getTarget()) + " ";
            description += label;
        }
        description = description.substring(0, (description.length() - 1));
        return description;
    }

    /**
     * Returns the list of adjacent faces for a node.
     * 
     * @param k
     *            the MCMFNode for which the neighbour faces are calculated
     * @return the list of faces which are neighbours of a MCMFNode
     */
    protected LinkedList<FaceNode> getNeighbourFaces(GraphNode k) {
        LinkedList<FaceNode> neighbours = new LinkedList<FaceNode>();
        LinkedList<Edge> edges = new LinkedList<Edge>();
        LinkedList<Edge> orderedEdges = new LinkedList<Edge>();
        edges.addAll(k.getElement().getEdges());
        // ordnen der Kanten im Uhrzeigersinn um den Knoten
        loop: for (Iterator<Node> i = comp.getAdjacencyList(k.getElement())
                .iterator(); i.hasNext();) {
            Node other = i.next();
            for (Edge e : edges) {
                if (((e.getSource() == k.getElement()) && (e.getTarget() == other))
                        || ((e.getTarget() == k.getElement()) && (e.getSource() == other))) {
                    orderedEdges.addFirst(e);
                    continue loop;
                }
            }
        }
        k.setEdges(orderedEdges);
        // Einf�gen der linken und rechten Fl�che f�r jede Kante
        for (Edge e : k.getEdges()) {
            if (e.getSource() == k.getElement()) {
                neighbours.add(edgeRightFaceNode.get(e));
            } else {
                neighbours.add(edgeLeftFaceNode.get(e));
            }
        }
        return neighbours;
    }

    /**
     * Computes the list of adjacent faces for a face.
     * 
     * @param node
     *            the FaceNode
     */
    protected void getNeighbourFaces(FaceNode node) {
        for (Edge e : node.getElement().getEdgelist()) {
            FaceNode left = edgeLeftFaceNode.get(e);
            FaceNode right = edgeRightFaceNode.get(e);
            if (left != node) {
                node.addNeighbourFace(e, left);
            }
            if (right != node) {
                node.addNeighbourFace(e, right);
            }
        }
    }

    /**
     * Constructs the nodes of the network.
     */
    private void constructMCMFNodes() {
        // MCMFNode f�r Quelle s und Senke t
        network.createS();
        network.createT();
        // MCMFNode f�r Knoten
        for (Node node : graph.getNodes()) {
            String label = (testBicomp.toString(node)).substring(5);
            network.createGraphNode(label, node);
        }
        // MCMFNode f�r Fl�chen
        for (int i = 0; i < faces.length; i++) {
            Face f = faces[i];
            String label;
            if (i == outerfaceIndex) {
                label = "Face 0";
                outerFace = network.createFaceNode(label, 0, f);
            } else {
                label = "Face " + faceLabel;
                network.createFaceNode(label, faceLabel, f);
                faceLabel++;
            }
        }
        // Sortieren der Fl�chen nach ihrem Label, damit immer dasselbe
        // Ergebnis bei der Berechnung der Orthogonalen Repr�sentation
        // geliefert wird
        network.sortFaceList();
    }

    /**
     * Constructs the edges of the MCMF network.
     */
    /*
     * Erzeugt: Quelle --> Knoten/Fl�che, Knoten/Fl�che --> Senke, Fl�che -->
     * Fl�che
     */
    private void constructMCMFArcs() {
        computeStartAndEnd(); // Berechnen der Kantenpunkte
        // Kanten einf�gen f�r GraphNode

        for (GraphNode k : network.getNodeList()) {
            // Von Quelle zum GraphNode bzw. GraphNode zur Senke
            int cap = k.getCapSourceNode();
            if (cap > 0) {
                MCMFArc arc1 = network.createArc(network.getS(), k, cap, 0);
                MCMFArc arc2 = network.createArc(k, network.getS(), 0, 0);
                arc1.setRestArc(arc2);
                arc2.setRestArc(arc1);
            } else {
                if (cap < 0) {
                    cap *= -1;
                    MCMFArc arc1 = network.createArc(k, network.getT(), cap, 0);
                    MCMFArc arc2 = network.createArc(network.getT(), k, 0, 0);
                    arc1.setRestArc(arc2);
                    arc2.setRestArc(arc1);
                }
            }
            // Von GraphNode zu benachbarten Fl�chen
            cap = 3;
            LinkedList<FaceNode> neighbours = getNeighbourFaces(k);
            k.addNeighbourFaces(neighbours);
            for (Edge e : k.getEdges()) {
                FaceNode f = null;
                if (e.getSource() == k.getElement()) {
                    f = edgeRightFaceNode.get(e);
                } else {
                    f = edgeLeftFaceNode.get(e);
                }
                MCMFArc arc1 = network.createVFArc(k, f, e, cap, edge_Start
                        .get(e).getLabel(), edge_End.get(e).getLabel());
                arc1.setEdge(e);
                MCMFArc arc2 = network.createVFArc(f, k, e, 0, edge_Start
                        .get(e).getLabel(), edge_End.get(e).getLabel());
                arc2.setEdge(e);
                arc1.setRestArc(arc2);
                arc2.setRestArc(arc1);
            }
        }

        // Kanten einf�gen f�r Fl�chen
        for (FaceNode f : network.getFaceList()) {
            // von Quelle zum FaceNode
            int cost = 0;
            int cap = f.getCapSourceFace();
            if (cap > 0) {
                MCMFArc arc1 = network.createArc(network.getS(), f, cap, cost);
                MCMFArc arc2 = network.createArc(f, network.getS(), 0, 0);
                arc1.setRestArc(arc2);
                arc2.setRestArc(arc1);
            } else {
                if (cap < 0) {
                    cap *= -1;
                    MCMFArc arc1 = network.createArc(f, network.getT(), cap,
                            cost);
                    MCMFArc arc2 = network.createArc(network.getT(), f, 0,
                            -cost);
                    arc1.setRestArc(arc2);
                    arc2.setRestArc(arc1);
                }
            }
            // von FaceNode zu benachbarten Fl�chen
            cap = Integer.MAX_VALUE;
            cost = 1;
            getNeighbourFaces(f);
            for (Edge edge : f.getEdges()) {
                FaceNode face = f.getNeighbourFace(edge);
                if (face != null) {
                    MCMFArc arc1 = network.createFFArc(f, face, edge,
                            edge_Start.get(edge).getLabel(), edge_End.get(edge)
                                    .getLabel());
                    MCMFArc arc2 = network.searchFFArc(face, f, edge);
                    if (arc2 != null) {
                        arc1.setRestArc(arc2);
                        arc2.setRestArc(arc1);
                    }
                }
            }
        }
    }

    /**
     * Creates Kandinsky nodes and edges.
     */
    /*
     * Erzeuge f�r jede Richtung der Kante e einen Hilfknoten:
     * H_e_inKantenRichtung und H_e_gegen KantenRichtung. Erzeuge eine Kante von
     * den angrenzenden Fl�chen f und g zum jeweiligen Hilfsknoten mit Kapazit�t
     * und Kosten 1 Erzeuge eine Kante vom Hilfsknoten zum Knoten
     * 
     * Erzeuge f�r jeden adjazenten Knoten von k (zwischen Fl�chen f und g) eine
     * Kante von f zum Hilfsknoten und eine von g zum Hilfsknoten mit Kapazit�t
     * und Kosten 1 Hilfsknoten --> Knoten
     */
    private void constructKandinskyDevices() {
        // Erzeuge f�r alle NODE-MCMFNode ein Kandinsky-Konstrukt:
        // Knoten: ein Hilfsknoten f�r jede Kantenrichtung
        // Kanten: Hilfsknoten --> Knoten & Fl�che --> Hilfsknoten
        Iterator<Edge> it = graph.getEdgesIterator();
        while (it.hasNext()) {
            Edge e = it.next();
            GraphNode source = edge_Start.get(e);
            GraphNode target = edge_End.get(e);
            // Erzeuge einen Hilfsknoten f�r die eine Kantenrichtung
            HelpNode help = network.createHelpNode(source, target, e, true);
            source.addHelpNode(e, help);
            int cap = 1;
            int cost = 0;
            // Kante von Hilfsknoten zu Knoten
            MCMFArc arc1 = network.createArc(help, help.getNode(), cap, cost);
            MCMFArc arc2 = network.createArc(help.getNode(), help, 0, -cost);
            arc1.setRestArc(arc2);
            arc2.setRestArc(arc1);

            // Erzeuge einen Hilfsknoten f�r die andere Kantenrichtung
            help = network.createHelpNode(target, source, e, false);
            target.addHelpNode(e, help);
            // Kante von Hilfsknoten zu Knoten
            arc1 = network.createArc(help, help.getNode(), cap, cost);
            arc2 = network.createArc(help.getNode(), help, 0, -cost);
            arc1.setRestArc(arc2);
            arc2.setRestArc(arc1);
        }

        // Kante von Fl�che zum Hilfsknoten
        int cap = 1;
        int cost = 1;
        for (GraphNode k : network.getNodeList()) {
            LinkedList<Edge> edges = k.getEdges();
            HelpNode previous = k.getHelpNode(edges.getLast());
            for (Edge e : edges) {
                HelpNode current = k.getHelpNode(e);
                FaceNode left = null;
                FaceNode right = null;
                boolean dir = true;
                if (k.getElement() == e.getSource()) {
                    left = edgeLeftFaceNode.get(e);
                    right = edgeRightFaceNode.get(e);
                    dir = true;
                } else {
                    right = edgeLeftFaceNode.get(e);
                    left = edgeRightFaceNode.get(e);
                    dir = false;
                }
                // String currentPrevious = "Current " + current.getLabel()
                // + " und Previous " + previous.getLabel();
                // String faces = "Kante (" + edge_Start.get(e).getLabel() + ","
                // + edge_End.get(e).getLabel();
                // faces += ") mit Fl�chen links " + left.getLabel()
                // + " und rechts " + right.getLabel();
                // System.out.println(currentPrevious);
                // System.out.println(faces);
                MCMFArc one = network.createArc(left, current, cap, cost);
                one.setEdge(e);
                MCMFArc arc1 = network.createArc(current, left, 0, -cost);
                arc1.setEdge(e);
                arc1.setRestArc(one);
                one.setRestArc(arc1);
                current.setFace(right);
                MCMFArc two = network.createArc(right, previous, cap, cost);
                two.setEdge(e);
                MCMFArc arc2 = network.createArc(previous, right, 0, -cost);
                arc2.setEdge(e);
                arc2.setRestArc(two);
                two.setRestArc(arc2);
                // Zuweisen der beiden Kanten um Edge e zu einem Device
                Device device = new Device(e, dir);
                device.setOne(one);
                device.setTwo(two);
                device.setCap(cap);
                network.addDevice(k, device);
                // Pointer von einem HelpNode auf den anderen desselben Devices
                current.setOther(previous, e);
                previous.setOther(current, e);
                previous = current;
            }
        }
    }

    /**
     * Transforms a Hashtable<Edge, Face> into a Hashtable<Edge, FaceNode>.
     * 
     * @param table
     *            the Hashtable<Edge, Face>.
     * @return the Hashtable<Edge, FaceNode>.
     */
    private Hashtable<Edge, FaceNode> transform(Hashtable<Edge, Face> table) {
        Hashtable<Edge, FaceNode> newTable = new Hashtable<Edge, FaceNode>();
        for (Enumeration<Edge> el = table.keys(); el.hasMoreElements();) {
            Edge e = el.nextElement();
            FaceNode face = network.searchFace(table.get(e));
            newTable.put(e, face);
        }
        return newTable;
    }

    /**
     * Computes the flow of the min-cost network.
     */
    protected void computeFlow() {
        flow.processRequest(network);
        // System.out.println();
        // System.out.println("Ergebnis total:");
        // printArcs();
        // System.out.println("Ergebnis:");
        // printFlowArcs();
        // System.out.println();
        shortestPaths = flow.getShortestPaths();
        // printDevice();
    }

    /**
     * Transforms the flow of oversaturated devices in the min-cost network.
     */
    protected void transformOversaturatedDevices() {
        boolean transformed = true;
        do {
            transformed = false;
            for (GraphNode n : network.getNodeList()) {
                boolean changed = n.searchOverSaturatedDevice(shortestPaths,
                        network);
                transformed = (transformed || changed);
                while (changed) {
                    // System.out
                    // .println("Ergebnis nach Transformation: Wiederholung");
                    // printDevice();
                    changed = n.searchOverSaturatedDevice(shortestPaths,
                            network);
                }
            }
            if (transformed) {
                // System.out.println("Endergebnis nach Transformation:");
                // printFlowArcs();
                // printDevice();
            }
        } while (transformed);
    }

    /**
     * Removes arcs with no flow and arcs, which were created for the residual
     * network.
     */
    protected void removeObsoleteArcs() {
        Object[] arcs = network.getArcs().toArray();
        // L�sche alle Kanten, die keinen Flu� tragen.
        for (int count = 0; count < arcs.length; count++) {
            if (((MCMFArc) arcs[count]).getFlow() == 0) {
                network.removeArc((MCMFArc) arcs[count]);
            }
        }
        arcs = network.getArcs().toArray();
        // Liste der bereits gel�schten Kanten
        LinkedList<MCMFArc> deleted = new LinkedList<MCMFArc>();
        for (int count = 0; count < arcs.length; count++) {
            MCMFArc a1 = (MCMFArc) arcs[count];
            MCMFNode start = a1.getFrom();
            MCMFNode end = a1.getTo();
            MCMFArc a2 = null;
            if ((start.getType() == Type.FACE) && (end.getType() == Type.FACE)) {
                a2 = network.searchFFArc((FaceNode) end, (FaceNode) start, a1
                        .getEdge());
            } else {
                a2 = network.searchArc(end, start, a1.getEdge());
            }
            if (!deleted.contains(a1) && (a2 != null)) {
                if (a1.getFlow() > a2.getFlow()) {
                    a1.setCap(a1.getCap() + a2.getFlow());
                    a1.setFlow(a1.getFlow() - a2.getFlow());
                    deleted.add(a2);
                    network.removeArc(a2);
                    if (a1.getFlow() == 0) {
                        network.removeArc(a1);
                    }
                } else {
                    a2.setCap(a2.getCap() + a1.getFlow());
                    a2.setFlow(a2.getFlow() - a1.getFlow());
                    deleted.add(a1);
                    network.removeArc(a1);
                    if (a2.getFlow() == 0) {
                        network.removeArc(a2);
                    }
                }
            }
        }
    }

    /**
     * Computes the start end target points of an edge.
     */
    private void computeStartAndEnd() {
        edge_Start = new Hashtable<Edge, GraphNode>();
        edge_End = new Hashtable<Edge, GraphNode>();
        Iterator<Edge> it = graph.getEdgesIterator();
        while (it.hasNext()) {
            Edge e = it.next();
            GraphNode source = network.searchNode(e.getSource());
            edge_Start.put(e, source);
            GraphNode target = network.searchNode(e.getTarget());
            edge_End.put(e, target);
        }
    }

    /**
     * Returns the outer face with label "0".
     */
    protected FaceNode getOuterFace() {
        return outerFace;
    }

    /**
     * @return network the <code>MCMFNetwork</code>.
     */
    protected MCMFNetwork getMCMFNetwork() {
        return network;
    }
}
