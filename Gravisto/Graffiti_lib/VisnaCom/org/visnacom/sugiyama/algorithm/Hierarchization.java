/*==============================================================================
*
*   Hierarchization.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: Hierarchization.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.algorithm;

import java.util.*;

import org.visnacom.model.*;
import org.visnacom.sugiyama.model.*;


/**
 * provides static methods, which implement step 1 of the static layout and
 * expansion
 */
public class Hierarchization {
    //~ Methods ================================================================

    /**
     * only for debug purposes:  checks, whether the given list of nodes only
     * contains EQLESS edges
     *
     * @param next DOCUMENT ME!
     * @param d DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @deprecated
     */
    public static boolean isGoodCycle(List next, DerivedGraph d) {
        for(Iterator it = next.iterator(); it.hasNext();) {
            Node n = (Node) it.next();
            for(Iterator it2 = d.getOutEdgesIterator(n); it2.hasNext();) {
                Object o = it2.next();
                DerivedEdge e = (DerivedEdge) o;
                if(e.isIntern() && !next.contains(e.getTarget())) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * see pseudocode  assign clevs to all nodes and all children of them in
     * the given list. method is only public because of debug purposes
     *
     * @param derivedGraph DOCUMENT ME!
     * @param w DOCUMENT ME!
     */
    public static void compLevelAssign(DerivedGraph derivedGraph, List w) {
        int maxlev = localLevelAssign(derivedGraph, w);

        /*construct all Z's in one run*/

        //initialize array
        List[] listOfZ = new List[maxlev];
        for(int i = 0; i < listOfZ.length; i++) {
            listOfZ[i] = new LinkedList();
        }

        //fill array
        for(Iterator it = w.iterator(); it.hasNext();) {
            SugiNode z = (SugiNode) it.next();
            listOfZ[z.getClev().getTail() - 1].addAll(derivedGraph.getChildren(
                    z));
        }

        //recursive call for each level
        for(int i = 0; i < listOfZ.length; i++) {
            if(!listOfZ[i].isEmpty()) {
                compLevelAssign(derivedGraph, listOfZ[i]);
            }
        }
    }

    /**
     * implementation of Algorithm "CreateDerivedGraph"
     *
     * @param sugigraph the compoundgraph D
     *
     * @return a derived graph with the same nodes as the compound graph and
     *         derived edges
     */
    public static DerivedGraph createDerivedGraph(SugiCompoundGraph sugigraph) {
        //create derived graph with all nodes of the compoundgraph
        DerivedGraph dg = new DerivedGraph(sugigraph);

        for(Iterator it = sugigraph.getAllEdgesIterator(); it.hasNext();) {
            Edge e = (Edge) it.next();
            Node source = e.getSource();
            Node target = e.getTarget();

            if(sugigraph.inclusionDepth(source).intValue() != sugigraph.inclusionDepth(
                    target).intValue()) {
                //ensure that source and target lie on same depth:
                //boolean isOriginal = true;
                while(sugigraph.inclusionDepth(source).intValue() > sugigraph.inclusionDepth(
                        target).intValue()) {
                    source = sugigraph.getParent(source);
                    //  isOriginal = false;
                }
                while(sugigraph.inclusionDepth(source).intValue() < sugigraph.inclusionDepth(
                        target).intValue()) {
                    target = sugigraph.getParent(target);
                    //  isOriginal = false;
                }

                if(dg.ensureEdge(source, target, DerivedEdge.LESS)) {
                    continue;
                }
            } else {
                if(dg.ensureEdge(source, target, DerivedEdge.LESS)) {
                    continue;
                }
            }

            //create some [<=]edges in the ancestors
            do {
                source = sugigraph.getParent(source);
                target = sugigraph.getParent(target);
            } while(source != target
                && !dg.ensureEdge(source, target, DerivedEdge.EQLESS));
        }

        assert testConsistenceOfDerivedGraph(dg);
        return dg;
    }

    /**
     * the method to call for hierarchization of the children of v at expand
     *
     * @param action DOCUMENT ME!
     */
    public static void expand(SugiActionExpand action) {
        List children = action.s.getChildren(action.v);
        DerivedGraph d = new DerivedGraph(children, action.v);

        //add derived edges
        for(Iterator it = action.internalEdges.iterator(); it.hasNext();) {
            SugiEdge e = (SugiEdge) it.next();
            d.ensureEdge(e.getSource(), e.getTarget(), DerivedEdge.LESS);
            //only internal edges should be present.
            assert children.contains(e.getTarget())
            && children.contains(e.getSource());
        }

        assert testConsistenceOfDerivedGraph(d);
        makeAcyclic(d, children);
        localLevelAssign(d, children);

        reverseEdges(action.s, action.internalEdges);
    }

    /**
     * the main method for hierarchization
     *
     * @param s DOCUMENT ME!
     */
    public static void hierarchize(SugiCompoundGraph s) {
        DerivedGraph dg = createDerivedGraph(s);
        resolveCycles(dg);
        levelAssignment(s, dg);
    }

    /**
     * see pseudecode: Algorithm LevelAssignment. uses the derivedgraph to
     * assign clev-values to the nodes. original compoundgraph is needed for
     * parent-children assoziation.
     *
     * @param g the compoundgraph
     * @param d the acyclic! derived graph of the given compoundgraph
     */
    public static void levelAssignment(SugiCompoundGraph g, DerivedGraph d) {
        List start = new LinkedList();
        start.add(d.getRoot());

        compLevelAssign(d, start);
        assert testConsistenceOfLevelAssignment(g);

        //reverse the orientation of original edges, if necessary
        reverseEdges(g, g.getAllEdges());
    }

    /**
     * makes a subgraph in the given derivedGraph acyclic by reversing some
     * edges. the subgraph is supposed to be separated from the restgraph by
     * the attribute DerivedEdge.isIntern i.e. the method only consideres
     * intern edges.  The type of the edges (eqless, less) is not
     * respected. This algorithm is taken from "bastert".
     *
     * @param derivedGraph the graph to work on
     * @param nodes the list of nodes of the subgraph to be processed.
     */
    public static void makeAcyclic(DerivedGraph derivedGraph, List nodes) {
        assert testConsistenceOfSubgraph(derivedGraph, nodes);

        //System.out.println("makeAcyclic: " + nodes);
        LinkedList Si = new LinkedList();
        LinkedList So = new LinkedList();
        LinkedList G = new LinkedList();

        HashMap indegrees = new HashMap();
        HashMap outdegrees = new HashMap();

        /*initialization*/

        //initialize Out- and InDegree values, both the attribute in suginode
        //and the hashmaps
        for(Iterator it = nodes.iterator(); it.hasNext();) {
            SugiNode n = (SugiNode) it.next();
            indegrees.put(n, new Integer(0));
            outdegrees.put(n, new Integer(0));
        }

        for(Iterator it = nodes.iterator(); it.hasNext();) {
            SugiNode n = (SugiNode) it.next();

            for(Iterator it2 = derivedGraph.getOutEdgesIterator(n);
                it2.hasNext();) {
                DerivedEdge e = (DerivedEdge) it2.next();
                SugiNode u = (SugiNode) e.getTarget();
                if(e.isIntern()) {
                    e.setAcycEnabled(true);
                    incDegree(outdegrees, n);
                    incDegree(indegrees, u);
                }
            }
        }

        //initialize G,Si,So
        for(Iterator it = nodes.iterator(); it.hasNext();) {
            SugiNode n = (SugiNode) it.next();
            if(getDegree(indegrees, n) == 0) {
                So.add(n);
            } else if(getDegree(outdegrees, n) == 0) {
                Si.add(n);
            } else {
                G.add(n);
            }
        }

        /*
         * ALgorithmus start
         */
        while(!G.isEmpty() || !Si.isEmpty() || !So.isEmpty()) {
            //process sinks
            while(!Si.isEmpty()) {
                SugiNode n = (SugiNode) Si.removeFirst();

                //for each incoming enabled edge e ...
                takeIncomingEdges(derivedGraph, n, G, Si, So, outdegrees, true);
            }

            //process sources
            while(!So.isEmpty()) {
                SugiNode n = (SugiNode) So.removeFirst();
                takeOutGoingEdges(derivedGraph, n, G, Si, So, indegrees);
            }

            //delete an other node
            if(!G.isEmpty()) {
                SugiNode max =
                    (SugiNode) Collections.max(G,
                        new DegreeComparator(indegrees, outdegrees));
                G.remove(max);

                //for each outgoing enabled edge e ...
                takeOutGoingEdges(derivedGraph, max, G, Si, So, indegrees);
                //for each incoming enabled edge e ...
                takeIncomingEdges(derivedGraph, max, G, Si, So, outdegrees,
                    false);
            }
        }
    }

    /**
     * implementation of the algorithm ResolveCycles. all edges have to be
     * marked intern
     *
     * @param dg the derived graph to work on
     */
    public static void resolveCycles(DerivedGraph dg) {
        //set of nodes, grouped by their inclusiondepth starting on level 0
        List depths =
            new ArrayList(dg.inclusionHeight(dg.getRoot()).intValue());

        //indicates, that all nodes belong to one subgraph at the beginning
        Object o = new Object();
        for(Iterator it = dg.getAllNodesIterator(); it.hasNext();) {
            SugiNode n = (SugiNode) it.next();
            n.setScc(o);

            int depth = dg.inclusionDepth(n).intValue();

            while(depth >= depths.size()) {
                depths.add(new LinkedList());
            }

            ((List) depths.get(depth)).add(n);
        }

        for(Iterator it = depths.iterator(); it.hasNext();) {
            List next = (List) it.next();
            resolveCycles(dg, next);
        }
    }

    /**
     * recursive help-method for resolveCycles Don't use it directly. is public
     * only because of JUnit-Tests.  the isIntern attribute in DerivedEdges is
     * assumed to be set according to implied subgraph. this subgraph is
     * further divided into scc's and eventually made acyclic.
     *
     * @param derivedGraph DOCUMENT ME!
     * @param subgraph list of nodes which implie an subgraph
     */
    public static void resolveCycles(DerivedGraph derivedGraph, List subgraph) {
        assert testConsistenceOfSubgraph(derivedGraph, subgraph);

        List listOfSccs = new Scc().findScc(derivedGraph, subgraph);

        for(Iterator it = listOfSccs.iterator(); it.hasNext();) {
            //CHECKED no changes while iterating
            boolean cycleExists = false; //flag for bad cycle detected
            DerivedEdge eqlessEdge = null;

            List externOutEdges = new LinkedList();
            List externInEdges = new LinkedList();

            List C_i = (List) it.next();

            for(Iterator it2 = (C_i).iterator(); it2.hasNext();) {
                //CHECKED no changes while iterating
                SugiNode v = (SugiNode) it2.next();

                //first, the outgoing edges are processed
                for(Iterator it3 = derivedGraph.getOutEdgesIterator(v);
                    it3.hasNext();) {
                    DerivedEdge e = (DerivedEdge) it3.next();

                    assert (v == e.getSource());

                    SugiNode u = (SugiNode) e.getTarget();
                    if(u.isInSameScc(v)) {
                        e.setIntern(true);
                        if(e.getType() != DerivedEdge.EQLESS) {
                            cycleExists = true;
                        } else {
                            eqlessEdge = e;
                        }
                    } else {
                        e.setIntern(false);
                        externOutEdges.add(e);
                    }
                }

                //second, the incoming edges are processed
                //the intern edges are processed twice!!
                //but is not a problem
                for(Iterator it3 = derivedGraph.getInEdgesIterator(v);
                    it3.hasNext();) {
                    DerivedEdge e = (DerivedEdge) it3.next();

                    assert (v == e.getTarget());

                    SugiNode u = (SugiNode) e.getSource();
                    if(u.isInSameScc(v)) {
                        e.setIntern(true);
                        if(e.getType() != DerivedEdge.EQLESS) {
                            cycleExists = true;
                        } else {
                            eqlessEdge = e;
                        }
                    } else {
                        e.setIntern(false);
                        externInEdges.add(e);
                    }
                }
            }

            if(cycleExists) {
                assert C_i.size() > 1; //otherwise something bad has happened

                if(eqlessEdge != null) {
                    derivedGraph.deleteEdge(eqlessEdge);

                    //recursive call
                    resolveCycles(derivedGraph, C_i);
                } else {
                    //special heuristic
                    assert (!C_i.isEmpty());
                    makeAcyclic(derivedGraph, C_i);
                }
            } else if(C_i.size() > 1) {
                //test, whether all w in C_i have the same parent
                boolean sameParent = true;
                Object pa = derivedGraph.getParent((Node) C_i.get(0));
                for(Iterator it2 = C_i.iterator(); it2.hasNext();) {
                    Node next = (Node) it2.next();
                    if(derivedGraph.getParent(next) != pa) {
                        sameParent = false;
                        break;
                    }
                }

                if(sameParent) {
                    //1. insert proxy node
                    ProxyNode proxyNode =
                        (ProxyNode) derivedGraph.newLeaf(derivedGraph.getParent(
                                (Node) C_i.get(0)));

                    //2.store all w in proxy
                    proxyNode.setOriginalNodes(C_i);

                    //3a. bend outgoing extern edges:
                    for(Iterator it2 = externOutEdges.iterator();
                        it2.hasNext();) {
                        //CHECKED externOutEdges is a local list, independant of graph
                        DerivedEdge exE = (DerivedEdge) it2.next();
                        derivedGraph.changeSource(exE, proxyNode);
                    }

                    //3b. bend incoming extern edges:
                    for(Iterator it2 = externInEdges.iterator(); it2.hasNext();) {
                        //CHECKED externInEdges is a local list, independant of graph
                        DerivedEdge exE = (DerivedEdge) it2.next();
                        derivedGraph.changeTarget(exE, proxyNode);
                    }

                    //4. move all children of nodes in C_i to proxy
                    //and
                    //5. delete all nodes in C_i from derivedgraph
                    for(Iterator it2 = C_i.iterator(); it2.hasNext();) {
                        Node n = (Node) it2.next();

                        //works on purpose with clone of list
                        for(Iterator it3 =
                                derivedGraph.getChildren(n).iterator();
                            it3.hasNext();) {
                            Node child = (Node) it3.next();
                            derivedGraph.moveNode(child, proxyNode);
                        }

                        derivedGraph.deleteLeaf(n);
                    }
                } else {
                    makeAcyclic(derivedGraph, C_i);
                }
            }
        }
    }

    /**
     * a derived graph is consistent, if there are only edges between nodes at
     * the same depth
     *
     * @param g DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static boolean testConsistenceOfDerivedGraph(DerivedGraph g) {
        for(Iterator it = g.getAllEdgesIterator(); it.hasNext();) {
            DerivedEdge e = (DerivedEdge) it.next();
            if(g.inclusionDepth(e.getSource()).intValue() != g.inclusionDepth(
                    e.getTarget()).intValue()) {
                assert false;
                return false;
            }
        }

        return true;
    }

    /**
     * only for debug
     *
     * @param s DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static boolean testConsistenceOfLevelAssignment(SugiCompoundGraph s) {
        return checkLArecursive(s, (SugiNode) s.getRoot());
    }

    /**
     * the subggraph is consistent, if  1. als nodes are contained in the
     * graph. 2. all nodes belong to the same scc. 3. all intern edges connect
     * nodes of the same scc. 4. all extern edges connect nodes of different
     * scc's.
     *
     * @param d the derived graph
     * @param nodes list of nodes that implie the subgraph
     *
     * @return true, if the criterions are fulfilled
     */
    public static boolean testConsistenceOfSubgraph(DerivedGraph d, List nodes) {
        if(!d.getAllNodes().containsAll(nodes)) {
            assert false;
            return false;
        }

        if(!nodes.isEmpty()) {
            SugiNode sccTest = (SugiNode) nodes.get(0);
            for(Iterator it = nodes.iterator(); it.hasNext();) {
                if(!((SugiNode) it.next()).isInSameScc(sccTest)) {
                    assert false;
                    return false;
                }
            }
        }

        for(Iterator it = nodes.iterator(); it.hasNext();) {
            //CHECKED no changes while iterating
            SugiNode v = (SugiNode) it.next();
            for(Iterator it2 = d.getAdjEdges(v).iterator(); it2.hasNext();) {
                //CHECKED no changes while iterating
                DerivedEdge e = (DerivedEdge) it2.next();

                SugiNode u = (SugiNode) e.getSource();
                if(u == v) {
                    u = (SugiNode) e.getTarget();
                }

                if(e.isIntern()) {
                    if(!u.isInSameScc(v)) {
                        assert false;
                        return false;
                    }

                    if(!nodes.contains(u)) {
                        assert false;
                        return false;
                    }
                } else {
                    if(u.isInSameScc(v)) {
                        assert false;
                        return false;
                    }

                    if(nodes.contains(u)) {
                        assert false;
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * help method during makeAcyclic
     *
     * @param hm DOCUMENT ME!
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected static int getDegree(HashMap hm, SugiNode n) {
        return ((Integer) hm.get(n)).intValue();
    }

    /**
     * checks, whether all children of the given node have a sublevel of the
     * clev of this node
     *
     * @param s the compound graph
     * @param pa the node whose children to check
     *
     * @return DOCUMENT ME!
     */
    private static boolean checkLArecursive(SugiCompoundGraph s, SugiNode pa) {
        boolean result = true;
        for(Iterator it = s.getChildrenIterator(pa); it.hasNext();) {
            SugiNode child = (SugiNode) it.next();
            if(!child.getClev().isSubLevelOf(pa.getClev())) {
                assert false;
                result = false;
            } else if(!checkLArecursive(s, child)) {
                result = false;
            }
        }

        return result;
    }

    /**
     * help method during makeAcyclic
     *
     * @param hm DOCUMENT ME!
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static int decrDegree(HashMap hm, SugiNode n) {
        int value = ((Integer) hm.get(n)).intValue();
        hm.put(n, new Integer(value - 1));
        return value - 1;
    }

    /**
     * help method during makeAcyclic
     *
     * @param hm DOCUMENT ME!
     * @param n DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private static int incDegree(HashMap hm, SugiNode n) {
        int value = ((Integer) hm.get(n)).intValue();
        hm.put(n, new Integer(value + 1));
        return value + 1;
    }

    /**
     * assigns all nodes in the given list to a level starting from "1". in
     * detail: sets the clev attribute in the suginodes and adds them to the
     * local hierarchy of their parents
     *
     * @param d DOCUMENT ME!
     * @param w the list of nodes
     *
     * @return the biggest used local level
     */
    private static int localLevelAssign(DerivedGraph d, List w) {
        assert !w.isEmpty();
        if(w.size() == 1 && w.get(0) == d.getRoot()) {
            ((SugiNode) d.getRoot()).setClev(CompoundLevel.getClevForRoot());
            return 1;
        }

        List sortedW = new TopSort().topSort(d, w);

        int returnvalue = 0;
        for(Iterator it = sortedW.iterator(); it.hasNext();) {
            SugiNode y = (SugiNode) it.next();

            //max-computation
            int levY = 1;
            for(Iterator it2 = d.getInEdgesIterator(y); it2.hasNext();) {
                DerivedEdge e = (DerivedEdge) it2.next();
                assert y == e.getTarget();

                SugiNode v = (SugiNode) e.getSource();
                assert v.getClev() != null;

                //compute l_v 
                int l_v = v.getClev().getTail();
                if(e.getType() != DerivedEdge.EQLESS) {
                    l_v++;
                }

                //compute current maximum
                levY = Math.max(levY, l_v);
            }

            returnvalue = Math.max(returnvalue, levY);

            //assign clev
            CompoundLevel parentclev = ((SugiNode) d.getParent(y)).getClev();
            y.setClev(parentclev.getSubLevel(levY));
        }

        return returnvalue;
    }

    /**
     * reverses the orientation of some edges if they lead from higher to lower 
     * levels
     *
     * @param s DOCUMENT ME!
     * @param edges DOCUMENT ME!
     */
    private static void reverseEdges(SugiCompoundGraph s, List edges) {
        for(Iterator it = edges.iterator(); it.hasNext();) {
            Edge e = (Edge) it.next();

            assert !((SugiNode) e.getSource()).getClev().equals(((SugiNode) e
                .getTarget()).getClev());

            if(((SugiNode) e.getSource()).getClev().compareTo(((SugiNode) e
                    .getTarget()).getClev()) > 0) {
                s.reverseEdge(e);
            }
        }
    }

    /**
     * belongs to algorithm makeAcyclic
     *
     * @param derivedGraph DOCUMENT ME!
     * @param n DOCUMENT ME!
     * @param G DOCUMENT ME!
     * @param Si DOCUMENT ME!
     * @param So DOCUMENT ME!
     * @param out DOCUMENT ME!
     * @param takeIt DOCUMENT ME!
     */
    private static void takeIncomingEdges(DerivedGraph derivedGraph, Node n,
        List G, List Si, List So, HashMap out, boolean takeIt) {
        for(Iterator it = derivedGraph.getInEdges(n).iterator(); it.hasNext();) {
            //works on purpose with clone of adjList
            DerivedEdge e = (DerivedEdge) it.next();
            SugiNode u = (SugiNode) e.getSource();
            assert n == e.getTarget();
            //the implication (enabled => intern) should hold.
            assert !e.isAcycEnabled() || e.isIntern();

            if(e.isAcycEnabled() && e.isIntern()) {
                //mark e taken or not taken
                if(!takeIt) {
                    derivedGraph.reverseEdge(e);
                }

                e.setAcycEnabled(false);
                decrDegree(out, u);
                if(getDegree(out, u) == 0) {
                    //u is in G or in So                   
                    if(!G.remove(u)) {
                        assert So.contains(u);
                        So.remove(u);
                    }

                    Si.add(u);
                }
            }
        }
    }

    /**
     * belongs to algorithm makeAcyclic
     *
     * @param derivedGraph DOCUMENT ME!
     * @param n DOCUMENT ME!
     * @param G DOCUMENT ME!
     * @param Si DOCUMENT ME!
     * @param So DOCUMENT ME!
     * @param in DOCUMENT ME!
     */
    private static void takeOutGoingEdges(DerivedGraph derivedGraph, Node n,
        List G, List Si, List So, HashMap in) {
        for(Iterator it = derivedGraph.getOutEdgesIterator(n); it.hasNext();) {
            DerivedEdge e = (DerivedEdge) it.next();
            SugiNode u = (SugiNode) e.getTarget();
            assert n == e.getSource();
            //the implication (enabled => intern) should hold.
            assert !e.isAcycEnabled() || e.isIntern();

            if(e.isAcycEnabled() && e.isIntern()) {
                //mark e taken: do nothing
                e.setAcycEnabled(false);
                decrDegree(in, u);
                if(getDegree(in, u) == 0) {
                    //u is in G,
                    //Si is empty
                    assert G.contains(u);
                    if(!G.remove(u)) {
                        assert Si.contains(u);
                        Si.remove(u);
                    }

                    So.add(u);
                }
            }
        }
    }

    //~ Inner Classes ==========================================================

    /**
     * compares two SugiNodes by their difference between outdegree and
     * indegree
     */
    public static class DegreeComparator implements Comparator {
        private HashMap indegrees;
        private HashMap outdegrees;

        /**
         * Creates a new DegreeComparator object.
         *
         * @param indegrees DOCUMENT ME!
         * @param outdegrees DOCUMENT ME!
         */
        public DegreeComparator(HashMap indegrees, HashMap outdegrees) {
            this.indegrees = indegrees;
            this.outdegrees = outdegrees;
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
            return (getDegree(outdegrees, (SugiNode) arg0)
            - getDegree(indegrees, (SugiNode) arg0))
            - (getDegree(outdegrees, (SugiNode) arg1)
            - getDegree(indegrees, (SugiNode) arg1));
        }
    }
}
