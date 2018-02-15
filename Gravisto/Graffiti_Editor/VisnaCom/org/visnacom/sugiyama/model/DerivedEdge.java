/*==============================================================================
*
*   DerivedEdge.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: DerivedEdge.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

import org.visnacom.model.Edge;
import org.visnacom.model.Node;

/**
 * the edge type for derived compound graphs
 */
public class DerivedEdge extends Edge implements DFSEdge {
    //~ Static fields/initializers =============================================


    /** represents the type "equal or less" */
    public static final Type EQLESS = new Type("[<=]");

    /** represents the type "strict less" */
    public static final Type LESS = new Type("[<]");

    //~ Instance fields ========================================================

    private Type type = EQLESS;

    /*
     * used in makeAcyclic. initialised to false. is set to true in preprocessing
     * of makeAcyclic, if edge is intern
     */
    private boolean acycEnabled = false;

    /**
     * indicates whether an edge is contained in a scc or is extern must be
     * initialized to true, because the Scc is called first on the whole graph
     * with no edges having been marked at all
     */
    private boolean isIntern = true;

    //~ Constructors ===========================================================


    /**
     * Creates a new DerivedEdge object.
     */
    public DerivedEdge() {
        super();
    }

    /**
     * Creates a new DerivedEdge object.
     *
     * @param source DOCUMENT ME!
     * @param target DOCUMENT ME!
     */
    public DerivedEdge(Node source, Node target) {
        super(source, target);
    }

    //~ Methods ================================================================


    /**
     * only to be used in MakeAcyclic!
     *
     * @param b DOCUMENT ME!
     */
    public void setAcycEnabled(boolean b) {
        acycEnabled = b;
    }

    /**
     * only to be used in MakeAcyclic!
     *
     * @return DOCUMENT ME!
     */
    public boolean isAcycEnabled() {
        return acycEnabled;
    }

   
    /**
     * @see org.visnacom.sugiyama.model.DFSEdge#getDFSSource()
     */
    public DFSNode getDFSSource() {
        return (DFSNode) getSource();
    }

  
    /**
     * @see org.visnacom.sugiyama.model.DFSEdge#getDFSTarget()
     */
    public DFSNode getDFSTarget() {
        return (DFSNode) getTarget();
    }

    /**
     * used in resolveCycles
     *
     * @param isIntern
     */
    public void setIntern(boolean isIntern) {
        this.isIntern = isIntern;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the isIntern.
     */
    public boolean isIntern() {
        return isIntern;
    }

    /**
     * sets the type of this edge
     *
     * @param type DOCUMENT ME!
     *
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     *
     * @return DOCUMENT ME!
     */
    public Type getType() {
        return type;
    }

    /** 
     * not supported.
     * 
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        throw new UnsupportedOperationException();
    }

    /**
     * sets the type of this edge, so that it is at least the given value.
     *
     * @param value the mimimum value
     */
    public void ensure(Type value) {
        if(value == LESS) {
            this.type = value;
        }
    }

    /**
     * @see org.visnacom.model.Edge#toString()
     */
    public String toString() {
        String result = "";
        result += "(" + getSource().getId();
        result += type;
        result += getTarget().getId();
        result += ")";
        return result;
    }

    //~ Inner Classes ==========================================================

    /**
     *
     */
    public static class Type {
        private String type;

        /**
         * Creates a new Type object.
         *
         * @param type DOCUMENT ME!
         */
        private Type(String type) {
            this.type = type;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return type;
        }
    }
}
