/*==============================================================================
*
*   RandomGraphsTest.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: RandomGraphsTest.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.test;

import org.visnacom.model.CompoundGraph;
import org.visnacom.model.Node;
import org.visnacom.sugiyama.eval.RandomGraphs;
import org.visnacom.sugiyama.eval.RandomGraphs.CreationError;
import org.visnacom.sugiyama.eval.RandomGraphs.Entry;
import org.visnacom.view.Geometry;

import junit.framework.TestCase;

/**
 *
 */
public class RandomGraphsTest extends TestCase {
    //~ Methods ================================================================

    /**
     *
     *
     * @param args DOCUMENT ME!
     */
    public static void main(String[] args) {
        junit.swingui.TestRunner.run(RandomGraphsTest.class);
    }

  
    /**
     *
     */
    public void test1() {
        Geometry geo = new Geometry();
        CompoundGraph c = geo.getView();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(c.getRoot());
        Node n5 = c.newLeaf(c.getRoot());
        Node n6 = c.newLeaf(c.getRoot());
        Node n7 = c.newLeaf(n6);
        Node n8 = c.newLeaf(n6);
        Node n9 = c.newLeaf(n6);
        Node n10 = c.newLeaf(c.getRoot());
        Node n11 = c.newLeaf(c.getRoot());
        Node n12 = c.newLeaf(c.getRoot());
        Node[] nodes =
            (Node[]) c.getAllNodes().toArray(new Node[c.getNumOfNodes()]);
        Entry[][] matrix = new Entry[nodes.length][nodes.length];

        RandomGraphs.bottom_up(c.getRoot(), c, matrix);
        RandomGraphs.top_down(c.getRoot(), c, matrix);
    }

    /**
     *
     */
    public void test2() {
        Geometry geo = new Geometry();
        CompoundGraph c = geo.getView();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(n1);
        Node n4 = c.newLeaf(n2);
        Node n5 = c.newLeaf(n3);
        Node n6 = c.newLeaf(n4);
        Node n7 = c.newLeaf(n6);
        Node[] nodes =
            (Node[]) c.getAllNodes().toArray(new Node[c.getNumOfNodes()]);
        Entry[][] matrix = new Entry[nodes.length][nodes.length];


        RandomGraphs.bottom_up(c.getRoot(), c, matrix);
        RandomGraphs.top_down(c.getRoot(), c, matrix);
    }

    /**
     *
     */
    public void testaddEdges() throws CreationError {
        Geometry geo = new Geometry();
        CompoundGraph c = geo.getView();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(c.getRoot());
        Node n4 = c.newLeaf(c.getRoot());
        Node n5 = c.newLeaf(c.getRoot());
        Node n6 = c.newLeaf(c.getRoot());
        Node n7 = c.newLeaf(n6);
        Node n8 = c.newLeaf(n6);
        Node n9 = c.newLeaf(n6);
        Node n10 = c.newLeaf(c.getRoot());
        Node n11 = c.newLeaf(c.getRoot());
        Node n12 = c.newLeaf(c.getRoot());

//        RandomGraphs.addEdges(c, 7, 2.5, 0,null);
    }

    /**
     *
     */
    public void testaddEdges2() throws CreationError {
        Geometry geo = new Geometry();
        CompoundGraph c = geo.getView();
        Node n1 = c.newLeaf(c.getRoot());
        Node n2 = c.newLeaf(c.getRoot());
        Node n3 = c.newLeaf(n1);
        Node n4 = c.newLeaf(n2);
        Node n5 = c.newLeaf(n3);
        Node n6 = c.newLeaf(n4);
        Node n7 = c.newLeaf(n6);
//        RandomGraphs.addEdges(c, 7, 3.25, 0, null);
    }

    //~ Inner Classes ==========================================================

    /**
     *
     */
    public static class DebugView {
        public Entry[][] matrix;

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String toString() {
            String result = "[";
            for(int i = 0; i < matrix.length; i++) {
                for(int j = 0; j < matrix[i].length; j++) {
                    result += (matrix[i][j]) + ",";
                }

                result += ("\n");
            }

            return result;
        }
    }
}
