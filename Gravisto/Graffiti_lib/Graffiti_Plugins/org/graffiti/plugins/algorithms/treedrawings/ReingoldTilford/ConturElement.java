// =============================================================================
//
//   ConturElement.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ConturElement.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.algorithms.treedrawings.ReingoldTilford;

/**
 * 
 * @author Beiqi
 * @version $Revision: 5766 $ $Date: 2006-07-03 22:10:05 +0200 (Mo, 03 Jul 2006)
 *          $
 * 
 *          A contur element is a segment with two Points.
 */
public class ConturElement {
    /** coordinate for the first point */
    private double x1, y1;
    /** coordinate for the second point */
    private double x2, y2;
    /** value for moving */
    private int shift;
    /** contur for node? */
    private boolean isEdge;
    /** point to his ancestor */
    private int ancestorIndex;
    private int threadValue;
    private int extremeShift;

    public ConturElement() {
        x1 = x2 = y1 = y2 = 0;
        shift = threadValue = extremeShift = 0;
        isEdge = false;
        ancestorIndex = 0;
    }

    public double getX1() {
        return x1;
    }

    public double getX2() {
        return x2;
    }

    public double getY1() {
        return y1;
    }

    public double getY2() {
        return y2;
    }

    public int getShift() {
        return shift;
    }

    public int getExtremeShift() {
        return extremeShift;
    }

    public int getThreadValue() {
        return threadValue;
    }

    public boolean getIsEdge() {
        return isEdge;
    }

    public int getAncestorIndex() {
        return ancestorIndex;
    }

    public void setPoint1(double x1, double y1) {
        this.x1 = x1;
        this.y1 = y1;
    }

    public void setPoint2(double x2, double y2) {
        this.x2 = x2;
        this.y2 = y2;
    }

    public void setY1(double y1) {
        this.y1 = y1;
    }

    public void setShift(int shift) {
        this.shift = shift;
    }

    public void setExtremeShift(int extremeShift) {
        this.extremeShift = extremeShift;
    }

    public void setThreadValue(int threadValue) {
        this.threadValue = threadValue;
    }

    public void setIsEdge(boolean isEdge) {
        this.isEdge = isEdge;
    }

    public void setAncestorIndex(int ancestorIndex) {
        this.ancestorIndex = ancestorIndex;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
