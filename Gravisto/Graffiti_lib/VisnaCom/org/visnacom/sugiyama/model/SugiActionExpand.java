/*==============================================================================
*
*   SugiActionExpand.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: SugiActionExpand.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

import java.util.*;

import org.visnacom.model.ActionExpand;

/**
 * information object concerning the internal sugi compoundgraph 
 */
public class SugiActionExpand {
    //~ Instance fields ========================================================

    public ActionExpand origAction;
    public List externalDummyNodes = new LinkedList();

    /* edges between children of v */
    public List internalEdges = new LinkedList();
    public List localDummyNodes = new LinkedList();
    public SugiCompoundGraph s;
    public SugiNode v;
    private List mappings = new LinkedList();

    //~ Constructors ===========================================================

    /**
     * Creates a new SugiActionExpand object.
     *
     * @param sugiGraph DOCUMENT ME!
     */
    public SugiActionExpand(SugiCompoundGraph sugiGraph) {
        s = sugiGraph;
    }

    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Iterator getDummyNodesIterator() {
        List l = new LinkedList();
        l.add(externalDummyNodes);
        l.add(localDummyNodes);
        // assert false;
        return new IteratorOfCollections(l);
    }

    /**
     * DOCUMENT ME!
     *
     * @param p_u DOCUMENT ME!
     * @param u DOCUMENT ME!
     * @param vToUOrUToV DOCUMENT ME!
     * @param p_uU DOCUMENT ME!
     * @param isFromVtoU DOCUMENT ME!
     * @param origMapping DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public SugiMapping addMapping(DummyNode p_u, SugiNode u,
        SugiEdge vToUOrUToV, SugiEdge p_uU, boolean isFromVtoU,
        ActionExpand.Mapping origMapping) {
        assert (!isFromVtoU
        || (vToUOrUToV.getSource() == v && vToUOrUToV.getTarget() == u));
        assert (isFromVtoU
        || (vToUOrUToV.getSource() == u && vToUOrUToV.getTarget() == v));

        assert (!isFromVtoU
        || (p_uU.getSource() == p_u && p_uU.getTarget() == u));
        assert (isFromVtoU
        || (p_uU.getSource() == u && p_uU.getTarget() == p_u));

        SugiMapping m = new SugiMapping();
        m.vToUOrUToV = vToUOrUToV;
        m.p_u = p_u;
        m.p_uU = p_uU;
        m.isFromVToU = isFromVtoU;
        m.u = u;
        m.origMapping = origMapping;
        mappings.add(m);

        if(m.isPuExternal()) {
            externalDummyNodes.add(p_u);
        } else {
            localDummyNodes.add(p_u);
        }

        return m;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Iterator mappingsIterator() {
        return mappings.iterator();
    }

    //~ Inner Classes ==========================================================

    /**contains pairs: old edge (v,u) <-> single inserted dummy node p_u
     * they are created in normalization. not valid after vertex ordering(splitting)
     * */
    public class SugiMapping {
        public DummyNode p_u;
        public ActionExpand.Mapping origMapping;
        public SugiEdge p_uU;
        public SugiEdge vToUOrUToV;
        public SugiNode u;
        public boolean isFromVToU;
        private List vDashCedges = new LinkedList();

        /**
         * @return 
         *
         */
        public int getNumberOfVDashs() {
            return vDashCedges.size();
        }

        /**
         *
         *
         * @return DOCUMENT ME!
         */
        public boolean isPuExternal() {
            return s.getParent(v) != s.getParent(u);
        }

        /**
         * DOCUMENT ME!
         *
         * @param edge DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public SugiNode getVDash(SugiEdge edge) {
            if(isFromVToU) {
                assert !((SugiNode) edge.getSource()).isDummyNode();
                assert ((SugiNode) edge.getTarget()).isDummyNode();
                return (SugiNode) edge.getSource();
            } else {
                assert !((SugiNode) edge.getTarget()).isDummyNode();
                assert ((SugiNode) edge.getSource()).isDummyNode();
                return (SugiNode) edge.getTarget();
            }
        }

        /**
         * DOCUMENT ME!
         *
         * @return DOCUMENT ME!
         */
        public List getVDashCs() {
            return vDashCedges;
        }

        /**
         * DOCUMENT ME!
         *
         * @param e DOCUMENT ME!
         */
        public void addEdge(SugiEdge e) {
            assert (!isFromVToU
            || (s.getParent(e.getSource()) == v
            && s.getParent(e.getTarget()) == p_u));
            assert (isFromVToU
            || (s.getParent(e.getSource()) == p_u
            && s.getParent(e.getTarget()) == v));
            vDashCedges.add(e);
        }
    }
}
