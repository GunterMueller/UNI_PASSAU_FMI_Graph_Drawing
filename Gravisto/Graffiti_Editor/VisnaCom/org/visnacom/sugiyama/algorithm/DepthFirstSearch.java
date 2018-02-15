/*==============================================================================
*
*   DepthFirstSearch.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: DepthFirstSearch.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.algorithm;

import java.util.*;

import org.visnacom.sugiyama.model.*;

/**
 * implementation of Brandes, Methoden der Netzwerkanalyse(SS2003) Algo 1. The
 * interfaces DFSNode and DFSEdge are used here.
 * 
 * This is a generic depth first search. no real work is done here. see subclasses
 * for applications.
 * it is also independant of the actual representation of the graph. Instead the
 * methods getNextUnmarkedEdge and initializeNewNodeInStack must be implemented.
 */
public abstract class DepthFirstSearch {
    //~ Instance fields ========================================================

    //represents an 'marked' attribute of the nodes
    HashSet marked = new HashSet();
    Stack S = new Stack();

    //~ Methods ================================================================

    /**
     * this method is called, when the next edge of a node should be traversed.
     * returns null, if there is no further edge.
     *
     * @param n the inzident node
     *
     * @return the next edge to traverse
     */
    protected abstract DFSEdge getNextUnmarkedEdge(DFSNode n);

    /**
     * see Brandes, Methoden der Netzwerkanalyse(SS2003) Algo 1
     *
     * @param w
     * @param node it occurs, that it is null
     */
    protected abstract void backtrack(DFSNode w, DFSNode node);

    /**
     * this method is to be called by the subclasses to start processing
     *
     * @param nodes a list of nodes
     */
    protected void depthFirstSearch(List nodes) {
        int counter = 1; //counter for DFSNumbers
        for(Iterator it = nodes.iterator(); it.hasNext();) {
            //CHECKED no changes while iterating
            DFSNode s = (DFSNode) it.next();
            if(!marked.contains(s)) {
                marked.add(s);

                s.setDfsNumber(counter++);
                //incoming[s] = nil
                S.push(s);
                initializeNewNodeInStack(s);
                root(s);
                while(!S.empty()) {
                    DFSNode v = (DFSNode) S.peek();
                    DFSEdge e = getNextUnmarkedEdge(v);

                    //exists an unmarked edge?
                    if(e != null) {
                        //take source or target
                        DFSNode w = e.getDFSTarget();
                        if(w == v) {
                            w = e.getDFSSource();
                        }

                        assert (nodes.contains(w));
                        if(!marked.contains(w)) {
                            w.setDfsNumber(counter++);
                            //incoming[w] = e;
                            S.push(w);
                            initializeNewNodeInStack(w);
                        }

                        traverse(v, e, w);

                        /*on purpose behind traverse, probably an mistake in
                         * brandes, otherwise the tree-edges aren't recognized
                         * correctly */
                        marked.add(w);
                    } else {
                        //no more edges left
                        DFSNode w = (DFSNode) S.pop();
                        DFSNode topS = null;
                        if(!S.isEmpty()) {
                            topS = (DFSNode) S.peek();
                        }

                        backtrack(w, topS);
                    }
                }
            }
        }
    }

    /**
     * this method is called, when a new node is put on the stack. usually a
     * iterator over its edges should be opened here.
     *
     * @param node DOCUMENT ME!
     */
    protected abstract void initializeNewNodeInStack(DFSNode node);

    /**
     * see Brandes, Methoden der Netzwerkanalyse(SS2003) Algo 1

     * @param node DOCUMENT ME!
     */
    protected abstract void root(DFSNode node);

    /**
     * see Brandes, Methoden der Netzwerkanalyse(SS2003) Algo 1
     *
     * @param v DOCUMENT ME!
     * @param e DOCUMENT ME!
     * @param w DOCUMENT ME!
     */
    protected abstract void traverse(DFSNode v, DFSEdge e, DFSNode w);
}
