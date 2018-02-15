/*==============================================================================
*
*   CrossCount.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: CrossCount.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.algorithm;

import java.util.*;

import org.visnacom.sugiyama.model.SortableEdge;
import org.visnacom.sugiyama.model.SortableNode;

/**
 * provides static method for Bilayer Cross Counting by Barth, Jünger, Mutzel
 */
public class CrossCount {
    //~ Methods ================================================================

    /**
     * sorts the edges lexicographically. the position attributes must be
     * correct. public for debug only.
     *
     * @param level_i the SortableNode-objects in the upper level
     * @param level_iP1 the SortableNode-objects in the lower level
     * @param edges all SortableEdge-objects
     *
     * @return DOCUMENT ME!
     */
    public static List radixSort(List level_i, List level_iP1, Iterator edges) {
        List[] buckets = new List[Math.max(level_iP1.size(), level_i.size())];
        for(int i = 0; i < buckets.length; i++) {
            buckets[i] = new LinkedList();
        }

        List debugList = new LinkedList();

        while(edges.hasNext()) {
            SortableEdge edge = (SortableEdge) edges.next();
            assert edge.getSortableTarget().getPosition() < buckets.length;
            buckets[edge.getSortableTarget().getPosition()].add(edge);
            assert debugList.add(edge);
        }

        List edgeList = new LinkedList();
        for(int i = 0; i < level_iP1.size(); i++) {
            edgeList.addAll(buckets[i]);
            buckets[i].clear();
        }

        if(edgeList.size() <= 1) {
            return edgeList;
        }

        for(Iterator it = edgeList.iterator(); it.hasNext();) {
            SortableEdge e = (SortableEdge) it.next();
            buckets[e.getSortableSource().getPosition()].add(e);
        }

        edgeList.clear();

        for(int i = 0; i < level_i.size(); i++) {
            edgeList.addAll(buckets[i]);
        }

        assert checkOrder(edgeList, debugList);
        return edgeList;
    }

    /**
     * taken from Barth, Jünger, Mutzel works on SortableNode and SortableEdge
     * interfaces.
     *
     * @param level_i the nodes at upper level
     * @param level_iP1 the nodes at lower level
     * @param edges an iterator over all edges.
     *
     * @return the number of crossings
     */
    public static int simpleAndEfficientCrossCount(List level_i,
        List level_iP1, Iterator edges) {
        List sortedEdges = radixSort(level_i, level_iP1, edges);
        if(sortedEdges.size() <= 1) {
            return 0;
        }

        double c = Math.ceil(Math.log(level_iP1.size()) / Math.log(2.0));
        int akkTreeSize = (int) Math.pow(2.0, c + 1) - 1;
        int numInnerNodes = (int) Math.pow(2.0, c) - 1;
        assert akkTreeSize >= level_iP1.size();

        int[] akkTree = new int[akkTreeSize];
        int crossings = 0;
        for(Iterator it = sortedEdges.iterator(); it.hasNext();) {
            SortableEdge be = (SortableEdge) it.next();
            SortableNode bn = be.getSortableTarget();
            int pos = bn.getPosition();
            pos += numInnerNodes;
            akkTree[pos]++;
            while(pos > 0) {
                if(pos % 2 != 0) { //in this implementation, the left children have odd positions
                    crossings += akkTree[pos + 1];
                }

                assert (pos - 1) / 2 == (int) Math.floor(((double) pos - 1) / 2.0);
                pos = (pos - 1) / 2;
                akkTree[pos]++;
            }
        }

        return crossings;
    }

    /**
     * only for debug in radixSort.
     *
     * @param sortedList DOCUMENT ME!
     * @param originalOrder DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static boolean checkOrder(List sortedList, List originalOrder) {
        Collections.sort(originalOrder, new BiLayerLexicographicOrder());

        assert sortedList.size() == originalOrder.size();
        assert sortedList.containsAll(originalOrder);
        assert originalOrder.containsAll(sortedList);

        Iterator it = sortedList.iterator();
        Iterator it2 = originalOrder.iterator();
        while(it.hasNext() && it2.hasNext()) {
            Object o1 = it.next();
            Object o2 = it2.next();
            assert o1.equals(o2);
        }

        return true;
    }

    //~ Inner Classes ==========================================================

    /**
     * real lexicographic order of the edges. used only for debug so far
     */
    private static class BiLayerLexicographicOrder implements Comparator {
       
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object arg0, Object arg1) {
            SortableEdge e0 = (SortableEdge) arg0;
            SortableEdge e1 = (SortableEdge) arg1;
            int diff =
                e0.getSortableSource().getPosition()
                - e1.getSortableSource().getPosition();
            if(diff != 0) {
                return diff;
            } else {
                return e0.getSortableTarget().getPosition()
                - e1.getSortableTarget().getPosition();
            }
        }
    }
}
