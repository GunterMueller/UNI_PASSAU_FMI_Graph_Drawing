// =============================================================================
//
//   AbstractValueEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractValueEditComponent.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.editcomponent;

import java.util.LinkedList;

import javax.swing.JComponent;

import org.graffiti.event.AttributeEvent;
import org.graffiti.plugin.Displayable;

/**
 * The class <code>AbstractValueEditComponent</code> provides some generic
 * implementation for <code>ValueEditComponent</code>s.
 * 
 * @see ValueEditComponent
 */
public abstract class AbstractValueEditComponent extends
        ValueEditComponentAdapter {

    /** The field to edit the value of the displayable. */
    protected JComponent editField;

    /**
     * Set to true if this component should display nothing instead of the value
     * of the attribute it represents.
     */
    protected boolean showEmpty = false;

    /** The list holding all value edit component listeners. **/
    private LinkedList<ValueEditComponentListener> vecListeners = new LinkedList<ValueEditComponentListener>();

    protected AbstractValueEditComponent() {
        this((Displayable<?>) null);
    }

    /**
     * Constructs a new <code>AbstractValueEditComponent</code>.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    protected AbstractValueEditComponent(Displayable<?> disp) {
        super(disp);
    }

    /**
     * Constructs a new <code>AbstractValueEditComponent</code>.
     * 
     * @param disps
     *            DOCUMENT ME!
     */
    protected AbstractValueEditComponent(Displayable<?>[] disps) {
        super(disps);
    }

    /**
     * Sets the displayable.
     * 
     * @param disp
     */
    public void setDisplayable(Displayable<?> disp) {
        this.displayable = disp;
        this.displayables = null;
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.ValueEditComponent#setDisplayables(
     * org.graffiti.plugin.Displayable<?>[])
     */
    public void setDisplayables(Displayable<?>[] disps) {
        this.displayables = disps;
        if (disps.length > 0) {
            this.displayable = disps[0];
        }
    }

    /**
     * Returns the <code>Attribute</code> instance the current
     * <code>ValueEditComponent</code> contains.
     * 
     * @return the <code>Attribute</code> instance the current
     *         <code>ValueEditComponent</code> contains.
     */
    public Displayable<?> getDisplayable() {
        return this.displayable;
    }

    /**
     * Returns the <code>Attribute</code> instances the current
     * <code>ValueEditComponent</code> contains.
     * 
     * @return the <code>Attribute</code> instances the current
     *         <code>ValueEditComponent</code> contains.
     */
    public Displayable<?>[] getDisplayables() {
        if (this.displayables != null)
            return this.displayables;
        return new Displayable<?>[] { this.displayable };
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.ValueEditComponent#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        getComponent().setEnabled(enabled);
    }

    /*
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#isEnabled()
     */
    public boolean isEnabled() {
        return getComponent().isEnabled();
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.ValueEditComponent#setShowEmpty(boolean
     * )
     */
    public void setShowEmpty(boolean showEmpty) {
        if (showEmpty == this.showEmpty)
            return;
        this.showEmpty = showEmpty;
        setEditFieldValue();
    }

    /**
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#getShowEmpty()
     */
    public boolean getShowEmpty() {
        return this.showEmpty;
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.ValueEditComponent#setEditFieldValue()
     */
    public void setEditFieldValue() {
        if (this.displayables == null) {
            setDispEditFieldValue();
        } else {
            Object value = this.displayable.getValue();
            boolean allEqual = true;
            for (Displayable<?> d : this.displayables) {
                if (!d.getValue().equals(value)) {
                    allEqual = false;
                    break;
                }
            }
            showEmpty = !allEqual;
            setDispEditFieldValue();
        }
    }

    /*
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#setValue()
     */
    @SuppressWarnings("unchecked")
    public void setValue() {

        setDispValue();

        if (this.displayables != null && !showEmpty) {
            Object value = this.displayable.getValue();
            for (Displayable<?> d : this.displayables) {
                ((Displayable<Object>) d).setValue(value);
            }
        }
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.ValueEditComponent#setEditFieldValue()
     */
    abstract protected void setDispEditFieldValue();

    /*
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#setValue()
     */
    abstract protected void setDispValue();

    /**
     * Called after a change of an displayable took place.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    @Override
    public void postAttributeChanged(AttributeEvent e) {
        if (e.getAttribute().equals(this.displayable)) {
            setEditFieldValue();
        }
    }

    /**
     * Called before a change of an displayable takes place.
     * 
     * @param e
     *            the AttributeEvent detailing the changes.
     */
    @Override
    public void preAttributeChanged(AttributeEvent e) {
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.ValueEditComponent#addVECChangeListener
     * (org.graffiti.plugin.editcomponent.ValueEditComponentListener)
     */
    public void addVECChangeListener(ValueEditComponentListener listener) {
        vecListeners.add(listener);
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.ValueEditComponent#removeVECChangeListener
     * (org.graffiti.plugin.editcomponent.ValueEditComponentListener)
     */
    public void removeVECChangeListener(ValueEditComponentListener listener) {
        vecListeners.remove(listener);
    }

    /**
     * Create and fire a new VECChangeEvent.
     */
    protected void fireVECChanged() {
        VECChangeEvent vce = new VECChangeEvent(this,
                displayables == null ? new Displayable<?>[] { displayable }
                        : displayables);
        fireVECChanged(vce);
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.ValueEditComponent#fireVECChanged(org
     * .graffiti.plugin.editcomponent.VECChangeEvent)
     */
    public void fireVECChanged(VECChangeEvent event) {
        for (ValueEditComponentListener l : vecListeners) {
            l.vecChanged(event);
        }
    }

    // /**
    // * Called just before an displayable is added.
    // *
    // * @param e the AttributeEvent detailing the changes.
    // */
    // public void preAttributeAdded(AttributeEvent e) {}
    //
    // /**
    // * Called just before an displayable is removed.
    // *
    // * @param e the AttributeEvent detailing the changes.
    // */
    // public void preAttributeRemoved(AttributeEvent e) {}
    //
    // /**
    // * Called just before an displayable is removed.
    // *
    // * @param e the AttributeEvent detailing the changes.
    // */
    // public void postAttributeRemoved(AttributeEvent e) {}
    //
    // /**
    // * Called if a transaction got started.
    // *
    // * @param t the transaction event.
    // */
    // public void transactionStarted(TransactionEvent t) {}
    //
    // /**
    // * Called if a transaction got finished.
    // *
    // * @param t the transaction event.
    // */
    // public void transactionFinished(TransactionEvent t) {}
    //
    // /**
    // * Called after an displayable as been added.
    // *
    // * @param e the AttributeEvent detailing changes.
    // */
    // public void postAttributeAdded(AttributeEvent e) {}
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
