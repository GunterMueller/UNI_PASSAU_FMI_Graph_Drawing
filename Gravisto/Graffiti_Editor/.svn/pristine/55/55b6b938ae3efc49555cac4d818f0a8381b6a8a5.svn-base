package org.visnacom.sugiyama.test;

import org.visnacom.model.Node;
import org.visnacom.sugiyama.algorithm.*;
import org.visnacom.sugiyama.eval.DummyPicture;
import org.visnacom.sugiyama.model.SugiCompoundGraph;

import junit.framework.TestCase;

public class StaticLayoutTest extends TestCase {

    public StaticLayoutTest() {
        super();
    }

    public StaticLayoutTest(String arg0) {
        super(arg0);
    }


    public final void testMultipleEdges() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        Node n1 = s.newLeaf(s.getRoot());
        Node n2 = s.newLeaf(s.getRoot());
        Node n3 = s.newLeaf(n1);
        Node n4 = s.newLeaf(n2);
        Node n5 = s.newLeaf(n1);
        Node n6 = s.newLeaf(n3);
        s.newEdge(n6, n4);
        s.newEdge(n6, n4);
        s.newEdge(n5, n6);
        s.newEdge(n6, n5);

        Hierarchization.hierarchize(s);
        Normalization.normalize(s);
        VertexOrdering.order(s, 1);
        MetricLayout.layout(s,4);
        DummyPicture.show(s);
    }
}
