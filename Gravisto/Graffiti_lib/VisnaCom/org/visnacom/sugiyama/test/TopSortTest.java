/*==============================================================================
*
*   TopSortTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: TopSortTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.visnacom.model.Edge;
import org.visnacom.model.Node;
import org.visnacom.sugiyama.algorithm.Hierarchization;
import org.visnacom.sugiyama.algorithm.TopSort;
import org.visnacom.sugiyama.model.CompoundLevel;
import org.visnacom.sugiyama.model.DerivedEdge;
import org.visnacom.sugiyama.model.DerivedGraph;
import org.visnacom.sugiyama.model.SugiNode;

import junit.framework.TestCase;

/**
 * DOCUMENT ME!
 */
public class TopSortTest extends TestCase {
    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(TopSortTest.class);
    }

    /**
     * DOCUMENT ME!
     */
    public final void testEmptySet() {
        DerivedGraph c = new DerivedGraph();
        List l = new LinkedList();
        try {
            new TopSort().topSort(c, l);
        } catch(Exception e) {
            fail();
        }
    }

    /**
     * DOCUMENT ME!
     */
    public final void testOnlyOneNode() {
        DerivedGraph c = new DerivedGraph();
        Node n0 = c.newLeaf(c.getRoot());
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(n0);
        Node n4 = c.newLeaf(n0);
        Node n5 = c.newLeaf(n1);
        Node n6 = c.newLeaf(n2);

        DerivedEdge e1 = c.newEdge(n3, n5, DerivedEdge.LESS);
        DerivedEdge e2 = c.newEdge(n4, n5, DerivedEdge.LESS);
        DerivedEdge e3 = c.newEdge(n5, n6, DerivedEdge.LESS);
        DerivedEdge e4 = c.newEdge(n1, n2, DerivedEdge.LESS);
        DerivedEdge e5 = c.newEdge(n6, n5, DerivedEdge.LESS);
        DerivedEdge e6 = c.newEdge(n3, n1, DerivedEdge.LESS);
        DerivedEdge e7 = c.newEdge(n1, n4, DerivedEdge.LESS);
        DerivedEdge e8 = c.newEdge(n4, n1, DerivedEdge.LESS);
        List l = new LinkedList();
        l.add(n1);


        List sorted = new TopSort().topSort(c, l);
        assertTrue(checkEdges(c, sorted));
        assertTrue(c.getEdge(n3, n1).isEmpty());
        assertTrue(c.getEdge(n4, n1).isEmpty());
    }

    /**
     * DOCUMENT ME!
     */
    public final void testTopSort1() {
        //System.out.println(this.getName());
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

        //((SugiNode) c.getRoot()).setClev(new CompoundLevel());
        DerivedEdge e2 = c.newEdge(n2, n1, DerivedEdge.LESS);
        DerivedEdge e3 = c.newEdge(n2, n3, DerivedEdge.LESS);
        DerivedEdge e4 = c.newEdge(n3, n1, DerivedEdge.LESS);
        DerivedEdge e5 = c.newEdge(n2, n4, DerivedEdge.LESS);
        DerivedEdge e6 = c.newEdge(n3, n4, DerivedEdge.LESS);
        DerivedEdge e7 = c.newEdge(n5, n4, DerivedEdge.LESS);
        DerivedEdge e8 = c.newEdge(n4, n6, DerivedEdge.LESS);
        DerivedEdge e9 = c.newEdge(n5, n6, DerivedEdge.LESS);
        DerivedEdge e11 = c.newEdge(n5, n7, DerivedEdge.LESS);
        DerivedEdge e12 = c.newEdge(n8, n6, DerivedEdge.LESS);
        DerivedEdge e13 = c.newEdge(n8, n9, DerivedEdge.LESS);
        DerivedEdge e14 = c.newEdge(n10, n1, DerivedEdge.LESS);
        DerivedEdge e15 = c.newEdge(n11, n1, DerivedEdge.LESS);
        DerivedEdge e16 = c.newEdge(n11, n10, DerivedEdge.LESS);
        DerivedEdge e17 = c.newEdge(n11, n12, DerivedEdge.LESS);
        DerivedEdge e18 = c.newEdge(n11, n13, DerivedEdge.LESS);
        DerivedEdge e19 = c.newEdge(n11, n14, DerivedEdge.LESS);
        DerivedEdge e20 = c.newEdge(n11, n15, DerivedEdge.LESS);
        DerivedEdge e21 = c.newEdge(n12, n13, DerivedEdge.LESS);
        DerivedEdge e22 = c.newEdge(n13, n15, DerivedEdge.LESS);
        DerivedEdge e24 = c.newEdge(n14, n10, DerivedEdge.LESS);
        DerivedEdge e25 = c.newEdge(n10, n16, DerivedEdge.LESS);
        DerivedEdge e26 = c.newEdge(n10, n17, DerivedEdge.LESS);
        DerivedEdge e27 = c.newEdge(n10, n18, DerivedEdge.LESS);
        DerivedEdge e28 = c.newEdge(n16, n17, DerivedEdge.LESS);
        DerivedEdge e29 = c.newEdge(n16, n19, DerivedEdge.LESS);
        DerivedEdge e31 = c.newEdge(n18, n17, DerivedEdge.LESS);
        DerivedEdge e32 = c.newEdge(n19, n20, DerivedEdge.LESS);
        DerivedEdge e33 = c.newEdge(n20, n18, DerivedEdge.LESS);

        List sortedList = new TopSort().topSort(c, c.getAllNodes());

        //System.out.println(sortedList);
        assertTrue(checkEdges(c, sortedList));
    }

    /**
     * DOCUMENT ME!
     */
    public final void testsmall() {
        //        System.out.println();
        //System.out.println(this.getName());
        DerivedGraph c = new DerivedGraph();
        Node n0 = c.newLeaf(c.getRoot());
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(c.getRoot());
        Node n5 = c.newLeaf(c.getRoot());
        Node n6 = c.newLeaf(c.getRoot());
        Node n7 = c.newLeaf(c.getRoot());

        //((SugiNode) c.getRoot()).setClev(new CompoundLevel());
        DerivedEdge e1 = c.newEdge(n1, n3, DerivedEdge.LESS);
        DerivedEdge e2 = c.newEdge(n3, n6, DerivedEdge.LESS);
        DerivedEdge e3 = c.newEdge(n6, n4, DerivedEdge.LESS);
        DerivedEdge e4 = c.newEdge(n0, n2, DerivedEdge.LESS);
        DerivedEdge e5 = c.newEdge(n4, n0, DerivedEdge.LESS);
        DerivedEdge e6 = c.newEdge(n1, n6, DerivedEdge.LESS);
        DerivedEdge e7 = c.newEdge(n3, n4, DerivedEdge.LESS);
        DerivedEdge e8 = c.newEdge(n6, n0, DerivedEdge.LESS);
        DerivedEdge e9 = c.newEdge(n4, n2, DerivedEdge.LESS);
        DerivedEdge e10 = c.newEdge(n2, n5, DerivedEdge.LESS);
        DerivedEdge e11 = c.newEdge(n3, n5, DerivedEdge.LESS);
        DerivedEdge e12 = c.newEdge(n0, n5, DerivedEdge.LESS);
        DerivedEdge e13 = c.newEdge(n7, n4, DerivedEdge.LESS);

        //  System.out.println(c);
        List l = new LinkedList(c.getAllNodes());
        l.remove(n7);


        List sortedList = new TopSort().topSort(c, l);

        //System.out.println(sortedList);
        assertTrue(checkEdges(c, sortedList));
        assertTrue(c.getEdge(n7, n4).isEmpty());
    }

    /**
     * all edges have to point to higher positions in the list
     *
     * @param c DOCUMENT ME!
     * @param sortedList DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static boolean checkEdges(DerivedGraph c, List sortedList) {
        for(Iterator it = sortedList.iterator(); it.hasNext();) {
            Node u = (Node) it.next();
            for(Iterator it2 = c.getInEdges(u).iterator(); it2.hasNext();) {
                Edge e = (Edge) it2.next();
                Node v = e.getSource();
                if(sortedList.indexOf(u) < sortedList.indexOf(v)) {
                    return false;
                }
            }
        }

        return true;
    }
}
