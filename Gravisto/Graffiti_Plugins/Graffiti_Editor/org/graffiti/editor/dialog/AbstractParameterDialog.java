// =============================================================================
//
//   AbstractParameterDialog.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: AbstractParameterDialog.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.dialog;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JDialog;

import org.graffiti.plugin.editcomponent.ValueEditComponent;

/**
 * <code>AbstractParameterDialog</code> provides an abstract implementation of
 * the <code>ParamterDialog</code> for editing name-value pairs.
 * 
 * @see javax.swing.JDialog
 * @see ParameterDialog
 */
public abstract class AbstractParameterDialog extends JDialog implements
        ParameterDialog {

    /**
     * 
     */
    private static final long serialVersionUID = -6466027298853748633L;
    /** The <code>ValueEditContainer</code> for this dialog. */
    protected ValueEditContainer valueEditContainer;

    /**
     * Constructs a new abstract parameter dialog.
     * 
     * @param parent
     *            the parent frame.
     * @param modal
     *            <code>true</code>, if this dialog should be modal.
     */
    public AbstractParameterDialog(Frame parent, boolean modal) {
        super(parent, modal);
    }

    /**
     * Sets the <code>ValueEditContainer</code> of this
     * <code>ParameterDialog</code> to the specified value.
     * 
     * @param vec
     *            the <code>ValueEditContainer</code> to be set.
     */
    public void setValueEditContainer(ValueEditContainer vec) {
        this.valueEditContainer = vec;
    }

    /**
     * Checks if all the edit components have a syntactically correct input.
     * 
     * @return a <code>java.util.List</code> of <code>EditComponent</code>s
     *         which have a syntactically incorrect input.
     */
    public List<ValueEditComponent> validateComponents() {
        List<ValueEditComponent> badComponents = new ArrayList<ValueEditComponent>();

        // for (ValueEditComponent valueEditComponent:
        // valueEditContainer.getEditComponents())
        // {
        // // check each of the components
        // // e.g. by looking if an exception is thrown when setting the
        // // value
        // // create a list with all the components having bad entries
        // }

        return badComponents;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
