// =============================================================================
//
//   ValueEditContainer.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ValueEditContainer.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.editor.dialog;

import java.util.List;

import org.graffiti.plugin.editcomponent.ValueEditComponent;

/**
 * <code>ValueEditContainer</code> is an interface for an arbitrary component
 * containing a set of <code>ValueEditComponent</code>s. A class implementing
 * this interface can be used either within a dialog or within a seperate frame
 * etc.
 * 
 * @see org.graffiti.plugin.editcomponent.ValueEditComponent
 */
public interface ValueEditContainer {

    /**
     * Returns a <code>java.util.List</code> containing all the edit components
     * of this <code>ValueEditContainer</code>.
     * 
     * @return a <code>java.util.List</code> containing all the edit components
     *         of this <code>ValueEditContainer</code>.
     */
    public List<ValueEditComponent> getEditComponents();

    /**
     * Adds another <code>ValueEditComponent</code> to the dialog.
     * 
     * @param vec
     *            the <code>ValueEditComponent</code> to be added to the dialog.
     */
    public void addValueEditComponent(ValueEditComponent vec);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
