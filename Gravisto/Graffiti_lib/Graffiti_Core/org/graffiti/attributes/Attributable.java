// =============================================================================
//
//   Attributable.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: Attributable.java 5767 2010-05-07 18:42:02Z gleissner $

package org.graffiti.attributes;

import org.graffiti.event.ListenerManager;

/**
 * Interfaces an object that contains a hierarchy of attributes. This interface
 * defines methods for accessing and modifying this hierarchy.
 * 
 * @version $Revision: 5767 $
 * 
 * @see AttributeTypesManager
 */
public interface Attributable {
    /**
     * Returns the <code>Attribute</code> located at the given path.
     * 
     * @param path
     *            the path to the <code>Attribute</code>.
     * 
     * @return DOCUMENT ME!
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the location
     *                specified by <code>path</code>.
     */
    public Attribute getAttribute(String path)
            throws AttributeNotFoundException;

    /**
     * Returns the attributes of the current object in the base hierarchie in a
     * <code>CollectionAttribute</code>.
     * 
     * @return the attributes of the current object.
     */
    public CollectionAttribute getAttributes();

    /**
     * Sets the <code>Attribute</code> at the given path to the given value. The
     * <code>Attribute</code> is created at the given location, if it does not
     * yet exist.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     */
    public void setBoolean(String path, boolean value);

    /**
     * Returns the value of the <code>Attribute</code> at the given path.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * 
     * @return the value of the <code>Attribute</code> at the given path.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public boolean getBoolean(String path) throws AttributeNotFoundException;

    /**
     * Sets the <code>Attribute</code> at the given path to the given value. The
     * <code>Attribute</code> is created at the given location, if it does not
     * yet exist.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     */
    public void setByte(String path, byte value);

    /**
     * Returns the value of the <code>Attribute</code> at the given path.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * 
     * @return the value of the <code>Attribute</code> at the given path.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public byte getByte(String path) throws AttributeNotFoundException;

    /**
     * Sets the <code>Attribute</code> at the given path to the given value. The
     * attribute is created at the given location, if it does not yet exist.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     */
    public void setDouble(String path, double value);

    /**
     * Returns the value of the <code>Attribute</code> at the given path.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * 
     * @return the value of the <code>Attribute</code> at the given path.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public double getDouble(String path) throws AttributeNotFoundException;

    /**
     * Sets the <code>Attribute</code> at the given path to the given value. The
     * attribute is created at the given location, if it does not yet exist.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     */
    public void setFloat(String path, float value);

    /**
     * Returns the value of the <code>Attribute</code> at the given path.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * 
     * @return the value of the <code>Attribute</code> at the given path.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public float getFloat(String path) throws AttributeNotFoundException;

    /**
     * Sets the <code>Attribute</code> at the given path to the given value. The
     * <code>Attribute</code> is created at the given location, if it does not
     * yet exist.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     */
    public void setInteger(String path, int value);

    /**
     * Returns the value of the <code>Attribute</code> at the given path.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * 
     * @return the value of the <code>Attribute</code> at the given path.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public int getInteger(String path) throws AttributeNotFoundException;

    /**
     * Returns the <code>ListenerManager</code> asscociated to this
     * <code>Attributable</code>.
     * 
     * @return the <code>ListenerManager</code> asscociated to this
     *         <code>Attributable</code>.
     */
    public ListenerManager getListenerManager();

    /**
     * Sets the <code>Attribute</code> at the given path to the given value. The
     * <code>Attribute</code> is created at the given location, if it does not
     * yet exist.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     */
    public void setLong(String path, long value);

    /**
     * Returns the value of the <code>Attribute</code> at the given path.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * 
     * @return the value of the <code>Attribute</code> at the given path.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public long getLong(String path) throws AttributeNotFoundException;

    /**
     * Sets the <code>Attribute</code> at the given path to the given value. The
     * <code>Attribute</code> is created at the given location, if it does not
     * yet exist.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     */
    public void setShort(String path, short value);

    /**
     * Returns the value of the <code>Attribute</code> at the given path.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * 
     * @return the value of the <code>Attribute</code> at the given path.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public short getShort(String path) throws AttributeNotFoundException;

    /**
     * Sets the <code>Attribute</code> at the given path to the given value. The
     * <code>Attribute</code> is created at the given location, if it does not
     * yet exist.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     */
    public void setString(String path, String value);

    /**
     * Returns the value of the <code>Attribute</code> at the given path.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * 
     * @return the value of the <code>Attribute</code> at the given path.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public String getString(String path) throws AttributeNotFoundException;

    /**
     * Adds <code>attr</code> to the attributes at position indicated by path.
     * Informs the ListenerManager about the change: calls
     * <code>preAttributeAdded</code> and <code>postAttributeAdded</code> in the
     * ListenManager. Also sets the <code>parent</code> and the
     * <code>attributable</code> of <code>attr</code>.
     * 
     * <p>
     * <b>Implementation Notes:</b><code>path</code> specifies the location of
     * the CollectionAttribute or (in case <code>path</code> is the empty
     * string) the Attributable <code>attr</code> should be added to. Usage
     * should look as follows: <blockquote>
     * 
     * <pre>
     *     Graph g = new Graph(..);
     *     CollectionAttribute ca = new CollectionAttribute(&quot;root&quot;);
     *     //add ca to the attributable g as root attribute
     *     g.addAttribute(ca, &quot;&quot;);
     *     IntegerAttribute ia = new IntegerAttribute(&quot;int&quot;, 10);
     *     //add ia as child of root
     *     g.addAttribute(ia, &quot;root&quot;);
     * </pre>
     * 
     * </blockquote>
     * </p>
     * 
     * @param attr
     *            the <code>Attribute</code> to be added.
     * 
     * @exception AttributeExistsException
     *                if there is already an <code>Attribute</code> with the
     *                same <code>id</code> as <code>attr</code> at location
     *                <code>path</code>.
     * @exception NoCollectionAttributeException
     *                if the <code>Attribute</code> at location
     *                <code>path</code> is not a
     *                <code>CollectionAttribute</code>.
     */
    public void addAttribute(Attribute attr, String path)
            throws AttributeExistsException, NoCollectionAttributeException,
            FieldAlreadySetException;

    /**
     * Adds a <code>BooleanAttribute</code> with the given value and id to a
     * <code>CollectionAttribute</code> at <code>path</code>.
     * 
     * @param path
     *            the path to the <code>CollectionAttribute</code> the new
     *            IntegerAttribute should be added to.
     * @param id
     *            the id of the newly created <code>Attribute</code>.
     * @param value
     *            the value of the newly created <code>Attribute</code>.
     * 
     * @exception NoCollectionAttributeException
     *                if the <code>Attribute</code> at the location specified by
     *                <code>path</code> is no <code>CollectionAttribute</code>.
     * @exception AttributeExistsException
     *                if there is already an <code>Attribute</code> with the
     *                given <code>id</code> at the given <code>path</code>.
     */
    public void addBoolean(String path, String id, boolean value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException;

    /**
     * Adds an ByteAttribute with the given value and id to a
     * <code>CollectionAttribute</code> at <code>path</code>.
     * 
     * @param path
     *            the path to the <code>CollectionAttribute</code> the new
     *            ByteAttribute should be added to.
     * @param id
     *            the id of the new ByteAttribute.
     * @param value
     *            the value of the new ByteAttribute.
     * 
     * @exception NoCollectionAttributeException
     *                if the <code>Attribute</code> at the location specified by
     *                <code>path</code> is no <code>CollectionAttribute</code>.
     * @exception AttributeExistsException
     *                if there is already an <code>Attribute</code> with the
     *                given <code>id</code> at the given <code>path</code>.
     */
    public void addByte(String path, String id, byte value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException;

    /**
     * Adds a <code>DoubleAttribute</code> with the given value and
     * <code>id</code> to a <code>CollectionAttribute</code> at
     * <code>path</code>.
     * 
     * @param path
     *            the path to the <code>CollectionAttribute</code> the new
     *            <code>IntegerAttribute</code> should be added to.
     * @param id
     *            the id of the newly created <code>Attribute</code>.
     * @param value
     *            the value of the newly created <code>Attribute</code>.
     * 
     * @exception NoCollectionAttributeException
     *                if the <code>Attribute</code> at the location specified by
     *                <code>path</code> is no <code>CollectionAttribute</code>.
     * @exception AttributeExistsException
     *                if there is already an <code>Attribute</code> with the
     *                given <code>id</code> at the given <code>path</code>.
     */
    public void addDouble(String path, String id, double value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException;

    /**
     * Adds a <code>FloatAttribute</code> with the given value and
     * <code>id</code> to a <code>CollectionAttribute</code> at
     * <code>path</code>.
     * 
     * @param path
     *            the path to the <code>CollectionAttribute</code> the new
     *            IntegerAttribute should be added to.
     * @param id
     *            the id of the newly created <code>Attribute</code>.
     * @param value
     *            the value of the newly created <code>Attribute</code>.
     * 
     * @exception NoCollectionAttributeException
     *                if the <code>Attribute</code> at the location specified by
     *                <code>path</code> is no <code>CollectionAttribute</code>.
     * @exception AttributeExistsException
     *                if there is already an <code>Attribute</code> with the
     *                given <code>id</code> at the given <code>path</code>.
     */
    public void addFloat(String path, String id, float value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException;

    /**
     * Adds an IntegerAttribute with the given value and <code>id</code> to a
     * <code>CollectionAttribute</code> at <code>path</code>.
     * 
     * @param path
     *            the path to the <code>CollectionAttribute</code> the new
     *            IntegerAttribute should be added to.
     * @param id
     *            the id of the new IntegerAttribute.
     * @param value
     *            the value of the new IntegerAttribute.
     * 
     * @exception NoCollectionAttributeException
     *                if the <code>Attribute</code> at the location specified by
     *                <code>path</code> is no <code>CollectionAttribute</code>.
     * @exception AttributeExistsException
     *                if there is already an <code>Attribute</code> with the
     *                given <code>id</code> at the given <code>path</code>.
     */
    public void addInteger(String path, String id, int value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException;

    /**
     * Adds an LongAttribute with the given value and id to a
     * <code>CollectionAttribute</code> at <code>path</code>.
     * 
     * @param path
     *            the path to the <code>CollectionAttribute</code> the new
     *            LongAttribute should be added to.
     * @param id
     *            the id of the new LongAttribute.
     * @param value
     *            the value of the new LongAttribute.
     * 
     * @exception NoCollectionAttributeException
     *                if the <code>Attribute</code> at the location specified by
     *                <code>path</code> is no <code>CollectionAttribute</code>.
     * @exception AttributeExistsException
     *                if there is already an <code>Attribute</code> with the
     *                given <code>id</code> at the given <code>path</code>.
     */
    public void addLong(String path, String id, long value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException;

    /**
     * Adds an ShortAttribute with the given value and id to a
     * <code>CollectionAttribute</code> at <code>path</code>.
     * 
     * @param path
     *            the path to the <code>CollectionAttribute</code> the new
     *            ShortAttribute should be added to.
     * @param id
     *            the id of the new ShortAttribute.
     * @param value
     *            the value of the new ShortAttribute.
     * 
     * @exception NoCollectionAttributeException
     *                if the <code>Attribute</code> at the location specified by
     *                <code>path</code> is no <code>CollectionAttribute</code>.
     * @exception AttributeExistsException
     *                if there is already an <code>Attribute</code> with the
     *                given <code>id</code> at the given <code>path</code>.
     */
    public void addShort(String path, String id, short value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException;

    /**
     * Adds an StringAttribute with the given value and id to a
     * <code>CollectionAttribute</code> at <code>path</code>.
     * 
     * @param path
     *            the path to the <code>CollectionAttribute</code> the new
     *            IntegerAttribute should be added to.
     * @param id
     *            the id of the newly created <code>Attribute</code>.
     * @param value
     *            the value of the newly created <code>Attribute</code>.
     * 
     * @exception NoCollectionAttributeException
     *                if the <code>Attribute</code> at the location specified by
     *                <code>path</code> is no <code>CollectionAttribute</code>.
     * @exception AttributeExistsException
     *                if there is already an <code>Attribute</code> with the
     *                given <code>id</code> at the given <code>path</code>.
     */
    public void addString(String path, String id, String value)
            throws NoCollectionAttributeException, AttributeExistsException,
            FieldAlreadySetException;

    /**
     * Changes the <code>Attribute</code> at the given path to the given value.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public void changeBoolean(String path, boolean value)
            throws AttributeNotFoundException;

    /**
     * Changes the <code>Attribute</code> at the given path to the given value.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public void changeByte(String path, byte value)
            throws AttributeNotFoundException;

    /**
     * Changes the <code>Attribute</code> at the given path to the given value.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public void changeDouble(String path, double value)
            throws AttributeNotFoundException;

    /**
     * Changes the <code>Attribute</code> at the given path to the given value.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public void changeFloat(String path, float value)
            throws AttributeNotFoundException;

    /**
     * Changes the <code>Attribute</code> at the given path to the given value.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public void changeInteger(String path, int value)
            throws AttributeNotFoundException;

    /**
     * Changes the <code>Attribute</code> at the given path to the given value.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public void changeLong(String path, long value)
            throws AttributeNotFoundException;

    /**
     * Changes the <code>Attribute</code> at the given path to the given value.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public void changeShort(String path, short value)
            throws AttributeNotFoundException;

    /**
     * Changes the <code>Attribute</code> at the given path to the given value.
     * 
     * @param path
     *            the path to search for the <code>Attribute</code>.
     * @param value
     *            the value to set to the <code>Attribute</code> to.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the specified
     *                location.
     */
    public void changeString(String path, String value)
            throws AttributeNotFoundException;

    /**
     * Deletes the <code>Attribute</code> located at the given path from the
     * attributes. Informs the ListenerManager about the change: calls
     * <code>pre-/postAttributeRemoved</code> in the ListenerManager.
     * 
     * @param path
     *            the path of the <code>Attribute</code> to be removed.
     * 
     * @exception AttributeNotFoundException
     *                if there is no <code>Attribute</code> at the location
     *                specified by <code>path</code>.
     */
    public void removeAttribute(String path) throws AttributeNotFoundException;

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
