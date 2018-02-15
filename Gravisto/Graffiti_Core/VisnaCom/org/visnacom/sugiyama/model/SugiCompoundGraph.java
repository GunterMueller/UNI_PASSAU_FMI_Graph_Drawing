/*==============================================================================
*
*   SugiCompoundGraph.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: SugiCompoundGraph.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

import java.util.*;

import org.visnacom.controller.Preferences;
import org.visnacom.model.*;
import org.visnacom.sugiyama.SugiyamaDrawingStyle;


/**
 * internal CompoundGraph datastructure of Sugiyama layout
 */
public class SugiCompoundGraph extends CompoundGraph {
    //~ Static fields/initializers =============================================

    /** constant for use in setWantedNodeType */
    public static final int SUGI_NODE = 0;

    /** constant for use in setWantedNodeType */
    public static final int DUMMY_NODE = 1;

    //~ Instance fields ========================================================

    /* the corresponding CompoundGraph object */
    private CompoundGraph correspondingGraph = null;

    /* key: original Edge; value: list of sugi edges */
    private HashMap edgeMapping = new HashMap();

    /* key: original Node; value:  SugiNode */
    private HashMap nodeMapping = new HashMap();

    /* to take over some metric parameters */
    private Preferences preferences;

    /* points to the node, that corresponds to the root in "correspondingGraph"
     * I use a dummy root, because of problems at expanding the root. */
    private SugiNode metricRoot;

    /* indicates wether the local hierarchies are activated.
     * if set to true, every graph-changing operation is reflected in the local
     * hierarchies. gets activated after normalization */
    private boolean lhActive = false;

    /*indicates whether all operations are reflected automatically in the mapping*/
    private boolean mappingAutomatic = false;
    private int drawingStyle = SugiyamaDrawingStyle.FINAL_STYLE;
    private int wantedNodeType = SUGI_NODE;

    //~ Constructors ===========================================================

    /**
     * Creates a new SugiCompoundGraph object. only for testing purposes!
     */
    public SugiCompoundGraph() {
        super();
        //        noCorrespondingGraph = true;
        edgeMapping = null;
        nodeMapping = null;
        metricRoot = (SugiNode) getRoot();
        setWantedNodeType(SUGI_NODE);
    }

    /**
     * Creates a new SugiCompoundGraph object, that is a clone of the given
     * CompoundGraph, but uses SugiNode and SugiEdge
     *
     * @param cg DOCUMENT ME!
     */
    public SugiCompoundGraph(CompoundGraph cg) {
        super();
        correspondingGraph = cg;
        setWantedNodeType(SUGI_NODE);
        assert getRoot() instanceof SugiNode;
        getRoot().setId(-1);
        ((SugiNode) getRoot()).isDummyRoot = true;
        //this is a trick to avoid NullPointerException at expanding the root
        metricRoot = (SugiNode) newLeaf(getRoot());

        establishNodeMapping(metricRoot, cg.getRoot());

        this.idCounter = cg.idCounter;
        //create new nodes
        insertSubTree(cg, cg.getRoot(), metricRoot);
        //fill adjLists
        for(Iterator it = cg.getAllEdgesIterator(); it.hasNext();) {
            Edge originalE = (Edge) it.next();
            transferEdge(originalE);
        }

        setWantedNodeType(DUMMY_NODE);
    }

    //~ Methods ================================================================

    /**
     * returns a list of nodes, that are children of the given nodes and lie on
     * the given sublevel
     *
     * @param i the sublevel
     * @param parents list of parents
     *
     * @return DOCUMENT ME!
     */
    public static List getChildrenAtSubLevel(int i, List parents) {
        //  works with local hierarchies so far.
        //would be possible without them nevertheless.
        List result = new LinkedList();
        for(Iterator it = parents.iterator(); it.hasNext();) {
            SugiNode parent = (SugiNode) it.next();
            result.addAll(parent.getChildrenAtLevel(i));
        }

        return result;
    }

    /**
     * returns the edge path, that corresponds to the given edge.
     *
     * @param originalEdge
     *
     * @return the edge path in this graph
     */
    public List getCorrespondingEdges(Edge originalEdge) {
        if(correspondingGraph == null) {
            return null;
        }

        assert !(originalEdge instanceof SugiEdge);

        List path = (List) edgeMapping.get(originalEdge);
        if(path != null) {
            assert ((List) edgeMapping.get(originalEdge)).size() > 0;
            assert ((SugiEdge) ((List) edgeMapping.get(originalEdge)).get(0))
            .getOriginalEdge() == originalEdge;
        }

        return path;
    }

    /**
     * usually, a sugicompound graph is created as a copy of some other
     * compound graph. a mapping is stored between the nodes of both graphs.
     *
     * @param v the node of the original graph
     *
     * @return a node of this graph, null if none exists
     */
    public SugiNode getCorrespondingNode(Node v) {
        if(correspondingGraph == null) {
            return null;
        }

        assert !(v instanceof SugiNode);

        SugiNode sn = (SugiNode) nodeMapping.get(v);
        assert sn == null || sn.getOriginalNode() == v;
        return sn;
    }

    /**
     * DOCUMENT ME!
     *
     * @param style must be SugiyamaDrawingStyle.DEBUG_STYLE or
     *        SugiyamaDrawingStyle.FINAL_STYLE
     */
    public void setDrawingStyle(int style) {
        drawingStyle = style;
    }

    /**
     * returns, how this graph should be drawn.
     *
     * @return DOCUMENT ME!
     */
    public int getDrawingStyle() {
        return drawingStyle;
    }

    /**
     * returns, whether the given edge leads from left to right. It is assumed,
     * that the edge is proper and causes a horizontal local hierarchy edge in
     * some ancestor
     *
     * @param edge DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isLeftToRight(SugiEdge edge) {
        SugiNode v = (SugiNode) edge.getSource();
        SugiNode u = (SugiNode) edge.getTarget();

        SugiNode paV = (SugiNode) getParent(v);
        SugiNode paU = (SugiNode) getParent(u);

        //only proper horizontal edges
        assert (paU.getClev().equals(paV.getClev()));
        assert Math.abs(v.getClev().getTail() - u.getClev().getTail()) <= 1;
        assert paV != paU;

        //traverse to nca
        while(paV != paU) {
            v = paV;
            u = paU;
            paV = (SugiNode) getParent(v);
            paU = (SugiNode) getParent(u);
        }

        return u.getPosition() > v.getPosition();
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     */
    public void setMappingAutomatic(boolean b) {
        mappingAutomatic = b;
    }

    /**
     * this method is to be used in MetricLayout, because the root of this
     * sugigraph is not necessarily the corresponding node to the root in the
     * original graph.
     *
     * @return DOCUMENT ME!
     */
    public SugiNode getMetricRoot() {
        return metricRoot;
    }

    /**
     * DOCUMENT ME!
     *
     * @param clev DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getNodesAtLevel(CompoundLevel clev) {
        assert lhActive; //works with local hierarchies so far.

        //would be possible without them nevertheless.
        Iterator it = clev.levelIterator();
        Integer front = (Integer) it.next();
        assert front.intValue() == 1;

        List nodes = new LinkedList();
        nodes.add(getRoot());
        while(it.hasNext()) {
            Integer i = (Integer) it.next();
            nodes = getChildrenAtSubLevel(i.intValue(), nodes);
        }

        return nodes;
    }

    /**
     * DOCUMENT ME!
     *
     * @param prefs DOCUMENT ME!
     */
    public void setPreferences(Preferences prefs) {
        preferences = prefs;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Preferences getPreferences() {
        return preferences;
    }

    /**
     * only for testing. usually, only dummy nodes are inserted into a
     * sugigraph
     *
     * @param type DOCUMENT ME!
     */
    public void setWantedNodeType(int type) {
        wantedNodeType = type;
    }

    /**
     * activates all local hierarchies. if wished, no edges are proccessed.
     *
     * @param withEdges DOCUMENT ME!
     */
    public void activateAllLHs(boolean withEdges) {
        lhActive = true;
        activateLHs((SugiNode) getRoot(), getAllEdges(), withEdges);
    }

    /**
     * activates the local hierarchies in the subtree rooted at the given node
     *
     * @param node the root of the subtree
     * @param edges a list of edges to process
     */
    public void activateLH(SugiNode node, List edges) {
        activateLHs(node, edges, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param originalEdge DOCUMENT ME!
     * @param newSegment DOCUMENT ME!
     * @param atFront DOCUMENT ME!
     */
    public void addToEdgeMapping(Edge originalEdge, SugiEdge newSegment,
        boolean atFront) {
        if(correspondingGraph == null) {
            return;
        }

        List edgepath = getCorrespondingEdges(originalEdge);
        newSegment.setOriginalEdge(originalEdge);
        if(atFront) {
            edgepath.add(0, newSegment);
        } else {
            edgepath.add(newSegment);
        }
    }

    /**
     * @see org.visnacom.model.CompoundGraph#changeSource(org.visnacom.model.Edge,
     *      org.visnacom.model.Node)
     */
    public void changeSource(Edge e, Node newSource) {
        if(lhActive) {
            toggleEdgeInLH((SugiEdge) e, false);
        }

        super.changeSource(e, newSource);
        if(lhActive) {
            toggleEdgeInLH((SugiEdge) e, true);
        }

        assert checkLHs(0);
    }

    /**
     * @see org.visnacom.model.CompoundGraph#changeTarget(org.visnacom.model.Edge,
     *      org.visnacom.model.Node)
     */
    public void changeTarget(Edge e, Node newTarget) {
        if(lhActive) {
            toggleEdgeInLH((SugiEdge) e, false);
        }

        super.changeTarget(e, newTarget);

        if(lhActive) {
            toggleEdgeInLH((SugiEdge) e, true);
        }

        assert checkLHs(0);
    }

    /**
     * only for debug
     *
     * @param edgePath DOCUMENT ME!
     * @param degree DOCUMENT ME!
     * @param printWarning DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean checkEdgeMapping(List edgePath, int degree,
        boolean printWarning) {
        if(correspondingGraph == null) {
            return true;
        }

        //tests, if the edgepath's first and last node are the end nodes of original edge
        //and all segment are in the graph
        Edge origE = ((SugiEdge) edgePath.get(0)).getOriginalEdge();
        assert origE != null;
        assert degree < 1 || correspondingGraph.getAllEdges().contains(origE);

        Node origNode1 =
            ((SugiNode) ((Edge) edgePath.get(0)).getSource()).getOriginalNode();
        Node origNode2 =
            ((SugiNode) ((Edge) edgePath.get(edgePath.size() - 1)).getTarget())
            .getOriginalNode();
        if(!((origE.getSource() == origNode1 && origE.getTarget() == origNode2)
            || (origE.getTarget() == origNode1
            && origE.getSource() == origNode2))) {
            if(printWarning) {
                System.err.println("Warning: original edge " + origE
                    + " has no complete edgepath " + edgePath);
            }

            if(degree == 1) {
                assert false;
            }
        }

        for(Iterator it2 = edgePath.iterator(); it2.hasNext();) {
            SugiEdge se = (SugiEdge) it2.next();
            if(!contains(se)) {
                if(degree == 1) {
                    assert false;
                } else {
                    if(printWarning) {
                        System.err.println("Warning: SugiEdge" + se
                            + "was not in graph,but in edgepath" + edgePath);
                    }
                }
            }
        }

        return true;
    }

    /**
     * only for debug purposes! all segments of edgepaths must be in the graph
     * 0 means only warn, if an segment has no original edge. warn, if an
     * edgepath ends not at the end nodes of the original edge.  1 means use
     * assert for all conditions
     *
     * @param degree DOCUMENT ME!
     * @param printWarning DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean checkEdgeMappings(int degree, boolean printWarning) {
        if(correspondingGraph == null) {
            return true;
        }

        //each edge of this graph must have an original edge and be part of
        //its edge path
        for(Iterator it = getAllEdgesIterator(); it.hasNext();) {
            SugiEdge e = (SugiEdge) it.next();
            Edge origE = e.getOriginalEdge();

            if(origE == null) {
                if(degree == 1) {
                    assert false;
                }

                if(printWarning) {
                    System.err.println("Warning: " + e
                        + " belongs to no edge path");
                }

                /* this should only happen to the edges (p_u,u) between
                 *  expand::normalization and expand::vertexordering
                 */
            } else {
                List edgepath = getCorrespondingEdges(origE);
                assert edgepath.contains(e);
            }
        }

        //tests, if the edgepath's first and last node are the end nodes of original edge
        //and all segment are in the graph
        for(Iterator it = edgeMapping.values().iterator(); it.hasNext();) {
            List edgepath = (List) it.next();
            checkEdgeMapping(edgepath, degree, printWarning);
        }

        //        if(printWarning) {
        //            System.out.println("end of checkEdgeMappings");
        //        }
        return true;
    }

    /**
     * only for debug
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean checkEdgeMappings(int i) {
        return checkEdgeMappings(i, false);
    }

    /**
     * for debug purposes. checks the consistence of all local hierarchies.
     *
     * @param degree indicates whether the lambdarho values should be correct,
     *        too.
     * @param printWarning DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean checkLHs(int degree, boolean printWarning) {
        return checkLHRec((SugiNode) getRoot(), degree, printWarning);
    }

    /**
     * only for debug
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean checkLHs(int i) {
        return checkLHs(i, false);
    }

    /**
     * for debug purposes only. is not efficient!
     *
     * @param edge DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean contains(SugiEdge edge) {
        List l = getEdge(edge.getSource(), edge.getTarget());
        int count = 0;
        for(Iterator it = l.iterator(); it.hasNext();) {
            Edge e = (Edge) it.next();
            if(e == edge) {
                count++;
            }
        }

        return count == 1 && getAllEdges().contains(edge);
    }

    /**
     * deactivates all local hierarchies.
     */
    public void deactivateAllLHs() {
        lhActive = false;
    }

    /**
     * @see org.visnacom.model.CompoundGraph#deleteLeaf(org.visnacom.model.Node)
     */
    public void deleteLeaf(Node leaf) {
        if(correspondingGraph != null && mappingAutomatic) {
            nodeMapping.remove(((SugiNode) leaf).getOriginalNode());
            assert !correspondingGraph.getAllNodes().contains(((SugiNode) leaf)
                .getOriginalNode());
        }

        if(lhActive) {
            SugiNode parent = (SugiNode) getParent(leaf);
            super.deleteLeaf(leaf);
            parent.notifyDeletedChild((SugiNode) leaf);
            assert checkLHs(0);
        } else {
            super.deleteLeaf(leaf);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param oldEdge DOCUMENT ME!
     */
    public void deleteMapping(Edge oldEdge) {
        if(correspondingGraph == null) {
            return;
        }

        assert edgeMapping.containsKey(oldEdge);
        edgeMapping.remove(oldEdge);
    }

    /**
     * deletes the subtree rooted at the given node.
     *
     * @param sn DOCUMENT ME!
     */
    public void deleteSubTree(SugiNode sn) {
        sn.setLHactive(false);

        //works on purpose with clone of children list
        for(Iterator it = getChildren(sn).iterator(); it.hasNext();) {
            SugiNode child = (SugiNode) it.next();
            deleteSubTree(child);
        }

        deleteLeaf(sn);
    }

    /**
     * inserts a new child at level i, if none exists.
     *
     * @param p_u the dummy node
     * @param i the wished level
     *
     * @return a child of p_u at level i
     */
    public DummyNode ensureDummyChild(DummyNode p_u, int i) {
        for(Iterator it = getChildrenIterator(p_u); it.hasNext();) {
            DummyNode child = (DummyNode) it.next();

            if(child.getClev().getTail() == i) {
                return child;
            }
        }

        assert lhActive; //so far, this method is only used after static layout

        //        if(lhActive) {
        DummyNode c = newDummyLeaf(p_u, i, DummyNode.UNKNOWN);
        return c;
        //        } else {
        //            DummyNode c = newDummyLeaf(p_u, i);
        //            return c;
        //        }
    }

    /**
     * sets both the entry in the hashmap from originaledge to the edgepath and
     * the attribute in sugiedge to the original edge
     *
     * @param originalEdge the edge in the original compound graph
     * @param list the edge path in the sugi compound graph
     */
    public void establishEdgeMapping(Edge originalEdge, List list) {
        if(correspondingGraph == null) {
            return;
        }

        assert !(originalEdge instanceof SugiEdge);
        edgeMapping.put(originalEdge, list);
        for(Iterator it = list.iterator(); it.hasNext();) {
            SugiEdge next = (SugiEdge) it.next();
            next.setOriginalEdge(originalEdge);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param e DOCUMENT ME!
     * @param ctovDash DOCUMENT ME!
     */
    public void establishEdgeMapping(Edge e, SugiEdge ctovDash) {
        if(correspondingGraph == null) {
            return;
        }

        List l = new LinkedList();
        l.add(ctovDash);
        establishEdgeMapping(e, l);
    }

    /**
     * @see org.visnacom.model.CompoundGraph#merge(org.visnacom.model.Node)
     */
    public void merge(Node innerNode) {
        throw new UnsupportedOperationException();
        //if this method is needed, check any side effects on LH's
    }

    /**
     * @see org.visnacom.model.CompoundGraph#moveNode(org.visnacom.model.Node, org.visnacom.model.Node)
     */
    public void moveNode(Node node, Node newParent) {
        throw new UnsupportedOperationException();
        //if this method is needed, check any side effects on LH's
    }

    /**
     * this version is preferred to be used. it inserts the new child into LH.
     *
     * @param parent the parent node
     * @param sublevel the wished compoundlevel for the new child
     * @param type the wished type; used in preprocessing of barycenter
     *        ordering
     *
     * @return DOCUMENT ME!
     */
    public DummyNode newDummyLeaf(SugiNode parent, int sublevel,
        DummyNode.Type type) {
        return newDummyLeaf(parent, sublevel, -1, type);
    }

    /**
     * DOCUMENT ME!
     *
     * @param pa the parent node
     * @param sublevel the level to assign the child to
     * @param i the position inside the level to insert the child
     * @param type used in preprocessing of barycenter ordering
     *
     * @return the new child
     */
    public DummyNode newDummyLeaf(SugiNode pa, int sublevel, int i,
        DummyNode.Type type) {
        DummyNode newNode = (DummyNode) super.newLeaf(pa);
        newNode.setClev(pa.getClev().getSubLevel(sublevel));
        newNode.setType(type);
        if(lhActive) {
            pa.addChildToLH(newNode, i);
            newNode.setLHactive(true);
            newNode.resetLambdaRho();
            assert checkLHs(0);
        }

        return newNode;
    }

    /**
     * DOCUMENT ME!
     *
     * @see org.visnacom.model.CompoundGraph#newEdge(org.visnacom.model.Node, org.visnacom.model.Node)
     */
    public Edge newEdge(Node source, Node target) {
        SugiEdge edge = (SugiEdge) super.newEdge(source, target);
        if(lhActive) {
            SugiNode paS = (SugiNode) getParent(source);
            SugiNode paT = (SugiNode) getParent(target);
            assert (paS.getClev().equals(paT.getClev()));

            //bei expand::normalization passiert es, dass lhActive gilt, aber
            //v.lhActive false ist,d.h. der call kommt hierher, obwohl ich
            //keinen eintrag in LH(v) will
            if(((SugiNode) source).getClev() != null
                && ((SugiNode) target).getClev() != null) {
                toggleEdgeInLH(edge, true);
            } else {
                //                System.out.println("Warning: new edge " + edge +
                //                    "was not inserted in LH");
            }

            assert checkLHs(0);
        }

        return edge;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @see org.visnacom.model.CompoundGraph#newLeaf(org.visnacom.model.Node) only to be used at
     *      beginning usually only dummy nodes are inserted into a
     *      sugicompoundgraph, except for the clone action at the beginning.
     * @deprecated
     */
    public Node newLeaf(Node parent) {
        assert parent instanceof SugiNode;

        SugiNode newNode = (SugiNode) super.newLeaf(parent);

        //this method should only be used at the beginning, before the lh's
        //gets initialized
        //assert !lhActive;
        assert !((SugiNode) parent).isLHActive();
        return newNode;
    }

    /**
     * @see org.visnacom.model.CompoundGraph#reverseEdge(org.visnacom.model.Edge)
     */
    public void reverseEdge(Edge edge) {
        super.reverseEdge(edge);
        if(lhActive) {
            //this method is not called so far in combination with LHs
            //in expand only local edges between ch(v) are reversed
            assert !((SugiNode) getParent(edge.getSource())).isLHActive();

            assert getParent(edge.getSource()) == getParent(edge.getTarget());
            assert checkLHs(0);
        }
    }

    /**
     * @see org.visnacom.model.CompoundGraph#split(java.util.List)
     */
    public Node split(List l) {
        throw new UnsupportedOperationException();
        //if this method is needed, check any side effects on LH's
    }

    /**
     * @see org.visnacom.model.CompoundGraph#splitGivenCluster(java.util.List,
     *      org.visnacom.model.Node, org.visnacom.model.Node)
     */
    public void splitGivenCluster(List l, Node par, Node clus) {
        throw new UnsupportedOperationException();
        //if this method is needed, check any side effects on LH's
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        String result = this.getClass().toString() + "\n";
        List nodes = getAllNodes();
        Collections.sort(nodes, new IdComparator());
        for(Iterator it = nodes.iterator(); it.hasNext();) {
            result += constructString((Node) it.next());
        }

        return result;
    }

    /**
     * inserts a SugiEdge into the graph that corresponds to the given edge
     *
     * @param e the original edge
     *
     * @return DOCUMENT ME!
     */
    public SugiEdge transferEdge(Edge e) {
        assert correspondingGraph != null;

        SugiNode source = getCorrespondingNode(e.getSource());
        SugiNode target = getCorrespondingNode(e.getTarget());
        SugiEdge se = (SugiEdge) newEdge(source, target);
        establishEdgeMapping(e, se);
        return se;
    }

    /**
     * inserts a new node in the sugicompoundgraph, that corresponds to the
     * given original node of the view
     *
     * @param originalNode the node to insert a corresponding suginode
     * @param originalParent the parent node of the original node
     *
     * @return DOCUMENT ME!
     */
    public SugiNode transferLeaf(Node originalNode, Node originalParent) {
        assert correspondingGraph != null;

        SugiNode parent = getCorrespondingNode(originalParent);
        setWantedNodeType(SUGI_NODE);

        SugiNode newNode = (SugiNode) newLeaf(parent);
        setWantedNodeType(DUMMY_NODE);
        establishNodeMapping(newNode, originalNode);
        return newNode;
    }

    /**
     * @see org.visnacom.model.CompoundGraph#deleteEdgeSupressDynBinding(org.visnacom.model.Edge)
     */
    protected void deleteEdgeSupressDynBinding(Edge edge) {
        super.deleteEdgeSupressDynBinding(edge);
        if(correspondingGraph != null && mappingAutomatic) {
            Edge origE = ((SugiEdge) edge).getOriginalEdge();
            if(origE != null) {
                List edgepath = (List) edgeMapping.get(origE);
                assert edgepath.contains(edge);
                edgepath.remove(edge);
                //System.out.println("removed from edgemapping" + edge);
                if(edgepath.isEmpty()) {
                    edgeMapping.remove(((SugiEdge) edge).getOriginalEdge());
                }
            }
        }

        if(lhActive) {
            toggleEdgeInLH((SugiEdge) edge, false);
        }
    }

    /**
     * @see org.visnacom.model.CompoundGraph#newEdgeFac(org.visnacom.model.Node, org.visnacom.model.Node)
     */
    protected Edge newEdgeFac(Node source, Node target) {
        return new SugiEdge(source, target);
    }

    /**
     * @see org.visnacom.model.CompoundGraph#newNodeFac()
     */
    protected Node newNodeFac() {
        switch(wantedNodeType) {
            case SUGI_NODE:
                return new SugiNode(idCounter++);

            case DUMMY_NODE:
                if(correspondingGraph != null) {
                    correspondingGraph.idCounter = idCounter + 1;
                }

                return new DummyNode(idCounter++);

            default:
                assert false;
                return super.newNodeFac();
        }
    }

    /**
     * here is the actual activation done. first the children are inserted.
     * then the edges are processed.
     *
     * @param node
     * @param edges a list of edges to insert in the local hierarchies.
     * @param withEdges usually true.
     */
    private void activateLHs(SugiNode node, List edges, boolean withEdges) {
        initializeChildrenInLHRec(node);

        if(!withEdges) {
            return;
        }

        //initialize edges
        for(Iterator it = edges.iterator(); it.hasNext();) {
            SugiEdge e = (SugiEdge) it.next();
            toggleEdgeInLH(e, true);
        }

        assert checkLHs(0);
    }

    /**
     * tests recursivly the local hierarchies of the subtree rooted at the
     * given node.
     *
     * @param n DOCUMENT ME!
     * @param degree DOCUMENT ME!
     * @param printWarning DOCUMENT ME!
     *
     * @return true, if no error found.
     */
    private boolean checkLHRec(SugiNode n, int degree, boolean printWarning) {
        boolean result = true;
        result = n.checkLH(this, n, degree, printWarning);

        //recursive call
        for(Iterator it = getChildrenIterator(n); it.hasNext();) {
            SugiNode child = (SugiNode) it.next();
            if(!checkLHRec(child, degree, printWarning)) {
                result = false;
            }
        }

        return result;
    }

    /**
     * used in the beginning.
     *
     * @param newNode DOCUMENT ME!
     * @param n DOCUMENT ME!
     */
    private void establishNodeMapping(SugiNode newNode, Node n) {
        if(correspondingGraph == null) {
            return;
        }

        nodeMapping.put(n, newNode);
        newNode.setOriginalNode(n);

        //only for debug
        newNode.setId(n.getId());
    }

    /**
     * all nodes are inserted in the LH of their parent. but no edges are processed.
     *
     * @param pa the parent node
     */
    private void initializeChildrenInLHRec(SugiNode pa) {
        List children = getChildren(pa);
        pa.setLHactive(true);
        //add children to local hierarchy
        pa.addChildrenToLH(children);
        //recursive call
        for(Iterator it = children.iterator(); it.hasNext();) {
            SugiNode child = (SugiNode) it.next();
            child.resetLambdaRho();
            initializeChildrenInLHRec(child);
        }
    }

    /**
     * used in initialising. copies the given subtree in the original compound
     * graph into this compound graph.
     *
     * @param cpg the original compound graph
     * @param oldRoot the root of the subtree in cpg.
     * @param newRoot the root of the subtree in this. Is a leaf at time of
     *        calling.
     */
    private void insertSubTree(CompoundGraph cpg, Node oldRoot, SugiNode newRoot) {
        for(Iterator it = cpg.getChildrenIterator(oldRoot); it.hasNext();) {
            Node oldNode = (Node) it.next();
            SugiNode newNode = (SugiNode) newLeaf(newRoot);
            establishNodeMapping(newNode, oldNode);
            insertSubTree(cpg, oldNode, newNode);
        }
    }

    /**
     * inserts or deletes a edge in the correct local hierarchy
     *
     * @param e the edge
     * @param insert if true, insert else delete.
     */
    private void toggleEdgeInLH(SugiEdge e, boolean insert) {
        SugiNode v = (SugiNode) e.getSource();
        SugiNode u = (SugiNode) e.getTarget();

        SugiNode paV = (SugiNode) getParent(v);
        SugiNode paU = (SugiNode) getParent(u);

        //traverse to nca
        while(paV != paU) {
            v = paV;
            u = paU;
            paV = (SugiNode) getParent(v);
            paU = (SugiNode) getParent(u);
        }

        if(insert) {
            LHEdge lhe = paV.ensureLHEdge(v, u, e);
            if(lhe != null) {
                if(lhe.isHorizontal()) {
                    //horizontal edges are only allowed between children of u and v
                    assert e.getSource() != lhe.getSource()
                    && e.getTarget() != lhe.getTarget();
                    assert isAncestor(lhe.getSource(), e.getSource());
                    assert isAncestor(lhe.getTarget(), e.getTarget());
                } else {
                    //handle multiple edges
                    //as all edges have been normalized, a vertical edge is only possible
                    //in the local hierarchy of its immediate parent
                    assert e.getSource() == lhe.getSource();
                    assert e.getTarget() == lhe.getTarget();
                }
            }
        } else {
            paV.notifyDeletedEdgeInLH(v, u, e);
        }
    }

    //~ Inner Classes ==========================================================

    /**
     * only for debug purposes. sorts the nodes by Id.
     */
    private static class IdComparator implements Comparator {
        /**
         * DOCUMENT ME!
         *
         * @param arg0 DOCUMENT ME!
         * @param arg1 DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public int compare(Object arg0, Object arg1) {
            return ((Node) arg0).getId() - ((Node) arg1).getId();
        }
    }
}
