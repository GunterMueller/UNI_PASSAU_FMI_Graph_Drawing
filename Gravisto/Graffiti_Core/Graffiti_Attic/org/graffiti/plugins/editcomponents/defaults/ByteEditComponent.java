// =============================================================================
//
//   ByteEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ByteEditComponent.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.editcomponents.defaults;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.NumberEditComponent;

/**
 * Represents a gui component, which handles byte values. May be empty since
 * superclass handles all primitive types.
 * 
 * @see NumberEditComponent
 */
public class ByteEditComponent extends NumberEditComponent {

    /**
     * Creates a new ByteEditComponent object.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public ByteEditComponent(Displayable<?> disp) {
        super(disp);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
