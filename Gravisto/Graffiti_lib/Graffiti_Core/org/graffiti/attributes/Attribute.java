// =============================================================================
//
//   Attribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Attribute.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

import org.graffiti.core.DeepCopy;
import org.graffiti.plugin.Displayable;

/**
 * Interfaces an object, which contains an <code>id</code> and a value. An
 * <code>Attribute</code> may contain other <code>Attribute</code> instances and
 * thereby form a complete attribute hierarchy.
 * 
 * @version $Revision: 5767 $
 * 
 * @see AttributeTypesManager
 */
public interface Attribute extends Displayable<Object>, DeepCopy {
    /** The path separator for the attributes hierarchy. */
    public static final String SEPARATOR = ".";

    /**
     * Returns the attributable the <code>Attribute</code> belongs to.
     * 
     * @return the <code>Attribute</code>'s attributable.
     */
    public Attributable getAttributable();

    /**
     * The attribute's value is set so that its <code>getValue()</code> method
     * will not return <code>null</code>.
     */
    public void setDefaultValue();

    /**
     * Returns the <code>Attribute</code>'s identifier.
     * 
     * @return the <code>Attribute</code>'s identifier.
     */
    public String getId();

    /**
     * Sets the <code>Attribute</code>'s parent. <b>Implementation Note:</b>
     * This function should only work if the <code>Attribute</code>'s parent is
     * <code>null</code> before. Normaly only <code>addAttribute()</code>
     * methods should call it.
     * 
     * @param parent
     *            the <code>Attribute</code>'s parent.
     * 
     * @exception FieldAlreadySetException
     *                if the parent was already set before
     */
    public void setParent(CollectionAttribute parent)
            throws FieldAlreadySetException;

    /**
     * Returns the <code>Attribute</code>'s parent. If <code>this</code> is
     * already the root <code>Attribute</code>, <code>null</code> is returned.
     * 
     * @return the <code>Attribute</code>'s parent.
     */
    public CollectionAttribute getParent();

    /**
     * Returns the <code>Attribute</code>'s path.
     * 
     * @return the <code>Attribute</code>'s path.
     */
    public String getPath();

    /**
     * Sets the value of this <code>Attribute</code> to the given value. If the
     * value is set via <code>att.setValue(x)</code> and then retrieved via
     * <code>y = att.getValue()</code> it is only guaranteed that x.equals(y)
     * not x==y, i.e. some particular implementations can provide x==y but this
     * behaviour is not general, in contrast x.equals(y) must be always
     * guaranteed.
     * 
     * @param v
     *            the new value.
     * 
     * @exception IllegalArgumentException
     *                if v is not of the apropriate type.
     */
    public void setValue(Object v) throws IllegalArgumentException;

    /**
     * Returns the value of this <code>Attribute</code>. If the value is set via
     * <code>att.setValue(x)</code> and then retrieved via <code>y =
     * att.getValue()</code> it is only guaranteed that x.equals(y) not x==y,
     * i.e. some particular implementations can provide x==y but this behaviour
     * is not general, in contrast x.equals(y) must be always guaranteed. <b>See
     * documentation of particular implementations of this interface for exact
     * specification of this method's behaviour!</b>
     * 
     * @return the value of this <code>Attribute</code>.
     */
    public Object getValue();

    /**
     * Returns a string representation prepended by <code>n</code> spaces of
     * this attribute.
     * 
     * @return DOCUMENT ME!
     */
    public String toString(int n);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
