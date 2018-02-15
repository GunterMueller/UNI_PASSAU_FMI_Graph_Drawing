/*==============================================================================
*
*   CrossCountTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: CrossCountTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import java.util.*;

import org.visnacom.model.Node;
import org.visnacom.sugiyama.algorithm.*;
import org.visnacom.sugiyama.model.*;

import junit.framework.TestCase;

/**
 *
 */
public class CrossCountTest extends TestCase {
    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(CrossCountTest.class);
    }

 
    /**
     *
     */
    public void testVerySmall() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        Node n1 = s.newLeaf(s.getRoot());
        Node n2 = s.newLeaf(s.getRoot());
        Node n3 = s.newLeaf(s.getRoot());

        //        s.newEdge(n1, n3);
        Hierarchization.hierarchize(s);
        ((SugiNode) n3).setClev(((SugiNode) s.getRoot()).getClev().getSubLevel(2));
        s.activateAllLHs(true);

        LocalHierarchy lh = ((SugiNode) s.getRoot()).getLocalHierarchy();
        CrossCount.radixSort(lh.getNodesAtLevel(1), lh.getNodesAtLevel(2),
            new IteratorOfCollections(lh.getVertical().values()));
        assertEquals(CrossCount.simpleAndEfficientCrossCount(
                lh.getNodesAtLevel(1), lh.getNodesAtLevel(2),
                new IteratorOfCollections(lh.getVertical().values())), 0);
    }

    /**
     *
     */
    public void testcrosscountMiddle() {
        LocalHierarchy lh;
        SugiNode n0;
        SugiNode n1;
        SugiNode n2;
        SugiNode n3;
        SugiNode n4;
        SugiNode n5;
        SugiNode n6;
        SugiNode n7;
        SugiNode n8;
        SugiNode n9;
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
        LinkedList list;

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
        lh.addNode(n0);
        lh.addNode(n1);
        lh.addNode(n2);
        lh.addNode(n3);
        lh.addNode(n4);
        lh.addNode(n5);
        lh.addNode(n6);
        lh.addNode(n7);
        lh.addNode(n8);
        lh.addNode(n9);
        lh.addNode(n10);
        lh.addNode(n11);
        lh.addNode(n12);
        lh.addNode(n13);
        lh.addNode(n14);
        lh.addNode(n15);
        lh.addNode(n16);
        lh.addNode(n17);
        lh.addNode(n18);
        lh.addNode(n19);

        List nodes = lh.getNodes();
        List nodesView = new ArrayList();
        HashMap down = lh.getVertical();
        HashMap horizontal = lh.getHorizontal();
        VertexOrdering.preprocessing(lh);

        LHEdge e3 = lh.ensureEdge(n1, n10);
        LHEdge e6 = lh.ensureEdge(n3, n13);
        LHEdge e8 = lh.ensureEdge(n4, n14);
        LHEdge e4 = lh.ensureEdge(n1, n12);
        LHEdge e14 = lh.ensureEdge(n8, n16);
        LHEdge e7 = lh.ensureEdge(n3, n14);
        LHEdge e12 = lh.ensureEdge(n6, n16);
        LHEdge e1 = lh.ensureEdge(n0, n10);
        LHEdge e9 = lh.ensureEdge(n4, n15);
        LHEdge e13 = lh.ensureEdge(n7, n16);
        LHEdge e10 = lh.ensureEdge(n5, n14);
        LHEdge e3b = lh.ensureEdge(n1, n11);
        LHEdge e5 = lh.ensureEdge(n3, n12);
        LHEdge e2 = lh.ensureEdge(n0, n11);
        LHEdge e11 = lh.ensureEdge(n5, n15);
        lh.ensureEdge(n2, n16);

        List sorted =
            CrossCount.radixSort(lh.getNodesAtLevel(1),
                lh.getNodesAtLevel(2),
                new IteratorOfCollections(lh.getVertical().values()));
        assertEquals(CrossCount.simpleAndEfficientCrossCount(
                lh.getNodesAtLevel(1), lh.getNodesAtLevel(2),
                new IteratorOfCollections(down.values())), 9);
    }

    /**
     *
     */
    public void testcrosscountSmall() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        Node n1 = s.newLeaf(s.getRoot());
        Node n2 = s.newLeaf(s.getRoot());
        Node n3 = s.newLeaf(s.getRoot());
        s.newEdge(n1, n3);
        Hierarchization.hierarchize(s);
        s.activateAllLHs(true);

        LocalHierarchy lh = ((SugiNode) s.getRoot()).getLocalHierarchy();
        CrossCount.radixSort(lh.getNodesAtLevel(1), lh.getNodesAtLevel(2),
            new IteratorOfCollections(lh.getVertical().values()));
        assertEquals(CrossCount.simpleAndEfficientCrossCount(
                lh.getNodesAtLevel(1), lh.getNodesAtLevel(2),
                new IteratorOfCollections(lh.getVertical().values())), 0);
    }

    /**
     */
    public void testcrosscountSmall2() {
        SugiCompoundGraph s = new SugiCompoundGraph();
        Node n1 = s.newLeaf(s.getRoot());
        Node n2 = s.newLeaf(s.getRoot());
        Node n3 = s.newLeaf(s.getRoot());
        Node n4 = s.newLeaf(s.getRoot());
        Node n5 = s.newLeaf(s.getRoot());
        Node n6 = s.newLeaf(s.getRoot());
        Node n7 = s.newLeaf(s.getRoot());
        Node n8 = s.newLeaf(s.getRoot());
        Node n9 = s.newLeaf(s.getRoot());
        Node n10 = s.newLeaf(s.getRoot());
        Node n11 = s.newLeaf(s.getRoot());
        s.newEdge(n2, n9);
        s.newEdge(n6, n11);
        s.newEdge(n3, n10);
        s.newEdge(n1, n7);
        s.newEdge(n4, n7);
        s.newEdge(n3, n11);
        s.newEdge(n5, n10);
        s.newEdge(n2, n8);
        s.newEdge(n3, n7);
        s.newEdge(n4, n9);
        s.newEdge(n6, n9);
        Hierarchization.hierarchize(s);
        s.activateAllLHs(true);

        LocalHierarchy lh = ((SugiNode) s.getRoot()).getLocalHierarchy();
        CrossCount.radixSort(lh.getNodesAtLevel(1), lh.getNodesAtLevel(2),
            new IteratorOfCollections(lh.getVertical().values()));
        //!!! funktioniert nur, weil sonst keine kanten existieren
        assertEquals(CrossCount.simpleAndEfficientCrossCount(
                lh.getNodesAtLevel(1), lh.getNodesAtLevel(2),
                new IteratorOfCollections(lh.getVertical().values())), 12);
    }
}
