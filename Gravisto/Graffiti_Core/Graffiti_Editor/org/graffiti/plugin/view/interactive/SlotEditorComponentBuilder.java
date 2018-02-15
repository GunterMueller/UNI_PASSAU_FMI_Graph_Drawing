// =============================================================================
//
//   SlotEditorComponentBuilder.java
//
//   Copyright (c) 2001-2007, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.view.interactive;

import javax.swing.JComponent;

/**
 * Returns a {@code SlotEditorComponentBuilder} for this slot. When using
 * this slot to define the parameter of a trigger, the builder can be used
 * to graphically edit that parameter. May return {@code null}. Then the
 * tool system tries to infer an adequate component based on the slot type.
 * If this fails, the parameter is not graphically editable.
 * 
 * @return a {@code SlotEditorComponentBuilder} for this slot.
 */

/**
 * Common base class of objects that create components for {@code Observable}s
 * in order to graphically edit the value of a parameter slot.
 * 
 * @author Andreas Glei&szlig;ner
 * @version $Revision$ $Date$
 * @param <T>
 *            the type of the observed values for which the components are to be
 *            created.
 * @see Slot#getBuilder()
 */
public abstract class SlotEditorComponentBuilder<T> {
    /**
     * Creates a new component that automatically displays and modifies the
     * specified observed value in order to allow for the graphical editing of
     * the value of a parameter slot. In terms of MVC, the returned component
     * acts as the view and controller, while the model is represented by the
     * parameter slot.
     * 
     * @param binding
     *            an observed value that reflects the value that is assigned to
     *            a parameter slot.
     * @return a new component that automatically displays and modifies the
     *         specified observed value.
     * @see Slot#getBuilder()
     */
    public abstract JComponent createComponent(Observable<T> binding);
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
