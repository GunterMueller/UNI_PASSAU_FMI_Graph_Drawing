// =============================================================================
//
//   ShapeDescriptionAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ShapeDescriptionAttribute.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.graphics;

import java.awt.geom.Point2D;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.LinkedHashMapAttribute;

/**
 * 
 */
public class ShapeDescriptionAttribute extends LinkedHashMapAttribute {

    // private CoordinateAttribute[] coords;
    //

    /**
     * Constructor for ShapeDescriptionAttribute.
     * 
     * @param id
     */
    public ShapeDescriptionAttribute(String id) {
        super(id);
    }

    // /**
    // * Returns a cloned list of attributes in this
    // * <code>CollectionAttribute</code>.
    // *
    // * @return a clone of the list of attributes in this
    // * <code>CollectionAttribute</code>.
    // */
    // public Map getCollection()
    // {
    // return (LinkedHashMap) ((LinkedHashMap) attributes).clone();
    // }

    /**
     * Replaces potentially existing coordinate attributes with the new given
     * ones.
     * 
     * @param points
     *            a <code>List</code> of <code>Point2D</code> objects.
     */
    public void setCoordinates(List<Point2D> points) {
        attributes = new LinkedHashMap<String, Attribute>();

        CoordinateAttribute coord;
        boolean inform = getAttributable() == null;

        int i = 0;

        for (Point2D p : points) {
            coord = new CoordinateAttribute("coord" + (i++), p);
            add(coord, inform);
        }
    }

    //
    // /**
    // * @see org.graffiti.attributes.HashMapAttribute#getCollection()
    // */
    // public Map getCollection() {
    // Map map = new LinkedHashMap();
    // for (int i = 0; i < coords.length; i++) {
    // map.put("coord" + (i+1), coords[i].copy());
    // }
    // return map;
    // }
    // /**
    // * Gets the x and y coordinates. The method returns an object of type
    // * <code>Pair</code> that encapsulates two int arrays.
    // *
    // * @return
    // */
    // public Pair getCoordinates() {
    // int nr = this.attributes.entrySet().size();
    // int[] xs = new int[nr];
    // int[] ys = new int[nr];
    //		
    // int cnt = 0;
    // CoordinateAttribute coord;
    // for (Iterator it = this.attributes.values().iterator(); it.hasNext();){
    // coord = (CoordinateAttribute)it.next();
    // xs[cnt] = (int)coord.getX();
    // ys[cnt] = (int)coord.getY();
    // cnt++;
    // }
    // return new Pair(xs, ys);
    // }
    //
    // /**
    // * Replaces potentially existing coordinate attributes with the new given
    // * ones.
    // *
    // * @param xs
    // * @param ys
    // */
    // public void setCoordinates(int[] xs, int[] ys) {
    // attributes = new LinkedHashMap();
    //
    // CoordinateAttribute coord;
    // if (getAttributable() == null) {
    // for (int i = 0; i < xs.length; i++) {
    // coord = new CoordinateAttribute("coord" + (i+1), xs[i], ys[i]);
    // add(coord);
    // }
    // } else {
    // for (int i = 0; i < xs.length; i++) {
    // coord = new CoordinateAttribute("coord" + (i+1), xs[i], ys[i]);
    // add(coord, false);
    // }
    // }
    // }
    //

    /**
     * Gets the x and y coordinates. The method returns a <code>List</code>
     * containing <code>Point2D</code> objects.
     * 
     * @return DOCUMENT ME!
     */
    public List<Point2D> getCoordinates() {
        List<Point2D> points = new LinkedList<Point2D>();
        CoordinateAttribute coord;

        for (Attribute attribute : this.attributes.values()) {
            coord = (CoordinateAttribute) attribute;
            points.add(coord.getCoordinate());
        }

        return points;
    }

    /**
     * Copies this <code>CollectionAttribute</code> and returns the copy. All
     * sub-attributes will be copied, too, i.e. a deep-copy is returned.
     * 
     * @return a copy of the <code>CollectionAttribute</code>.
     */
    @Override
    public Object copy() {
        ShapeDescriptionAttribute copy = new ShapeDescriptionAttribute(this
                .getId());

        for (Attribute attr : attributes.values()) {
            Attribute copiedAttribute = (Attribute) attr.copy();
            copiedAttribute.setParent(this);
            copy.attributes.put(copiedAttribute.getId(), copiedAttribute);
        }

        return copy;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
