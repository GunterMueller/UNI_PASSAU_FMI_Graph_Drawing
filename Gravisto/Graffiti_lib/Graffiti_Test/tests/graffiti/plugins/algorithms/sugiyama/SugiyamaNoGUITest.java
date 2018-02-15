// =============================================================================
//
//   GenericSugiyamaTest.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: SugiyamaNoGUITest.java 5771 2010-05-07 18:46:57Z gleissner $

package tests.graffiti.plugins.algorithms.sugiyama;

import junit.framework.TestCase;

import org.graffiti.graph.AdjListGraph;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;
import org.graffiti.plugins.algorithms.sugiyama.Sugiyama;
import org.graffiti.plugins.algorithms.sugiyama.crossmin.BaryCenter;
import org.graffiti.plugins.algorithms.sugiyama.decycling.DFSDecycling;
import org.graffiti.plugins.algorithms.sugiyama.layout.SocialBrandesKoepf;
import org.graffiti.plugins.algorithms.sugiyama.levelling.LongestPath;
import org.graffiti.plugins.algorithms.sugiyama.util.SugiyamaData;

/**
 * @author Ferdinand H�bner
 */
public class SugiyamaNoGUITest extends TestCase {
    private AdjListGraph g;
    private Node[] nodes;
    private Edge[] edges;

    public SugiyamaNoGUITest(String name) {
        super(name);
    }

    public static void main(String[] args) {
        junit.textui.TestRunner.run(SugiyamaNoGUITest.class);
    }

    public void testSugiyama() {
        Sugiyama s = new Sugiyama();
        SugiyamaData d = s.getData();
        DFSDecycling p1 = new DFSDecycling();
        LongestPath p2 = new LongestPath();
        BaryCenter p3 = new BaryCenter();
        SocialBrandesKoepf p4 = new SocialBrandesKoepf();
        d.getSelectedAlgorithms()[0] = p1;
        d.getSelectedAlgorithms()[1] = p2;
        d.getSelectedAlgorithms()[2] = p3;
        d.getSelectedAlgorithms()[3] = p4;
        p1.setData(d);
        p2.setData(d);
        p3.setData(d);
        p4.setData(d);
        s.attach(g);
        s.setParameters(s.getParameters());
        try {
            s.check();
        } catch (Exception e) {
            fail();
        }
        s.execute();
    }

    @Override
    protected void setUp() {
        g = new AdjListGraph();
        nodes = new Node[4];

        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = g.addNode();
        }

        edges = new Edge[4];
        edges[0] = g.addEdge(nodes[0], nodes[1], true);
        edges[1] = g.addEdge(nodes[1], nodes[2], true);
        edges[2] = g.addEdge(nodes[2], nodes[3], true);
        edges[3] = g.addEdge(nodes[3], nodes[0], true);

    }

    @Override
    protected void tearDown() {
        g.clear();
        nodes = null;
        edges = null;
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
