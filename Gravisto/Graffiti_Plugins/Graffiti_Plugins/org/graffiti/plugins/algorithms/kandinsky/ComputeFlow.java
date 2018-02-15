package org.graffiti.plugins.algorithms.kandinsky;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.graffiti.plugins.algorithms.kandinsky.MCMFNode.Type;

/**
 * This program solves the shortest path problem for a weighted Graph using
 * Dijkstra's Algorithm.
 */
public class ComputeFlow {

    /** Default value for a node which has not yet been visited. */
    public static final int INFINITY = Integer.MAX_VALUE;

    /** The network with the edge list and node list. */
    private MCMFNetwork network;

    /**
     * The sinkNode node. Defaults to <code>null</code>. May be set by
     * <code>setSinkNode</code>.
     */
    private MCMFNode sinkNode = null;

    /**
     * The sourceNode node. Defaults to <code>null</code>. May be set by
     * <code>setSourceNode</code>.
     */
    private MCMFNode sourceNode = null;

    /** The cost of the shortest path. */
    private int completeCost = 0;

    /** The value of the residual flow of the shortest path. */
    private int resCap = INFINITY;

    /** The value of the flow of the network. */
    private int completeFlow = 0;

    /** Stores the computed shortest paths, which contain device arcs. */
    private HashMap<HelpNode, LinkedList<MCMFArc>> shortestPaths = new HashMap<HelpNode, LinkedList<MCMFArc>>();

    /**
     * This outputs the result of the shortest path tree.
     */
    public void printShortestPath(Map<MCMFNode, MCMFNode> predecessorOnPath) {
        MCMFNode currentNodeOnPath = sinkNode;

        while (predecessorOnPath.get(currentNodeOnPath) != null) {
            MCMFNode predecessorNodeOnPath = predecessorOnPath
                    .get(currentNodeOnPath);
            System.out.print(currentNodeOnPath.getLabel() + " <--("
                    + currentNodeOnPath.getArcCap() + ", " + resCap + ")-- ");
            currentNodeOnPath = predecessorNodeOnPath;
        }
        System.out.println(currentNodeOnPath.getLabel());
        System.out.println("Der k�rzeste Weg hat die Kosten " + completeCost
                + ".");
    }

    /** Resets each vertex to default values. */
    private void clearAll() {
        Iterator<MCMFNode> itr = network.elementList.iterator();
        while (itr.hasNext()) {
            MCMFNode n = itr.next();
            n.setDist(INFINITY);
            n.setArcCap(0);
            n.setPrev(null);
            n.setKnown(false);
            n.setCount(INFINITY);
        }
        resCap = INFINITY;
    }

    /**
     * Solution to shortest path problem for a weighted directed Graph using
     * Dijkstra's algorithm.
     */
    public int getResidualCapacity(Map<MCMFNode, MCMFNode> predecessorOnPath) {
        clearAll();
        // Initialisierung
        // dist-Wert des Startknoten auf 0 setzen, restliche Knoten auf
        // unendlich
        for (MCMFNode node : network.elementList) {
            node.setDist(INFINITY);
            node.setCount(INFINITY);
        }
        sourceNode.setDist(0);
        sourceNode.setCount(0);
        sourceNode.setArcCap(INFINITY);

        // Berechnung des Dijkstra-Abstandes:
        // reducedCost(Kante) wurde mit cost(Kante) belegt
        // d(v_i) = min(d(v_i), d(v_i-1) + reducedCost(Kante(v_i-1, v_i)))

        // create a priority queue
        java.util.TreeSet<MCMFNode> priorityQueue = new java.util.TreeSet<MCMFNode>();
        priorityQueue.add(sourceNode);
        while (!priorityQueue.isEmpty()) {
            // get the next lowest-cost unknown vertex
            MCMFNode v = priorityQueue.first();
            priorityQueue.remove(v);
            v.setKnown(true);
            // now cruise its adjacency list
            Iterator<MCMFArc> itr = v.getOutArcs().iterator();
            while (itr.hasNext()) {
                MCMFArc next = itr.next();
                if (next.getCap() > 0) { // ignoriere ges�ttigte Kanten
                    // Check if the vertex is known. If not, see if we can
                    // update the value of its dist.
                    MCMFNode node = next.getTo();
                    if (!node.isKnown()) {
                        if ((v.getDist() + next.getReducedCost()) < node
                                .getDist()) {
                            priorityQueue.remove(node);
                            node.setDist(v.getDist() + next.getReducedCost());
                            node.setPrev(v);
                            node.setCount(next.getFrom().getCount() + 1);
                            node.setArcCap(next.getCap());
                            // insert the new cost into the priority queue
                            priorityQueue.add(node);
                        } else {
                            // Nimm immer k�rzesten Weg wenn gleiche Kosten
                            if ((v.getDist() + next.getReducedCost()) == node
                                    .getDist()) {
                                if ((v.getCount() + 1) < node.getCount()) {
                                    priorityQueue.remove(node);
                                    node.setDist(v.getDist()
                                            + next.getReducedCost());
                                    node.setPrev(v);
                                    node
                                            .setCount(next.getFrom().getCount() + 1);
                                    node.setArcCap(next.getCap());
                                    // insert the new cost into the
                                    // priority queue
                                    priorityQueue.add(node);
                                }
                            }
                        }
                    }
                }
            }
        }

        // Berechnung der reduzierten Kosten
        for (MCMFArc arc : network.getArcs()) {
            arc.setReducedCost(arc.getReducedCost() - arc.getTo().getDist()
                    + arc.getFrom().getDist());
        }

        MCMFNode current = sinkNode;
        // initialize the map with sourceNode node.
        predecessorOnPath.put(sourceNode, null);
        // Berechnung der Flu�es im Restnetzwerk
        resCap = sinkNode.getArcCap();
        while (current != null) {
            predecessorOnPath.put(current, current.getPrev());
            resCap = java.lang.Math.min(resCap, current.getArcCap());
            current = current.getPrev();
        }
        if (!predecessorOnPath.containsKey(sinkNode))
            return 0;
        // printShortestPath(predecessorOnPath);
        priorityQueue.clear();
        return resCap;
    }

    /**
     * Process a request.
     */
    public void processRequest(MCMFNetwork network) {
        this.network = network;
        sourceNode = network.getS();
        sinkNode = network.getT();
        // a map which contains as key-value pairs nodes with their predecessors
        // on the augmentig path.
        Map<MCMFNode, MCMFNode> predecessorOnPath = new HashMap<MCMFNode, MCMFNode>();
        // get the shortest path
        int resFlow = getResidualCapacity(predecessorOnPath);
        while (resFlow > 0) {
            augmentFlow(resFlow, predecessorOnPath);
            completeFlow += resFlow;
            resFlow = getResidualCapacity(predecessorOnPath);
        }
    }

    /* Flu�verst�rkung und Berechnung des Restnetzwerkes. */
    /**
     * Computes the augmentation of the flow and the residual network.
     * 
     * @param resCap
     *            The additional capacity.
     * @param predecessorOnPath
     *            The Map which contains the predecessors of the nodes.
     * 
     */
    private void augmentFlow(int resCap,
            Map<MCMFNode, MCMFNode> predecessorOnPath) {
        // Speichern des k�rzesten Weges
        LinkedList<MCMFArc> path = new LinkedList<MCMFArc>();
        // Key f�r meine Liste der k�rzesten Wege
        LinkedList<HelpNode> key = new LinkedList<HelpNode>();
        MCMFNode currentNodeOnPath = sinkNode;

        while (predecessorOnPath.get(currentNodeOnPath) != null) {
            MCMFNode predecessorNodeOnPath = predecessorOnPath
                    .get(currentNodeOnPath);
            MCMFArc arc1 = network.searchArc(predecessorNodeOnPath,
                    currentNodeOnPath);
            if ((predecessorNodeOnPath.getType() == Type.FACE)
                    && (currentNodeOnPath.getType() == Type.HELP)) {
                key.add((HelpNode) currentNodeOnPath);
            }
            path.add(arc1); // ein Teil des k�rzesten Weges
            MCMFArc arc2 = arc1.getRestArc();
            if (arc1.getCap() >= resCap) {
                arc1.setFlow(arc1.getFlow() + resCap);
                arc1.setCap(arc1.getCap() - resCap);
                if (arc2 != null) {
                    arc2.setCap(arc2.getCap() + resCap);
                } else {
                    System.out
                            .println("Fehler: Die entgegengesetzte Kante m��te bereits existieren.");
                    arc2 = network.createArc(currentNodeOnPath,
                            predecessorNodeOnPath, resCap, -arc1
                                    .getReducedCost());
                    arc2.setEdge(arc1.getEdge());
                    System.exit(-1);
                }
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
                        .println("Fehler: Flu� �ber �bers�ttigte Kante von Dijkstra berechnet.");
            }
            currentNodeOnPath = predecessorNodeOnPath;
        }
        if (key.size() > 0) {
            for (HelpNode h : key) {
                shortestPaths.put(h, path);
            }
            // System.out.print("K�rzester Weg: ");
            // for (MCMFArc a: path)
            // {
            // System.out.print(a + ", ");
            // }
            // System.out.println();
        }
    }

    /**
     * Returns the value of the residual flow of the shortest path.
     * 
     * @return the value of the residual flow of the shortest path.
     */
    public int getFlow() {
        return completeFlow;
    }

    /**
     * Return the cost of the residual flow of the shortest path.
     * 
     * @return the cost of the residual flow of the shortest path.
     */
    public int getCost() {
        return completeCost;
    }

    /**
     * Returns the HashMap, which stores the shortestPaths.
     * 
     * @return the shortestPaths.
     */
    public HashMap<HelpNode, LinkedList<MCMFArc>> getShortestPaths() {
        return shortestPaths;
    }

    /**
     * Sets the network.
     * 
     * @param network
     *            the network to set.
     * @param sink
     *            the MCMFNode to which the shortest path is calculated.
     */
    public void setNetwork(MCMFNetwork network, MCMFNode sink) {
        this.network = network;
        sourceNode = network.getS();
        sinkNode = sink;
    }
}
