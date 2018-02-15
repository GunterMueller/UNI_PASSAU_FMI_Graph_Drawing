// =============================================================================
//
//   IdentifyOuterFaceAlgorithm.java
//
//   Copyright (c) 2001-2014, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.tutte;

import java.awt.Color;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.ColorAttribute;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.BooleanParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugins.algorithms.SDlayout.ObjectAttribute;
import org.graffiti.plugins.algorithms.connectivity.Connect;
import org.graffiti.plugins.algorithms.rotation.Rotationsystem;

/**
 * @author hanauer
 * @version $Revision$ $Date$
 */
public class IdentifyOuterFaceAlgorithm extends AbstractAlgorithm {
    
    private List<Node> nodesOnOuterFace = new LinkedList<Node>();
    
    private List<Edge> edgesOnOuterFace = new LinkedList<Edge>();
    
    private boolean colorNodes = false;
    
    private boolean colorEdges = false;
    
    private Color coloring = Color.RED;
    
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        
        BooleanParameter colorNodes = new BooleanParameter(false, "Color nodes?",
                "If selected, the nodes on the outer face are colored.");
        BooleanParameter colorEdges = new BooleanParameter(true, "Color edges?",
                "If selected, the edges on the outer face are colored.");

        return new Parameter[] { colorNodes, colorEdges };
    }
    
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        colorNodes = ((BooleanParameter) params[0]).getBoolean();
        colorEdges = ((BooleanParameter) params[1]).getBoolean();
    }

    @Override
    public void check() throws PreconditionException {
        if (graph.getEdges().size() == 0) {
            throw new PreconditionException(
                    "Graph must have at least one edge.");
        }
        Connect con = new Connect();
        con.attach(graph);
        try {
            con.check();
        } catch (PreconditionException e) {
            throw e;
        }
        
        con.execute();
        if (! con.isConnected()) {
            throw new PreconditionException(
                    "Graph must be connected.");
        }
        con.reset();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        
        nodesOnOuterFace.clear();
        edgesOnOuterFace.clear();
        
        graph.getListenerManager().transactionStarted(this);
        
        Rotationsystem rotationSystem = new Rotationsystem();
        rotationSystem.attach(graph);
        try {
            rotationSystem.check();
        } catch (PreconditionException e) {
            // all preconditions should have already been checked..
            graph.getListenerManager().transactionFinished(this);
            return;
        }
        rotationSystem.execute();
        
        List<Node> nodes = graph.getNodes();

        Node nodeAtMinX = null;
        Edge edgeAtMinX = null;

        double minX = Double.MAX_VALUE;
        boolean isNode = true;

        for (Node n : nodes) {
            double x = n.getDouble(GraphicAttributeConstants.COORDX_PATH);
            if (x < minX) {
                minX = x;
                nodeAtMinX = n;
            }
        }

        assert nodeAtMinX != null;

        for (Edge e : graph.getEdges()) {
            SortedCollectionAttribute bends = (SortedCollectionAttribute) e
                    .getAttribute(GraphicAttributeConstants.BENDS_PATH);
            Map<String, Attribute> map = bends.getCollection();

            for (Map.Entry<String, Attribute> entry : map.entrySet()) {
                CoordinateAttribute bend = (CoordinateAttribute) entry
                        .getValue();
                if (bend.getX() < minX) {
                    minX = bend.getX();
                    edgeAtMinX = e;
                    isNode = false;
                }
            }
        }

        if (isNode) {
            startWithNode(nodeAtMinX);
        } else {
            startWithEdge(edgeAtMinX, minX);
        }
        completeOuterFace(edgesOnOuterFace, nodesOnOuterFace);
        
        if (colorEdges)
            colorOuterFaceEdges(edgesOnOuterFace);
        if (colorNodes)
            colorOuterFaceNodes(nodesOnOuterFace);
        
        rotationSystem.reset();
        
        graph.getListenerManager().transactionFinished(this);
    }
    
    /**
     * Start traversing the outer face at the given node.
     * 
     * @param n the node to start with
     */
    private void startWithNode(Node n) {
        ObjectAttribute oa = (ObjectAttribute) n
                .getAttribute(".rotationsystem");
        @SuppressWarnings("unchecked")
        List<Edge> rotationSystem = (List<Edge>) oa.getValue();

        Edge nextEdge = null;

        for (Edge e : rotationSystem) {
            double angle;
            if (e.getSource() == n) {
                angle = e.getDouble("rotation.sourceAngle");
            } else {
                angle = e.getDouble("rotation.targetAngle");
            }

            if (angle < 0) {
                nextEdge = e;
            }
        }

        if (nextEdge == null) {
            nextEdge = rotationSystem.get(rotationSystem.size() - 1);
        }
        
        Node otherNode = nextEdge.getSource() == n ? nextEdge.getTarget() : nextEdge.getSource();
        
        if (n.getEdges().size() == 1) {
            nodesOnOuterFace.add(otherNode);
            nodesOnOuterFace.add(n);
            nodesOnOuterFace.add(otherNode);
            
            edgesOnOuterFace.add(nextEdge);
            edgesOnOuterFace.add(nextEdge);
        } else {
           nodesOnOuterFace.add(n);
           nodesOnOuterFace.add(otherNode);

           edgesOnOuterFace.add(nextEdge);
        }
    }
    
    /**
     * Start traversing the outer face at the given edge.
     * 
     * @param e the edge to start with
     * @param minX the minimum x coordinate
     */
    private void startWithEdge(Edge e, double minX) {
        SortedCollectionAttribute bends = (SortedCollectionAttribute) e
                .getAttribute(GraphicAttributeConstants.BENDS_PATH);
        Map<String, Attribute> map = bends.getCollection();

        boolean srcToTgt = true;
        double lastY = e.getSource().getDouble(
                GraphicAttributeConstants.COORDY_PATH);

        boolean foundBend = false;
        double minBendY = lastY;

        for (Map.Entry<String, Attribute> entry : map.entrySet()) {
            CoordinateAttribute bend = (CoordinateAttribute) entry
                    .getValue();

            if (bend.getX() == minX) {
                // found min bend
                double y = bend.getY();
                minBendY = y;
                if (lastY < y) {
                    srcToTgt = false;
                    break;
                } else if (lastY > y) {
                    break;
                } else {
                    foundBend = true;
                }
            } else {
                if (!foundBend) {
                    lastY = bend.getY();
                } else {
                    if (lastY < bend.getY()) {
                        srcToTgt = false;
                    }
                    // hack, indicate that comparison with target node not
                    // necessary
                    foundBend = false;
                    break;
                }
            }
        }
        if (foundBend) {
            // min bend is last bend, compare with target coords
            if (minBendY < e.getTarget().getDouble(
                    GraphicAttributeConstants.COORDY_PATH)) {
                srcToTgt = false;
            }
        }

        edgesOnOuterFace.add(e);

        if (srcToTgt) {
            nodesOnOuterFace.add(e.getSource());
            nodesOnOuterFace.add(e.getTarget());
        } else {
            nodesOnOuterFace.add(e.getTarget());
            nodesOnOuterFace.add(e.getSource());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "Identify the outer face";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        nodesOnOuterFace.clear();
        edgesOnOuterFace.clear();
        super.reset();
    }
    
    /**
     * Finds the successor edge of <code>lastEdge</code> in
     * a traversal of the outer face.
     * 
     * @param lastEdge the predecessor edge
     * @param lastNode the node both edges have in common
     * @return the successor edge of <code>lastEdge</code> at <code>lastNode</code>
     */
    private Edge getNextEdge(Edge lastEdge, Node lastNode) {

        ObjectAttribute oa = (ObjectAttribute) 
                lastNode.getAttribute(".rotationsystem");
        @SuppressWarnings("unchecked")
        List<Edge> rotationSystem = (List<Edge>) oa.getValue();
        
        Edge next = rotationSystem.get(0);
        if (next == lastEdge) {
            next = rotationSystem.get(rotationSystem.size() - 1);
        } else {
            for (Edge e : rotationSystem) {
                if (e == lastEdge) {
                    break;
                } else {
                    next = e;
                }
            }
        }

        return next;
    }
    
    
    /**
     * Finishes the traversal of the outer face.
     * 
     * @param edges the list of edges already known to be on the outer face
     * @param nodes the list of nodes already known to be on the outer face
     */
    private void completeOuterFace(List<Edge> edges, List<Node> nodes) {
        
        Node firstNode = nodes.get(0);
        Edge firstEdge = edges.get(0);
        
        Node lastNode = nodes.get(nodes.size() - 1);
        Edge lastEdge = edges.get(edges.size() - 1);
        
        boolean traversalFinished = false;
        
        while (!traversalFinished) {
            lastEdge = getNextEdge(lastEdge, lastNode);
            
            if (lastEdge == firstEdge && lastNode == firstNode) {
                nodes.remove(nodes.size() - 1);
                traversalFinished = true;
            } else {
                if (lastEdge.getSource() == lastNode) {
                    lastNode = lastEdge.getTarget();
                } else {
                    lastNode = lastEdge.getSource();
                }

                edges.add(lastEdge);
                nodes.add(lastNode);
            }
        }
    }
    
    /**
     * Colors the edges of the outer face.
     * 
     * @param edges the edges of the outer face
     */
    private void colorOuterFaceEdges(List<Edge> edges) {
        
        for (Edge e : edges) {
            ColorAttribute ca = (ColorAttribute) e
                    .getAttribute(GraphicAttributeConstants.GRAPHICS
                            + Attribute.SEPARATOR
                            + GraphicAttributeConstants.FRAMECOLOR);
            ca.setColor(coloring);
        }
    }
    
    /**
     * Colors the nodes of the outer face.
     * 
     * @param nodes the nodes of the outer face
     */
    private void colorOuterFaceNodes(List<Node> nodes) {
        
        for (Node n : nodes) {
            ColorAttribute ca = (ColorAttribute) n
                    .getAttribute(GraphicAttributeConstants.FILLCOLOR_PATH);
            ca.setColor(coloring);
        }
    }
    
    /**
     * Returns the list of vertices on the outer face in counter-clockwise order.
     * 
     * @return the vertices on the outer face
     */
    public List<Node> getNodesOnOuterFace() {
        return Collections.unmodifiableList(nodesOnOuterFace);
    }
    
    /**
     * Returns the list of edges on the outer face in counter-clockwise order.
     * 
     * @return the edges on the outer face
     */
    public List<Edge> getEdgesOnOuterFace() {
        return Collections.unmodifiableList(edgesOnOuterFace);
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
