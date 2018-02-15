package org.graffiti.plugins.algorithms.kandinsky;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Map;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * Creates a network node for the nodes of the graph. The GraphNode stores the
 * list of its devices and checks, if there is an over-saturated one. If there
 * is such a device, the flow over it is undone and a new path is computed.
 */
public class GraphNode extends MCMFNode {

    /** The graph element is a <Code>Node</Code>. */
    private Node element;

    /** List of adjacent Edges of a node. */
    private LinkedList<Edge> listEdges;

    /** List of adjacent faces. */
    private LinkedList<FaceNode> listNeighbourFaces;

    /** List of the part left to the over-saturated device. */
    private LinkedList<Device> leftPart = new LinkedList<Device>();

    /** List of the part right to the over-saturated device. */
    private LinkedList<Device> rightPart = new LinkedList<Device>();

    /** List of devices around the node. */
    private LinkedList<Device> deviceList = new LinkedList<Device>();

    /** List of angle constraint for this node. */
    private LinkedList<FaceNode> constraintList = new LinkedList<FaceNode>();

    /**
     * Mapping of an <code>Edge</code> and its <code>HelpNode</code> around the
     * node.
     */
    private Hashtable<Edge, HelpNode> helpList = new Hashtable<Edge, HelpNode>();

    /**
     * Mapping of the <code>FaceNode</code> and its <code>AngleNode</code>
     * around the node.
     */
    private Hashtable<FaceNode, AngleNode> angleList = new Hashtable<FaceNode, AngleNode>();

    /** The capacity for the edge from the source to the <Code>GraphNode</Code>. */
    private int cap;

    /** Index of the over-saturated device. */
    private int overSaturated;

    /** Left part contains a constraint. */
    // im linken Teil ist in einem Device eine dritte Kante
    private boolean left = false;

    /** Right part contains a constraint. */
    // im rechten Teil ist in einem Device eine dritte Kante
    private boolean right = false;

    /** HashMap stores the shortest path if it contains a device arc. */
    private HashMap<HelpNode, LinkedList<MCMFArc>> shortestPaths = null;

    /** The network of <code>MCMFArcs</code>. */
    private MCMFNetwork network = null;

    /** The over-saturated device. */
    private Device overD;

    /** Old shortest path between the targetNode and the sink. Not undone. */
    // dieser Teil wird nicht r�ckg�ngig gemacht, sondern beibehalten
    private LinkedList<MCMFArc> toAdd = null;

    /**
     * Creates a network node for the nodes of the graph.
     * 
     * @param label
     *            Label of node.
     * @param node
     *            The <code>Node</code> of the graph.
     * @param id
     *            The id of the node.
     */
    public GraphNode(String label, Node node, int id) {
        super(label, Type.NODE, id);
        super.setElement(node);
        listEdges = new LinkedList<Edge>();
        listNeighbourFaces = new LinkedList<FaceNode>();
        this.element = node;
    }

    /**
     * Returns the <code>Node</code> of the graph for which the GraphNode was
     * constructed.
     * 
     * @return the <code>Node</code>.
     */
    @Override
    public Node getElement() {
        return element;
    }

    /**
     * Calculates the capacity for a node.
     * 
     * @return the capacity of an edge from the source to a node.
     */
    protected int getCapSourceNode() {
        int deg = element.getEdges().size();
        cap = 4 - deg;
        return cap;
    }

    /**
     * Sets the list of the adjacent <code>Edge</code>s.
     * 
     * @param edges
     *            the adjacent list of <code>Edge</code>s to set.
     */
    public void setEdges(LinkedList<Edge> edges) {
        this.listEdges = edges;
    }

    /**
     * Gets the list of the adjacent <code>Edge</code>s.
     */
    public LinkedList<Edge> getEdges() {
        return listEdges;
    }

    /**
     * Sets the list of the adjacent faces.
     * 
     * @param list
     *            LinkedList of the adjacent <code>FaceNode</code>s to set.
     */
    public void addNeighbourFaces(LinkedList<FaceNode> list) {
        this.listNeighbourFaces = list;
    }

    /**
     * Gets the list of the adjacent faces around the node in clockwise order.
     * 
     * @return list of <code>FaceNode</code>s
     */
    public LinkedList<FaceNode> getNeighbourFaces() {
        return listNeighbourFaces;
    }

    /**
     * Returns the list of devices around the node.
     * 
     * @return the list of <code>Device</code>s around the node.
     */
    protected LinkedList<Device> getDeviceList() {
        return deviceList;
    }

    /**
     * Adds a device to the list of devices around the node.
     * 
     * @param device
     *            the <code>Device</code> to add.
     */
    protected void addDevice(Device device) {
        deviceList.add(device);
    }

    /**
     * Returns the left part of the device list.
     * 
     * @return the leftPart.
     */
    /*
     * Enth�lt die maximale Anzahl an B�ndeln mit einer nichtleeren Kante links
     * vom �bers�ttigten Device plus einem leeren B�ndel.
     */
    protected LinkedList<Device> getLeftPart() {
        return leftPart;
    }

    /**
     * Sets the left part of the device list.
     * 
     * @param index
     *            the index of the oversaturated device.
     * 
     * @return true, if one device of the left part of the device list has a 3rd
     *         arc
     */
    /*
     * Bildet die maximale Liste der nichtleeren Devices, die links des
     * �bers�ttigten Devices liegen. F�gt zum Schlu� noch das n�chste leere
     * Device ein.
     */
    protected boolean setLeftPart(int index) {
        boolean has3rdArc = false;
        this.leftPart = new LinkedList<Device>();
        int count = index - 1;
        if (index == 0) {
            count = deviceList.size() - 1;
        }
        Device d = deviceList.get(count);
        while (!d.isEmpty()) {
            leftPart.add(d);
            if (count == 0) {
                count = deviceList.size() - 1;
            } else {
                count--;
            }
            if (d.hasThirdArc()) {
                has3rdArc = true;
            }
            d = deviceList.get(count);
        }
        leftPart.add(d);
        return has3rdArc;
    }

    /**
     * Returns the right part of the device list.
     * 
     * @return the right part.
     */
    /*
     * Enth�lt die maximale Anzahl an B�ndeln mit einer nichtleeren Kante rechts
     * vom �bers�ttigten Device plus einem leeren B�ndel.
     */
    protected LinkedList<Device> getRightPart() {
        return rightPart;
    }

    /**
     * Sets the right part of the device list.
     * 
     * @param index
     *            the index of the oversaturated device.
     * 
     * @return true, if one device of the right part of the device list has a
     *         3rd arc
     */
    /*
     * Bildet die maximale Liste der nichtleeren Devices, die rechts des
     * �bers�ttigten Devices liegen. F�gt zum Schlu� noch das n�chste leere
     * Device ein.
     */
    protected boolean setRightPart(int index) {
        boolean has3rdArc = false;
        this.rightPart = new LinkedList<Device>();
        int count = index + 1;
        if (count == deviceList.size()) {
            count = 0;
        }
        Device d = deviceList.get(count);
        while (!d.isEmpty()) {
            rightPart.add(d);
            if (count == deviceList.size() - 1) {
                count = 0;
            } else {
                count++;
            }
            if (d.hasThirdArc()) {
                has3rdArc = true;
            }
            d = deviceList.get(count);
        }
        rightPart.add(d);
        return has3rdArc;
    }

    /**
     * Scans the list of devices for an over-saturated device in a sketch driven
     * network.
     * 
     * @param shortestPaths
     *            List of shortest paths which contain HelpNodes.
     * @param network
     *            The <code>MCMFNetwork</code>.
     */
    protected boolean searchOverSaturatedDevice(
            HashMap<HelpNode, LinkedList<MCMFArc>> shortestPaths,
            MCMFNetwork network) {
        this.network = network;
        this.shortestPaths = shortestPaths;
        boolean isOversaturated = false;
        for (Device d : deviceList) {
            if (d.isOversaturated()) {
                overD = d;
                // System.out.println("�bers�ttigtes Device: "
                // + overD.getOne().getLabel() + ", "
                // + overD.getTwo().getLabel());
                overSaturated = deviceList.indexOf(overD);
                left = setLeftPart(overSaturated);
                right = setRightPart(overSaturated);
                isOversaturated = true;
                if (overD.hasThirdArc()) {
                    transformSketchDevice();
                } else {
                    transformKandinskyDevice();
                }
                left = false;
                right = false;
            }
        }
        return isOversaturated;
    }

    /*
     * Wenn man nun eine Teil--Liste mit einem �bers�ttigten
     * Kandinsky--Konstrukt transformiert, mu� man erst �berpr�fen, ob eine der
     * beiden Seiten der Liste einen Constraint definiert, d.h. eine dritte
     * Kante mit positivem Flu� besitzt. In diesem Fall ist diejenige
     * Transformation anzuwenden, welche diese Seite nicht �ndert.
     */
    /**
     * Transforms an over-saturated device in a sketch driven network.
     */
    protected void transformSketchDevice() {
        // transfCost = 0;
        if (overD.getOne().getFlow() > 0) // erste B�ndelkante hat Flu�
        {
            // System.out
            // .println("Transformation wegen Flu� auf erster und dritter
            // B�ndelkante.");
            // transformiere nach rechts und ersetze erste Kante;
            correctOversaturation(overD.getOne());
            // vorsichtshalber
            overD.getTwo().setCap(0);
        } else
        // zweite B�ndelkante hat Flu�
        {
            // System.out
            // .println("Transformation wegen Flu� auf zweiter und dritter
            // B�ndelkante.");
            correctOversaturation(overD.getTwo());
            // vorsichtshalber
            overD.getOne().setCap(0);
        }
    }

    /*
     * Wenn man nun eine Teil--Liste mit einem �bers�ttigten
     * Kandinsky--Konstrukt transformiert, mu� man erst �berpr�fen, ob eine der
     * beiden Seiten der Liste einen Constraint definiert, d.h. eine dritte
     * Kante mit positivem Flu� besitzt. In diesem Fall ist diejenige
     * Transformation anzuwenden, welche diese Seite nicht �ndert.
     */
    /**
     * Transforms an over-saturated device.
     */
    protected void transformKandinskyDevice() {
        if (left || right) {
            if (right) {
                // System.out
                // .println("Links-Transformation wegen Constraint im rechten
                // Teil");
                correctOversaturation(overD.getTwo());
            } else {
                // System.out
                // .println("Rechts-Transformation wegen Constraint im linken
                // Teil");
                correctOversaturation(overD.getOne());
            }
        } else
        // kein Constraint in einer der beiden Teillisten
        {
            if (leftPart.size() <= rightPart.size()) {
                // System.out.println("Links-Transformation, keine 3. Kante.");
                correctOversaturation(overD.getTwo());
            } else {
                // System.out.println("Rechts-Transformation, keine 3. Kante.");
                correctOversaturation(overD.getOne());
            }
        }
    }

    /**
     * Undoes the flow over the over-saturated edge and computes a new path.
     * 
     * @param arc
     *            The over-saturated arc, which has to be reset.
     */
    private void correctOversaturation(MCMFArc arc) {
        // Der Ziel-Knoten, zu dem der neue Flu� berechnet werden soll, weil
        // der alte Flu� bis dorthin r�ckg�ngig gemacht wird
        MCMFNode target = reduceFlow(arc);
        // �bers�ttigte Kante erh�lt Kapazit�t 0, damit der neue Flu� nicht
        // dar�ber laufen kann
        arc.setCap(0);
        computeNewPath(target);
    }

    /**
     * Calculates a new shortest path from the source to the calculated node.
     * 
     * @param newTarget
     *            The end point of the new arc which was calculated in order to
     *            replace the oversaturated arc. This is the
     *            <code>MCMFNode</code> to which the new shortest path has to be
     *            computed.
     */
    private void computeNewPath(MCMFNode newTarget) {
        MCMFNode node = newTarget;
        ComputeFlow flow = new ComputeFlow();
        flow.setNetwork(network, node);
        Map<MCMFNode, MCMFNode> predecessorOnPath = new HashMap<MCMFNode, MCMFNode>();
        flow.getResidualCapacity(predecessorOnPath);
        MCMFNode currentNodeOnPath = node;
        LinkedList<MCMFArc> list = new LinkedList<MCMFArc>();
        while (currentNodeOnPath.getPrev() != null) {
            MCMFNode predecessorNodeOnPath = currentNodeOnPath.getPrev();
            MCMFArc arc = network.searchArc(predecessorNodeOnPath,
                    currentNodeOnPath);
            if (arc.getCap() == 0) {
                for (MCMFArc l : network.getEdges(predecessorNodeOnPath,
                        currentNodeOnPath)) {
                    if ((l.getEdge() != overD.getEdge()) && (l.getCap() > 0)) {
                        arc = l;
                        break;
                    }
                }
            }
            list.add(arc);
            currentNodeOnPath = predecessorNodeOnPath;
        }
        // System.out.print("Neuer Flu� nach " + newTarget.getLabel() + ": ");
        augmentNewPath(list);
    }

    /**
     * Augments the flow on the calculated shortes path.
     * 
     * @param list
     *            The list of arcs, where the new flow has to be.
     */
    private void augmentNewPath(LinkedList<MCMFArc> list) {
        // System.out.print("Neuer Flu�: ");
        // for (MCMFArc arc1: list)
        // {
        // System.out.print(arc1 + ", ");
        // }
        // System.out.println();

        // Speichern des k�rzesten Weges
        LinkedList<MCMFArc> path = new LinkedList<MCMFArc>();
        // Key f�r meine Liste der k�rzesten Wege
        LinkedList<HelpNode> key = new LinkedList<HelpNode>();
        for (MCMFArc arc1 : list) {
            int resCap = 1;
            MCMFNode currentNodeOnPath = arc1.getFrom();
            MCMFNode predecessorNodeOnPath = arc1.getTo();
            if ((predecessorNodeOnPath.getType() == Type.HELP)
                    && (currentNodeOnPath.getType() == Type.FACE)) {
                key.add((HelpNode) predecessorNodeOnPath);
            }
            path.add(arc1); // ein Teil des k�rzesten Weges
            MCMFArc arc2 = arc1.getRestArc();
            if (arc1.getCap() >= resCap) {
                arc1.setFlow(arc1.getFlow() + resCap);
                arc1.setCap(arc1.getCap() - resCap);
                if (arc2 != null) {
                    arc2.setCap(arc2.getCap() + resCap);
                } else {
                    arc2 = network.createArc(currentNodeOnPath,
                            predecessorNodeOnPath, resCap, -arc1
                                    .getReducedCost());
                }
                // Fl�sse �ber Kante und R�ckw�rtskante heben sich gegenseitig
                // auf.
                if ((arc1.getFlow() > 0) && (arc2.getFlow() > 0)) {
                    int a1 = arc1.getFlow();
                    int a2 = arc2.getFlow();
                    int diff = a1 - a2;
                    if (diff > 0) {
                        // a1 hat mehr Flu�
                        arc1.setFlow(diff);
                        arc2.setFlow(0);
                    } else {
                        // a2 hat mehr Flu�
                        arc2.setFlow(-diff);
                        arc1.setFlow(0);
                    }
                }
            } else {
                System.out
                        .println("Fehler: Flu� �ber �bers�ttigte Kante von Dijkstra berechnet: "
                                + arc1 + ".");
                System.exit(-1);
                break;
            }
        }
        for (MCMFArc arc : toAdd) {
            MCMFNode currentNodeOnPath = arc.getFrom();
            MCMFNode predecessorNodeOnPath = arc.getTo();
            if ((predecessorNodeOnPath.getType() == Type.HELP)
                    && (currentNodeOnPath.getType() == Type.FACE)) {
                key.add((HelpNode) predecessorNodeOnPath);
            }
        }
        path.addAll(0, toAdd);
        // System.out.print("Kompletter Flu�: ");
        // for (MCMFArc arc1: path)
        // {
        // System.out.print(arc1);
        // }
        // System.out.println();
        if (key.size() > 0) {
            for (HelpNode h : key) {
                shortestPaths.put(h, path);
            }
        }
        // System.out.println();
    }

    /**
     * Undoes the flow over an over-saturated arc.
     * 
     * @param arc
     *            The device arc which is over-saturated.
     * @return the <code>MCMFNode</code> to which the new shortest path has to
     *         be computed.
     */
    private MCMFNode reduceFlow(MCMFArc arc) {
        // Teil vom targetNode bis zur Senke bleibt
        toAdd = new LinkedList<MCMFArc>();
        LinkedList<MCMFArc> toUndo = shortestPaths.get(arc.getTo());
        // System.out.print(arc.getTo().getLabel() + " vor undone: ");
        int index = toUndo.indexOf(arc);
        if (index == -1) {
            System.out.println(arc.getLabel()
                    + " ist nicht mehr in den shortestPaths gespeichert.");
            System.exit(-1);
        }
        index = (index - 1 + toUndo.size()) % toUndo.size();
        MCMFNode target = toUndo.get(index).getTo();
        // Weg vom Endpunkt der Vorg�nderkante nach t bleibt
        for (int i = 0; i < index; i++) {
            toAdd.add(toUndo.get(i));
        }
        toUndo.removeAll(toAdd);
        // for (MCMFArc a: toUndo)
        // {
        // System.out.print(a);
        // }
        // System.out.println();
        for (MCMFArc a : toUndo) {
            int resCap = 1;
            MCMFNode currentNodeOnPath = a.getFrom();
            MCMFNode successorNodeOnPath = a.getTo();
            MCMFArc arc1 = network.searchArc(currentNodeOnPath,
                    successorNodeOnPath);
            MCMFArc arc2 = network.searchArc(successorNodeOnPath,
                    currentNodeOnPath);
            if (arc1.getFlow() >= resCap) {
                arc1.setFlow(arc1.getFlow() - resCap);
                arc1.setCap(arc1.getCap() + resCap);
                if (arc2 != null) {
                    arc2.setCap(arc2.getCap() - resCap);
                } else {
                    System.out
                            .println("Fehler: Die entgegengesetzte Kante m��te bereits existieren.");
                    System.out
                            .println("Fehler: Wieso versucht er eine Kante zu reduzieren, die nicht existieren kann, sonst h�tte sie ja einen Restnetzkante??!?!");
                    System.exit(-1);
                }
            } else {
                if (arc1.getFlow() == 0) {
                    arc1.setCap(1);
                    arc2.setCap(arc2.getCap() - 1);
                    arc2.setFlow(arc2.getFlow() + 1);
                } else {
                    System.out
                            .println("Fehler: Flu� �ber �bers�ttigte Kante von uns berechnet.");
                }
            }
            currentNodeOnPath = successorNodeOnPath;
        }
        // System.out.print(arc.getTo().getLabel() + " nach undone: ");
        // for (MCMFArc a: toUndo)
        // {
        // System.out.print(a);
        // }
        // System.out.println();
        return target;
    }

    /**
     * Returns the HelpNode which is associated to the Edge in the helpList.
     * 
     * @param edge
     *            The <code>Edge</code> which is on the left side of the
     *            HelpNode.
     * @return the HelpNode which belongs to the Edge.
     */
    protected HelpNode getHelpNode(Edge edge) {
        return helpList.get(edge); // Hilfsknoten rechts der Kante
    }

    /**
     * Adds a HelpNode to the helpList.
     * 
     * @param edge
     *            the edge of the HelpNode.
     * @param help
     *            the HelpNode to be added to the helpList.
     */
    /*
     * Mapping des rechten Hilfknoten zu der Kante.
     */
    protected void addHelpNode(Edge edge, HelpNode help) {
        helpList.put(edge, help);
    }

    /**
     * Adds an AngleNode at this node to the angleList.
     * 
     * @param face
     *            the edge of the AngleNode.
     * @param angle
     *            the AngleNode to be added to the angleList.
     */
    /*
     * Speichert den AngleNode zu der Fl�che, deren Winkel am Knoten ge�ndert
     * werden soll.
     */
    protected void addAngleNode(FaceNode face, AngleNode angle) {
        angleList.put(face, angle);
    }

    /**
     * Returns the AngleNode which is in the face at this node.
     * 
     * @param face
     *            The <code>FaceNode</code> the AngleNode is belonging to.
     * 
     * @return the AngleNode which is in the face.
     */
    protected AngleNode getAngleNode(FaceNode face) {
        for (AngleNode a : angleList.values()) {
            if (a.getFace() == face)
                return a;
        }
        return null;
    }

    /**
     * Returns true if there was already a constraint defined for the face at
     * this node.
     * 
     * @param constraint
     *            the constraint to be checked.
     * 
     * @return true if constraint is already in the list of constraint.
     */
    protected boolean hasConstraint(FaceNode constraint) {
        return constraintList.contains(constraint);
    }

    /**
     * Adds a constraint to the list of constraints for a face at this node.
     * 
     * @param constraint
     *            the constraint to add.
     */
    protected void addConstraint(FaceNode constraint) {
        this.constraintList.add(constraint);
    }
}
