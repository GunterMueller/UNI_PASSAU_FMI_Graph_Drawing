// =============================================================================
//
//   DummyNode.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.compactor;

import org.graffiti.attributes.HashMapAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.graphics.NodeGraphicAttribute;
import org.graffiti.plugins.algorithms.sugiyama.levelling.AbstractCyclicLevelingAlgorithm;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class DummyNode extends RealNode {
    
    private Edge edge;
    private Node dummyNode;
    
    protected RealNode north;
    protected RealNode south;
    protected int northInvIndex;
    protected int southInvIndex;
    
    protected DummyNode(Edge edge) {
        this.edge = edge;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int addNorth(RealNode node, int invIndex) {
        north = node;
        northInvIndex = invIndex;
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected int addSouth(RealNode node, int invIndex) {
        south = node;
        southInvIndex = invIndex;
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void replaceNorth(int index, RealNode node, int invIndex) {
        north = node;
        northInvIndex = invIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void replaceSouth(int index, RealNode node, int invIndex) {
        south = node;
        southInvIndex = invIndex;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected RealNode[] getNorth() {
        return new RealNode[] { north };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RealNode[] getSouth() {
        return new RealNode[] { south };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node getGraphNode() {
        return dummyNode;
    }

    @Override
    public void makeDummy(Graph graph) {
        dummyNode = graph.addNode();
        
        if (!dummyNode.containsAttribute("graphics")) {
            dummyNode.addAttribute(new NodeGraphicAttribute(), "");
        }
        
        AbstractCyclicLevelingAlgorithm.setDummyShape(dummyNode);
        
        dummyNode.addAttribute(new HashMapAttribute(
                SugiyamaConstants.PATH_SUGIYAMA), "");
        dummyNode.setBoolean(SugiyamaConstants.PATH_DUMMY, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void wireDummy(Graph graph) {
        if (north instanceof VertexNode) {
            edge.setTarget(dummyNode);
        }
        
        graph.addEdge(dummyNode, south.getGraphNode(), true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeType getType() {
        return NodeType.Dummy;
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
