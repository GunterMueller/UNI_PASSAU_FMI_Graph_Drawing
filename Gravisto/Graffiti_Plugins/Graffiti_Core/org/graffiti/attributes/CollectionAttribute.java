// =============================================================================
//
//   CollectionAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: CollectionAttribute.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

import java.util.Map;

import org.graffiti.core.DeepCopy;

/**
 * Contains a <code>Collection</code> of <code>Attribute</code>s. It is similar
 * to a directory which contains files and subdirectories. All methods modifying
 * the <code>CollectionAttribute</code> (no <code>get</code>-Methods or anything
 * similar) have to inform the <code>ListenerManager</code> about the
 * modification. The <code>ListenerManager</code> is accessible via
 * <code>getAttributable().getListenerManager()</code>.
 * 
 * <p>
 * <b>Implementation note:</b> The information of the
 * <code>ListenerManager</code> could look as follows: <blockquote>
 * 
 * <pre>
 *     public whatever modify() {
 *         AttributeEvent attr = new AttributeEvent(...);
 *         ListenerManager lm = getAttributable().getListenerManager();
 *         lm.preAttributeModified();
 *         // ...
 *         // here the Attribute will be modified.
 *         // ...
 *         lm.postAttributeModified();
 *         return anything;
 *     }
 * </pre>
 * 
 * </blockquote>
 * </p>
 * 
 * @version $Revision: 5767 $
 */
public interface CollectionAttribute extends DeepCopy, Attribute {
    /**
     * Sets the attributable the <code>Attribute</code> belongs to.
     * <b>Implementation Note:</b> This function should only work if the
     * <code>Attribute</code>'s attributable is null before. Normaly only
     * <code>addAttribute()</code> methods should call it.
     * 
     * @param att
     *            the <code>Attribute</code>'s attributable.
     * 
     * @exception FieldAlreadySetException
     *                if the attributable was already set before.
     */
    public void setAttributable(Attributable att)
            throws FieldAlreadySetException;

    /**
     * Returns the <code>Attribute</code> located at <code>path</code>.
     * 
     * @param path
     *            the relative path to the attribute from <code>this</code>.
     * 
     * @return DOCUMENT ME!
     * 
     * @exception AttributeNotFoundException
     *                if there is no attribute at the given location.
     */
    public Attribute getAttribute(String path)
            throws AttributeNotFoundException, NoCollectionAttributeException;

    /**
     * Sets the collection of attributes contained within this
     * <tt>CollectionAttribute</tt>
     * 
     * @param attrs
     *            the map that contains all attributes.
     */
    public void setCollection(Map<String, Attribute> attrs);

    /**
     * Returns the map of ids to attributes in the
     * <code>CollectionAttribute</code>.
     * 
     * @return the map of ids to attributes in the
     *         <code>CollectionAttribute</code>.
     */
    public Map<String, Attribute> getCollection();

    /**
     * Returns <code>true</code> if the <code>CollectionAttribute</code> is
     * empty.
     * 
     * @return <code>true</code> if this CollectionAttribute is empty.
     */
    public boolean isEmpty();

    /**
     * Adds a given <code>Attribute</code> to the list. The id of given
     * attribute may not be already in the list.
     * 
     * @param att
     *            the new attribute to add to the list.
     * 
     * @exception AttributeExistsException
     *                if there is already an attribute with the same id as the
     *                given attribute.
     */
    public void add(Attribute att) throws AttributeExistsException,
            FieldAlreadySetException;

    /**
     * Adds a given attribute to the collection. Only informs the
     * <code>ListenerManager</code> about the addition when <code>inform</code>
     * is set to true.
     * 
     * @param a
     *            the new attribute to add to the list.
     * @param inform
     *            when true, <code>ListenerManager</code> gets informed
     *            otherwise not
     * 
     * @exception AttributeExistsException
     *                if there is already an attribute with the id of a.
     * @exception FieldAlreadySetException
     *                thrown if Attribute a already has a parent or attributable
     *                associated with it.
     */
    public void add(Attribute a, boolean inform)
            throws AttributeExistsException, FieldAlreadySetException;

    /**
     * Removes the <code>Attribute</code> with the given <code>id</code> from
     * the list.
     * 
     * @param id
     *            the id of the attribute.
     * 
     * @exception AttributeNotFoundException
     *                if there is no attribute with the given id.
     */
    public void remove(String id) throws AttributeNotFoundException;

    /**
     * Removes the given <code>Attribute</code> from the list.
     * 
     * @param att
     *            the attribute to be removed.
     * 
     * @exception AttributeNotFoundException
     *                if the given attribute is not part of this
     *                <code>CollectionAttribute</code>.
     */
    public void remove(Attribute att) throws AttributeNotFoundException;

    /**
     * Returns if the given path contains an <code>Attribute</code> of any kind.
     * 
     * @param path
     *            The path of the <code>Attribute</code> to be tested.
     * 
     * @return <code>true</code>, if an <code>Attribute</code> is saved in the
     *         given path, <code>false</code> otherwise.
     */
    public boolean containsAttribute(String path);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
