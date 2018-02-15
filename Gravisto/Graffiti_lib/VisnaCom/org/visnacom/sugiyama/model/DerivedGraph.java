/*==============================================================================
*
*   DerivedGraph.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: DerivedGraph.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

import java.util.Iterator;
import java.util.List;

import org.visnacom.model.*;

/**
 * implementation of a derived compound graph
 */
public class DerivedGraph extends CompoundGraph {
    //~ Constructors ===========================================================

    /**
     * Creates a new DerivedGraph object, that has the same nodes and the same
     * inclusion hierarchy as the given compoundgraph, but no edges.
     *
     * @param c DOCUMENT ME!
     */
    public DerivedGraph(SugiCompoundGraph c) {
        super(c.getRoot());
        this.idCounter = c.idCounter;
        assert getRoot() instanceof SugiNode;
        copyHierarchy(c, getRoot());
    }

    /**
     * Creates a new DerivedGraph object, that contains the given parent and
     * its children, but no edges.
     *
     * @param children DOCUMENT ME!
     * @param parent DOCUMENT ME!
     */
    public DerivedGraph(List children, SugiNode parent) {
        super(parent);
        for(Iterator it = children.iterator(); it.hasNext();) {
            SugiNode n = (SugiNode) it.next();
            newGivenNode(n, parent);
        }
    }

    /**
     * Creates a new DerivedGraph object.
     */
    public DerivedGraph() {
        super();
        idCounter = 0;
    }

    //~ Methods ================================================================

    /**
     * used at inserting the proxynode
     *
     * @see org.visnacom.model.CompoundGraph#changeSource(org.visnacom.model.Edge,
     *      org.visnacom.model.Node)
     */
    public void changeSource(Edge e, Node newSource) {
        assert e instanceof DerivedEdge;
        super.changeSource(e, newSource);
    }

    /**
     * used at inserting the proxynode
     *
     * @see org.visnacom.model.CompoundGraph#changeTarget(org.visnacom.model.Edge,
     *      org.visnacom.model.Node)
     */
    public void changeTarget(Edge e, Node newTarget) {
        assert e instanceof DerivedEdge;
        super.changeTarget(e, newTarget);
    }

  
    /**
     * ensures, that, after the call, there exists an edge between source and
     * target with at least the given type
     *
     * @param source the source node
     * @param target the target node
     * @param type the wanted type
     *
     * @return true, if there was already an edge
     */
    public boolean ensureEdge(Node source, Node target, DerivedEdge.Type type) {
        List edges = getEdge(source, target);
        if(!edges.isEmpty()) {
            assert edges.size() == 1;
            ((DerivedEdge) edges.get(0)).ensure(type);
            return true;
        } else {
            newEdge(source, target, type);
            return false;
        }
    }

    /**
     * only for testing purposes. usually ensureEdge ist used. with this method
     * one can construct an derived graph without having an
     * sugicompoundgraph.
     *
     * @param source DOCUMENT ME!
     * @param target DOCUMENT ME!
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @deprecated
     */
    public DerivedEdge newEdge(Node source, Node target, DerivedEdge.Type type) {
        DerivedEdge newEdge = (DerivedEdge) super.newEdge(source, target);
        newEdge.setType(type);
        return newEdge;
    }

    /**
     * is not allowed. use newEdge(Node, Node, DerivedEdge.Type) instead
     *
     * @see org.visnacom.model.CompoundGraph#newEdge(org.visnacom.model.Node, org.visnacom.model.Node)
     */
    public Edge newEdge(Node source, Node target) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see org.visnacom.model.CompoundGraph#newEdgeFac(org.visnacom.model.Node, org.visnacom.model.Node)
     */
    public Edge newEdgeFac(Node source, Node target) {
        return new DerivedEdge(source, target);
    }

    /**
     * @see org.visnacom.model.CompoundGraph#newNodeFac()
     */
    protected Node newNodeFac() {
        return new ProxyNode(idCounter++);
    }
}
