// =============================================================================
//
//   WeightAttribute.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugins.algorithms.mst.adapters.attribute;

import java.util.Collection;

import org.graffiti.attributes.AbstractAttribute;
import org.graffiti.attributes.Attribute;
import org.graffiti.graph.Edge;
import org.graffiti.graphics.LabelAttribute;

/**
 * A read-only attribute storing weights as float values. This class is
 * basically a wrapper around a <tt>LabelAttribute</tt> that can be read as a
 * float value.
 * 
 * @author Harald
 * @version $Revision$ $Date$
 */
public class WeightAttribute extends AbstractAttribute implements Attribute {
    /**
     * Copied (with minor modifications) from
     * {@link java.lang.Double#valueOf(String)}.
     */
    private static final String DIGITS = "(\\p{Digit}+)";

    /**
     * Copied (with minor modifications) from
     * {@link java.lang.Double#valueOf(String)}.
     */
    private static final String HEX_DIGITS = "(\\p{XDigit}+)";

    /**
     * Copied (with minor modifications) from
     * {@link java.lang.Double#valueOf(String)}.
     */
    private static final String EXP = "[eE][+-]?" + DIGITS;

    /**
     * Copied (with minor modifications) from
     * {@link java.lang.Double#valueOf(String)}.
     */
    private static final String FLOATING_POINT_LITERAL = ("[\\x00-\\x20]*" + // Optional
            // leading
            // "whitespace"
            "[+-]?(" + // Optional sign character
            "NaN|" + // "NaN" string
            "Infinity|" + // "Infinity" string

            // A decimal floating-point string representing a finite positive
            // number without a leading sign has at most five basic pieces:
            // Digits . Digits ExponentPart FloatTypeSuffix
            // 
            // Since this method allows integer-only strings as input
            // in addition to strings of floating-point literals, the
            // two sub-patterns below are simplifications of the grammar
            // productions from the Java Language Specification, 2nd
            // edition, section 3.10.2.

            // Digits ._opt Digits_opt ExponentPart_opt FloatTypeSuffix_opt
            "(((" + DIGITS + "(\\.)?(" + DIGITS + "?)(" + EXP + ")?)|" +

    // . Digits ExponentPart_opt FloatTypeSuffix_opt
            "(\\.(" + DIGITS + ")(" + EXP + ")?)|" +

            // Hexadecimal strings
            "((" +
            // 0[xX] HexDigits ._opt BinaryExponent FloatTypeSuffix_opt
            "(0[xX]" + HEX_DIGITS + "(\\.)?)|" +

            // 0[xX] HexDigits_opt . HexDigits BinaryExponent
            // FloatTypeSuffix_opt
            "(0[xX]" + HEX_DIGITS + "?(\\.)" + HEX_DIGITS + ")" +

            ")[pP][+-]?" + DIGITS + "))" + "[fFdD]?))" + "[\\x00-\\x20]*");// Optional

    // trailing
    // "whitespace"

    /**
     * The edge this attribute belongs to.
     */
    private Edge edge = null;

    /**
     * The weight used for unlabelled edges.
     */
    private Float defaultWeight = Float.NaN;

    /**
     * Creates a weight attribute with the specified id and owner.
     * 
     * @param id
     *            the id of this weight attribute.
     * @param e
     *            the edge this attributes belongs to.
     */
    public WeightAttribute(String id, Edge e) {
        this(id, e, Float.NaN);
    }

    /**
     * Creates a weight attribute with the specified id, owner and default
     * weight.
     * 
     * @param id
     *            the id of this weight attribute.
     * @param e
     *            the edge this weight attribute belongs to.
     * @param defaultWeight
     *            the weight used for unlabelled edges.
     */
    public WeightAttribute(String id, Edge e, float defaultWeight) {
        super(id);
        edge = e;
        this.defaultWeight = defaultWeight;
    }

    /**
     * Computes the value of this attribute from the labels of the edge owning
     * this attribute.
     * 
     * @return the value of this attribute.
     */
    private float computeWeight() {
        Collection<String> labels = new java.util.ArrayList<String>(1);
        for (Attribute a : edge.getAttributes().getCollection().values()) {
            if (a instanceof LabelAttribute) {
                String label = ((LabelAttribute) a).getLabel();
                if (label.matches(FLOATING_POINT_LITERAL)) {
                    labels.add(label);
                }
            }
        }
        if (labels.isEmpty())
            return defaultWeight;
        else if (labels.size() == 1)
            return Float.parseFloat(labels.iterator().next());
        else
            throw new AssertionError();
    }

    /**
     * Throws <tt>UnsupportedOperationException</tt>.
     */
    @Override
    protected void doSetValue(Object v) throws IllegalArgumentException {
        throw new UnsupportedOperationException();
    }

    /**
     * Returns the value of this attribute.
     * 
     * @return the value of this attribute.
     */
    public Object getValue() {
        return computeWeight();
    }

    /**
     * Does nothing.
     */
    public void setDefaultValue() {
    }

    /**
     * Throws <tt>UnsupportedOperationException</tt>.
     */
    public Object copy() {
        throw new java.lang.UnsupportedOperationException();
    }

}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
