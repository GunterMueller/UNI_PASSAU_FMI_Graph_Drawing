/*==============================================================================
*
*   SugiNode.java
*
*   @author Michael Proepster
*
*==============================================================================
* $Id: SugiNode.java 907 2005-10-19 12:03:31Z raitner $
*/

package org.visnacom.sugiyama.model;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.List;

import org.visnacom.model.Node;

/**
 * this type is used in sugi compound graphs
 */
public class SugiNode extends Node implements BaryNode {
    //~ Static fields/initializers =============================================

    //if set to true, the barycenter values are shifted a little
    private static boolean baryFlag = false;

    //~ Instance fields ========================================================

    private CompoundLevel clev = null;
    private LocalHierarchy localHierarchy = null;

    /*reference to the original node that will get the coordinate*/
    private Node originalNode;
    private boolean contractAllowed = true;
    
    /* metrical layout stuff */
    public SugiNode hLayoutAlign;
    public SugiNode hLayoutParentSink;
    public SugiNode hLayoutPred;
    public SugiNode hLayoutRoot;
    public SugiNode hLayoutSink;
    public int[] localXs = new int[4];
    public int biasedX;
    public int hLayoutShift;
    private int height = 0;
    private int localX = 0;
    private int absoluteX = 0;
    private int width = 0;
    private int absoluteY = 0;
    private int localY = 0;
    
    //flag for updateHorizontalLayout
    private boolean hLayoutValid = false;

    /* representative of the scc of this node. is used in resolveCycles
     */
    protected Object scc = null;
    
    /*
     * Vertex ordering
     */
    private int LHPosition = -1;
    protected float barryCenter = 0.0f;
    private int lambda = -1;
    private int rho = -1;
    private float baryDelta = 0;
    private boolean verticalEdgeBent = false;
    
    
    //for debug. comparison of two layout algorithms
    private int heightSaved = height;
    private int widthSaved = width;
    private int localYSaved = localY;
    private int localXSaved = localX;
    private int absoluteXSaved = absoluteX;
    private int absoluteYSaved = absoluteY;
    private boolean debugMode = false;
    
    boolean isDummyRoot = false; //for debug
    
    private boolean lambdarhoInit = false;

    //indicates whether the local hierarchy is automatically updated
    private boolean lhActive = false;

    /* the dfsnumber are assigned during a dfs*/
    private int dfsNumber = 0;

    //~ Constructors ===========================================================

    /**
     * Creates a new SugiNode object.
     *
     * @param id DOCUMENT ME!
     */
    public SugiNode(int id) {
        super(id);
    }

    /**
     * Creates a new SugiNode object.
     */
    public SugiNode() {
        super();
    }

    /**
     * Creates a new SugiNode object.
     *
     * @param node DOCUMENT ME!
     */
    public SugiNode(Node node) {
        super(node.getId());
        originalNode = node;
    }

    //~ Methods ================================================================

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setAbsoluteX(int value) {
        //assert !debugMode || value == absoluteXSaved;
        this.absoluteX = value;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getAbsoluteX() {
        assert !isDummyRoot;
        return absoluteX;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setAbsoluteY(int value) {
        assert !isDummyRoot;
        // assert !debugMode || value == absoluteYSaved;
        this.absoluteY = value;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getAbsoluteY() {
        if(isDummyRoot) {
            //            System.err.println(
            //                "Warning: access to metric coordinates of dummy "
            //                + "root. Should only happen at expanding the root");
        }

        return absoluteY;
    }

    /**
     * @see org.visnacom.sugiyama.model.BaryNode#setBarryCenter(float)
     */
    public void setBarryCenter(float barryCenter) {
        this.barryCenter = barryCenter;
    }

    /**
     * @see org.visnacom.sugiyama.model.BaryNode#getBarryCenter()
     */
    public float getBarryCenter() {
        if(verticalEdgeBent) {
            while(barryCenter + baryDelta == barryCenter) {
                baryDelta *= 2;
            }
        }

        return barryCenter + baryDelta;
        //
        //            return barryCenter + f;
        //            } else {
        //                while(barryCenter - f == barryCenter) {
        //                    f *= 2; 
        //                }
        //
        //            return barryCenter - f;
        //            }
        //        } else {
        //            return barryCenter;
        //        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param i DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getChildrenAtLevel(int i) {
        assert lhActive;
        return localHierarchy.getNodesAtLevel(i);
    }

    /**
     * DOCUMENT ME!
     *
     * @param clev DOCUMENT ME!
     */
    public void setClev(CompoundLevel clev) {
        this.clev = clev;
    }

    /**
     * DOCUMENT ME!
     *
     * @return the compound level of this node. null, if none was set.
     */
    public CompoundLevel getClev() {
        return clev;
    }

    /**
     * DOCUMENT ME!
     *
     * @param b DOCUMENT ME!
     */
    public void setContractAllowed(boolean b) {
        contractAllowed = b;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isContractAllowed() {
        return contractAllowed;
    }

    /**
     * DOCUMENT ME!
     *
     * @param n DOCUMENT ME!
     */
    public void setDfsNumber(int n) {
        dfsNumber = n;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getDfsNumber() {
        return dfsNumber;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isDummyNode() {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param layoutAlign The hLayoutAlign to set.
     */
    public void setHLayoutAlign(SugiNode layoutAlign) {
        assert !isDummyRoot;
        hLayoutAlign = layoutAlign;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the hLayoutAlign.
     */
    public SugiNode getHLayoutAlign() {
        assert !isDummyRoot;
        return hLayoutAlign;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     */
    public void setHLayoutParentSink(SugiNode parent) {
        assert !isDummyRoot;
        this.hLayoutParentSink = parent;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public SugiNode getHLayoutParentSink() {
        assert !isDummyRoot;
        return hLayoutParentSink;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pred DOCUMENT ME!
     */
    public void setHLayoutPred(SugiNode pred) {
        assert !isDummyRoot;
        hLayoutPred = pred;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public SugiNode getHLayoutPred() {
        assert !isDummyRoot;
        return hLayoutPred;
    }

    /**
     * DOCUMENT ME!
     *
     * @param layoutRoot The hLayoutRoot to set.
     */
    public void setHLayoutRoot(SugiNode layoutRoot) {
        assert !isDummyRoot;
        hLayoutRoot = layoutRoot;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the hLayoutRoot.
     */
    public SugiNode getHLayoutRoot() {
        assert !isDummyRoot;
        return hLayoutRoot;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the hLayoutsink.
     */
    public SugiNode getHLayoutSink() {
        assert !isDummyRoot;
        return hLayoutSink;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setHLayoutValid(boolean value) {
        hLayoutValid = value;
    }

    /**
     * Method isHLayoutValid.
     *
     * @return boolean
     */
    public boolean isHLayoutValid() {
        return hLayoutValid;
    }

    /**
     * DOCUMENT ME!
     *
     * @param shift The hLayoutshift to set.
     */
    public void setHLayoutshift(int shift) {
        assert !isDummyRoot;
        hLayoutShift = shift;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the hLayoutshift.
     */
    public int getHLayoutshift() {
        assert !isDummyRoot;
        return hLayoutShift;
    }

    /**
     * DOCUMENT ME!
     *
     * @param layoutsink The hLayoutsink to set.
     */
    public void setHLayoutsink(SugiNode layoutsink) {
        assert !isDummyRoot;
        hLayoutSink = layoutsink;
    }

    /**
     * DOCUMENT ME!
     *
     * @param h DOCUMENT ME!
     */
    public void setHeight(int h) {
        assert !isDummyRoot;
        //assert !debugMode || h == heightSaved;
        height = h;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the height.
     */
    public int getHeight() {
        assert !isDummyRoot;
        return height;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Iterator getHorizontalLHEdges() {
        assert lhActive;
        return localHierarchy.horizontalEdgesIterator();
    }

    /**
     * DOCUMENT ME!
     *
     * @param otherNode DOCUMENT ME!
     *
     * @return value of the attribute sccId
     */
    public boolean isInSameScc(SugiNode otherNode) {
        return otherNode.scc == this.scc;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isLHActive() {
        return lhActive;
    }

    /**
     * DOCUMENT ME!
     *
     * @param active DOCUMENT ME!
     */
    public void setLHactive(boolean active) {
        if(active) {
            lhActive = true;
            localHierarchy = new LocalHierarchy();
        } else {
            lhActive = false;
            localHierarchy = null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getLambda() {
        assert lambdarhoInit;
        return lambda;
    }

    /**
     * DOCUMENT ME!
     *
     * @param lambda DOCUMENT ME!
     * @param rho DOCUMENT ME!
     */
    public void setLambdaRho(int lambda, int rho) {
        lambdarhoInit = true;
        this.lambda = lambda;
        this.rho = rho;
    }

    /**
     * @see org.visnacom.sugiyama.model.BaryNode#getLambdaRho()
     */
    public float getLambdaRho() {
        assert lambdarhoInit;
        return lambda - rho;
    }

    /**
     * @see org.visnacom.sugiyama.model.BaryNode#getLevel()
     */
    public int getLevel() {
        return clev.getTail();
    }

    /**
     * only for debug purposes don't use it.
     *
     * @param lh DOCUMENT ME!
     *
     * @deprecated
     */
    public void setLocalHierarchy(LocalHierarchy lh) {
        lhActive = true;
        localHierarchy = lh;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the localHierarchy.
     */
    public LocalHierarchy getLocalHierarchy() {
        assert lhActive;
        return localHierarchy;
    }

    /**
     * DOCUMENT ME!
     *
     * @param localX DOCUMENT ME!
     */
    public void setLocalX(int localX) {
        assert !isDummyRoot;
        // assert !debugMode || localX == localXSaved;
        this.localX = localX;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getLocalX() {
        assert !isDummyRoot;
        return localX;
    }

    /**
     * DOCUMENT ME!
     *
     * @param localY DOCUMENT ME!
     */
    public void setLocalY(int localY) {
        assert !isDummyRoot;
        //assert !debugMode || localY == localYSaved;
        this.localY = localY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getLocalY() {
        assert !isDummyRoot;
        return localY;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getNumberOfLHLevels() {
        assert lhActive;
        return localHierarchy.getNumberOfLevels();
    }

    /**
     * DOCUMENT ME!
     *
     * @param originalNode DOCUMENT ME!
     */
    public void setOriginalNode(Node originalNode) {
        this.originalNode = originalNode;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Node getOriginalNode() {
        return originalNode;
    }

  
    /**
     * @see org.visnacom.sugiyama.model.BaryNode#setPosition(int)
     */
    public void setPosition(int i) {
        LHPosition = i;
    }

   
    /**
     * @see org.visnacom.sugiyama.model.SortableNode#getPosition()
     */
    public int getPosition() {
        return LHPosition;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Rectangle getRect() {
        return new Rectangle(getAbsoluteX(), getAbsoluteY(), getWidth(),
            getHeight());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getRho() {
        assert lambdarhoInit;
        return rho;
    }

    /**
     * DOCUMENT ME!
     *
     * @param scc DOCUMENT ME!
     */
    public void setScc(Object scc) {
        this.scc = scc;
    }

    /**
     * DOCUMENT ME!
     *
     * @param width DOCUMENT ME!
     */
    public void setWidth(int width) {
        assert !isDummyRoot;
        //assert !debugMode || width == widthSaved;
        this.width = width;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getWidth() {
        assert !isDummyRoot;
        return width;
    }

    /**
     *
     */
    public void checkCoordinates() {
        assert debugMode;
        assert localXSaved == localX;
        if(localYSaved != localY) {
            System.err.println(this + "has localYSaved " + localYSaved);
        }

        assert widthSaved == width;
        assert heightSaved == height;
        assert absoluteXSaved == absoluteX;
        if(absoluteY != absoluteYSaved) {
            System.err.println(this + "has absoluteYSaved " + absoluteYSaved);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param graph DOCUMENT ME!
     * @param n DOCUMENT ME!
     * @param degree DOCUMENT ME!
     * @param printWarning DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean checkLH(SugiCompoundGraph graph, SugiNode n, int degree,
        boolean printWarning) {
        assert degree < 1 || lhActive;
        if(lhActive) {
            return localHierarchy.checkConsistence(graph, n, degree,
                printWarning);
        } else {
            return true;
        }
    }

    /**
     * @see org.visnacom.model.Node#clone()
     */
    public Object clone() {
        throw new UnsupportedOperationException();
    }

    /**
     * DOCUMENT ME!
     *
     * @param sn DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean containsInLH(SugiNode sn) {
        assert lhActive;
        return localHierarchy.contains(sn);
    }

    /**
     *
     */
    public void incLambda() {
        assert lambdarhoInit;
        lambda++;
    }

    /**
     *
     */
    public void incRho() {
        assert lambdarhoInit;
        rho++;
    }

    /**
     *
     */
    public void initializeSinkAndShift() {
        hLayoutSink = this;
        hLayoutShift = Integer.MAX_VALUE;
        hLayoutParentSink = this;
        //    childSinks = new LinkedHashSet();
    }

    /**
     * this method is used because of problems with local dummy nodes during
     * ordering. Some crossings can not be seen any more.
     *
     * @param bool DOCUMENT ME!
     */
    public void notifyEdgeBent(boolean bool) {
        if(bool && !verticalEdgeBent) {
            if(baryFlag = !baryFlag) {
                baryDelta = Float.MIN_VALUE;
            } else {
                baryDelta = -Float.MIN_VALUE;
            }

            while(barryCenter + baryDelta == barryCenter) {
                baryDelta *= 2;
            }
        } else if(!bool && verticalEdgeBent) {
            baryDelta = 0;
        }

        verticalEdgeBent = bool;
    }

    /**
     *
     */
    public void resetLambdaRho() {
        lambda = 0;
        rho = 0;
        lambdarhoInit = true;
    }

    /**
     * for debug only.
     */
    public void saveCoordinates() {
        localXSaved = localX;
        localYSaved = localY;
        widthSaved = width;
        heightSaved = height;
        absoluteXSaved = absoluteX;
        absoluteYSaved = absoluteY;
        debugMode = true;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        String result = "SNode " + getId();

        // result += " scc[";
        //        if(scc != null) {
        //            result += ((Node) scc).getId();
        //        } else {
        //            result += "?";
        //        }
        //result += "]";
                result += " lev";
                if(clev != null) {
                    result += getClev().toString();
                } else {
                    result += "?";
                }
//        result += " LR(" + lambda + "," + rho + ")";
//        result += " p" + getPosition();
//        result += " bc" + getBarryCenter();
        //                result += " Pred:" ;
        //                if(getHLayoutPred()!= null) {
        //               result+= getHLayoutPred().getId();
        //                }else {
        //                    result+="null";
        //                }
        //                result += " All:";
        //                if(getHLayoutAlign() != null) {
        //                    result += getHLayoutAlign().getId();
        //                } else {
        //                    result += "null";
        //                }
        //                result += " Root:";
        //                if(getHLayoutRoot() != null) {
        //                    result += getHLayoutRoot().getId();
        //                } else {
        //                    result += "null";
        //                }
        //        
        //                result += " Sink:";
        //                if(getHLayoutSink() != null) {
        //                    result += getHLayoutSink().getId();
        //                } else {
        //                    result += "null";
        //                }
        //
        //        result += "pSink";
        //        result += hLayoutParentSink.getId();
        //        result += " Hsh:";
        //        result += getHLayoutshift();
        //                result += "[" + localXs[0] + "," + localXs[1] + "," + localXs[2] + ","
        //                + localXs[3] + "]";
        //                        result += "biaX" + biasedX;
                result += " h" + height + " ay" + absoluteY + " ly" + localY;
        result += " w" + width + " ax" + absoluteX + " lx" + localX;
        return result;
    }

    /**
     * add this child to Local Hierarchy child must have a correct clev already
     *
     * @param child DOCUMENT ME!
     * @param i the wished position inside the level. If i == -1, child is
     *        inserted at the right end
     */
    void addChildToLH(SugiNode child, int i) {
        if(lhActive) {
            assert child.getClev() != null;
            setHLayoutValid(false);

            localHierarchy.addNode(child, i);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param children DOCUMENT ME!
     */
    void addChildrenToLH(List children) {
        assert lhActive;
        localHierarchy.addNodes(children);
        setHLayoutValid(false);
    }

    /**
     * gets the LHEdge between the given nodes. creates it, if necessary
     *
     * @param source the source
     * @param target the target
     * @param edge DOCUMENT ME!
     *
     * @return the LHEdge between v and u
     */
    LHEdge ensureLHEdge(SugiNode source, SugiNode target, SugiEdge edge) {
        if(lhActive) {
            //only proper edges
            assert ((SugiNode) edge.getSource()).getClev().isSiblingTo(((SugiNode) edge
                .getTarget()).getClev());
            assert Math.abs(((SugiNode) edge.getSource()).getClev().getTail()
                - ((SugiNode) edge.getTarget()).getClev().getTail()) <= 1;

            LHEdge result = localHierarchy.ensureEdge(source, target);
            if(result != null) {
                result.addOriginalEdge(edge);
                setHLayoutValid(false);
            }

            return result;
        } else {
            //            System.out.println("SugiNode: tried to insert SugiEdge " + edge +
            //                "into deactivated LH");
            return null;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param child DOCUMENT ME!
     */
    void notifyDeletedChild(SugiNode child) {
        setHLayoutValid(false);
        if(lhActive) {
            localHierarchy.deleteNode(child, true);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param source DOCUMENT ME!
     * @param target DOCUMENT ME!
     * @param edge DOCUMENT ME!
     */
    void notifyDeletedEdgeInLH(SugiNode source, SugiNode target, SugiEdge edge) {
        if(lhActive) {
            //only proper edges
            assert ((SugiNode) edge.getSource()).getClev().isSiblingTo(((SugiNode) edge
                .getTarget()).getClev());
            assert Math.abs(((SugiNode) edge.getSource()).getClev().getTail()
                - ((SugiNode) edge.getTarget()).getClev().getTail()) <= 1;

            localHierarchy.notifyDeletedEdge(source, target, edge);
            //setHLayoutValid(false);
        }
    }
}
