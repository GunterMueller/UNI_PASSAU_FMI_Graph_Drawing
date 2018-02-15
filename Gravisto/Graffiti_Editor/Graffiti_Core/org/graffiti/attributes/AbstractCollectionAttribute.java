// =============================================================================
//
//   AbstractCollectionAttribute.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractCollectionAttribute.java 5779 2010-05-10 20:31:37Z gleissner $

package org.graffiti.attributes;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.graffiti.event.AttributeEvent;
import org.graffiti.plugin.XMLHelper;
import org.graffiti.util.logging.GlobalLoggerSetting;

/**
 * Provides common functionality for <code>CollectionAttribute</code> instances.
 * Calls the <code>ListenerManager</code> and delegates the functionality to the
 * implementing class.
 * 
 * @version $Revision: 5779 $
 */
public abstract class AbstractCollectionAttribute extends AbstractAttribute
        implements CollectionAttribute {
    /** The logger for this class */
    private static final Logger logger = Logger
            .getLogger(AbstractCollectionAttribute.class.getName());
    
    static {
        logger.setLevel(GlobalLoggerSetting.LOGGER_LEVEL);
    }

    /**
     * The internal map which maps the ids to the Attributes which are in this
     * <code>CollectionAttribute</code>.
     */
    protected Map<String, Attribute> attributes;

    /**
     * The <code>Attributable</code> of this <code>Attribute</code>. This
     * reference is <code>null</code> except for the root.
     */
    private Attributable attributable;

    /**
     * Constructor for setting the id of an
     * <code>AbstractCollectionAttribute</code>.
     * 
     * @param id
     *            the id of the <code>Attribute</code>.
     * 
     * @exception IllegalIdException
     *                if the given id contains a seperator. This is checked for
     *                in the constructor of the superclass
     *                <code>AbstractAttribute</code>.
     */
    public AbstractCollectionAttribute(String id) throws IllegalIdException {
        super(id);
    }

    /**
     * Sets the <code>Attribute</code>'s <code>Attributable</code>.
     * 
     * <p>
     * <b>Implementation Notes:</b> This method should only be called once and
     * only by an <code>addAttribute()</code> method call! The attributable
     * property may only be set on the root <code>Attribute</code> of a
     * hierarchy
     * </p>
     * 
     * @param att
     *            the new <code>Attributable</code> of the
     *            <code>Attribute</code>.
     * 
     * @throws FieldAlreadySetException
     *             DOCUMENT ME!
     */
    public void setAttributable(Attributable att)
            throws FieldAlreadySetException {
        assert att != null : "must not set attributable to null";
        assert this.getParent() == null : "Only the root attribute has a reference to the attributable "
                + " the hierarchy belongs to. Only call setAttributable on "
                + "attributes where parent == null.";

        // different from setParent, attributable is only null when
        // not set before
        if (this.attributable != null) {
            logger.fine("could not set attributable, was already set.");
            throw new FieldAlreadySetException(
                    "'attributable' field already set");
        } else {
            this.attributable = att;
            logger.fine("attributable set.");
        }
    }

    /**
     * Returns the <code>Attribute</code>'s <code>Attributable</code>.
     * 
     * @return the <code>Attribute</code>'s <code>Attributable</code>.
     */
    @Override
    public Attributable getAttributable() {
        Attribute parent = getParent();

        if (parent == null)
            return attributable;
        else
            return parent.getAttributable();
    }

    /**
     * Returns the attribute located at <code>path</code>.
     * 
     * @param path
     *            the relative path to the attribute from <code>this</code>.
     * 
     * @return the attribute found at <code>path</code>.
     * 
     * @exception AttributeNotFoundException
     *                if there is no attribute located at path.
     */
    public Attribute getAttribute(String path)
            throws AttributeNotFoundException {
        assert path != null;

        if (logger.isLoggable(Level.FINE)) {
            logger.fine("Searching for attribute at path " + path + ".");
        }

        if (path.equals("")) {
            logger.fine("Attribute found.");

            return this;
        } else {
            int sepIndex = path.indexOf(Attribute.SEPARATOR);
            String fstPath = sepIndex < 0 ? path : path.substring(0, sepIndex);

            if (logger.isLoggable(Level.FINER)) {
                logger.finer("getting attribute with id " + fstPath);
            }

            Attribute attr = attributes.get(fstPath);

            if (attr == null) {
                if (logger.isLoggable(Level.INFO)) {
                    logger
                            .info("No Attribute at path/subpath " + fstPath
                                    + ".");
                }

                throw new AttributeNotFoundException(
                        "Did not find sub attribute with ID " + fstPath);
            } else {
                if (sepIndex == -1) {
                    if (logger.isLoggable(Level.INFO)) {
                        logger.info("Path " + path
                                + " can't be separated any more.");
                    }

                    return attr;
                } else {
                    String subPath = path.substring(sepIndex
                            + Attribute.SEPARATOR.length());

                    try {
                        logger.info("Return a CollectionAttribute.");

                        return ((CollectionAttribute) attr)
                                .getAttribute(subPath);
                    } catch (ClassCastException cce) {
                        throw new AttributeNotFoundException(
                                "Attribute with ID "
                                        + fstPath
                                        + " is no "
                                        + "CollectionAttribute and therefore can't "
                                        + "contain subattribute with ID "
                                        + subPath);
                    }
                }
            }
        }
    }

    /**
     * Returns <code>true</code> if the HashMapAttribute is empty. The same as
     * <code>getCollection().isEmpty()</code> would yield, but this method
     * should be faster since the map is not copied.
     * 
     * @return <code>true</code> if the HashMapAttribute is empty.
     */
    public boolean isEmpty() {
        return attributes.isEmpty();
    }

    /**
     * Returns the value of this attribute, i.e. map between contained
     * attributes' ids and these attributes. The behaviour of this method
     * depends on implementation of method <code>getCollection()</code> in
     * concret classes which inherit this one. See documentation of concret
     * classes for more information.
     * 
     * @return the value of this attribute.
     */
    public Object getValue() {
        return getCollection();
    }

    /**
     * Adds a given attribute to the collection. Informs the
     * <code>ListenerManager</code> about the addition.
     * 
     * @param a
     *            the new attribute to add to the list.
     * 
     * @exception AttributeExistsException
     *                if there is already an attribute with the id of a.
     * @exception FieldAlreadySetException
     *                thrown if Attribute a already has a parent or attributable
     *                associated with it.
     */
    public void add(Attribute a) throws AttributeExistsException,
            FieldAlreadySetException {
        assert a != null;

        String attrId = a.getId();

        if (attributes.containsKey(attrId))
            throw new AttributeExistsException("Attribute with ID " + attrId
                    + " already exists in " + "this HashMapAttribute!");
        else {
            AttributeEvent attrEvent = new AttributeEvent(a);
            callPreAttributeAdded(attrEvent);
            a.setParent(this);

            if (logger.isLoggable(Level.INFO)) {
                logger.info("Parent of attibute with id " + attrId + " set.");
            }

            attributes.put(attrId, a);

            if (logger.isLoggable(Level.INFO)) {
                logger.info("Attribute with id " + attrId + " added.");
            }

            callPostAttributeAdded(attrEvent);
        }
    }

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
            throws AttributeExistsException, FieldAlreadySetException {
        assert a != null;

        if (inform) {
            add(a);
        } else {
            if (logger.isLoggable(Level.INFO)) {
                logger.info("Adding Attribute with id " + id + " without "
                        + "informing the ListenerManager.");
            }

            String attrId = a.getId();

            if (attributes.containsKey(attrId)) {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning("Attribute with id " + attrId + " already "
                            + "exists.");
                }

                throw new AttributeExistsException("Attribute with ID "
                        + attrId + "already exists in this HashMapAttribute!");
            } else {
                a.setParent(this);

                if (logger.isLoggable(Level.INFO)) {
                    logger.info("Parent of attribute with id " + attrId
                            + " set.");
                }

                attributes.put(attrId, a);

                if (logger.isLoggable(Level.INFO)) {
                    logger.info("Attribute with id " + attrId + " added.");
                }
            }
        }
    }

    /**
     * Removes the attribute with the given id from the collection. Notifies
     * <code>ListenerManager</code> with an AttributeRemoved event when the
     * attribute hierarchy is attached to an <code>Attributable</code>.
     * 
     * @param attrId
     *            the id of the attribute.
     * 
     * @exception AttributeNotFoundException
     *                if there is no attribute with the given id.
     * @throws IllegalIdException
     *             DOCUMENT ME!
     */
    public void remove(String attrId) throws AttributeNotFoundException {
        assert attrId != null;
        logger.info("Remove Attribute with id " + attrId
                + " from the collection.");

        if (attrId.indexOf(Attribute.SEPARATOR) != -1) {
            logger.severe("id contains SEPARATOR => seems to be a path.");
            throw new IllegalIdException(
                    "An id must not contain the SEPARATOR chararcter.");
        } else {
            Attribute attr = attributes.get(attrId);

            if (attr == null) {
                logger.info("No attribute with id " + attrId + " available.");
                throw new AttributeNotFoundException("Attribute with ID "
                        + attrId + "does not exist in "
                        + "this HashMapAttribute");
            } else {
                // notify ListenerManager
                AttributeEvent attrEvent = new AttributeEvent(attr);
                callPreAttributeRemoved(attrEvent);
                attributes.remove(attrId);
                logger.info("Removed attribute with id " + attrId + ".");
                callPostAttributeRemoved(attrEvent);
            }
        }
    }

    /**
     * Removes the given attribute from the collection by calling
     * <code>remove(String id)</code> with the attribute's id as parameter.
     * Notifies <code>ListenerManager</code> with an AttributeRemoved event when
     * the attribute hierarchy is attached to an <code>Attributable</code>.
     * 
     * @param attr
     *            the attribute to be removed.
     * 
     * @exception AttributeNotFoundException
     *                if the given attribute is not in the HashMap.
     */
    public void remove(Attribute attr) throws AttributeNotFoundException {
        assert attr != null;

        CollectionAttribute parent = attr.getParent();

        if (parent == null) {
            logger
                    .info("Parent of the specified Attribute to remove is null.\n"
                            + "It seems to be a Collection.");

            AttributeEvent attrEvent = new AttributeEvent(attr);
            callPreAttributeRemoved(attrEvent);

            for (String s : getCollection().keySet()) {
                attributes.remove(s);

                logger.info("Removed attribute with id " + attr.getId()
                        + " from the Collection.");
            }

            callPostAttributeRemoved(attrEvent);
        } else {
            // parent.remove cares about sending events to the ListenerManager
            parent.remove(attr.getId());
            logger.info("Removed Attribute with id " + attr.getId() + ".");
        }
    }

    /**
     * @see org.graffiti.attributes.Attribute#toString(int)
     */
    @Override
    public String toString(int n) {
        StringBuffer sb = new StringBuffer();

        sb.append(getSpaces(n) + id + " " + getClass().getName() + " {\n");

        for (Attribute attribute : attributes.values()) {
            sb.append(attribute.toString(n + 1) + "\n");
        }

        sb.append(getSpaces(n) + "}");

        return sb.toString();
    }

    /**
     * @see org.graffiti.plugin.Displayable#toXMLString()
     */
    @Override
    public String toXMLString() {
        StringBuffer valString = new StringBuffer();
        valString.append(XMLHelper.spc(4) + "<subAttributes>"
                + XMLHelper.getDelimiter());

        for (Attribute attr : attributes.values()) {
            valString.append(XMLHelper.spc(6) + "<subattr>"
                    + attr.toXMLString() + "</subattr>"
                    + XMLHelper.getDelimiter());
        }

        valString.append(XMLHelper.spc(4) + "</subAttributes>"
                + XMLHelper.getDelimiter() + XMLHelper.spc(4)
                + "<sorted>false</sorted>");

        return getStandardXML(valString.toString());
    }

    /**
     * Returns if the given path contains an <code>Attribute</code> of any kind.
     * 
     * @param path
     *            The path of the <code>Attribute</code> to be tested.
     * 
     * @return <code>true</code>, if an <code>Attribute</code> is saved in the
     *         given path, <code>false</code> otherwise.
     */
    public boolean containsAttribute(String path) {
        boolean attributeExists;

        if (path == null) {
            attributeExists = false;
        } else if (path.equals("")) {
            attributeExists = true;
        } else {
            int sepIndex = path.indexOf(Attribute.SEPARATOR);
            String fstPath = sepIndex < 0 ? path : path.substring(0, sepIndex);

            if (!attributes.containsKey(fstPath)) {
                attributeExists = false;
            } else {
                if (sepIndex == -1) {
                    attributeExists = true;
                } else {
                    Attribute attr = attributes.get(fstPath);

                    if (attr instanceof CollectionAttribute) {
                        CollectionAttribute col = (CollectionAttribute) attr;
                        String subPath = path.substring(sepIndex
                                + Attribute.SEPARATOR.length());
                        attributeExists = col.containsAttribute(subPath);
                    } else {
                        attributeExists = false;
                    }
                }
            }
        }
        return attributeExists;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
