// =============================================================================
//
//   ValueEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ValueEditComponent.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.editcomponent;

import javax.swing.JComponent;

import org.graffiti.event.AttributeListener;
import org.graffiti.event.EdgeListener;
import org.graffiti.event.GraphListener;
import org.graffiti.event.NodeListener;
import org.graffiti.event.TransactionListener;
import org.graffiti.plugin.Displayable;

/**
 * A generic extension of a <code>javax.swing.JComponent</code> which allows
 * editing of Attributes. Each class extending <code>ValueEditComponent</code>
 * contains an <code>org.graffiti.attributes.Attribute</code> and a
 * <code>javax.swing.JComponent</code> for editing the
 * <code>org.graffiti.attributes.Attribute</code>. As attribute values may
 * change from several sources a <code>ValueEditComponent</code> must implement
 * the <code>org.graffiti.event.AttributeListener</code>-interface.
 * 
 * @see org.graffiti.attributes.Attribute
 * @see org.graffiti.event.AttributeListener
 * @see javax.swing.JComponent
 */
public interface ValueEditComponent extends AttributeListener, EdgeListener,
        GraphListener, NodeListener, TransactionListener {

    /**
     * Preferred string to be displayed by edit components that have a showEmpty
     * value of true.
     */
    public static final String EMPTY_STRING = "---";

    /**
     * Returns the <code>ValueEditComponent</code>'s <code>JComponent</code>.
     * 
     * @return the <code>ValueEditComponent</code>'s <code>JComponent</code>.
     */
    public JComponent getComponent();

    /**
     * Sets the object that will be displayed.
     * 
     * @param disp
     *            the object to connect to this component.
     */
    public void setDisplayable(Displayable<?> disp);

    /**
     * Sets the objects that will be displayed.
     * 
     * @param disps
     *            the objects to connect to this component.
     */
    public void setDisplayables(Displayable<?>[] disps);

    /**
     * Returns the <code>Displayable</code> instance the current
     * <code>ValueEditComponent</code> contains.
     * 
     * @return the <code>Displayable</code> instance the current
     *         <code>ValueEditComponent</code> contains.
     */
    public Displayable<?> getDisplayable();

    /**
     * Returns the <code>Displayable</code> instances the current
     * <code>ValueEditComponent</code> contains.
     * 
     * @return the <code>Displayable</code> instances the current
     *         <code>ValueEditComponent</code> contains.
     */
    public Displayable<?>[] getDisplayables();

    /**
     * Sets the current value of the <code>Displayable</code> in the
     * corresponding <code>JComponent</code>. If <code>showEmpty</code> is set
     * to true, this component should instead show only empty fields.
     */
    public void setEditFieldValue();

    /**
     * Specifies whether this component should allow editing.
     * 
     * @param enabled
     */
    public void setEnabled(boolean enabled);

    /**
     * Returns whether this component allows editing.
     * 
     * @return DOCUMENT ME!
     */
    public boolean isEnabled();

    /**
     * Called with a value of true when this component should display nothing
     * instead of the value of its associated displayable. This is used when
     * several displayables use this component but have different values. When
     * set to false, the value of the displayable associated with this component
     * is used.
     */
    public void setShowEmpty(boolean showEmpty);

    /**
     * Returns true when this component actually does not represent the value of
     * an attribute.
     * 
     * @return DOCUMENT ME!
     */
    public boolean getShowEmpty();

    /**
     * Sets the value of the <code>Displayable</code> specified in the
     * <code>JComponent</code>. Should only change the value if the value is
     * really different otherwise too many events will be sent.
     */
    public void setValue();

    /**
     * Returns true if the value of the <code>ValueEditComponent</code> is
     * valid,
     * 
     * @return DOCUMENT ME!
     */
    public boolean isValid();

    /**
     * Returns an error message if the value of the
     * <code>ValueEditComponent</code> is invalid,
     * 
     * @return DOCUMENT ME!
     */
    public String getErrorMessageOfInvalidParameter();

    /**
     * Register a new value edit component listener.
     * 
     * @param listener
     *            the listener to add.
     */
    public void addVECChangeListener(ValueEditComponentListener listener);

    /**
     * Remove a value edit component listener.
     * 
     * @param listener
     *            the listener to remove.
     */
    public void removeVECChangeListener(ValueEditComponentListener listener);

    /**
     * Fire a VECChange event.
     * 
     * @param event
     *            the event to fire.
     */
    public void fireVECChanged(VECChangeEvent event);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
