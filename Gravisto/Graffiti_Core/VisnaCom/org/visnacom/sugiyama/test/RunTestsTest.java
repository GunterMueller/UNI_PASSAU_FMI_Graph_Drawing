/*==============================================================================
*
*   EvaluationTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: RunTestsTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import java.util.List;

import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.sugiyama.algorithm.*;
import org.visnacom.sugiyama.eval.DummyPicture;
import org.visnacom.sugiyama.eval.Evaluation;
import org.visnacom.sugiyama.eval.IntPair;
import org.visnacom.sugiyama.eval.RunTests;
import org.visnacom.sugiyama.model.SugiCompoundGraph;
import org.visnacom.sugiyama.model.SugiNode;

import junit.framework.TestCase;

/**
 *
 */
public class RunTestsTest extends TestCase {
    //~ Methods ================================================================

    /**
     *
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(RunTestsTest.class);
    }

    /**
     *
     */
    public final void testComputeCrossingsAndCuts() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        SugiNode n1 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n2 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n3 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n4 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n5 = (SugiNode) s.newLeaf(n1);
        SugiNode n6 = (SugiNode) s.newLeaf(n1);
        SugiNode n7 = (SugiNode) s.newLeaf(n2);
        SugiNode n8 = (SugiNode) s.newLeaf(n4);
        SugiNode n9 = (SugiNode) s.newLeaf(n4);
        SugiNode n10 = (SugiNode) s.newLeaf(n6);
        SugiNode n11 = (SugiNode) s.newLeaf(n7);
        SugiNode n12 = (SugiNode) s.newLeaf(n9);
        SugiNode n13 = (SugiNode) s.newLeaf(n10);
        SugiNode n14 = (SugiNode) s.newLeaf(n12);
        s.newEdge(n13, n14);

        Hierarchization.hierarchize(s);
        Normalization.normalize(s);
        VertexOrdering.order(s, -1);//this works not correctly.
        //therefore the assertions in LocalHierarchy fail.
       

        MetricLayout.layout(s);//will fail if assertions are turned on. is caused
        //by "order(s,-1)". turn of assertions!
        DummyPicture.show(s);

        IntPair result = RunTests.computeCrossingsAndCuts(s);
        assertEquals(0,result.int1);
        assertEquals(5,result.int2);
    }

    /**
     *
     */
    public final void testComputeCrossingsAndCuts2() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        s.setDrawingStyle(SugiyamaDrawingStyle.DEBUG_STYLE);
        SugiNode n1 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n2 = (SugiNode) s.newLeaf(n1);
        SugiNode n3 = (SugiNode) s.newLeaf(n1);
        SugiNode n4 = (SugiNode) s.newLeaf(n1);
        SugiNode n5 = (SugiNode) s.newLeaf(n3);
        SugiNode n6 = (SugiNode) s.newLeaf(n3);
        SugiNode n7 = (SugiNode) s.newLeaf(n3);
        s.newEdge(n2,n3);
        s.newEdge(n3, n4);
        s.newEdge(n5, n6);
        s.newEdge(n6, n7);
        
        SugiNode n11 = (SugiNode) s.newLeaf(s.getRoot());
        SugiNode n12 = (SugiNode) s.newLeaf(n11);
        SugiNode n13 = (SugiNode) s.newLeaf(n11);
        SugiNode n14 = (SugiNode) s.newLeaf(n11);
        SugiNode n15 = (SugiNode) s.newLeaf(n13);
        SugiNode n16 = (SugiNode) s.newLeaf(n13);
        SugiNode n17 = (SugiNode) s.newLeaf(n13);
        s.newEdge(n12, n13);
        s.newEdge(n13, n14);
        s.newEdge(n15, n16);
        s.newEdge(n16, n17);
        SugiNode n20 = (SugiNode) s.newLeaf(s.getRoot());
//        SugiNode n21 = (SugiNode) s.newLeaf(s.getRoot());
//        SugiNode n22 = (SugiNode) s.newLeaf(s.getRoot());
//        SugiNode n23 = (SugiNode) s.newLeaf(s.getRoot());
//        SugiNode n24 = (SugiNode) s.newLeaf(s.getRoot());
//        SugiNode n25 = (SugiNode) s.newLeaf(s.getRoot());
//        SugiNode n26 = (SugiNode) s.newLeaf(s.getRoot());

        s.newEdge(n2,n14);
        s.newEdge(n2,n20);
        s.newEdge(n13, n20);
        s.newEdge(n4, n14);
        s.newEdge(n6,n17);
        Hierarchization.hierarchize(s);
        Normalization.normalize(s);
        VertexOrdering.order(s, 0);
        List level = ((SugiNode) s.getRoot()).getChildrenAtLevel(1);
        level.remove(n11);
        level.add(n11);
        ((SugiNode) s.getRoot()).getLocalHierarchy().updatePositions();
        MetricLayout.layout(s);
        DummyPicture.show(s);

        IntPair result = RunTests.computeCrossingsAndCuts(s);
        assertEquals(5,result.int1);
        assertEquals(0,result.int2);
    }
}
