// =============================================================================
//
//   EdgeAdapterTest.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package tests.graffiti.plugins.algorithms.mst;

import junit.framework.TestCase;

import org.graffiti.graph.Edge;
import org.graffiti.plugins.algorithms.mst.adapters.EdgeAdapter;
import org.graffiti.plugins.algorithms.mst.adapters.EdgeAdapterFactory;

/**
 * @author Harald
 * @version $Revision$ $Date$
 */
public class EdgeAdapterTest extends TestCase {

    private GraphFixture graphFixture;
    private Edge edge;
    private EdgeAdapter edgeAdapter;

    /*
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        graphFixture = new GraphFixture();
        graphFixture.setUpConnectedCircles();
        edge = graphFixture.getEdges().iterator().next();
        edgeAdapter = new EdgeAdapterFactory().createAdapter(edge);
    }

    /*
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testNoEdgeAdapterEqualsNull() {
        assertFalse(edgeAdapter.equalsEdge(null));
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
