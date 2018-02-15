/*==============================================================================
*
*   SccTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: SccTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import java.util.Iterator;
import java.util.List;

import org.visnacom.model.Edge;
import org.visnacom.model.Node;
import org.visnacom.sugiyama.algorithm.Hierarchization;
import org.visnacom.sugiyama.algorithm.Scc;
import org.visnacom.sugiyama.model.DerivedEdge;
import org.visnacom.sugiyama.model.DerivedGraph;
import org.visnacom.sugiyama.model.SugiNode;

import junit.framework.TestCase;

/**
 * DOCUMENT ME!
 */
public class SccTest extends TestCase {
    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(SccTest.class);
    }

    /**
     * DOCUMENT ME!
     */
    public final void testBigFindScc() {
        ////System.out.println();
        ////System.out.println(getName());
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
        c.newEdge(n1, n2, DerivedEdge.LESS);
        c.newEdge(n2, n1, DerivedEdge.LESS);
        c.newEdge(n2, n3, DerivedEdge.LESS);
        c.newEdge(n3, n1, DerivedEdge.LESS);
        c.newEdge(n2, n4, DerivedEdge.LESS);
        c.newEdge(n3, n4, DerivedEdge.LESS);
        c.newEdge(n5, n4, DerivedEdge.LESS);
        c.newEdge(n4, n6, DerivedEdge.LESS);
        c.newEdge(n5, n6, DerivedEdge.LESS);
        c.newEdge(n6, n5, DerivedEdge.LESS);
        c.newEdge(n5, n7, DerivedEdge.LESS);
        c.newEdge(n8, n6, DerivedEdge.LESS);
        c.newEdge(n8, n9, DerivedEdge.LESS);
        c.newEdge(n10, n1, DerivedEdge.LESS);
        c.newEdge(n11, n1, DerivedEdge.LESS);
        c.newEdge(n11, n10, DerivedEdge.LESS);
        c.newEdge(n11, n12, DerivedEdge.LESS);
        c.newEdge(n11, n13, DerivedEdge.LESS);
        c.newEdge(n11, n14, DerivedEdge.LESS);
        c.newEdge(n11, n15, DerivedEdge.LESS);
        c.newEdge(n12, n13, DerivedEdge.LESS);
        c.newEdge(n13, n15, DerivedEdge.LESS);
        c.newEdge(n15, n12, DerivedEdge.LESS);
        c.newEdge(n14, n10, DerivedEdge.LESS);
        c.newEdge(n10, n16, DerivedEdge.LESS);
        c.newEdge(n10, n17, DerivedEdge.LESS);
        c.newEdge(n10, n18, DerivedEdge.LESS);
        c.newEdge(n16, n17, DerivedEdge.LESS);
        c.newEdge(n16, n19, DerivedEdge.LESS);
        c.newEdge(n17, n19, DerivedEdge.LESS);
        c.newEdge(n18, n17, DerivedEdge.LESS);
        c.newEdge(n19, n20, DerivedEdge.LESS);
        c.newEdge(n20, n18, DerivedEdge.LESS);


        ////System.out.println(c);
        List l = new Scc().findScc(c, c.getAllNodes());

        ////System.out.println();
        ////System.out.println(l);
        for(Iterator it = l.iterator(); it.hasNext();) {
            List list = (List) it.next();
            if(list.contains(n1)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n2));
                assertTrue(list.contains(n3));
            }

            if(list.contains(n2)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n1));
                assertTrue(list.contains(n3));
            }

            if(list.contains(n3)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n1));
                assertTrue(list.contains(n2));
            }

            if(list.contains(n4)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n5));
                assertTrue(list.contains(n6));
            }

            if(list.contains(n5)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n4));
                assertTrue(list.contains(n6));
            }

            if(list.contains(n6)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n4));
                assertTrue(list.contains(n5));
            }

            if(list.contains(n7)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n8)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n9)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n10)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n11)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n12)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n13));
                assertTrue(list.contains(n15));
            }

            if(list.contains(n13)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n12));
                assertTrue(list.contains(n15));
            }

            if(list.contains(n14)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n15)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n12));
                assertTrue(list.contains(n13));
            }

            if(list.contains(n16)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n17)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n18));
                assertTrue(list.contains(n19));
                assertTrue(list.contains(n20));
            }

            if(list.contains(n18)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n17));
                assertTrue(list.contains(n19));
                assertTrue(list.contains(n20));
            }

            if(list.contains(n19)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n17));
                assertTrue(list.contains(n18));
                assertTrue(list.contains(n20));
            }

            if(list.contains(n20)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n17));
                assertTrue(list.contains(n18));
                assertTrue(list.contains(n19));
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public final void testBigFindSccInSubgraph() {
        ////System.out.println();
        ////System.out.println(getName());
        DerivedGraph c = new DerivedGraph();
        Node n0 = c.newLeaf(c.getRoot());
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
        Edge e1 = c.newEdge(n1, n2, DerivedEdge.LESS);
        Edge e2 = c.newEdge(n2, n1, DerivedEdge.LESS);
        Edge e3 = c.newEdge(n2, n3, DerivedEdge.LESS);
        Edge e4 = c.newEdge(n3, n1, DerivedEdge.LESS);
        Edge e5 = c.newEdge(n2, n4, DerivedEdge.LESS);
        Edge e6 = c.newEdge(n3, n4, DerivedEdge.LESS);
        Edge e7 = c.newEdge(n5, n4, DerivedEdge.LESS);
        Edge e8 = c.newEdge(n4, n6, DerivedEdge.LESS);
        Edge e9 = c.newEdge(n5, n6, DerivedEdge.LESS);
        Edge e10 = c.newEdge(n6, n5, DerivedEdge.LESS);
        Edge e11 = c.newEdge(n5, n7, DerivedEdge.LESS);
        Edge e12 = c.newEdge(n8, n6, DerivedEdge.LESS);
        Edge e13 = c.newEdge(n8, n9, DerivedEdge.LESS);
        Edge e14 = c.newEdge(n10, n1, DerivedEdge.LESS);
        Edge e15 = c.newEdge(n11, n1, DerivedEdge.LESS);
        Edge e16 = c.newEdge(n11, n10, DerivedEdge.LESS);
        Edge e17 = c.newEdge(n11, n12, DerivedEdge.LESS);
        Edge e18 = c.newEdge(n11, n13, DerivedEdge.LESS);
        Edge e19 = c.newEdge(n11, n14, DerivedEdge.LESS);
        Edge e20 = c.newEdge(n11, n15, DerivedEdge.LESS);
        Edge e21 = c.newEdge(n12, n13, DerivedEdge.LESS);
        Edge e22 = c.newEdge(n13, n15, DerivedEdge.LESS);
        Edge e23 = c.newEdge(n15, n12, DerivedEdge.LESS);
        Edge e24 = c.newEdge(n14, n10, DerivedEdge.LESS);
        Edge e25 = c.newEdge(n10, n16, DerivedEdge.LESS);
        Edge e26 = c.newEdge(n10, n17, DerivedEdge.LESS);
        Edge e27 = c.newEdge(n10, n18, DerivedEdge.LESS);
        Edge e28 = c.newEdge(n16, n17, DerivedEdge.LESS);
        Edge e29 = c.newEdge(n16, n19, DerivedEdge.LESS);
        Edge e30 = c.newEdge(n17, n19, DerivedEdge.LESS);
        Edge e31 = c.newEdge(n18, n17, DerivedEdge.LESS);
        Edge e32 = c.newEdge(n19, n20, DerivedEdge.LESS);
        Edge e33 = c.newEdge(n20, n18, DerivedEdge.LESS);

        ////System.out.println(c);
        List subgraph = c.getAllNodes();

        ((DerivedEdge) e7).setIntern(false);
        ((DerivedEdge) e8).setIntern(false);

        subgraph.remove(n5);
        subgraph.remove(n6);
        subgraph.remove(n7);
        subgraph.remove(n8);
        subgraph.remove(n9);
        ((SugiNode) n5).setScc(n5);
        ((SugiNode) n6).setScc(n5);
        ((SugiNode) n7).setScc(n5);
        ((SugiNode) n8).setScc(n5);
        ((SugiNode) n9).setScc(n5);


        List l = new Scc().findScc(c, subgraph);

        ////System.out.println();
        ////System.out.println(l);
        for(Iterator it = l.iterator(); it.hasNext();) {
            List list = (List) it.next();
            if(list.contains(n1)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n2));
                assertTrue(list.contains(n3));
            }

            if(list.contains(n2)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n1));
                assertTrue(list.contains(n3));
            }

            if(list.contains(n3)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n1));
                assertTrue(list.contains(n2));
            }

            if(list.contains(n4)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n5)) {
                fail();
            }

            if(list.contains(n6)) {
                fail();
            }

            if(list.contains(n7)) {
                fail();
            }

            if(list.contains(n8)) {
                fail();
            }

            if(list.contains(n9)) {
                fail();
            }

            if(list.contains(n10)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n11)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n12)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n13));
                assertTrue(list.contains(n15));
            }

            if(list.contains(n13)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n12));
                assertTrue(list.contains(n15));
            }

            if(list.contains(n14)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n15)) {
                assertEquals(list.size(), 3);
                assertTrue(list.contains(n12));
                assertTrue(list.contains(n13));
            }

            if(list.contains(n16)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n17)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n18));
                assertTrue(list.contains(n19));
                assertTrue(list.contains(n20));
            }

            if(list.contains(n18)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n17));
                assertTrue(list.contains(n19));
                assertTrue(list.contains(n20));
            }

            if(list.contains(n19)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n17));
                assertTrue(list.contains(n18));
                assertTrue(list.contains(n20));
            }

            if(list.contains(n20)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n17));
                assertTrue(list.contains(n18));
                assertTrue(list.contains(n19));
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public final void testEmptySet() {
        ////System.out.println();
        ////System.out.println(getName());
        DerivedGraph c = new DerivedGraph();

        ////System.out.println(c);
        List l = new Scc().findScc(c, c.getAllNodes());

        ////System.out.println();
        ////System.out.println(l);
    }

    /**
     * DOCUMENT ME!
     */
    public final void testFindScc() {
        ////System.out.println();
        ////System.out.println(getName());
        DerivedGraph c = new DerivedGraph();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(n1);
        Node n4 = c.newLeaf(n1);
        Node n5 = c.newLeaf(n1);
        Node n6 = c.newLeaf(n2);
        c.newEdge(n3, n5, DerivedEdge.LESS);
        c.newEdge(n4, n5, DerivedEdge.LESS);
        c.newEdge(n5, n6, DerivedEdge.LESS);
        c.newEdge(n2, n1, DerivedEdge.LESS);
        c.newEdge(n6, n5, DerivedEdge.LESS);


        ////System.out.println(c);
        List l = new Scc().findScc(c, c.getAllNodes());

        ////System.out.println();
        ////System.out.println(l);
        for(Iterator it = l.iterator(); it.hasNext();) {
            List list = (List) it.next();
            if(list.contains(n1)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n2)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n3)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n4)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n5)) {
                assertEquals(list.size(), 2);
                assertTrue(list.contains(n6));
            }

            if(list.contains(n6)) {
                assertEquals(list.size(), 2);
                assertTrue(list.contains(n5));
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public final void testFindScc2() {
        //another graph
        ////System.out.println();
        ////System.out.println(getName());
        DerivedGraph c = new DerivedGraph();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(c.getRoot());
        Node n5 = c.newLeaf(c.getRoot());
        Node n6 = c.newLeaf(c.getRoot());
        c.newEdge(n1, n2, DerivedEdge.LESS);
        c.newEdge(n1, n3, DerivedEdge.LESS);
        c.newEdge(n2, n3, DerivedEdge.LESS);
        c.newEdge(n3, n5, DerivedEdge.LESS);
        c.newEdge(n4, n6, DerivedEdge.LESS);
        c.newEdge(n5, n1, DerivedEdge.LESS);
        c.newEdge(n6, n1, DerivedEdge.LESS);
        c.newEdge(n6, n4, DerivedEdge.LESS);


        //System.out.println(c);
        List l = new Scc().findScc(c, c.getAllNodes());

        //System.out.println();
        //System.out.println(l);
        for(Iterator it = l.iterator(); it.hasNext();) {
            List list = (List) it.next();
            if(list.contains(n1)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n2));
                assertTrue(list.contains(n3));
                assertTrue(list.contains(n5));
            }

            if(list.contains(n2)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n1));
                assertTrue(list.contains(n3));
                assertTrue(list.contains(n5));
            }

            if(list.contains(n3)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n1));
                assertTrue(list.contains(n3));
                assertTrue(list.contains(n5));
            }

            if(list.contains(n4)) {
                assertEquals(list.size(), 2);
                assertTrue(list.contains(n6));
            }

            if(list.contains(n5)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n2));
                assertTrue(list.contains(n3));
                assertTrue(list.contains(n1));
            }

            if(list.contains(n6)) {
                assertEquals(list.size(), 2);
                assertTrue(list.contains(n4));
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public final void testFindScc3() {
        //the same as testFindScc2 in other order
        //System.out.println();
        //System.out.println(getName());
        DerivedGraph c = new DerivedGraph();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(c.getRoot());

        Node n5 = c.newLeaf(c.getRoot());
        Node n6 = c.newLeaf(c.getRoot());
        c.newEdge(n2, n3, DerivedEdge.LESS);
        c.newEdge(n2, n4, DerivedEdge.LESS);
        c.newEdge(n3, n4, DerivedEdge.LESS);
        c.newEdge(n4, n5, DerivedEdge.LESS);
        c.newEdge(n1, n6, DerivedEdge.LESS);
        c.newEdge(n5, n2, DerivedEdge.LESS);
        c.newEdge(n6, n2, DerivedEdge.LESS);
        c.newEdge(n6, n1, DerivedEdge.LESS);


        //System.out.println(c);
        List l = new Scc().findScc(c, c.getAllNodes());

        //System.out.println();
        //System.out.println(l);
        for(Iterator it = l.iterator(); it.hasNext();) {
            List list = (List) it.next();
            if(list.contains(n4)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n2));
                assertTrue(list.contains(n3));
                assertTrue(list.contains(n5));
            }

            if(list.contains(n2)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n4));
                assertTrue(list.contains(n3));
                assertTrue(list.contains(n5));
            }

            if(list.contains(n3)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n4));
                assertTrue(list.contains(n2));
                assertTrue(list.contains(n5));
            }

            if(list.contains(n1)) {
                assertEquals(list.size(), 2);
                assertTrue(list.contains(n6));
            }

            if(list.contains(n5)) {
                assertEquals(list.size(), 4);
                assertTrue(list.contains(n2));
                assertTrue(list.contains(n3));
                assertTrue(list.contains(n4));
            }

            if(list.contains(n6)) {
                assertEquals(list.size(), 2);
                assertTrue(list.contains(n1));
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public final void testFindSccInSubgraph() {
        //System.out.println();
        //System.out.println(getName());
        DerivedGraph c = new DerivedGraph();
        Node n0 = c.newLeaf(c.getRoot());
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(n0);
        Node n4 = c.newLeaf(c.getRoot());
        Node n5 = c.newLeaf(n1);
        c.newEdge(n0, n1, DerivedEdge.LESS);
        c.newEdge(n0, n2, DerivedEdge.LESS);
        c.newEdge(n1, n2, DerivedEdge.LESS);


        DerivedEdge e0 = (DerivedEdge) c.newEdge(n2, n4, DerivedEdge.LESS);
        c.newEdge(n3, n5, DerivedEdge.LESS);


        DerivedEdge e1 = (DerivedEdge) c.newEdge(n4, n0, DerivedEdge.LESS);
        c.newEdge(n5, n0, DerivedEdge.LESS);
        c.newEdge(n5, n3, DerivedEdge.LESS);
        //System.out.println(c);
        //deactive node 5
        assertTrue(e0.isIntern());
        e0.setIntern(false);
        assertFalse(e0.isIntern());
        assertTrue(e1.isIntern());
        e1.setIntern(false);
        assertFalse(e1.isIntern());


        List subgraph = c.getAllNodes();
        subgraph.remove(n4);
        ((SugiNode) n4).setScc(n4);
        assertTrue(Hierarchization.testConsistenceOfSubgraph(c, subgraph));


        List l = new Scc().findScc(c, subgraph);

        //System.out.println();
        //System.out.println(l);
        for(Iterator it = l.iterator(); it.hasNext();) {
            List list = (List) it.next();
            if(list.contains(n1)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n2)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n0)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n3)) {
                assertEquals(list.size(), 2);
                assertTrue(list.contains(n5));
            }

            if(list.contains(n5)) {
                assertEquals(list.size(), 2);
                assertTrue(list.contains(n3));
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public final void testFindSccInSubgraph2() {
        //System.out.println();
        //System.out.println(getName());
        DerivedGraph c = new DerivedGraph();
        Node n1 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());

        Node n5 = c.newLeaf(c.getRoot());
        Node n6 = c.newLeaf(n2);
        c.newEdge(n4, n2, DerivedEdge.LESS);
        c.newEdge(n4, n3, DerivedEdge.LESS);
        c.newEdge(n2, n3, DerivedEdge.LESS);


        DerivedEdge e0 = (DerivedEdge) c.newEdge(n3, n5, DerivedEdge.LESS);
        c.newEdge(n1, n6, DerivedEdge.LESS);


        DerivedEdge e1 = (DerivedEdge) c.newEdge(n5, n4, DerivedEdge.LESS);
        c.newEdge(n6, n4, DerivedEdge.LESS);
        c.newEdge(n6, n1, DerivedEdge.LESS);
        //System.out.println(c);
        //deactive node 5
        assertTrue(e0.isIntern());
        e0.setIntern(false);
        assertFalse(e0.isIntern());
        assertTrue(e1.isIntern());
        e1.setIntern(false);
        assertFalse(e1.isIntern());


        List subgraph = c.getAllNodes();
        subgraph.remove(n5);
        ((SugiNode) n5).setScc(n5);
        assertTrue(Hierarchization.testConsistenceOfSubgraph(c, subgraph));


        List l = new Scc().findScc(c, subgraph);

        //System.out.println();
        //System.out.println(l);
        for(Iterator it = l.iterator(); it.hasNext();) {
            List list = (List) it.next();
            if(list.contains(n2)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n3)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n4)) {
                assertEquals(list.size(), 1);
            }

            if(list.contains(n1)) {
                assertEquals(list.size(), 2);
                assertTrue(list.contains(n6));
            }

            if(list.contains(n6)) {
                assertEquals(list.size(), 2);
                assertTrue(list.contains(n1));
            }
        }
    }
}
