/*==============================================================================
*
*   TestGraph2.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: TestGraph2.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import org.visnacom.model.Edge;
import org.visnacom.model.Node;
import org.visnacom.sugiyama.model.DerivedEdge;
import org.visnacom.sugiyama.model.DerivedGraph;
import org.visnacom.sugiyama.model.SugiCompoundGraph;


/**
 * DOCUMENT ME!
 */
public class TestGraph2 {
    //~ Instance fields ========================================================

    SugiCompoundGraph c;
    Node n1;
    Node n2;
    Node n3;
    Node n4;
    Node n5;
    Node n6;
    Node n7;
    Node n8;
    Node n9;
    Node n10;
    Node n11;
    Node n12;
    Node n13;
    Node n14;
    Node n15;
    Node n16;
    Node n17;
    Node n18;
    Edge e1;
    Edge e2;
    Edge e3;
    Edge e4;
    Edge e5;
    Edge e6;
    Edge e7;
    Edge e8;
    Edge e9;
    Edge e10;
    Edge e11;
    Edge e12;
    Edge e13;
    Edge e14;
    Edge e15;
    boolean alreadyUsed = false;

    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public SugiCompoundGraph getTestGraph2() {
        if(alreadyUsed) {
            return null;
        }

        alreadyUsed = true;

        c = new SugiCompoundGraph();
        n1 = c.newLeaf(c.getRoot());
        n2 = c.newLeaf(c.getRoot());
        n3 = c.newLeaf(c.getRoot());
        n4 = c.newLeaf(n1);
        n5 = c.newLeaf(n1);
        n6 = c.newLeaf(n1);
        n7 = c.newLeaf(n1);
        n8 = c.newLeaf(n2);
        n9 = c.newLeaf(n3);
        n10 = c.newLeaf(n3);
        n11 = c.newLeaf(n4);
        n12 = c.newLeaf(n5);
        n13 = c.newLeaf(n5);
        n14 = c.newLeaf(n8);
        n15 = c.newLeaf(n10);
        n16 = c.newLeaf(n10);
        n17 = c.newLeaf(n10);
        n18 = c.newLeaf(n10);
        e1 = c.newEdge(n1, n2);
        e2 = c.newEdge(n1, n16);
        e3 = c.newEdge(n3, n2);
        e4 = c.newEdge(n4, n6);
        e5 = c.newEdge(n8, n9);
        e6 = c.newEdge(n12, n8);
        e7 = c.newEdge(n12, n11);
        e8 = c.newEdge(n12, n14);
        e9 = c.newEdge(n13, n7);
        e10 = c.newEdge(n15, n17);
        e11 = c.newEdge(n15, n18);
        e12 = c.newEdge(n16, n15);
        e13 = c.newEdge(n16, n17);
        e14 = c.newEdge(n17, n18);
        e15 = c.newEdge(n18, n16);

        return c;
    }

    /**
     * DOCUMENT ME!
     *
     * @param dg DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean checkDerivedGraph(DerivedGraph dg) {
        boolean b = true;
        b = (dg.getAllEdges().size() == 17)
            && (TestGraph1.edgeExists(dg, n1, n2, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n1, n3, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n2, n3, DerivedEdge.EQLESS))
            && (TestGraph1.edgeExists(dg, n3, n2, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n4, n6, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n5, n8, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n5, n4, DerivedEdge.EQLESS))
            && (TestGraph1.edgeExists(dg, n5, n7, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n8, n9, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n12, n11, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n12, n14, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n15, n17, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n15, n18, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n16, n15, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n16, n17, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n17, n18, DerivedEdge.LESS))
            && (TestGraph1.edgeExists(dg, n18, n16, DerivedEdge.LESS));
        return b;
    }
}
