// =============================================================================
//
//   IncubatorNode.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.compactor;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;

import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugins.transitional.BendsAdapter;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class IncubatorNode {
    private static class InitialXComparator implements Comparator<IncubatorNode> {
        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(IncubatorNode o1, IncubatorNode o2) {
            return Double.compare(o1.x, o2.x);
        }
    };
    
    public static final InitialXComparator INITIAL_X_COMPARATOR = new InitialXComparator();
    
    protected RealNode node;
    
    private double x;
    
    private ArrayList<IncubatorNode> north;
    private ArrayList<IncubatorNode> south;
    
    public IncubatorNode(RealNode node, double x) {
        this.node = node;
        this.x = x;
        
        north = new ArrayList<IncubatorNode>();
        south = new ArrayList<IncubatorNode>();
    }
    
    public void addNorth(IncubatorNode node) {
        north.add(node);
    }
    
    public void addSouth(IncubatorNode node) {
        south.add(node);
    }
    
    void initEdges(int level, ArrayList<IncubatorNode>[] levels, Map<Node, IncubatorNode> nodeMap, TreeSet<Double> levelYs) {
        Node source = ((VertexNode) node).vertex;
        double sourceX = source.getDouble(GraphicAttributeConstants.COORDX_PATH);
        double sourceY = source.getDouble(GraphicAttributeConstants.COORDY_PATH);
        NavigableSet<Double> tailSet = levelYs.tailSet(sourceY, false);
        
        for (Edge edge : source.getAllOutEdges()) {
            Iterator<Double> lvlIter = tailSet.iterator();
            IncubatorNode target = nodeMap.get(edge.getTarget());
            double targetY = target.node.getGraphNode().getDouble(GraphicAttributeConstants.COORDY_PATH);
            double x = sourceX;
            double y = lvlIter.next();
            IncubatorNode prev = this;
            int lvl = level;
            BendsAdapter bends = new BendsAdapter(edge);
            Iterator<Point2D> bendIter = bends.iterator();
            Point2D nextBend = bendIter.hasNext() ? bendIter.next() : null;
            
            while (y < targetY) {
                if (nextBend != null && y == nextBend.getY()) { // sic!
                    x = nextBend.getX();
                    nextBend = bendIter.hasNext() ? bendIter.next() : null;
                }
                
                //
                DummyNode dummyNode = new DummyNode(edge);
                IncubatorNode incubatorNode = new IncubatorNode(dummyNode, x);

                incubatorNode.addNorth(prev);
                prev.addSouth(incubatorNode);
                prev = incubatorNode;

                lvl++;
                levels[lvl].add(incubatorNode);
                //
                
                y = lvlIter.next();
            }
            
            target.addNorth(prev);
            prev.addSouth(target);
            
            bends.clear();
            bends.commit();
        }
    }
    
    void sort() {
        Collections.sort(north, INITIAL_X_COMPARATOR);
        
        int i = 0;
        for (IncubatorNode nn : north) {
            int index = nn.node.addSouth(node, i);
            node.addNorth(nn.node, index);
            i++;
        }
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
