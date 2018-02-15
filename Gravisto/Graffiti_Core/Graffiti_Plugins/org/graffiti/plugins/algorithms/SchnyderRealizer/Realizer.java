package org.graffiti.plugins.algorithms.SchnyderRealizer;

import java.util.HashMap;
import java.util.Iterator;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * This class represents a Schnyder realizer. It consists of three
 * <code>HashMap</code>s forming the trees of the realizer. Furthermore it
 * contains methods used for the algorithms of Brehm and He e.g. to flip a faces
 * or get all cw / ccw faces.
 * 
 * @author hofmeier
 */
public class Realizer implements Comparable<Realizer> {
    /** The green tree (key = child, value = parent) */
    private HashMap<Node, Node> green = new HashMap<Node, Node>();

    /** The blue tree (key = child, value = parent) */
    private HashMap<Node, Node> blue = new HashMap<Node, Node>();

    /** The red tree (key = child, value = parent) */
    private HashMap<Node, Node> red = new HashMap<Node, Node>();

    /** All edges in the green tree */
    private HashList<Edge> greenEdges = new HashList<Edge>();

    /** All edges in the blue tree */
    private HashList<Edge> blueEdges = new HashList<Edge>();

    /** All edges in the red tree */
    private HashList<Edge> redEdges = new HashList<Edge>();

    /** The drawing algorithm that created the realizer */
    private AbstractDrawingAlgorithm alg;

    /**
     * Creates a new instance of the class.
     * 
     * @param a
     *            the drawing algorithm that created the realizer.
     */
    public Realizer(AbstractDrawingAlgorithm a) {
        this.alg = a;
    }

    /**
     * Adds a node (and edge) to the green tree.
     * 
     * @param child
     *            the node to add.
     * @param parent
     *            the parent of the added node.
     */
    public void addGreen(Node child, Node parent) {
        this.greenEdges.append(this.alg.getGraph().getEdges(child, parent)
                .iterator().next());
        this.green.put(child, parent);
    }

    /**
     * Adds a node (and edge) to the blue tree.
     * 
     * @param child
     *            the node to add.
     * @param parent
     *            the parent of the added node.
     */
    public void addBlue(Node child, Node parent) {
        this.blueEdges.append(this.alg.getGraph().getEdges(child, parent)
                .iterator().next());
        this.blue.put(child, parent);
    }

    /**
     * Adds a node (and edge) to the red tree.
     * 
     * @param child
     *            the node to add.
     * @param parent
     *            the parent of the added node.
     */
    public void addRed(Node child, Node parent) {
        this.redEdges.append(this.alg.getGraph().getEdges(child, parent)
                .iterator().next());
        this.red.put(child, parent);
    }

    /**
     * Gets the path to the green root starting at a given node. This is done by
     * just walking up the tree to the root.
     * 
     * @param n
     *            the node to start with.
     * @return the path to the root.
     */
    public HashList<Node> getPathToGreenRoot(Node n) {
        HashList<Node> path = new HashList<Node>();
        Node currentNode = n;
        while (this.green.get(currentNode) != null) {
            path.append(currentNode);
            currentNode = this.green.get(currentNode);
        }
        return path;
    }

    /**
     * Gets the path to the blue root starting at a given node. This is done by
     * just walking up the tree to the root.
     * 
     * @param n
     *            the node to start with.
     * @return the path to the root.
     */
    public HashList<Node> getPathToBlueRoot(Node n) {
        HashList<Node> path = new HashList<Node>();
        Node currentNode = n;
        while (this.blue.get(currentNode) != null) {
            path.append(currentNode);
            currentNode = this.blue.get(currentNode);
        }
        return path;
    }

    /**
     * Gets the path to the red root starting at a given node. This is done by
     * just walking up the tree to the root.
     * 
     * @param n
     *            the node to start with.
     * @return the path to the root.
     */
    public HashList<Node> getPathToRedRoot(Node n) {
        HashList<Node> path = new HashList<Node>();
        Node currentNode = n;
        while (this.red.get(currentNode) != null) {
            path.append(currentNode);
            currentNode = this.red.get(currentNode);
        }
        return path;
    }

    /**
     * Compares the realizer to another realizer. This is primitively done by
     * comparing all child-parent-realitions in all three trees.
     * 
     * @param comp
     *            the realizer to compare this realizer with.
     * @return 0 if the realizers are identic, -1 else.
     */
    public int compareTo(Realizer comp) {
        if (this.green.keySet().size() != comp.green.keySet().size())
            return -1;
        if (this.blue.keySet().size() != comp.blue.keySet().size())
            return -1;
        if (this.red.keySet().size() != comp.red.keySet().size())
            return -1;

        Iterator<Node> it = this.green.keySet().iterator();
        while (it.hasNext()) {
            Node child = it.next();
            if (!(this.green.get(child) == comp.green.get(child)))
                return -1;
        }
        it = this.blue.keySet().iterator();
        while (it.hasNext()) {
            Node child = it.next();
            if (!(this.blue.get(child) == comp.blue.get(child)))
                return -1;
        }
        it = this.red.keySet().iterator();
        while (it.hasNext()) {
            Node child = it.next();
            if (!(this.red.get(child) == comp.red.get(child)))
                return -1;
        }
        return 0;
    }

    /**
     * Checks for every face if it is flippable, that means all edges are in cw
     * direction and are of three different colors.
     * 
     * @return all flippable faces.
     */
    public HashList<Face> getFlippableFaces() {
        HashList<Face> flippableFaces = new HashList<Face>();
        Iterator<Face> it = alg.getFaces().iterator();
        while (it.hasNext()) {
            Face face = it.next();
            if (this.isFlippable(face)) {
                flippableFaces.append(face);
            }
        }
        return flippableFaces;
    }

    /**
     * Returns all faces with exactly two cw edges.
     * 
     * @return all faces with two cw edges.
     */
    public HashList<Face> getFacesWithTwoCWEdges() {
        HashList<Face> facesWithTwoCWEdges = new HashList<Face>();
        Iterator<Face> it = alg.getFaces().iterator();
        while (it.hasNext()) {
            Face face = it.next();
            if (this.hasTwoClockwiseEdges(face) && !face.isOuterFace()) {
                facesWithTwoCWEdges.append(face);
            }
        }
        return facesWithTwoCWEdges;
    }

    /**
     * Returns all faces whith exactly three cw edges.
     * 
     * @return all faces whith exactly three cw edges.
     */
    public HashList<Face> getCWFaces() {
        HashList<Face> cwFaces = new HashList<Face>();
        Iterator<Face> it = alg.getFaces().iterator();
        while (it.hasNext()) {
            Face face = it.next();
            if (this.isClockwise(face) && !face.isOuterFace()) {
                cwFaces.append(face);
            }
        }
        return cwFaces;
    }

    /**
     * Checks if a face is flippable by checking if it is three-colored and if
     * all edges are directed cw.
     * 
     * @param face
     *            the face to check.
     * @return true if the face is flippable.
     */
    private boolean isFlippable(Face face) {
        return (this.isThreeColored(face) && this.isClockwise(face));
    }

    /**
     * Checks a given face if it is three-colored, that means every edge has a
     * distinct color.
     * 
     * @param f
     *            the face to check.
     * @return true if the face is three-colored.
     */
    public boolean isThreeColored(Face f) {
        int[] numOfEdges = new int[3];
        for (int i = 0; i < f.getEdges().length; i++) {
            if (this.greenEdges.contains(f.getEdges()[i])) {
                numOfEdges[AbstractDrawingAlgorithm.GREEN - 1]++;
            } else if (this.blueEdges.contains(f.getEdges()[i])) {
                numOfEdges[AbstractDrawingAlgorithm.BLUE - 1]++;
            } else if (this.redEdges.contains(f.getEdges()[i])) {
                numOfEdges[AbstractDrawingAlgorithm.RED - 1]++;
            }
        }
        return ((numOfEdges[0] == 1) && (numOfEdges[1] == 1) && (numOfEdges[2] == 1));
    }

    /**
     * Checks if a given faces is directed cw. That means all edges are directed
     * cw.
     * 
     * @param f
     *            the face to check.
     * @return true if the face is directed cw.
     */
    public boolean isClockwise(Face f) {
        if ((this.green.get(f.getNodes()[0]) == f.getNodes()[1])
                || (this.blue.get(f.getNodes()[0]) == f.getNodes()[1])
                || (this.red.get(f.getNodes()[0]) == f.getNodes()[1]))
            return false;
        if ((this.green.get(f.getNodes()[1]) == f.getNodes()[2])
                || (this.blue.get(f.getNodes()[1]) == f.getNodes()[2])
                || (this.red.get(f.getNodes()[1]) == f.getNodes()[2]))
            return false;
        if ((this.green.get(f.getNodes()[2]) == f.getNodes()[0])
                || (this.blue.get(f.getNodes()[2]) == f.getNodes()[0])
                || (this.red.get(f.getNodes()[2]) == f.getNodes()[0]))
            return false;
        return true;
    }

    /**
     * Checks if a given faces is directed ccw. That means all edges are
     * directed ccw. (Only used for the test data that is written to the DB, but
     * not for any algorithm).
     * 
     * @param f
     *            the face to check.
     * @return true if the face is directed ccw.
     */
    public boolean isCounterClockwise(Face f) {
        if ((this.green.get(f.getNodes()[1]) == f.getNodes()[0])
                || (this.blue.get(f.getNodes()[1]) == f.getNodes()[0])
                || (this.red.get(f.getNodes()[1]) == f.getNodes()[0]))
            return false;
        if ((this.green.get(f.getNodes()[2]) == f.getNodes()[1])
                || (this.blue.get(f.getNodes()[2]) == f.getNodes()[1])
                || (this.red.get(f.getNodes()[2]) == f.getNodes()[1]))
            return false;
        if ((this.green.get(f.getNodes()[0]) == f.getNodes()[2])
                || (this.blue.get(f.getNodes()[0]) == f.getNodes()[2])
                || (this.red.get(f.getNodes()[0]) == f.getNodes()[2]))
            return false;
        return true;
    }

    /**
     * Checks if a given faces is has exactly two edges, which are directed cw.
     * 
     * @param f
     *            the face to check.
     * @return true if the face has exactly two cw edges.
     */
    private boolean hasTwoClockwiseEdges(Face f) {
        int cwEdges = 0;
        if (!((this.green.get(f.getNodes()[0]) == f.getNodes()[1])
                || (this.blue.get(f.getNodes()[0]) == f.getNodes()[1]) || (this.red
                .get(f.getNodes()[0]) == f.getNodes()[1]))) {
            cwEdges++;
        }
        if (!((this.green.get(f.getNodes()[1]) == f.getNodes()[2])
                || (this.blue.get(f.getNodes()[1]) == f.getNodes()[2]) || (this.red
                .get(f.getNodes()[1]) == f.getNodes()[2]))) {
            cwEdges++;
        }
        if (!((this.green.get(f.getNodes()[2]) == f.getNodes()[0])
                || (this.blue.get(f.getNodes()[2]) == f.getNodes()[0]) || (this.red
                .get(f.getNodes()[2]) == f.getNodes()[0]))) {
            cwEdges++;
        }
        return cwEdges == 2;
    }

    /**
     * Flips a face. That means reverse the direction of each edge and give each
     * edge the succeeding color. If the "face" to flip is a separating
     * triangle, a method is called to flip the inner edges too.
     * 
     * @param face
     *            the face to flip.
     */
    public void flip(Face face) {
        int[] oldColors = { this.getColor(face.getEdges()[0]),
                this.getColor(face.getEdges()[1]),
                this.getColor(face.getEdges()[2]) };
        for (int i = 0; i < face.getNodes().length; i++) {
            if (this.getColor(face.getEdges()[i]) == AbstractDrawingAlgorithm.GREEN) {
                this.green.remove(face.getNodes()[(i + 1) % 3]);
                this.green.put(face.getNodes()[(i + 1) % 3],
                        face.getNodes()[(i + 2) % 3]);
            }
            if (this.getColor(face.getEdges()[i]) == AbstractDrawingAlgorithm.BLUE) {
                this.blue.remove(face.getNodes()[(i + 1) % 3]);
                this.blue.put(face.getNodes()[(i + 1) % 3],
                        face.getNodes()[(i + 2) % 3]);
            }
            if (this.getColor(face.getEdges()[i]) == AbstractDrawingAlgorithm.RED) {
                this.red.remove(face.getNodes()[(i + 1) % 3]);
                this.red.put(face.getNodes()[(i + 1) % 3],
                        face.getNodes()[(i + 2) % 3]);
            }
        }
        for (int i = 0; i < face.getEdges().length; i++) {
            this.addEgde(face.getEdges()[i], oldColors[(i + 2) % 3]);
        }

        if (face instanceof SeparatingTriangle) {
            SeparatingTriangle triangle = (SeparatingTriangle) face;
            this.flipInnerEdges(triangle);
        }
    }

    /**
     * Creates a copy of the realizer. The <code>HashMap</code> are deep copied,
     * while the nodes of the graph are (of course) shallow copied.
     */
    @Override
    public Realizer clone() {
        Realizer clone = new Realizer(this.alg);
        Iterator<Node> it = this.green.keySet().iterator();
        while (it.hasNext()) {
            Node key = it.next();
            clone.green.put(key, this.green.get(key));
        }
        it = this.blue.keySet().iterator();
        while (it.hasNext()) {
            Node key = it.next();
            clone.blue.put(key, this.blue.get(key));
        }
        it = this.red.keySet().iterator();
        while (it.hasNext()) {
            Node key = it.next();
            clone.red.put(key, this.red.get(key));
        }
        Iterator<Edge> it2 = this.greenEdges.iterator();
        while (it2.hasNext()) {
            clone.greenEdges.append(it2.next());
        }
        it2 = this.blueEdges.iterator();
        while (it2.hasNext()) {
            clone.blueEdges.append(it2.next());
        }
        it2 = this.redEdges.iterator();
        while (it2.hasNext()) {
            clone.redEdges.append(it2.next());
        }
        return clone;
    }

    /**
     * Gets the color of an edge, that means returns an integer value,
     * indicating the tree the edge belongs to.
     * 
     * @param e
     *            the edge to test.
     * @return 1 for GREEN, 2 for BLUE, 3 for RED.
     */
    private int getColor(Edge e) {
        if (this.greenEdges.contains(e))
            return AbstractDrawingAlgorithm.GREEN;
        else if (this.blueEdges.contains(e))
            return AbstractDrawingAlgorithm.BLUE;
        else
            return AbstractDrawingAlgorithm.RED;
    }

    /**
     * Adds an edge to a tree of a given color and before that removes it from
     * the tree it belonged to up to now.
     * 
     * @param e
     *            the edge to add (and remove)
     * @param color
     *            the color of the tree the edge will be added to.
     */
    private void addEgde(Edge e, int color) {
        this.greenEdges.remove(e);
        this.blueEdges.remove(e);
        this.redEdges.remove(e);
        if (color == AbstractDrawingAlgorithm.GREEN) {
            this.greenEdges.append(e);
        }
        if (color == AbstractDrawingAlgorithm.BLUE) {
            this.blueEdges.append(e);
        }
        if (color == AbstractDrawingAlgorithm.RED) {
            this.redEdges.append(e);
        }
    }

    /**
     * Flips the inner edges of a separating triangle, by giving each edge the
     * color preceeding the color it had up to now.
     * 
     * @param face
     *            the separating triangle whose inner edges are flipped.
     */
    public void flipInnerEdges(SeparatingTriangle face) {
        HashMap<Node, Node> tempGreen = new HashMap<Node, Node>();
        HashMap<Node, Node> tempBlue = new HashMap<Node, Node>();
        HashMap<Node, Node> tempRed = new HashMap<Node, Node>();
        Iterator<Edge> edgesIt = face.getInnerEdges().iterator();
        while (edgesIt.hasNext()) {
            Edge innerEdge = edgesIt.next();
            Node child = innerEdge.getSource();
            Node father = innerEdge.getTarget();
            if (this.greenEdges.contains(innerEdge)) {
                if (!(this.green.get(child).equals(father))) {
                    child = innerEdge.getTarget();
                    father = innerEdge.getSource();
                }
                this.greenEdges.remove(innerEdge);
                this.redEdges.append(innerEdge);
                tempRed.put(child, father);
            } else if (this.blueEdges.contains(innerEdge)) {
                if (!(this.blue.get(child).equals(father))) {
                    child = innerEdge.getTarget();
                    father = innerEdge.getSource();
                }
                this.blueEdges.remove(innerEdge);
                this.greenEdges.append(innerEdge);
                tempGreen.put(child, father);
            } else if (this.redEdges.contains(innerEdge)) {
                if (!(this.red.get(child).equals(father))) {
                    child = innerEdge.getTarget();
                    father = innerEdge.getSource();
                }
                this.redEdges.remove(innerEdge);
                this.blueEdges.append(innerEdge);
                tempBlue.put(child, father);

            }
        }
        Iterator<Node> greenIt = tempGreen.keySet().iterator();
        while (greenIt.hasNext()) {
            Node child = greenIt.next();
            this.green.put(child, tempGreen.get(child));
        }
        Iterator<Node> blueIt = tempBlue.keySet().iterator();
        while (blueIt.hasNext()) {
            Node child = blueIt.next();
            this.blue.put(child, tempBlue.get(child));
        }
        Iterator<Node> redIt = tempRed.keySet().iterator();
        while (redIt.hasNext()) {
            Node child = redIt.next();
            this.red.put(child, tempRed.get(child));
        }
    }

    /**
     * Returns all edges from the blue tree.
     * 
     * @return all edges from the blue tree.
     */
    public HashList<Edge> getBlueEdges() {
        return blueEdges;
    }

    /**
     * Returns all edges from the green tree.
     * 
     * @return all edges from the green tree.
     */
    public HashList<Edge> getGreenEdges() {
        return greenEdges;
    }

    /**
     * Returns all edges from the red tree.
     * 
     * @return all edges from the red tree.
     */
    public HashList<Edge> getRedEdges() {
        return redEdges;
    }

    /**
     * Returns the blue tree.
     * 
     * @return the blue tree.
     */
    public HashMap<Node, Node> getBlue() {
        return blue;
    }

    /**
     * Returns the green tree.
     * 
     * @return the green tree.
     */
    public HashMap<Node, Node> getGreen() {
        return green;
    }

    /**
     * Returns the red tree.
     * 
     * @return the red tree.
     */
    public HashMap<Node, Node> getRed() {
        return red;
    }
}
