/*==============================================================================
*
*   MetricLayoutTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: MetricLayoutTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.sugiyama.algorithm.*;
import org.visnacom.sugiyama.eval.DummyPicture;
import org.visnacom.sugiyama.model.SugiCompoundGraph;

import junit.framework.TestCase;

/**
 * tests for the naive horizontal metrical layout
 */
public class MetricLayoutTest extends TestCase {
    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(MetricLayoutTest.class);
    }

   
    /**
     * DOCUMENT ME!
     */
    public final void test1() {
        System.out.println(getClass());
        System.out.println(getName());


        TestGraph1 tg = new TestGraph1();
        SugiCompoundGraph s = tg.getSugiTestGraph1();

        //System.out.println(c);
        Hierarchization.hierarchize(s);
        Normalization.normalize(s);
        VertexOrdering.order(s, 5);
        for(int i = 0; i <= 4; i++) {
            MetricLayout.layout(s, i);
            DummyPicture.show(s);
            //DummyPicture.write(s, "testgraph1_"+ i);
        }
    }

    /**
     * DOCUMENT ME!
     */
    public final void test2() {
        System.out.println(getClass());
        System.out.println(getName());


        TestGraph2 tg = new TestGraph2();
        SugiCompoundGraph s = tg.getTestGraph2();
        Hierarchization.hierarchize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);

        DummyPicture.show(s);
        //DummyPicture.write(s, "testgraph2");
        //System.out.println(s);
    }

    /**
     *
     */
    public final void testEmptySet() {
        System.out.println(getClass());
        System.out.println(getName());


        SugiCompoundGraph s = new SugiCompoundGraph();
        Hierarchization.hierarchize(s);
        VertexOrdering.order(s, 0);
        MetricLayout.layout(s, 4);
        System.out.println(s);
    }

    /**
     *
     */
    public final void testGraph3useProxy() {
        //        System.out.println(getClass());
        //        System.out.println(this.getName());
        TestGraph3 tg = new TestGraph3();
        SugiCompoundGraph s = tg.getTestGraph3withproxy();
        Hierarchization.hierarchize(s);
        VertexOrdering.order(s, -1);
        MetricLayout.layoutNaive(s);

        DummyPicture.show(s);
        //DummyPicture.write(s, "testgraph3withproxy.jpg");
        //System.out.println(s);
    }
}
