package org.graffiti.plugins.algorithms.hexagonalTrees;

import static org.graffiti.graphics.GraphicAttributeConstants.GRID_PATH;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.EdgeGraphicAttribute;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugins.grids.HexagonalGrid2;

public class HexaToHexa2 extends AbstractAlgorithm {
    // the distance between two points in the hexagonal grid
    protected static final int UNIT = 50;

    private void changeCoordinate(CoordinateAttribute ca) {
        double oldX = ca.getX();
        double oldY = ca.getY();

        double newY = oldY / (Math.sqrt(3) / 2);
        double newX = oldX - UNIT / 2 + newY / 2;

        ca.setX(newX);
        ca.setY(newY);

    }

    private void moveNode(Node n) {
        NodeGraphicAttribute ngaRoot = (NodeGraphicAttribute) n
                .getAttribute("graphics");
        CoordinateAttribute ca = ngaRoot.getCoordinate();
        changeCoordinate(ca);
    }

    private void moveBends(Edge e) {
        EdgeGraphicAttribute edgeAttr = (EdgeGraphicAttribute) e
                .getAttribute("graphics");
        CollectionAttribute ca = edgeAttr.getBends();
        for (Attribute a : ca.getCollection().values()) {
            changeCoordinate((CoordinateAttribute) a);
        }

    }

    private void setGrid() {
        ((GridAttribute) graph.getAttribute(GRID_PATH))
                .setGrid(new HexagonalGrid2());
    }

    public void execute() {
        graph.getListenerManager().transactionStarted(this);
        for (Node n : graph.getNodes()) {
            moveNode(n);
        }
        for (Edge e : graph.getEdges()) {

            moveBends(e);
        }
        setGrid();
        graph.getListenerManager().transactionFinished(this);

    }

    public String getName() {
        return "Hexa to Hexa2";
    }

}
