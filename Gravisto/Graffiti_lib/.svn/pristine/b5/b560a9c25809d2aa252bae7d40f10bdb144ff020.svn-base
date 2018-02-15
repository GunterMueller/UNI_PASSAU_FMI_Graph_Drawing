/*
 * Created on Sep 7, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package org.graffiti.plugins.algorithms.fpp;

/**
 * @author Le Pham Hai Dang
 */
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.ObjectAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.planarity.TestedGraph;

/**
 * According to the determine faces this method ("CalculateOrder") decides the
 * Canonical Ordering.
 */
public class CalculateOrder {

    // ~ Instance fields
    // ========================================================
    private static String PATH = "FPPnumber";

    private static final String FACE = "FPPFace";

    private Face[] faces;

    private int outerfaceIndex;

    private int orderNodeIndex = 0;

    private int numberOfExistsFaces = 0;

    private LinkedList<OrderNode> reverseOrder;

    private OrderNode[] reverseInduction;

    private Graph graph;

    private Face outerface;

    private CalculateFace calculatefaces;

    private Edge initEdge;

    private Node v1, v2, vN;

    private Node[] nodePosition;

    private LinkedList<Face> possibleNextFaces;

    private LinkedList<Node> possibleNextNode;

    // ~ Constructors
    // ===========================================================
    /**
     * @param faces
     *            <code>Face[]</code>
     * @param outerfaceIndex
     *            <code>int</code>
     * @param graph
     *            <code>Graph</code>
     * @param calculatefaces
     *            <code>CalculateFace</code>
     * @param tGraph
     *            <code>TestedGraph</code>
     */
    public CalculateOrder(Face[] faces, int outerfaceIndex, Graph graph,
            CalculateFace calculatefaces, TestedGraph tGraph) {
        this.faces = faces;
        this.outerfaceIndex = outerfaceIndex;
        this.graph = graph;
        this.calculatefaces = calculatefaces;
        this.reverseOrder = new LinkedList<OrderNode>();
        possibleNextFaces = new LinkedList<Face>();
        possibleNextNode = new LinkedList<Node>();
        numberOfExistsFaces = calculatefaces.getNumberOfFaces();
        outerface = faces[outerfaceIndex];
        v1 = outerface.getNodelist().getFirst();
        v2 = outerface.getNodelist().getLast();
        vN = outerface.getNodelist().get(1);
        initEdge = calculatefaces.getEdge(v1, v2);
        addAttributes();
        order();
    }

    // ~ Methods
    // ================================================================
    /**
     * Add to the graph the attributes <code>sepf</code>, <code>visited</code>
     * and <code>out</code>. <code>sepf</code> is an abbreviation for separation
     * face. This method saves the nodes in an array, too. The index of the
     * array "nodePosition" is the number of the node itself. Calculate the
     * initial value of <code>sepf</code> of all nodes.
     */
    private void addAttributes() {
        Collection<Node> nodes = graph.getNodes();
        nodePosition = new Node[nodes.size()];
        int index;
        int degree;

        for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            Node current = i.next();
            /**
             * The index of the array "nodePosition" is the number of the node
             * itself.
             */
            index = new Integer(current.getString(PATH)).intValue();
            nodePosition[index] = current;

            /** Attribute of CalculateOrder */
            current.addInteger("", "FPPsepf", 0);
            current.addInteger("", "FPPvisited", 0);
            degree = current.getNeighbors().size();
            current.addInteger("", "FPPdegree", degree);
            current.addBoolean("", "FPPout", false);
            current.addInteger("", "FPPnext", -1);
            current.addInteger("", "FPPprevious", -1);

            /** Attribute of Drawing */
            current.addBoolean("", "FPPcorrect", false);
            current.addInteger("", "FPPshift", 0);
            current.addInteger("", "FPPrshift", 0);
            current.addInteger("", "FPPxInsert", 0);
            current.addInteger("", "FPPx", 0);
            current.addInteger("", "FPPy", 0);

        }

        /** Guarantee that all edges has the attribute <code>FPPout</code> */
        Collection<Edge> edges = graph.getEdges();
        for (Iterator<Edge> i = edges.iterator(); i.hasNext();) {
            Edge edge = i.next();
            edge.addBoolean("", "FPPout", false);
        }

        for (Iterator<Edge> i = outerface.getEdgelist().iterator(); i.hasNext();) {
            Edge edge = i.next();
            edge.setBoolean("FPPout", true);
        }

        Node[] outArray = outerface.getNodelist().toArray(new Node[0]);
        for (int i = 0; i < outArray.length; i++) {
            outArray[i].setBoolean("FPPout", true);
            int j = i + 1;
            if (j == outArray.length) {
                j = 0;
            }
            addNode(outArray[i], outArray[j]);
        }

        /** Calculate the initial value of <code>sepf</code> of all nodes. */
        for (int i = 0; i < faces.length; i++) {
            Face face = faces[i];
            face.setOutv(0);
            face.setOute(0);
            for (Iterator<Node> j = face.getNodelist().iterator(); j.hasNext();) {
                Node current = j.next();
                if (current.getBoolean("FPPout")) {
                    face.incrementOutv();
                }
            }
            for (Iterator<Edge> j = face.getEdgelist().iterator(); j.hasNext();) {
                Edge edge = j.next();
                if (edge.getBoolean("FPPout") && edge != initEdge) {
                    face.incrementOute();
                }
            }
        }

        for (Iterator<Node> i = graph.getNodes().iterator(); i.hasNext();) {
            Node current = i.next();
            sepf(current);
        }
    }

    /**
     * The Node <code>addNode</code> becomes part of the outerface. The element
     * is inserted and becomes the successor of <code>current</code>.
     * 
     * @param current
     *            <code>Node</code>
     * @param addNode
     *            <code>Node</code>
     * 
     * @return true, if <code>addNode</code> is added, otherwise false.
     */
    protected boolean addNode(Node current, Node addNode) {
        if (current == null || addNode == null)
            return false;
        int successorIndex = current.getInteger("FPPnext");
        int currentIndex = new Integer(current.getString(PATH)).intValue();
        int addNodeIndex = new Integer(addNode.getString(PATH)).intValue();
        if (successorIndex == -1) // current hat keinen Nachfolger
        {
            current.setInteger("FPPnext", addNodeIndex);
            addNode.setInteger("FPPprevious", currentIndex);
        } else
        // current has a successor
        {
            Node successor = nodePosition[successorIndex];

            current.setInteger("FPPnext", addNodeIndex);
            addNode.setInteger("FPPprevious", currentIndex);
            addNode.setInteger("FPPnext", successorIndex);
            successor.setInteger("FPPprevious", addNodeIndex);
        }
        return true;
    }

    /**
     * Remove the Node "current" from the outerface
     * 
     * @param current
     *            <code>Node</code> will be deleted
     * 
     * @return <code>true</code>, if <code>current</code> is deleted, otherwise
     *         <code>false</code>.
     */
    protected boolean removeNode(Node current) {
        if (current == null)
            return false;
        int succ = current.getInteger("FPPnext");
        int pred = current.getInteger("FPPprevious");
        if (succ != -1) // current has a successor
        {
            if (pred != -1) // current has a successor and a predecessor
            {
                nodePosition[pred].setInteger("FPPnext", succ);
                nodePosition[succ].setInteger("FPPprevious", pred);
                current.setInteger("FPPnext", -1);
                current.setInteger("FPPprevious", -1);
            } else
            // current has ONLY a sucessor
            {
                current.setInteger("FPPnext", -1);
                nodePosition[succ].setInteger("FPPprevious", -1);
            }
        } else
        // current has NOT a sucessor
        {
            if (pred != -1) // current has ONLY a predecessor
            {
                current.setInteger("FPPprevious", -1);
                nodePosition[pred].setInteger("FPPnext", -1);
            } else {
            }
        }
        return true;
    }

    /**
     * This method assigns which node or handle will be the next in our reverse
     * order.
     */
    private void order() {
        Node node;
        boolean done;
        updateNode(vN);
        while (numberOfExistsFaces > 1) {
            while (!possibleNextFaces.isEmpty()) {
                Face face = possibleNextFaces.removeFirst();
                if (nextFace(face) && faces[face.getIndex()] != null) {
                    updateHandle(face);
                }
            }
            done = false;
            while (!possibleNextNode.isEmpty() && !done) {
                node = possibleNextNode.removeFirst();
                if (node.getInteger("FPPnext") != -1 && isNextNode(node)) {
                    updateNode(node);
                    done = true;
                }
            }
        }
        OrderNode sec = new OrderNode(v2, ++orderNodeIndex);
        OrderNode first = new OrderNode(v1, ++orderNodeIndex);
        reverseOrder.add(sec);
        reverseOrder.add(first);
        getReverseInduction(reverseOrder);
    }

    /**
     * 
     * The variable sepf(v) for every vertex v, denoting the number of incident
     * faces of v, containing a separation pair.
     * 
     * @param node
     *            <code>Node</code>
     * @return true, if <code>node</code> gets his sepf value, otherwise false.
     */
    private boolean sepf(Node node) {
        int value, index;
        Face face;
        LinkedList<Integer> facelist;
        if (node == null)
            return false;

        facelist = ((NodeAttributes) ((ObjectAttribute) node.getAttribute(FACE))
                .getValue()).getFaces();
        value = 0;
        Iterator<Integer> i = facelist.iterator();
        while (i.hasNext()) {
            index = i.next();
            face = faces[index];
            if (face == null) {
                i.remove();
            } else {
                if (isSepf(face) && index != outerfaceIndex) {
                    value++;
                    possibleNextFaces.add(face);
                    if (!face.getIsSepf()) {
                        for (Iterator<Node> j = face.getNodelist().iterator(); j
                                .hasNext();) {
                            Node current = j.next();
                            int sepf = current.getInteger("FPPsepf");
                            sepf++;
                            current.setInteger("FPPsepf", sepf);
                        }
                    }
                    face.setIsSepf(true);
                    possibleNextFaces.add(face);
                }
                if (!isSepf(face) && face.getIsSepf()
                        && index != outerfaceIndex) {
                    for (Iterator<Node> j = face.getNodelist().iterator(); j
                            .hasNext();) {
                        Node current = j.next();
                        int sepf = current.getInteger("FPPsepf");
                        sepf--;
                        current.setInteger("FPPsepf", sepf);
                    }
                    face.setIsSepf(false);
                }
                possibleNextFaces.add(face);
            }
            node.setInteger("FPPsepf", value);
        }
        ((NodeAttributes) ((ObjectAttribute) node.getAttribute(FACE))
                .getValue()).setFaceList(facelist);
        return true;
    }

    /**
     * @param face
     *            <code>Face</code>
     * @return <code>true</code>, if <code>face</code> a separtion face,
     *         otherwise <code>false</code>.
     * 
     *         Face F is a separtion face if outv(F) >= 3 or outv(F) == 2 and
     *         oute(F) == 0
     */
    private boolean isSepf(Face face) {
        boolean cond1 = face.getOutv() >= 3;
        boolean cond2 = face.getOutv() == 2 && face.getOute() == 0;
        if (cond1 || cond2)
            return true;
        return false;
    }

    /**
     * 
     * @param face
     *            <code>Face</code>
     * @return <code>true</code>, if the condition outv(F) = oute(F) + 1 and
     *         oute(F) >= 2 is hold, otherwise <code>false</code>.
     * 
     *         Every face F with outv(F) = oute(F) + 1 and oute(F) >= 2 can be
     *         the next face in our ordering
     */
    private boolean nextFace(Face face) {
        if (face == null || face.getIndex() == outerfaceIndex)
            return false;
        return (face.getOutv() == (face.getOute() + 1))
                && (face.getOute() >= 2);
    }

    /**
     * @param current
     *            <code>Node</code>
     * @return true, if current is the next Node in our Ordering, otherwise
     *         false
     */
    private boolean isNextNode(Node current) {
        int visited = current.getInteger("FPPvisited");
        int sepf = current.getInteger("FPPsepf");
        boolean unequal = current != v1 & current != v2;
        if (unequal && (sepf == 0) && (visited >= 1))
            return true;
        return false;
    }

    /**
     * @param vertex
     *            <code>Node</code>
     * 
     *            1. remove(vertex) from the outerface and calculate rightvertex
     *            and leftvertex from vertex 2. All neighbours increment their
     *            visited value and decrement their degree value 3. Calculate
     *            the new Border of the outerface, when vertex will be delete 4.
     *            All nodes, which become part of the outerface, calculate their
     *            separation face value ( sepf(v))
     */
    private boolean updateNode(Node vertex) {
        /**
         * 1. remove(vertex) from the outerface and calculate rightvertex and
         * leftvertex from vertex 2. All neighbours increment their visited
         * value and decrement their degree value 3. Calculate the new Border of
         * the outerface, when vertex will be delete 4. All nodes, which become
         * part of the outerface, calculate their separation face value (
         * sepf(v))
         */
        Node left, right;
        OrderNode oNode = new OrderNode(vertex, ++orderNodeIndex);
        LinkedList<Node> newBorder;

        /**
         * 1. remove(vertex) from the outerface and calculate rightvertex and
         * leftvertex from vertex
         */
        left = nodePosition[vertex.getInteger("FPPprevious")];
        right = nodePosition[vertex.getInteger("FPPnext")];
        oNode.setLeftvertex(left);
        oNode.setRightvertex(right);
        removeNode(vertex);
        vertex.setBoolean("FPPout", false);

        /**
         * 2. All neighbours increment their visited value and decrement their
         * degree value
         */
        for (Iterator<Node> i = vertex.getNeighborsIterator(); i.hasNext();) {
            Node current = i.next();
            incrementVisited(current);
            int degree = current.getInteger("FPPdegree") - 1;
            current.setInteger("FPPdegree", degree);
        }

        /**
         * 3. Calculate the new Border of the outerface, when vertex will be
         * delete
         */
        newBorder = getNewBorder(vertex, oNode);

        /**
         * 4. All nodes, which become part of the outerface, calculate their
         * separation face value ( sepf(v))
         */
        for (Iterator<Node> i = newBorder.iterator(); i.hasNext();) {
            Node current = i.next();
            possibleNextNode.add(current);
            sepf(current);
        }
        possibleNextNode.add(left);
        possibleNextNode.add(right);
        reverseOrder.add(oNode);
        return true;
    }

    /**
     * @param vertex
     *            <code>Node</code>
     * @param oNode
     *            <code>OrderNode</code>
     * 
     * @return the new part of the outerface as a <code>LinkedList</code>.
     *         Calculate the new part outerface when "node" will be deleted. All
     *         faces, which contain vertex, will be deleted (apart from
     *         outerface) Calculate the new Border and update of oute(face) and
     *         outv(face), which belong the outerface.
     */
    private LinkedList<Node> getNewBorder(Node vertex, OrderNode oNode) {
        int value;
        Face face;
        String next, previous;
        LinkedList<Node> newBorder = new LinkedList<Node>();
        Node left = oNode.getLeftvertex();
        Node right = oNode.getRightvertex();
        Node current, nextNode = null;
        Edge leftEdge, nextEdge;
        int edgeIndex = -1;
        boolean done = false;

        /**
         * All faces, which contain vertex, will be deleted (apart from
         * outerface)
         */
        NodeAttributes attr = (NodeAttributes) vertex.getAttribute(FACE)
                .getValue();
        LinkedList<Integer> facelist = attr.getFaces();
        for (Iterator<Integer> i = facelist.iterator(); i.hasNext();) {
            value = i.next();
            face = faces[value];
            if (value != outerfaceIndex && face != null) {
                if (face.getIsSepf()) {
                    for (Iterator<Node> j = face.getNodelist().iterator(); j
                            .hasNext();) {
                        Node node = j.next();
                        int sepf = node.getInteger("FPPsepf");
                        sepf--;
                        node.setInteger("FPPsepf", sepf);
                    }
                    face.setIsSepf(true);
                }
                faces[value] = null;
                numberOfExistsFaces--;
            }
        }

        /** Calculate the successornode of vertex relative to left */
        next = left.getString(PATH) + "FPPnext";
        previous = left.getString(PATH) + "FPPprevious";
        leftEdge = calculatefaces.getEdge(vertex, left);
        nextEdge = calculatefaces.getEdge(leftEdge.getInteger(next));
        nextEdge.setBoolean("FPPout", true);
        updateOutE(nextEdge);
        calculatefaces.removeEdge(leftEdge, next, previous);
        updateFace(nextEdge);

        /** Delete all edges, which belong to vertex */
        for (Iterator<Edge> i = vertex.getEdgesIterator(); i.hasNext();) {
            Edge edge = i.next();
            next = vertex.getString(PATH) + "FPPnext";
            previous = vertex.getString(PATH) + "FPPprevious";
            edge.setInteger(next, -1);
            edge.setInteger(previous, -1);
            Node node = calculatefaces.getOppositeNode(vertex, edge);

            next = node.getString(PATH) + "FPPnext";
            previous = node.getString(PATH) + "FPPprevious";
            calculatefaces.removeEdge(edge, next, previous);
        }

        /**
         * Calculate the new Border and increment the oute and outv, which
         * belong to the outerface.
         */
        nextNode = left;
        while (!done) {
            current = nextNode;
            nextNode = calculatefaces.getOppositeNode(nextNode, nextEdge);
            if (nextNode == right) {
                done = true;
            } else {
                addNode(current, nextNode);
                newBorder.add(nextNode);
                nextNode.setBoolean("FPPout", true);
                updateOutV(nextNode);

                next = nextNode.getString(PATH) + "FPPnext";
                edgeIndex = nextEdge.getInteger(next);
                nextEdge = calculatefaces.getEdge(edgeIndex);
                nextEdge.setBoolean("FPPout", true);
                updateOutE(nextEdge);
            }
        }
        updateFace(nextEdge);
        return newBorder;
    }

    /** Increment the <code>visited</code>-value of <code>node</code> */
    private void incrementVisited(Node node) {
        int value = node.getInteger("FPPvisited");
        node.setInteger("FPPvisited", ++value);
    }

    /**
     * Increase the value outv of all faces from node.
     * 
     * @param node
     *            <code>Node</code> (owner)
     * 
     */
    private void updateOutV(Node node) {
        ObjectAttribute objAttr = (ObjectAttribute) node.getAttribute(FACE);
        NodeAttributes nodeAttr = (NodeAttributes) objAttr.getObject();
        for (Iterator<Integer> i = nodeAttr.getFaces().iterator(); i.hasNext();) {
            int faceIndex = i.next();
            Face face = faces[faceIndex];
            if (face != null && faceIndex != outerfaceIndex) {
                face.incrementOutv();
            }
        }
    }

    /**
     * Increase the value oute of all faces from edge
     * 
     * @param edge
     *            <code>Edge</code>
     */
    private void updateOutE(Edge edge) {
        ObjectAttribute objAttr = (ObjectAttribute) edge.getAttribute(FACE);
        EdgeAttributes edgeAttr = (EdgeAttributes) objAttr.getObject();
        for (Iterator<Integer> i = edgeAttr.getFaces().iterator(); i.hasNext();) {
            int faceIndex = i.next();
            Face face = faces[faceIndex];
            if (face != null && faceIndex != outerfaceIndex) {
                face.incrementOute();
            }
        }
    }

    /**
     * 1. Calculate nodes, who belong to outerface and face. The node ,which has
     * <code>degree</code> = 2, defines the handle. 2. If we have exact one
     * node, we invoke updateNode(Node). Otherwise we have a real handle and
     * calculate the leftvertex and rightvertex of the handle. 3. All neighbours
     * increment their visited value and decrement their degree value 4.
     * Calculate the new Border of the outerface, when vertex will be delete. 5.
     * All nodes, which become part of the outerface, calculate their separation
     * face value ( sepf(v))
     * 
     * @param face
     *            <code>Face</code>
     */
    private boolean updateHandle(Face face) {
        if (numberOfExistsFaces == 2) {
            OrderNode oNode;
            LinkedList<Node> handle = new LinkedList<Node>();
            Node current = nodePosition[v1.getInteger("FPPnext")];
            int index = 0;

            while (current != v2) {
                handle.add(current);
                index = current.getInteger("FPPnext");
                removeNode(current);
                current = nodePosition[index];
            }
            if (handle.size() == 1) {
                oNode = new OrderNode(handle.getFirst(), ++orderNodeIndex);
            } else {
                oNode = new OrderNode(handle, ++orderNodeIndex);
            }
            oNode.setLeftvertex(v1);
            oNode.setRightvertex(v2);
            reverseOrder.add(oNode);
            faces[face.getIndex()] = null;
            numberOfExistsFaces--;
            return true;
        }

        LinkedList<Node> handle = new LinkedList<Node>();
        LinkedList<Node> newBorder;
        OrderNode oNode;
        Node first, out, left, right;
        /**
         * 1. Calculate nodes, who belong to outerface and face. The node ,which
         * has <code>degree</code> = 2, becomes the handle.
         */

        Iterator<Node> k = face.getNodelist().iterator();
        out = k.next();
        first = out;
        if (out.getBoolean("FPPout")) {
            out = k.next();
            while (out.getBoolean("FPPout") && out != first) {
                if (k.hasNext()) {
                    out = k.next();
                } else {
                    k = face.getNodelist().iterator();
                    out = k.next();
                }
            }
        }
        first = out;
        if (!out.getBoolean("FPPout")) {
            if (k.hasNext()) {
                out = k.next();
            } else {
                k = face.getNodelist().iterator();
                out = k.next();
            }
        }
        while (!out.getBoolean("FPPout") && out != first) {
            if (k.hasNext()) {
                out = k.next();
            } else {
                k = face.getNodelist().iterator();
                out = k.next();
            }
        }
        /**
         * This loop cares about the case, if all nodes are part of the
         * outerface
         */
        while (out.getInteger("FPPdegree") == 2) {
            if (k.hasNext()) {
                out = k.next();
            } else {
                k = face.getNodelist().iterator();
                out = k.next();
            }
        }
        first = out;
        boolean cond = out.getInteger("FPPdegree") == 2;
        if (out.getBoolean("FPPout")) {
            if (out != v1 && out != v2 && cond) {
                handle.addFirst(out);
            }
            if (k.hasNext()) {
                out = k.next();
            } else {
                k = face.getNodelist().iterator();
                out = k.next();
            }
        }
        while (out.getBoolean("FPPout") && out != first) {
            cond = out.getInteger("FPPdegree") == 2;
            if (out != v1 && out != v2 && cond) {
                handle.addFirst(out);
            }
            if (k.hasNext()) {
                out = k.next();
            } else {
                k = face.getNodelist().iterator();
                out = k.next();
            }
        }

        /**
         * 2. If we have exact one node, we invoke updateNode(Node). Otherwise
         * we have a real handle and calculate the leftvertex and rightvertex of
         * the handle.
         */
        if (handle.size() == 1) {
            updateNode(handle.getFirst());
            return true;
        }
        /** Create new OrderNode and adding into reverseOrder */
        oNode = new OrderNode(handle, ++orderNodeIndex);
        left = nodePosition[(handle.getFirst()).getInteger("FPPprevious")];
        right = nodePosition[(handle.getLast()).getInteger("FPPnext")];
        oNode.setLeftvertex(left);
        oNode.setRightvertex(right);
        reverseOrder.add(oNode);

        /**
         * 3. All neighbours increment their visited value and decrement their
         * degree value
         */
        Node firsthandle, lasthandle, neighbour;
        firsthandle = handle.getFirst();
        lasthandle = handle.getLast();
        for (Iterator<Node> j = firsthandle.getNeighborsIterator(); j.hasNext();) {
            neighbour = j.next();
            incrementVisited(neighbour);
            int degree = neighbour.getInteger("FPPdegree") - 1;
            neighbour.setInteger("FPPdegree", degree);
        }

        for (Iterator<Node> j = lasthandle.getNeighborsIterator(); j.hasNext();) {
            neighbour = j.next();
            incrementVisited(neighbour);
            int degree = neighbour.getInteger("FPPdegree") - 1;
            neighbour.setInteger("FPPdegree", degree);
        }

        /**
         * 4. Calculate the new Border of the outerface, when vertex will be
         * delete
         */
        newBorder = getNewBorder(handle, oNode);

        /**
         * 5. All nodes, which become part of the outerface, calculate their
         * separation face value ( sepf(v))
         */
        for (Iterator<Node> i = newBorder.iterator(); i.hasNext();) {
            Node current = i.next();
            possibleNextNode.add(current);
            sepf(current);
        }
        possibleNextNode.add(left);
        possibleNextNode.add(right);
        return true;
    }

    /**
     * 1. All faces, which contain node(s) of the hanlde, will be deleted (apart
     * from outerface) 2. Calculate the new Border and increment the oute and
     * outv, which belong the outerface. All faces, which contain nodes of the
     * handle, will be deleted (apart from outerface)
     * 
     * @param handle
     *            <code>LinkedList</code>
     * @param oNode
     *            <code>OrderNode</code>
     * 
     * @return the new part of the outerface as a <code>LinkedList</code>.
     */
    private LinkedList<Node> getNewBorder(LinkedList<Node> handle,
            OrderNode oNode) {
        Node left = oNode.getLeftvertex();
        Node right = oNode.getRightvertex();
        Node first = handle.getFirst();
        Node last = handle.getLast();
        Node current, nextNode = null;
        String next, previous;
        Edge nextEdge, leftEdge;
        int edgeIndex = -1;
        boolean done = false;

        LinkedList<Node> newBorder = new LinkedList<Node>();
        NodeAttributes nodeAttr;
        ObjectAttribute objAttr;

        /**
         * 1. All faces, which contain node(s) of the handle, will be deleted
         * (apart from outerface)
         */
        current = handle.getFirst();
        objAttr = (ObjectAttribute) current.getAttribute(FACE);
        nodeAttr = (NodeAttributes) objAttr.getObject();
        for (Iterator<Integer> j = nodeAttr.getFaces().iterator(); j.hasNext();) {
            int index = j.next();
            Face face = faces[index];
            if (face != null && index != outerfaceIndex) {
                if (face.getIsSepf()) {
                    for (Iterator<Node> i = face.getNodelist().iterator(); i
                            .hasNext();) {
                        Node node = i.next();
                        int sepf = node.getInteger("FPPsepf");
                        sepf--;
                        node.setInteger("FPPsepf", sepf);
                    }
                    face.setIsSepf(true);
                }
                faces[index] = null;
                numberOfExistsFaces--;
            }
        }

        for (Iterator<Node> i = handle.iterator(); i.hasNext();) {
            Node node = i.next();
            removeNode(node);
        }

        /**
         * 2. Calculate the new Border and increment the oute and outv, which
         * belong the outerface.
         */
        next = left.getString(PATH) + "FPPnext";
        previous = left.getString(PATH) + "FPPprevious";
        leftEdge = calculatefaces.getEdge(first, left);
        nextEdge = calculatefaces.getEdge(leftEdge.getInteger(next));
        nextEdge.setBoolean("out", true);
        updateOutE(nextEdge);
        calculatefaces.removeEdge(leftEdge, next, previous);
        updateFace(nextEdge);

        /** Delete all edges, which belong to Node first */
        for (Iterator<Edge> i = first.getEdgesIterator(); i.hasNext();) {
            Edge edge = i.next();
            next = first.getString(PATH) + "FPPnext";
            previous = first.getString(PATH) + "FPPprevious";
            edge.setInteger(next, -1);
            edge.setInteger(previous, -1);
            Node node = calculatefaces.getOppositeNode(first, edge);

            next = node.getString(PATH) + "FPPnext";
            previous = node.getString(PATH) + "FPPprevious";
            calculatefaces.removeEdge(edge, next, previous);
        }
        /** Delete all edges, which belong to Node last */
        for (Iterator<Edge> i = last.getEdgesIterator(); i.hasNext();) {
            Edge edge = i.next();
            next = last.getString(PATH) + "FPPnext";
            previous = last.getString(PATH) + "FPPprevious";
            edge.setInteger(next, -1);
            edge.setInteger(previous, -1);
            Node node = calculatefaces.getOppositeNode(first, edge);

            next = node.getString(PATH) + "FPPnext";
            previous = node.getString(PATH) + "FPPprevious";
            calculatefaces.removeEdge(edge, next, previous);
        }
        nextNode = left;
        while (!done) {
            current = nextNode;
            nextNode = calculatefaces.getOppositeNode(nextNode, nextEdge);
            if (nextNode == right) {
                done = true;
            } else {
                addNode(current, nextNode);
                newBorder.add(nextNode);
                nextNode.setBoolean("FPPout", true);
                updateOutV(nextNode);

                next = nextNode.getString(PATH) + "FPPnext";
                edgeIndex = nextEdge.getInteger(next);

                nextEdge = calculatefaces.getEdge(edgeIndex);
                nextEdge.setBoolean("FPPout", true);
                updateOutE(nextEdge);
            }
        }
        updateFace(nextEdge);
        return newBorder;
    }

    /**
     * Transform a LinkedList into an OrderNode[]
     * 
     * @param reverseOrder
     *            <code>LinkedList</code>
     */
    private void getReverseInduction(LinkedList<OrderNode> reverseOrder) {
        reverseInduction = reverseOrder.toArray(new OrderNode[0]);
    }

    /**
     * There is a possibility that a face, which be at first a separation face
     * and only now not be a separation face. These faces will be check now.
     * 
     * @param nextEdge
     *            is the "next" edge relative to the leftvertex or it is the
     *            "previous" edge relative to the rightvertex. nextEdge concerns
     *            only the leftvertex respectively the rightvertex from a
     *            OrderNode.
     */
    private void updateFace(Edge nextEdge) {
        EdgeAttributes edgeAttr = (EdgeAttributes) nextEdge.getAttribute(FACE)
                .getValue();
        LinkedList<Integer> edgeFacelist = edgeAttr.getFaces();
        Face face;
        int value;
        for (Iterator<Integer> j = edgeFacelist.iterator(); j.hasNext();) {
            value = j.next();
            face = faces[value];
            if (face != null && isSepf(face) && !face.getIsSepf()) {
                for (Iterator<Node> i = face.getNodelist().iterator(); i
                        .hasNext();) {
                    Node node = i.next();
                    int sepf = node.getInteger("FPPsepf");
                    sepf++;
                    node.setInteger("FPPsepf", sepf);
                }
                face.setIsSepf(true);
            }
            if (face != null && !isSepf(face) && face.getIsSepf()) {
                for (Iterator<Node> i = face.getNodelist().iterator(); i
                        .hasNext();) {
                    Node node = i.next();
                    int sepf = node.getInteger("FPPsepf");
                    sepf--;
                    node.setInteger("FPPsepf", sepf);
                }
                face.setIsSepf(false);
            }
            possibleNextFaces.add(face);
        }
    }

    /** @return the reverseInduction */
    public OrderNode[] getReverseInduction() {
        return reverseInduction;
    }

    /** @return the second vertex */
    protected Node getVertex2() {
        return v2;
    }

    /** @return the first vertex */
    protected Node getVertex1() {
        return v1;
    }

    /** @return the last vertex */
    protected Node getVertexN() {
        return vN;
    }

    /** @return nodePosition */
    protected Node[] getNodePosition() {
        return nodePosition;
    }
}
