/*==============================================================================
*
*   CreateDerivedGraphTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: CreateDerivedGraphTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import org.visnacom.model.CompoundGraph;
import org.visnacom.model.Edge;
import org.visnacom.model.Node;
import org.visnacom.sugiyama.algorithm.Hierarchization;
import org.visnacom.sugiyama.model.DerivedEdge;
import org.visnacom.sugiyama.model.DerivedGraph;
import org.visnacom.sugiyama.model.SugiCompoundGraph;
import org.visnacom.sugiyama.model.SugiEdge;
import org.visnacom.sugiyama.model.SugiNode;

import junit.framework.TestCase;

/**
 * DOCUMENT ME!
 */
public class CreateDerivedGraphTest extends TestCase {
    //~ Constructors ===========================================================

    /**
     * Creates a new CreateDerivedGraphTest object.
     *
     * @param arg0 DOCUMENT ME!
     */
    public CreateDerivedGraphTest(String arg0) {
        super(arg0);
    }

    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(CreateDerivedGraphTest.class);
    }

    /**
     * DOCUMENT ME!
     */
    protected void setUp() {}

    /**
     * DOCUMENT ME!
     */
    public void testUsesSugiNodes() {
        CompoundGraph c = new SugiCompoundGraph();

        Node n1 = c.newLeaf(c.getRoot());
        assertTrue(n1 instanceof SugiNode);
        assertTrue(c.getRoot() instanceof SugiNode);
        
        CompoundGraph d = new DerivedGraph();

        Node n2 = d.newLeaf(d.getRoot());
        assertTrue(n2 instanceof SugiNode);
        assertTrue(d.getRoot() instanceof SugiNode);
    }

    /**
     * DOCUMENT ME!
     */
    public void testUsesSugiEdges() {
        CompoundGraph c = new SugiCompoundGraph();

        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());

        Edge e = c.newEdge(n1, n2);
        assertTrue(e instanceof SugiEdge);
        
        DerivedGraph d = new DerivedGraph();

        Node n3 = d.newLeaf(d.getRoot());
        Node n4 = d.newLeaf(d.getRoot());

        Edge e2 = d.newEdge(n3, n4, DerivedEdge.LESS);
        assertTrue(e2 instanceof DerivedEdge);
    }

    /**
     * DOCUMENT ME!
     */
    public void testEmptySet() {
        //System.out.println();
        //System.out.println(getName());
        SugiCompoundGraph c = new SugiCompoundGraph();

        //System.out.println(c);
        try {
            DerivedGraph dg = Hierarchization.createDerivedGraph(c);
        } catch(Exception e) {
            fail();
        }

        //System.out.println(dg);
    }

    /**
     * DOCUMENT ME!
     */
    public void testOneNode() {
        //System.out.println(getClass());
        //System.out.println(getName());
        SugiCompoundGraph c = new SugiCompoundGraph();
        Node n0 = c.newLeaf(c.getRoot());
        //System.out.println(c);


        DerivedGraph dg = Hierarchization.createDerivedGraph(c);

        //System.out.println(dg);
        assertTrue(dg.getAllEdges().isEmpty());
        assertEquals(dg.getAllNodes().size(), 2); 
    }

    /**
     * DOCUMENT ME!
     */
    public final void testCreateDerivedGraph() {
        //System.out.println(getClass());
        //System.out.println(getName());


        SugiCompoundGraph c = new SugiCompoundGraph();
        Node n0 = c.newLeaf(c.getRoot());
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(c.getRoot());
        Node n5 = c.newLeaf(n1);
        c.newEdge(n1, n2);
        c.newEdge(n3, n4);
        c.newEdge(n1, n3);
        c.newEdge(n4, n5);
//        System.out.println(c);


        DerivedGraph dg = Hierarchization.createDerivedGraph(c);
        //System.out.println(dg);
        assertEquals(dg.getAllEdges().size(), 4);
        assertTrue(TestGraph1.edgeExists(dg, n1, n2, DerivedEdge.LESS));
        assertTrue(TestGraph1.edgeExists(dg, n1, n3, DerivedEdge.LESS));
        assertTrue(TestGraph1.edgeExists(dg, n3, n4, DerivedEdge.LESS));
        assertTrue(TestGraph1.edgeExists(dg, n4, n1, DerivedEdge.LESS));
    }

    /**
     * DOCUMENT ME!
     */
    public final void testCreateDerivedGraph2() {
//        System.out.println(getClass());
//        System.out.println(getName());
        TestGraph2 tg = new TestGraph2();
        SugiCompoundGraph c = tg.getTestGraph2();
      
        //System.out.println(c);
        DerivedGraph dg = Hierarchization.createDerivedGraph(c);

        //System.out.println(dg);
        assertTrue(tg.checkDerivedGraph(dg));
       
    }

    /**
     * DOCUMENT ME!
     */
    public final void testCreateDerivedGraph3() {
        //System.out.println();
        //System.out.println(getName());
        TestGraph1 tg = new TestGraph1();
        SugiCompoundGraph c = tg.getSugiTestGraph1();

        //System.out.println(c);
        DerivedGraph dg = Hierarchization.createDerivedGraph(c);

        //System.out.println(dg);
        tg.checkDerivedGraph(dg);
    }

   
}
