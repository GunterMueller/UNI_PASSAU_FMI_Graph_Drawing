// =============================================================================
//
//   LAPVisualizationAlgorithm.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.lapvis;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.graffiti.attributes.SortedCollectionAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.CoordinateAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.algorithm.AbstractAlgorithm;
import org.graffiti.plugin.algorithm.PreconditionException;
import org.graffiti.plugin.parameter.DoubleParameter;
import org.graffiti.plugin.parameter.Parameter;
import org.graffiti.plugin.parameter.SelectionParameter;
import org.graffiti.selection.Selection;

/**
 * @author hanauer
 * @version $Revision$ $Date$
 */
public class LAPVisualizationAlgorithm extends AbstractAlgorithm {

    private static final double DEFAULT_NODE_DISTANCE = 50.0;
    
    private static final double DEFAULT_ARC_HEIGHT = 0.5;
    
    private double nodeDistance = DEFAULT_NODE_DISTANCE;
    
    private double arcHeight = DEFAULT_ARC_HEIGHT;
    
    /**
     * Nodes to arrange.
     */
    private Selection selection;
    
    @Override
    public void setAlgorithmParameters(Parameter<?>[] params) {
        this.parameters = params;
        selection = ((SelectionParameter) params[0]).getSelection();
        nodeDistance = ((DoubleParameter) params[1]).getDouble();
        arcHeight = ((DoubleParameter) params[2]).getDouble();
    }
    
    @Override
    public Parameter<?>[] getAlgorithmParameters() {
        SelectionParameter selParam = new SelectionParameter("Nodes",
                "Nodes to draw arrangement for.");
        DoubleParameter nodeDist = new DoubleParameter(DEFAULT_NODE_DISTANCE, "Node Distance",
                "Distance of nodes in arrangement.", 25.0, 75.0);
        DoubleParameter arcHeight = new DoubleParameter(DEFAULT_ARC_HEIGHT, "Arc Height",
                "Relation between distance of source/target and arc height.", 0.0, 1.0);

        return new Parameter[] { selParam, nodeDist, arcHeight };
    }
    
    @Override
    public void check() throws PreconditionException {

        if ((selection == null) || (selection.getNodes().isEmpty())) {
            throw new PreconditionException(
                    "Please select the nodes you want to arrange.");
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void execute() {
        
        graph.getListenerManager().transactionStarted(this);
        
        List<Node> nodes = selection.getNodes();

        Collections.sort(nodes, new Comparator<Node>() {

            @Override
            public int compare(Node o1, Node o2) {
                CoordinateAttribute c1 = (CoordinateAttribute) o1
                        .getAttribute(GraphicAttributeConstants.COORD_PATH);
                CoordinateAttribute c2 = (CoordinateAttribute) o2
                        .getAttribute(GraphicAttributeConstants.COORD_PATH);

                double diff = c1.getX() - c2.getX();

                return diff < 0 ? -1 : diff > 0 ? 1 : 0;
            }

        });
        
        CoordinateAttribute ca = (CoordinateAttribute) nodes.get(0).getAttribute(GraphicAttributeConstants.COORD_PATH);
        double startX = Math.round(ca.getX());
        double startY = Math.round(ca.getY());
        
        double posX = startX;
        for (Node node: nodes) {
            putNodeAt(node, posX, startY);
            posX += nodeDistance;
        }
        
        List<Edge> edges = selection.getEdges();
        
        double distance;
        for (Edge edge: edges) {
            
            int targetPos = nodes.indexOf(edge.getTarget());
            int sourcePos = nodes.indexOf(edge.getSource());
            
            if ((targetPos == -1) || (sourcePos == -1)) {
                // source or target was not selected
                continue;
            }
            
            distance = (targetPos - sourcePos) * nodeDistance;
            
            // remove old bends
            SortedCollectionAttribute bends = (SortedCollectionAttribute) edge.getAttribute(GraphicAttributeConstants.BENDS_PATH);
            for (String key: bends.getCollection().keySet()) {
                bends.remove(key);
            }
            
            // create new bend if nodes are not neighbors
            if (Math.abs(targetPos - sourcePos) > 1 || distance < 0) {
                CoordinateAttribute bend = new CoordinateAttribute("bend0");
                bend.setX(startX + sourcePos * nodeDistance + distance / 2.0);
                bend.setY(startY - distance * arcHeight);
                bends.add(bend);

                StringAttribute shape = (StringAttribute) edge
                        .getAttribute(GraphicAttributeConstants.SHAPE_PATH);
                shape.setString(GraphicAttributeConstants.SMOOTH_CLASSNAME);
            }
        }
        
        graph.getListenerManager().transactionFinished(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "LAP Visualization";
    }
    
    private void putNodeAt(Node node, double x, double y) {
        CoordinateAttribute ca = (CoordinateAttribute) node.getAttribute(GraphicAttributeConstants.COORD_PATH);
        ca.setX(x);
        ca.setY(y);
    }

}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
