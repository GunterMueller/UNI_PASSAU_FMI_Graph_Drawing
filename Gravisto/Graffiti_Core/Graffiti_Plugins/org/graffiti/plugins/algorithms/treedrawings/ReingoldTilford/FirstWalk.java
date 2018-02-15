/*
 * Created on 05.10.2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford;

import java.util.ArrayList;
import java.util.Collections;

/**
 * @author Beiqi
 * 
 *        FirsWalk traverses the tree bottom-up. Placing the subtrees from left
 *         to right, and positioning the parent in the middle of their children.
 * 
 */

public class FirstWalk {
    // ~ Instance fields
    // ========================================================
    /** minimal Seperation between node and node */
    private Integer minSeperationNodeNode;
    /** minimal Seperation between node and edge */
    private Integer minSeperationNodeEdge;
    /** levelMode, Glboral or Local? */
    private int levelMode;
    /** distance between two levels */
    private Integer levelDistance;
    /** edgeLayoutMode, StraightLine or BuyLayout? */
    private int edgeLayoutMode;
    /** portMode, Center or Ingoing-Outgoing? */
    private int portMode;
    /** heuristic Mode **/
    private int heuristicMode;

    // ~ Constructors
    // ===========================================================
    /**
     * Create a new FirstWalk object.
     * 
     */
    public FirstWalk(Integer minSeperationNodeNode,
            Integer minSeperationNodeEdge, int levelMode,
            Integer levelDistance, int edgeLayoutMode, int portMode,
            int heuristicMode) {
        this.minSeperationNodeNode = minSeperationNodeNode;
        this.minSeperationNodeEdge = minSeperationNodeEdge;
        this.levelMode = levelMode;
        this.levelDistance = levelDistance;
        this.edgeLayoutMode = edgeLayoutMode;
        this.portMode = portMode;
        this.heuristicMode = heuristicMode;
    }

    // ~ Methods
    // ================================================================
    /**
     * This Methode for assinging relative X-coordinate to all nodes in the tree
     * rooted by Paramater rtNode.
     * 
     * @param rtNode
     *            RTNode
     * @param yPosition
     *            double, the position of the top line of the node
     * @param level
     *            int
     */
    public void firstWalk(RTNode rtNode, double yPosition, int level) {
        rtNode.setY(yPosition + rtNode.getHeight() / 2);
        rtNode.setLevel(level);

        // the rtNode is a leaf
        if (rtNode.getNumberOfChildren() == 0) {
            rtNode.setX(0);

            ConturElement c1 = new ConturElement();
            c1.setPoint1(-rtNode.getWidth() / 2, yPosition);
            c1
                    .setPoint2(-rtNode.getWidth() / 2, yPosition
                            + rtNode.getHeight());

            ConturElement c2 = new ConturElement();
            c2.setPoint1(rtNode.getWidth() / 2, yPosition);
            c2.setPoint2(rtNode.getWidth() / 2, yPosition + rtNode.getHeight());

            rtNode.setMinXP(new MinMaxXPosition(-rtNode.getWidth() / 2,
                    yPosition));
            rtNode.setMaxXP(new MinMaxXPosition(rtNode.getWidth() / 2,
                    yPosition));
            rtNode.getLeftContur().getContur().addFirst(c1);
            rtNode.getRightContur().getContur().addFirst(c2);

        }
        // the rtNode has more than one child
        else if (rtNode.getNumberOfChildren() >= 1) {

            // "0" for Global; "1" for Local
            int levelAdj;
            if (levelMode == 0) {
                levelAdj = levelDistance;
            } else {
                levelAdj = (int) rtNode.getHeight() + levelDistance;
            }

            for (int i = 0; i < rtNode.getNumberOfChildren(); i++) {

                firstWalk((RTNode) rtNode.getChildren().get(i), yPosition
                        + levelAdj, level + 1);
            }

            if (heuristicMode == 2) {
                ArrayList<Object> sortList = rtNode.getChildren();
                Collections.sort(sortList, new ChildrenComparator());
                int size = sortList.size();
                ArrayList<Object> list = new ArrayList<Object>(sortList);

                for (int k = 0; k < size; k++) {
                    if (k % 2 == 0) {
                        list.set((k / 2), sortList.get(k));
                    } else {
                        list.set(((size - 1) - k / 2), sortList.get(k));
                    }
                }
                if (rtNode.getLevel() % 2 == 1) {
                    Collections.reverse(list);
                }
                rtNode.setChildren(list);
            }

            double[] shiftSpacingout = new double[rtNode.getNumberOfChildren()];
            double[] changeSpacingout = new double[rtNode.getNumberOfChildren()];
            SpacingoutDate sd = new SpacingoutDate(0, shiftSpacingout,
                    changeSpacingout);

            // position ith subtrees relative to the (i-1)th subtrees
            for (int k = 0; k < rtNode.getNumberOfChildren() - 1; k++) {
                positionTrees(k, rtNode, sd);

            }

            if (heuristicMode == 0) {
                shiftSpacingout = sd.shiftArray;
                changeSpacingout = sd.changeArray;
                double shift = 0;
                double change = 0;
                for (int k = shiftSpacingout.length - 2; k >= 0; k--) {
                    change += changeSpacingout[k + 1];
                    shift += shiftSpacingout[k + 1] + change;
                    ConturElement c = ((RTNode) rtNode.getChildren().get(k))
                            .getRightContur().getContur().getFirst();
                    c.setShift(c.getShift() + (int) Math.round(shift));
                }
            }

            // update contur
            RTNode rt1 = (RTNode) rtNode.getChildren().get(0);
            RTNode rt2 = (RTNode) rtNode.getChildren().get(
                    rtNode.getNumberOfChildren() - 1);

            ConturElement ce1 = rt1.getLeftContur().getContur().getFirst();
            ConturElement ce2 = rt2.getRightContur().getContur().getFirst();

            if (rtNode.getNumberOfChildren() == 1) {
                rtNode.setX(rt1.getX());
            } else {
                double d = ce1.getX1() + ce2.getX1() + ce2.getShift();
                rtNode.setX(Math.round(d / 2));
            }

            ConturElement ceNew1 = new ConturElement();
            ceNew1.setPoint1(rtNode.getX(), yPosition + rtNode.getHeight());
            ceNew1.setPoint2(ce1.getX1() + rt1.getWidth() / 2, ce1.getY1());
            ceNew1.setIsEdge(true);

            ConturElement ceNew2 = new ConturElement();
            ceNew2.setPoint1(rtNode.getX() - rtNode.getWidth() / 2, yPosition);
            ceNew2.setPoint2(rtNode.getX() - rtNode.getWidth() / 2, yPosition
                    + rtNode.getHeight());

            ConturElement ceNew3 = new ConturElement();
            ceNew3.setPoint1(rtNode.getX(), yPosition + rtNode.getHeight());
            ceNew3.setPoint2(ce2.getX1() + ce2.getShift() - rt2.getWidth() / 2,
                    ce2.getY1());
            ceNew3.setIsEdge(true);

            ConturElement ceNew4 = new ConturElement();
            ceNew4.setPoint1(rtNode.getX() + rtNode.getWidth() / 2, yPosition);
            ceNew4.setPoint2(rtNode.getX() + rtNode.getWidth() / 2, yPosition
                    + rtNode.getHeight());

            // update minX, maxX position
            if ((rtNode.getX() - rtNode.getWidth() / 2) < rt1.getMinXP().xValue) {
                rtNode.setMinXP(new MinMaxXPosition((rtNode.getX() - rtNode
                        .getWidth() / 2), yPosition));
            } else {
                rtNode.setMinXP(rt1.getMinXP());
            }

            if ((rtNode.getX() + rtNode.getWidth() / 2) > rt2.getMaxXP().xValue) {
                rtNode.setMaxXP(new MinMaxXPosition((rtNode.getX() + rtNode
                        .getWidth() / 2), yPosition));
            } else {
                rtNode.setMaxXP(rt2.getMaxXP());

            }

            rtNode.getLeftContur().getContur().addLast(ceNew2);
            rtNode.getLeftContur().getContur().addLast(ceNew1);
            rtNode.getLeftContur().getContur().addAll(
                    rt1.getLeftContur().getContur());

            rtNode.getRightContur().getContur().addLast(ceNew4);
            rtNode.getRightContur().getContur().addLast(ceNew3);
            rtNode.getRightContur().getContur().addAll(
                    rt2.getRightContur().getContur());

        }
    }

    /**
     * placing the ite subtree
     * 
     * @param index
     *            int, the index of current left subtree
     * @param rtNode
     *            RTNode, current parent
     * @param sd
     *            SpacingoutDate, the information for spacing out
     */
    public void positionTrees(int index, RTNode rtNode, SpacingoutDate sd) {

        Contur cLeft = ((RTNode) rtNode.getChildren().get(index))
                .getRightContur();
        Contur cRight = ((RTNode) rtNode.getChildren().get(index + 1))
                .getLeftContur();
        Contur uLeft = ((RTNode) rtNode.getChildren().get(0)).getLeftContur();
        Contur uRight = ((RTNode) rtNode.getChildren().get(index + 1))
                .getRightContur();

        RunConturDate rcd1 = runContur(cLeft, cRight);

        if (index == rtNode.getNumberOfChildren() - 2) {
            rcd1 = checkDistance(rcd1, rtNode, uLeft, uRight);
        }

        MinMaxXPosition minXP1 = caculateMinX(0, index + 1, rtNode, rcd1.minSep);
        MinMaxXPosition maxXP1 = caculateMaxX(index, index + 1, rtNode,
                rcd1.minSep);
        double width1 = maxXP1.xValue - minXP1.xValue;

        boolean swap = false;

        // swap the two subtrees if width2 is smaller than width1
        if (heuristicMode == 1) {
            Contur cLeft2 = ((RTNode) rtNode.getChildren().get(index + 1))
                    .getRightContur();
            Contur cRight2 = ((RTNode) rtNode.getChildren().get(0))
                    .getLeftContur();
            Contur uLeft2 = ((RTNode) rtNode.getChildren().get(index + 1))
                    .getLeftContur();
            Contur uRight2 = ((RTNode) rtNode.getChildren().get(index))
                    .getRightContur();

            RunConturDate rcd2 = runContur(cLeft2, cRight2);

            if (index == rtNode.getNumberOfChildren() - 2) {
                rcd2 = checkDistance(rcd2, rtNode, uLeft2, uRight2);
            }

            MinMaxXPosition minXP2 = caculateMinX(index + 1, 0, rtNode,
                    rcd2.minSep);
            MinMaxXPosition maxXP2 = caculateMaxX(index + 1, index, rtNode,
                    rcd2.minSep);
            double width2 = maxXP2.xValue - minXP2.xValue;

            if (width2 < width1) {
                // System.out.println("!swap1 - width2 < width1!");
                swap = true;

                swap(index, rtNode, rcd2.minSep);
                cLeft = cLeft2;
                cRight = cRight2;
                uLeft = uLeft2;
                uRight = uRight2;
                rcd1 = rcd2;
                minXP1 = minXP2;
                maxXP1 = maxXP2;

            }

        }

        int sepBegin = rcd1.sepBegin;
        int minSep = rcd1.minSep;
        int i = rcd1.i;
        int j = rcd1.j;
        int lShiftSum = rcd1.lShiftSum;
        int rShiftSum = rcd1.rShiftSum;

        ConturElement ce2 = cRight.getContur().getLast();
        ConturElement ce3 = uRight.getContur().getLast();

        if (!swap) {
            // set shift value
            (uRight.getContur().getFirst()).setShift(minSep);

        }

        // update extreme value
        ce2.setExtremeShift(ce2.getExtremeShift() + minSep);
        ce3.setExtremeShift(ce3.getExtremeShift() + minSep);

        // spacing out the small children between the big silbings
        if (heuristicMode == 0) {
            if (minSep > sepBegin) {
                ConturElement ce1;
                if ((cLeft.getContur().getLast()).getY2() > (cRight.getContur()
                        .getLast()).getY2()) {
                    ce1 = cLeft.getContur().get(i);
                } else {
                    ce1 = cLeft.getContur().getLast();
                }

                int conflict = 0;
                if (ce1.getAncestorIndex() > 0) {
                    conflict = ce1.getAncestorIndex();
                } else {
                    conflict = sd.defaultAncestor;
                }

                int subtrees = index + 1 - conflict;
                if (subtrees >= 2) {
                    sd.shiftArray[index + 1] += minSep - sepBegin;
                    sd.changeArray[index + 1] -= (minSep - sepBegin) / subtrees;
                    sd.changeArray[conflict] += (minSep - sepBegin) / subtrees;
                }
            }

            if ((cLeft.getContur().getLast()).getY2() <= (cRight.getContur()
                    .getLast()).getY2()) {
                sd.defaultAncestor = index + 1;
            } else {
                /*
                 * for (int m = 0; m < uRight.getContur().size(); m++) {
                 * ConturElement c = (ConturElement)uRight.getContur().get(m);
                 * c.setAncestorIndex(index+1); }
                 */
            }
        }

        // merg contur
        // left tree is higher than right tree
        if ((i < cLeft.getContur().size()) && (j == cRight.getContur().size())) {

            ConturElement ce1 = cLeft.getContur().get(i);
            double y = ce2.getY2();
            ce1.setY1(y);

            // int thread = rShiftSum + minSep - lShiftSum;
            if (ce1.getIsEdge() == false) {
                // thread = rShiftSum + minSep;
                int thread = ce3.getExtremeShift();
                ce1.setThreadValue(-thread);
            } else {
                int thread = ce3.getExtremeShift() - lShiftSum;
                ce1.setThreadValue(ce1.getThreadValue() - thread);

            }

            uRight.getContur().addAll(
                    cLeft.getContur().subList(i, cLeft.getContur().size()));
        }

        // right tree is higher than left tree
        if ((i == cLeft.getContur().size()) && (j < cRight.getContur().size())) {

            ConturElement ce1 = cRight.getContur().get(j);
            ConturElement ce4 = uLeft.getContur().getLast();
            double y = ce4.getY2();
            ce1.setY1(y);

            // int thread = rShiftSum + minSep - lShiftSum;
            int thread = rShiftSum + minSep - ce4.getExtremeShift();
            ce1.setThreadValue(ce1.getThreadValue() + thread);

            uLeft.getContur().addAll(
                    cRight.getContur().subList(j, cRight.getContur().size()));
        }

        ((RTNode) rtNode.getChildren().get(0)).setMinXP(minXP1);
        ((RTNode) rtNode.getChildren().get(index + 1)).setMaxXP(maxXP1);

    }

    /**
     * 
     * Check the distance between right contur of left subtree and the left
     * contur of right subtree.
     * 
     * @param cLeft
     * @param cRight
     * @return rcd RunConturDate, the changed information during the running
     */
    public RunConturDate runContur(Contur cLeft, Contur cRight) {

        ConturElement left = cLeft.getContur().getFirst();
        ConturElement right = cRight.getContur().getFirst();
        int lShiftSum = 0;
        int rShiftSum = 0;

        int sepBegin = (int) Math.ceil(left.getX1() + left.getShift()
                + minSeperationNodeNode.doubleValue() - right.getX1());
        int minSep = sepBegin;

        int i = 0;
        int j = 0;

        for (int m = 0; m < cLeft.getContur().size(); m++) {
            // ConturElement c = cLeft.getContur().get(m);

        }

        for (int m = 0; m < cRight.getContur().size(); m++) {
            // ConturElement c = cRight.getContur().get(m);

        }

        boolean iStop = false;
        boolean jStop = false;
        while ((i != cLeft.getContur().size())
                && (j != cRight.getContur().size())) {

            left = cLeft.getContur().get(i);
            right = cRight.getContur().get(j);

            double cursor = Math.min(left.getY2(), right.getY2());

            if (!iStop) {
                lShiftSum += left.getShift() + left.getThreadValue();
            } else {
                iStop = false;
            }

            if (!jStop) {
                rShiftSum += right.getShift() + right.getThreadValue();
            } else {
                jStop = false;
            }

            // cursor is on both sides
            if ((cursor == left.getY2()) && (cursor == right.getY2())) {
                // both contur elements are node contur
                if ((left.getIsEdge() == false) && (right.getIsEdge() == false)) {

                    minSep = caculateNodeNodeSep(left.getX2(), right.getX2(),
                            lShiftSum, rShiftSum, minSep);
                }
                // left is edge contur and right is node contur
                else if ((left.getIsEdge() == true)
                        && (right.getIsEdge() == false)) {

                    ConturElement tmp = cLeft.getContur().get(i + 1);
                    minSep = caculateNodeNodeSep(tmp.getX1(), right.getX2(),
                            lShiftSum + tmp.getShift(), rShiftSum, minSep);
                }
                // left is node contur and left is edge contur
                else if ((left.getIsEdge() == false)
                        && (right.getIsEdge() == true)) {

                    ConturElement tmp = cRight.getContur().get(j + 1);
                    minSep = caculateNodeNodeSep(left.getX2(), tmp.getX1(),
                            lShiftSum, rShiftSum + tmp.getShift(), minSep);

                }

                i++;
                j++;

            }
            // cursor is on left side
            else if (cursor == left.getY2()) {

                // left is node contur
                if (left.getIsEdge() == false) {

                    if (right.getIsEdge() == false) {
                        minSep = caculateNodeNodeSep(left.getX2(), right
                                .getX2(), lShiftSum, rShiftSum, minSep);

                    } else // right is edge contur
                    {

                        double heightAdj1 = 0;
                        double heightAdj2 = 0;
                        // "1" for outgoint, ingoint; "0" for center
                        if (portMode == 0) {
                            ConturElement tmp1 = cRight.getContur().get(j - 1);
                            ConturElement tmp2 = cRight.getContur().get(j + 1);
                            heightAdj1 = (tmp1.getY2() - tmp1.getY1()) / 2;
                            heightAdj2 = (tmp2.getY2() - tmp2.getY2()) / 2;

                        }
                        minSep = caculateNodeEdgeSep(left, right, heightAdj1,
                                heightAdj2, lShiftSum, rShiftSum, minSep, true);

                    }
                }
                i++;
                jStop = true;
            }
            // cursor is on right side
            else if (cursor == right.getY2()) {

                // right is node contur
                if (right.getIsEdge() == false) {

                    // left is node contur
                    if (left.getIsEdge() == false) {
                        minSep = caculateNodeNodeSep(left.getX2(), right
                                .getX2(), lShiftSum, rShiftSum, minSep);

                    } else // left is edge contur
                    {

                        double heightAdj1 = 0;
                        double heightAdj2 = 0;
                        // "1" for outgoint, ingoint; "0" for center
                        if (portMode == 0) {
                            ConturElement tmp1 = cLeft.getContur().get(i - 1);
                            ConturElement tmp2 = cLeft.getContur().get(i + 1);
                            heightAdj1 = (tmp1.getY2() - tmp1.getY1()) / 2;
                            heightAdj2 = (tmp2.getY2() - tmp2.getY2()) / 2;

                        }
                        minSep = caculateNodeEdgeSep(left, right, heightAdj1,
                                heightAdj2, lShiftSum, rShiftSum, minSep, false);

                    }
                }
                j++;
                iStop = true;
            }
        }

        RunConturDate rcd = new RunConturDate(sepBegin, minSep, i, j,
                lShiftSum, rShiftSum);

        return rcd;
    }

    /**
     * To sure the distance between two outside children is even.
     * 
     * @param rcd
     * @param rtNode
     * @param uLeft
     * @param uRight
     * @return rcd
     */
    public RunConturDate checkDistance(RunConturDate rcd, RTNode rtNode,
            Contur uLeft, Contur uRight) {

        double d = (uLeft.getContur().getFirst()).getX1()
                + (uRight.getContur().getFirst()).getX1() + rcd.minSep;
        if (d % 2 == 1) {
            rcd.minSep = rcd.minSep + 1;
        }

        return rcd;
    }

    /**
     * Caculate current minimal x position of the rtNode.
     * 
     * @param index1
     * @param index2
     * @param rtNode
     * @param minSep
     * @return minXP minMaxXPosition
     */
    public MinMaxXPosition caculateMinX(int index1, int index2, RTNode rtNode,
            int minSep) {

        RTNode rt1 = (RTNode) rtNode.getChildren().get(index1);
        RTNode rt2 = (RTNode) rtNode.getChildren().get(index2);

        ConturElement c1 = rt1.getLeftContur().getContur().getLast();

        MinMaxXPosition minXP1 = rt1.getMinXP();
        MinMaxXPosition minXP2 = rt2.getMinXP();
        // MinMaxXPosition minXP = minXP1;
        MinMaxXPosition minXP = new MinMaxXPosition();
        minXP.xValue = minXP1.xValue;
        minXP.yValue = minXP1.yValue;

        if (minXP2.yValue > c1.getY2()) {

            if ((minXP2.xValue + minSep) < minXP1.xValue) {

                minXP.xValue = minXP2.xValue + minSep;
                minXP.yValue = minXP2.yValue;
            }

        }

        return minXP;
    }

    /**
     * Caculate the current maximal x position of the rtNode
     * 
     * @param index1
     * @param index2
     * @param rtNode
     * @param minSep
     * @return maxXP MinMaxXPosition
     */
    public MinMaxXPosition caculateMaxX(int index1, int index2, RTNode rtNode,
            int minSep) {

        RTNode rt1 = (RTNode) rtNode.getChildren().get(index1);
        RTNode rt2 = (RTNode) rtNode.getChildren().get(index2);

        ConturElement c2 = rt2.getRightContur().getContur().getLast();

        MinMaxXPosition maxXP1 = rt1.getMaxXP();
        MinMaxXPosition maxXP2 = rt2.getMaxXP();

        // MinMaxXPosition maxXP = maxXP2;
        MinMaxXPosition maxXP = new MinMaxXPosition();
        maxXP.xValue = maxXP2.xValue + minSep;
        maxXP.yValue = maxXP2.yValue;

        if (maxXP1.yValue > c2.getY2()) {
            if (maxXP1.xValue > maxXP2.xValue + minSep) {

                maxXP.xValue = maxXP1.xValue;
                maxXP.yValue = maxXP1.yValue;
            }
        }

        return maxXP;
    }

    /**
     * Swap two subtrees
     * 
     * @param index
     * @param rtNode
     * @param shiftAdj
     */
    public void swap(int index, RTNode rtNode, int shiftAdj) {

        RTNode tmp = (RTNode) rtNode.getChildren().get(index + 1);
        ArrayList<Object> list = rtNode.getChildren();
        for (int i = index; i >= 0; i--) {
            // update extreme value
            RTNode rt = (RTNode) rtNode.getChildren().get(i);

            ConturElement ce1 = rt.getRightContur().getContur().getFirst();
            ce1.setShift(ce1.getShift() + shiftAdj);

            list.set(i + 1, rt);

        }
        list.set(0, tmp);

    }

    /**
     * Caculte the distance between two nodes.
     * 
     * @param p1
     *            double right point of left node
     * @param p2
     *            double left point of right node
     * @param lShiftSum
     * @param rShiftSum
     * @param minSep
     * @return minSep int, the minimal distance which needed
     */
    public int caculateNodeNodeSep(double p1, double p2, int lShiftSum,
            int rShiftSum, int minSep) {
        int curSep = (int) Math.ceil(p1 + lShiftSum
                + minSeperationNodeNode.doubleValue() - p2 - rShiftSum);

        if (curSep > minSep) {
            minSep = curSep;
        }

        return minSep;
    }

    /**
     * Caculate the distance between node and edge
     * 
     * @param left
     * @param right
     * @param heightAdj1
     * @param heightAdj2
     * @param lShiftSum
     * @param rShiftSum
     * @param minSep
     * @param nodeOnLeft
     * @return minSep int, the minimal distance which needed
     */
    public int caculateNodeEdgeSep(ConturElement left, ConturElement right,
            double heightAdj1, double heightAdj2, int lShiftSum, int rShiftSum,
            int minSep, boolean nodeOnLeft) {
        int adj = 0;
        rShiftSum += minSep;

        if (nodeOnLeft) {
            double x1 = right.getX1() + rShiftSum;
            double x2 = right.getX2() + rShiftSum;
            double y1 = right.getY1() - heightAdj1;
            double y2 = right.getY2() + heightAdj2;

            double xP = left.getX2() + lShiftSum;
            double yP = left.getY2();

            // "0" for Straight Line
            if (edgeLayoutMode == 0) {
                // edge is vertikal line
                if (x1 == x2) {
                    if (left.getY1() > right.getY1()) {
                        double tmp = xP + minSeperationNodeEdge.doubleValue();
                        if (x2 < tmp) {
                            adj = (int) Math.ceil(tmp - x2);
                        }
                    }
                } else {
                    double xPSoll = (yP - y1) * (x2 - x1) / (y2 - y1) + x1;
                    if (xP > xPSoll - minSeperationNodeEdge) {
                        adj = (int) Math.ceil(xP
                                - (xPSoll - minSeperationNodeEdge));
                    }
                }
            } else // Bus-Layout
            {
                if (yP > (y2 - y1) / 2 + y1) {
                    adj = (int) Math.ceil(xP - x2 + minSeperationNodeEdge);
                }
            }
        } else // nodeOnLeft = false
        {
            double x1 = left.getX1() + lShiftSum;
            double x2 = left.getX2() + lShiftSum;
            double y1 = left.getY1() - heightAdj1;
            double y2 = left.getY2() + heightAdj2;

            double xP = right.getX2() + rShiftSum;
            double yP = right.getY2();

            // "0" for Straight Line
            if (edgeLayoutMode == 0) {
                // edge is vertikal line
                if (x1 == x2) {
                    if (right.getY1() > left.getY1()) {
                        double tmp = x2 + minSeperationNodeEdge.doubleValue();
                        if (xP < tmp) {
                            adj = (int) Math.ceil(tmp - xP);
                        }
                    }
                } else {
                    double xPSoll = (yP - y1) * (x2 - x1) / (y2 - y1) + x1;
                    if (xP < xPSoll + minSeperationNodeEdge) {
                        adj = (int) Math.ceil(xPSoll + minSeperationNodeEdge
                                - xP);
                    }
                }
            } else // Bus-Layout
            {
                if (yP > (y2 - y1) / 2 + y1) {
                    adj = (int) Math.ceil(x2 - xP + minSeperationNodeEdge);
                }
            }
        }

        if (adj > 0) {
            minSep += adj;
        }

        return minSep;
    }

}
// ------------------------------------------------------------------------------
// end of file
// ------------------------------------------------------------------------------