/*==============================================================================
*
*   DFSNode.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: DFSNode.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

/**
 * used in DepthFirstSearch. any node that implements this interface can be used
 * there.
 */
public interface DFSNode {
    //~ Methods ================================================================

    /**
     *
     *
     * @param number DOCUMENT ME!
     */
    public void setDfsNumber(int number);

    /**
     *
     *
     * @return DOCUMENT ME!
     */
    public int getDfsNumber();
}
