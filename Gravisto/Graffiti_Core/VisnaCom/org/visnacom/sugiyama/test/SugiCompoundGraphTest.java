/*==============================================================================
*
*   SugiCompoundGraphTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: SugiCompoundGraphTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import org.visnacom.model.CompoundGraph;
import org.visnacom.model.Node;
import org.visnacom.model.Static;
import org.visnacom.sugiyama.algorithm.*;
import org.visnacom.sugiyama.eval.DummyPicture;
import org.visnacom.sugiyama.model.SugiCompoundGraph;

import junit.framework.TestCase;

/**
 *
 */
public class SugiCompoundGraphTest extends TestCase {
    //~ Methods ================================================================

    /**
     *
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(SugiCompoundGraphTest.class);
    }

    /**
     *
     */
    public void testInitSugiCG() {
        CompoundGraph c = new Static();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(c.getRoot());
        Node n5 = c.newLeaf(n1);
        c.newEdge(n1, n2);
        c.newEdge(n3, n4);
        c.newEdge(n1, n3);
        c.newEdge(n4, n5);
        System.out.println(c);


        SugiCompoundGraph s = new SugiCompoundGraph(c);
        Hierarchization.hierarchize(s);
        Normalization.normalize(s);
        VertexOrdering.order(s, 3);
        MetricLayout.layout(s, 4);
        DummyPicture.show(s);
    }

    /**
     *
     */
    public void testInitSugiCG2() {
        TestGraph1 tg = new TestGraph1();
        CompoundGraph c = tg.getCompoundTestGraph1();

        SugiCompoundGraph s = new SugiCompoundGraph(c);
        Hierarchization.hierarchize(s);
        Normalization.normalize(s);
        VertexOrdering.order(s, 10);
        MetricLayout.layout(s, 4);
        DummyPicture.show(s);
    }
}
