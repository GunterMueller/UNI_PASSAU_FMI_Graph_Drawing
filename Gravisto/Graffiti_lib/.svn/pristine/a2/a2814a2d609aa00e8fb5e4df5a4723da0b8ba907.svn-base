package org.graffiti.plugins.algorithms.fpp;

/**
 * @author Le Pham Hai Dang
 */

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.views.defaults.StraightLineEdgeShape;

public class Drawing {

    // ~ Instance fields
    // ========================================================
    private static String PATH = "FPPnumber";

    private Graph graph;

    private OrderNode[] lmc;

    private Node[] nodePosition;

    private Node v1, vN;

    private CalculateOrder calculateOrder;

    private int grid, yMax;

    // ~ Constructors
    // ================================================================
    /**
     * @param graph
     *            <code>Graph</code>
     * @param lmc
     *            <code>OrderNode[]</code>
     * @param calculateOrder
     *            <code>CalculateOrder</code>
     * @param grid
     *            <code>int</code>
     */
    public Drawing(Graph graph, OrderNode[] lmc, CalculateOrder calculateOrder,
            int grid) {
        this.graph = graph;
        this.lmc = lmc;
        this.calculateOrder = calculateOrder;
        this.nodePosition = calculateOrder.getNodePosition();
        this.grid = grid;

        v1 = lmc[0].getOrderNode();
        vN = lmc[lmc.length - 1].getOrderNode();
        v1.setBoolean("FPPcorrect", true);
        linearStraightLineDraw();
        removeAttributes();
    }

    // ~ Methods
    // ================================================================
    /**
     * Remove all added attributes from the graph
     */
    private void removeAttributes() {
        Collection<Node> nodes = graph.getNodes();
        Collection<Edge> edges = graph.getEdges();

        for (Iterator<Edge> i = edges.iterator(); i.hasNext();) {
            Edge edge = i.next();
            String forward, backward;
            String next, previous;
            Node source, target;

            /** calculateFace */
            source = edge.getSource();
            target = edge.getTarget();
            forward = source.getString(PATH) + "FPP-" + target.getString(PATH);
            backward = target.getString(PATH) + "FPP-" + source.getString(PATH);

            next = source.getString(PATH) + "FPPnext";
            previous = source.getString(PATH) + "FPPprevious";
            edge.removeAttribute(next);
            edge.removeAttribute(previous);

            next = target.getString(PATH) + "FPPnext";
            previous = target.getString(PATH) + "FPPprevious";
            edge.removeAttribute(next);
            edge.removeAttribute(previous);

            edge.removeAttribute(forward);
            edge.removeAttribute(backward);
            edge.removeAttribute("FPPFace");
            edge.removeAttribute("FPPout");
            edge.removeAttribute("FPPnumber");
        }

        for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            Node node = i.next();

            /** CalculateFace */
            node.removeAttribute("FPPFace");
            node.removeAttribute(PATH);

            /** CalculateOrder */
            node.removeAttribute("FPPsepf");
            node.removeAttribute("FPPvisited");
            node.removeAttribute("FPPout");
            node.removeAttribute("FPPdegree");
            node.removeAttribute("FPPnext");
            node.removeAttribute("FPPprevious");

            /** Drawing */
            node.removeAttribute("FPPcorrect");
            node.removeAttribute("FPPshift");
            node.removeAttribute("FPPrshift");
            node.removeAttribute("FPPxInsert");
            node.removeAttribute("FPPx");
            node.removeAttribute("FPPy");
        }
    }

    /** According to the lmc-Ordering we calculate the coordinates */
    private void linearStraightLineDraw() {
        Node left, right, current = null;
        LinkedList<Node> handle = null;
        int shiftValue = 0;
        for (int i = 2; i < lmc.length; i++) {
            left = lmc[i].getLeftvertex();
            right = lmc[i].getRightvertex();
            update(right);

            if ((lmc[i].getHandle())) {
                int y = 0, x = 0;
                handle = lmc[i].getOrderList();
                updateOuterface(left, right, handle, i);
                right.setInteger("FPPshift", right.getInteger("FPPshift") + 2);
                getCoordinate(left, right, handle.getFirst());
                current = handle.getFirst();
                y = current.getInteger("FPPy");
                x = current.getInteger("FPPx");

                for (Iterator<Node> j = handle.iterator(); j.hasNext();) {
                    current = j.next();
                    current.setInteger("FPPx", x);
                    current.setInteger("FPPxInsert", x);
                    current.setInteger("FPPy", y);
                    x += 2;
                }
                shiftValue = right.getInteger("FPPshift") + 2
                        * (handle.size() - 1);
                right.setInteger("FPPshift", shiftValue);
            } else {
                current = lmc[i].getOrderNode();
                updateOuterface(left, right, current, i);
                shiftValue = right.getInteger("FPPshift") + 2;
                right.setInteger("FPPshift", shiftValue);
                getCoordinate(left, right, current);
            }
            yMax = vN.getInteger("FPPy");
        }

        Collection<Node> nodes = graph.getNodes();
        for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            current = i.next();
            current.setInteger("FPPshift", 0);
            current.setInteger("FPPrshift", 0);
        }

        for (int i = lmc.length - 1; i > 0; i--) {
            left = lmc[i].getLeftvertex();
            right = lmc[i].getRightvertex();

            LinkedList<Node> neighbours;
            int x;
            if (lmc[i].getHandle()) {
                /** Update interior neighbours */
                neighbours = lmc[i].getInteriorNeighbours();
                handle = lmc[i].getOrderList();
                Node first = handle.getFirst();
                Node previous = null;
                for (Iterator<Node> k = neighbours.iterator(); k.hasNext();) {
                    Node interiorNeighbour = k.next();
                    shiftValue = first.getInteger("FPPshift")
                            + first.getInteger("FPPrshift") + 1;
                    interiorNeighbour.setInteger("FPPshift", shiftValue);
                }

                Iterator<Node> j = handle.iterator();
                previous = j.next();
                while (j.hasNext()) {
                    current = j.next();
                    shiftValue = current.getInteger("FPPrshift")
                            + previous.getInteger("FPPrshift");
                    current.setInteger("FPPrshift", shiftValue);
                    previous = current;
                }
                shiftValue = right.getInteger("FPPrshift")
                        + current.getInteger("FPPrshift") + 2 * handle.size();
                right.setInteger("FPPrshift", shiftValue);

                for (Iterator<Node> k = handle.iterator(); k.hasNext();) {
                    current = k.next();
                    x = current.getInteger("FPPxInsert")
                            + current.getInteger("FPPshift")
                            + current.getInteger("FPPrshift");
                    current.setInteger("FPPx", x);
                }
            } else {
                /** Update interior neighbours */
                neighbours = lmc[i].getInteriorNeighbours();
                current = lmc[i].getOrderNode();
                if (left != null) {
                    for (Iterator<Node> k = neighbours.iterator(); k.hasNext();) {
                        Node interiorNeighbour = k.next();
                        shiftValue = current.getInteger("FPPshift")
                                + current.getInteger("FPPrshift") + 1;
                        interiorNeighbour.setInteger("FPPshift", shiftValue);
                    }
                    shiftValue = right.getInteger("FPPrshift")
                            + current.getInteger("FPPrshift") + 2;
                    right.setInteger("FPPrshift", shiftValue);

                }
                x = current.getInteger("FPPxInsert")
                        + current.getInteger("FPPshift")
                        + current.getInteger("FPPrshift");
                current.setInteger("FPPx", x);
            }
        }
        drawNodes();
    }

    /**
     * This method travers along the outerface from Node right toward Node left
     * until we find the first "true" marked correct(correctNode). Then we walk
     * back from correctNode to Node right and update shift value of Nodes
     * between correctNode and Node right
     * 
     * @param right
     *            <code>Node</code>
     */
    private void update(Node right) {
        int shiftValue = 0, x;
        Node current = nodePosition[right.getInteger("FPPprevious")];
        while (!current.getBoolean("FPPcorrect")) {
            current = nodePosition[current.getInteger("FPPprevious")];
        }

        current = nodePosition[current.getInteger("FPPnext")];

        while (current != right) {
            shiftValue += current.getInteger("FPPshift");
            x = current.getInteger("FPPx") + shiftValue;
            current.setBoolean("FPPcorrect", true);
            current.setInteger("FPPx", x);
            current = nodePosition[current.getInteger("FPPnext")];
        }
        shiftValue += right.getInteger("FPPshift");
        right.setInteger("FPPshift", shiftValue);
    }

    /**
     * Update the outerface: Adding the handle
     * 
     * @param left
     *            <code>Node</code>
     * @param right
     *            <code>Node</code>
     * @param handle
     *            <code>LinkedList</code>
     */
    private void updateOuterface(Node left, Node right,
            LinkedList<Node> handle, int orderIndex) {
        Node current = left;
        int nodeIndex;
        /** Update outerface by adding handle */
        for (Iterator<Node> i = handle.iterator(); i.hasNext();) {
            Node addNode = i.next();
            calculateOrder.addNode(current, addNode);
            current = addNode;
        }
        current = nodePosition[current.getInteger("FPPnext")];
        while (current != right) {
            nodeIndex = current.getInteger("FPPnext");
            calculateOrder.removeNode(current);
            lmc[orderIndex].addInteriorNeighbours(current);
            current = nodePosition[nodeIndex];
        }
    }

    /**
     * Update the outerface: Adding a Node
     * 
     * @param left
     *            <code>Node</code>
     * @param right
     *            <code>Node</code>
     * @param out
     *            <code>Node</code>
     */
    private void updateOuterface(Node left, Node right, Node out, int orderIndex) {
        Node current = left;
        int nodeIndex;
        calculateOrder.addNode(current, out);
        current = nodePosition[out.getInteger("FPPnext")];
        while (current != right) {
            nodeIndex = current.getInteger("FPPnext");
            calculateOrder.removeNode(current);
            lmc[orderIndex].addInteriorNeighbours(current);
            current = nodePosition[nodeIndex];
        }
    }

    /**
     * According to left and right vertex this method calculates the new
     * coordinates for newNode.
     * 
     * @param left
     *            <code>Node</code>
     * @param right
     *            <code>Node</code>
     * @param newNode
     *            <code>Node</code>
     */
    private void getCoordinate(Node left, Node right, Node newNode) {
        int x, y;
        x = (left.getInteger("FPPx") - left.getInteger("FPPy")
                + right.getInteger("FPPx") + right.getInteger("FPPshift") + right
                .getInteger("FPPy")) / 2;
        y = (-left.getInteger("FPPx") + left.getInteger("FPPy")
                + right.getInteger("FPPx") + right.getInteger("FPPshift") + right
                .getInteger("FPPy")) / 2;
        newNode.setInteger("FPPx", x);
        newNode.setInteger("FPPxInsert", x);
        newNode.setInteger("FPPy", y);
    }

    /**
     * 1. Transform all polylines into straigth lines 2. Set the coordinates
     * into the graph
     */
    private void drawNodes() {
        Collection<Edge> edges = graph.getEdges();
        Collection<Node> nodes = graph.getNodes();
        int x, y, brink;

        /** 1. Transform all polylines into straigth lines */
        for (Iterator<Edge> i = edges.iterator(); i.hasNext();) {
            Edge edge = i.next();
            try {
                EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) edge
                        .getAttribute("graphics");
                SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                        "bends");
                edgeAttr.setBends(bends);
                edgeAttr.setShape(StraightLineEdgeShape.class.getName());
            } catch (Exception e) {
                // There are not any polylines!
            }

        }

        /** 2. Set the coordinates into the graph */
        for (Iterator<Node> i = nodes.iterator(); i.hasNext();) {
            CoordinateAttribute ca;
            Node current = i.next();
            try {
                ca = (CoordinateAttribute) current
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
            } catch (Exception e) {
                NodeGraphicAttribute ngAttribute = new NodeGraphicAttribute();
                current.addAttribute(ngAttribute, "");
                ca = (CoordinateAttribute) current
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
            }
            x = current.getInteger("FPPx");
            y = current.getInteger("FPPy");
            y = yMax - y;
            brink = grid;
            if (brink < 25) {
                brink = 25;
            }
            Point2D point = new Point2D.Double((x * grid) + brink, (y * grid)
                    + brink);
            ca.setCoordinate(point);
        }
    }
}
