/*==============================================================================
*
*   ResolveCyclesTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: ResolveCyclesTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.visnacom.model.Node;
import org.visnacom.sugiyama.algorithm.Hierarchization;
import org.visnacom.sugiyama.algorithm.Scc;
import org.visnacom.sugiyama.model.DerivedEdge;
import org.visnacom.sugiyama.model.DerivedGraph;
import org.visnacom.sugiyama.model.SugiCompoundGraph;
import org.visnacom.sugiyama.model.SugiNode;

import junit.framework.TestCase;

/**
 * DOCUMENT ME!
 */
public class ResolveCyclesTest extends TestCase {
    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(ResolveCyclesTest.class);
    }

    /**
     * DOCUMENT ME!
     */
    public void testEmptySet() {
        System.out.println(getClass());
        System.out.println(this.getName());


        DerivedGraph c = new DerivedGraph();

        //  System.out.println(c);
        List l = new LinkedList();
        Hierarchization.resolveCycles(c, l);
        System.out.println(c);
    }

    /**
     * DOCUMENT ME!
     */
    public final void testOnlyOneNode() {
        System.out.println(getClass());
        System.out.println(this.getName());


        DerivedGraph c = new DerivedGraph();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(n1);
        Node n4 = c.newLeaf(n1);
        Node n5 = c.newLeaf(n1);
        Node n6 = c.newLeaf(n2);
        DerivedEdge e1 = c.newEdge(n3, n5, DerivedEdge.LESS);
        DerivedEdge e2 = c.newEdge(n4, n5, DerivedEdge.LESS);
        DerivedEdge e3 = c.newEdge(n5, n6, DerivedEdge.LESS);
        DerivedEdge e4 = c.newEdge(n2, n1, DerivedEdge.LESS);
        DerivedEdge e5 = c.newEdge(n6, n5, DerivedEdge.LESS);

        System.out.println(c);


        List l = new LinkedList();
        l.add(n2);
        e4.setIntern(false);
        ((SugiNode) n1).setScc(n1);
        ((SugiNode) n3).setScc(n1);
        ((SugiNode) n4).setScc(n1);
        ((SugiNode) n5).setScc(n1);
        ((SugiNode) n6).setScc(n1);
        Hierarchization.resolveCycles(c, l);
        System.out.println(c);
    }

    /**
     * DOCUMENT ME!
     */
    public final void testbig1() {
        System.out.println(getClass());
        System.out.println(this.getName());


        DerivedGraph c = new DerivedGraph();

        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(c.getRoot());
        Node n5 = c.newLeaf(c.getRoot());
        Node n6 = c.newLeaf(c.getRoot());
        Node n7 = c.newLeaf(c.getRoot());
        Node n8 = c.newLeaf(c.getRoot());
        Node n9 = c.newLeaf(c.getRoot());
        Node n10 = c.newLeaf(c.getRoot());
        Node n11 = c.newLeaf(c.getRoot());
        Node n12 = c.newLeaf(c.getRoot());
        Node n13 = c.newLeaf(c.getRoot());
        Node n14 = c.newLeaf(c.getRoot());
        Node n15 = c.newLeaf(c.getRoot());
        Node n16 = c.newLeaf(c.getRoot());
        Node n17 = c.newLeaf(c.getRoot());
        Node n18 = c.newLeaf(c.getRoot());
        Node n19 = c.newLeaf(c.getRoot());
        Node n20 = c.newLeaf(c.getRoot());
        DerivedEdge e1 = c.newEdge(n1, n2, DerivedEdge.LESS);
        DerivedEdge e2 = c.newEdge(n2, n1, DerivedEdge.LESS);
        DerivedEdge e3 = c.newEdge(n2, n3, DerivedEdge.EQLESS);
        DerivedEdge e4 = c.newEdge(n3, n1, DerivedEdge.EQLESS);
        DerivedEdge e5 = c.newEdge(n2, n4, DerivedEdge.LESS);
        DerivedEdge e6 = c.newEdge(n3, n4, DerivedEdge.LESS);
        DerivedEdge e7 = c.newEdge(n5, n4, DerivedEdge.LESS);
        DerivedEdge e8 = c.newEdge(n4, n6, DerivedEdge.LESS);
        DerivedEdge e9 = c.newEdge(n5, n6, DerivedEdge.LESS);
        DerivedEdge e10 = c.newEdge(n6, n5, DerivedEdge.LESS);
        DerivedEdge e12 = c.newEdge(n8, n6, DerivedEdge.LESS);
        DerivedEdge e13 = c.newEdge(n8, n9, DerivedEdge.LESS);
        DerivedEdge e14 = c.newEdge(n9, n8, DerivedEdge.EQLESS);
        DerivedEdge e16 = c.newEdge(n11, n10, DerivedEdge.LESS);
        DerivedEdge e17 = c.newEdge(n11, n12, DerivedEdge.LESS);
        DerivedEdge e18 = c.newEdge(n11, n13, DerivedEdge.LESS);
        DerivedEdge e19 = c.newEdge(n11, n14, DerivedEdge.LESS);
        DerivedEdge e20 = c.newEdge(n11, n15, DerivedEdge.LESS);
        DerivedEdge e21 = c.newEdge(n12, n13, DerivedEdge.LESS);
        DerivedEdge e22 = c.newEdge(n13, n15, DerivedEdge.LESS);
        DerivedEdge e23 = c.newEdge(n15, n12, DerivedEdge.LESS);
        DerivedEdge e24 = c.newEdge(n14, n10, DerivedEdge.LESS);
        DerivedEdge e25 = c.newEdge(n10, n16, DerivedEdge.LESS);
        DerivedEdge e26 = c.newEdge(n10, n17, DerivedEdge.LESS);
        DerivedEdge e27 = c.newEdge(n10, n18, DerivedEdge.LESS);
        DerivedEdge e28 = c.newEdge(n16, n17, DerivedEdge.LESS);
        DerivedEdge e29 = c.newEdge(n16, n19, DerivedEdge.LESS);
        DerivedEdge e30 = c.newEdge(n17, n19, DerivedEdge.EQLESS);
        DerivedEdge e31 = c.newEdge(n18, n17, DerivedEdge.EQLESS);
        DerivedEdge e32 = c.newEdge(n19, n20, DerivedEdge.EQLESS);
        DerivedEdge e33 = c.newEdge(n20, n18, DerivedEdge.EQLESS);

        System.out.println(c);
        Hierarchization.resolveCycles(c);
        System.out.println(c);

        /**
         * ab hier unsauber, da findScc nicht dafuer gedacht ist, nach
         * resolveCycles aufgerufen zu werden
         */
        for(Iterator it = c.getAllNodes().iterator(); it.hasNext();) {
            ((SugiNode) it.next()).setScc(n1);
        }

        for(Iterator it = c.getAllEdges().iterator(); it.hasNext();) {
            ((DerivedEdge) it.next()).setIntern(true);
        }


        //now all nodes should be an own scc
        List result = new Scc().findScc(c, c.getAllNodes());

        // System.out.println(result);
        for(Iterator it = result.iterator(); it.hasNext();) {
            List next = (List) it.next();
            assertTrue((next).size() == 1
                || Hierarchization.isGoodCycle(next, c));
        }
    }

    /**
     * DOCUMENT ME!
     */
    public final void testgraph3useProxy() {
        System.out.println();
        System.out.println(this.getName());


        TestGraph3 tg = new TestGraph3();
        SugiCompoundGraph c = tg.getTestGraph3withproxy();
        DerivedGraph dg = Hierarchization.createDerivedGraph(c);
        tg.checkDerivedGraph(dg);

        Hierarchization.resolveCycles(dg);
        System.out.println(dg);
    }

    /**
     *
     */
    public final void testgraph3withproblem() {
        System.out.println();
        System.out.println(this.getName());


        TestGraph3 tg = new TestGraph3();
        SugiCompoundGraph c = tg.getTestGraph3withoutproxy();
        DerivedGraph dg = Hierarchization.createDerivedGraph(c);
        tg.checkDerivedGraph(dg);

        Hierarchization.resolveCycles(dg);
        System.out.println(dg);
    }
}
