/*==============================================================================
*
*   TopSort.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: TopSort.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.algorithm;

import java.util.*;

import org.visnacom.model.Node;
import org.visnacom.sugiyama.model.*;


/**
 * implementation of topological sorting, uses depth first search.
 */
public class TopSort extends DepthFirstSearch {
    //~ Instance fields ========================================================

    HashMap iterators = new HashMap();
    private DerivedGraph derivedGraph;
    private LinkedList sortedNodes = new LinkedList();
    private List w;

    //~ Methods ================================================================

    /**
     * returns a new List containing all given nodes the given list is not
     * altered. Additionally, all edges coming from outside the list, are
     * deleted!
     *
     * @param graph the derived graph
     * @param nodes a list of nodes to sort
     *
     * @return a list containing the sorted nodes
     */
    public List topSort(DerivedGraph graph, List nodes) {
        w = nodes;
        this.derivedGraph = graph;
        super.depthFirstSearch(nodes);
        assert sortedNodes.containsAll(nodes);
        assert nodes.containsAll(sortedNodes);
        assert sortedNodes.size() == nodes.size();

        return sortedNodes;
    }

    /**
     * At the time of calling topsort, I must handle the fact, that the
     * isIntern flag of the edges in not set properly. So I don't use it.
     *
     * @see org.visnacom.View.Sugiyama.DepthFirstSearch#getNextUnmarkedEdge(org.visnacom.View.Sugiyama.DerivedGraph,
     *      org.visnacom.model.Node)
     */
    protected DFSEdge getNextUnmarkedEdge(DFSNode v) {
        //the for loop is only traversed to the first suitable element
        for(Iterator it = (Iterator) iterators.get(v); it.hasNext();) {
            //CHECKED concurrent remove handled, see initializeNewNodeInStack
            DerivedEdge e = (DerivedEdge) it.next();
            assert e.getTarget() == v;

            SugiNode u = (SugiNode) e.getSource();

            if(w.contains(u)) {
                return e;
            } else {
                //important for levelassignment line 17 (the max-computation):
                //edges leading out of W are unimportant.
                //the ones coming from outside(above or below) are bad, they confuse
                //the max computation
                derivedGraph.deleteEdge(e);
            }
        }

        return null;
    }

    /**
     * @see org.visnacom.sugiyama.algorithm.DepthFirstSearch#backtrack(org.visnacom.sugiyama.model.DFSNode,
     *      org.visnacom.sugiyama.model.DFSNode)
     */
    protected void backtrack(DFSNode w, DFSNode node) {
        sortedNodes.addLast(w);
        //System.out.println("backtrack " + w);
    }

    /**
     * this topsort works with the incoming edges of nodes, so that they can be
     * deleted, if coming from outside
     *
     * @see org.visnacom.View.Sugiyama.DepthFirstSearch#initializeNewNodeInStack(org.visnacom.View.Sugiyama.DerivedGraph,
     *      org.visnacom.model.Node)
     */
    protected void initializeNewNodeInStack(DFSNode n) {
        iterators.put(n, derivedGraph.getInEdges((Node) n).iterator());
        //works on purpose with clone of list
    }

    /**
     * @see org.visnacom.sugiyama.algorithm.DepthFirstSearch#root(org.visnacom.sugiyama.model.DFSNode)
     */
    protected void root(DFSNode s) {
        //    System.out.println("root " + s);    
    }

    /**
     * @see org.visnacom.sugiyama.algorithm.DepthFirstSearch#traverse(org.visnacom.sugiyama.model.DFSNode,
     *      org.visnacom.sugiyama.model.DFSEdge, org.visnacom.sugiyama.model.DFSNode)
     */
    protected void traverse(DFSNode v, DFSEdge e, DFSNode w) {
        //    System.out.println("traverse " + e);    
    }
}
