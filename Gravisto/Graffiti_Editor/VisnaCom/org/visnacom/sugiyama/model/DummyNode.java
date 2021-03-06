/*==============================================================================
*
*   DummyNode.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: DummyNode.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

/**
 * representation of dummy nodes.
 */
public class DummyNode extends SugiNode {
    //~ Static fields/initializers =============================================

    /** possible value for setType(Type) */
    public static final Type UNKNOWN = new Type("unknown");

    /** possible value for setType(Type) */
    public static final Type HORIZONTAL = new Type("horizontal");

    /** possible value for setType(Type) */
    public static final Type NORMAL = new Type("normal");

    /** possible value for setType(Type) */
    public static final Type LOCAL_OR_EXTERNAL = new Type("localExternal");

    //~ Instance fields ========================================================

    private Type type = UNKNOWN;

    //~ Constructors ===========================================================

    /**
     * Creates a new DummyNode object.
     *
     * @param id DOCUMENT ME!
     */
    public DummyNode(int id) {
        super(id);
    }

    /**
     * Creates a new DummyNode object.
     */
    public DummyNode() {
        super();
    }

    //~ Methods ================================================================

    /**
     * avoids the instanceof operator
     * 
     * @return true
     *
     * @see org.visnacom.sugiyama.model.SugiNode#isDummyNode()
     */
    public boolean isDummyNode() {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Type getType() {
        return type;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "Du" + super.toString();
    }

    //~ Inner Classes ==========================================================

    /**
     *
     */
    public static class Type {
        private String string;

        /**
         * there should not be created any objects besides the four constants
         * 
         * @param string DOCUMENT ME!
         */
        protected Type(String string) {
            this.string = string;
        }

      
        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return "Type:" + string;
        }
    }
}
