// =============================================================================
//
//   AnyRealNode.java
//
//   Copyright (c) 2001-2010, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.sugiyama.compactor;

import java.awt.Color;

import org.graffiti.graph.Graph;
import org.graffiti.graph.Node;

/**
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 */
public abstract class RealNode extends AnyNode {
    
    protected Color color;//TEMP
    
    protected RealNode west;
    
    protected abstract int addNorth(RealNode node, int invIndex);
    
    protected abstract int addSouth(RealNode node, int invIndex);
    
    protected abstract void replaceNorth(int index, RealNode node, int invIndex);
    
    protected abstract void replaceSouth(int index, RealNode node, int invIndex);
    
    protected abstract RealNode[] getNorth();
    
    protected abstract RealNode[] getSouth();
    
    protected abstract Node getGraphNode();
    
    public abstract void makeDummy(Graph graph);
    
    public void wireDummy(Graph graph) {
    }
}

// -----------------------------------------------------------------------------
//   end of file
// -----------------------------------------------------------------------------
