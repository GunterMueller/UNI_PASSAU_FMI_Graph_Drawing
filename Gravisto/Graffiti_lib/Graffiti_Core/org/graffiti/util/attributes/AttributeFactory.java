// =============================================================================
//
//   AttributeFactory.java
//
//   Copyright (c) 2001-2008, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.util.attributes;

import org.graffiti.attributes.Attribute;
import org.graffiti.util.Callback;
import org.graffiti.util.VoidCallback;

/**
 * Factory class for creating attributes. Which attribute is to be created is
 * determined by the respective subclass of {@code AttributeFactory} by
 * implementing {@link #createAttribute(String)}. All setter methods of the
 * created {@link Attribute} must feature the following structure:
 * <ol>
 * <li>Call {@link #preCallback}.</li>
 * <li>Iff {@code #preCallback} returns true, actually set the value.</li>
 * <li>Call {@link #postCallback}.</li>
 * </ol>
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @see AttributeFactoryFactory
 */
public abstract class AttributeFactory {
    /**
     * The callback to be called before the value of an attribute created by
     * this factory is actually set.
     */
    protected Callback<Boolean, Object> preCallback;

    /**
     * The callback to be called after the value of an attribute created by this
     * factory has actually been set.
     */
    protected VoidCallback<Object> postCallback;

    /**
     * Sets the callbacks. The callbacks have to be called by all setter methods
     * of the attributes created by this factory.
     * 
     * @param preCallback
     *            the callback to be called before the value of an attribute
     *            created by this factory is actually set.
     * @param postCallback
     *            the callback to be called after the value of an attribute
     *            created by this factory has actually been set.
     */
    protected final void setCallback(Callback<Boolean, Object> preCallback,
            VoidCallback<Object> postCallback) {
        this.preCallback = preCallback;
        this.postCallback = postCallback;
    }

    /**
     * Creates a new attribute with the specified id. Before calling {@code
     * #createAttribute(String)}, the callbacks have to be set by
     * {@link #setCallback(Callback, VoidCallback)}.
     * 
     * @param id
     *            the id of the attribute to be created.
     * @return a new attribute with the specified id. The setter methods of the
     *         returned attribute must feature the following structure:
     *         <ol>
     *         <li>Call {@link #preCallback}.</li>
     *         <li>Iff {@code #preCallback} returns true, actually set the
     *         value.</li>
     *         <li>Call {@link #postCallback}.</li>
     *         </ol>
     */
    protected abstract Attribute createAttribute(String id);

    /**
     * Calls the {@link #preCallback} earlier set by
     * {@link #setCallback(Callback, VoidCallback)}. Must only be called by
     * setter methods of attributes created by this factory.
     * 
     * @param o
     *            value passed to the setter method of the attribute calling
     *            this method.
     * @return true, if the setter method of the attribute calling this method
     *         should actually set the new value.
     */
    protected final boolean preCallback(Object o) {
        return preCallback.call(o);
    }

    /**
     * Calls the {@link #postCallback} earlier set by
     * {@link #setCallback(Callback, VoidCallback)}. Must only be called by
     * setter methods of attributes created by this factory.
     * 
     * @param o
     *            value passed to the setter method of the attribute calling
     *            this method.
     */
    protected final void postCallback(Object o) {
        postCallback.call(o);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
