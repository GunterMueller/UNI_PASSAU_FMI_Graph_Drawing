// =============================================================================
//
//   PentaTreeGrid.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.treeGridDrawings;

import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.StringSelectionParameter;
import org.graffiti.plugins.algorithms.treedrawings.GraphChecker;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;

/**
 * @author Tom
 * @version $Revision$ $Date$
 */
public class HDLayout extends AbstractAlgorithm {

    /** chooses the method, which is used to draw the tree */
    private StringSelectionParameter drawingMethod;

    private StringSelectionParameter compositionMethod;

    // grid unit
    private int unit = HexaConstants.unit;

    private double unitHeight = (Math.sqrt(3) / 2) * unit;

    private int depth;

    private Node root = null;

    /**
     * Constructs a new instance.
     */
    public HDLayout() {
        String[] methods = { "REGULAR", "BEND_LAYOUT" };
        drawingMethod = new StringSelectionParameter(methods,
                "Drawing Method:", "<html><p>Regular</p>" + "<p>With Bends</p>"
                        + "</html>");

        String[] composition = { "Horizontally", "Vertically", "Alternating" };
        compositionMethod = new StringSelectionParameter(composition,
                "Composition Method:", "<html><p>Hoizontally</p>"
                        + "<p>Vertically</p>" + "<p>Alternating</p>"
                        + "</html>");
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#check()
     */
    @Override
    public void check() throws PreconditionException {
        this.root = GraphChecker.checkTree(this.graph, 3);
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        if (root == null)
            throw new RuntimeException("Must call method \"check\" before "
                    + " calling \"execute\".");

        this.graph.getListenerManager().transactionStarted(this);
        visit(root);
        depth = calculateTreeDepth(root, 1);

        removeBends(root);

        if (drawingMethod.getSelectedValue() == "REGULAR") {
            alignHD(root, 0, 1);
        } else if (drawingMethod.getSelectedValue() == "BEND_LAYOUT") {
            alignHDWithBends(root, 0, 1);
        }
        this.graph.getListenerManager().transactionFinished(this);
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        return "HDLayout (dated)";
    }

    /*
     * @see org.graffiti.plugin.algorithm.Algorithm#getParameters()
     */
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new Parameter[] { drawingMethod, compositionMethod };
    }

    /*
     * @see
     * org.graffiti.plugin.algorithm.Algorithm#setParameters(org.graffiti.plugin
     * .parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
    }

    /*
     * Aligns the tree along a hexagonal grid @param root root of the subtree
     * 
     * @param position indicates the position relative to it's father
     */
    private double alignHD(Node root, int position, int currentDepth) {
        // 'distance' is the distance, the subtrees are moved away from their
        // father; distanceHeight is the height of one of the triangles on the
        // grid with side length 'distance'
        double distance = 0;
        double maxX = 0, maxY = 0;

        // the current node is a leaf
        if (root.getOutDegree() == 0) {
            CoordinateAttribute ca = (CoordinateAttribute) root
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(0.0, 0.0));
            return unit;

        } else {

            distance = (Math.pow(2.6, (depth - (currentDepth + 1)))) * unit;

            // double distanceHeight = (Math.sqrt(3) / 2) * distance;

            // set the position of the root of the current subtree
            CoordinateAttribute ca = (CoordinateAttribute) root
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(1.5 * distance, 0));

            Collection<Node> nodes = root.getAllOutNeighbors();
            int i = 0;
            Iterator<Node> it = nodes.iterator();

            while (it.hasNext()) {

                Node node = it.next();
                alignHD(node, i, currentDepth + 1);

                // move the subtree to the position of it's father
                CoordinateAttribute ca2 = (CoordinateAttribute) node
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);

                move(node, 1.5 * distance - ca2.getX(), 0);

                // move the subtree to it's destined position
                switch (i) {
                // move the middle subtree one unit to the lower right
                case 0:
                    move(node, 0.5 * unit, (Math.sqrt(3) / 2) * unit);
                    CoordinateAttribute can = (CoordinateAttribute) node
                            .getAttribute(GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.COORDINATE);

                    // calculate the maximum X and Y position in this
                    // subtree
                    maxX = maxX(node).getX() - can.getX();
                    maxY = maxY(node).getY() - can.getY();

                    break;

                // move the right subtree one unit farther than the breadth
                // of the middle subtree
                case 1:

                    double tmpDistance = Math.ceil(maxX / unit) * unit;
                    if (tmpDistance < unit) {
                        move(node, unit, 0);
                    } else {
                        move(node, tmpDistance + 2 * unit, 0);
                    }
                    break;

                // move the lower left subtree one unit farther than the
                // height of the middle subtree
                case 2:
                    double tmpDistanceHeight = Math.ceil(maxY / unitHeight
                            * unitHeight);
                    if (tmpDistanceHeight < unitHeight) {
                        move(node, -0.5 * unit, (Math.sqrt(3) / 2) * unit);
                    } else {

                        tmpDistance = (tmpDistanceHeight / (Math.sqrt(3) / 2) + 2 * unit);
                        tmpDistanceHeight = (Math.sqrt(3) / 2) * tmpDistance;

                        move(node, -0.5 * tmpDistance, tmpDistanceHeight);
                    }

                    // move(node, -0.5 * distance, distanceHeight);
                    break;

                }

                i++;

            }

        }
        return 2 * distance;

    }

    /*
     * Aligns the tree along a hexagonal grid @param root root of the subtree
     * 
     * @param position indicates the position relative to it's father
     */
    private double alignHDWithBends(Node root, int position, int currentDepth) {
        // 'distance' is the distance, the subtrees are moved away from their
        // father;
        double distance = 0;

        // the current node is a leaf
        if (root.getOutDegree() == 0) {
            CoordinateAttribute ca = (CoordinateAttribute) root
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(0.0, 0.0));
            return unit;

        } else {

            distance = (Math.pow(2.6, (depth - (currentDepth + 1)))) * unit;

            // distanceHeight = (Math.sqrt(3) / 2) * distance;

            // set the position of the root of the current subtree
            CoordinateAttribute ca = (CoordinateAttribute) root
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(1.5 * distance, 0));

            if (compositionMethod.getSelectedValue() == "Horizontally") {

                Collection<Node> nodes = root.getAllOutNeighbors();
                int i = 0;
                Iterator<Node> it = nodes.iterator();
                Node nodeL = null, nodeM = null, nodeR = null;

                while (it.hasNext()) {

                    Node node = it.next();
                    alignHDWithBends(node, i, currentDepth + 1);

                    // move the subtree to the position of it's father
                    CoordinateAttribute ca2 = (CoordinateAttribute) node
                            .getAttribute(GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.COORDINATE);

                    move(node, 1.5 * distance - ca2.getX(), 0);

                    if (maxY(node).getY() > maxY(nodeR).getY()) {
                        if (root.getAllOutNeighbors().size() <= 2) {
                            nodeL = nodeR;
                        } else {
                            nodeL = nodeM;
                            nodeM = nodeR;
                        }
                        nodeR = node;
                    } else if (maxY(node).getY() > maxY(nodeM).getY()) {
                        if (root.getAllOutNeighbors().size() <= 2) {
                            nodeL = node;
                        } else {
                            nodeL = nodeM;
                            nodeM = node;
                        }
                    } else {
                        nodeL = node;
                    }
                }

                composeHorizontally(root, nodeR, nodeM, nodeL, distance, ca);
            }

            else if (compositionMethod.getSelectedValue() == "Vertically") {

                Node nodeU = null, nodeM = null, nodeD = null;

                Collection<Node> nodes = root.getAllOutNeighbors();
                int i = 0;
                Iterator<Node> it = nodes.iterator();

                while (it.hasNext()) {

                    Node node = it.next();
                    alignHDWithBends(node, i, currentDepth + 1);

                    // move the subtree to the position of it's father
                    CoordinateAttribute ca2 = (CoordinateAttribute) node
                            .getAttribute(GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.COORDINATE);

                    move(node, 1.5 * distance - ca2.getX(), 0);
                    Point2D p = breadth(node);
                    double breadth = p.getX() - p.getY();
                    Point2D pD = breadth(nodeD);
                    Point2D pM = breadth(nodeM);

                    if (breadth > pD.getX() - pD.getY()) {
                        if (root.getAllOutNeighbors().size() <= 2) {
                            nodeU = nodeD;
                        } else {
                            nodeU = nodeM;
                            nodeM = nodeD;
                        }
                        nodeD = node;
                    } else if (breadth > pM.getX() - pM.getY()) {
                        if (root.getAllOutNeighbors().size() <= 2) {
                            nodeU = node;
                        } else {
                            nodeU = nodeM;
                            nodeM = node;
                        }
                    } else {
                        nodeU = node;
                    }
                }
                composeVertically(root, nodeU, nodeM, nodeD, distance, ca);
            }

            else if (compositionMethod.getSelectedValue() == "Alternating") {
                Node nodeU = null, nodeM = null, nodeD = null, nodeL = null, nodeR = null;

                Collection<Node> nodes = root.getAllOutNeighbors();
                int i = 0;
                Iterator<Node> it = nodes.iterator();

                while (it.hasNext()) {

                    Node node = it.next();
                    alignHDWithBends(node, i, currentDepth + 1);

                    // move the subtree to the position of it's father
                    CoordinateAttribute ca2 = (CoordinateAttribute) node
                            .getAttribute(GraphicAttributeConstants.GRAPHICS
                                    + Attribute.SEPARATOR
                                    + GraphicAttributeConstants.COORDINATE);

                    move(node, 1.5 * distance - ca2.getX(), 0);

                    if ((depth - currentDepth) % 2 == 0) {
                        Point2D p = breadth(node);
                        double breadth = p.getX() - p.getY();
                        Point2D pD = breadth(nodeD);
                        Point2D pM = breadth(nodeM);
                        if (breadth > pD.getX() - pD.getY()) {
                            if (root.getAllOutNeighbors().size() <= 2) {
                                nodeU = nodeD;
                            } else {
                                nodeU = nodeM;
                                nodeM = nodeD;
                            }
                            nodeD = node;
                        } else if (breadth > pM.getX() - pM.getY()) {
                            if (root.getAllOutNeighbors().size() <= 2) {
                                nodeU = node;
                            } else {
                                nodeU = nodeM;
                                nodeM = node;
                            }
                        } else {
                            nodeU = node;
                        }

                    } else {

                        if (maxY(node).getY() > maxY(nodeR).getY()) {
                            if (root.getAllOutNeighbors().size() <= 2) {
                                nodeL = nodeR;
                            } else {
                                nodeL = nodeM;
                                nodeM = nodeR;
                            }
                            nodeR = node;
                        } else if (maxY(node).getY() > maxY(nodeM).getY()) {
                            if (root.getAllOutNeighbors().size() <= 2) {
                                nodeL = node;
                            } else {
                                nodeL = nodeM;
                                nodeM = node;
                            }
                        } else {
                            nodeL = node;
                        }

                    }

                }

                if ((depth - currentDepth) % 2 == 0) {
                    composeVertically(root, nodeU, nodeM, nodeD, distance, ca);
                } else {
                    composeHorizontally(root, nodeR, nodeM, nodeL, distance, ca);
                }

            }

        }
        return 2 * distance;

    }

    /*
     * Moves the subtree under root x units along the X direction and y units
     * along the Y direction
     */
    private void move(Node root, double x, double y) {
        CoordinateAttribute ca = (CoordinateAttribute) root
                .getAttribute(GraphicAttributeConstants.GRAPHICS
                        + Attribute.SEPARATOR
                        + GraphicAttributeConstants.COORDINATE);
        ca.setCoordinate(new Point2D.Double(ca.getX() + x, ca.getY() + y));

        Collection<Node> nodes = root.getAllOutNeighbors();
        Iterator<Node> it = nodes.iterator();

        Iterator<Edge> edgeIt = root.getAllOutEdges().iterator();

        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) e
                    .getAttribute("graphics");
            if (edgeAttr.getNumberOfBends() > 0) {
                SortedCollectionAttribute attr = edgeAttr.getBends();
                CoordinateAttribute caTmp = (CoordinateAttribute) attr
                        .getAttribute("bend0");
                caTmp.setCoordinate(new Point2D.Double(caTmp.getX() + x, caTmp
                        .getY()
                        + y));
            }

        }

        while (it.hasNext()) {
            Node node = it.next();
            move(node, x, y);
        }

    }

    /**
     * calculates the tree depth recursively
     * 
     * @param n
     *            a node
     * @param level
     *            current depth
     * @return the depth of the subtree n
     */
    private int calculateTreeDepth(Node n, int level) {
        n.setBoolean("visited", true);
        int maxDepth = level;
        for (Node x : n.getNeighbors()) {

            if (!x.getBoolean("visited")) {

                maxDepth = Math.max(maxDepth, calculateTreeDepth(x, level + 1));
            }
        }
        return maxDepth;
    }

    private void visit(Node n) {
        n.setBoolean("visited", false);
        for (Node x : n.getAllOutNeighbors()) {
            visit(x);
        }

    }

    /**
     * Retrieves the maximum x value of the subtree under n
     * 
     * @param n
     * @return maximum X Value of the subtree
     */
    private Point2D maxX(Node n) {
        if (n == null) {
            Point2D maxX = new Point2D.Double(-1, -1);
            return maxX;
        }

        Point2D maxX = new Point2D.Double(0, 0);

        if (n.getAllOutNeighbors().size() == 0) {
            CoordinateAttribute ca = (CoordinateAttribute) n
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            maxX = ca.getCoordinate();
        } else {
            Iterator<Node> it = n.getAllOutNeighbors().iterator();
            while (it.hasNext()) {
                Node currentNode = it.next();
                Point2D tmp = maxX(currentNode);
                if ((tmp.getX() > maxX.getX()) && (tmp.getY() >= maxX.getY())) {
                    maxX = tmp;
                } else if ((tmp.getX() > maxX.getX())
                        && ((tmp.getX() - maxX.getX()) * 2 / unit > (maxX
                                .getY() - tmp.getY())
                                / unitHeight)) {
                    maxX = tmp;
                } else if ((((maxX.getX() - tmp.getX()) * 2) / unit < (tmp
                        .getY() - maxX.getY())
                        / unitHeight)) {
                    maxX = tmp;
                }
            }
        }
        return maxX;
    }

    /**
     * Retrieves the maximum y value of the subtree under n
     * 
     * @param n
     * @return maximum Y Value of the subtree
     */
    private Point2D maxY(Node n) {
        if (n == null) {
            Point2D maxY = new Point2D.Double(-1, -1);
            return maxY;
        }

        Point2D maxY = new Point2D.Double(0, 0);
        if (n.getAllOutNeighbors().size() == 0) {
            CoordinateAttribute ca = (CoordinateAttribute) n
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            maxY = ca.getCoordinate();
        } else {
            Iterator<Node> it = n.getAllOutNeighbors().iterator();
            while (it.hasNext()) {
                Node currentNode = it.next();
                Point2D tmp = maxY(currentNode);
                if (tmp.getY() > maxY.getY()) {
                    maxY = tmp;
                }
            }
        }
        return maxY;
    }

    private Point2D breadth(Node n) {
        Point2D maxXminX = new Point2D.Double(0, 0);
        if (n == null) {
            maxXminX = new Point2D.Double(-1, 1);
            return maxXminX;
        }

        if (n.getAllOutNeighbors().size() == 0) {
            CoordinateAttribute ca = (CoordinateAttribute) n
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            maxXminX = ca.getCoordinate();
        } else {
            Iterator<Node> it = n.getAllOutNeighbors().iterator();
            while (it.hasNext()) {
                Node currentNode = it.next();
                Point2D tmp = breadth(currentNode);
                if (tmp.getX() > maxXminX.getX()) {
                    maxXminX.setLocation(tmp.getX(), maxXminX.getY());
                }

                if (tmp.getY() < maxXminX.getY()) {
                    maxXminX.setLocation(maxXminX.getX(), tmp.getY());
                }
            }
        }
        return maxXminX;
    }

    private void removeBends(Node root) {

        Iterator<Edge> edgeIt = root.getAllOutEdges().iterator();

        while (edgeIt.hasNext()) {
            Edge e = edgeIt.next();
            EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) e
                    .getAttribute("graphics");
            if (edgeAttr.getNumberOfBends() > 0) {
                SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                        "bends");
                edgeAttr.setBends(bends);
            }

            Node n = e.getTarget();
            removeBends(n);

        }

    }

    private void composeVertically(Node root, Node nodeU, Node nodeM,
            Node nodeD, double distance, CoordinateAttribute ca) {

        double maxY = 0;

        double tmpDistance;

        // move the right subtree two units to the right
        if (nodeU != null) {
            if (nodeU.getAllOutNeighbors().size() == 0) {
                move(nodeU, unit, 0);
            } else {
                if (root.getAllOutNeighbors().size() <= 2) {
                    move(nodeU, unit, 0);
                } else {
                    move(nodeU, 2 * unit, 0);
                }
            }

            // calculate the maximum Y position in this subtree
            Point2D maxSubtree = maxY(nodeU);
            maxY = maxSubtree.getY() - ca.getY();
            // maxY = maxY + (maxSubtree.getY() / unitHeight) *
            // unitHeight;

        }

        // move the middle subtree one unit farther down than the height
        // of the right subtree
        if (nodeM != null) {
            Edge edge = graph.getEdges(root, nodeM).iterator().next();

            tmpDistance = Math.ceil(maxY / unitHeight) * unitHeight;

            if (nodeM.getAllOutNeighbors().size() == 0) {
                move(nodeM, 0.5 * unit, unitHeight);
            } else {
                EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) edge
                        .getAttribute("graphics");
                SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                        "bends");
                Point2D currentBend = new Point2D.Double(1.5 * distance + 0.5
                        * unit, unitHeight);
                if (tmpDistance != 0) {
                    bends.add(new CoordinateAttribute("bend0", currentBend));
                }
                edgeAttr.setBends(bends);
                edgeAttr.setShape(PolyLineEdgeShape.class.getName());

                move(nodeM, -(tmpDistance) / Math.sqrt(3) + (0.5 * unit),
                        tmpDistance + unitHeight);
            }

            // calculate the maximum Y poisition in this subtree
            maxY = maxY(nodeM).getY() - ca.getY();
        }

        // move the left subtree one unit farther down than
        // the middle subtree
        if (nodeD != null) {

            tmpDistance = Math.ceil(maxY / unitHeight) * unitHeight;

            if (nodeD.getAllOutNeighbors().size() == 0) {
                move(nodeD, -0.5 * unit, unitHeight);
            } else {
                move(nodeD, -(tmpDistance / Math.sqrt(3)) - (0.5 * unit),
                        tmpDistance + unitHeight);
            }

        }

    }

    private void composeHorizontally(Node root, Node nodeR, Node nodeM,
            Node nodeL, double distance, CoordinateAttribute ca) {

        double maxX = 0;

        double tmpDistance;

        // move the subtree to it's destined position

        // move the left subtree two units to the lower left
        if (nodeL != null) {
            // calculate the maximum X position in this subtree
            Point2D maxSubtree = maxX(nodeL);
            maxX = maxSubtree.getX() - ca.getX();
            maxX = maxX + (((maxSubtree.getY()) / unitHeight) * unit) / 2;
            if (nodeL.getAllOutNeighbors().size() == 0) {
                move(nodeL, -0.5 * unit, unitHeight);
            } else {
                if (root.getAllOutNeighbors().size() <= 2) {
                    move(nodeL, -0.5 * unit, unitHeight);
                } else {
                    move(nodeL, -unit, 2 * unitHeight);
                }
            }

        }

        // move the middle subtree one unit farther than the breadth
        // of the left subtree
        if (nodeM != null) {
            Edge edge = graph.getEdges(root, nodeM).iterator().next();

            tmpDistance = Math.ceil(maxX / unit) * unit;

            if (nodeM.getAllOutNeighbors().size() == 0) {
                move(nodeM, 0.5 * unit, unitHeight);
            } else {
                EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) edge
                        .getAttribute("graphics");
                SortedCollectionAttribute bends = new LinkedHashMapAttribute(
                        "bends");
                Point2D currentBend = new Point2D.Double(1.5 * distance + 0.5
                        * unit, unitHeight);
                if (tmpDistance != 0) {
                    bends.add(new CoordinateAttribute("bend0", currentBend));
                }
                edgeAttr.setBends(bends);
                edgeAttr.setShape(PolyLineEdgeShape.class.getName());

                move(nodeM, tmpDistance + 0.5 * unit, unitHeight);
            }

            // calculate the maximum X poisition in this subtree
            // maxX = maxX(nodeM).getX() - ca.getX();
            Point2D maxSubtree = maxX(nodeM);
            maxX = maxSubtree.getX() - ca.getX();
            maxX = maxX + ((maxSubtree.getY() / unitHeight) * unit) / 2;

        }

        // move the right subtree one unit farther to the left than
        // the middle subtree
        if (nodeR != null) {

            tmpDistance = Math.ceil(maxX / unit) * unit;

            if (nodeR.getAllOutNeighbors().size() == 0) {
                move(nodeR, unit, 0);
            } else {
                move(nodeR, tmpDistance + unit, 0);
            }

        }
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
