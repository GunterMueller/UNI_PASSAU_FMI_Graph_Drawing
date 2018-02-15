// =============================================================================
//
//   VECChangeEvent.java
//
//   Copyright (c) 2001-2009, Gravisto Team, University of Passau
//
// =============================================================================
// $Id$

package org.graffiti.plugin.editcomponent;

import java.util.EventObject;

import org.graffiti.plugin.Displayable;

/**
 * @author hanauer
 * @version $Revision$ $Date$
 */
public class VECChangeEvent extends EventObject {

    /**
     * 
     */
    private static final long serialVersionUID = 7926089855549199589L;
    private Displayable<?>[] displayables;

    /**
     * Create a new VECChangeEvent.
     * 
     * @param source
     *            the ValueEditComponent that fired the event.
     */
    public VECChangeEvent(ValueEditComponent source,
            Displayable<?>[] displayables) {
        super(source);
        this.displayables = displayables;
    }

    /*
     * @see java.util.EventObject#getSource()
     */
    @Override
    public ValueEditComponent getSource() {
        return (ValueEditComponent) source;
    }

    /**
     * Returns the attributes associated with the VEC.
     * 
     * @return the VEC's attributes.
     */
    public Displayable<?>[] getDisplayables() {
        return displayables;
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
