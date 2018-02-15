/*
 * GraphCheck.java
 * 
 * Copyright (c) 2001-2006 Gravisto Team, University of Passau
 * 
 * Created on Jul 16, 2005
 *
 */

package org.graffiti.plugins.algorithms.graphcheck;

import java.awt.geom.Point2D;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.IntegerParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.GeoThickness.HeapList;

/**
 * @author ma
 * 
 *         TODO To change the template for this generated type comment go to
 *         Window - Preferences - Java - Code Style - Code Templates
 */
public class GraphCheck extends AbstractAlgorithm {

    private IntegerParameter scaleParameter;

    private double scale;

    public GraphCheck() {
        scaleParameter = new IntegerParameter(100, "scale Graph", "");
    }

    /*
     * 
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#getName()
     */
    public String getName() {
        // TODO Auto-generated method stub
        return "Graph check";
    }

    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        return new IntegerParameter[] { scaleParameter };
    }

    @Override
    public void check() throws PreconditionException {
        PreconditionException errors = new PreconditionException();

        if (scaleParameter.getInteger().compareTo(new Integer(1)) < 0) {
            errors.add("the parameter is may not be small than 0");
        }

        // The graph is inherited from AbstractAlgorithm.
        if (graph == null) {
            errors.add("The graph instance may not be null.");
        }

        if (!errors.isEmpty())
            throw errors;
    }

    /*
     * 
     * 
     * @see org.graffiti.plugin.algorithm.Algorithm#execute()
     */
    public void execute() {
        // TODO Auto-generated method stub
        scaleGraph();
    }

    private void scaleGraph() {
        List<Node> nodeList = this.graph.getNodes();
        HeapList heapNode;
        double nodex, nodey;
        CoordinateAttribute ca;

        this.scale = scaleParameter.getInteger().doubleValue() / 100;

        heapNode = new HeapList(1, 1);

        for (int i = 0; i < nodeList.size(); i++) {
            heapNode.setElement(nodeList.get(i));
        }

        setXcoor(heapNode);

        heapNode = new HeapList(1, 2);

        for (int i = 0; i < nodeList.size(); i++) {
            heapNode.setElement(nodeList.get(i));
        }

        setYcoor(heapNode);

        for (int i = 0; i < nodeList.size(); i++) {
            Node node = nodeList.get(i);
            ca = (CoordinateAttribute) node
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            nodex = node.getDouble(GraphicAttributeConstants.COORDX_PATH);
            nodey = node.getDouble(GraphicAttributeConstants.COORDY_PATH);
            ca.setCoordinate(new Point2D.Double(nodex * scale, nodey * scale));
        }
    }

    private void setXcoor(HeapList heapNode) {
        Node node1, node2;
        CoordinateAttribute ca;

        node1 = (Node) heapNode.getElement();
        node2 = (Node) heapNode.getElement();

        double node1x = node1.getDouble(GraphicAttributeConstants.COORDX_PATH);
        double node2x = node2.getDouble(GraphicAttributeConstants.COORDX_PATH);

        if (node1x == node2x) {
            ca = (CoordinateAttribute) node2
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(node2x + 10, node2
                    .getDouble(GraphicAttributeConstants.COORDY_PATH)));
        }

        do {
            node1 = node2;

            if (!heapNode.isEmpty()) {
                node2 = (Node) heapNode.getElement();
            } else {
                node2 = null;
            }

            if (node2 == null) {
                break;
            }

            node1x = node1.getDouble(GraphicAttributeConstants.COORDX_PATH);
            node2x = node2.getDouble(GraphicAttributeConstants.COORDX_PATH);

            if (node1x == node2x) {
                ca = (CoordinateAttribute) node2
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
                ca.setCoordinate(new Point2D.Double(node2x + 10, node2
                        .getDouble(GraphicAttributeConstants.COORDY_PATH)));
            }

        } while (!heapNode.isEmpty());
    }

    private void setYcoor(HeapList heapNode) {
        Node node1, node2;
        CoordinateAttribute ca;

        node1 = (Node) heapNode.getElement();
        node2 = (Node) heapNode.getElement();

        double node1y = node1.getDouble(GraphicAttributeConstants.COORDY_PATH);
        double node2y = node2.getDouble(GraphicAttributeConstants.COORDY_PATH);

        if (node1y == node2y) {
            ca = (CoordinateAttribute) node2
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.COORDINATE);
            ca.setCoordinate(new Point2D.Double(node2
                    .getDouble(GraphicAttributeConstants.COORDX_PATH),
                    node2y + 10));

        }

        do {
            node1 = node2;

            if (!heapNode.isEmpty()) {
                node2 = (Node) heapNode.getElement();
            } else {
                node2 = null;
            }

            if (node2 == null) {
                break;
            }

            node1y = node1.getDouble(GraphicAttributeConstants.COORDY_PATH);
            node2y = node2.getDouble(GraphicAttributeConstants.COORDY_PATH);

            if (node1y == node2y) {
                ca = (CoordinateAttribute) node2
                        .getAttribute(GraphicAttributeConstants.GRAPHICS
                                + Attribute.SEPARATOR
                                + GraphicAttributeConstants.COORDINATE);
                ca.setCoordinate(new Point2D.Double(node2
                        .getDouble(GraphicAttributeConstants.COORDX_PATH),
                        node2y + 10));
            }

        } while (!heapNode.isEmpty());
    }

}
