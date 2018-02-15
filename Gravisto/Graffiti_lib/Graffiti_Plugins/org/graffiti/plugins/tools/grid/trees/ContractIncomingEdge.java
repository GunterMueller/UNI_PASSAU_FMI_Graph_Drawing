package org.graffiti.plugins.tools.grid.trees;

import static org.graffiti.graphics.GraphicAttributeConstants.GRID_PATH;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugins.grids.HexagonalGrid;
import org.graffiti.plugins.grids.HexagonalGrid2;
import org.graffiti.plugins.grids.OctogonalGrid;
import org.graffiti.plugins.grids.OrthogonalGrid;
import org.graffiti.selection.Selection;
import org.graffiti.util.CoreGraphEditing;

public class ContractIncomingEdge extends AbstractAlgorithm {
    Selection selection;

    Node currNode;

    boolean moveOut = false;

    @Override
    protected Parameter<?>[] getAlgorithmParameters() {
        return new Parameter[] { new SelectionParameter("root node",
                "the root node of the tree to move in") };
    }

    @Override
    protected void setAlgorithmParameters(Parameter<?>[] params) {
        selection = ((SelectionParameter) params[0]).getSelection();
    }

    @Override
    public void check() throws PreconditionException {
        if (selection.getNodes().size() != 1)
            throw new PreconditionException(
                    "The root of the subtree has to be selected.");
        Grid grid = ((GridAttribute) (graph.getAttribute(GRID_PATH))).getGrid();
        if (!(grid instanceof HexagonalGrid || grid instanceof OrthogonalGrid
                || grid instanceof HexagonalGrid2 || grid instanceof OctogonalGrid))
            throw new PreconditionException(
                    "There is no grid to calculate the step value for the movement.");

        currNode = selection.getNodes().get(0);
    }

    private void moveNode(Node n, double dx, double dy) {
        CoordinateAttribute ca = (CoordinateAttribute) n
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca.setX(ca.getX() + dx);
        ca.setY(ca.getY() + dy);
    }

    private void moveBends(Edge e, double dx, double dy) {
        SortedCollectionAttribute sca = (SortedCollectionAttribute) e
                .getAttribute(GraphicAttributeConstants.BENDS_PATH);
        for (Attribute a : sca.getCollection().values()) {
            CoordinateAttribute ca = (CoordinateAttribute) a;
            ca.setX(ca.getX() + dx);
            ca.setY(ca.getY() + dy);
        }
    }

    public void execute() {
        graph.getListenerManager().transactionStarted(this);
       
        LinkedHashSet<Node> list =  (LinkedHashSet)currNode.getAllInNeighbors();
        
        Node parent = null;
        for (Node node : currNode.getInNeighbors()) {
            parent = node;
        }
        
        
        CoordinateAttribute caParent = (CoordinateAttribute) parent
                .getAttribute(GraphicAttributeConstants.COORD_PATH);
       
       
            CoordinateAttribute caCurrNode = (CoordinateAttribute) currNode
                    .getAttribute(GraphicAttributeConstants.COORD_PATH);
            List<Node> subgraphNodes = CoreGraphEditing
                    .selectReachableSubgraph(currNode).getNodes();
            List<Edge> subgraphEdges = CoreGraphEditing
                    .selectReachableSubgraph(currNode).getEdges();

            double dx = (caParent.getX() - caCurrNode.getX());
            double dy = (caParent.getY() - caCurrNode.getY());
            double length = Math.sqrt(dx * dx + dy * dy);

            Grid grid = ((GridAttribute) (graph.getAttribute(GRID_PATH)))
                    .getGrid();

            int gridWidth = 0;
            int gridHeight = 0;
            if (grid instanceof HexagonalGrid) {

                gridWidth = (Integer) (graph
                        .getAttribute("graphics.grid.parameters.hexCellWidth"))
                        .getValue();
                gridHeight = gridWidth;

                if (moveOut) {
                    dx = -dx;
                    dy = -dy;
                }

                dx = dx / length * gridWidth;
                dy = dy / length * gridHeight;

            }
            // to move subtrees in or out in a hexagrid2 or an orthogonal grid
            else if ((grid instanceof HexagonalGrid2)
                    || (grid instanceof OrthogonalGrid)
                    || grid instanceof OctogonalGrid) {
                gridWidth = (Integer) (graph
                        .getAttribute("graphics.grid.parameters.cellWidth"))
                        .getValue();

                gridHeight = (Integer) (graph
                        .getAttribute("graphics.grid.parameters.cellHeight"))
                        .getValue();

                if (moveOut) {
                    dx = -dx;
                    dy = -dy;
                }

                if (dx != 0) {
                    dx = dx / Math.abs(dx) * gridWidth;
                }

                if (dy != 0) {
                    dy = dy / Math.abs(dy) * gridHeight;
                }

            }
            for (Node n : subgraphNodes) {
                moveNode(n, dx, dy);
            }
            for (Edge e : subgraphEdges) {
                moveBends(e, dx, dy);
            }

        
        graph.getListenerManager().transactionFinished(this);
    }

    public String getName() {
        return "Move subtrees in";
    }

}
