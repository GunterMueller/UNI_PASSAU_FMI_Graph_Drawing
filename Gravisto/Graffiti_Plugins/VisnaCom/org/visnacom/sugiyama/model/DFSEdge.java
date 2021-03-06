/*==============================================================================
*
*   DFSEdge.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: DFSEdge.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

/**
 * interface for DepthFirstSearch. any edge, that implements this interface can
 * be used there.
 */
public interface DFSEdge {
    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @return the source
     */
    public DFSNode getDFSSource();

    /**
     * DOCUMENT ME!
     *
     * @return the target
     */
    public DFSNode getDFSTarget();
}
