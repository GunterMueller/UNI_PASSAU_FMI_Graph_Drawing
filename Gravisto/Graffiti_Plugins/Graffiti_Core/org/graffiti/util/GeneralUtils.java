// =============================================================================
//
//   GeneralUtils.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: GeneralUtils.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.graffiti.attributes.Attribute;
import org.graffiti.attributes.CollectionAttribute;
import org.graffiti.attributes.CompositeAttribute;
import org.graffiti.graph.Edge;
import org.graffiti.graph.Node;

/**
 * 
 */
public class GeneralUtils {
    /**
     * Returns a <code>Collection</code> of edges between <code>n1</code>
     * (source) and <code>n2</code> (target).
     * 
     * <p>
     * If no edge exists, returns an empty instance of <code>Collection</code>.
     * <code>n1==n2</code> allowed.
     * </p>
     * 
     * @param n1
     * @param n2
     * 
     * @return Collection
     */
    public static final Collection<Edge> getEdges(Node n1, Node n2) {
        Collection<Edge> col = new LinkedList<Edge>();

        for (Iterator<Edge> iter = n1.getEdgesIterator(); iter.hasNext();) {
            Edge edge = iter.next();

            if (n2.equals(edge.getSource()) || n2.equals(edge.getTarget())) {
                col.add(edge);
            }
        }

        return col;
    }

    /**
     * DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static final String getNewLineString() {
        return System.getProperty("line.separator");
    }

    /**
     * DOCUMENT ME!
     * 
     * @param s
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static final int[] getPositionOfOnes(String s) {
        int len = s.length();
        ArrayList<Integer> pos = new ArrayList<Integer>(len / 2);

        for (int i = 0; i < len; i++) {
            if ("1".equals(String.valueOf(s.charAt(i)))) {
                pos.add(new Integer(len - i - 1));
            }
        }

        int[] intarray = new int[pos.size()];

        int cnt = 0;

        for (Integer integer : pos) {
            intarray[cnt++] = integer.intValue();
        }

        return intarray;
    }

    /**
     * Returns <code>true</code> iff first parameter is a power of second, i.e.
     * returns true iff <code>base^a</code> is a natural number.
     * 
     * @param a
     *            number to check
     * @param base
     * 
     * @return DOCUMENT ME!
     */
    public static final boolean isPowerOf(int a, int base) {
        double log = log(a, base);

        return ((int) log == log);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param inStr
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static final String XMLify(String inStr) {
        inStr = inStr.replaceAll("&", "&amp;");
        inStr = inStr.replaceAll("<", "&lt;");
        inStr = inStr.replaceAll(">", "&gt;");
        inStr = inStr.replaceAll("'", "&apos;");
        inStr = inStr.replaceAll("\"", "&quot;");

        return inStr;
    }

    /**
     * True iff any edge between <code>n1</code> (source) and <code>n2</code>
     * (target) exists.
     * 
     * <p>
     * <code>n1==n2</code> allowed.
     * </p>
     * 
     * @param n1
     * @param n2
     * 
     * @return boolean
     */
    public boolean existsEdge(Node n1, Node n2) {
        for (Iterator<Edge> iter = n1.getEdgesIterator(); iter.hasNext();) {
            Edge edge = iter.next();

            if (n2.equals(edge.getSource()) || n2.equals(edge.getTarget()))
                return true;
        }

        return false;
    }

    /**
     * Returns the logarithm of <code>a</code> with respect to base
     * <code>base</code>.
     * 
     * @param a
     *            DOCUMENT ME!
     * @param base
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static final double log(double a, double base) {
        return Math.log(a) / Math.log(base);
    }

    /**
     * DOCUMENT ME!
     * 
     * @param d
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static final boolean nan(double d) {
        return Double.doubleToRawLongBits(d) == Double
                .doubleToRawLongBits(Double.NaN);
    }

    /**
     * Returns the number of "1"s within the string s.
     * 
     * @param s
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public static final int numberOfOnes(String s) {
        int cnt = 0;

        for (int i = 0; i < s.length(); i++) {
            if ("1".equals(String.valueOf(s.charAt(i)))) {
                cnt++;
            }
        }

        return cnt;
    }

    /**
     * Returns the first attribute it finds that has the given class type.
     * Warning: this method delivers only one contained attribute of the given
     * type. If there are more than one attibute of that type and you expect all
     * that attributes you should better use either
     * {@link #searchForAttributes(Attribute, Class, List)}.
     * 
     * @param attr
     *            the root attribute for the search
     * @param attributeType
     *            class to match
     * 
     * @return first child (depth first) of attr that matches class type
     *         attributeType.
     */
    public static final Attribute searchForAttribute(Attribute attr,
            Class<?> attributeType) {
        if (attributeType.isInstance(attr))
            return attr;
        else {
            if (attr instanceof CollectionAttribute) {
                for (Attribute attribute : ((CollectionAttribute) attr)
                        .getCollection().values()) {
                    Attribute newAttr = searchForAttribute(attribute,
                            attributeType);

                    if (newAttr != null)
                        return newAttr;
                }
            } else if (attr instanceof CompositeAttribute)
                // TODO: treat those correctly; some of those have not yet
                // been correctly implemented
                return null;
        }

        return null;
    }

    /**
     * Searches for all attributes that have the given class type.
     * 
     * @param attr
     *            the root attribute for the search
     * @param attributeType
     *            class to match
     * @param attributesList
     *            list which will be filled with found attributes.
     */
    public static void searchForAttributes(Attribute attr,
            Class<?> attributeType, List<Attribute> attributesList) {
        if (attributeType.isInstance(attr)) {
            attributesList.add(attr);
        }

        if (attr instanceof CollectionAttribute) {
            for (Attribute attribute : ((CollectionAttribute) attr)
                    .getCollection().values()) {
                searchForAttributes(attribute, attributeType, attributesList);
            }
        } else if (attr instanceof CompositeAttribute) {
            // TODO: treat those correctly; some of those have not yet
            // been correctly implemented
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
