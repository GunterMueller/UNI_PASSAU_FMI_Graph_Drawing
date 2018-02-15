/*==============================================================================
*
*   SugiEdge.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: SugiEdge.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

import org.visnacom.model.Edge;
import org.visnacom.model.Node;

/**
 * this edges are used in a sugicompoundgraph.
 */
public class SugiEdge extends Edge implements SortableEdge {
    //~ Instance fields ========================================================

    /* reference to the edge in the original compoundgraph i.e. the View object
     * that has been cloned.
     * */
    private Edge originalEdge;

    //~ Constructors ===========================================================

    /**
     * Creates a new SugiEdge object.
     */
    public SugiEdge() {
        super();
    }

    /**
     * Creates a new SugiEdge object.
     *
     * @param source DOCUMENT ME!
     * @param target DOCUMENT ME!
     */
    public SugiEdge(Node source, Node target) {
        super(source, target);
    }

    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param originalEdge The originalEdge to set.
     */
    public void setOriginalEdge(Edge originalEdge) {
        this.originalEdge = originalEdge;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Edge getOriginalEdge() {
        return originalEdge;
    }

    
    /**
     * @see org.visnacom.sugiyama.model.SortableEdge#getSortableSource()
     */
    public SortableNode getSortableSource() {
        return (SortableNode) getSource();
    }

   
    /**
     * @see org.visnacom.sugiyama.model.SortableEdge#getSortableTarget()
     */
    public SortableNode getSortableTarget() {
        return (SortableNode) getTarget();
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        throw new UnsupportedOperationException();
    }
}
