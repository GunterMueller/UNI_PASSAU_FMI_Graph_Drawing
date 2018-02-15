/*
 * Created on Sep 6, 2005
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

package org.graffiti.plugins.algorithms.fpp;

/**
 * @author Le Pham Hai Dang
 */

import java.util.LinkedList;

import org.graffiti.core.DeepCopy;

/**
 * @author le
 */

public class EdgeAttributes implements DeepCopy {

    // ~ Instance fields
    // ========================================================
    private LinkedList<Integer> faces;

    // ~ Constructors
    // ================================================================
    /** Create a new EdgeAttributes with the name <code>String</code>. */
    public EdgeAttributes() {
        faces = new LinkedList<Integer>();
    }

    // ~ Methods
    // ================================================================
    /**
     * Add face to the EdgeAttribute
     * 
     * @param faceIndex
     *            <code>int</code>
     */
    protected void addFace(int faceIndex) {
        Integer index = new Integer(faceIndex);
        faces.add(index);
    }

    /**
     * @return all faces <code>LinkedList</code>, which belong the EdgeAttribute
     */
    protected LinkedList<Integer> getFaces() {
        return faces;
    }

    /**
     * @return <code>true</code>, if there are no faces, <code>false</code>
     *         otherwise.
     */
    protected boolean isEmpty() {
        return faces.isEmpty();
    }

    /** @return the number of faces, which belong the EdgeAttribute */
    protected int getSize() {
        return faces.size();
    }

    public Object copy() {
        // TODO Auto-generated method stub
        return null;
    }
}
