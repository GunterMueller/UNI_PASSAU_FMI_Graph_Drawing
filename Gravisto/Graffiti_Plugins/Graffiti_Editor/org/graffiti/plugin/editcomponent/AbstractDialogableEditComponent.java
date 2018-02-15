// =============================================================================
//
//   AbstractDialogableEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractDialogableEditComponent.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.editcomponent;

import javax.swing.JButton;
import javax.swing.JDialog;

import org.graffiti.plugin.Displayable;

/**
 * <code>AbstractDialogableEditComponent</code> provides some default extensions
 * of the <code>AbstractValueEditComponent</code> for those instances of
 * <code>ValueEditComponent</code> which offer an arbitrary dialog for
 * specifying the corresponding attribute value.
 * 
 * @see javax.swing.JButton
 * @see javax.swing.JDialog
 */
public abstract class AbstractDialogableEditComponent extends
        AbstractValueEditComponent {

    /** The button for opening the dialog. */
    protected JButton button;

    /** The dialog for specifying the value. */
    protected JDialog dialog;

    /**
     * Constructs a new <code>AbstractDialogableEditComponent</code>.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    protected AbstractDialogableEditComponent(Displayable<?> disp) {
        super(disp);
    }

    /**
     * Returns the button of the <code>AbstractDialogableEditComponent</code>.
     * 
     * @return the button of the <code>AbstractDialogableEditComponent</code>.
     */
    public JButton getButton() {
        return this.button;
    }

    /**
     * Returns the dialog of this <code>AbstractDialogableEditComponent</code>.
     * 
     * @return the dialog of this <code>AbstractDialogableEditComponent</code>.
     */
    public JDialog getDialog() {
        return this.dialog;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
