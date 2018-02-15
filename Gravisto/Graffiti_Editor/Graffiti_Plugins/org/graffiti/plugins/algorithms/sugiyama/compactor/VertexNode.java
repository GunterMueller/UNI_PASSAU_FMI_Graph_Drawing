// =============================================================================
//
//   VertexNode.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.compactor;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaConstants;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public class VertexNode extends RealNode {

    protected Node vertex;
    
    protected RealNode[] north;
    protected RealNode[] south;
    protected int[] northInvIndex;
    protected int[] southInvIndex;
    protected int northDegree;
    protected int southDegree;
    
    public VertexNode(Node vertex) {
        this.vertex = vertex;
        
        int northDeg = vertex.getInDegree();
        int southDeg = vertex.getOutDegree();
        
        north = new RealNode[northDeg];
        south = new RealNode[southDeg];
        northInvIndex = new int[northDeg];
        southInvIndex = new int[southDeg];
        
        northDegree = 0;
        southDegree = 0;
    }
    
    @Override
    protected int addNorth(RealNode node, int invIndex) {
        north[northDegree] = node;
        northInvIndex[northDegree] = invIndex;
        return northDegree++;
    }

    @Override
    protected int addSouth(RealNode node, int invIndex) {
        south[southDegree] = node;
        southInvIndex[southDegree] = invIndex;
        return southDegree++;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void replaceNorth(int index, RealNode node, int invIndex) {
        north[index] = node;
        northInvIndex[index] = invIndex;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void replaceSouth(int index, RealNode node, int invIndex) {
        south[index] = node;
        southInvIndex[index] = invIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RealNode[] getNorth() {
        return north;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected RealNode[] getSouth() {
        return south;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Node getGraphNode() {
        return vertex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void makeDummy(Graph graph) {
        vertex.setBoolean(SugiyamaConstants.PATH_DUMMY, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected NodeType getType() {
        return NodeType.Vertex;
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
