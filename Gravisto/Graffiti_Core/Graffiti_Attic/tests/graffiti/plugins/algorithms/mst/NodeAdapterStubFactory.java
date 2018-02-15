// =============================================================================
//
//   NodeAdapterStubFactory.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package tests.graffiti.plugins.algorithms.mst;

import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.mst.adapters.NodeAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.NodeAdapterFactory;

/**
 * @author Harald
 * @version $Revision$ $Date$
 */
public class NodeAdapterStubFactory extends NodeAdapterFactory {
    @Override
    public NodeAdapter createNodeAdapter(Node n) {
        return new NodeAdapterStub();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
