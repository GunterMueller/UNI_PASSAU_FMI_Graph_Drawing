/*==============================================================================
*
*   BarryOrderTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: BarryOrderTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import java.util.*;

import org.visnacom.sugiyama.algorithm.VertexOrdering;
import org.visnacom.sugiyama.model.*;

import junit.framework.TestCase;

/**
 *
 */
public class BarryOrderTest extends TestCase {
    //~ Instance fields ========================================================

    LinkedList list;
    LocalHierarchy lh;
    SugiNode n0;
    SugiNode n1;
    SugiNode n10;
    SugiNode n11;
    SugiNode n12;
    SugiNode n13;
    SugiNode n14;
    SugiNode n15;
    SugiNode n16;
    SugiNode n17;
    SugiNode n18;
    SugiNode n19;
    SugiNode n2;
    SugiNode n3;
    SugiNode n4;
    SugiNode n5;
    SugiNode n6;
    SugiNode n7;
    SugiNode n8;
    SugiNode n9;

    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(BarryOrderTest.class);
    }

    /**
     *
     */
    public final void testBol1() {
        n4.setLambdaRho(1, 0);
        n6.setLambdaRho(1, 2);

        LHEdge e1 = lh.ensureEdge(n0, n10);
        LHEdge e2 = lh.ensureEdge(n0, n11);
        LHEdge e3 = lh.ensureEdge(n1, n10);
        LHEdge e3b = lh.ensureEdge(n1, n11);
        LHEdge e4 = lh.ensureEdge(n1, n12);
        LHEdge e5 = lh.ensureEdge(n3, n12);
        LHEdge e6 = lh.ensureEdge(n3, n13);
        LHEdge e7 = lh.ensureEdge(n3, n14);
        LHEdge e8 = lh.ensureEdge(n4, n14);
        LHEdge e9 = lh.ensureEdge(n4, n15);
        LHEdge e10 = lh.ensureEdge(n5, n14);
        LHEdge e11 = lh.ensureEdge(n5, n15);
        LHEdge e12 = lh.ensureEdge(n6, n16);
        LHEdge e13 = lh.ensureEdge(n7, n16);
        LHEdge e14 = lh.ensureEdge(n8, n16);

        List nodes = lh.getNodes();
        HashMap down = lh.getVertical();
        HashMap horizontal = lh.getHorizontal();
        VertexOrdering.preprocessing(lh);
        VertexOrdering.bol(1, nodes, down);
        //computes barrycenter-values correct?
        assertEquals(n0.getBarryCenter(), 0.5, 0);
        assertEquals(n1.getBarryCenter(), 1, 0);
        assertEquals(n2.getBarryCenter(), 1, 0);
        assertEquals(n3.getBarryCenter(), 3, 0);
        assertEquals(n4.getBarryCenter(), 4.5, 0);
        assertEquals(n5.getBarryCenter(), 4.5, 0);
        assertEquals(n6.getBarryCenter(), 6, 0);
        assertEquals(n7.getBarryCenter(), 6, 0);
        assertEquals(n8.getBarryCenter(), 6, 0);
        assertEquals(n9.getBarryCenter(), 6, 0);

        //
        //
        //        //sorting correct?
        List level1 = (List) nodes.get(1);
        assertEquals(level1.indexOf(n0), 1);
        assertEquals(level1.indexOf(n1), 2);
        assertEquals(level1.indexOf(n2), 3);
        assertEquals(level1.indexOf(n3), 4);
        assertEquals(level1.indexOf(n4), 0);
        assertEquals(level1.indexOf(n5), 5);
        assertEquals(level1.indexOf(n6), 9);
        assertEquals(level1.indexOf(n7), 6);
        assertEquals(level1.indexOf(n8), 7);
        assertEquals(level1.indexOf(n9), 8);
        //position-values correct?
        LocalHierarchy.checkPositions(nodes);
    }

    /**
     *
     */
    public void testBOEmpty() {
        //the level 0 is empty
        List nodes = lh.getNodes();
        HashMap down = lh.getVertical();
        HashMap horizontal = lh.getHorizontal();
        VertexOrdering.preprocessing(lh);
        VertexOrdering.bou(1, nodes, down);
        VertexOrdering.bol(1, nodes, down);
    }

    /**
     *
     */
    public final void testBou1() {
        //do nothing with the lambdarho-values, so all edges remain in view
        LHEdge e1 = lh.ensureEdge(n0, n11);
        LHEdge e2 = lh.ensureEdge(n0, n12);
        LHEdge e3 = lh.ensureEdge(n1, n11);
        LHEdge e4 = lh.ensureEdge(n1, n12);
        LHEdge e5 = lh.ensureEdge(n4, n13);
        LHEdge e6 = lh.ensureEdge(n4, n14);
        LHEdge e7 = lh.ensureEdge(n4, n18);
        LHEdge e8 = lh.ensureEdge(n5, n16);
        LHEdge e9 = lh.ensureEdge(n5, n17);
        LHEdge e10 = lh.ensureEdge(n6, n16);
        LHEdge e11 = lh.ensureEdge(n7, n16);
        LHEdge e12 = lh.ensureEdge(n7, n17);
        LHEdge e13 = lh.ensureEdge(n8, n18);
        List nodes = lh.getNodes();
        HashMap down = lh.getVertical();
        HashMap horizontal = lh.getHorizontal();
        VertexOrdering.preprocessing(lh);
        VertexOrdering.bou(2, nodes, down);
        //computes barrycenter-values correct?
        assertEquals(n10.getBarryCenter(), -1, 0);
        assertEquals(n11.getBarryCenter(), 0.5, 0);
        assertEquals(n12.getBarryCenter(), 0.5, 0);
        assertEquals(n13.getBarryCenter(), 4, 0);
        assertEquals(n14.getBarryCenter(), 4, 0);
        assertEquals(n15.getBarryCenter(), 4, 0);
        assertEquals(n16.getBarryCenter(), 6, 0);
        assertEquals(n17.getBarryCenter(), 6, 0);
        assertEquals(n18.getBarryCenter(), 6, 0);
        assertEquals(n19.getBarryCenter(), 6, 0);

        //sorting correct?
        List level2 = (List) nodes.get(2);
        assertEquals(level2.indexOf(n10), 0);
        assertEquals(level2.indexOf(n11), 1);
        assertEquals(level2.indexOf(n12), 2);
        assertEquals(level2.indexOf(n13), 3);
        assertEquals(level2.indexOf(n14), 4);
        assertEquals(level2.indexOf(n15), 5);
        assertEquals(level2.indexOf(n16), 6);
        assertEquals(level2.indexOf(n17), 7);
        assertEquals(level2.indexOf(n18), 8);
        assertEquals(level2.indexOf(n19), 9);
        //position-values correct?
        LocalHierarchy.checkPositions(nodes);
    }

    /**
     *
     */
    public void testBouEmpty() {
        List nodes = lh.getNodes();
        HashMap down = lh.getVertical();
        VertexOrdering.preprocessing(lh);
        VertexOrdering.bou(3, nodes, down);
    }

    /**
     *
     */
    public void testCreateLocalHierarchy() {
        //data structure nodes correct?
        List nodes = lh.getNodes();
        assertEquals(nodes.size(), 3);
        assertTrue(((List) nodes.get(0)).isEmpty());
        assertEquals(((List) nodes.get(1)).size(), 10);
        assertEquals(((List) nodes.get(2)).size(), 10);

        //position values correct?
        List level1 = (List) nodes.get(1);
        for(int i = 0; i < level1.size(); i++) {
            assertEquals(((SugiNode) level1.get(i)).getPosition(), i);
        }

        List level2 = (List) nodes.get(2);
        for(int i = 0; i < level2.size(); i++) {
            assertEquals(((SugiNode) level2.get(i)).getPosition(), i);
        }
    }

    /**
     *
     */
    public final void testIduLDDUL() {
        CompoundLevel clev = n1.getClev();
        DummyNode dn = new DummyNode(-1);
        dn.setType(DummyNode.HORIZONTAL);
        dn.setClev(clev);
        lh.addNode(dn);
        lh.ensureEdge(dn, n5);
        lh.ensureEdge(n2, dn);

        DummyNode dn2 = new DummyNode(-2);
        dn2.setType(DummyNode.HORIZONTAL);
        dn2.setClev(clev);
        lh.addNode(dn2);
        lh.ensureEdge(dn2, n1);
        lh.ensureEdge(n2, dn2);

        LHEdge e1 = lh.ensureEdge(n10, n12);
        LHEdge e2 = lh.ensureEdge(n12, n11);
        LHEdge e3 = lh.ensureEdge(n12, n13);
        LHEdge e4 = lh.ensureEdge(n13, n14);
        LHEdge e5 = lh.ensureEdge(n14, n12);
        LHEdge e5b = lh.ensureEdge(n14, n17);
        LHEdge e6 = lh.ensureEdge(n16, n15);
        LHEdge e7 = lh.ensureEdge(n17, n19);
        LHEdge e8 = lh.ensureEdge(n18, n17);

        List info = VertexOrdering.preprocessing(lh);
        VertexOrdering.idu(1, lh);
        VertexOrdering.ddu(1, lh);
        VertexOrdering.idu(2, lh);
        VertexOrdering.ddu(2, lh);
        VertexOrdering.idl(1, lh);
        VertexOrdering.ddl(1, lh);
        VertexOrdering.idl(2, lh);
        VertexOrdering.ddl(2, lh);
    }

    /**
     *
     */
    public final void testReplaceExternalDummynodes() {
        CompoundLevel clev = n1.getClev();
        DummyNode dn = new DummyNode(-1);
        dn.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn.setLambdaRho(1, 0);
        dn.setClev(clev);
        lh.addNode(dn, 1);
        lh.ensureEdge(dn, n5);

        DummyNode dn2 = new DummyNode(-2);
        dn2.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn2.setLambdaRho(0, 1);
        dn2.setClev(clev);
        lh.addNode(dn2, 7);
        lh.ensureEdge(n6, dn2);

        List nodes = lh.getNodes();
        HashMap down = lh.getVertical();
        HashMap horizontal = lh.getHorizontal();

        assertEquals(1.0f, dn.getLambdaRho(), 0);
        assertEquals(-1.0, dn2.getLambdaRho(), 0);

        List info = VertexOrdering.preprocessing(lh);
        assertEquals(1, n5.getLambdaRho(), 0);
        assertEquals(-1, n6.getLambdaRho(), 0);

        assertTrue(((List) nodes.get(0)).isEmpty());
        assertEquals(((List) nodes.get(1)).size(), 10);

        VertexOrdering.postprocessing(lh, info);

        assertEquals(1, n5.getLambdaRho(), 0);
        assertTrue(n5.getPosition() > dn.getPosition());
        assertEquals(-1, n6.getLambdaRho(), 0);
        assertTrue(n6.getPosition() < dn2.getPosition());
    }

    /**
     *
     */
    public void testAll() {
        SugiNode v = new SugiNode();
        v.setLocalHierarchy(lh);

        SugiNode n20 = new SugiNode(20);
        SugiNode n21 = new SugiNode(21);
        SugiNode n22 = new SugiNode(22);
        SugiNode n23 = new SugiNode(23);
        SugiNode n24 = new SugiNode(24);
        SugiNode n25 = new SugiNode(25);
        SugiNode n26 = new SugiNode(26);
        SugiNode n27 = new SugiNode(27);
        SugiNode n28 = new SugiNode(28);
        SugiNode n29 = new SugiNode(29);
        CompoundLevel root = CompoundLevel.getClevForRoot();
        n20.setClev(root.getSubLevel(3));
        n21.setClev(root.getSubLevel(3));
        n22.setClev(root.getSubLevel(3));
        n23.setClev(root.getSubLevel(3));
        n24.setClev(root.getSubLevel(3));
        n25.setClev(root.getSubLevel(3));
        n26.setClev(root.getSubLevel(3));
        n27.setClev(root.getSubLevel(3));
        n28.setClev(root.getSubLevel(3));
        n29.setClev(root.getSubLevel(3));
        lh.addNode(n20);
        lh.addNode(n21);
        lh.addNode(n22);
        lh.addNode(n23);
        lh.addNode(n24);
        lh.addNode(n25);
        lh.addNode(n26);
        lh.addNode(n27);
        lh.addNode(n28);
        lh.addNode(n29);
        n20.setLambdaRho(0, 0);
        n21.setLambdaRho(1, 1);
        n22.setLambdaRho(0, 0);
        n23.setLambdaRho(0, 0);
        n24.setLambdaRho(0, 0);
        n25.setLambdaRho(0, 0);
        n26.setLambdaRho(0, 0);
        n27.setLambdaRho(0, 0);
        n28.setLambdaRho(0, 1);
        n29.setLambdaRho(0, 0);
        n15.setLambdaRho(0, 2);
        n18.setLambdaRho(0, 1);
        n19.setLambdaRho(0, 1);

        DummyNode dn1 = new DummyNode(-1);
        DummyNode dn2 = new DummyNode(-2);
        DummyNode dn3 = new DummyNode(-3);
        DummyNode dn4 = new DummyNode(-4);
        DummyNode dn5 = new DummyNode(-5);
        DummyNode dn6 = new DummyNode(-6);
        DummyNode dn7 = new DummyNode(-7);
        DummyNode dn8 = new DummyNode(-8);
        DummyNode dn9 = new DummyNode(-9);
        DummyNode dn10 = new DummyNode(-10);
        DummyNode dn11 = new DummyNode(-11);
        DummyNode dn12 = new DummyNode(-12);
        DummyNode dn13 = new DummyNode(-13);
        DummyNode dn14 = new DummyNode(-14);
        DummyNode dn15 = new DummyNode(-15);
        DummyNode dn16 = new DummyNode(-16);

        dn1.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn2.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn3.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn4.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn5.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn6.setType(DummyNode.HORIZONTAL);
        dn7.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn8.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn9.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn10.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn11.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn12.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn13.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn14.setType(DummyNode.NORMAL);
        dn15.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn16.setType(DummyNode.NORMAL);
        dn1.setClev(n10.getClev());
        dn2.setClev(n10.getClev());
        dn3.setClev(n10.getClev());
        dn4.setClev(n10.getClev());
        dn5.setClev(n10.getClev());
        dn6.setClev(n0.getClev());
        dn7.setClev(n0.getClev());
        dn8.setClev(n0.getClev());
        dn9.setClev(n10.getClev());
        dn10.setClev(n10.getClev());
        dn11.setClev(n20.getClev());
        dn12.setClev(n20.getClev());
        dn13.setClev(n0.getClev());
        dn14.setClev(n10.getClev());
        dn15.setClev(n20.getClev());
        dn16.setClev(n10.getClev());
        dn1.resetLambdaRho();
        dn2.resetLambdaRho();
        dn3.resetLambdaRho();
        dn4.resetLambdaRho();
        dn5.resetLambdaRho();
        dn6.resetLambdaRho();
        dn7.setLambdaRho(0, 1);
        dn8.setLambdaRho(1, 0);
        dn9.setLambdaRho(1, 0);
        dn10.setLambdaRho(0, 1);
        dn11.resetLambdaRho();
        dn12.resetLambdaRho();
        dn13.resetLambdaRho();
        dn14.resetLambdaRho();
        dn15.resetLambdaRho();
        dn16.resetLambdaRho();
        lh.addNode(dn1);
        lh.addNode(dn2);
        lh.addNode(dn3);
        lh.addNode(dn4);
        lh.addNode(dn5);
        lh.addNode(dn6);
        lh.addNode(dn7);
        lh.addNode(dn8);
        lh.addNode(dn9);
        lh.addNode(dn10);
        lh.addNode(dn11);
        lh.addNode(dn12);
        lh.addNode(dn13);
        lh.addNode(dn14);
        lh.addNode(dn15);
        lh.addNode(dn16);

        lh.ensureEdge(n0, dn4);
        lh.ensureEdge(dn4, n12);
        lh.ensureEdge(n12, dn2);
        lh.ensureEdge(dn2, n20);
        lh.ensureEdge(n1, n12);
        lh.ensureEdge(n3, dn3);
        lh.ensureEdge(dn3, n12);
        lh.ensureEdge(n12, dn1);
        lh.ensureEdge(dn1, n24);
        lh.ensureEdge(n5, dn5);
        lh.ensureEdge(dn5, n12);
        lh.ensureEdge(n4, dn6);

        lh.ensureEdge(dn6, n3);
        lh.ensureEdge(n0, n1);
        lh.ensureEdge(n0, n2);
        lh.ensureEdge(n4, n5);
        lh.ensureEdge(n4, n7);
        lh.ensureEdge(n6, n16);
        lh.ensureEdge(n7, n17);
        lh.ensureEdge(n8, n14);
        lh.ensureEdge(n9, dn7);
        lh.ensureEdge(n0, dn8);
        lh.ensureEdge(n26, n27);
        lh.ensureEdge(n28, n26);
        lh.ensureEdge(dn9, n11);
        lh.ensureEdge(n11, dn10);
        lh.ensureEdge(dn11, n25);
        lh.ensureEdge(dn12, n26);
        lh.ensureEdge(n16, dn11);
        lh.ensureEdge(n16, dn12);
        lh.ensureEdge(n1, dn13);
        lh.ensureEdge(dn13, dn14);
        lh.ensureEdge(dn14, dn15);
        lh.ensureEdge(dn15, n21);
        lh.ensureEdge(n9, dn16);
        lh.ensureEdge(dn16, n23);
        VertexOrdering.vOrderLocal(v);
    }

    /**
     *
     */
    public void testReplaceHorizontal() {
        CompoundLevel clev = n1.getClev();
        DummyNode dn = new DummyNode(-1);
        dn.setType(DummyNode.HORIZONTAL);
        dn.setClev(clev);
        lh.addNode(dn);
        lh.ensureEdge(dn, n5);
        lh.ensureEdge(n2, dn);

        DummyNode dn2 = new DummyNode(-2);
        dn2.setType(DummyNode.HORIZONTAL);
        dn2.setClev(clev);
        lh.addNode(dn2);
        lh.ensureEdge(dn2, n1);
        lh.ensureEdge(n2, dn2);

        List nodes = lh.getNodes();
        HashMap down = lh.getVertical();
        HashMap horizontal = lh.getHorizontal();
        List info = VertexOrdering.preprocessing(lh);

        //check nodesView
        assertTrue(((List) nodes.get(0)).isEmpty());
        assertEquals(((List) nodes.get(1)).size(), 10);
        assertEquals(((List) nodes.get(2)).size(), 10);
        VertexOrdering.postprocessing(lh, info);
    }

    /**
     *
     */
    public void testReplaceInternalDummyNodes() {
        SugiNode v = new SugiNode();
        v.setLocalHierarchy(lh);

        SugiNode n20 = new SugiNode(20);
        SugiNode n21 = new SugiNode(21);
        SugiNode n22 = new SugiNode(22);
        SugiNode n23 = new SugiNode(23);
        SugiNode n24 = new SugiNode(24);
        SugiNode n25 = new SugiNode(25);
        SugiNode n26 = new SugiNode(26);
        SugiNode n27 = new SugiNode(27);
        SugiNode n28 = new SugiNode(28);
        SugiNode n29 = new SugiNode(29);
        CompoundLevel root = CompoundLevel.getClevForRoot();
        n20.setClev(root.getSubLevel(3));
        n21.setClev(root.getSubLevel(3));
        n22.setClev(root.getSubLevel(3));
        n23.setClev(root.getSubLevel(3));
        n24.setClev(root.getSubLevel(3));
        n25.setClev(root.getSubLevel(3));
        n26.setClev(root.getSubLevel(3));
        n27.setClev(root.getSubLevel(3));
        n28.setClev(root.getSubLevel(3));
        n29.setClev(root.getSubLevel(3));
        lh.addNode(n20);
        lh.addNode(n21);
        lh.addNode(n22);
        lh.addNode(n23);
        lh.addNode(n24);
        lh.addNode(n25);
        lh.addNode(n26);
        lh.addNode(n27);
        lh.addNode(n28);
        lh.addNode(n29);
        n20.setLambdaRho(0, 0);
        n21.setLambdaRho(1, 1);
        n22.setLambdaRho(0, 0);
        n23.setLambdaRho(0, 0);
        n24.setLambdaRho(0, 0);
        n25.setLambdaRho(0, 0);
        n26.setLambdaRho(0, 0);
        n27.setLambdaRho(0, 0);
        n28.setLambdaRho(0, 0);
        n29.setLambdaRho(0, 0);

        CompoundLevel clev = n10.getClev();
        DummyNode dn1 = new DummyNode(-1);
        DummyNode dn2 = new DummyNode(-2);
        DummyNode dn3 = new DummyNode(-3);
        DummyNode dn4 = new DummyNode(-4);
        DummyNode dn5 = new DummyNode(-5);

        dn1.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn2.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn3.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn4.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn5.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn1.setClev(clev);
        dn2.setClev(clev);
        dn3.setClev(clev);
        dn4.setClev(clev);
        dn5.setClev(clev);
        dn1.resetLambdaRho();
        dn2.resetLambdaRho();
        dn3.resetLambdaRho();
        dn4.resetLambdaRho();
        dn5.resetLambdaRho();
        lh.addNode(dn1);
        lh.addNode(dn2);
        lh.addNode(dn3);
        lh.addNode(dn4);
        lh.addNode(dn5);

        lh.ensureEdge(n0, dn4);
        lh.ensureEdge(dn4, n12);
        lh.ensureEdge(n12, dn2);
        lh.ensureEdge(dn2, n20);
        lh.ensureEdge(n1, n12);
        lh.ensureEdge(n3, dn3);
        lh.ensureEdge(dn3, n12);
        lh.ensureEdge(n12, dn1);
        lh.ensureEdge(dn1, n24);
        lh.ensureEdge(n5, dn5);
        lh.ensureEdge(dn5, n12);

        VertexOrdering.vOrderLocal(v);
        assertEquals(0, n10.getPosition());
        assertEquals(1, n11.getPosition());
        assertEquals(2, dn4.getPosition());
        assertEquals(3, dn2.getPosition());
        assertEquals(4, n12.getPosition());
        assertEquals(5, dn3.getPosition());
        assertEquals(6, dn1.getPosition());
        assertEquals(7, dn5.getPosition());
        assertEquals(8, n13.getPosition());
    }

    /**
     *
     */
    public void testReplacelocal1() {
        CompoundLevel clev = n1.getClev();
        DummyNode dn = new DummyNode(-1);
        dn.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn.setClev(clev);
        dn.resetLambdaRho();
        lh.addNode(dn, 5);
        lh.ensureEdge(n4, dn);

        CompoundLevel clev2 = n11.getClev();
        DummyNode dn2 = new DummyNode(-2);
        dn2.setType(DummyNode.LOCAL_OR_EXTERNAL);
        dn2.setClev(clev2);
        dn2.resetLambdaRho();
        lh.addNode(dn2, 5);

        lh.ensureEdge(dn, dn2);
        lh.ensureEdge(dn2, n14);

        List nodes = lh.getNodes();
        List info = VertexOrdering.preprocessing(lh);
        VertexOrdering.postprocessing(lh, info);
    }

    /**
     *
     */
    public void testvOrderLocal() {
        SugiNode v = new SugiNode();
        LocalHierarchy lh = new LocalHierarchy();
        v.setLocalHierarchy(lh);

        SugiNode n0;
        SugiNode n1;
        SugiNode n2;
        SugiNode n3;
        SugiNode n4;
        n0 = new SugiNode(0);
        n1 = new SugiNode(1);
        n2 = new SugiNode(2);
        n3 = new SugiNode(3);
        n4 = new SugiNode(4);
        n0.resetLambdaRho();
        n1.resetLambdaRho();
        n2.resetLambdaRho();
        n3.resetLambdaRho();
        n4.resetLambdaRho();

        CompoundLevel root = CompoundLevel.getClevForRoot();
        n0.setClev(root.getSubLevel(1));
        n1.setClev(root.getSubLevel(1));
        n2.setClev(root.getSubLevel(3));
        n3.setClev(root.getSubLevel(4));
        n4.setClev(root.getSubLevel(4));
        lh.addNode(n0);
        lh.addNode(n1);
        lh.addNode(n2);
        lh.addNode(n3);
        lh.addNode(n4);

        lh.ensureEdge(n0, n1);
        lh.ensureEdge(n1, n0);
        lh.ensureEdge(n2, n3);
        lh.ensureEdge(n2, n4);
        lh.ensureEdge(n3, n4);
        VertexOrdering.vOrderLocal(v);
    }

    /**
     *
     */
    public void testvOrderLocal2() {
        SugiNode v = new SugiNode();
        v.setLocalHierarchy(lh);

        SugiNode n20 = new SugiNode(20);
        SugiNode n21 = new SugiNode(21);
        SugiNode n22 = new SugiNode(22);
        SugiNode n23 = new SugiNode(23);
        SugiNode n24 = new SugiNode(24);
        SugiNode n25 = new SugiNode(25);
        SugiNode n26 = new SugiNode(26);
        SugiNode n27 = new SugiNode(27);
        SugiNode n28 = new SugiNode(28);
        SugiNode n29 = new SugiNode(29);
        CompoundLevel root = CompoundLevel.getClevForRoot();
        n20.setClev(root.getSubLevel(3));
        n21.setClev(root.getSubLevel(3));
        n22.setClev(root.getSubLevel(3));
        n23.setClev(root.getSubLevel(3));
        n24.setClev(root.getSubLevel(3));
        n25.setClev(root.getSubLevel(3));
        n26.setClev(root.getSubLevel(3));
        n27.setClev(root.getSubLevel(3));
        n28.setClev(root.getSubLevel(3));
        n29.setClev(root.getSubLevel(3));
        lh.addNode(n20);
        lh.addNode(n21);
        lh.addNode(n22);
        lh.addNode(n23);
        lh.addNode(n24);
        lh.addNode(n25);
        lh.addNode(n26);
        lh.addNode(n27);
        lh.addNode(n28);
        lh.addNode(n29);
        n0.setLambdaRho(0, 0);
        n1.setLambdaRho(1, 1);
        n2.setLambdaRho(1, 0);
        n3.setLambdaRho(1, 0);
        n4.setLambdaRho(3, 0);
        n5.setLambdaRho(3, 0);
        n6.setLambdaRho(0, 0);
        n7.setLambdaRho(2, 0);
        n8.setLambdaRho(0, 0);
        n9.setLambdaRho(0, 1);
        n10.setLambdaRho(0, 0);
        n11.setLambdaRho(0, 0);
        n12.setLambdaRho(0, 0);
        n13.setLambdaRho(1, 0);
        n14.setLambdaRho(0, 0);
        n15.setLambdaRho(0, 0);
        n16.setLambdaRho(0, 2);
        n17.setLambdaRho(0, 0);
        n18.setLambdaRho(0, 0);
        n19.setLambdaRho(0, 0);
        n20.setLambdaRho(0, 1);
        n21.setLambdaRho(1, 1);
        n22.setLambdaRho(0, 0);
        n23.setLambdaRho(0, 0);
        n24.setLambdaRho(0, 0);
        n25.setLambdaRho(0, 0);
        n26.setLambdaRho(0, 0);
        n27.setLambdaRho(0, 0);
        n28.setLambdaRho(0, 0);
        n29.setLambdaRho(0, 0);

        lh.ensureEdge(n0, n10);
        lh.ensureEdge(n0, n11);
        lh.ensureEdge(n1, n10);
        lh.ensureEdge(n1, n11);
        lh.ensureEdge(n1, n12);
        lh.ensureEdge(n3, n12);
        lh.ensureEdge(n3, n13);
        lh.ensureEdge(n3, n14);
        lh.ensureEdge(n4, n14);
        lh.ensureEdge(n4, n15);
        lh.ensureEdge(n5, n14);
        lh.ensureEdge(n5, n15);
        lh.ensureEdge(n6, n16);
        lh.ensureEdge(n7, n16);
        lh.ensureEdge(n8, n16);

        lh.ensureEdge(n10, n21);
        lh.ensureEdge(n10, n22);
        lh.ensureEdge(n11, n21);
        lh.ensureEdge(n11, n22);
        lh.ensureEdge(n14, n23);
        lh.ensureEdge(n14, n24);
        lh.ensureEdge(n14, n28);
        lh.ensureEdge(n15, n26);
        lh.ensureEdge(n15, n27);
        lh.ensureEdge(n16, n26);
        lh.ensureEdge(n17, n26);
        lh.ensureEdge(n17, n27);
        lh.ensureEdge(n18, n28);

        lh.ensureEdge(n0, n2);
        lh.ensureEdge(n2, n1);
        lh.ensureEdge(n2, n3);
        lh.ensureEdge(n3, n4);
        lh.ensureEdge(n4, n2);
        lh.ensureEdge(n4, n7);
        lh.ensureEdge(n6, n5);
        lh.ensureEdge(n6, n8);
        lh.ensureEdge(n7, n9);
        lh.ensureEdge(n8, n7);

        lh.ensureEdge(n10, n12);
        lh.ensureEdge(n12, n11);
        lh.ensureEdge(n14, n13);
        lh.ensureEdge(n14, n15);
        lh.ensureEdge(n14, n17);
        lh.ensureEdge(n17, n15);
        lh.ensureEdge(n18, n19);
        lh.ensureEdge(n21, n22);
        lh.ensureEdge(n22, n23);
        lh.ensureEdge(n22, n24);
        lh.ensureEdge(n25, n24);
        lh.ensureEdge(n25, n26);
        lh.ensureEdge(n29, n27);
        lh.ensureEdge(n29, n28);
        VertexOrdering.vOrderLocal(v);
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() {
        lh = new LocalHierarchy();
        n0 = new SugiNode(0);
        n1 = new SugiNode(1);
        n2 = new SugiNode(2);
        n3 = new SugiNode(3);
        n4 = new SugiNode(4);
        n5 = new SugiNode(5);
        n6 = new SugiNode(6);
        n7 = new SugiNode(7);
        n8 = new SugiNode(8);
        n9 = new SugiNode(9);
        n10 = new SugiNode(10);
        n11 = new SugiNode(11);
        n12 = new SugiNode(12);
        n13 = new SugiNode(13);
        n14 = new SugiNode(14);
        n15 = new SugiNode(15);
        n16 = new SugiNode(16);
        n17 = new SugiNode(17);
        n18 = new SugiNode(18);
        n19 = new SugiNode(19);
        list = new LinkedList();
        list.add(n10);
        list.add(n11);
        list.add(n12);
        list.add(n13);
        list.add(n14);
        list.add(n15);
        list.add(n16);
        list.add(n17);
        list.add(n18);
        list.add(n19);
        list.add(n0);
        list.add(n1);
        list.add(n2);
        list.add(n3);
        list.add(n4);
        list.add(n5);
        list.add(n6);
        list.add(n7);
        list.add(n8);
        list.add(n9);
        for(Iterator it = list.iterator(); it.hasNext();) {
            ((SugiNode) it.next()).resetLambdaRho();
        }

        CompoundLevel root = CompoundLevel.getClevForRoot();
        n0.setClev(root.getSubLevel(1));
        n1.setClev(root.getSubLevel(1));
        n2.setClev(root.getSubLevel(1));
        n3.setClev(root.getSubLevel(1));
        n4.setClev(root.getSubLevel(1));
        n5.setClev(root.getSubLevel(1));
        n6.setClev(root.getSubLevel(1));
        n7.setClev(root.getSubLevel(1));
        n8.setClev(root.getSubLevel(1));
        n9.setClev(root.getSubLevel(1));
        n10.setClev(root.getSubLevel(2));
        n11.setClev(root.getSubLevel(2));
        n12.setClev(root.getSubLevel(2));
        n13.setClev(root.getSubLevel(2));
        n14.setClev(root.getSubLevel(2));
        n15.setClev(root.getSubLevel(2));
        n16.setClev(root.getSubLevel(2));
        n17.setClev(root.getSubLevel(2));
        n18.setClev(root.getSubLevel(2));
        n19.setClev(root.getSubLevel(2));
        lh.addNodes(list);
    }
}
