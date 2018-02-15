/*==============================================================================
*
*   MetricLayout.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: MetricLayout.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.algorithm;

import java.util.*;
import java.util.Map.Entry;

import org.visnacom.controller.Preferences;
import org.visnacom.model.DLL;
import org.visnacom.model.Node;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;
import org.visnacom.sugiyama.model.*;


/**
 * provides static methods for metric layout of static layout.
 */
public class MetricLayout {
    //~ Static fields/initializers =============================================

    /* the parameters used for layout; values are filled in method "setParameters"*/
    private static int upperGap; // used, if there is no level 0
    private static int gapAboveLev0;
    private static int gapLev0_Lev1; //gap between lev0 and lev1
    private static int verticalGap; //usual gap between levels
    private static int lowerGap; // used, if there is no level n + 1
    private static int gap_Lev_n_Lev_nP1;
    private static int gapBelowLev_nP1;
    private static int basicHeight;
    public static int basicWidth;
    private static int leftGap;
    private static int horizontalGap;
    private static int horizontalGapBetweenDummies;
    private static int rightGap;
    private static int basicHeightDummy;
    private static int basicWidthDummy;
    private static int leftAndRightGapInDummies;

    /* the four orientations of horizontal metric layout*/
    public static final int UP_MOST = 0;
    public static final int DOWN_MOST = 1;
    public static final int LEFT_MOST = 2;
    public static final int RIGHT_MOST = 3;

    //~ Methods ================================================================

    /**
     * creates a clone of the given nodes list. uses ArrayList. sets the
     * position-values and the Pred-pointer to meet the returned view
     *
     * @param nodes a list of list containing the nodes
     * @param hdirection the wished horizontal direction,
     * @param vdirection the wished vertical direction
     *
     * @return DOCUMENT ME!
     */
    public static List createNodesView(Collection nodes, int hdirection,
        int vdirection) {
        ArrayList nodesView = new ArrayList(nodes.size());
        for(Iterator it = nodes.iterator(); it.hasNext();) {
            List leveli = (List) it.next();
            List clone = new ArrayList(leveli);
            if(hdirection == RIGHT_MOST) {
                Collections.reverse(clone);
            } else {
                assert hdirection == LEFT_MOST;
            }

            nodesView.add(clone);
        }

        if(vdirection == DOWN_MOST) {
            Collections.reverse(nodesView);
        } else {
            assert vdirection == UP_MOST;
        }

        //falls leftmost
        if(hdirection == LEFT_MOST) {
            for(Iterator it = nodes.iterator(); it.hasNext();) {
                List leveli = (List) it.next();
                int i = 0;
                SugiNode niM1 = null;
                for(Iterator it2 = leveli.iterator(); it2.hasNext(); i++) {
                    SugiNode ni = (SugiNode) it2.next();
                    ni.setHLayoutPred(niM1);
                    ni.setPosition(i);
                    niM1 = ni;
                }
            }
        } else {
            assert hdirection == RIGHT_MOST;
            for(Iterator it = nodes.iterator(); it.hasNext();) {
                List leveli = (List) it.next();
                int i = 0;
                SugiNode niM1 = null;
                for(Iterator it2 = leveli.iterator(); it2.hasNext(); i++) {
                    SugiNode ni = (SugiNode) it2.next();
                    if(i > 0) {
                        niM1.setHLayoutPred(ni);
                    }

                    ni.setPosition(leveli.size() - 1 - i);
                    niM1 = ni;
                }

                //passt schon so
                if(niM1 != null) {
                    niM1.setHLayoutPred(null);
                }
            }
        }

        return nodesView;
    }

    /**
     * works for all four directions, if 'nodes' and the Pred attribute is
     * correct
     *
     * @param nodes DOCUMENT ME!
     * @param i DOCUMENT ME!
     */
    public static void horizontalCompaction(List nodes, int i) {
        for(Iterator it = new IteratorOfCollections(nodes); it.hasNext();) {
            SugiNode next = (SugiNode) it.next();
            next.initializeSinkAndShift();
        }

        HashMap xvalues = new LinkedHashMap();
        for(Iterator it = new IteratorOfCollections(nodes); it.hasNext();) {
            SugiNode next = (SugiNode) it.next();
            if(next.getHLayoutRoot() == next) {
                place_block(next, xvalues);
            }
        }

        //compute absolute shift values
        for(Iterator it = nodes.iterator(); it.hasNext();) {
            List level = (List) it.next();
            if(!level.isEmpty()) {
                //only nodes at position 0 might be sinks
                SugiNode next = (SugiNode) level.get(0);

                //is really sink and has parent-sink?
                if(next.getHLayoutSink() == next
                    && next.getHLayoutParentSink() != next) {
                    int parentShift =
                        next.getHLayoutParentSink().getHLayoutshift();
                    if(parentShift < Integer.MAX_VALUE) {
                        //increment shift by the shift of the parent
                        next.setHLayoutshift(next.getHLayoutshift()
                            + next.getHLayoutParentSink().getHLayoutshift());
                    }
                }
            }
        }

        for(Iterator it = new IteratorOfCollections(nodes); it.hasNext();) {
            SugiNode next = (SugiNode) it.next();
            Integer rootValue = (Integer) xvalues.get(next.getHLayoutRoot());

            next.localXs[i] = rootValue.intValue();

            int shift =
                next.getHLayoutRoot().getHLayoutSink().getHLayoutshift();

            if(shift < Integer.MAX_VALUE) {
                //              absoluteX.put(next, new Integer(rootValue.intValue() + shift));
                next.localXs[i] += shift;
            }
        }

        //return //;
    }

    /**
     * the main method of this class
     *
     * @param s DOCUMENT ME!
     */
    public static void layout(SugiCompoundGraph s) {
        layout(s, 4);
    }

    /**
     *
     * @param s
     * @param dir if dir==4, full layout. if 0: leftmost upper. 1:rightmost
     *        upper 2: leftmost lower. 3: rightmost lower.
     */
    public static void layout(SugiCompoundGraph s, int dir) {
        assert dir <= 4 && dir >= 0;
        setParameters(s.getDrawingStyle(), s.getPreferences());
        verticalLayout(s);

        for(Iterator it = s.getAllNodesIterator(); it.hasNext();) {
            ((SugiNode) it.next()).setHLayoutValid(false);
        }

        horizontalLayout(s, dir);
    }

    /**
     * the old drawing method. only for debug. the graph need not to
     * be normalized.
     *
     * @param s DOCUMENT ME!
     */
    public static void layoutNaive(SugiCompoundGraph s) {
        setParameters(SugiyamaDrawingStyle.DEBUG_STYLE, null);
        verticalLayout(s);
        horizontalLayoutNaive(s);
    }

    /**
     * bottom up traversal sets the localX-values of the children of v and the
     * width-value of v.
     *
     * @param s DOCUMENT ME!
     * @param v DOCUMENT ME!
     * @param dir DOCUMENT ME!
     */
    public static void localHLayout(SugiCompoundGraph s, SugiNode v, int dir) {
        for(Iterator it = s.getChildrenIterator(v); it.hasNext();) {
            SugiNode next = (SugiNode) it.next();

            localHLayout(s, next, dir);
        }

        if(!v.isHLayoutValid()) {
            boolean changedWidth = layoutLocalHierarchy(s, v, dir);
            if(changedWidth && s.getParent(v) != null) {
                ((SugiNode) s.getParent(v)).setHLayoutValid(false);
            }
        }
    }

    /**
     * 1. fills the two given Maps with outgoing and incoming edges according
     * to the given nodes and vertical edges in the local hierarchy. 2. sorts
     * the outgoing edges by their target node  and the incoming edges by
     * their source
     *
     * @param lh the local hierarchy
     * @param outgoing the hashmap to fill with outgoing edges
     * @param incoming the hashmap to fill with incoming edges
     */
    public static void prepareAdjLists(LocalHierarchy lh, Map outgoing,
        Map incoming) {
        for(Iterator it = new IteratorOfCollections(lh.getNodes());
            it.hasNext();) {
            Object node = it.next();
            outgoing.put(node, new LinkedList());
            incoming.put(node, new LinkedList());
        }

        //fill the hashmap incoming with edges in order of their source node
        for(Iterator it = new IteratorOfCollections(lh.getNodes());
            it.hasNext();) {
            SugiNode nexts = (SugiNode) it.next();
            for(Iterator it2 = lh.getVerticalEdgesIterator(nexts);
                it2.hasNext();) {
                LHEdge nextE = (LHEdge) it2.next();
                List incomingEdges = (List) incoming.get(nextE.getTarget());
                incomingEdges.add(nextE);
            }
        }

        //fill the hashmap outgoing with edges in order of their target node
        for(Iterator it = new IteratorOfCollections(lh.getNodes());
            it.hasNext();) {
            SugiNode nexts = (SugiNode) it.next();
            List edges = (List) incoming.get(nexts);
            for(Iterator it2 = edges.iterator(); it2.hasNext();) {
                LHEdge nextE = (LHEdge) it2.next();
                List outgoingEdges = (List) outgoing.get(nextE.getSource());
                outgoingEdges.add(nextE);
            }
        }
    }

    /**
     * implementation of Algo 1 of brandes. nodes must contain a level 0. the
     * indices start at 0
     *
     * @param nodes DOCUMENT ME!
     * @param up DOCUMENT ME!
     */
    public static void preprocessing(List nodes, HashMap up) {
        assert !((List) nodes.get(nodes.size() - 1)).isEmpty();
        //nodes.size sollte genau 'h' entsprechen

        /*
         * i starts at 1, because the zero level is only for dummy nodes.
         *  unlike in common graphs, there can be inner
         * segments from first to second level in compound graphs.
         */
        for(int i = 1; i <= nodes.size() - 2; i++) {
            assert ((List) nodes.get(0)).isEmpty();

            int k_0 = -1;
            LHEdge v_k0Iv_lIP1 = null;
            int l = 0;

            List levelI = (List) nodes.get(i);
            List levelIP1 = (List) nodes.get(i + 1);

            //faengt l_1 bei 1 an?
            for(int l_1 = 0; l_1 <= levelIP1.size() - 1; l_1++) {
                boolean condition = false;
                int k_1 = -1;
                LHEdge v_k1Iv_l1IP1 = null;

                //the order of the following two if's is important
                if(l_1 == levelIP1.size() - 1) {
                    condition = true;
                    k_1 = levelI.size() - 1;
                    v_k1Iv_l1IP1 = null;
                }

                SugiNode v_l1IP1 = (SugiNode) levelIP1.get(l_1);
                List incidentEdges = (List) up.get(v_l1IP1);

                //a node cannot be incident to more than one upper inner
                // segment
                //(and one lower)
                if(!incidentEdges.isEmpty()) {
                    LHEdge e = (LHEdge) incidentEdges.get(0);
                    if(e.isInnerSegment()) {
                        condition = true;
                        assert e.getTarget() == v_l1IP1
                        || e.getSource() == v_l1IP1;
                        assert incidentEdges.size() == 1;
                        k_1 = ((SugiNode) getOtherNode(e, v_l1IP1)).getPosition();
                        v_k1Iv_l1IP1 = e;
                    }
                }

                if(condition) {
                    for(; l <= l_1; l++) {
                        SugiNode v_lIP1 = (SugiNode) levelIP1.get(l);
                        for(Iterator it = ((List) up.get(v_lIP1)).iterator();
                            it.hasNext();) {
                            LHEdge v_kIv_lIP1 = (LHEdge) it.next();
                            SugiNode v_kI =
                                (SugiNode) getOtherNode(v_kIv_lIP1, v_lIP1);
                            assert v_kIv_lIP1.getTarget() == v_lIP1
                            || v_kIv_lIP1.getSource() == v_lIP1;

                            int k = v_kI.getPosition();

                            assert k_1 != -1;
                            if(k < k_0 || k > k_1) {
                                //attention to type2 conflicts
                                if(v_kIv_lIP1.isInnerSegment()) {
                                    if(k < k_0) {
                                        assert v_k0Iv_lIP1 != null;
                                        //System.out.println(
                                        //"found type2 conflict"
                                        //+ v_k0Iv_lIP1 + " and "
                                        //+ v_kIv_lIP1);
                                    } else {
                                        assert k > k_1;
                                        assert v_k1Iv_l1IP1 != null;
                                        //System.out.println(
                                        //"found type2 conflict" + v_kIv_lIP1
                                        //+ " and " + v_k1Iv_l1IP1);
                                    }
                                } else {
                                    v_kIv_lIP1.setNonVertical(true);
                                    //System.out
                                    //.println("found type1 conflict, noninnersegment="
                                    //+ v_kIv_lIP1);
                                }
                            }
                        }
                    }

                    k_0 = k_1;
                    v_k0Iv_lIP1 = v_k1Iv_l1IP1;
                }
            }
        }
    }

    /**
     * belongs to brandes horizontal metric layout
     *
     * @param nodes DOCUMENT ME!
     * @param edges DOCUMENT ME!
     */
    public static void verticalAlignment(List nodes, HashMap edges) {
        for(Iterator it = new IteratorOfCollections(nodes); it.hasNext();) {
            SugiNode next = (SugiNode) it.next();
            next.setHLayoutRoot(next);
            next.setHLayoutAlign(next);
        }

        for(int i = 1; i <= nodes.size() - 1; i++) {
            int r = -1;

            List levelI = (List) nodes.get(i); //richtig, weil es level 0 gibt
            int k = 0;
            for(Iterator it = levelI.iterator(); it.hasNext(); k++) {
                SugiNode v_kI = (SugiNode) it.next();
                assert v_kI.getPosition() == k;

                List incidentEdges = (List) edges.get(v_kI);
                if(!incidentEdges.isEmpty()) {
                    LHEdge leftmedian;
                    LHEdge rightmedian;

                    int d = incidentEdges.size();
                    if((d + 1) % 2 == 0) {
                        int index = ((d + 1) / 2) - 1; //as list starts at index 0
                        leftmedian = (LHEdge) incidentEdges.get(index);
                        rightmedian = null;
                    } else {
                        int index = (d / 2) - 1;
                        leftmedian = (LHEdge) incidentEdges.get(index);
                        rightmedian = (LHEdge) incidentEdges.get(index + 1);
                    }

                    assert (v_kI.getHLayoutAlign() == v_kI);
                    if(v_kI.getHLayoutAlign() == v_kI) {
                        int result = tryEdge(leftmedian, v_kI, r);
                        if(result != -1) {
                            r = result;
                            assert !(v_kI.getHLayoutAlign() == v_kI);
                        }
                    }

                    if(v_kI.getHLayoutAlign() == v_kI && rightmedian != null) {
                        int result = tryEdge(rightmedian, v_kI, r);
                        if(result != -1) {
                            r = result;
                        }
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     * @param v DOCUMENT ME!
     * @param offset DOCUMENT ME!
     */
    static void absoluteVLayout(SugiCompoundGraph g, SugiNode v, int offset) {
        v.setAbsoluteY(v.getLocalY() + offset);
        for(Iterator it = g.getChildrenIterator(v); it.hasNext();) {
            absoluteVLayout(g, (SugiNode) it.next(), v.getAbsoluteY());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     * @param dir DOCUMENT ME!
     */
    static void horizontalLayout(SugiCompoundGraph g, int dir) {
        localHLayout(g, g.getMetricRoot(), dir);
        g.getMetricRoot().setLocalX(0);
        absoluteHLayout(g, g.getMetricRoot(), 0);
    }

    /**
     * works recursive, if wished.  the local actions are: sets the height for
     * all elements of w, and the localY for all children of the elements of w
     *
     * @param w a list of nodes, which lie on the same level
     * @param recursive DOCUMENT ME!
     *
     * @return Returns the space needed by the nodes in w
     */
    static int localVLayout(List w, boolean recursive) {
        assert !w.isEmpty();

        int height = gapAboveLev0;
        int maxNumOfLev = 0; // is n + 2
        boolean onlyDummies = true; //w contains only dummies
        boolean childrenExist = false; //elements of w have children

        //==============================================================
        //special case: level 0
        List Z_0 = new LinkedList();

        for(Iterator it = w.iterator(); it.hasNext();) {
            SugiNode elementW = (SugiNode) it.next();
            Z_0.addAll(elementW.getChildrenAtLevel(0));

            //additional processing
            maxNumOfLev = Math.max(maxNumOfLev, elementW.getNumberOfLHLevels());
            if(!elementW.isDummyNode()) {
                onlyDummies = false;
            }
        }

        if(!Z_0.isEmpty()) {
            childrenExist = true;

            //assumption: dummy nodes at level 0 have no children
            //avoids recursion
            for(Iterator it = Z_0.iterator(); it.hasNext();) {
                SugiNode next = (SugiNode) it.next();
                assert next.isDummyNode();
                next.setHeight(basicHeightDummy);
                next.setLocalY(height);
            }

            height += basicHeightDummy + gapLev0_Lev1;
        } else {
            height = upperGap;
        }

        //==============================================================
        //usual cases
        for(int i = 1; i <= maxNumOfLev - 2; i++) {
            List Z = new LinkedList();
            for(Iterator it = w.iterator(); it.hasNext();) {
                SugiNode elementW = (SugiNode) it.next();
                Z.addAll(elementW.getChildrenAtLevel(i));
            }

            //all Z's should be nonempty from 1 to maxlev
            assert !Z.isEmpty();
            if(!Z.isEmpty()) {
                childrenExist = true;
                for(Iterator it = Z.iterator(); it.hasNext();) {
                    SugiNode z = (SugiNode) it.next();
                    z.setLocalY(height);
                }

                if(recursive) {
                    height += localVLayout(Z, true) + verticalGap;
                } else {
                    //all elements of Z have now localY value.
                    //remains the height.
                    height += determineHeight(Z) + verticalGap;
                }
            }
        }

        //==================================================================
        //special case level n + 1
        
        if(maxNumOfLev > 1) {
            //attention: is only maybe the level n + 1, might be level n.
            boolean lastLevOnlyDummies = true; //children on last level are only
                                               // dummies

            List Z_NP1 = new LinkedList();
            for(Iterator it = w.iterator(); it.hasNext();) {
                SugiNode elementW = (SugiNode) it.next();
                Z_NP1.addAll(elementW.getChildrenAtLevel(maxNumOfLev - 1));
            }

            assert !Z_NP1.isEmpty();
            if(!Z_NP1.isEmpty()) {
                childrenExist = true;
                for(Iterator it = Z_NP1.iterator(); it.hasNext();) {
                    SugiNode z = (SugiNode) it.next();
                    z.setLocalY(height);
                    if(!z.isDummyNode()) {
                        lastLevOnlyDummies = false;
                    }
                }

                if(lastLevOnlyDummies) {
                    height += gap_Lev_n_Lev_nP1 - verticalGap;
                    for(Iterator it = Z_NP1.iterator(); it.hasNext();) {
                        SugiNode z = (SugiNode) it.next();
                        z.setHeight(basicHeightDummy);
                        //change of height value changes localY of children
                        z.setLocalY(height);
                    }

                    height += basicHeightDummy + gapBelowLev_nP1;
                } else {
                    if(recursive) {
                        height += localVLayout(Z_NP1, true) + lowerGap;
                    } else {
                        height += determineHeight(Z_NP1) + lowerGap;
                    }
                }
            }
        }

        //============================================================
        if(!childrenExist) {
            height = onlyDummies ? basicHeightDummy : basicHeight;
        }

        //set the height value of all elements of w
        for(Iterator it = w.iterator(); it.hasNext();) {
            SugiNode n = (SugiNode) it.next();
            n.setHeight(height);
        }

        return height;
    }

    /**
     * Returns the endnode of the given edge that is not the given node n. so n
     * must be one of the endnodes of the edge.
     *
     * @param e DOCUMENT ME!
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static Node getOtherNode(LHEdge e, Node n) {
        if(e.getTarget() == n) {
            return e.getSource();
        } else {
            assert e.getSource() == n;
            return e.getTarget();
        }
    }

    /**
     * this is the place where all parameters are set.
     *
     * @param value DOCUMENT ME!
     * @param preferences DOCUMENT ME!
     */
    private static void setParameters(int value, Preferences preferences) {
        switch(value) {
            case SugiyamaDrawingStyle.DEBUG_STYLE:
                upperGap = 10;
                verticalGap = 20;
                gapAboveLev0 = 10;
                gapLev0_Lev1 = verticalGap;
                lowerGap = upperGap;
                gapBelowLev_nP1 = gapAboveLev0;
                gap_Lev_n_Lev_nP1 = gapLev0_Lev1;
                basicHeight = 15;
                basicWidth = 15;
                leftGap = 10;
                horizontalGap = 20;
                horizontalGapBetweenDummies = horizontalGap;
                rightGap = leftGap;
                basicHeightDummy = basicHeight;
                basicWidthDummy = basicWidth;
                leftAndRightGapInDummies = leftGap; //=rightGap
                break;

            case SugiyamaDrawingStyle.FINAL_STYLE:
                upperGap = 5;
                verticalGap = 20;
                gapAboveLev0 = 0;
                gapLev0_Lev1 = 15;
                lowerGap = upperGap;
                gapBelowLev_nP1 = gapAboveLev0;
                gap_Lev_n_Lev_nP1 = gapLev0_Lev1;
                basicHeight = 14;
                basicWidth = 14;
                leftGap = 10;
                horizontalGap = 20;
                horizontalGapBetweenDummies = 5;
                rightGap = 10;
                basicHeightDummy = 0;
                basicWidthDummy = 2;
                leftAndRightGapInDummies = 0;
                break;

            case SugiyamaDrawingStyle.EDITOR_STYLE:
                upperGap = preferences.clusOffset;
                verticalGap = preferences.clusOffset;
                gapAboveLev0 = 0;
                gapLev0_Lev1 = preferences.clusOffset;
                lowerGap = upperGap;
                gapBelowLev_nP1 = gapAboveLev0;
                gap_Lev_n_Lev_nP1 = gapLev0_Lev1;
                basicHeight = preferences.leafHeight;
                basicWidth = preferences.leafWidth;
                leftGap = preferences.clusOffset;
                horizontalGap = preferences.clusOffset;
                horizontalGapBetweenDummies = 5;
                rightGap = leftGap;
                basicHeightDummy = 0;
                basicWidthDummy = 2;
                leftAndRightGapInDummies = 0;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param g DOCUMENT ME!
     * @param v DOCUMENT ME!
     * @param offset DOCUMENT ME!
     */
    private static void absoluteHLayout(SugiCompoundGraph g, SugiNode v,
        int offset) {
        v.setAbsoluteX(v.getLocalX() + offset);

        for(Iterator it = g.getChildrenIterator(v); it.hasNext();) {
            SugiNode w = (SugiNode) it.next();
            absoluteHLayout(g, w, v.getAbsoluteX());
        }

        v.setHLayoutValid(true);
    }

    /**
     * belongs to brandes horizontal metric layout
     *
     * @param a DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static int bias(int[] a) {
        assert a.length == 4;

        int mini = 0;
        int maxi = 0;
        int sum = a[0];
        for(int i = 1; i < a.length; i++) {
            if(a[i] < a[mini]) {
                mini = i;
            } else if(a[i] > a[maxi]) {
                maxi = i;
            }

            sum += a[i];
        }

        sum -= a[mini];
        sum -= a[maxi];
        sum /= 2;
        return sum;
    }

    /**
     * belongs to brandes horizontal metric layout
     *
     * @param v DOCUMENT ME!
     * @param u DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static int delta(SugiNode v, SugiNode u) {
        int width = (v.getWidth() + u.getWidth()) / 2;
        if(v.isDummyNode() && u.isDummyNode()) {
            width += horizontalGapBetweenDummies;
        } else if(v.isDummyNode() || u.isDummyNode()) {
            width += horizontalGapBetweenDummies / 2 + horizontalGap / 2;
        } else {
            width += horizontalGap;
        }

        return width;
    }

    /**
     * used during expand. determines the height of v's sublevels, to give 
     * children(v) the correct height.
     *
     * @param Z DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static int determineHeight(List Z) {
        assert ((SugiNode) Z.get(0)).getClev().getTail() != 0;

        int height = basicHeight;
        for(Iterator it = Z.iterator(); it.hasNext();) {
            SugiNode z = (SugiNode) it.next();
            if(z.getHeight() > 0) {
                height = z.getHeight();
                break;
            }
        }

        for(Iterator it = Z.iterator(); it.hasNext();) {
            SugiNode z = (SugiNode) it.next();

            //either a new node or has correct value already or it is 0
            assert z.getHeight() == 0 || z.getHeight() == height;
            z.setHeight(height);
        }

        return height;
    }

    /**
     * old layout method
     *
     * @param g DOCUMENT ME!
     */
    private static void horizontalLayoutNaive(SugiCompoundGraph g) {
        localHLayoutNaive(g, g.getMetricRoot(), 0);
        absoluteHLayout(g, g.getMetricRoot(), 0);
    }

    /**
     * special layout method for children of dummy nodes. avoids brandes.
     *
     * @param s
     * @param v the dummy node
     */
    private static void layoutDummyNode(SugiCompoundGraph s, DummyNode v) {
        if(!s.hasChildren(v)) {
            v.setWidth(basicWidthDummy);
        } else {
            // easy variant
            int width = leftAndRightGapInDummies;
            for(Iterator it = s.getChildrenIterator(v); it.hasNext();) {
                SugiNode next = (SugiNode) it.next();
                next.setLocalX(leftAndRightGapInDummies);
                width =
                    Math.max(width, leftAndRightGapInDummies + next.getWidth());
            }

            v.setWidth(width + leftAndRightGapInDummies);
        }
    }

    /**
     * local horizontal layout of a local hierarchy
     *
     * @param s DOCUMENT ME!
     * @param v DOCUMENT ME!
     * @param dir DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static boolean layoutLocalHierarchy(SugiCompoundGraph s,
        SugiNode v, int dir) {
        int oldWidth = v.getWidth();

        //special treatments, avoid brandes-algo at all
        if(v.isDummyNode()) {
            layoutDummyNode(s, (DummyNode) v);
        } else if(!s.hasChildren(v)) {
            //special case: ordinary node without children
            v.setWidth(basicWidth);
        } else {
            /* Brandes Algo */

            assert v.checkLH(s, v, 2, false);

            Collection originalNodesMatrix = v.getLocalHierarchy().getNodes();
            HashMap down = new LinkedHashMap();
            HashMap up = new LinkedHashMap();
            prepareAdjLists(v.getLocalHierarchy(), down, up);

            List[] views = new List[4];
            views[0] = createNodesView(originalNodesMatrix, LEFT_MOST, UP_MOST);
            assert LocalHierarchy.checkPositions(views[0]);
            //use the leftUp for preprocessing
            preprocessing(views[0], up);

            /*four runs of Algo 2 and 3 */
            verticalAlignment(views[0], up);
            horizontalCompaction(views[0], 0);
            views[1] =
                createNodesView(originalNodesMatrix, RIGHT_MOST, UP_MOST);
            assert LocalHierarchy.checkPositions(views[1]);
            //adjust adjList 'up' to rightmost
            reverseOrder(up);
            verticalAlignment(views[1], up);
            horizontalCompaction(views[1], 1);
            views[2] =
                createNodesView(originalNodesMatrix, LEFT_MOST, DOWN_MOST);
            assert LocalHierarchy.checkPositions(views[2]);
            verticalAlignment(views[2], down);
            horizontalCompaction(views[2], 2);
            views[3] =
                createNodesView(originalNodesMatrix, RIGHT_MOST, DOWN_MOST);
            assert LocalHierarchy.checkPositions(views[3]);
            //adjust adjList 'down' to rightmost
            reverseOrder(down);
            verticalAlignment(views[3], down);
            horizontalCompaction(views[3], 3);

            //restore position-values
            v.getLocalHierarchy().updatePositions();

            //invert rightmost values
            for(Iterator it = s.getChildrenIterator(v); it.hasNext();) {
                SugiNode w = (SugiNode) it.next();

                //x values mark middle of the node, so this is correct
                w.localXs[1] = -w.localXs[1];
                w.localXs[3] = -w.localXs[3];
            }

            /*determine smallest width assignment */

            //1. determine minimum x-values
            int[] minX = new int[4];

            //important to use a leftoriented list of lists
            for(Iterator it = views[0].iterator(); it.hasNext();) {
                List level = (List) it.next();
                if(!level.isEmpty()) {
                    SugiNode n = (SugiNode) level.get(0);
                    for(int i = 0; i < 4; i++) {
                        minX[i] = Math.min(minX[i], n.localXs[i]);
                    }
                }
            }

            //2. determine maximum x-values
            int[] maxX = new int[4];

            //important to use a rightoriented list of lists
            for(Iterator it = views[1].iterator(); it.hasNext();) {
                List level = (List) it.next();
                if(!level.isEmpty()) {
                    SugiNode n = (SugiNode) level.get(0);
                    for(int i = 0; i < 4; i++) {
                        maxX[i] = Math.max(maxX[i], n.localXs[i]);
                    }
                }
            }

            //3. determine smallest width
            int minindex = 0;
            int minValue = Integer.MAX_VALUE;
            for(int i = 0; i < 4; i++) {
                if(maxX[i] - minX[i] < minValue) {
                    minindex = i;
                    minValue = maxX[i] - minX[i];
                }
            }

            int offset0 = minX[minindex] - minX[0];
            int offset2 = minX[minindex] - minX[2];
            int offset1 = maxX[minindex] - maxX[1];
            int offset3 = maxX[minindex] - maxX[3];
            for(Iterator it = new IteratorOfCollections(originalNodesMatrix);
                it.hasNext();) {
                SugiNode n = (SugiNode) it.next();

                n.localXs[0] += offset0;
                n.localXs[2] += offset2;
                n.localXs[1] += offset1;
                n.localXs[3] += offset3;
            }

            /* compute biased coordinates */

            //localXs mark middle of node
            for(Iterator it = s.getChildrenIterator(v); it.hasNext();) {
                SugiNode w = (SugiNode) it.next();
                w.biasedX = bias(w.localXs);
            }

            //only for debug purposes. usually: dir == 4
            if(dir <= 3) {
                for(Iterator it =
                        new IteratorOfCollections(originalNodesMatrix);
                    it.hasNext();) {
                    SugiNode w = (SugiNode) it.next();
                    w.biasedX = w.localXs[dir];
                }
            }

            /* align completed coordinates to 0 */
            int minLeftBound = Integer.MAX_VALUE;

            //important to use a leftoriented list of lists
            for(Iterator it = views[0].iterator(); it.hasNext();) {
                List level = (List) it.next();
                if(!level.isEmpty()) {
                    SugiNode n = (SugiNode) level.get(0);
                    minLeftBound =
                        Math.min(minLeftBound, n.biasedX - n.getWidth() / 2);
                }
            }

            int maxRightBound = Integer.MIN_VALUE;

            //important to use a rightoriented list of lists
            for(Iterator it = views[1].iterator(); it.hasNext();) {
                List level = (List) it.next();
                if(!level.isEmpty()) {
                    SugiNode n = (SugiNode) level.get(0);
                    maxRightBound =
                        Math.max(maxRightBound, n.biasedX + n.getWidth() / 2);
                }
            }

            //2. compute localX-values, IMPORTANT, they now mark left bound of a node
            for(Iterator it = new IteratorOfCollections(originalNodesMatrix);
                it.hasNext();) {
                SugiNode w = (SugiNode) it.next();

                //transform x-value so that it marks left bound of node
                int x = w.biasedX - w.getWidth() / 2;
                w.setLocalX(leftGap + x - minLeftBound);
            }

            //set width of parent node
            v.setWidth(leftGap + (maxRightBound - minLeftBound) + rightGap);
        }

        return oldWidth != v.getWidth();
    }

    /**
     * old layout method
     *
     * @param g DOCUMENT ME!
     * @param v DOCUMENT ME!
     * @param localX DOCUMENT ME!
     */
    private static void localHLayoutNaive(SugiCompoundGraph g, SugiNode v,
        int localX) {
        int[] width = new int[v.getNumberOfLHLevels()];
        for(int i = 0; i < width.length; i++) {
            width[i] = leftGap;
            for(Iterator it = v.getChildrenAtLevel(i).iterator(); it.hasNext();) {
                SugiNode w = (SugiNode) it.next();
                localHLayoutNaive(g, w, width[i]);
                width[i] += w.getWidth() + horizontalGap;
            }

            //correction at right end
            if(width[i] != leftGap) {
                width[i] += rightGap - horizontalGap;
            } else {
                width[i] = basicWidth;
            }
        }

        int maxwidth = basicWidth;
        for(int i = 0; i < width.length; i++) {
            maxwidth = Math.max(maxwidth, width[i]);
        }

        assert maxwidth != 0;
        v.setWidth(maxwidth);

        v.setLocalX(localX);
    }

    /**
     * belongs to brandes
     *
     * @param v DOCUMENT ME!
     * @param xvalues DOCUMENT ME!
     */
    private static void place_block(SugiNode v, HashMap xvalues) {
        if(!xvalues.containsKey(v)) {
            xvalues.put(v, new Integer(0));

            LinkedList stack = new LinkedList();
            stack.addLast(new StackElement(v, v));
            place_block_help(xvalues, stack);
        }
    }

    /**
     * help method for place_block.
     * 
     * is a non-recursive variant of place_block. a little tricky
     *
     * @param xvalues DOCUMENT ME!
     * @param stack DOCUMENT ME!
     */
    private static void place_block_help(HashMap xvalues, LinkedList stack) {
newLoop: 
        while(!stack.isEmpty()) {
            StackElement elem = (StackElement) stack.getLast();
            SugiNode v = elem.v;
            do {
                if(elem.w.getPosition() > 0) {
                    SugiNode u = elem.w.getHLayoutPred().getHLayoutRoot();

                    if(!xvalues.containsKey(u)) {
                        xvalues.put(u, new Integer(0));
                        stack.addLast(new StackElement(u, u));
                        continue newLoop;
                    }

                    SugiNode sinkU = u.getHLayoutSink();
                    if(v.getHLayoutSink() == v) {
                        v.setHLayoutsink(sinkU);
                    }

                    if(v.getHLayoutSink() != sinkU) {
                        int x_v = ((Integer) xvalues.get(v)).intValue();
                        int x_u = ((Integer) xvalues.get(u)).intValue();
                        int otherValue =
                            x_v - x_u - delta(elem.w, elem.w.getHLayoutPred());
                        if(sinkU.getHLayoutshift() > otherValue) {
                            sinkU.setHLayoutshift(otherValue);
                            sinkU.setHLayoutParentSink(v.getHLayoutSink());
                        }
                    } else {
                        int x_v = ((Integer) xvalues.get(v)).intValue();
                        int x_u = ((Integer) xvalues.get(u)).intValue();
                        int otherValue =
                            x_u + delta(elem.w, elem.w.getHLayoutPred());
                        if(x_v < otherValue) {
                            xvalues.put(v, new Integer(otherValue));
                        }
                    }
                }

                elem.w = elem.w.getHLayoutAlign();
            } while(elem.w != v);

            stack.removeLast();
        }
    }

    /**
     * reverses the order of the edges in the adjlists stored in the given map.
     * works not with DLL objects.
     * 
     * @param adjList DOCUMENT ME!
     */
    private static void reverseOrder(Map adjList) {
        for(Iterator it = adjList.values().iterator(); it.hasNext();) {
            List edges = (List) it.next();
            if(edges instanceof DLL) {
                System.out.println(
                    "Warning: internal error in MetricLayout.reverseOrder");
            } else {
                //following method does not work with DLLs
                Collections.reverse(edges);
            }
        }
    }

    /**
     * belongs to brandes horizontal metric layout
     *
     * @param medianEdge DOCUMENT ME!
     * @param v_kI DOCUMENT ME!
     * @param r DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static int tryEdge(LHEdge medianEdge, SugiNode v_kI, int r) {
        SugiNode u_m = (SugiNode) getOtherNode(medianEdge, v_kI);

        if(!medianEdge.isNonVertical() && r < u_m.getPosition()) {
            u_m.setHLayoutAlign(v_kI);
            v_kI.setHLayoutRoot(u_m.getHLayoutRoot());
            v_kI.setHLayoutAlign(v_kI.getHLayoutRoot());
            return u_m.getPosition();
        }

        return -1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param s DOCUMENT ME!
     */
    private static void verticalLayout(SugiCompoundGraph s) {
        //it starts at the child of the root, because the root is not really the root
        List start = new LinkedList();
        start.add(s.getMetricRoot());
        localVLayout(start, true);
        s.getMetricRoot().setLocalY(0);
        absoluteVLayout(s, s.getMetricRoot(), 0);
    }

    //~ Inner Classes ==========================================================

    /**
     * compares two LHedges by their target node. the target nodes must be of
     * type SugiNode. the edges must be normalized
     */
    public static class TargetComparator implements Comparator {
        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         * @param arg1 DOCUMENT ME!
         *
         * @return a negative integer if the first argument is less than the
         *         second
         */
        public int compare(Object arg0, Object arg1) {
            BaryNode target0 = ((BaryEdge) arg0).getBTarget();
            BaryNode target1 = ((BaryEdge) arg1).getBTarget();
            return target0.getPosition() - target1.getPosition();
        }
    }

    /**
     * is used in the non-recursive version of place_block
     */
    private static class StackElement {
        SugiNode v;
        SugiNode w;

        /**
         * Creates a new StackElement object.
         *
         * @param w DOCUMENT ME!
         * @param v DOCUMENT ME!
         */
        public StackElement(SugiNode w, SugiNode v) {
            this.w = w;
            this.v = v;
            //this.u = u;
        }

        /**
         * Creates a new StackElement object.
         *
         * @param v DOCUMENT ME!
         */
        public StackElement(SugiNode v) {
            this.v = v;
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String toString() {
            return "v=" + v + " w=" + w;
        }
    }
}
