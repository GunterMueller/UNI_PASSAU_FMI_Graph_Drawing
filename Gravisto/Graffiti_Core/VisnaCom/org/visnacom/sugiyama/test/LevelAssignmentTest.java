/*==============================================================================
*
*   LevelAssignmentTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: LevelAssignmentTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import java.util.LinkedList;
import java.util.List;

import org.visnacom.sugiyama.algorithm.Hierarchization;
import org.visnacom.sugiyama.algorithm.TopSort;
import org.visnacom.sugiyama.model.CompoundLevel;
import org.visnacom.sugiyama.model.DerivedEdge;
import org.visnacom.sugiyama.model.DerivedGraph;
import org.visnacom.sugiyama.model.SugiCompoundGraph;
import org.visnacom.sugiyama.model.SugiNode;

import junit.framework.TestCase;

/**
 * DOCUMENT ME!
 */
public class LevelAssignmentTest extends TestCase {
    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(LevelAssignmentTest.class);
    }

    /**
     * DOCUMENT ME!
     */
    public final void test1() {
        System.out.println(getClass());
        System.out.println(getName());


        TestGraph1 tg = new TestGraph1();
        SugiCompoundGraph c = tg.getSugiTestGraph1();

        //System.out.println(c);
        DerivedGraph dg = Hierarchization.createDerivedGraph(c);

        tg.checkDerivedGraph(dg);
        Hierarchization.resolveCycles(dg);


        List l = new TopSort().topSort(dg, c.getAllNodes());
        assertTrue(TopSortTest.checkEdges(dg, l));

        System.out.println(dg);
        Hierarchization.levelAssignment(c, dg);
        System.out.println(c);
    }

    /**
     * DOCUMENT ME!
     */
    public final void test2() {
        System.out.println(getClass());
        System.out.println(getName());


        TestGraph2 tg = new TestGraph2();
        SugiCompoundGraph c = tg.getTestGraph2();

        //System.out.println(c);
        DerivedGraph dg = Hierarchization.createDerivedGraph(c);
        assertTrue(tg.checkDerivedGraph(dg));
        Hierarchization.testConsistenceOfDerivedGraph(dg);
        Hierarchization.resolveCycles(dg);
        System.out.println(dg);
        Hierarchization.levelAssignment(c, dg);
        System.out.println(c);
    }

    /**
     *
     */
    public final void testEmptySet() {
        System.out.println(getClass());
        System.out.println(getName());


        SugiCompoundGraph s = new SugiCompoundGraph();
        DerivedGraph d = new DerivedGraph();
        Hierarchization.levelAssignment(s, d);
    }

    /**
     * DOCUMENT ME!
     */
    public final void testGraph3useProxy() {
        System.out.println(getClass());
        System.out.println(this.getName());


        TestGraph3 tg = new TestGraph3();
        SugiCompoundGraph c = tg.getTestGraph3withproxy();
        DerivedGraph dg = Hierarchization.createDerivedGraph(c);
        tg.checkDerivedGraph(dg);

        Hierarchization.resolveCycles(dg);
        System.out.println(dg);

        Hierarchization.levelAssignment(c, dg);
        System.out.println(c);
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

        Hierarchization.levelAssignment(c, dg);
        System.out.println(c);
    }
}
