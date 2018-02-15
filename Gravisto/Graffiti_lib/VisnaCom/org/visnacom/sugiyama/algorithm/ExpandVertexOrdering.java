/*==============================================================================
*
*   ExpandVertexOrdering.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: ExpandVertexOrdering.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.algorithm;

import java.util.*;

import org.visnacom.model.DLL;
import org.visnacom.model.Edge;
import org.visnacom.sugiyama.model.*;
import org.visnacom.sugiyama.model.SugiActionExpand.SugiMapping;


/**
 * contains operations for vertex ordering during expand.
 */
public class ExpandVertexOrdering {
    //~ Methods ================================================================

    /**
     * implementation of the vertex ordering steps during expand
     *
     * @param action DOCUMENT ME!
     */
    public static void expand(SugiActionExpand action) {
        SugiCompoundGraph s = action.s;

        /*1.part */
        /*preprocessing*/
        ExpandVertexOrdering.updateLRofP_us(s, action);
        assert s.checkLHs(0); //lambdarhovalues cannot be correct, 

        //because p_u's are not ordered so far
        LocalHierarchy lhOfPaV =
            ((SugiNode) s.getParent(action.v)).getLocalHierarchy();
        List levelV = lhOfPaV.getNodesAtLevel(action.v.getClev().getTail());
        Map vertical = lhOfPaV.getVertical();
        assert levelV.contains(action.v);
        assert levelV.containsAll(action.localDummyNodes);
        assert levelV.containsAll(action.externalDummyNodes);

        levelV.removeAll(action.localDummyNodes);

        levelV.removeAll(action.externalDummyNodes);
        VertexOrdering.insertExternalDummyNodes(action.v,
            action.externalDummyNodes, lhOfPaV);

        //divide the local dummy nodes in two groups depending whether they 
        //connect to the level above or below
        DLL localDummyNodesUp = new DLL();
        DLL localDummyNodesDown = new DLL();
        for(Iterator it = action.localDummyNodes.iterator(); it.hasNext();) {
            Object next = it.next();
            if(((List) vertical.get(next)).isEmpty()) {
                localDummyNodesUp.add(next);
            } else {
                localDummyNodesDown.add(next);
            }
        }

        VertexOrdering.insertLocalDummyNodes(action.v, localDummyNodesUp,
            localDummyNodesDown, lhOfPaV);
        LocalHierarchy.updatePositions(levelV);
        assert lhOfPaV.checkPositions();

        /* 2.part ***************/
        //preprocessing: complete LH(v)
        s.activateLH(action.v, action.internalEdges);
        VertexOrdering.analyseLambdaRho(s, (SugiNode) s.getParent(action.v));
        //vorsicht, das funktioniert nur, wenn ausschliesslich die neuen
        //waagrechten kanten vorhanden sind
        assert s.checkLHs(1);

        //order children(v)
        VertexOrdering.vOrderGlobal(s, action.v);

        //only called because of additional work in this method
        //basically there are no horizontal edges inside v and the p_u's
        for(Iterator it = action.getDummyNodesIterator(); it.hasNext();) {
            VertexOrdering.vOrderGlobal(s, (SugiNode) it.next());
        }

        assert s.checkLHs(2);

        /* 3.part */
        ExpandVertexOrdering.splitDummyNodesOnPath(action);

        assert s.checkLHs(2, false);
    }

    /**
     * splitting of the dummy nodes on the whole edge path
     *
     * @param action DOCUMENT ME!
     */
    static void splitDummyNodesOnPath(SugiActionExpand action) {
        assert action.s.checkEdgeMappings(0);

        //foreach p_u ...
        for(Iterator it = action.mappingsIterator(); it.hasNext();) {
            SugiMapping m = (SugiMapping) it.next();

            /*create new dummy nodes p_2,...,p_k*/
            List P =
                multiplyDummyNode(action.s, m.p_u, m.getNumberOfVDashs() - 1,
                    true);
            P.add(0, m.p_u);

            /*determine correct ordering of v_1,...,v_k */
            List vDashCs = m.getVDashCs();

            //in fact, I order the old edges (v',c), taking into account only
            // v'
            boolean vLiesLeft;
            if(!m.isFromVToU) {
                if(m.p_u.getPosition() < action.v.getPosition()) {
                    //bottom to top
                    Collections.sort(vDashCs, new LexicographicOrder(false, m));
                    //v lies right
                    vLiesLeft = false;
                } else {
                    //top to bottom
                    Collections.sort(vDashCs, new LexicographicOrder(true, m));
                    //v lies left
                    vLiesLeft = true;
                }
            } else {
                if(m.p_u.getPosition() < action.v.getPosition()) {
                    //top to bottom
                    Collections.sort(vDashCs, new LexicographicOrder(true, m));
                    // v lies right
                    vLiesLeft = false;
                } else {
                    //bottom to top
                    Collections.sort(vDashCs, new LexicographicOrder(false, m));
                    // v lies left
                    vLiesLeft = true;
                }
            }

            Iterator it2 = vDashCs.iterator();
            Iterator it3 = P.iterator();

            //omit v_1
            it2.next();
            it3.next();

            assert P.size() == vDashCs.size();
            assert P.size() >= 1;

            /*foreach v_i, i=2..k*/
            while(it2.hasNext() && it3.hasNext()) {
                //I need that here for the reference to originalEdge
                SugiEdge vDashC = (SugiEdge) it2.next();
                DummyNode p_i = (DummyNode) it3.next();

                //p_i's have their lambdarho value already
                assert (m.isPuExternal()
                && Math.abs(p_i.getLambda() - p_i.getRho()) == 1)
                || ((!m.isPuExternal())
                && Math.abs(p_i.getLambda() - p_i.getRho()) == 0);

                assert !action.s.hasChildren(p_i);

                if(m.isFromVToU) {
                    DummyNode oldC = (DummyNode) vDashC.getTarget();

                    //create matching dummy node c as child of p_i
                    DummyNode newC =
                        action.s.ensureDummyChild(p_i, oldC.getClev().getTail());

                    //bend edge (v_i,c)
                    action.s.changeTarget(vDashC, newC);
                } else {
                    //create matching dummy node c as child of p_i
                    DummyNode newC =
                        action.s.ensureDummyChild(p_i,
                            ((SugiNode) vDashC.getSource()).getClev().getTail());

                    //bend edge (c,v_i)
                    action.s.changeSource(vDashC, newC);
                }
            }

            //delete all unneeded c's in p_u
            int counter = 0; //for debug

            //works on purpose with clone of children list
            for(Iterator it4 = action.s.getChildren(m.p_u).iterator();
                it4.hasNext();) {
                DummyNode c = (DummyNode) it4.next();
                if(action.s.getAdjEdges(c).isEmpty()) {
                    action.s.deleteLeaf(c);
                } else {
                    counter++;
                }
            }

            assert counter == 1;

            //update the lambdarho values of the c's
            for(Iterator it4 = vDashCs.iterator(); it4.hasNext();) {
                SugiEdge vDashC = (SugiEdge) it4.next();
                DummyNode c;
                if(m.isFromVToU) {
                    c = (DummyNode) vDashC.getTarget();
                } else {
                    c = (DummyNode) vDashC.getSource();
                }

                if(m.p_u.getPosition() < action.v.getPosition()) {
                    c.setLambdaRho(0, 1);
                } else {
                    c.setLambdaRho(1, 0);
                }
            }

            //only called because of additional work in this method
            for(Iterator it4 = P.iterator(); it4.hasNext();) {
                VertexOrdering.vOrderGlobal(action.s, (SugiNode) it4.next());
            }

            //the template edge path
            List E = action.s.getCorrespondingEdges(m.origMapping.oldEdge);
            boolean EliesLeft;

            //a list of the new original edges of the view is needed for access 
            //to the new edge paths
            List origEdges = new LinkedList();
            for(Iterator it4 = vDashCs.iterator(); it4.hasNext();) {
                SugiEdge e = (SugiEdge) it4.next();
                origEdges.add(e.getOriginalEdge());
            }

            if(!m.origMapping.oldEdgeDeleted) {
                assert action.s.contains(m.p_uU);
                action.s.deleteEdge(m.p_uU);

                EliesLeft = vLiesLeft;
            } else {
                action.s.deleteMapping(m.origMapping.oldEdge);

                SugiEdge v1C = (SugiEdge) vDashCs.get(0);

                if(m.isFromVToU) {
                    //replace (v,u) by (p1,u)
                    SugiEdge temp = (SugiEdge) E.set(0, m.p_uU);
                    assert temp == m.vToUOrUToV;
                    //add (v1,c)
                    E.add(0, v1C);
                } else {
                    //replace (u,v) by (u,p1)
                    SugiEdge temp = (SugiEdge) E.set(E.size() - 1, m.p_uU);
                    assert temp == m.vToUOrUToV;
                    //add (c,v1)
                    E.add(v1C);
                }

                EliesLeft = true;

                //transfer E to new original edge
                action.s.establishEdgeMapping(v1C.getOriginalEdge(), E);

                //delete p_u from P
                assert P.indexOf(m.p_u) == 0;
                P.remove(m.p_u);

                //ignore E_1
                Edge origEdge_1 = (Edge) origEdges.remove(0);
                assert origEdge_1 == v1C.getOriginalEdge();
            }

            //now, P contains exactly the nodes which have no edge-path yet,
            // and E is the template.
            assert E.size() >= 1;

            P = splitHomogenMiddlePart(action, m, E, P, origEdges, EliesLeft);

            assert action.s.checkEdgeMappings(0);
            assert action.s.checkLHs(2);

            /* last edge-segment */
            SugiEdge edge;
            if(m.isFromVToU) {
                edge = (SugiEdge) E.get(E.size() - 1);
                assert edge.getTarget() == getOriginalEndNode(action, m);
            } else {
                edge = (SugiEdge) E.get(0);
                assert edge.getSource() == getOriginalEndNode(action, m);
            }

            Iterator it5 = origEdges.iterator();
            assert origEdges.size() == P.size();

            for(Iterator it4 = P.iterator(); it4.hasNext();) {
                DummyNode p_i = (DummyNode) it4.next();
                Edge origEdge_i = (Edge) it5.next();
                SugiEdge p_iW;
                if(m.isFromVToU) {
                    p_iW = (SugiEdge) action.s.newEdge(p_i, edge.getTarget());
                    action.s.addToEdgeMapping(origEdge_i, p_iW, false);
                } else {
                    p_iW = (SugiEdge) action.s.newEdge(edge.getSource(), p_i);
                    action.s.addToEdgeMapping(origEdge_i, p_iW, true);
                }
            }
        }

        assert action.s.checkEdgeMappings(1, true);
        assert action.s.checkLHs(2, false); //set it to true, if warnings wished
    }

    /**
     * sets the lambdarho values of the new nodes p_u
     *
     * @param s DOCUMENT ME!
     * @param action DOCUMENT ME!
     */
    static void updateLRofP_us(SugiCompoundGraph s, SugiActionExpand action) {
        SugiNode paV = (SugiNode) s.getParent(action.v);
        for(Iterator it = action.mappingsIterator(); it.hasNext();) {
            SugiActionExpand.SugiMapping m = (SugiMapping) it.next();

            //update lambda-rho value caused by (p_u,u)
            m.p_u.resetLambdaRho();
            if(m.isPuExternal()) {
                if(m.isFromVToU == s.isLeftToRight(m.vToUOrUToV)) {
                    m.p_u.incRho();
                } else {
                    m.p_u.incLambda();
                }
            } else {
                if(m.origMapping.oldEdgeDeleted) {
                    assert !paV.getLocalHierarchy().contains(m.vToUOrUToV);
                    assert !s.contains(m.vToUOrUToV);
                }
            }
        }
    }

    /**
     * only for debug purposes.
     *
     * @param action DOCUMENT ME!
     * @param m DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static SugiNode getOriginalEndNode(SugiActionExpand action,
        SugiMapping m) {
        if(action.v == action.s.getCorrespondingNode(
                m.origMapping.oldEdge.getSource())) {
            return action.s.getCorrespondingNode(m.origMapping.oldEdge
                .getTarget());
        } else {
            assert action.v == action.s.getCorrespondingNode(m.origMapping.oldEdge
                .getTarget());
            return action.s.getCorrespondingNode(m.origMapping.oldEdge
                .getSource());
        }
    }

    /**
     * inserts a child into each element of "parents". clev is transfered from
     * "node". transfers lambda and rho value, too. transfers the metric
     * coordinates in anticipation of metric layout.
     *
     * @param s the graph
     * @param node the template child
     * @param parents list of nodes
     *
     * @return list of the new children
     */
    private static List multiplyDummyChild(SugiCompoundGraph s, DummyNode node,
        List parents) {
        List result = new LinkedList();
        for(Iterator it = parents.iterator(); it.hasNext();) {
            DummyNode newPu = (DummyNode) it.next();
            DummyNode newC =
                s.newDummyLeaf(newPu, node.getClev().getTail(),
                    DummyNode.UNKNOWN);

            newC.setLambdaRho(node.getLambda(), node.getRho());
            result.add(newC);

            newC.setLocalY(node.getLocalY());
            newC.setAbsoluteY(node.getAbsoluteY());
            newC.setHeight(node.getHeight());
        }

        return result;
    }

    /**
     * inserts n copies of the given node into the given graph as siblings!
     * transfers the compoundlevel and the lambda-rho value of the node.
     * vOrderGlobal is not called. transfers the metric coordinates, too.
     *
     * @param s the containing graph
     * @param node the node to copy
     * @param n the number of wished copies
     * @param nodeLiesLeft if true, the copies of node are inserted to the
     *        right.
     *
     * @return the list of new dummy nodes.
     */
    private static List multiplyDummyNode(SugiCompoundGraph s, DummyNode node,
        int n, boolean nodeLiesLeft) {
        LinkedList list = new LinkedList();
        SugiNode pa = (SugiNode) s.getParent(node);

        int insertPos = node.getPosition() + (nodeLiesLeft ? 1 : 0);
        for(int i = 1; i <= n; i++) {
            SugiNode newPu =
                s.newDummyLeaf(pa, node.getClev().getTail(), insertPos,
                    node.getType());
            newPu.setLambdaRho(node.getLambda(), node.getRho());
            list.addFirst(newPu);

            newPu.setLocalY(node.getLocalY());
            newPu.setAbsoluteY(node.getAbsoluteY());
            newPu.setHeight(node.getHeight());
        }

        return list;
    }

    /**
     * inserts n copies of the given node into the given graph. transfers the
     * compoundlevel and the lambda-rho value of the node. Copies all ancestor
     * that are dummy nodes, too. the compoundlevel and lambdarho value of the
     * ancestors is tranfered, too.  vOrderGlobal is applied to all the new
     * nodes. So, the whole local hierarchies in the new dummy nodes are
     * completed.
     *
     * @param s DOCUMENT ME!
     * @param node the node to copy
     * @param n DOCUMENT ME!
     * @param parents the list to fill with parents of the new nodes. is only
     *        done, if parent was dummy node
     * @param eliesLeft DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static List multiplyDummyNodeWithAncs(SugiCompoundGraph s,
        DummyNode node, int n, List parents, boolean eliesLeft) {
        assert s.checkLHs(0);

        List newAncs = new LinkedList();
        List newParents = new LinkedList();
        List result = null;

        Stack stack = new Stack();
        SugiNode anc = node;
        while(anc.isDummyNode()) {
            stack.push(anc);
            anc = (SugiNode) s.getParent(anc);
        }

        newAncs = multiplyDummyNode(s, (DummyNode) stack.pop(), n, eliesLeft);
        result = newAncs;

        while(!stack.empty()) {
            DummyNode template = (DummyNode) stack.pop();
            newParents = result;
            result = multiplyDummyChild(s, template, result);
        }

        for(Iterator it = newAncs.iterator(); it.hasNext();) {
            DummyNode newPu = (DummyNode) it.next();
            VertexOrdering.vOrderGlobal(s, newPu);
        }

        if(!newParents.isEmpty()) {
            parents.addAll(newParents);
        }

        return result;
    }

    /**
     * implements the splitting of the dummy nodes along the edge path.  see
     * thesis for description.
     *
     * @param action DOCUMENT ME!
     * @param m DOCUMENT ME!
     * @param E the template edge path
     * @param P the current P set. (represents the parents of the last step)
     * @param origEdges list of original edges to have access to the edge paths
     * @param EliesLeft indicates whether the template edge path lies left of
     *        the copies.
     *
     * @return the valid P set after splitting.
     */
    private static List splitHomogenMiddlePart(SugiActionExpand action,
        SugiMapping m, List E, List P, List origEdges, boolean EliesLeft) {
        ListIterator Eiterator;

        SugiEdge currentEdge;

        assert action.s.checkEdgeMappings(0);
        assert action.s.checkEdgeMapping(E, 1, false);

        if(m.isFromVToU) {
            Eiterator = E.listIterator();
            if(m.origMapping.oldEdgeDeleted) {
                //omit first edge
                Eiterator.next();
            }

            currentEdge = (SugiEdge) Eiterator.next();
        } else {
            Eiterator = E.listIterator(E.size());
            if(m.origMapping.oldEdgeDeleted) {
                //omit first edge
                Eiterator.previous();
            }

            currentEdge = (SugiEdge) Eiterator.previous();
        }

        assert !m.origMapping.oldEdgeDeleted || currentEdge == m.p_uU;
        assert m.origMapping.oldEdgeDeleted || currentEdge == m.vToUOrUToV;

        //for special case 2
        boolean externalPathMode = false;
        List pathlocations = new LinkedList();

        /* while target(e_i) != w */
        while((m.isFromVToU && Eiterator.hasNext())
            || (!m.isFromVToU && Eiterator.hasPrevious())) {
            SugiEdge nextEdge;
            DummyNode firstN;
            DummyNode secondN;
            if(m.isFromVToU) {
                nextEdge = (SugiEdge) Eiterator.next();
                firstN = (DummyNode) currentEdge.getTarget();
                secondN = (DummyNode) nextEdge.getSource();
            } else {
                nextEdge = (SugiEdge) Eiterator.previous();
                firstN = (DummyNode) currentEdge.getSource();
                secondN = (DummyNode) nextEdge.getTarget();
            }

            //if the while loop is entered, there must be another edge
            assert firstN != getOriginalEndNode(action, m);

            List T = new LinkedList();
            List nextP = new LinkedList();
            if(!externalPathMode) {
                if(action.s.getParent(currentEdge.getSource()) == action.s
                    .getParent(currentEdge.getTarget()) && firstN == secondN) {
                    T = multiplyDummyNodeWithAncs(action.s, firstN, P.size(),
                            null, EliesLeft);
                    nextP = T;
                } else if(action.s.getParent(currentEdge.getSource()) != action.s
                    .getParent(currentEdge.getTarget()) && firstN == secondN) {
                    assert ((SugiNode) action.s.getParent(firstN)).isDummyNode();

                    T = multiplyDummyNodeWithAncs(action.s, firstN, P.size(),
                            pathlocations, EliesLeft);
                    //the vOrderGlobal call, that is contained in this call, is
                    //not necessarily redundant
                    nextP = T;
                    if(action.s.getParent(nextEdge.getSource()) == action.s
                        .getParent(nextEdge.getTarget())) {
                        externalPathMode = true;
                    }
                } else if(action.s.getParent(secondN) == firstN) {
                    nextP =
                        multiplyDummyNodeWithAncs(action.s, secondN, P.size(),
                            T, EliesLeft);
                } else if(action.s.getParent(firstN) == secondN) {
                    T = multiplyDummyNodeWithAncs(action.s, firstN, P.size(),
                            nextP, EliesLeft);
                } else {
                    assert false;
                }
            } else {
                //special case as the generated dummy nodes become not siblings
                assert firstN == secondN;
                assert action.s.getParent(currentEdge.getSource()) == action.s
                .getParent(currentEdge.getTarget());

                T = multiplyDummyChild(action.s, firstN, pathlocations);
                nextP = T;
                if(action.s.getParent(nextEdge.getSource()) != action.s
                    .getParent(nextEdge.getTarget())) {
                    for(Iterator it = pathlocations.iterator(); it.hasNext();) {
                        DummyNode newPu = (DummyNode) it.next();
                        VertexOrdering.vOrderGlobal(action.s, newPu);
                    }

                    externalPathMode = false;
                    pathlocations = null;
                }
            }

            assert P.size() == T.size();
            assert origEdges.size() == P.size();

            Iterator it4 = P.iterator();
            Iterator it5 = T.iterator();
            Iterator it6 = origEdges.iterator();

            //for each t_i
            while(it4.hasNext()) {
                DummyNode p_i = (DummyNode) it4.next();
                DummyNode t_i = (DummyNode) it5.next();
                SugiEdge p_iT_i;
                Edge origEdge_i = (Edge) it6.next();
                if(m.isFromVToU) {
                    p_iT_i = (SugiEdge) action.s.newEdge(p_i, t_i);
                    action.s.addToEdgeMapping(origEdge_i, p_iT_i, false);
                } else {
                    p_iT_i = (SugiEdge) action.s.newEdge(t_i, p_i);
                    action.s.addToEdgeMapping(origEdge_i, p_iT_i, true);
                }
            }

            currentEdge = nextEdge;
            P = nextP;
        }

        /* end of while loop */
        assert action.s.checkEdgeMappings(0);

        assert !m.isFromVToU
        || !((SugiNode) currentEdge.getTarget()).isDummyNode();
        assert m.isFromVToU
        || !((SugiNode) currentEdge.getSource()).isDummyNode();
        assert !m.isFromVToU
        || (currentEdge.getTarget() == action.s.getCorrespondingNode(currentEdge.getOriginalEdge()
                                                                                .getTarget())
        || currentEdge.getTarget() == action.s.getCorrespondingNode(currentEdge.getOriginalEdge()
                                                                               .getSource()));
        assert m.isFromVToU
        || (currentEdge.getSource() == action.s.getCorrespondingNode(currentEdge.getOriginalEdge()
                                                                                .getTarget())
        || currentEdge.getSource() == action.s.getCorrespondingNode(currentEdge.getOriginalEdge()
                                                                               .getSource()));

        return P;
    }

    //~ Inner Classes ==========================================================

    /**
     * used for Expansion. implements the orders as proposed in raitner algo
     * 3.3 i.e. the edges are ordered according to the position of the v'
     */
    private static class LexicographicOrder implements Comparator {
        private SugiMapping m;
        private boolean fromTopToBottom;

        /**
         * Creates a new LexicographicOrder object.
         *
         * @param fromTopToBottom DOCUMENT ME!
         * @param m the mapping that knows, whether the edges lead from v' to c
         *        or from c to v'.
         */
        public LexicographicOrder(boolean fromTopToBottom, SugiMapping m) {
            this.fromTopToBottom = fromTopToBottom;
            this.m = m;
        }

        
        /**
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Object arg0, Object arg1) {
            SugiNode v0;
            SugiNode v1;
            v0 = m.getVDash((SugiEdge) arg0);
            v1 = m.getVDash((SugiEdge) arg1);

            int level0 = v0.getClev().getTail();
            int level1 = v1.getClev().getTail();
            int pos0 = v0.getPosition();
            int pos1 = v1.getPosition();
            if(level0 < level1) {
                return fromTopToBottom ? -1 : 1;
            } else if(level0 == level1) {
                assert pos0 != pos1;
                return pos0 - pos1;
            } else {
                return fromTopToBottom ? 1 : -1;
            }
        }
    }
}
