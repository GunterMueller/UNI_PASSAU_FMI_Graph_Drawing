/*==============================================================================
*
*   BaryDummyEdge.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: BaryDummyEdge.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

import org.visnacom.model.Node;

/**
 * this class is used in barycenter ordering. it is needed because LHEdges
 * cannot hold BaryDummyNodes.
 */
public class BaryDummyEdge implements BaryEdge {
    //~ Instance fields ========================================================

    private BaryEdge dummyEdgeLeft = null;
    private BaryEdge dummyEdgeRight = null;
    private BaryNode dummyNode;
    private BaryNode source;
    private BaryNode target;

    //~ Constructors ===========================================================

    /**
     * Creates a new BaryDummyEdge object.
     *
     * @param source DOCUMENT ME!
     * @param target DOCUMENT ME!
     */
    public BaryDummyEdge(BaryNode source, BaryNode target) {
        this.source = source;
        this.target = target;
    }

    //~ Methods ================================================================

    /**
     * @see org.visnacom.sugiyama.model.BaryEdge#setBSource(org.visnacom.sugiyama.model.BaryNode)
     */
    public void setBSource(BaryNode source) {
        this.source = source;
    }

    /**
     * @see BaryEdge#getBSource()
     */
    public BaryNode getBSource() {
        return source;
    }

    /**
     * @see org.visnacom.sugiyama.model.BaryEdge#setBTarget(org.visnacom.sugiyama.model.BaryNode)
     */
    public void setBTarget(BaryNode target) {
        this.target = target;
    }

    /**
     * @see BaryEdge#getBTarget()
     */
    public BaryNode getBTarget() {
        return target;
    }

    /**
     * @see org.visnacom.sugiyama.model.DFSEdge#getDFSSource()
     */
    public DFSNode getDFSSource() {
        return source;
    }

    /**
     * @see org.visnacom.sugiyama.model.DFSEdge#getDFSTarget()
     */
    public DFSNode getDFSTarget() {
        return target;
    }

    /**
     * @see BaryEdge#setDummyEdgeLeft(BaryEdge)
     */
    public void setDummyEdgeLeft(BaryEdge dummyEdgeLeft) {
        this.dummyEdgeLeft = dummyEdgeLeft;
    }

    /**
     * @see BaryEdge#getDummyEdgeLeft()
     */
    public BaryEdge getDummyEdgeLeft() {
        return dummyEdgeLeft;
    }

    /**
     * @see BaryEdge#setDummyEdgeRight(BaryEdge)
     */
    public void setDummyEdgeRight(BaryEdge dummyEdgeRight) {
        this.dummyEdgeRight = dummyEdgeRight;
    }

    /**
     * @see BaryEdge#getDummyEdgeRight()
     */
    public BaryEdge getDummyEdgeRight() {
        return dummyEdgeRight;
    }

    /**
     * @see BaryEdge#setDummyNode(BaryNode)
     */
    public void setDummyNode(BaryNode dummyNode) {
        this.dummyNode = dummyNode;
    }

    /**
     * @see BaryEdge#getDummyNode()
     */
    public BaryNode getDummyNode() {
        return dummyNode;
    }

   
    /**
     * @see org.visnacom.sugiyama.model.SortableEdge#getSortableSource()
     */
    public SortableNode getSortableSource() {
        return source;
    }

   
    /**
     * @see org.visnacom.sugiyama.model.SortableEdge#getSortableTarget()
     */
    public SortableNode getSortableTarget() {
        return target;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String result = "Bedge[";
        if(source instanceof SugiNode) {
            if(target instanceof BaryDummyNode) {
                if(((BaryDummyNode) target).e.getBSource() == source) {
                    result += ((Node) ((BaryDummyNode) target).e.getBTarget())
                    .getId();
                } else {
                    result += ((Node) ((BaryDummyNode) target).e.getBSource())
                    .getId();
                }
            } else {
                result += ((SugiNode) source).getId() + ","
                + ((SugiNode) target).getId();
            }
        } else {
            if(source instanceof BaryDummyNode) {
                result += ((SugiNode) target).getId();
            } else {
                result += ((SugiNode) source).getId() + ","
                + ((SugiNode) target).getId();
            }
        }

        result += "]";
        return result;
    }
}
