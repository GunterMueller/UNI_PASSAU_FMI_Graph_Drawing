/*==============================================================================
*
*   VertexOrdering.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: VertexOrdering.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.algorithm;

import java.util.*;
import java.util.Map.Entry;

import org.visnacom.model.DLL;
import org.visnacom.model.Node;
import org.visnacom.sugiyama.model.*;


/**
 * provides operations for vertex ordering in static layout
 */
public class VertexOrdering {
    //~ Static fields/initializers =============================================

    //a limit for the iterations during barycenter ordering
    static int MAX_ITERATION = 20;

    //~ Methods ================================================================

    /**
     * bary order lower
     *
     * @param i DOCUMENT ME!
     * @param nodes DOCUMENT ME!
     * @param vertical DOCUMENT ME!
     */
    public static void bol(int i, List nodes, Map vertical) {
        assert i >= 0 && i <= nodes.size() - 2;
        assert LocalHierarchy.checkPositions(nodes);

        List levelI = (List) nodes.get(i);
        computeBaryCenterLower(levelI, vertical);

        //sort level i
        Collections.sort(levelI, new LambdaBaryComparator());
        LocalHierarchy.updatePositions(levelI);
    }

    /**
     * bary order upper. the position attributes must be correct.
     *
     * @param i DOCUMENT ME!
     * @param nodes DOCUMENT ME!
     * @param vertical DOCUMENT ME!
     */
    public static void bou(int i, List nodes, Map vertical) {
        assert i <= nodes.size() - 1 && i > 0;
        assert LocalHierarchy.checkPositions(nodes);

        List levelIM = (List) nodes.get(i - 1);
        List levelI = (List) nodes.get(i);

        computeBaryCenterUpper(levelI, levelIM, vertical, false);

        //sort level i
        Collections.sort(levelI, new LambdaBaryComparator());
        LocalHierarchy.updatePositions(levelI);
    }

    /**
     * delete dummy nodes lower.
     *
     * @param i the level
     * @param lh DOCUMENT ME!
     */
    public static void ddl(int i, LocalHierarchy lh) {
        deleteDummyNodes(i, false, lh);
    }

    /**
     * delete dummy nodes upper.
     *
     * @param i the level
     * @param lh DOCUMENT ME!
     */
    public static void ddu(int i, LocalHierarchy lh) {
        deleteDummyNodes(i, true, lh);
    }

    /**
     * insert dummy nodes lower
     *
     * @param i DOCUMENT ME!
     * @param lh DOCUMENT ME!
     */
    public static void idl(int i, LocalHierarchy lh) {
        insertDummyNodes(i, false, lh);
    }

    /**
     * insert dummy nodes upper
     *
     * @param i DOCUMENT ME!
     * @param lh DOCUMENT ME!
     */
    public static void idu(int i, LocalHierarchy lh) {
        insertDummyNodes(i, true, lh);
    }

    /**
     * only for debug purposes. lets the caller define the number of loops. 0
     * means no ordering. but it does initialize the nodes and edges in lh
     * this requires normalization. -1 means no ordering and only initializes
     * the nodes in the LH. doesn't require normalization, but prohibits
     * advanced horizontal metric layout
     *
     * @param s DOCUMENT ME!
     * @param i DOCUMENT ME!
     *
     * @deprecated
     */
    public static void order(SugiCompoundGraph s, int i) {
        s.activateAllLHs(i >= 0);

        if(i < 0) {
            return;
        }

        MAX_ITERATION = i;
        vOrderGlobal(s, (SugiNode) s.getRoot());
        assert s.checkLHs(2);
    }

    /**
     * the main method for static layout. the given compound graph must be
     * normalized. it is necessary for the metrical layout to call the
     * ordering,  otherwise the LH's don't get initialized
     *
     * @param s DOCUMENT ME!
     */
    public static void order(SugiCompoundGraph s) {
        order(s, MAX_ITERATION);
    }

    /**
     * must be called after barycenter ordering
     *
     * @param lh DOCUMENT ME!
     * @param undoInformation DOCUMENT ME!
     */
    public static void postprocessing(LocalHierarchy lh, List undoInformation) {
        lh.trimNodeList();

        assert undoInformation.size() == 4;

        Iterator it = undoInformation.iterator();

        /* process UnDoHorizontal-objects */
        List next = (List) it.next();
        for(Iterator it2 = next.iterator(); it2.hasNext();) {
            UnDoHorizontal undo = (UnDoHorizontal) it2.next();
            lh.deleteGivenEdge(undo.proxyEdge);

            int pos1 = ((BaryNode) undo.incoming.getSource()).getPosition();
            int pos2 = ((BaryNode) undo.outgoing.getTarget()).getPosition();
            int insertPos =
                Math.min(pos1, pos2) + 1 + Math.abs(pos1 - pos2) / 2;
            lh.addNode(undo.dummyNode, insertPos); /*important for
               computation of other horizontal edges*/

            lh.addGivenEdge(undo.incoming);
            lh.addGivenEdge(undo.outgoing);
        }

        /*process UnDoExternal-objects */
        next = (List) it.next();

        HashMap externalDummyNodes = new LinkedHashMap();
        for(Iterator it2 = next.iterator(); it2.hasNext();) {
            UnDoExternal ue = (UnDoExternal) it2.next();

            //prepare dummy nodes
            lh.registerNode(ue.dummyNode);
            lh.addGivenEdge(ue.edge);

            //determine v
            Node v;
            if(ue.edge.getSource() == ue.dummyNode) {
                v = ue.edge.getTarget();
            } else {
                assert ue.edge.getBTarget() == ue.dummyNode;
                v = ue.edge.getSource();
            }

            //build up mapping between v and belonging dummy nodes
            List exdummyNodesofV = (List) externalDummyNodes.get(v);
            if(exdummyNodesofV == null) {
                exdummyNodesofV = new LinkedList();
                externalDummyNodes.put(v, exdummyNodesofV);
            }

            exdummyNodesofV.add(ue.dummyNode);
        }

        //handle each pair of v and dummy nodes
        for(Iterator it2 = externalDummyNodes.entrySet().iterator();
            it2.hasNext();) {
            Map.Entry entry = (Entry) it2.next();

            insertExternalDummyNodes((SugiNode) entry.getKey(),
                (List) entry.getValue(), lh);
            lh.updatePositions(((BaryNode) entry.getKey()).getLevel());
        }

        /* process UnDoToLocal-objects and UnDoVertical-objects.
         * they both belong to local DummyNodes, in the first case connecting to
         * the level below, in the second, connecting to the next level above.
         * (important to come last, so local dummy nodes are placed directly next to v)
         * */
        next = (List) it.next();

        //here the delta of all real nodes is resetted so they are inserted in the middle
        //of the direct local dummy nodes.
        for(Iterator it2 = new IteratorOfCollections(lh.getNodes());
            it2.hasNext();) {
            SugiNode sn = (SugiNode) it2.next();
            sn.notifyEdgeBent(false);
        }

        /*first a mapping between the node v and its local dummy nodes is established*/
        Map undoObjectsOfVs = new LinkedHashMap();
        for(Iterator it2 = next.iterator(); it2.hasNext();) {
            UnDoToLocal ul = (UnDoToLocal) it2.next();
            Node v = ul.horizontalEdge.getSource();
            List objectsOfv = (List) undoObjectsOfVs.get(v);
            if(objectsOfv == null) {
                objectsOfv = new LinkedList();
                undoObjectsOfVs.put(v, objectsOfv);
            }

            objectsOfv.add(ul);
        }

        next = (List) it.next();
        for(Iterator it2 = next.iterator(); it2.hasNext();) {
            UnDoVertical ul = (UnDoVertical) it2.next();
            Object v = ul.horizontalEdge.getTarget();
            List objectsOfv = (List) undoObjectsOfVs.get(v);
            if(objectsOfv == null) {
                objectsOfv = new LinkedList();
                undoObjectsOfVs.put(v, objectsOfv);
            }

            objectsOfv.add(ul);
        }

        //now handle these mappings
        for(Iterator it2 = undoObjectsOfVs.entrySet().iterator();
            it2.hasNext();) {
            Map.Entry entry = (Entry) it2.next();
            SugiNode v = (SugiNode) entry.getKey();
            List undoObjects = (List) entry.getValue();

            DLL localDummyNodesDown = new DLL();
            DLL localDummyNodesUp = new DLL();

            //prepare the local hierarchy
            for(Iterator it3 = undoObjects.iterator(); it3.hasNext();) {
                Object nextO = it3.next();
                if(nextO instanceof UnDoToLocal) {
                    UnDoToLocal ul = (UnDoToLocal) nextO;
                    lh.registerNode(ul.dummyNode);
                    lh.addGivenEdge(ul.horizontalEdge);
                    lh.restoreOrigSource(ul.verticalEdge, ul.dummyNode);
                    assert ul.horizontalEdge.getTarget() == ul.dummyNode;
                    assert ul.horizontalEdge.getSource() == v;
                    localDummyNodesDown.add(ul.dummyNode);
                } else {
                    assert nextO instanceof UnDoVertical;

                    UnDoVertical ul = (UnDoVertical) nextO;
                    lh.registerNode(ul.dummyNode);
                    lh.addGivenEdge(ul.horizontalEdge);
                    lh.restoreOrigTarget(ul.verticalEdge, ul.dummyNode);
                    assert ul.horizontalEdge.getSource() == ul.dummyNode;
                    assert ul.horizontalEdge.getTarget() == v;
                    localDummyNodesUp.add(ul.dummyNode);
                }
            }

            //now insert the local dummy nodes
            insertLocalDummyNodes(v, localDummyNodesUp, localDummyNodesDown, lh);
            lh.updatePositions(((SugiNode) entry.getKey()).getLevel());
            resetDeltaFlag(localDummyNodesDown);
            resetDeltaFlag(localDummyNodesUp);
        }

        assert lh.checkPositions();
    }

    /**
     * must be called before barycenter ordering. contains splitting method
     *
     * @param lh the original matrix of nodes
     *
     * @return a list of information for the postprocessing.
     */
    public static List preprocessing(LocalHierarchy lh) {
        //contains the horizontal edges, that lead to a horizontal dummy node
        List edgesHorizToHoriz = new LinkedList();

        //contains the horizontal edges, that lead to a local dummy node
        List edgesHorizToLocal = new LinkedList();

        //contains the horizontal edges, that lead to a external dummy node
        List edgesHorizToExtern = new LinkedList();

        //contains the horizontal edges, that come from a external dummy node
        List edgesHorizFromExtern = new LinkedList();

        //contains the vertical edges, that lead to a local dummy node
        List edgesVerticalToLocal = new LinkedList();

        //retrieve all situations
        //because I have a problem to access incoming edges directly
        Map horizontal = lh.getHorizontal();
        Map vertical = lh.getVertical();
        for(Iterator it = new IteratorOfCollections(horizontal.values());
            it.hasNext();) {
            LHEdge se = (LHEdge) it.next();
            SugiNode source = (SugiNode) se.getSource();
            if(source.isDummyNode()) {
                DummyNode dn = (DummyNode) source;
                if(dn.getType() == DummyNode.LOCAL_OR_EXTERNAL
                    && dn.getLambdaRho() != 0) {
                    edgesHorizFromExtern.add(se);
                    assert !((SugiNode) se.getTarget()).isDummyNode();
                }
            }

            SugiNode target = (SugiNode) se.getTarget();
            if(target.isDummyNode()) {
                DummyNode dn = (DummyNode) target;
                if(dn.getType() == DummyNode.HORIZONTAL) {
                    edgesHorizToHoriz.add(se);
                    assert !((SugiNode) se.getSource()).isDummyNode();
                } else if(dn.getType() == DummyNode.LOCAL_OR_EXTERNAL
                    && dn.getLambdaRho() != 0) {
                    edgesHorizToExtern.add(se);
                } else if(dn.getType() == DummyNode.LOCAL_OR_EXTERNAL
                    && dn.getLambdaRho() == 0) {
                    edgesHorizToLocal.add(se);
                }
            }
        }

        for(Iterator it = new IteratorOfCollections(vertical.values());
            it.hasNext();) {
            LHEdge se = (LHEdge) it.next();
            SugiNode target = (SugiNode) se.getTarget();
            if(target.isDummyNode()) {
                DummyNode dn = (DummyNode) target;
                if(dn.getType() == DummyNode.LOCAL_OR_EXTERNAL
                    && dn.getLambdaRho() == 0) {
                    edgesVerticalToLocal.add(se);
                }
            }
        }

        //handle these situations(is extra step because of problems with
        // iterators) and save them in four groups
        List undoInformation = new LinkedList();
        List unDoHorizontalList = new LinkedList();
        undoInformation.add(unDoHorizontalList);

        List unDoExternalList = new LinkedList();
        undoInformation.add(unDoExternalList);

        List unDoToLocalList = new LinkedList();
        undoInformation.add(unDoToLocalList);

        List unDoVerticalList = new LinkedList();
        undoInformation.add(unDoVerticalList);

        for(Iterator it = edgesHorizToHoriz.iterator(); it.hasNext();) {
            LHEdge se = (LHEdge) it.next();
            DummyNode dn = (DummyNode) se.getTarget();

            SugiNode origSource = (SugiNode) se.getSource();
            List edges = (List) horizontal.get(dn);
            assert edges.size() == 1;

            LHEdge se2 = (LHEdge) edges.get(0);
            SugiNode origTarget = (SugiNode) se2.getTarget();
            lh.deleteGivenEdge(se);
            lh.deleteGivenEdge(se2);
            lh.deleteNode(dn, false);

            BaryEdge be = lh.addDummyEdge(origSource, origTarget);
            UnDoHorizontal uh = new UnDoHorizontal();
            uh.dummyNode = dn;
            uh.incoming = se;
            uh.outgoing = se2;
            uh.proxyEdge = be;
            unDoHorizontalList.add(uh);
        }

        for(Iterator it = edgesHorizFromExtern.iterator(); it.hasNext();) {
            LHEdge se = (LHEdge) it.next();
            DummyNode dn = (DummyNode) se.getSource();
            lh.deleteGivenEdge(se);
            lh.deleteNode(dn, false);
            if(dn.getLambdaRho() == 1) {
                ((SugiNode) se.getTarget()).incLambda();
            } else {
                ((SugiNode) se.getTarget()).incRho();
                assert dn.getLambdaRho() == -1;
            }

            UnDoExternal ue = new UnDoExternal();
            ue.dummyNode = dn;
            ue.edge = se;
            unDoExternalList.add(ue);
        }

        for(Iterator it = edgesHorizToExtern.iterator(); it.hasNext();) {
            LHEdge se = (LHEdge) it.next();
            DummyNode dn = (DummyNode) se.getTarget();
            lh.deleteGivenEdge(se);
            lh.deleteNode(dn, false);
            if(dn.getLambdaRho() == 1) {
                ((SugiNode) se.getSource()).incLambda();
            } else {
                ((SugiNode) se.getSource()).incRho();
                assert dn.getLambdaRho() == -1;
            }

            UnDoExternal ue = new UnDoExternal();
            ue.dummyNode = dn;
            ue.edge = se;
            unDoExternalList.add(ue);
        }

        for(Iterator it = edgesHorizToLocal.iterator(); it.hasNext();) {
            LHEdge se = (LHEdge) it.next();
            DummyNode dn = (DummyNode) se.getTarget();
            List edges = (List) vertical.get(dn);
            assert edges.size() == 1;

            LHEdge se2 = (LHEdge) edges.get(0);

            lh.deleteGivenEdge(se);
            lh.bendSourceVertical(se2, (SugiNode) se.getSource());
            lh.deleteNode(dn, false);

            UnDoToLocal ul = new UnDoToLocal();
            ul.horizontalEdge = se;
            ul.dummyNode = dn;
            ul.verticalEdge = se2;
            unDoToLocalList.add(ul);
        }

        for(Iterator it = edgesVerticalToLocal.iterator(); it.hasNext();) {
            LHEdge se = (LHEdge) it.next();
            DummyNode dn = (DummyNode) se.getTarget();
            List edges = (List) horizontal.get(dn);
            assert edges.size() == 1;

            LHEdge se2 = (LHEdge) edges.get(0);
            SugiNode newTarget = (SugiNode) se2.getTarget();
            lh.deleteGivenEdge(se2);
            lh.bendTargetVertical(se, newTarget);
            lh.deleteNode(dn, false);

            UnDoVertical uv = new UnDoVertical();
            uv.verticalEdge = se;
            uv.dummyNode = dn;
            uv.horizontalEdge = se2;
            unDoVerticalList.add(uv);
        }

        /* determine initial positions of the node in each level */
        List nodes = lh.getNodes();
        Map incoming = LocalHierarchy.createIncomingMap(nodes, horizontal);
        for(int i = 0; i < nodes.size(); i++) {
            new PositionInitializer().order((List) nodes.get(i), horizontal,
                incoming);
        }

        //insert level n+1
        nodes.add(new LinkedList());
        lh.updatePositions();
        return undoInformation;
    }

    /**
     * does the local ordering of an local hierarchy
     *
     * @param v DOCUMENT ME!
     */
    public static void vOrderLocal(SugiNode v) {
        LocalHierarchy lh = v.getLocalHierarchy();
        List undoInformation = preprocessing(lh);

        boolean onlyOneChildperlevel = true;
        for(int i = 0; i < v.getNumberOfLHLevels(); i++) {
            if(v.getChildrenAtLevel(i).size() > 1) {
                onlyOneChildperlevel = false;
                break;
            }
        }

        if(!onlyOneChildperlevel) {
            baryOrdering(lh);
        }

        postprocessing(lh, undoInformation);
    }

    /**
     * called after local ordering. determines the orientation of the
     * horizontal lhedges and increments then  lambda and rho values. deletes
     * the horizontal edges afterwards.
     *
     * @param s DOCUMENT ME!
     * @param pa the node whose local hierarchy is to analyse
     */
    static void analyseLambdaRho(SugiCompoundGraph s, SugiNode pa) {
        for(Iterator it = pa.getHorizontalLHEdges(); it.hasNext();) {
            LHEdge lhe = (LHEdge) it.next();

            //determine orientation
            boolean leftToRight;
            SugiNode source = (SugiNode) lhe.getSource();
            SugiNode target = (SugiNode) lhe.getTarget();

            leftToRight = target.getPosition() > source.getPosition();

            for(Iterator it2 = lhe.getOriginalEdges().iterator();
                it2.hasNext();) {
                SugiEdge e = (SugiEdge) it2.next();
                SugiNode v = (SugiNode) e.getSource();
                SugiNode u = (SugiNode) e.getTarget();

                //e.setLeftToRight(leftToRight);
                //traverse to nca
                //while(v != u) {
                while(v != source && u != source) {
                    assert v != target && u != target;

                    if(leftToRight) {
                        v.incRho();
                        u.incLambda();
                    } else {
                        v.incLambda();
                        u.incRho();
                    }

                    v = (SugiNode) s.getParent(v);
                    u = (SugiNode) s.getParent(u);
                }
            }
        }

        pa.getLocalHierarchy().discardHorizontalEdges();
    }

    /**
     * inserts external dummy nodes of a node v into the local hierarchy at
     * fitting positions. the dummy nodes are assumed not to be in the
     * node-matrix, but have already entries in the adjList. The horizontal
     * edge is assumed to be in the adjList. The position attribute are not
     * corrected!
     *
     * @param v DOCUMENT ME!
     * @param externalDummyNodes DOCUMENT ME!
     * @param lh DOCUMENT ME!
     */
    static void insertExternalDummyNodes(SugiNode v, List externalDummyNodes,
        LocalHierarchy lh) {
        assert lh.contains(v);
        assert !lh.containsAny(externalDummyNodes);

        List levelV = lh.getNodesAtLevel(v.getLevel());
        SugiNode rightest = v;
        for(Iterator it = externalDummyNodes.iterator(); it.hasNext();) {
            DummyNode next = (DummyNode) it.next();
            if(next.getLambdaRho() == 1) {
                insertLeftOf(levelV, next, v);
            } else {
                assert next.getLambdaRho() == -1;
                insertRightOf(levelV, next, rightest);
                rightest = next;
            }
        }
    }

    /**
     * inserts the local dummy nodes of a node v next to it into the local
     * hierarchy. the dummy nodes are assumed not to be present in the given
     * level, but to have entries in the adjList. the horizontal and vertical
     * edges must be present. the position attributes of all nodes in the node
     * matrix must be correct. they are not corrected afterwards! the ordering
     * of the dummy nodes is derived from their bary values respecting the
     * next node on the edge path.
     *
     * @param v the orginal node,that is source or target of the horizontal
     *        edges
     * @param localDummyNodesUp the local dummy nodes that are connected to the
     *        level above.
     * @param localDummyNodesDown the local dummy nodes that are connected to
     *        the level below
     * @param lh the local hierarchy
     */
    static void insertLocalDummyNodes(SugiNode v, DLL localDummyNodesUp,
        DLL localDummyNodesDown, LocalHierarchy lh) {
        assert lh.contains(v);
        assert !lh.containsAny(localDummyNodesUp);
        assert !lh.containsAny(localDummyNodesDown);

        assert localDummyNodesDown.checkConsistency();
        assert localDummyNodesUp.checkConsistency();

        //        int savedPosOfV = v.getPosition();
        localDummyNodesUp.add(0, v);
        LocalHierarchy.updatePositions(localDummyNodesUp);
        computeBaryCenterUpper(localDummyNodesUp,
            lh.getNodesAtLevel(v.getLevel() - 1), lh.getVertical(), true);

        Collections.sort(localDummyNodesUp, new BaryComparator());

        //bary center of v is < 0, when it has no vertical edges.
        //it is important, that v has been inserted to the front of the list, 
        //so then it has baryvalue ca. -1(not exactly because of delta).
        //in this case, v is moved to the middle of all nodes. 
        moveVtoMiddle(v, localDummyNodesUp, v.getBarryCenter() >= 0);
        assert localDummyNodesUp.checkConsistency();

        /* second part */
        localDummyNodesDown.add(0, v);
        computeBaryCenterLower(localDummyNodesDown, lh.getVertical());

        Collections.sort(localDummyNodesDown, new BaryComparator());

        moveVtoMiddle(v, localDummyNodesDown, v.getBarryCenter() >= 0);
        assert localDummyNodesDown.checkConsistency();

        /* now insert the nodes next to v (is a kind of merge operation) */
        Iterator upIt = localDummyNodesUp.iterator();
        Iterator downIt = localDummyNodesDown.iterator();

        //SugiNode nextUp = null;
        // nextDown = null;
        assert upIt.hasNext(); //at least v is there
        assert downIt.hasNext();

        SugiNode nextUp = (SugiNode) upIt.next();
        SugiNode nextDown = (SugiNode) downIt.next();

        List levelV = lh.getNodesAtLevel(v.getLevel());

        //add all nodes at left side of v, till v is reached
        while(true) {
            if(nextUp != v
                && (nextDown == v
                || nextUp.getBarryCenter() <= nextDown.getBarryCenter())) {
                insertLeftOf(levelV, nextUp, v);
                nextUp = (SugiNode) upIt.next();
            } else if(nextDown != v) {
                insertLeftOf(levelV, nextDown, v);
                nextDown = (SugiNode) downIt.next();
            } else {
                break;
            }
        }

        /* leave v out */
        if(upIt.hasNext()) {
            nextUp = (SugiNode) upIt.next();
        } else {
            nextUp = null;
        }

        if(downIt.hasNext()) {
            nextDown = (SugiNode) downIt.next();
        } else {
            nextDown = null;
        }

        SugiNode last = v;

        /* now, right side of v */
        while(nextUp != null || nextDown != null) {
            if((nextUp != null && nextDown != null
                && nextUp.getBarryCenter() <= nextDown.getBarryCenter())
                || nextDown == null) {
                insertRightOf(levelV, nextUp, last);
                last = nextUp;
                if(upIt.hasNext()) {
                    nextUp = (SugiNode) upIt.next();
                } else {
                    nextUp = null;
                }
            } else {
                insertRightOf(levelV, nextDown, last);
                last = nextDown;
                if(downIt.hasNext()) {
                    nextDown = (SugiNode) downIt.next();
                } else {
                    nextDown = null;
                }
            }
        }
    }

    /**
     * see pseudocode
     *
     * @param s DOCUMENT ME!
     * @param pa DOCUMENT ME!
     */
    static void vOrderGlobal(SugiCompoundGraph s, SugiNode pa) {
        if(s.hasChildren(pa)) {
            vOrderLocal(pa);
        }

        analyseLambdaRho(s, pa);

        for(Iterator it = s.getChildrenIterator(pa); it.hasNext();) {
            SugiNode w = (SugiNode) it.next();
            vOrderGlobal(s, w);
        }
    }

    /**
     * implements the barycenter ordering of Sugiyama.
     *
     * @param lh DOCUMENT ME!
     */
    private static void baryOrdering(LocalHierarchy lh) {
        List nodes = lh.getNodes();
        Map vertical = lh.getVertical();
        Map horizontal = lh.getHorizontal();
        int crossingsUpLastRound;
        int cuttingsUpLastRound;
        int crossingsUp = crossCount(nodes, vertical);
        int cuttingsUp = cutCount(horizontal);

        int crossingsDownLastRound;
        int crossingsDown = crossingsUp;
        int cuttingsDown = cuttingsUp;
        int cuttingsDownLastRound;
        boolean upBetter = false;
        boolean downBetter = false;

        int iterations = 0;
        while(true) {
            iterations++;
            if(iterations > MAX_ITERATION) {
                System.out.println(
                    "WARNING:premature break of vertex ordering, because limit reached");
                break;
            }

            /*unlike in the paper of sugiyama, my levels start at 0,
             * But then, the parent node has to be a dummy node,
             * and dummy nodes have only one node per level, so ordering is trivial.
             * therefore I leave the start at 1
             */
            assert ((Collection) nodes.get(0)).isEmpty();

            int n = nodes.size() - 2;

            /*special sorting at beginning, so that the dummynodes (IDU1) are sorted
             * appropriately*/
            bou(1, nodes, vertical);

            /* downwards run */
            idu(1, lh);
            bou(1, nodes, vertical);
            ddu(1, lh);
            for(int i = 2; i <= n; i++) {
                bou(i, nodes, vertical);

                idu(i, lh);
                bou(i, nodes, vertical);
                ddu(i, lh);
            }

            crossingsDownLastRound = crossingsDown;
            cuttingsDownLastRound = cuttingsDown;
            crossingsDown = crossCount(nodes, vertical);
            cuttingsDown = cutCount(horizontal);

            downBetter =
                crossingsDown + cuttingsDown < crossingsDownLastRound
                + cuttingsDownLastRound;

            /* possible break */
            if(!downBetter && !upBetter) {
                break;
            }

            /* upwards run */
            idl(n, lh);
            bol(n, nodes, vertical);
            ddl(n, lh);
            for(int i = n - 1; i >= 1; i--) {
                bol(i, nodes, vertical);

                idl(i, lh);

                bol(i, nodes, vertical);
                ddl(i, lh);
            }

            crossingsUpLastRound = crossingsUp;
            cuttingsUpLastRound = cuttingsUp;
            crossingsUp = crossCount(nodes, vertical);
            cuttingsUp = cutCount(horizontal);

            upBetter =
                crossingsUp + cuttingsUp < crossingsUpLastRound
                + cuttingsUpLastRound;
        }
    }

    /**
     * updates the barycenter values of nodes of a level regarding their edges
     * to the lower level.
     *
     * @param level a list of nodes, that have entries in the adjacency list
     * @param adjList a map containing Lists of edges
     */
    private static void computeBaryCenterLower(List level, Map adjList) {
        BaryNode last = null;
        for(Iterator it = level.iterator(); it.hasNext();) {
            BaryNode v = (BaryNode) it.next();
            float sum = 0;
            float numberDE = 0; //number of down edges
            List downEdges = (List) adjList.get(v);
            for(Iterator it2 = downEdges.iterator(); it2.hasNext();) {
                BaryEdge e = (BaryEdge) it2.next();
                BaryNode u = e.getBTarget();

                sum += u.getPosition();
                numberDE++;
            }

            if(numberDE > 0) {
                v.setBarryCenter(sum / numberDE);
            } else {
                if(last != null) {
                    v.setBarryCenter(last.getBarryCenter());
                } else {
                    v.setBarryCenter(-1); //if this is changed, look in
                    //insertLocalDummyNodes
                }
            }

            last = v;
        }
    }

    /**
     * updates the barycenter values of nodes of a level regarding their edges
     * to the upper level.
     *
     * @param levelI the level to process
     * @param levelIM the (i-1)th level. is needed as incoming edges cannot be
     *        accessed directly
     * @param vertical the adjlist
     * @param isSubSet indicates whether the list "levelI" is a subset of the
     *        real level i and therefore whether there might exist edges
     *        not leading to "levelI"
     */
    private static void computeBaryCenterUpper(List levelI, List levelIM,
        Map vertical, boolean isSubSet) {
        float[] sum = new float[levelI.size()];
        float[] noe = new float[levelI.size()];

        for(Iterator it = levelIM.iterator(); it.hasNext();) {
            BaryNode v = (BaryNode) it.next();

            List downEdges = (List) vertical.get(v);
            for(Iterator it2 = downEdges.iterator(); it2.hasNext();) {
                BaryEdge e = (BaryEdge) it2.next();
                BaryNode u = e.getBTarget();

                if(!isSubSet || levelI.contains(u)) {
                    sum[u.getPosition()] += v.getPosition();
                    noe[u.getPosition()]++;
                }
            }
        }

        BaryNode last = null;
        int j = 0;
        for(Iterator it = levelI.iterator(); it.hasNext(); j++) {
            BaryNode u = (BaryNode) it.next();
            if(noe[j] > 0) {
                u.setBarryCenter(sum[j] / noe[j]);
            } else {
                if(last != null) {
                    u.setBarryCenter(last.getBarryCenter());
                } else {
                    u.setBarryCenter(-1); //if this is changed, look in
                    //insertLocalDummyNodes
                }
            }

            last = u;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param nodes DOCUMENT ME!
     * @param vertical DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static int crossCount(List nodes, Map vertical) {
        int crossings = 0;
        for(int i = 0; i < nodes.size() - 1; i++) {
            crossings += CrossCount.simpleAndEfficientCrossCount((List) nodes
                .get(i), (List) nodes.get(i + 1),
                new EdgeIteratorOfLevel((List) nodes.get(i), vertical));
        }

        return crossings;
    }

    /**
     * this method counts the crossings of horizontal edges through other nodes
     *
     * @param horizontal
     *
     * @return the number of cuts
     */
    private static int cutCount(Map horizontal) {
        int sum = 0;
        for(Iterator it = new IteratorOfCollections(horizontal.values());
            it.hasNext();) {
            BaryEdge e = (BaryEdge) it.next();
            sum += Math.abs(e.getBSource().getPosition()
                - e.getBTarget().getPosition()) - 1;
        }

        return sum;
    }

    /**
     * deletes the previously inserted dummy nodes representing horizontal
     * edges
     *
     * @param i the level
     * @param up the orientation
     * @param lh the matrix of the nodes
     */
    private static void deleteDummyNodes(int i, boolean up, LocalHierarchy lh) {
        //IDU
        assert !up || (i >= 1 && i <= lh.getNumberOfLevels() - 1);
        //IDL
        assert up || (i >= 1 && i <= lh.getNumberOfLevels() - 2);

        List levelI = lh.getNodesAtLevel(i);
        List levelIMP1 = lh.getNodesAtLevel(up ? i - 1 : i + 1);
        for(Iterator it = levelI.iterator(); it.hasNext();) {
            BaryNode v = (BaryNode) it.next();
            for(Iterator it2 = lh.getHorizontalEdgesIterator(v); it2.hasNext();) {
                BaryEdge e = (BaryEdge) it2.next();
                assert e.getBSource() == v;

                BaryNode u = e.getBTarget();

                assert (levelI.contains(u));

                //delete dummy node d
                BaryNode d = e.getDummyNode();

                //in case of "up",
                //dummy edges (d,v)and (d,u) are automatically removed at
                //the call deleteNode
                if(!up) {
                    //delete dummy edge (v,d)and (v,u)
                    BaryEdge vde = e.getDummyEdgeLeft();
                    BaryEdge ude = e.getDummyEdgeRight();
                    lh.deleteGivenEdge(vde);
                    lh.deleteGivenEdge(ude);
                }

                lh.deleteNode(d, false);
                e.setDummyNode(null);
                e.setDummyEdgeLeft(null);
                e.setDummyEdgeRight(null);
            }
        }

        LocalHierarchy.updatePositions(levelIMP1);
    }

    /**
     * this method works for upper and lower direction.
     * 
     * the ith level is assumed to be sorted refering to the i+-1 level.  the
     * dummy nodes are appended to the level i+-1. and the i+-1 level is sorted.
     *
     * @param i DOCUMENT ME!
     * @param up indicates whether the dummy nodes are inserted in the i-1 level 
     * or in i+1 level.
     * @param lh DOCUMENT ME!
     *
     * @return whether there have been inserted any dummy nodes
     */
    private static boolean insertDummyNodes(int i, boolean up, LocalHierarchy lh) {
        //      IDU
        assert !up || (i >= 1 && i <= lh.getNumberOfLevels() - 1);
        //IDL
        assert up || (i >= 1 && i <= lh.getNumberOfLevels() - 2);

        List level = lh.getNodesAtLevel(i);
        List levelIMP = lh.getNodesAtLevel(up ? i - 1 : i + 1);
        List savedLevelIMP = lh.getNodesAtLevelClone(up ? i - 1 : i + 1);
        List newDummyNodes = new LinkedList();
        for(Iterator it = level.iterator(); it.hasNext();) {
            BaryNode v = (BaryNode) it.next();
            for(Iterator it2 = lh.getHorizontalEdgesIterator(v); it2.hasNext();) {
                BaryEdge e = (BaryEdge) it2.next();
                assert e.getBSource() == v;

                BaryNode u = e.getBTarget();

                assert level.contains(u);

                //insert dummy node d in level i-1/i+1
                BaryDummyNode d = new BaryDummyNode(e, up ? i - 1 : i + 1);
                d.setLambdaRho(Math.min(v.getLambdaRho(), u.getLambdaRho())
                    + Math.abs((v.getLambdaRho() - u.getLambdaRho())) / 2);

                int insertPos = -1;

                lh.addNode(d, insertPos);
                newDummyNodes.add(d);
                e.setDummyNode(d);

                //insert dummy edge (d,v)/(v,d)
                BaryEdge dve;
                if(up) {
                    dve = lh.addDummyEdge(d, v);
                } else {
                    dve = lh.addDummyEdge(v, d);
                }

                e.setDummyEdgeLeft(dve);

                //insert dummy edge (d,u)/(u,d)
                BaryEdge due;
                if(up) {
                    due = lh.addDummyEdge(d, u);
                } else {
                    due = lh.addDummyEdge(u, d);
                }

                e.setDummyEdgeRight(due);
            }
        }

        LocalHierarchy.updatePositions(levelIMP);

        assert savedLevelIMP.size() <= levelIMP.size();
        assert savedLevelIMP.size() + newDummyNodes.size() == levelIMP.size();

        if(savedLevelIMP.size() < levelIMP.size()) {
            if(up) {
                bol(i - 1, lh.getNodes(), lh.getVertical());
            } else {
                bou(i + 1, lh.getNodes(), lh.getVertical());
            }

            //to guarantee, that the relative positions of the old nodes are unchanged,
            //the list of nodes is replaced after the sorting.
            lh.getNodes().set(up ? i - 1 : i + 1, savedLevelIMP);

            Collections.sort(newDummyNodes, new PositionComparator());
            for(Iterator it = newDummyNodes.iterator(); it.hasNext();) {
                BaryNode next = (BaryNode) it.next();
                savedLevelIMP.add(next.getPosition(), next);
            }

            LocalHierarchy.updatePositions(savedLevelIMP);

            assert savedLevelIMP.containsAll(levelIMP);
            assert levelIMP.containsAll(savedLevelIMP);
            assert savedLevelIMP.size() == levelIMP.size();
            return true;
        } else {
            return false;
        }
    }

    /**
     * inserts a new element into a list next to an existing element
     *
     * @param list DOCUMENT ME!
     * @param newNode DOCUMENT ME!
     * @param referringNode DOCUMENT ME!
     */
    private static void insertLeftOf(List list, SugiNode newNode,
        SugiNode referringNode) {
        if(list instanceof DLL) {
            ((DLL) list).addPred(referringNode, newNode);
        } else {
            list.add(list.indexOf(referringNode), newNode);
        }
    }

    /**
     * inserts a new element into a list next to an existing element
     *
     * @param list DOCUMENT ME!
     * @param newNode DOCUMENT ME!
     * @param referringNode DOCUMENT ME!
     */
    private static void insertRightOf(List list, SugiNode newNode,
        SugiNode referringNode) {
        if(list instanceof DLL) {
            ((DLL) list).addSucc(referringNode, newNode);
        } else {
            list.add(list.indexOf(referringNode) + 1, newNode);
        }
    }

    /**
     * the given list is assumed to be ordered by barrycenter values. inside a
     * group of nodes with the same barrycenter, the given node "v" will be
     * moved to the middle. if respectBary is false, then the given node is
     * moved to the middle in every case.
     *
     * @param v
     * @param list
     * @param respectBary DOCUMENT ME!
     */
    private static void moveVtoMiddle(SugiNode v, DLL list, boolean respectBary) {
        int distancePred = 0;
        int distanceSucc = 0;
        BaryNode pred = (BaryNode) list.getPredEl(v);
        BaryNode succ = (BaryNode) list.getSuccEl(v);
        while(pred != null
            && (!respectBary || pred.getBarryCenter() == v.getBarryCenter())) {
            pred = (BaryNode) list.getPredEl(pred);
            distancePred++;
        }
        while(succ != null
            && (!respectBary || succ.getBarryCenter() == v.getBarryCenter())) {
            succ = (BaryNode) list.getSuccEl(succ);
            distanceSucc++;
        }

        int move = (distancePred - distanceSucc) / 2;
        BaryNode rightNeighbour;
        if(move > 0) {
            rightNeighbour = v;
            while(move > 0) {
                rightNeighbour = (BaryNode) list.getPredEl(rightNeighbour);
                move--;
            }

            list.remove(v);
            list.addPred(rightNeighbour, v);
        } else if(move < 0) {
            rightNeighbour = (BaryNode) list.getSuccEl(v);
            while(move < 0) {
                rightNeighbour = (BaryNode) list.getSuccEl(rightNeighbour);
                move++;
            }

            list.remove(v);
            list.addPred(rightNeighbour, v);
        }
    }

    /**
     * sets the edgeBent flag for the bary delta in SugiNode to false
     *
     * @param coll DOCUMENT ME!
     */
    private static void resetDeltaFlag(Collection coll) {
        if(coll != null) {
            for(Iterator it = coll.iterator(); it.hasNext();) {
                ((SugiNode) it.next()).notifyEdgeBent(false);
            }
        }
    }

    //~ Inner Classes ==========================================================

    /**
     * compares two BaryNodes by their barycenter value
     */
    private static class BaryComparator implements Comparator {
        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         * @param arg1 DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int compare(Object arg0, Object arg1) {
            BaryNode n0 = (BaryNode) arg0;
            BaryNode n1 = (BaryNode) arg1;
            float result = n0.getBarryCenter() - n1.getBarryCenter();
            if(result < 0) {
                return -1;
            }

            if(result > 0) {
                return 1;
            }

            return 0;
        }
    }

    /**
     * compares two BaryNodes primarily by their lambdarho value, secondarily
     * by their barycenter values.
     */
    private static class LambdaBaryComparator implements Comparator {
        /**
         * nodes are primary compared by their lambdarho values, secondary by
         * their barryCenter values
         *
         * @param arg0 DOCUMENT ME!
         * @param arg1 DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int compare(Object arg0, Object arg1) {
            BaryNode n0 = (BaryNode) arg0;
            BaryNode n1 = (BaryNode) arg1;
            float result = (n1.getLambdaRho()) - (n0.getLambdaRho());
            if(result < 0) {
                return -1;
            }

            if(result > 0) {
                return 1;
            }

            result = n0.getBarryCenter() - n1.getBarryCenter();
            if(result < 0) {
                return -1;
            }

            if(result > 0) {
                return 1;
            }

            return 0;
        }
    }

    /**
     * compares two SortableNodes by their position attribute
     */
    private static class PositionComparator implements Comparator {
        /**
         * Creates a new PositionComparator object.
         */
        public PositionComparator() {
            super();
        }

        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         * @param arg1 DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int compare(Object arg0, Object arg1) {
            return ((SortableNode) arg0).getPosition()
            - ((SortableNode) arg1).getPosition();
        }
    }

    /**
     * stores information to restore local hierarchy after ordering
     */
    private static class UnDoExternal {
        public DummyNode dummyNode;
        public LHEdge edge;

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String toString() {
            return "[UDE:" + dummyNode + "," + edge + "]";
        }
    }

    /**
     * stores information to restore local hierarchy after ordering
     */
    private static class UnDoHorizontal {
        public BaryEdge proxyEdge;
        public DummyNode dummyNode;
        public LHEdge incoming;
        public LHEdge outgoing;
    }

    /**
     * stores information to restore local hierarchy after ordering
     */
    private static class UnDoToLocal {
        public DummyNode dummyNode;
        public LHEdge horizontalEdge;
        public LHEdge verticalEdge;

        //        public SugiNode markedNode;

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String toString() {
            return "[UDL:" + dummyNode + "," + horizontalEdge + ","
            + verticalEdge + "]";
        }
    }

    /**
     * stores information to restore local hierarchy after ordering
     */
    private static class UnDoVertical {
        public DummyNode dummyNode;
        public LHEdge horizontalEdge;
        public LHEdge verticalEdge;

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public String toString() {
            return "[UDV:" + dummyNode + "," + horizontalEdge + ","
            + verticalEdge + "]";
        }
    }
}
