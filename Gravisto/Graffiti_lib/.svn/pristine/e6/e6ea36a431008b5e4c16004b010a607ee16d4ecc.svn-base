package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * This class implements the original algorithm of Schnyder to draw a planar
 * graph. A canonical order is calculated by the method of "edge- contraction",
 * from this the angles of the faces are labeled, one(!) realizer is calculated
 * and the coordinates of the nodes are calculated (in the
 * <code>BarycentricRepresentation</code>).
 * 
 * @author hofmeier
 */
public class SchnyderOneRealizer extends AbstractDrawingAlgorithm {

    /**
     * Creates a new instance of the class.
     * 
     * @param g
     *            the graph to be drawn.
     * @param m
     *            the maximum of realizers (not used in here).
     */
    public SchnyderOneRealizer(Graph g, int m) {
        super(g, m);
    }

    /**
     * This method calculates a valid canonical order by the method of "edge
     * contraction". This means find a node x who has exactly two common
     * neighbors with the first outer node and remove it the following way: -
     * remove x and all incident edges - insert an edge from the first outer
     * node to every neighbor of x that was no neighbor of the first outer node
     * before. - add x to the canoical order In this implementation no nodes and
     * edges are really removed. This is all done via a counter, that counts the
     * number of common neighbors between the first outer node and every inner
     * node
     * 
     * @return the canonical order
     */
    public LinkedList<Node> getOneEdgeContractionOrder() {
        // The canonical order is saved in here
        LinkedList<Node> order = new LinkedList<Node>();
        // Counts the common neighbors between first outer node and every
        // inner node
        int[] neighborCounter = new int[this.graph.getNodes().size()];
        // Neighbors of the first outer node
        HashList<Node> neighbors = new HashList<Node>();
        // Nodes that have exactly two common neighbors with the first outer
        // node
        HashList<Node> possibleToRemove = new HashList<Node>();
        // Nodes that are already done
        HashSet<Node> finishedNodes = new HashSet<Node>();
        // The node that is currently "removed" and added to the canonical order
        Node currentNode = this.outerNodes[0];

        while (order.size() < this.graph.getNodes().size() - 3) {
            finishedNodes.add(currentNode);
            // For each neighbor of the currentNode...
            Iterator<Node> ait = this.adjacenceLists.get(currentNode)
                    .iterator();
            while (ait.hasNext()) {
                Node toAdd = ait.next();
                int toAddIndex = this.nodeIndex.get(toAdd).intValue();
                // ...if it is not already a neighbor or is allready removed...
                if (!finishedNodes.contains(toAdd)
                        && !neighbors.contains(toAdd)) {
                    // ...add it to the neighbors...
                    neighbors.append(toAdd);
                    Iterator<Node> it = this.adjacenceLists.get(toAdd)
                            .iterator();
                    int toAddCounter = 0;
                    // ...and update the neighbor counter
                    while (it.hasNext()) {
                        Node neighbor = it.next();
                        if (neighbors.contains(neighbor)) {
                            toAddCounter++;
                            int neighborIndex = this.nodeIndex.get(neighbor)
                                    .intValue();
                            neighborCounter[neighborIndex]++;
                            // Add every node whose counter is set to 2 to the
                            // nodes
                            // that can be removed in the next step
                            if ((neighborCounter[neighborIndex] == 2)
                                    && (!isOuterNode(neighbor))) {
                                possibleToRemove.append(neighbor);
                            }
                            // Remove every node whose counter is set to 3 from
                            // the nodes
                            // that can be removed in the next step
                            if (neighborCounter[neighborIndex] == 3) {
                                possibleToRemove.remove(neighbor);
                            }
                        }
                    }
                    neighborCounter[toAddIndex] = toAddCounter;
                    if ((neighborCounter[toAddIndex] == 2)
                            && (!isOuterNode(toAdd))) {
                        possibleToRemove.append(toAdd);
                    }
                }
            }

            // Choose a next node to be removed.
            currentNode = possibleToRemove.getFirst();
            possibleToRemove.remove((currentNode));
            neighbors.remove(currentNode);
            // Remove it from the list of neighbors and update the neighbor
            // counter
            ait = this.adjacenceLists.get(currentNode).iterator();
            while (ait.hasNext()) {
                Node neighbor = ait.next();
                if (neighbors.contains(neighbor)) {

                    int neighborIndex = this.nodeIndex.get(neighbor).intValue();
                    neighborCounter[neighborIndex]--;
                    if ((neighborCounter[neighborIndex] == 2)
                            && (!isOuterNode(neighbor))) {
                        possibleToRemove.append(neighbor);
                    }
                    if (neighborCounter[neighborIndex] == 1) {
                        possibleToRemove.remove(neighbor);
                    }
                }
            }
            // Add the current node to the canonical order
            order.add(currentNode);
        }
        return order;
    }

    /**
     * Method creates a realizer the following way: Every edge e has four
     * labels, that are the labels of the four angles e is part of. On the one
     * end of the edge there are two identical labels. - If this label is "1":
     * Add e to the green tree of the realizer (and direct it towards the end
     * with label "1" - If this label is "2": Add e to the blue tree of the
     * realizer (and direct it towards the end with label "2" - If this label is
     * "3": Add e to the red tree of the realizer (and direct it towards the end
     * with label "3"
     */
    protected Realizer createRealizer() {
        Realizer realizer = new Realizer(this);
        // For every edge of the graph...
        Iterator<Edge> edgeIt = this.graph.getEdgesIterator();
        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            Node source = e.getSource();
            Node target = e.getTarget();
            Face f1 = this.facesByEdges.get(e).getFirst();
            Face f2 = this.facesByEdges.get(e).getLast();

            // ...get the labels of the angles it belongs to
            int sourceIndex1 = f1.getAngle(f1.getPosition(source));
            int sourceIndex2 = f2.getAngle(f2.getPosition(source));
            int targetIndex1 = f1.getAngle(f1.getPosition(target));

            // Check if the labels at the source are equal and the edge
            // is no outer edge
            if ((sourceIndex1 == sourceIndex2) && (target != outerNodes[0])
                    && (target != outerNodes[1]) && (target != outerNodes[2])) {
                if (sourceIndex1 == GREEN) {
                    realizer.addGreen(target, source);
                }
                if (sourceIndex1 == BLUE) {
                    realizer.addBlue(target, source);
                }
                if (sourceIndex1 == RED) {
                    realizer.addRed(target, source);
                }
                e.reverse();
            }

            // Check if the labels at the target are equal and the edge
            // is no outer edge
            else if ((source != outerNodes[0]) && (source != outerNodes[1])
                    && (source != outerNodes[2])) {
                if (targetIndex1 == GREEN) {
                    realizer.addGreen(source, target);
                }
                if (targetIndex1 == BLUE) {
                    realizer.addBlue(source, target);
                }
                if (targetIndex1 == RED) {
                    realizer.addRed(source, target);
                }
            }
        }
        return realizer;
    }

    /**
     * Creates the labels of the angles of each face according to the canonocal
     * order. From the canonical order the node is known whose angle must have
     * label "1". The other angles in a face are labeled "2" and "3" in
     * counterclockwise order.
     */
    protected void enumerateAngles() {
        for (int i = this.canonicalOrder.size() - 1; i > -1; i--) {
            Node n = this.canonicalOrder.get(i);
            Iterator<Face> it = this.facesByNodes.get(n).iterator();
            while (it.hasNext()) {
                it.next().enumerateAngles(n);
            }
        }
        Iterator<Face> it = this.facesByNodes.get(outerNodes[0]).iterator();
        while (it.hasNext()) {
            it.next().enumerateAngles(outerNodes[0]);
        }
    }

    /**
     * Helper method indicating if a given node is the second or the third outer
     * node. (CAUTION: Does not (and must not) check if it is the first outer
     * node)
     * 
     * @param n
     *            the node to check
     */
    protected boolean isOuterNode(Node n) {
        return ((n == this.outerNodes[1]) || (n == this.outerNodes[2]));
    }

    /**
     * Executes the algorithm by calculating a valid canonical order, creating a
     * realizer and drawing the graph.
     */
    @Override
    public void execute() {
        // Perform the algorithm
        this.canonicalOrder = this.getOneEdgeContractionOrder();
        this.enumerateAngles();
        Realizer realizer = this.createRealizer();
        this.realizers.add(realizer);
        this.barycentricReps.add(new BarycentricRepresentation(realizer,
                this.graph, this.outerNodes));
    }
}
