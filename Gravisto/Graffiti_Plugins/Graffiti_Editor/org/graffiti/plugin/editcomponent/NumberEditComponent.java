// =============================================================================
//
//   NumberEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: NumberEditComponent.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.editcomponent;

import javax.swing.JComponent;

import org.graffiti.plugin.Displayable;

/**
 * <code>NumberEditComponent</code> provides an abstract implementation for
 * editing numerical attributes.
 * 
 * @see AbstractValueEditComponent
 * @see Number
 * @see javax.swing.JTextField
 */
public abstract class NumberEditComponent extends AbstractValueEditComponent {

    /** The gui element of this component. */
    protected SpinnerEditComponent spinner;

    /**
     * Constructs a new integer edit component.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    protected NumberEditComponent(Displayable<?> disp) {
        super(disp);
        spinner = new SpinnerEditComponent(disp);
    }

    /**
     * Returns the <code>JComponent</code> associated with this value edit
     * component. In this case a JSpinner.
     * 
     * @return the <code>JComponent</code> associated with this value edit
     *         component.
     */
    public JComponent getComponent() {
        return spinner.getComponent();
    }

    /**
     * Sets the displayable.
     * 
     * @param attr
     *            DOCUMENT ME!
     */
    @Override
    public void setDisplayable(Displayable<?> attr) {
        this.displayable = attr;
        spinner.setDisplayable(attr);
    }

    /**
     * Sets the current value of the <code>Attribute</code> in the corresponding
     * <code>JComponent</code>.
     */
    @Override
    protected void setDispEditFieldValue() {
        spinner.setEditFieldValue();
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.AbstractValueEditComponent#setEnabled
     * (boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        spinner.setEnabled(enabled);
    }

    /*
     * @see
     * org.graffiti.plugin.editcomponent.AbstractValueEditComponent#setShowEmpty
     * (boolean)
     */
    @Override
    public void setShowEmpty(boolean showEmpty) {
        // super.setShowEmpty(showEmpty);
        this.showEmpty = showEmpty;
        spinner.setShowEmpty(showEmpty);
    }

    /**
     * Sets the value of the displayable specified in the
     * <code>JComponent</code>. Calls setAttribute in the associated spinner,
     * i.e. it only changes the value if it is different.
     */
    @Override
    protected void setDispValue() {
        spinner.setValue();
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
