/*==============================================================================
*
*   LHEdge.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: LHEdge.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

import java.util.LinkedList;
import java.util.List;

import org.visnacom.model.Edge;
import org.visnacom.model.Node;

/**
 * edge class for LocalHierarchy
 */
public class LHEdge extends Edge implements BaryEdge {
    //~ Instance fields ========================================================

    private BaryEdge dummyEdgeLeft = null;
    private BaryEdge dummyEdgeRight = null;

    private BaryNode dummyNode;
    private List originalEdges = new LinkedList();
    private SugiNode origSource = null;
    private SugiNode origTarget = null;
    private boolean nonVertical;

    //~ Constructors ===========================================================

    /**
     * DOCUMENT ME!
     *
     * @param s
     * @param t
     */
    public LHEdge(SugiNode s, SugiNode t) {
        super(s, t);
    }

    //~ Methods ================================================================

    /**
     * the node must be of type Node
     *
     * @see org.visnacom.sugiyama.model.BaryEdge#setBSource(org.visnacom.sugiyama.model.BaryNode)
     *      this method is only to be called with SugiNode objects
     */
    public void setBSource(BaryNode source) {
        setSource((Node) source);
    }

    /**
     * @see BaryEdge#getBSource()
     */
    public BaryNode getBSource() {
        return (BaryNode) getSource();
    }

    /**
     * @see org.visnacom.sugiyama.model.BaryEdge#setBTarget(org.visnacom.sugiyama.model.BaryNode)
     *      this method is only to be called with Node objects
     */
    public void setBTarget(BaryNode target) {
        setTarget((Node) target);
    }

    /**
     * @see BaryEdge#getBTarget()
     */
    public BaryNode getBTarget() {
        return (BaryNode) getTarget();
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
     * used at adding to the LH
     *
     * @return true if source and target lie on the same level
     */
    public boolean isHorizontal() {
        assert ((SugiNode) getSource()).getClev().isSiblingTo(((SugiNode) getTarget())
            .getClev());
        return ((SugiNode) getSource()).getLevel() == ((SugiNode) getTarget())
        .getLevel();
    }

    /**
     * used in horizontal metric layout
     *
     * @return DOCUMENT ME!
     */
    public boolean isInnerSegment() {
        return ((SugiNode) getSource()).isDummyNode()
        && ((SugiNode) getTarget()).isDummyNode();
    }

    /**
     * @return DOCUMENT ME!
     */
    public int getMultiplicity() {
        return originalEdges.size();
    }

    /**
     * used in horizontal metric layout
     *
     * @param nonVertical DOCUMENT ME!
     */
    public void setNonVertical(boolean nonVertical) {
        assert !isInnerSegment();
        this.nonVertical = nonVertical;
    }

    /**
     * used in horizontal metric layout
     *
     * @return DOCUMENT ME!
     */
    public boolean isNonVertical() {
        return nonVertical;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getOriginalEdges() {
        return originalEdges;
    }

    /**
     * @see org.visnacom.sugiyama.model.SortableEdge#getSortableSource()
     */
    public SortableNode getSortableSource() {
        return getBSource();
    }

    /**
     * @see org.visnacom.sugiyama.model.SortableEdge#getSortableTarget()
     */
    public SortableNode getSortableTarget() {
        return getBTarget();
    }

    /**
     * stores the given edge in this LHEdge
     *
     * @param e DOCUMENT ME!
     */
    public void addOriginalEdge(SugiEdge e) {
        originalEdges.add(e);
    }

    /**
     * this method is only to be used in preprocessing bary ordering. it stores
     * the original source and notifies the target about the change
     *
     * @param newSource DOCUMENT ME!
     */
    public void bendSource(SugiNode newSource) {
        origSource = (SugiNode) getSource();
        setSource(newSource);
        if(origTarget != null) {
            assert origTarget != getTarget();
            origTarget.notifyEdgeBent(true);
        } else {
            ((SugiNode) getTarget()).notifyEdgeBent(true);
        }
    }

    /**
     * this method is only to be used in preprocessing bary ordering. it stores
     * the original target and notifies the source about the change
     *
     * @param target DOCUMENT ME!
     */
    public void bendTarget(SugiNode target) {
        origTarget = (SugiNode) getTarget();
        setTarget(target);
        if(origSource != null) {
            assert origSource != getSource();
            origSource.notifyEdgeBent(true);
        } else {
            ((SugiNode) getSource()).notifyEdgeBent(true);
        }
    }

    /**
     * @see java.lang.Object#clone()
     */
    public Object clone() {
        throw new UnsupportedOperationException();
    }

    /**
     * undoes the bendSource operation
     */
    public void restoreSource() {
        setSource(origSource);
        origSource = null;
    }

    /**
     * undoes the bendTarget operation
     */
    public void restoreTarget() {
        setTarget(origTarget);
        origTarget = null;
    }
}
