// =============================================================================
//
//   CoordinatesUtil.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CoordinatesUtil.java 5820 2010-12-10 17:48:20Z gleissner $

package org.graffiti.plugins.algorithms.sugiyama.util;

import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;

import org.graffiti.attributes.AttributeNotFoundException;
import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.graphics.grid.GridAttribute;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.view.Grid;
import org.graffiti.plugins.grids.OrthogonalGrid;
import org.graffiti.plugins.grids.RadialGrid;

/**
 * This class contains helper-methods to update or manipulate the coordinates of
 * a <tt>Graph</tt>.
 * 
 * @author Ferdinand H&uuml;bner
 */
public class CoordinatesUtil {

    /**
     * This class collects the <tt>CoordinateAttribute</tt> in the
     * sugiyama-attribute-tree and sets the <tt>coordinates</tt> of the
     * <tt>Node</tt> in the graphics-attribute-tree.
     * 
     */
    public static void updateRealCoordinates(Graph graph) {
        // graphics.coordinate
        Iterator<Node> iter = graph.getNodesIterator();
        Node current;
        CoordinateAttribute ca;
        CoordinateAttribute realCa;

        while (iter.hasNext()) {
            current = iter.next();
            ca = null;
            try {
                ca = (CoordinateAttribute) current
                        .getAttribute(SugiyamaConstants.PATH_COORDINATE);
            } catch (Exception ex) {

            }
            // if there is no CoordinateAttribute, I don't know what to do!
            if (ca == null) {
                continue;
            }

            // check for the graphics-attribute-tree
            try {
                current.getAttribute(GraphicAttributeConstants.GRAPHICS);
            } catch (AttributeNotFoundException anfe) {
                System.err.println("WARNING: Attribute with id "
                        + GraphicAttributeConstants.GRAPHICS
                        + "does not exist.");
                current.addAttribute(new HashMapAttribute(
                        GraphicAttributeConstants.GRAPHICS), "");
            }
            // check for the CoordinateAttribute and set it
            try {
                realCa = (CoordinateAttribute) current
                        .getAttribute(GraphicAttributeConstants.COORD_PATH);
                realCa.setX(ca.getX());
                realCa.setY(ca.getY());
            } catch (AttributeNotFoundException anfe) {
                System.err.println("WARNING: Attribute with id "
                        + GraphicAttributeConstants.COORD_PATH
                        + " does not exist.");
                realCa = (CoordinateAttribute) ca.copy();
                try {
                    current.addAttribute(realCa,
                            GraphicAttributeConstants.COORD_PATH);
                } catch (AttributeNotFoundException aanfe) {
                    System.err.println("ERROR: The attribute-tree \"graphics\""
                            + " does not exist.");
                }
            }

        }
    }

    /**
     * This method aligns the current nodes of a graph either to a grid or
     * around the point of origin (depending on the value of
     * data.algorithmType). This is needed to make changes is the graph visible
     * to the user.
     * 
     * @param graph
     *            The attached Graph
     * @param data
     *            necessary information for each sugiyama-phase
     */
    public static void updateGraph(Graph graph, SugiyamaData data) {
        if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_HORIZONTAL_SUGIYAMA)) {
            updateGraphOnGrid(graph, data.getLayers(), data);
        } else if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_RADIAL_SUGIYAMA)) {
            updateGraphCyclic(graph, data);
        } else if (data.getAlgorithmType().equals(
                SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
            updateGraphCyclic(graph, data);
        } else
            throw new IllegalArgumentException("I don't know how to display "
                    + "the SugiyamaData.algorithmType "
                    + data.getAlgorithmType());
    }

    public static void addGrid(Graph graph, SugiyamaData data) {
        try {
            GridAttribute gridAttr = (GridAttribute) graph
                    .getAttribute(GraphicAttributeConstants.GRID_PATH);

            if (data.getAlgorithmType().equals(
                    SugiyamaConstants.PARAM_CYCLIC_SUGIYAMA)) {
                gridAttr.setClass(new RadialGrid().getClass());
            } else {
                gridAttr.setClass(new OrthogonalGrid().getClass());
            }

            data.setGridType(gridAttr.getGrid());

            Grid genericGrid = gridAttr.getGrid();
            if (genericGrid.getClass().getName().equals(
                    SugiyamaConstants.GRID_CLASSNAME_ORTHOGONAL)) {
                OrthogonalGrid grid = (OrthogonalGrid) genericGrid;

                Parameter<?>[] gridParams = data.getGridParameters();

                grid.cellHeight = ((IntegerParameter) gridParams[0]).getValue();
                grid.cellWidth = ((IntegerParameter) gridParams[1]).getValue();

                for (int i = 0; i < data.getLayers().getNumberOfLayers(); i++) {
                    ArrayList<Node> layer = data.getLayers().getLayer(i);
                    for (int j = 0; j < layer.size(); j++) {
                        Node current = layer.get(j);
                        CoordinateAttribute ca = (CoordinateAttribute) current
                                .getAttribute(GraphicAttributeConstants.COORD_PATH);
                        ca.setX(j * grid.cellWidth);
                        ca.setY(i * grid.cellHeight);
                    }
                }
            } else if (genericGrid.getClass().getName().equals(
                    SugiyamaConstants.GRID_CLASSNAME_RADIAL)) {
                RadialGrid grid = (RadialGrid) genericGrid;

                // align nodes to the grid
                Iterator<Node> nIter = data.getGraph().getNodesIterator();
                while (nIter.hasNext()) {
                    Node tmp = nIter.next();

                    CoordinateAttribute ca = (CoordinateAttribute) tmp
                            .getAttribute(GraphicAttributeConstants.COORD_PATH);

                    ca.setCoordinate(grid.snapNode(ca.getCoordinate()));
                    updateGraphCyclic(graph, data);
                }
            }
        } catch (AttributeNotFoundException anfe) {
            // do nothing
        }
    }

    /**
     * This method aligns the current nodes of a graph to a grid. This is needed
     * to make changes is the graph visible to the user.
     * 
     * @param graph
     *            The attached Graph
     * @param layers
     *            This object defines the grid itself.
     * @param data
     *            The SugiyamaData bean
     */
    public static void updateGraphOnGrid(Graph graph, NodeLayers layers,
            SugiyamaData data) {

        // TODO The whole method must be rewritten.
        // Bugfix
        if (!graph.containsAttribute(GraphicAttributeConstants.GRID_PATH)) {
            return;
        }
        
        GridAttribute gridAttr = (GridAttribute) graph
                .getAttribute(GraphicAttributeConstants.GRID_PATH);
        Grid genericGrid = gridAttr.getGrid();

        if (genericGrid.getClass().getName().equals(
                SugiyamaConstants.GRID_CLASSNAME_ORTHOGONAL)) {
            OrthogonalGrid grid = (OrthogonalGrid) genericGrid;
            Point2D origin = grid.getOrigin();
            double originX = origin.getX();
            double originY = origin.getY();

            Iterator<Node> iter;
            Node tmp;
            CoordinateAttribute ca;
            int currentNode;

            for (int i = 0; i < layers.getNumberOfLayers(); i++) {
                iter = layers.getLayer(i).iterator();
                currentNode = 0;

                while (iter.hasNext()) {
                    currentNode++;
                    tmp = iter.next();
                    try {
                        ca = (CoordinateAttribute) tmp
                                .getAttribute(GraphicAttributeConstants.COORD_PATH);
                        ca.setY(originY + (i * grid.cellHeight));

                        int xPos = (int) tmp
                                .getDouble(SugiyamaConstants.PATH_XPOS);

                        ca.setX(originX + xPos * grid.cellWidth);

                    } catch (AttributeNotFoundException anfe) {
                        try {
                            ca = (CoordinateAttribute) tmp
                                    .getAttribute(GraphicAttributeConstants.COORD_PATH);

                            // ca.setX(originX + (currentNode - 1) *
                            // grid.cellWidth);
                        } catch (Exception e) {
                            // no gui?
                        }
                    }

                }
            }
        } else {
            double grid_min_x = Double.POSITIVE_INFINITY;
            double grid_min_y = Double.POSITIVE_INFINITY;
            double grid_max_x = Double.NEGATIVE_INFINITY;
            double grid_max_y = Double.NEGATIVE_INFINITY;

            Node tmp;
            double cur_x;
            double cur_y;
            CoordinateAttribute ca;

            Iterator<Node> iter = graph.getNodesIterator();
            while (iter.hasNext()) {
                tmp = iter.next();
                try {
                    ca = (CoordinateAttribute) tmp
                            .getAttribute(GraphicAttributeConstants.COORD_PATH);
                    cur_x = ca.getX();
                    cur_y = ca.getY();
                    if (cur_x < grid_min_x) {
                        grid_min_x = cur_x;
                    }
                    if (cur_x > grid_max_x) {
                        grid_max_x = cur_x;
                    }
                    if (cur_y < grid_min_y) {
                        grid_min_y = cur_y;
                    }
                    if (cur_y > grid_max_y) {
                        grid_max_y = cur_y;
                    }
                } catch (AttributeNotFoundException afne) {

                }
            }

            int nodesOnLevel;
            double levelPadding = (grid_max_y - grid_min_y)
                    / (layers.getNumberOfLayers() - 1);

            if (levelPadding < 5) {
                levelPadding = 50;
            }

            double nodePadding;
            int currentNode;

            for (int i = 0; i < layers.getNumberOfLayers(); i++) {

                iter = layers.getLayer(i).iterator();
                nodesOnLevel = layers.getLayer(i).size();
                if (nodesOnLevel == 1) {
                    nodePadding = 0;
                } else {
                    nodePadding = (grid_max_x - grid_min_x)
                            / (nodesOnLevel - 1);
                    if (nodePadding < 5) {
                        nodePadding = 50;
                    }
                }

                currentNode = 0;

                while (iter.hasNext()) {

                    currentNode++;
                    tmp = iter.next();
                    try {
                        ca = (CoordinateAttribute) tmp
                                .getAttribute(GraphicAttributeConstants.COORD_PATH);
                        ca.setY(grid_min_y + (i * levelPadding));
                        ca.setX(grid_min_x + (currentNode - 1) * nodePadding);
                    } catch (AttributeNotFoundException anfe) {
                        // No GUI
                    }

                }
            }
        }
    }

    public static void makeSpiralEdges(Graph graph) {

        Iterator<Edge> it = graph.getEdgesIterator();
        while (it.hasNext()) {
            it.next().setString(GraphicAttributeConstants.SHAPE_PATH,
                    "org.graffiti.plugins.algorithms.core.SpiralShape");
        }
    }

    /**
     * This method aligns the current nodes of a graph around the point of
     * origin (depending on the value of data.algorithmType). This is needed to
     * make changes is the graph visible to the user.
     * 
     * @param graph
     *            The attached Graph
     * @param data
     *            necessary information for each sugiyama-phase
     */
    public static void updateGraphCyclic(Graph graph, SugiyamaData data) {

        NodeLayers layers = data.getLayers();

        GridAttribute gridAttr = (GridAttribute) graph
                .getAttribute(GraphicAttributeConstants.GRID_PATH);
        Grid genericGrid = gridAttr.getGrid();

        if (genericGrid.getClass().getName().equals(
                SugiyamaConstants.GRID_CLASSNAME_RADIAL)) {
            // fix sector count of the radial grid
            RadialGrid radialGrid = (RadialGrid) genericGrid;
            radialGrid.sectorCount = layers.getNumberOfLayers();
            // fix circle distance of the radial grid
            radialGrid.circleDistance = data.getCyclicLayoutRadiusDelta();
        }

        // spread the layers equally counterclockwise around the full circle
        double angleBetweenLayers = -(2 * PI) / layers.getNumberOfLayers();

        // for each layer
        for (int i = 0; i < layers.getNumberOfLayers(); i++) {
            ArrayList<Node> layer = layers.getLayer(i);

            // for each node of the current layer
            for (int j = 0; j < layer.size(); j++) {
                double radius = data.getCyclicLayoutRadiusOffset()
                        + (j * data.getCyclicLayoutRadiusDelta());
                try {
                    Node node = layer.get(j);
                    CoordinateAttribute ca = (CoordinateAttribute) node
                            .getAttribute(GraphicAttributeConstants.COORD_PATH);
                    ca.setX(cos(i * angleBetweenLayers) * radius);
                    ca.setY(sin(i * angleBetweenLayers) * radius);
                } catch (AttributeNotFoundException anfe) {
                    // No GUI
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
