/*==============================================================================
*
*   NormalizationTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: NormalizationTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import org.visnacom.model.*;
import org.visnacom.sugiyama.algorithm.*;
import org.visnacom.sugiyama.eval.DummyPicture;
import org.visnacom.sugiyama.model.*;

import junit.framework.TestCase;

/**
 *
 */
public class NormalizationTest extends TestCase {
    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(NormalizationTest.class);
    }

    /**
     *
     */
    public final void test1() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        SugiNode n1 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n2 = (SugiNode) s.newLeaf(n1);
        SugiNode n3 = (SugiNode) s.newLeaf(n1);
        s.newEdge(n2, n3);
        Hierarchization.hierarchize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        //DummyPicture.write(s, "Normalization" + getName()+ ".jpg");
        Normalization.normalize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        DummyPicture.show(s);
//        DummyPicture.write(s, "Normalization" + getName());

    }

    /**
     *
     */
    public final void test2() {
        CompoundGraph c = new Static();
        Node n1 =  c.newLeaf(c.getRoot());
        Node n2 =  c.newLeaf(n1);
        Node n3 = c.newLeaf(n1);
        Node n4 = c.newLeaf(n1);
        Edge e1 = c.newEdge(n2, n3);
        Edge e2 =         c.newEdge(n3, n4);
        Edge e3 = c.newEdge(n2, n4);
        SugiCompoundGraph s = new SugiCompoundGraph(c);
        Hierarchization.hierarchize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        //DummyPicture.write(s, "Normalization" + getName()+ ".jpg");
        Normalization.normalize(s);
//        SugiEdge e1a = s.getCorrespondingEdge(e1,n2);
//        SugiEdge e1b = s.getCorrespondingEdge(e1, n3);
//        SugiEdge e2a = s.getCorrespondingEdge(e2,n3);
//        SugiEdge e2b = s.getCorrespondingEdge(e2, n4);
//        SugiEdge e3a = s.getCorrespondingEdge(e3,n2);
//        SugiEdge e3b = s.getCorrespondingEdge(e3, n4);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        DummyPicture.show(s);
//        DummyPicture.write(s, "Normalization" + getName());
    }

    /**
     *
     */
    public final void test3() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        SugiNode n1 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n2 = (SugiNode) s.newLeaf(n1);
        SugiNode n3 = (SugiNode) s.newLeaf(n1);
        SugiNode n4 = (SugiNode) s.newLeaf(n1);
        SugiNode n5 = (SugiNode) s.newLeaf(n1);
        SugiNode n6 = (SugiNode) s.newLeaf(n1);
        SugiNode n7 = (SugiNode) s.newLeaf(n1);
        s.newEdge(n2, n3);
        s.newEdge(n3, n4);
        s.newEdge(n4, n5);
        s.newEdge(n5, n6);
        s.newEdge(n6, n7);
        s.newEdge(n2, n7);
        Hierarchization.hierarchize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        Normalization.normalize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        DummyPicture.show(s);
//        DummyPicture.write(s, "Normalization" + getName());

    }

    /**
     *
     */
    public final void test4() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        SugiNode n2 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n3 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n4 = (SugiNode) s.newLeaf(n2);
        SugiNode n5 = (SugiNode) s.newLeaf(n3);
        SugiNode n6 = (SugiNode) s.newLeaf(n4);
        SugiNode n7 = (SugiNode) s.newLeaf(n5);
        SugiNode n8 = (SugiNode) s.newLeaf(n4);

        s.newEdge(n6, n8);
        s.newEdge(n8, n7);

        //edge under test
        s.newEdge(n6, n7);

        Hierarchization.hierarchize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        //DummyPicture.write(s, "Normalization" + getName()+ ".jpg");
        Normalization.normalize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        DummyPicture.show(s);
//        DummyPicture.write(s, "Normalization" + getName());
    }

    /**
     *
     */
    public final void test5() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        SugiNode n1 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n2 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n3 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n4 = (SugiNode) s.newLeaf(n2);
        SugiNode n5 = (SugiNode) s.newLeaf(n3);
        SugiNode n6 = (SugiNode) s.newLeaf(n4);
        SugiNode n7 = (SugiNode) s.newLeaf(n5);
        SugiNode n8 = (SugiNode) s.newLeaf(n6);
        SugiNode n9 = (SugiNode) s.newLeaf(n7);
        SugiNode n10 = (SugiNode) s.newLeaf(n8);
        SugiNode n11 = (SugiNode) s.newLeaf(n9);
        SugiNode n12 = (SugiNode) s.newLeaf(n10);
        SugiNode n13 = (SugiNode) s.newLeaf(n11);

        SugiNode n14 = (SugiNode) s.newLeaf(n10);
        SugiNode n15 = (SugiNode) s.newLeaf(n10);
        SugiNode n16 = (SugiNode) s.newLeaf(n10);
        SugiNode n17 = (SugiNode) s.newLeaf(n10);

        //edge under test
        s.newEdge(n12, n13);

        s.newEdge(n1, n2);
        s.newEdge(n1, n3);
        s.newEdge(n12, n14);
        s.newEdge(n14, n15);
        s.newEdge(n15, n16);
        s.newEdge(n16, n17);
        s.newEdge(n17, n13);

        Hierarchization.hierarchize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        // DummyPicture.write(s, "Normalization" + getName()+ ".jpg");
        //DummyPicture.show(s);
        Normalization.normalize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        DummyPicture.show(s);
//        DummyPicture.write(s, "Normalization" + getName());
    }

    /**
     *
     */
    public final void test6() {
        CompoundGraph c = new Static();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(n1);
        Node n3 = c.newLeaf(n2);
        Node n4 = c.newLeaf(n3);
        Node v = c.newLeaf(n4);
        Node u = c.newLeaf(c.getRoot());
        Edge e = c.newEdge(v, u);

        SugiCompoundGraph s = new SugiCompoundGraph(c);
        Hierarchization.hierarchize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        //DummyPicture.write(s, "Normalization" + getName()+ ".jpg");
        //DummyPicture.show(s);
        Normalization.normalize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        DummyPicture.show(s);
//        DummyPicture.write(s, "Normalization" + getName());
    }

    /**
     *
     */
    public final void test7() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        SugiNode n1 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n2 = (SugiNode) s.newLeaf(n1);
        SugiNode n3 = (SugiNode) s.newLeaf(n2);
        SugiNode n4 = (SugiNode) s.newLeaf(n3);
        SugiNode u = (SugiNode) s.newLeaf(n4);
        SugiNode v = (SugiNode) s.newLeaf(s.getRoot());

        s.newEdge(v, u);
        Hierarchization.hierarchize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        //DummyPicture.write(s, "Normalization" + getName()+ ".jpg");
        //DummyPicture.show(s);
        Normalization.normalize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        DummyPicture.show(s);
//        DummyPicture.write(s, "Normalization" + getName());
    }

    /**
     *
     */
    public final void test8() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        SugiNode n1 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n2 = (SugiNode) s.newLeaf(n1);
        SugiNode n3 = (SugiNode) s.newLeaf(n2);
        SugiNode n4 = (SugiNode) s.newLeaf(n3);
        SugiNode v = (SugiNode) s.newLeaf(n4);
        SugiNode n6 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n7 = (SugiNode) s.newLeaf(n6);
        SugiNode n8 = (SugiNode) s.newLeaf(n7);
        SugiNode n9 = (SugiNode) s.newLeaf(n8);
        SugiNode u = (SugiNode) s.newLeaf(n9);
        SugiNode n11 = (SugiNode) s.newLeaf(s.getRoot());

        //edge under test
        s.newEdge(v, u);

        s.newEdge(n1, n6);
        s.newEdge(n1, n11);
        s.newEdge(n11, n6);

        Hierarchization.hierarchize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        // DummyPicture.write(s, "Normalization" + getName()+ ".jpg");
        //DummyPicture.show(s);
        Normalization.normalize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);
        DummyPicture.show(s);
//        DummyPicture.write(s, "Normalization" + getName());
    }
 }
