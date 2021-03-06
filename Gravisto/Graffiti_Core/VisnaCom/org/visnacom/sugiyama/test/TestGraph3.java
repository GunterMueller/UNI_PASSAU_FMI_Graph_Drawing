/*==============================================================================
*
*   TestGraph3.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: TestGraph3.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import org.visnacom.model.Edge;
import org.visnacom.sugiyama.model.DerivedEdge;
import org.visnacom.sugiyama.model.DerivedGraph;
import org.visnacom.sugiyama.model.SugiCompoundGraph;
import org.visnacom.sugiyama.model.SugiNode;


/**
 *
 */
public class TestGraph3 {
    //~ Instance fields ========================================================

    SugiCompoundGraph c = new SugiCompoundGraph();
    SugiNode n1 = (SugiNode) c.newLeaf(c.getRoot());
    SugiNode n2 = (SugiNode) c.newLeaf(n1);
    SugiNode n3 = (SugiNode) c.newLeaf(n1);
    SugiNode n4 = (SugiNode) c.newLeaf(n1);
    SugiNode n5 = (SugiNode) c.newLeaf(n2);
    SugiNode n6 = (SugiNode) c.newLeaf(n2);
    SugiNode n7 = (SugiNode) c.newLeaf(n3);
    SugiNode n8 = (SugiNode) c.newLeaf(n4);
    SugiNode n9 = (SugiNode) c.newLeaf(n7);
    SugiNode n10 = (SugiNode) c.newLeaf(n7);
    SugiNode n11 = (SugiNode) c.newLeaf(n8);
    SugiNode n12 = (SugiNode) c.newLeaf(n8);
    Edge e1 = c.newEdge(n4, n2);
    Edge e2 = c.newEdge(n2, n3);
    Edge e3 = c.newEdge(n5, n7);
    Edge e4 = c.newEdge(n7, n6);
    Edge e6 = c.newEdge(n10, n11);
    Edge e7 = c.newEdge(n12, n9);
    boolean alreadyUsed = false;
    boolean withproxyversion;

    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public SugiCompoundGraph getTestGraph3withoutproxy() {
        if(alreadyUsed) {
            return null;
        }

        alreadyUsed = true;
        withproxyversion = false;
        return c;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public SugiCompoundGraph getTestGraph3withproxy() {
        if(alreadyUsed) {
            return null;
        }

        alreadyUsed = true;
        withproxyversion = true;
        c.deleteEdge(e1); //this edge causes the problem
        return c;
    }

    /**
     * DOCUMENT ME!
     *
     * @param dg DOCUMENT ME!
     */
    public void checkDerivedGraph(DerivedGraph dg) {
        assert (dg.getAllEdges().size() == (withproxyversion ? 10 : 11));
        if(!withproxyversion) {
            assert (TestGraph1.edgeExists(dg, n4, n2, DerivedEdge.LESS));
        }

        assert (TestGraph1.edgeExists(dg, n2, n3, DerivedEdge.LESS));
        assert (TestGraph1.edgeExists(dg, n5, n7, DerivedEdge.LESS));
        assert (TestGraph1.edgeExists(dg, n7, n6, DerivedEdge.LESS));
        assert (TestGraph1.edgeExists(dg, n10, n11, DerivedEdge.LESS));
        assert (TestGraph1.edgeExists(dg, n12, n9, DerivedEdge.LESS));
        assert (TestGraph1.edgeExists(dg, n3, n2, DerivedEdge.EQLESS));
        assert (TestGraph1.edgeExists(dg, n3, n4, DerivedEdge.EQLESS));
        assert (TestGraph1.edgeExists(dg, n4, n3, DerivedEdge.EQLESS));
        assert (TestGraph1.edgeExists(dg, n7, n8, DerivedEdge.EQLESS));
        assert (TestGraph1.edgeExists(dg, n8, n7, DerivedEdge.EQLESS));
    }
}
