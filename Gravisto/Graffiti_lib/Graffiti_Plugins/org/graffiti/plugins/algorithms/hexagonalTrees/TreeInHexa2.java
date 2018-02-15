package org.graffiti.plugins.algorithms.hexagonalTrees;

import java.util.Iterator;

import org.graffiti.attributes.LinkedHashMapAttribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.views.defaults.PolyLineEdgeShape;

public abstract class TreeInHexa2 extends AbstractAlgorithm {
    // the distance between two points in the hexagonal grid
    public static final int UNIT = 50;

    protected Node root = null;

    protected int depth;

    /*
     * @see
     * org.graffiti.plugin.algorithm.Algorithm#setParameters(org.graffiti.plugin
     * .parameter.Parameter[])
     */
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
    }

    /**
     * calculates the tree depth recursively
     * 
     * @param n
     *            a node
     * @param level
     *            current depth
     * @return the depth of the subtree under n
     */
    protected int calculateTreeDepth(Node n, int level) {
        int maxDepth = level;
        for (Node x : n.getOutNeighbors()) {
            maxDepth = Math.max(maxDepth, calculateTreeDepth(x, level + 1));
        }
        return maxDepth;
    }

    /*
     * removes all bends in the tree under root
     */
    protected void removeBends(Node root) {
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

    protected void placeAtPosition(Node toPlace, double length, Node root,
            int position) {
        if (position == 0) {
            placeRightOf(toPlace, length, root);
        } else if (position == 1) {
            placeRightBelowOf(toPlace, length, root);
        } else if (position == 2) {
            placeBelowOf(toPlace, length, root);
        } else if (position == 3) {
            placeLeftOf(toPlace, length, root);
        } else if (position == 4) {
            placeLeftAboveOf(toPlace, length, root);
        } else if (position == 5) {
            placeAboveOf(toPlace, length, root);
        }
    }

    protected void placeBelowOf(Node toPlace, double length, Node root) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        NodeGraphicAttribute ngaToPlace = (NodeGraphicAttribute) toPlace
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();

        ngaToPlace.getCoordinate().setY(rootY + (length * UNIT));
        ngaToPlace.getCoordinate().setX(rootX);
    }

    protected void placeAboveOf(Node toPlace, double length, Node root) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        NodeGraphicAttribute ngaToPlace = (NodeGraphicAttribute) toPlace
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();

        ngaToPlace.getCoordinate().setY(rootY - (length * UNIT));
        ngaToPlace.getCoordinate().setX(rootX);
    }

    protected void placeRightOf(Node toPlace, double length, Node root) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        NodeGraphicAttribute ngaToPlace = (NodeGraphicAttribute) toPlace
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();

        ngaToPlace.getCoordinate().setY(rootY);
        ngaToPlace.getCoordinate().setX(rootX + (length * UNIT));
    }

    protected void placeLeftOf(Node toPlace, double length, Node root) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        NodeGraphicAttribute ngaToPlace = (NodeGraphicAttribute) toPlace
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();

        ngaToPlace.getCoordinate().setY(rootY);
        ngaToPlace.getCoordinate().setX(rootX - (length * UNIT));
    }

    protected void placeRightBelowOf(Node toPlace, double length, Node root) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        NodeGraphicAttribute ngaToPlace = (NodeGraphicAttribute) toPlace
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();

        ngaToPlace.getCoordinate().setY(rootY + (length * UNIT));
        ngaToPlace.getCoordinate().setX(rootX + (length * UNIT));
    }

    protected void placeLeftAboveOf(Node toPlace, double length, Node root) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        NodeGraphicAttribute ngaToPlace = (NodeGraphicAttribute) toPlace
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();

        ngaToPlace.getCoordinate().setY(rootY - (length * UNIT));
        ngaToPlace.getCoordinate().setX(rootX - (length * UNIT));
    }

    protected void placeLeftBelowOf(Node toPlace, double length, Node root) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        NodeGraphicAttribute ngaToPlace = (NodeGraphicAttribute) toPlace
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();
        Edge e = this.graph.getEdges(root, toPlace).iterator().next();
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) e
                .getAttribute("graphics");
        ega.getBends().add(
                new CoordinateAttribute("bend0", rootX, rootY + length * UNIT));
        ega.setShape(PolyLineEdgeShape.class.getName());

        ngaToPlace.getCoordinate().setY(rootY + (length * UNIT));
        ngaToPlace.getCoordinate().setX(rootX - (length * UNIT));
    }

    protected void placeRightAboveOf(Node toPlace, double length, Node root) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        NodeGraphicAttribute ngaToPlace = (NodeGraphicAttribute) toPlace
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();
        Edge e = this.graph.getEdges(root, toPlace).iterator().next();
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) e
                .getAttribute("graphics");
        ega.getBends().add(
                new CoordinateAttribute("bend0", rootX, rootY - length * UNIT));
        ega.setShape(PolyLineEdgeShape.class.getName());

        ngaToPlace.getCoordinate().setY(rootY - (length * UNIT));
        ngaToPlace.getCoordinate().setX(rootX + (length * UNIT));
    }

    protected void placeDoubleRightBelowOf(Node toPlace, double length,
            Node root) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        NodeGraphicAttribute ngaToPlace = (NodeGraphicAttribute) toPlace
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();

        Edge e = this.graph.getEdges(root, toPlace).iterator().next();
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) e
                .getAttribute("graphics");
        ega.getBends().add(
                new CoordinateAttribute("bend0", rootX + length * UNIT, rootY));
        ega.setShape(PolyLineEdgeShape.class.getName());

        ngaToPlace.getCoordinate().setY(rootY + (length * UNIT));
        ngaToPlace.getCoordinate().setX(rootX + (2 * length * UNIT));
    }

    protected void placeRightDoubleBelowOf(Node toPlace, double length,
            Node root) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        NodeGraphicAttribute ngaToPlace = (NodeGraphicAttribute) toPlace
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();

        Edge e = this.graph.getEdges(root, toPlace).iterator().next();
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) e
                .getAttribute("graphics");

        ega.getBends()
                .add(
                        new CoordinateAttribute("bend0", rootX, rootY
                                + (length * UNIT)));
        ega.setShape(PolyLineEdgeShape.class.getName());

        ngaToPlace.getCoordinate().setY(rootY + (2 * length * UNIT));
        ngaToPlace.getCoordinate().setX(rootX + (length * UNIT));
    }

    protected void placeDoubleLeftAboveOf(Node toPlace, double length, Node root) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        NodeGraphicAttribute ngaToPlace = (NodeGraphicAttribute) toPlace
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();

        Edge e = this.graph.getEdges(root, toPlace).iterator().next();
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) e
                .getAttribute("graphics");
        ega.getBends().add(
                new CoordinateAttribute("bend0", rootX - length * UNIT, rootY));
        ega.setShape(PolyLineEdgeShape.class.getName());

        ngaToPlace.getCoordinate().setY(rootY - (length * UNIT));
        ngaToPlace.getCoordinate().setX(rootX - (2 * length * UNIT));
    }

    protected void placeLeftDoubleAboveOf(Node toPlace, double length, Node root) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        NodeGraphicAttribute ngaToPlace = (NodeGraphicAttribute) toPlace
                .getAttribute("graphics");
        double rootX = ngaRoot.getCoordinate().getX();
        double rootY = ngaRoot.getCoordinate().getY();

        Edge e = this.graph.getEdges(root, toPlace).iterator().next();
        EdgeGraphicAttribute ega = (EdgeGraphicAttribute) e
                .getAttribute("graphics");

        ega.getBends()
                .add(
                        new CoordinateAttribute("bend0", rootX, rootY
                                - (length * UNIT)));
        ega.setShape(PolyLineEdgeShape.class.getName());

        ngaToPlace.getCoordinate().setY(rootY - (2 * length * UNIT));
        ngaToPlace.getCoordinate().setX(rootX - (length * UNIT));
    }

    protected void placeRoot() {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) root
                .getAttribute("graphics");
        ngaRoot.getCoordinate().setY(0);
        ngaRoot.getCoordinate().setX(0);
    }

}
