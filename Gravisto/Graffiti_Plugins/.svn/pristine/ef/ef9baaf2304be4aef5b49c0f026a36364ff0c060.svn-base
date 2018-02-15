package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * This class implements a barycentric representation of a graph. It calculates
 * three barycentric coordinates from a given realizer for each of the graph`s
 * nodes n by a formula given by Schnyder which uses the length of the paths to
 * the root of each tree starting in n. For a detailled description of the
 * formula, see "Embedding planar graphs on the grid" by W. Schnyder. The class
 * also contains methods to overwrite the calculated coordinbates with new ones
 * calculated by a formula of He, which - in most cases - slightly improves the
 * result.
 * 
 * @author hofmeier
 */
public class BarycentricRepresentation {

    /** The given realizer */
    private Realizer realizer;

    /** The three outer nodes of the graph */
    private Node[] outerNodes;

    /** The graph to be drawn */
    private Graph graph;

    /**
     * A mapping of each node towards an unique integer, which indicates the
     * array position in which the nodes coordinates are saved
     */
    private HashMap<Node, Integer> nodeIndex = new HashMap<Node, Integer>();

    /** The size of the green subtree for each node */
    private int[] sizeOfGreenSubTree;

    /** The size of the blue subtree for each node */
    private int[] sizeOfBlueSubTree;

    /** The size of the red subtree for each node */
    private int[] sizeOfRedSubTree;

    /**
     * The calculated coordinates for each node: coordinates[color][nodeIndex]
     * where color is (1 = green, 2 = blue, 3 = red)
     */
    private int[][] coordinates;

    /** For He`s formula: All nodes in the green subtree of a given node */
    private HashMap<Node, HashList<Node>> nodesInGreenSubTree;

    /** For He`s formula: All nodes in the blue subtree of a given node */
    private HashMap<Node, HashList<Node>> nodesInBlueSubTree;

    /** For He`s formula: All nodes in the red subtree of a given node */
    private HashMap<Node, HashList<Node>> nodesInRedSubTree;

    /** For He`s formula: All nodes in the green area of a given node */
    private HashMap<Node, HashList<Node>> nodesInGreenArea;

    /** For He`s formula: All nodes in the blue area of a given node */
    private HashMap<Node, HashList<Node>> nodesInBlueArea;

    /** For He`s formula: All nodes in the red area of a given node */
    private HashMap<Node, HashList<Node>> nodesInRedArea;

    /** For He`s formula: All faces in the green area of a given node */
    private HashMap<Node, HashList<Face>> facesInGreenArea;

    /** For He`s formula: All faces in the blue area of a given node */
    private HashMap<Node, HashList<Face>> facesInBlueArea;

    /** For He`s formula: All faces in the red area of a given node */
    private HashMap<Node, HashList<Face>> facesInRedArea;

    /**
     * Creates a new barycentric represenation of a graph
     * 
     * @param r
     *            the given realizer
     * @param g
     *            the given graph
     * @param o
     *            the outer nodes of the graph
     */
    public BarycentricRepresentation(Realizer r, Graph g, Node[] o) {
        this.realizer = r;
        this.outerNodes = o;
        this.graph = g;
        this.sizeOfGreenSubTree = new int[this.graph.getNodes().size()];
        this.sizeOfBlueSubTree = new int[this.graph.getNodes().size()];
        this.sizeOfRedSubTree = new int[this.graph.getNodes().size()];
        this.coordinates = new int[3][this.graph.getNodes().size()];
        this.createNodeIndex();
        this.calcSizeOfSubTree();
        this.calculateCoordinates();
    }

    /**
     * As the coordinates for each nodes have to be saved in arrays, this method
     * creates a mapping of each node towards an unique integer value, which
     * indicates its position in the array.
     */
    private void createNodeIndex() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        int i = 0;
        while (nodesIt.hasNext()) {
            this.nodeIndex.put(nodesIt.next(), new Integer(i));
            i++;
        }
    }

    /**
     * Calculates the size of the green, blue and red subtree for each node.
     */
    private void calcSizeOfSubTree() {
        // For each node n ...
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            // ... get all nodes that are on the path from n to the green
            // root...
            HashList<Node> path = realizer.getPathToGreenRoot(n);
            Iterator<Node> pathIt = path.iterator();
            while (pathIt.hasNext()) {
                Node next = pathIt.next();
                Integer index = this.nodeIndex.get(next);
                if (index != null && n != next) {
                    // ...and increment the size of their green subtree by one
                    this.sizeOfGreenSubTree[index.intValue()] += 1;
                }
            }
            // Do the same for the blue and red subtree.
            path = realizer.getPathToBlueRoot(n);
            pathIt = path.iterator();
            while (pathIt.hasNext()) {
                Node next = pathIt.next();
                Integer index = this.nodeIndex.get(next);
                if (index != null && n != next) {
                    this.sizeOfBlueSubTree[index.intValue()] += 1;
                }
            }
            path = realizer.getPathToRedRoot(n);
            pathIt = path.iterator();
            while (pathIt.hasNext()) {
                Node next = pathIt.next();
                Integer index = this.nodeIndex.get(next);
                if (index != null && n != next) {
                    this.sizeOfRedSubTree[index.intValue()] += 1;
                }
            }
        }
    }

    /**
     * Method calculates the barycentric coordinates of each node by the formula
     * of Schnyder. The outer nodes get coordinates (n-2,1,0) for the first
     * outer node, (0,n-2,1) for the second outer node and (1,0,n-2) for the
     * third outer node. For a detailled description of the formula, see
     * "Embedding planar graphs on the grid" by W. Schnyder.
     */
    private void calculateCoordinates() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            int index = this.nodeIndex.get(n);
            if (n == outerNodes[0]) {
                this.coordinates[0][index] = this.graph.getNodes().size() - 2;
                this.coordinates[1][index] = 1;
            } else if (n == outerNodes[1]) {
                this.coordinates[1][index] = this.graph.getNodes().size() - 2;
                this.coordinates[2][index] = 1;
            } else if (n == outerNodes[2]) {
                this.coordinates[2][index] = this.graph.getNodes().size() - 2;
                this.coordinates[0][index] = 1;
            } else {

                Iterator<Node> greenIt = realizer.getPathToGreenRoot(n)
                        .iterator();
                Iterator<Node> blueIt = realizer.getPathToBlueRoot(n)
                        .iterator();
                Iterator<Node> redIt = realizer.getPathToRedRoot(n).iterator();
                Integer nIndex = this.nodeIndex.get(n);
                int greenCoordinate = -this.sizeOfGreenSubTree[nIndex
                        .intValue()]
                        + this.realizer.getPathToBlueRoot(n).size();
                int blueCoordinate = -this.sizeOfBlueSubTree[nIndex.intValue()]
                        + this.realizer.getPathToRedRoot(n).size();
                int redCoordinate = -this.sizeOfRedSubTree[nIndex.intValue()]
                        + this.realizer.getPathToBlueRoot(n).size();

                while (greenIt.hasNext()) {
                    int greenIndex = this.nodeIndex.get(greenIt.next());
                    redCoordinate += this.sizeOfRedSubTree[greenIndex];
                    blueCoordinate += this.sizeOfBlueSubTree[greenIndex];
                }
                while (blueIt.hasNext()) {
                    int blueIndex = this.nodeIndex.get(blueIt.next());
                    greenCoordinate += this.sizeOfGreenSubTree[blueIndex];
                    redCoordinate += this.sizeOfRedSubTree[blueIndex];
                }
                while (redIt.hasNext()) {
                    int redIndex = this.nodeIndex.get(redIt.next());
                    blueCoordinate += this.sizeOfBlueSubTree[redIndex];
                    greenCoordinate += this.sizeOfGreenSubTree[redIndex];
                }
                this.coordinates[0][index] = greenCoordinate;
                this.coordinates[1][index] = blueCoordinate;
                this.coordinates[2][index] = redCoordinate;
            }
        }
    }

    /**
     * Overwrites the former coordinates by new ones calculated by a formula of
     * He. This formula counts all cw faces and faces witch two cw edges in the
     * green, blue and red region of every node. The grid size will then be (n -
     * 1 - a) x (n - 1 - a) where a is the number of cw faces in the graph.
     * 
     * @param facesByEdges
     *            the faces of the graph saved for every edge.
     */
    public void calculateCoordinatesByHe(
            HashMap<Edge, LinkedList<Face>> facesByEdges) {
        this.nodesInGreenSubTree = new HashMap<Node, HashList<Node>>();
        this.nodesInBlueSubTree = new HashMap<Node, HashList<Node>>();
        this.nodesInRedSubTree = new HashMap<Node, HashList<Node>>();
        this.nodesInGreenArea = new HashMap<Node, HashList<Node>>();
        this.nodesInBlueArea = new HashMap<Node, HashList<Node>>();
        this.nodesInRedArea = new HashMap<Node, HashList<Node>>();
        this.facesInGreenArea = new HashMap<Node, HashList<Face>>();
        this.facesInBlueArea = new HashMap<Node, HashList<Face>>();
        this.facesInRedArea = new HashMap<Node, HashList<Face>>();
        this.calcNodesInSubTree();
        this.calcNodesInAreas();
        this.coordinates = new int[3][this.graph.getNodes().size()];

        // Get all cw faces and faces with two cw nodes.
        HashList<Face> countingFaces = this.realizer.getCWFaces();
        countingFaces.append(this.realizer.getFacesWithTwoCWEdges());

        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            Iterator<Edge> edgesIt = this.graph.getEdgesIterator();
            while (edgesIt.hasNext()) {
                Edge e = edgesIt.next();
                // Get the paths to the root for every node.
                HashList<Node> greenPath = this.realizer.getPathToGreenRoot(n);
                greenPath.remove(n);
                greenPath.append(this.outerNodes[0]);
                HashList<Node> bluePath = this.realizer.getPathToBlueRoot(n);
                bluePath.remove(n);
                bluePath.append(this.outerNodes[1]);
                HashList<Node> redPath = this.realizer.getPathToRedRoot(n);
                redPath.remove(n);
                redPath.append(this.outerNodes[2]);
                Node source = e.getSource();
                Node target = e.getTarget();
                // For every edge of the graph determine in which area it is.
                if ((this.nodesInGreenArea.get(n).contains(source))
                        || (this.nodesInGreenArea.get(n).contains(target))
                        || (redPath.contains(source) && bluePath
                                .contains(target))
                        || (redPath.contains(target) && bluePath
                                .contains(source))) {
                    Face f1 = facesByEdges.get(e).getFirst();
                    Face f2 = facesByEdges.get(e).getLast();
                    if (countingFaces.contains(f1)) {
                        this.facesInGreenArea.get(n).append(f1);
                    }
                    if (countingFaces.contains(f2)) {
                        this.facesInGreenArea.get(n).append(f2);
                    }
                } else if ((this.nodesInBlueArea.get(n).contains(source))
                        || (this.nodesInBlueArea.get(n).contains(target))
                        || (redPath.contains(source) && greenPath
                                .contains(target))
                        || (redPath.contains(target) && greenPath
                                .contains(source))) {
                    Face f1 = facesByEdges.get(e).getFirst();
                    Face f2 = facesByEdges.get(e).getLast();
                    if (countingFaces.contains(f1)) {
                        this.facesInBlueArea.get(n).append(f1);
                    }
                    if (countingFaces.contains(f2)) {
                        this.facesInBlueArea.get(n).append(f2);
                    }
                } else if ((this.nodesInRedArea.get(n).contains(source))
                        || (this.nodesInRedArea.get(n).contains(target))
                        || (bluePath.contains(source) && greenPath
                                .contains(target))
                        || (bluePath.contains(target) && greenPath
                                .contains(source))) {
                    Face f1 = facesByEdges.get(e).getFirst();
                    Face f2 = facesByEdges.get(e).getLast();
                    if (countingFaces.contains(f1)) {
                        this.facesInRedArea.get(n).append(f1);
                    }
                    if (countingFaces.contains(f2)) {
                        this.facesInRedArea.get(n).append(f2);
                    }
                }
            }
        }
        // Finally for every node count the face in each area.
        nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            int index = this.nodeIndex.get(n);

            if (n == outerNodes[0]) {
                this.coordinates[0][index] = countingFaces.size();
                this.coordinates[1][index] = 0;
            } else if (n == outerNodes[1]) {
                this.coordinates[1][index] = countingFaces.size();
                this.coordinates[2][index] = 0;
            } else if (n == outerNodes[2]) {
                this.coordinates[2][index] = countingFaces.size();
                this.coordinates[0][index] = 0;
            } else {
                this.coordinates[0][index] = this.facesInGreenArea.get(n)
                        .size();
                this.coordinates[1][index] = this.facesInBlueArea.get(n).size();
                this.coordinates[2][index] = this.facesInRedArea.get(n).size();
                if (this.coordinates[0][index] == 0) {
                    this.coordinates[0][index] = 1;
                }
                if (this.coordinates[1][index] == 0) {
                    this.coordinates[1][index] = 1;
                }
                if (this.coordinates[2][index] == 0) {
                    this.coordinates[2][index] = 1;
                }
            }

        }
    }

    /**
     * Determines for every node which nodes are lying in its green, blue and
     * red subtree. This is done by for every node walkink up the path to the
     * root and adding the node to the subtree of every node on the path.
     */
    private void calcNodesInSubTree() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            this.nodesInGreenSubTree.put(n, new HashList<Node>());
            this.nodesInBlueSubTree.put(n, new HashList<Node>());
            this.nodesInRedSubTree.put(n, new HashList<Node>());
            this.nodesInGreenArea.put(n, new HashList<Node>());
            this.nodesInBlueArea.put(n, new HashList<Node>());
            this.nodesInRedArea.put(n, new HashList<Node>());
            this.facesInGreenArea.put(n, new HashList<Face>());
            this.facesInBlueArea.put(n, new HashList<Face>());
            this.facesInRedArea.put(n, new HashList<Face>());
        }

        nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            // ... get all nodes that are on the path from n to the green
            // root...
            HashList<Node> path = realizer.getPathToGreenRoot(n);
            Iterator<Node> pathIt = path.iterator();
            while (pathIt.hasNext()) {
                Node next = pathIt.next();
                Integer index = this.nodeIndex.get(next);
                if (index != null && n != next) {
                    this.nodesInGreenSubTree.get(next).append(n);
                }
            }
            // Do the same for the blue and red subtree.
            path = realizer.getPathToBlueRoot(n);
            pathIt = path.iterator();
            while (pathIt.hasNext()) {
                Node next = pathIt.next();
                Integer index = this.nodeIndex.get(next);
                if (index != null && n != next) {
                    this.nodesInBlueSubTree.get(next).append(n);
                }
            }
            path = realizer.getPathToRedRoot(n);
            pathIt = path.iterator();
            while (pathIt.hasNext()) {
                Node next = pathIt.next();
                Integer index = this.nodeIndex.get(next);
                if (index != null && n != next) {
                    this.nodesInRedSubTree.get(next).append(n);
                }
            }
        }
    }

    /**
     * Determines for every node which nodes are lying in its green, blue and
     * red area. This is don by walking up the three paths to the roots and for
     * every node on every path: if the node is on the green path: the nodes in
     * its blue subtree are lying in the blue area, the nodes in the red subtree
     * are lying in the red area. (analogue if the node is on the blue / red
     * path)
     * 
     */
    private void calcNodesInAreas() {
        Iterator<Node> nodesIt = this.graph.getNodesIterator();
        while (nodesIt.hasNext()) {
            Node n = nodesIt.next();
            Iterator<Node> greenIt = realizer.getPathToGreenRoot(n).iterator();
            Iterator<Node> blueIt = realizer.getPathToBlueRoot(n).iterator();
            Iterator<Node> redIt = realizer.getPathToRedRoot(n).iterator();
            while (greenIt.hasNext()) {
                Node onPath = greenIt.next();
                this.nodesInRedArea.get(n).append(
                        this.nodesInRedSubTree.get(onPath));
                this.nodesInBlueArea.get(n).append(
                        this.nodesInBlueSubTree.get(onPath));
            }
            while (blueIt.hasNext()) {
                Node onPath = blueIt.next();
                this.nodesInRedArea.get(n).append(
                        this.nodesInRedSubTree.get(onPath));
                this.nodesInGreenArea.get(n).append(
                        this.nodesInGreenSubTree.get(onPath));
            }
            while (redIt.hasNext()) {
                Node onPath = redIt.next();
                this.nodesInGreenArea.get(n).append(
                        this.nodesInGreenSubTree.get(onPath));
                this.nodesInBlueArea.get(n).append(
                        this.nodesInBlueSubTree.get(onPath));
            }
        }
    }

    /**
     * Gets the one barycentric coordinate for a node
     * 
     * @param n
     *            the node
     * @param color
     *            the color of the coordinate (1 = green, 2 = blue, 3 = red)
     * @return the coordinate
     */
    public int getCoordinate(Node n, int color) {
        return this.coordinates[color - 1][this.nodeIndex.get(n)];
    }

    public void setCoordinate(Node node, int color, int coordinate) {
        this.coordinates[color - 1][this.nodeIndex.get(node)] = coordinate;
    }
}
