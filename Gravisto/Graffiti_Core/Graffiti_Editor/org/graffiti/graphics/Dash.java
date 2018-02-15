// =============================================================================
//
//   Dash.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Dash.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

/**
 * Class that encapsulates the information needed to specify stroke properties.
 * 
 * @author schoeffl
 * @version $Revision: 5768 $
 */
public class Dash {

    /**
     * @see java.awt.BasicStroke
     */
    private float[] dashArray;

    /**
     * @see java.awt.BasicStroke
     */
    private float dashPhase;

    /**
     * Constructs a new Dash. Initializes the dashArray with <code>null</code>
     * and the dashPhase with 0.0.
     */
    public Dash() {
        this.dashArray = null;
        this.dashPhase = 0f;
    }

    /**
     * Constructs a new Dash. Sets the dashArray and the dashPhase to the given
     * values.
     * 
     * @param da
     *            the array to set the dashArray to.
     * @param dp
     *            the value to set the dashPhase to.
     */
    public Dash(float[] da, float dp) {
        this.dashArray = da;
        this.dashPhase = dp;
    }

    /**
     * Sets the dashArray to the given array.
     * 
     * @param da
     *            the array to set the dashArray to.
     */
    public void setDashArray(float[] da) {
        this.dashArray = da;
    }

    /**
     * Returns the dashArray.
     * 
     * @return the dashArray.
     */
    public float[] getDashArray() {
        return dashArray;
    }

    /**
     * Sets the dashPhase to the given value.
     * 
     * @param dp
     *            the new value for the dashPhase.
     */
    public void setDashPhase(float dp) {
        this.dashPhase = dp;
    }

    /**
     * Returns the dashPhase.
     * 
     * @return the dashPhase.
     */
    public float getDashPhase() {
        return dashPhase;
    }

    /**
     * Checks if that Dash is equal to this Dash.
     * 
     * @param that
     *            the Dash this will be compared to
     * @return <code>true</code>, if the dashes (i.e. their dashPhase and their
     *         dashArray) are equal, <code>false</code>, if not
     */
    @Override
    public boolean equals(Object that) {
        if (this.dashPhase != ((Dash) that).getDashPhase())
            return false;
        float[] thatDashArray = ((Dash) that).getDashArray();

        if (this.dashArray == null || thatDashArray == null)
            return this.dashArray == thatDashArray;
        if (this.dashArray.length != thatDashArray.length)
            return false;
        for (int i = 0; i < this.dashArray.length; i++) {
            if (this.dashArray[i] != thatDashArray[i])
                return false;
        }
        return true;
    }

    /**
     * Returns a hash code for this <code>Dash</code> object.
     * 
     * @return a hash code value for this object.
     */
    @Override
    public int hashCode() {
        int hashCode = Float.floatToIntBits(dashPhase);
        if (dashArray == null)
            return hashCode;
        for (int i = 0; i < dashArray.length; i++) {
            hashCode ^= Float.floatToIntBits((i + 1.3f) * dashArray[i]);
        }
        return hashCode;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
