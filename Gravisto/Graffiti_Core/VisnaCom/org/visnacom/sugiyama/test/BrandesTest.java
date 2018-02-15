/*==============================================================================
*
*   BrandesTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: BrandesTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import java.util.*;

import org.visnacom.model.*;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.sugiyama.algorithm.*;
import org.visnacom.sugiyama.eval.DummyPicture;
import org.visnacom.sugiyama.model.*;

import junit.framework.TestCase;

/**
 *
 */
public class BrandesTest extends TestCase {
    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(BrandesTest.class);
    }

    
    public void testemptylevel() {
        CompoundGraph s = new Static();
        Node n1 =  s.newLeaf(s.getRoot());
        Node n2 =  s.newLeaf(s.getRoot());
        Node n3 =  s.newLeaf(n1);
        Node n4 =  s.newLeaf(n2);
        Node n5 =  s.newLeaf(n2);
        Node n6 =  s.newLeaf(n1);
        Node n7 =  s.newLeaf(n1);
        Node n8 =  s.newLeaf(n1);
        Node n9 =  s.newLeaf(n1);
        s.newEdge(n4, n5);
        s.newEdge(n5, n6);
        s.newEdge(n6, n9);
        s.newEdge(n5,n7);
        s.newEdge(n7, n8);
        SugiyamaDrawingStyle sds = new SugiyamaDrawingStyle(s, SugiyamaDrawingStyle.DEBUG_STYLE);
        sds.drawImpl();
        DummyPicture.show(sds.s);
    }

    /**
     *
     */
    public final void testCreateNodesView() {
        int counter = 0;
        List nodes = new LinkedList();
        for(int i = 0; i < 10; i++) {
            List leveli = new LinkedList();
            for(int j = 0; j < 5; j++) {
                leveli.add(new SugiNode(counter++));
            }

            nodes.add(leveli);
        }


        List result = MetricLayout.createNodesView(nodes,
                MetricLayout.LEFT_MOST, MetricLayout.UP_MOST);

        //System.out.println(result);
        List result2 = MetricLayout.createNodesView(nodes,
                MetricLayout.RIGHT_MOST, MetricLayout.UP_MOST);

        //System.out.println(result2);
        //System.out.println(result);
        List result3 = MetricLayout.createNodesView(nodes,
                MetricLayout.LEFT_MOST, MetricLayout.DOWN_MOST);

        // System.out.println(result3);
        List result4 = MetricLayout.createNodesView(nodes,
                MetricLayout.RIGHT_MOST, MetricLayout.DOWN_MOST);

        //System.out.println(result4);
    }

    /**
     *
     */
    public final void testPreprocessing() {
        int counter = 0;
        List nodes = new LinkedList();
        for(int i = 0; i <= 8; i++) {
            nodes.add(new LinkedList());
        }


        HashMap up = new HashMap();

        SugiNode n0 = new SugiNode(counter++);
        SugiNode n1 = new SugiNode(counter++);
        SugiNode n2 = new SugiNode(counter++);
        SugiNode n3 = new SugiNode(counter++);
        SugiNode n101 = new DummyNode(101);
        SugiNode n4 = new SugiNode(counter++);
        SugiNode n5 = new DummyNode(counter++);
        SugiNode n6 = new DummyNode(counter++);
        SugiNode n7 = new DummyNode(counter++);
        SugiNode n102 = new DummyNode(102);
        SugiNode n8 = new SugiNode(counter++);
        SugiNode n9 = new SugiNode(counter++);
        SugiNode n10 = new SugiNode(counter++);
        SugiNode n11 = new SugiNode(counter++);
        SugiNode n12 = new DummyNode(counter++);
        SugiNode n13 = new DummyNode(counter++);
        SugiNode n14 = new SugiNode(counter++);
        SugiNode n15 = new DummyNode(counter++);
        SugiNode n16 = new SugiNode(counter++);
        SugiNode n17 = new SugiNode(counter++);
        SugiNode n18 = new SugiNode(counter++);
        SugiNode n19 = new SugiNode(counter++);
        SugiNode n20 = new SugiNode(counter++);
        SugiNode n21 = new SugiNode(counter++);
        SugiNode n22 = new DummyNode(counter++);
        SugiNode n23 = new SugiNode(counter++);
        SugiNode n24 = new SugiNode(counter++);
        SugiNode n25 = new SugiNode(counter++);
        SugiNode n26 = new SugiNode(counter++);
        SugiNode n27 = new DummyNode(counter++);
        SugiNode n28 = new SugiNode(counter++);
        SugiNode n29 = new DummyNode(counter++);
        SugiNode n30 = new SugiNode(counter++);
        SugiNode n31 = new SugiNode(counter++);
        SugiNode n103 = new DummyNode(103);

        SugiNode n32 = new SugiNode(counter++);
        SugiNode n33 = new DummyNode(counter++);
        SugiNode n34 = new SugiNode(counter++);
        SugiNode n35 = new SugiNode(counter++);
        SugiNode n104 = new DummyNode(104);

        ((List) nodes.get(1)).add(n0);
        ((List) nodes.get(1)).add(n1);
        ((List) nodes.get(1)).add(n2);
        ((List) nodes.get(1)).add(n3);
        ((List) nodes.get(1)).add(n101);
        ((List) nodes.get(2)).add(n4);
        ((List) nodes.get(2)).add(n5);
        ((List) nodes.get(2)).add(n6);
        ((List) nodes.get(2)).add(n7);
        ((List) nodes.get(2)).add(n102);
        ((List) nodes.get(2)).add(n8);
        ((List) nodes.get(2)).add(n9);
        ((List) nodes.get(2)).add(n10);
        ((List) nodes.get(3)).add(n11);
        ((List) nodes.get(3)).add(n12);
        ((List) nodes.get(3)).add(n13);
        ((List) nodes.get(3)).add(n14);
        ((List) nodes.get(3)).add(n15);
        ((List) nodes.get(3)).add(n16);
        ((List) nodes.get(3)).add(n17);
        ((List) nodes.get(4)).add(n18);
        ((List) nodes.get(4)).add(n19);
        ((List) nodes.get(4)).add(n20);
        ((List) nodes.get(5)).add(n21);
        ((List) nodes.get(6)).add(n22);
        ((List) nodes.get(6)).add(n23);
        ((List) nodes.get(6)).add(n24);
        ((List) nodes.get(6)).add(n25);
        ((List) nodes.get(6)).add(n26);
        ((List) nodes.get(6)).add(n27);
        ((List) nodes.get(6)).add(n28);
        ((List) nodes.get(7)).add(n29);
        ((List) nodes.get(7)).add(n30);
        ((List) nodes.get(7)).add(n31);
        ((List) nodes.get(7)).add(n103);
        ((List) nodes.get(7)).add(n32);
        ((List) nodes.get(7)).add(n33);
        ((List) nodes.get(8)).add(n34);
        ((List) nodes.get(8)).add(n35);
        ((List) nodes.get(8)).add(n104);
        for(Iterator it = new IteratorOfCollections(nodes); it.hasNext();) {
            SugiNode n = (SugiNode) it.next();
            up.put(n, new LinkedList());
        }

        ((List) up.get(n4)).add(new LHEdge(n0, n4));
        ((List) up.get(n5)).add(new LHEdge(n1, n5));
        ((List) up.get(n6)).add(new LHEdge(n2, n6));
        ((List) up.get(n7)).add(new LHEdge(n3, n7));
        ((List) up.get(n9)).add(new LHEdge(n3, n9));
        ((List) up.get(n102)).add(new LHEdge(n101, n102));
        ((List) up.get(n11)).add(new LHEdge(n4, n11));
        ((List) up.get(n12)).add(new LHEdge(n6, n12));
        ((List) up.get(n13)).add(new LHEdge(n5, n13));
        ((List) up.get(n14)).add(new LHEdge(n8, n14));
        ((List) up.get(n14)).add(new LHEdge(n102, n14));
        ((List) up.get(n15)).add(new LHEdge(n7, n15));
        ((List) up.get(n16)).add(new LHEdge(n10, n16));
        ((List) up.get(n17)).add(new LHEdge(n9, n17));
        ((List) up.get(n18)).add(new LHEdge(n12, n18));
        ((List) up.get(n19)).add(new LHEdge(n13, n19));
        ((List) up.get(n20)).add(new LHEdge(n15, n20));
        ((List) up.get(n22)).add(new LHEdge(n21, n22));
        ((List) up.get(n27)).add(new LHEdge(n21, n27));
        ((List) up.get(n29)).add(new LHEdge(n22, n29));
        ((List) up.get(n103)).add(new LHEdge(n24, n103));
        ((List) up.get(n32)).add(new LHEdge(n25, n32));
        ((List) up.get(n32)).add(new LHEdge(n28, n32));
        ((List) up.get(n33)).add(new LHEdge(n27, n33));
        ((List) up.get(n34)).add(new LHEdge(n29, n34));
        ((List) up.get(n35)).add(new LHEdge(n33, n35));
        ((List) up.get(n104)).add(new LHEdge(n103, n104));


        List anextView = MetricLayout.createNodesView(nodes,
                MetricLayout.LEFT_MOST, MetricLayout.UP_MOST);
        MetricLayout.preprocessing(anextView, up);

      
    }

    /**
     *
     */
    public void test1() {
        System.out.print(getClass());
        System.out.println(getName());


        SugiCompoundGraph s = new SugiCompoundGraph();
        SugiNode[] node = new SugiNode[28];
        for(int i = 0; i < node.length; i++) {
            node[i] = (SugiNode) s.newLeaf(s.getRoot());
        }


        CompoundLevel rootclev = CompoundLevel.getClevForRoot();
        ((SugiNode) s.getRoot()).setClev(rootclev);
        node[0].setClev(rootclev.getSubLevel(1));
        node[1].setClev(rootclev.getSubLevel(1));
        node[2].setClev(rootclev.getSubLevel(1));
        node[3].setClev(rootclev.getSubLevel(1));
        node[4].setClev(rootclev.getSubLevel(2));
        node[5].setClev(rootclev.getSubLevel(2));
        node[6].setClev(rootclev.getSubLevel(2));
        node[7].setClev(rootclev.getSubLevel(2));
        node[8].setClev(rootclev.getSubLevel(3));
        node[9].setClev(rootclev.getSubLevel(3));
        node[10].setClev(rootclev.getSubLevel(3));
        node[11].setClev(rootclev.getSubLevel(3));
        node[12].setClev(rootclev.getSubLevel(4));
        node[13].setClev(rootclev.getSubLevel(4));
        node[14].setClev(rootclev.getSubLevel(4));
        node[15].setClev(rootclev.getSubLevel(5));
        node[16].setClev(rootclev.getSubLevel(6));
        node[17].setClev(rootclev.getSubLevel(6));
        node[18].setClev(rootclev.getSubLevel(6));
        node[19].setClev(rootclev.getSubLevel(6));
        node[20].setClev(rootclev.getSubLevel(6));
        node[21].setClev(rootclev.getSubLevel(6));
        node[22].setClev(rootclev.getSubLevel(7));
        node[23].setClev(rootclev.getSubLevel(7));
        node[24].setClev(rootclev.getSubLevel(7));
        node[25].setClev(rootclev.getSubLevel(8));
        node[26].setClev(rootclev.getSubLevel(8));
        node[27].setClev(rootclev.getSubLevel(8));

        s.newEdge(node[0], node[4]);
        s.newEdge(node[0], node[4]);
        s.newEdge(node[1], node[13]);
        s.newEdge(node[2], node[12]);
        s.newEdge(node[3], node[5]);
        s.newEdge(node[3], node[6]);
        s.newEdge(node[3], node[14]);
        s.newEdge(node[3], node[7]);
        s.newEdge(node[4], node[8]);
        s.newEdge(node[5], node[9]);
        s.newEdge(node[6], node[11]);
        s.newEdge(node[7], node[10]);
        s.newEdge(node[8], node[12]);
        s.newEdge(node[9], node[12]);
        s.newEdge(node[13], node[15]);
        s.newEdge(node[15], node[18]);
        s.newEdge(node[15], node[19]);
        s.newEdge(node[15], node[20]);
        s.newEdge(node[15], node[21]);
        s.newEdge(node[15], node[26]);
        s.newEdge(node[15], node[27]);
        s.newEdge(node[15], node[27]);
        s.newEdge(node[16], node[22]);
        s.newEdge(node[16], node[23]);
        s.newEdge(node[17], node[23]);
        s.newEdge(node[18], node[24]);
        s.newEdge(node[18], node[26]);
        s.newEdge(node[19], node[24]);
        s.newEdge(node[20], node[24]);
        s.newEdge(node[21], node[24]);
        Normalization.normalize(s);

        VertexOrdering.order(s, 0);


        LocalHierarchy lh = ((SugiNode) s.getRoot()).getLocalHierarchy();
        List level2 = lh.getNodesAtLevel(2);
        List level3 = lh.getNodesAtLevel(3);
        List level6 = lh.getNodesAtLevel(6);
        List level7 = lh.getNodesAtLevel(7);
        level2.add(1, level2.remove(4));
        level2.add(2, level2.remove(5));
        level2.add(3, level2.remove(6));
        level3.add(1, level3.remove(4));
        level3.add(1, level3.remove(5));
        level3.add(4, level3.remove(6));

        level6.add(2, level6.remove(6));
        level6.add(3, level6.remove(7));
        level6.add(4, level6.remove(8));
        level7.add(2, level7.remove(3));
        level7.add(3, level7.remove(4));
        level7.add(4, level7.remove(5));
        level7.add(3, level7.remove(6));
        lh.updatePositions();
        MetricLayout.layout(s, 4);
        DummyPicture.show(s);
//                DummyPicture.write(s,"test1bildfertig.jpg");
        for(int i = 0; i <= 3; i++) {
            MetricLayout.layout(s, i);
            DummyPicture.show(s);
//                    DummyPicture.write(s,"test1bild" +i+".jpg");
        }

        //situation, as it is the result of a rightmost-lower Assignment
        assertSame(node[0].getHLayoutAlign(), node[8]);
        assertSame(node[3].getHLayoutAlign(), node[6]);
        assertSame(node[4].getHLayoutAlign(), node[0]);
        assertSame(node[5].getHLayoutAlign(), node[5]);
        assertSame(node[6].getHLayoutAlign(), node[3]);
        assertSame(node[7].getHLayoutAlign(), node[10]);
        assertSame(node[8].getHLayoutAlign(), node[4]);
        assertSame(node[9].getHLayoutAlign(), node[12]);
        assertSame(node[10].getHLayoutAlign(), node[7]);
        assertSame(node[11].getHLayoutAlign(), node[11]);
        assertSame(node[12].getHLayoutAlign(), node[9]);
        assertSame(node[13].getHLayoutAlign(), node[18]);
        assertSame(node[15].getHLayoutAlign(), node[13]);
        assertSame(node[16].getHLayoutAlign(), node[22]);
        assertSame(node[17].getHLayoutAlign(), node[23]);
        assertSame(node[18].getHLayoutAlign(), node[15]);
        assertSame(node[19].getHLayoutAlign(), node[19]);
        assertSame(node[20].getHLayoutAlign(), node[20]);
        assertSame(node[21].getHLayoutAlign(), node[24]);
        assertSame(node[22].getHLayoutAlign(), node[16]);
        assertSame(node[23].getHLayoutAlign(), node[17]);
        assertSame(node[24].getHLayoutAlign(), node[21]);
        assertSame(node[25].getHLayoutAlign(), node[25]);
        
        assertSame(node[0].getHLayoutRoot(), node[8]);
        assertSame(node[0].getHLayoutRoot(), node[8]); 
        assertSame(node[3].getHLayoutRoot(), node[6]); 
        assertSame(node[4].getHLayoutRoot(), node[8]); 
        assertSame(node[5].getHLayoutRoot(), node[5]); 
        assertSame(node[6].getHLayoutRoot(), node[6]); 
        assertSame(node[7].getHLayoutRoot(), node[10]); 
        assertSame(node[8].getHLayoutRoot(), node[8]); 
        assertSame(node[9].getHLayoutRoot(), node[12]); 
        assertSame(node[10].getHLayoutRoot(), node[10]); 
        assertSame(node[11].getHLayoutRoot(), node[11]); 
        assertSame(node[12].getHLayoutRoot(), node[12]); 
        assertSame(node[13].getHLayoutRoot(), node[18]); 
        assertSame(node[14].getHLayoutRoot(), node[14]); 
        assertSame(node[15].getHLayoutRoot(), node[18]); 
        assertSame(node[16].getHLayoutRoot(), node[22]); 
        assertSame(node[17].getHLayoutRoot(), node[23]); 
        assertSame(node[18].getHLayoutRoot(), node[18]); 
        assertSame(node[19].getHLayoutRoot(), node[19]); 
        assertSame(node[20].getHLayoutRoot(), node[20]); 
        assertSame(node[21].getHLayoutRoot(), node[24]); 
        assertSame(node[22].getHLayoutRoot(), node[22]); 
        assertSame(node[23].getHLayoutRoot(), node[23]); 
        assertSame(node[24].getHLayoutRoot(), node[24]); 
        assertSame(node[25].getHLayoutRoot(), node[25]); 
        assertSame(node[26].getHLayoutRoot(), node[26]); 
        assertSame(node[27].getHLayoutRoot(), node[27]);
        assertSame(node[0].getHLayoutSink(), node[0]); 
        assertSame(node[1].getHLayoutSink(), node[1]); 
        assertSame(node[2].getHLayoutSink(), node[2]); 
        assertSame(node[3].getHLayoutSink(), node[3]); 
        assertSame(node[4].getHLayoutSink(), node[4]); 
        assertSame(node[5].getHLayoutSink(), node[11]); 
        assertSame(node[6].getHLayoutSink(), node[11]); 
        assertSame(node[7].getHLayoutSink(), node[7]); 
        assertSame(node[8].getHLayoutSink(), node[24]); 
        assertSame(node[9].getHLayoutSink(), node[9]); 
        assertSame(node[10].getHLayoutSink(), node[11]); 
        assertSame(node[11].getHLayoutSink(), node[11]); 
        assertSame(node[12].getHLayoutSink(), node[24]); 
        assertSame(node[13].getHLayoutSink(), node[13]); 
        assertSame(node[14].getHLayoutSink(), node[11]); 
        assertSame(node[15].getHLayoutSink(), node[15]); 
        assertSame(node[16].getHLayoutSink(), node[16]); 
        assertSame(node[17].getHLayoutSink(), node[17]); 
        assertSame(node[18].getHLayoutSink(), node[24]); 
        assertSame(node[19].getHLayoutSink(), node[24]); 
        assertSame(node[20].getHLayoutSink(), node[24]); 
        assertSame(node[21].getHLayoutSink(), node[21]); 
        assertSame(node[22].getHLayoutSink(), node[24]); 
        assertSame(node[23].getHLayoutSink(), node[24]); 
        assertSame(node[24].getHLayoutSink(), node[24]); 
        assertSame(node[25].getHLayoutSink(), node[24]); 
        assertSame(node[26].getHLayoutSink(), node[24]); 
        assertSame(node[27].getHLayoutSink(), node[24]);
        System.out.print("");
    }

    /**
     *
     */
    public void testInitializeUp() {
        int counter = 0;
        SugiNode n0 = new SugiNode(counter++);
        SugiNode n1 = new SugiNode(counter++);
        SugiNode n2 = new SugiNode(counter++);
        SugiNode n3 = new SugiNode(counter++);
        SugiNode n4 = new SugiNode(counter++);
        SugiNode n5 = new DummyNode(counter++);
        SugiNode n6 = new DummyNode(counter++);
        SugiNode n7 = new DummyNode(counter++);
        SugiNode n8 = new SugiNode(counter++);
        SugiNode n9 = new SugiNode(counter++);
        SugiNode n10 = new SugiNode(counter++);
        SugiNode n11 = new SugiNode(counter++);
        SugiNode n12 = new DummyNode(counter++);
        SugiNode n13 = new DummyNode(counter++);
        SugiNode n14 = new SugiNode(counter++);
        SugiNode n15 = new DummyNode(counter++);
        SugiNode n16 = new SugiNode(counter++);
        SugiNode n17 = new SugiNode(counter++);
        SugiNode n18 = new SugiNode(counter++);
        SugiNode n19 = new SugiNode(counter++);
        SugiNode n20 = new SugiNode(counter++);
        SugiNode n21 = new SugiNode(counter++);
        SugiNode n22 = new DummyNode(counter++);
        SugiNode n23 = new DummyNode(counter++);
        SugiNode n24 = new SugiNode(counter++);
        SugiNode n25 = new SugiNode(counter++);
        SugiNode n26 = new SugiNode(counter++);
        SugiNode n27 = new DummyNode(counter++);
        SugiNode n28 = new SugiNode(counter++);
        SugiNode n29 = new DummyNode(counter++);
        SugiNode n30 = new DummyNode(counter++);
        SugiNode n31 = new DummyNode(counter++);
        SugiNode n32 = new SugiNode(counter++);
        SugiNode n33 = new DummyNode(counter++);
        SugiNode n34 = new SugiNode(counter++);
        SugiNode n35 = new SugiNode(counter++);
        LocalHierarchy lh = new LocalHierarchy();
        CompoundLevel rootclev = CompoundLevel.getClevForRoot();
        n0.setClev(rootclev.getSubLevel(1));
        n1.setClev(rootclev.getSubLevel(1));
        n2.setClev(rootclev.getSubLevel(1));
        n3.setClev(rootclev.getSubLevel(1));
        n4.setClev(rootclev.getSubLevel(2));
        n5.setClev(rootclev.getSubLevel(2));
        n6.setClev(rootclev.getSubLevel(2));
        n7.setClev(rootclev.getSubLevel(2));
        n8.setClev(rootclev.getSubLevel(2));
        n9.setClev(rootclev.getSubLevel(2));
        n10.setClev(rootclev.getSubLevel(2));
        n11.setClev(rootclev.getSubLevel(3));
        n12.setClev(rootclev.getSubLevel(3));
        n13.setClev(rootclev.getSubLevel(3));
        n14.setClev(rootclev.getSubLevel(3));
        n15.setClev(rootclev.getSubLevel(3));
        n16.setClev(rootclev.getSubLevel(3));
        n17.setClev(rootclev.getSubLevel(3));
        n18.setClev(rootclev.getSubLevel(4));
        n19.setClev(rootclev.getSubLevel(4));
        n20.setClev(rootclev.getSubLevel(4));
        n21.setClev(rootclev.getSubLevel(5));
        n22.setClev(rootclev.getSubLevel(6));
        n23.setClev(rootclev.getSubLevel(6));
        n24.setClev(rootclev.getSubLevel(6));
        n25.setClev(rootclev.getSubLevel(6));
        n26.setClev(rootclev.getSubLevel(6));
        n27.setClev(rootclev.getSubLevel(6));
        n28.setClev(rootclev.getSubLevel(6));
        n29.setClev(rootclev.getSubLevel(7));
        n30.setClev(rootclev.getSubLevel(7));
        n31.setClev(rootclev.getSubLevel(7));
        n32.setClev(rootclev.getSubLevel(7));
        n33.setClev(rootclev.getSubLevel(7));
        n34.setClev(rootclev.getSubLevel(8));
        n35.setClev(rootclev.getSubLevel(8));
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
        lh.addNode(n30);
        lh.addNode(n31);
        lh.addNode(n32);
        lh.addNode(n33);
        lh.addNode(n34);
        lh.addNode(n35);


        LHEdge e1 = lh.ensureEdge(n0, n4);
        LHEdge e2 = lh.ensureEdge(n1, n5);
        LHEdge e3 = lh.ensureEdge(n2, n6);
        LHEdge e4 = lh.ensureEdge(n3, n7);
        LHEdge e5 = lh.ensureEdge(n3, n10);
        LHEdge e6 = lh.ensureEdge(n3, n8);
        LHEdge e7 = lh.ensureEdge(n3, n9);
        LHEdge e8 = lh.ensureEdge(n4, n11);
        LHEdge e9 = lh.ensureEdge(n6, n12);
        LHEdge e10 = lh.ensureEdge(n5, n13);
        LHEdge e11 = lh.ensureEdge(n8, n14);
        LHEdge e12 = lh.ensureEdge(n7, n15);
        LHEdge e13 = lh.ensureEdge(n10, n16);
        LHEdge e14 = lh.ensureEdge(n9, n17);
        LHEdge e15 = lh.ensureEdge(n14, n18);
        LHEdge e16 = lh.ensureEdge(n11, n18);
        LHEdge e17 = lh.ensureEdge(n12, n18);
        LHEdge e18 = lh.ensureEdge(n13, n19);
        LHEdge e19 = lh.ensureEdge(n14, n20);
        LHEdge e20 = lh.ensureEdge(n14, n19);
        LHEdge e21 = lh.ensureEdge(n15, n20);
        LHEdge e22 = lh.ensureEdge(n19, n21);
        LHEdge e23 = lh.ensureEdge(n21, n25);
        LHEdge e24 = lh.ensureEdge(n21, n22);
        LHEdge e25 = lh.ensureEdge(n21, n23);
        LHEdge e26 = lh.ensureEdge(n21, n24);
        LHEdge e27 = lh.ensureEdge(n21, n26);
        LHEdge e28 = lh.ensureEdge(n21, n28);
        LHEdge e29 = lh.ensureEdge(n21, n27);
        LHEdge e30 = lh.ensureEdge(n22, n29);
        LHEdge e31 = lh.ensureEdge(n23, n31);
        LHEdge e32 = lh.ensureEdge(n24, n30);
        LHEdge e33 = lh.ensureEdge(n25, n32);
        LHEdge e34 = lh.ensureEdge(n24, n32);
        LHEdge e35 = lh.ensureEdge(n26, n32);
        LHEdge e36 = lh.ensureEdge(n28, n32);
        LHEdge e37 = lh.ensureEdge(n27, n33);
        LHEdge e38 = lh.ensureEdge(n29, n34);
        LHEdge e39 = lh.ensureEdge(n30, n34);
        LHEdge e40 = lh.ensureEdge(n33, n35);
        LHEdge e41 = lh.ensureEdge(n31, n35);

        //System.out.println(lh);
        HashMap up = new LinkedHashMap();
        MetricLayout.prepareAdjLists(lh, new HashMap(), up);

        assertTrue(((List) up.get(n4)).contains(e1));
        assertTrue(((List) up.get(n5)).contains(e2));
        assertTrue(((List) up.get(n6)).contains(e3));
        assertTrue(((List) up.get(n7)).contains(e4));
        assertTrue(((List) up.get(n10)).contains(e5));
        assertTrue(((List) up.get(n8)).contains(e6));
        assertTrue(((List) up.get(n9)).contains(e7));
        assertTrue(((List) up.get(n11)).contains(e8));
        assertTrue(((List) up.get(n12)).contains(e9));
        assertTrue(((List) up.get(n13)).contains(e10));
        assertTrue(((List) up.get(n14)).contains(e11));
        assertTrue(((List) up.get(n15)).contains(e12));
        assertTrue(((List) up.get(n16)).contains(e13));
        assertTrue(((List) up.get(n17)).contains(e14));
        assertTrue(((List) up.get(n18)).contains(e15));
        assertTrue(((List) up.get(n18)).contains(e16));
        assertTrue(((List) up.get(n18)).contains(e17));
        assertTrue(((List) up.get(n19)).contains(e18));
        assertTrue(((List) up.get(n20)).contains(e19));
        assertTrue(((List) up.get(n19)).contains(e20));
        assertTrue(((List) up.get(n20)).contains(e21));
        assertTrue(((List) up.get(n21)).contains(e22));
        assertTrue(((List) up.get(n25)).contains(e23));
        assertTrue(((List) up.get(n22)).contains(e24));
        assertTrue(((List) up.get(n23)).contains(e25));
        assertTrue(((List) up.get(n24)).contains(e26));
        assertTrue(((List) up.get(n26)).contains(e27));
        assertTrue(((List) up.get(n28)).contains(e28));
        assertTrue(((List) up.get(n27)).contains(e29));
        assertTrue(((List) up.get(n29)).contains(e30));
        assertTrue(((List) up.get(n31)).contains(e31));
        assertTrue(((List) up.get(n30)).contains(e32));
        assertTrue(((List) up.get(n32)).contains(e33));
        assertTrue(((List) up.get(n32)).contains(e34));
        assertTrue(((List) up.get(n32)).contains(e35));
        assertTrue(((List) up.get(n32)).contains(e36));
        assertTrue(((List) up.get(n33)).contains(e37));
        assertTrue(((List) up.get(n34)).contains(e38));
        assertTrue(((List) up.get(n34)).contains(e39));
        assertTrue(((List) up.get(n35)).contains(e40));
        assertTrue(((List) up.get(n35)).contains(e41));


        List nextView = MetricLayout.createNodesView(lh.getNodes(),
                MetricLayout.LEFT_MOST, MetricLayout.UP_MOST);
        MetricLayout.preprocessing(nextView, up);

        /* expected
           found type2 conflict(6,12) and (5,13)
           found type1 conflict, noninnersegment=(8,14)
           found type1 conflict, noninnersegment=(24,30)
           found type1 conflict, noninnersegment=(28,32)*/
        MetricLayout.verticalAlignment(nextView, up);
    }

    /**
     *
     */
    public void testsmall1() {
        System.out.print(getClass());
        System.out.println(getName());


        SugiCompoundGraph s = new SugiCompoundGraph();
        SugiNode[] node = new SugiNode[13];
        for(int i = 0; i < node.length; i++) {
            node[i] = (SugiNode) s.newLeaf(s.getRoot());
        }


        CompoundLevel rootclev = CompoundLevel.getClevForRoot();
        ((SugiNode) s.getRoot()).setClev(rootclev);
        node[0].setClev(rootclev.getSubLevel(1));
        node[1].setClev(rootclev.getSubLevel(1));
        node[2].setClev(rootclev.getSubLevel(2));
        node[3].setClev(rootclev.getSubLevel(2));
        node[4].setClev(rootclev.getSubLevel(2));
        node[5].setClev(rootclev.getSubLevel(3));
        node[6].setClev(rootclev.getSubLevel(3));
        node[7].setClev(rootclev.getSubLevel(3));
        node[8].setClev(rootclev.getSubLevel(3));
        node[9].setClev(rootclev.getSubLevel(4));
        node[10].setClev(rootclev.getSubLevel(4));
        node[11].setClev(rootclev.getSubLevel(4));
        node[12].setClev(rootclev.getSubLevel(4));

        s.newEdge(node[1], node[4]);
        s.newEdge(node[3], node[6]);
        s.newEdge(node[4], node[7]);
        s.newEdge(node[6], node[9]);
        s.newEdge(node[8], node[12]);

        // Normalization.normalize(s);
        VertexOrdering.order(s, 0);
        MetricLayout.layout(s, 4);
        DummyPicture.show(s);
        //        for(int i = 0; i <= 3; i++) {
        //            MetricLayout.layout(s, i);
        //        DummyPicture.show(s);
        //        //DummyPicture.write(s,"bugbild" +i+".jpg");
        //        }
        System.out.print("");
    }

    /**
     *
     */
    public void testsmall2() {
        System.out.print(getClass());
        System.out.println(getName());


        SugiCompoundGraph s = new SugiCompoundGraph();
        SugiNode[] node = new SugiNode[19];
        for(int i = 0; i < node.length; i++) {
            node[i] = (SugiNode) s.newLeaf(s.getRoot());
        }


        CompoundLevel rootclev = CompoundLevel.getClevForRoot();
        ((SugiNode) s.getRoot()).setClev(rootclev);
        node[0].setClev(rootclev.getSubLevel(1));
        node[1].setClev(rootclev.getSubLevel(1));
        node[2].setClev(rootclev.getSubLevel(2));
        node[3].setClev(rootclev.getSubLevel(2));
        node[4].setClev(rootclev.getSubLevel(2));
        node[5].setClev(rootclev.getSubLevel(2));
        node[6].setClev(rootclev.getSubLevel(3));
        node[7].setClev(rootclev.getSubLevel(3));
        node[8].setClev(rootclev.getSubLevel(3));
        node[9].setClev(rootclev.getSubLevel(3));
        node[10].setClev(rootclev.getSubLevel(4));
        node[11].setClev(rootclev.getSubLevel(4));
        node[12].setClev(rootclev.getSubLevel(4));
        node[13].setClev(rootclev.getSubLevel(4));
        node[14].setClev(rootclev.getSubLevel(4));
        node[15].setClev(rootclev.getSubLevel(4));
        node[16].setClev(rootclev.getSubLevel(4));
        node[17].setClev(rootclev.getSubLevel(5));
        node[18].setClev(rootclev.getSubLevel(5));

        s.newEdge(node[1], node[4]);
        s.newEdge(node[3], node[8]);
        s.newEdge(node[5], node[9]);
        s.newEdge(node[7], node[11]);
        s.newEdge(node[8], node[13]);
        s.newEdge(node[9], node[16]);
        s.newEdge(node[10], node[17]);
        s.newEdge(node[16], node[18]);

        VertexOrdering.order(s, 0);
        MetricLayout.layout(s, 4);
        DummyPicture.show(s);
        //    DummyPicture.write(s,"bug_bild_2_full.jpg");
        for(int i = 0; i <= 3; i++) {
            MetricLayout.layout(s, i);
            DummyPicture.show(s);
            //    DummyPicture.write(s,"bug_bild_2_" +i+".jpg");
        }

        System.out.print("");
    }
    
    public void testHeterogenWidth1() {
        System.out.print(getClass());
        System.out.println(getName());


        SugiCompoundGraph s = new SugiCompoundGraph();
        SugiNode[] node = new SugiNode[100];
        for(int i = 0; i < 14; i++) {
            node[i] = (SugiNode) s.newLeaf(s.getRoot());
        }
        node[14] = (SugiNode) s.newLeaf(node[0]);
        node[15] = (SugiNode) s.newLeaf(node[3]);
        node[16] = (SugiNode) s.newLeaf(node[3]);
        node[17] = (SugiNode) s.newLeaf(node[3]);
        node[18] = (SugiNode) s.newLeaf(node[11]);
        node[19] = (SugiNode) s.newLeaf(node[11]);
        
        CompoundLevel rootclev = CompoundLevel.getClevForRoot();
        
        ((SugiNode) s.getRoot()).setClev(rootclev);
        node[0].setClev(rootclev.getSubLevel(1));
        node[1].setClev(rootclev.getSubLevel(1));
        node[2].setClev(rootclev.getSubLevel(2));
        node[3].setClev(rootclev.getSubLevel(2));
        node[4].setClev(rootclev.getSubLevel(2));
        node[5].setClev(rootclev.getSubLevel(3));
        node[6].setClev(rootclev.getSubLevel(3));
        node[7].setClev(rootclev.getSubLevel(3));
        node[8].setClev(rootclev.getSubLevel(3));
        node[9].setClev(rootclev.getSubLevel(4));
        node[10].setClev(rootclev.getSubLevel(4));
        node[11].setClev(rootclev.getSubLevel(4));
        node[12].setClev(rootclev.getSubLevel(4));
        node[13].setClev(rootclev.getSubLevel(4));
        node[14].setClev(node[0].getClev().getSubLevel(1));
        node[15].setClev(node[3].getClev().getSubLevel(1));
        node[16].setClev(node[3].getClev().getSubLevel(1));
        node[17].setClev(node[3].getClev().getSubLevel(2));
        node[18].setClev(node[11].getClev().getSubLevel(1));
        node[19].setClev(node[11].getClev().getSubLevel(1));

        s.newEdge(node[1], node[4]);
        s.newEdge(node[3], node[6]);
        s.newEdge(node[4], node[7]);
        s.newEdge(node[6], node[9]);
        s.newEdge(node[8], node[12]);
        s.newEdge(node[7], node[10]);
        s.newEdge(node[7], node[11]);

        // Normalization.normalize(s);
        VertexOrdering.order(s, 0);
        MetricLayout.layout(s, 4);
        DummyPicture.show(s);
//                for(int i = 0; i <= 3; i++) {
//                    MetricLayout.layout(s, i);
//                DummyPicture.show(s);
//                DummyPicture.write(s,"bug_3_bild" +i+".jpg");
//                }
        //System.out.print("");
    }
}
