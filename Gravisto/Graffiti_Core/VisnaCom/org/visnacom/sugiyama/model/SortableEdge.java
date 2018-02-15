/*==============================================================================
*
*   SortableEdge.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: SortableEdge.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

/**
 * interface used in CrossCount
 */
public interface SortableEdge {
    //~ Methods ================================================================

    /**
     *
     *
     * @return DOCUMENT ME!
     */
    public SortableNode getSortableSource();

    /**
     *
     *
     * @return DOCUMENT ME!
     */
    public SortableNode getSortableTarget();
}
