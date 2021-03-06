/*==============================================================================
*
*   LocalHierarchy.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: LocalHierarchy.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.visnacom.model.DLL;
import org.visnacom.model.Node;

/**
 * implementation of local hierarchies
 */
public class LocalHierarchy {
    //~ Instance fields ========================================================

    /** contains horizontal edges */
    HashMap horizontal = new LinkedHashMap();

    /** contains vertical edges, all point downwards */
    private HashMap vertical = new LinkedHashMap();

    /**
     * kind of matrix. contains only as much levels as needed (except the level
     * 0)
     */
    private List nodes = new ArrayList();

    /*indicates whether the horizontal edges have been deactivated in this local
     * hierarchy */
    private boolean horizontalDeactivated = false;

    //~ Constructors ===========================================================

    /**
     * Creates a new LocalHierarchy object.
     */
    public LocalHierarchy() {}

    //~ Methods ================================================================

    /**
     * Returns the internal datastructure that contains the horizontal edges.
     * violates object-oriented principles, but I wanted to keep the algorithm
     * for vertex ordering separated from the data structure LocalHierarchy
     *
     * @return DOCUMENT ME!
     */
    public HashMap getHorizontal() {
        return horizontal;
    }

    /**
     * auxiliary method. creates an adjacency list of incoming edges. the edge
     * of a node will be sorted be source node according to their position in
     * the node matrix.
     *
     * @param nodes the node matrix.
     * @param outgoing the adjacency list of outgoing edges.
     *
     * @return the Map containing Lists with the incoming edges for each node
     */
    public static Map createIncomingMap(Collection nodes, Map outgoing) {
        Map incoming = new LinkedHashMap();
        for(Iterator it = new IteratorOfCollections(nodes); it.hasNext();) {
            Object node = it.next();

            //outgoing.put(node, new LinkedList());
            assert outgoing.containsKey(node);
            incoming.put(node, new LinkedList());
        }

        for(Iterator it = new IteratorOfCollections(nodes); it.hasNext();) {
            SugiNode nexts = (SugiNode) it.next();
            for(Iterator it2 = ((List) outgoing.get(nexts)).iterator();
                it2.hasNext();) {
                BaryEdge nextE = (BaryEdge) it2.next();
                List incomingEdges = (List) incoming.get(nextE.getBTarget());
                incomingEdges.add(nextE);
            }
        }

        return incoming;
    }

    /**
     * DOCUMENT ME!
     *
     * @param node DOCUMENT ME!
     *
     * @return an iterator over the incident horizontal edges of the given node
     */
    public Iterator getHorizontalEdgesIterator(BaryNode node) {
        return ((List) horizontal.get(node)).iterator();
    }

    /**
     * only for debug purposes. checks, whether the position attribute of all
     * nodes is set correct.
     *
     * @param nodes a list of lists containing the nodes
     *
     * @return DOCUMENT ME!
     */
    public static boolean checkPositions(List nodes) {
        boolean result = true;
        for(Iterator it = nodes.iterator(); it.hasNext();) {
            List list = (List) it.next();
            int i = 0;
            for(Iterator it2 = list.iterator(); it2.hasNext(); i++) {
                BaryNode next = (BaryNode) it2.next();
                if(next.getPosition() != i) {
                    result = false;
                    assert false;
                }
            }
        }

        return result;
    }

    /**
     * returns a cloned list of a certain level
     *
     * @param i the level number
     *
     * @return a clone of the level
     */
    public List getNodesAtLevelClone(int i) {
        List level = getNodesAtLevel(i);
        if(level instanceof DLL) {
            return new DLL((DLL) level);
        } else {
            return new LinkedList(level);
        }
    }

    /**
     * returns an iterator of the incident vertical edges of a node
     *
     * @param node DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Iterator getVerticalEdgesIterator(BaryNode node) {
        return ((List) vertical.get(node)).iterator();
    }

    /**
     * inserts a dummy edge between source and target
     *
     * @param source DOCUMENT ME!
     * @param target DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public BaryEdge addDummyEdge(BaryNode source, BaryNode target) {
        BaryEdge e = new BaryDummyEdge(source, target);
        addGivenEdge(e);
        return e;
    }

    /**
     * this method is only to be used in barycenter ordering. adds an already
     * existing edge to the local hierarchy.
     *
     * @param edge DOCUMENT ME!
     */
    public void addGivenEdge(BaryEdge edge) {
        int levs = edge.getBSource().getLevel();
        int levt = edge.getBTarget().getLevel();
        assert levs == levt || levs + 1 == levt;

        List edges;
        if(levs < levt) {
            edges = (List) vertical.get(edge.getBSource());
        } else {
            edges = (List) horizontal.get(edge.getBSource());
        }

        edges.add(edge);
    }

    /**
     * adds the given node at the end of the appropriate level.
     *
     * @param n DOCUMENT ME!
     *
     * @deprecated only for testing
     */
    public void addNode(BaryNode n) {
        addNode(n, -1);
    }

    /**
     * insert a node into its level. the compoundlevel of the given node is
     * presumed to be set.
     *
     * @param n the node to insert
     * @param i the wished position, -1 indicates the end of the level
     */
    public void addNode(BaryNode n, int i) {
        assert !contains(n);
        //create entry in vertical hashmap and horizontal
        registerNode(n);

        //ensure that the list is long enough
        // e.g. (lev = 10) ==> list.size() must be 11
        int lev = n.getLevel();

        while(lev >= nodes.size()) {
            nodes.add(getLevelType());
        }

        //        assert (checkSiblings(n.getClev()));
        //add node and update position-value!
        if(i == -1 || i == ((List) nodes.get(lev)).size()) {
            ((List) nodes.get(lev)).add(n);
            n.setPosition(((List) nodes.get(lev)).size() - 1);
            assert ((List) nodes.get(n.getLevel())).get(n.getPosition()) == n;
        } else {
            assert i < ((List) nodes.get(lev)).size();
            ((List) nodes.get(lev)).add(i, n);
            updatePositions((List) nodes.get(lev));
        }
    }

    /**
     * adds all given nodes to their appropriate level
     *
     * @param nodes DOCUMENT ME!
     */
    public void addNodes(List nodes) {
        for(Iterator it = nodes.iterator(); it.hasNext();) {
            BaryNode next = (BaryNode) it.next();
            addNode(next, -1);
        }
    }

    /**
     * this method is only to be used in barycenter ordering. it modifies
     * temporarily the source node of an edge. the real source is stored in
     * the edge for undoing. the new source must lie on the same level as the
     * old one. it can only be called on LHEdges and SugiNodes.
     *
     * @param edge the edge to bend
     * @param source the new source node
     */
    public void bendSourceVertical(LHEdge edge, SugiNode source) {
        //assert contains(source);
        assert edge.getBSource().getLevel() == source.getLevel();
        ((List) vertical.get(edge.getBSource())).remove(edge);
        //        edge.storeOrigSource();
        edge.bendSource(source);
        //        se2.notifySourceBent(true);
        //        edge.setBSource(source);
        ((List) vertical.get(source)).add(edge);
    }

    /**
     * this method is only to be used in barycenter ordering
     *
     * @param edge DOCUMENT ME!
     * @param target DOCUMENT ME!
     */
    public void bendTargetVertical(LHEdge edge, SugiNode target) {
        //assert contains(target);
        assert edge.getBSource().getLevel() == target.getLevel() - 1;
        //        edge.storeOrigTarget();
        edge.bendTarget(target);
        //        edge.setBTarget(target);
    }

    /**
     * only for debug purposes. performs some checks on conditions.
     *
     * @param s DOCUMENT ME!
     * @param v DOCUMENT ME!
     * @param degree 0 means, the nodes and the vertical edges must be correct
     *        in all activated LHs. 1 means, all LHs must be activated, the
     *        lambdarhovalues in all LHs must be correct,too. 2 means,
     *        additionally the horizontal edges must be deleted and
     *        deactivated in all LHs and all LHs must be activated.
     * @param printWarning DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean checkConsistence(SugiCompoundGraph s, SugiNode v,
        int degree, boolean printWarning) {
        //position attribute korrekt?
        boolean result = checkPositions();
        assert result;
        assert checkSiblings(v.getClev());

        for(Iterator it = nodes.iterator(); it.hasNext();) {
            Collection next = (Collection) it.next();
            if(next instanceof DLL) {
                ((DLL) next).checkConsistency();
            }
        }

        for(Iterator it = new IteratorOfCollections(nodes); it.hasNext();) {
            SugiNode sn = (SugiNode) it.next();
            if(vertical.get(sn) instanceof DLL) {
                ((DLL) vertical.get(sn)).checkConsistency();
            }

            if(horizontal.get(sn) instanceof DLL) {
                ((DLL) horizontal.get(sn)).checkConsistency();
            }
        }

        //alle kinder sind in local hierarchy
        for(Iterator it = s.getChildrenIterator(v); it.hasNext();) {
            if(!this.contains((SugiNode) it.next())) {
                result = false;
                assert false;
            }
        }

        //alle knoten in der local hierarchy sind kinder
        for(Iterator it = new IteratorOfCollections(nodes); it.hasNext();) {
            if(!s.getChildren(v).contains(it.next())) {
                result = false;
                assert false;
            }
        }

        //jeder knoten nur einmal enthalten
        HashSet visited = new HashSet(s.getNumOfNodes());
        for(Iterator it = new IteratorOfCollections(nodes); it.hasNext();) {
            Object next = it.next();
            if(visited.contains(next)) {
                assert false;
                result = false;
            } else {
                visited.add(next);
            }
        }

        //alle vertikale kanten der local hierarchy sind wirklich kanten
        for(Iterator it = new IteratorOfCollections(vertical.values());
            it.hasNext();) {
            LHEdge lhe = (LHEdge) it.next();
            List edges = s.getEdge(lhe.getSource(), lhe.getTarget());
            if(edges.size() != 1) {
                //moment,mehrfachkanten
                if(!edges.containsAll(lhe.getOriginalEdges())
                    || !lhe.getOriginalEdges().containsAll(edges)) {
                    result = false;
                    assert false;
                }
            }

            if(lhe.getMultiplicity() != edges.size()) {
                result = false;
                assert false;
            }

            if(!edges.contains(lhe.getOriginalEdges().get(0))) {
                result = false;
                assert false;
            }
        }

        //alle kanten von kindern von v, wenn intern, sind in dieser
        // localhierarchy
        for(Iterator it = s.getChildrenIterator(v); it.hasNext();) {
            for(Iterator it2 = s.getAdjEdges((Node) it.next()).iterator();
                it2.hasNext();) {
                SugiEdge se = (SugiEdge) it2.next();
                if(!s.getChildren(v).contains(se.getSource())
                    || !s.getChildren(v).contains(se.getTarget())) {
                    continue;
                }

                int levs = ((SugiNode) se.getSource()).getClev().getTail();
                int levt = ((SugiNode) se.getTarget()).getClev().getTail();

                //alle kanten normalisiert
                if(levs + 1 == levt) {
                    LHEdge lhe =
                        (LHEdge) getEdge((SugiNode) se.getSource(),
                            (SugiNode) se.getTarget());

                    //alle kanten haben localhierarchy edge?
                    if(lhe != null) {
                        //diese localhierarchy edge enthaelt "se"?
                        if(!lhe.getOriginalEdges().contains(se)) {
                            result = false;
                            assert false;
                        }
                    } else {
                        result = false;
                        assert false;
                    }
                } else {
                    result = false;
                    assert false;
                }
            }
        }

        if(degree >= 1) {
            assert degree == 1
            || (horizontalDeactivated
            && !(new IteratorOfCollections(horizontal.values())).hasNext());
            //are the lambda rho values correct?
            for(Iterator it = new IteratorOfCollections(nodes); it.hasNext();) {
                SugiNode node = (SugiNode) it.next();
                int lambda = 0;
                int rho = 0;

                //get all descendants
                List l1 = s.allDescendants(node);
                l1.add(node);
                //try all other nodes
                for(Iterator it2 = l1.iterator(); it2.hasNext();) {
                    SugiNode desc = (SugiNode) it2.next();
                    for(Iterator it3 = s.getAdjEdges(desc).iterator();
                        it3.hasNext();) {
                        SugiEdge e = (SugiEdge) it3.next();
                        SugiNode otherNode = getOtherNode(e, desc);
                        if(!s.isAncestor(v, otherNode)) {
                            assert !s.isAncestor(otherNode, node);

                            Stack s1 = new Stack();
                            Stack s2 = new Stack();

                            SugiNode parent1 = desc;
                            while(parent1 != null) {
                                s1.push(parent1);
                                parent1 = (SugiNode) s.getParent(parent1);
                            }

                            SugiNode parent2 = otherNode;
                            while(parent2 != null) {
                                s2.push(parent2);
                                parent2 = (SugiNode) s.getParent(parent2);
                            }

                            SugiNode nca = (SugiNode) s1.pop();
                            s2.pop();

                            SugiNode childOfNca1 = (SugiNode) s1.pop();
                            SugiNode childOfNca2 = (SugiNode) s2.pop();
                            assert nca == s.getRoot();
                            while(childOfNca1 == childOfNca2) {
                                nca = childOfNca1;
                                childOfNca1 = (SugiNode) s1.pop();
                                childOfNca2 = (SugiNode) s2.pop();
                            }

                            assert s.isAncestor(nca, v);
                            assert childOfNca1.getClev().equals(childOfNca2
                                .getClev());
                            if(childOfNca1.getPosition() < childOfNca2
                                .getPosition()) {
                                rho++;
                            } else if(childOfNca1.getPosition() > childOfNca2
                                .getPosition()) {
                                lambda++;
                            } else {
                                assert false;
                            }
                        }
                    }
                }

                if(node.getLambda() != lambda) {
                    if(printWarning) {
                        System.err.println("Warning: " + node
                            + "should have lambda(" + lambda + "),rho(" + rho
                            + ")");
                    }
                }

                if(node.getRho() != rho) {
                    if(printWarning) {
                        System.err.println("Warning: " + node
                            + "should have lambda(" + lambda + "),rho(" + rho
                            + ")");
                    }
                }
            }
        }

        return result;
    }

    /**
     * only for debug
     *
     * @return DOCUMENT ME!
     */
    public boolean checkPositions() {
        return LocalHierarchy.checkPositions(nodes);
    }

    /**
     * for debug only!
     *
     * @param edge DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean contains(SugiEdge edge) {
        boolean found = false;
        List edges = (List) vertical.get(edge.getSource());
        for(Iterator it = edges.iterator(); it.hasNext();) {
            LHEdge lhe = (LHEdge) it.next();
            if(lhe.getTarget() == edge.getTarget()) {
                List origEdges = lhe.getOriginalEdges();
                if(origEdges.contains(edge)) {
                    found = true;
                    break;
                }
            }
        }

        List horEdges = (List) horizontal.get(edge.getSource());
        boolean found2 = false;
        for(Iterator it = horEdges.iterator(); it.hasNext();) {
            LHEdge lhe = (LHEdge) it.next();
            if(lhe.getTarget() == edge.getTarget()) {
                List origEdges = lhe.getOriginalEdges();
                if(origEdges.contains(edge)) {
                    found2 = true;
                    break;
                }
            }
        }

        assert !(found && found2);
        return found || found2;
    }

    /**
     * only for debug purposes.
     *
     * @param node DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean contains(BaryNode node) {
        int level = node.getLevel();
        assert level >= 0;
        return (nodes.size() > level)
        && ((List) nodes.get(level)).contains(node);
    }

    /**
     * for debug only
     *
     * @param nodes DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean containsAny(List nodes) {
        for(Iterator it = nodes.iterator(); it.hasNext();) {
            if(contains((BaryNode) it.next())) {
                return true;
            }
        }

        return false;
    }

    /**
     * is used only during Vertex ordering. deletes an edge from the local
     * hierarchy without any further actions.
     *
     * @param edge DOCUMENT ME!
     */
    public void deleteGivenEdge(BaryEdge edge) {
        int levs = edge.getBSource().getLevel();
        int levt = edge.getBTarget().getLevel();
        assert levs == levt || levs + 1 == levt;

        List edges;
        if(levs < levt) {
            edges = (List) vertical.get(edge.getBSource());
        } else {
            edges = (List) horizontal.get(edge.getBSource());
        }

        assert edges.contains(edge);
        edges.remove(edge);
    }

    /**
     * deletes the given node and removes its entries in down and horizontal
     * hashmap and therefore all outgoing edges. all incoming must be deleted
     * elsewhere. the flag indicates whether the position attributes should be
     * updated.
     *
     * @param node the node to delete
     * @param updatePositions if true, position attributes of the remaining
     *        nodes in the same level are updated.
     */
    public void deleteNode(BaryNode node, boolean updatePositions) {
        vertical.remove(node);
        horizontal.remove(node);

        List level = (List) nodes.get(node.getLevel());
        assert level.contains(node);
        level.remove(node);

        if(updatePositions) {
            updatePositions(level);
            trimNodeList();
        }
    }

    /**
     * should be called after vertex ordering in static layout to indicate,
     * that the horizontal LHEdges are not needed anymore, and therefore have
     * not to be kept consistent at further changes to the graph, e.g. during
     * expand
     */
    public void discardHorizontalEdges() {
        for(Iterator it = horizontal.values().iterator(); it.hasNext();) {
            List next = (List) it.next();
            next.clear();
        }

        /*
         * 25.04 ich hab das loeschen wieder rein, weil sonst analyseLambdaRho
         * die alten kanten beim expand mitmacht
         */
        horizontalDeactivated = true;
    }

    /**
     * the single possibility from outside to add an edge to the local
     * hierarchy. prevents multiple edges.
     *
     * @param source the wished source of the local hierarchy edge. is an
     *        ancestor of the given edge's source.
     * @param target
     *
     * @return the edge object between the two given nodes
     */
    public LHEdge ensureEdge(SugiNode source, SugiNode target) {
        LHEdge result = (LHEdge) getEdge(source, target);

        if(result != null) {
            return result;
        } else {
            return addEdge(source, target);
        }
    }

    /**
     * this is done at LH(pa(v)), when temporarily horizontal edges are needed
     * because of the edges (v_i,c_i)
     */
    public void reactivateHorizontalEdges() {
        assert horizontalDeactivated
        || !(new IteratorOfCollections(horizontal.values())).hasNext();
        horizontalDeactivated = false;
    }

    /**
     * creates entries in the adjLists for the given node, but does not insert
     * it into the matrix. is in the first place a help method for addNode,
     * but is also used during vertex ordering
     *
     * @param node DOCUMENT ME!
     */
    public void registerNode(BaryNode node) {
        assert !contains(node);
        assert !vertical.containsKey(node);
        assert !horizontal.containsKey(node);
        vertical.put(node, getAdjListType());
        horizontal.put(node, getAdjListType());
    }

    /**
     * undoes the bending in method "bendSourceVertical"
     *
     * @param edge DOCUMENT ME!
     * @param origSource DOCUMENT ME!
     */
    public void restoreOrigSource(LHEdge edge, DummyNode origSource) {
        ((List) vertical.get(edge.getSource())).remove(edge);
        edge.restoreSource();
        assert edge.getSource() == origSource;

        ((List) vertical.get(edge.getSource())).add(edge);
    }

    /**
     * DOCUMENT ME!
     *
     * @param edge DOCUMENT ME!
     * @param origTarget DOCUMENT ME!
     */
    public void restoreOrigTarget(LHEdge edge, DummyNode origTarget) {
        edge.restoreTarget();
        assert edge.getTarget() == origTarget;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String result = "[";
        for(Iterator it = nodes.iterator(); it.hasNext();) {
            List level = (List) it.next();
            result += "[";
            for(Iterator it2 = level.iterator(); it2.hasNext();) {
                Object next = it2.next();
                result += next;
                result += " hor: ";
                for(Iterator it3 = ((List) horizontal.get(next)).iterator();
                    it3.hasNext();) {
                    Object e = it3.next();
                    result += e;
                }

                result += " ver: ";
                for(Iterator it3 = ((List) vertical.get(next)).iterator();
                    it3.hasNext();) {
                    Object e = it3.next();
                    result += e;
                }

                result += "\n";
            }

            result += "]\n";
        }

        result += "]";

        return result;
    }

    /**
     * deletes the levels at the lower end, if they are empty
     */
    public void trimNodeList() {
        while(!nodes.isEmpty()
            && ((List) nodes.get(nodes.size() - 1)).isEmpty()) {
            nodes.remove(nodes.size() - 1);
        }
    }

    /**
     * updates the position attribute of all nodes in the local hierarchy
     */
    public void updatePositions() {
        int size = nodes.size();
        for(int i = 0; i < size; i++) {
            updatePositions(getNodesAtLevel(i));
        }
    }

    /**
     * updates the position-attributes in the given list
     *
     * @param list the list to update
     */
    public static void updatePositions(List list) {
        int i = 0;
        int size = list.size();
        for(Iterator it = list.iterator(); i < size; i++) {
            BaryNode next = (BaryNode) it.next();
            next.setPosition(i);
        }
    }

    /**
     * Returns the internal datastructure that contains the nodes. violates
     * ideas of object-orientation, but I wanted to keep the algorithm for
     * vertex ordering separated from the data structure, so I needed the
     * access.
     *
     * @return DOCUMENT ME!
     */
    public List getNodes() {
        return nodes;
    }

    /**
     * returns all nodes at the specified level
     *
     * @param level DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getNodesAtLevel(int level) {
        if(level < nodes.size()) {
            return (List) nodes.get(level);
        } else {
            return getLevelType();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return the number of levels
     */
    public int getNumberOfLevels() {
        return nodes.size();
    }

    /**
     * Returns the internal datastructure that contains the vertical edges.
     *
     * @return DOCUMENT ME!
     */
    public HashMap getVertical() {
        return vertical;
    }

    /**
     * updates the position values of the nodes of one level
     *
     * @param level the index of the level
     */
    public void updatePositions(int level) {
        updatePositions(getNodesAtLevel(level));
    }

    /**
     * Returns an Iterator over all horizontal edges. Should be the common way
     * to access the edges, not getHorizontal()
     *
     * @return Returns an Iterator over all horizontal edges
     */
    Iterator horizontalEdgesIterator() {
        return new IteratorOfCollections(horizontal.values());
    }

    /**
     * should be called, when an edge is deleted from the sugi graph. there is
     * a little complication with horizontal edges. As they are only present
     * temporarily and it is refused to insert a horizontal edge, after the
     * flag 'horizontalDeactivated' is set. They need not to be present in the
     * local hierarchy any more.
     *
     * @param source DOCUMENT ME!
     * @param target DOCUMENT ME!
     * @param edge
     */
    void notifyDeletedEdge(SugiNode source, SugiNode target, SugiEdge edge) {
        assert contains(source);
        assert contains(target);

        int levs = source.getLevel();
        int levt = target.getLevel();

        //at vertical edges the situation is easy, they have to be present
        //at calling this method
        if(levs < levt) {
            assert levs + 1 == levt;
            assert (edge.getSource() == source && edge.getTarget() == target);

            List edges = (List) vertical.get(source);
            for(Iterator it = edges.iterator(); it.hasNext();) {
                LHEdge lhe = (LHEdge) it.next();
                if(lhe.getTarget() == target) {
                    List origEdges = lhe.getOriginalEdges();
                    assert origEdges.contains(edge);
                    origEdges.remove(edge);
                    if(origEdges.isEmpty()) {
                        it.remove();
                    }

                    return;
                }
            }

            assert false; //edge not found
            System.err.println("WARNING: edge not found in LocalHierarchy");
        } else if(levs == levt) {
            //at the moment horizontal edges are not present at the time of
            // calling this, because this case is only used by
            // bendTarget,bendSource
            //during splitDummyNodesOnPath in expand
            //if this case is needed at some time, test it.
            List horEdges = (List) horizontal.get(source);
            boolean found2 = false;
            for(Iterator it = horEdges.iterator(); it.hasNext();) {
                LHEdge lhe = (LHEdge) it.next();
                if(lhe.getTarget() == target) {
                    List origEdges = lhe.getOriginalEdges();
                    assert origEdges.contains(edge);
                    origEdges.remove(edge);
                    if(origEdges.isEmpty()) {
                        it.remove();
                    }

                    found2 = true;
                    break;
                }
            }

            assert !found2;
            assert horizontalDeactivated;
        }
    }

    /**
     * returns empty list, that should be used in this data structure.
     *
     * @return DOCUMENT ME!
     */
    private List getAdjListType() {
        //        return new LinkedList();
        return new DLL(); //the final implementation uses DLL
    }

    /**
     * DOCUMENT ME!
     *
     * @param source DOCUMENT ME!
     * @param target DOCUMENT ME!
     *
     * @return the edge, that connects source and target, is null if no such
     *         edge exists
     */
    private BaryEdge getEdge(BaryNode source, BaryNode target) {
        assert contains(source) && contains(target);

        int levs = source.getLevel();
        int levt = target.getLevel();
        assert levs == levt || levs + 1 == levt;

        List edges;
        if(levs < levt) {
            edges = (List) vertical.get(source);
        } else {
            edges = (List) horizontal.get(source);
        }

        for(Iterator it = edges.iterator(); it.hasNext();) {
            BaryEdge e = (BaryEdge) it.next();
            if(e.getBTarget() == target) {
                return e;
            }
        }

        return null;
    }

    /**
     * the type of lists, that used at the moment
     *
     * @return DOCUMENT ME!
     */
    private List getLevelType() {
        return new DLL();
        //            return new LinkedList();
    }

    /**
     * for debug purposes only!
     *
     * @param e DOCUMENT ME!
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static SugiNode getOtherNode(SugiEdge e, SugiNode n) {
        if(e.getTarget() == n) {
            return (SugiNode) e.getSource();
        } else {
            assert e.getSource() == n;
            return (SugiNode) e.getTarget();
        }
    }

    /**
     * inserts a new LHEdge in this local hierarchy and returns it. is private
     * to prevent multiple edges. use "ensureEdge" instead.
     *
     * @param source DOCUMENT ME!
     * @param target DOCUMENT ME!
     *
     * @return the created LHEdge object
     */
    private LHEdge addEdge(SugiNode source, SugiNode target) {
        assert contains(source);
        assert contains(target);

        int levs = source.getLevel();
        int levt = target.getLevel();
        assert levs == levt || levs + 1 == levt;

        List edges;
        if(levs < levt) {
            edges = (List) vertical.get(source);
        } else {
            if(horizontalDeactivated) {
                //just do nothing
                //System.out.println("horizontal edge (" + source.getId() + ","
                // +
                //      target.getId() + ") not inserted in deactivatedHoriz");
                return null;
            }

            edges = (List) horizontal.get(source);
        }

        LHEdge e = new LHEdge(source, target);

        edges.add(e);
        return e;
    }

    /**
     * only for debug purposes. checks whether the given level and the clev of
     * all nodes is consistent
     *
     * @param clevParent DOCUMENT ME!
     *
     * @return whether all conditions hold
     */
    private boolean checkSiblings(CompoundLevel clevParent) {
        boolean result = true;
        int lev = 0;
        for(Iterator it = nodes.iterator(); it.hasNext();) {
            List l = (List) it.next();
            for(Iterator it2 = l.iterator(); it2.hasNext();) {
                SugiNode next = (SugiNode) it2.next();
                if(!(next.getClev().isSubLevelOf(clevParent))) {
                    assert false;
                    result = false;
                }
            }

            lev++;
        }

        return result;
    }
}
