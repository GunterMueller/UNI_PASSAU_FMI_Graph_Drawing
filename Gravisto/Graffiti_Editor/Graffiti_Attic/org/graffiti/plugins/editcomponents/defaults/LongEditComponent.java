// =============================================================================
//
//   LongEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: LongEditComponent.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.editcomponents.defaults;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.NumberEditComponent;

/**
 * Represents a gui component, which handles long values. Can be empty since
 * superclass handles all primitive types.
 * 
 * @see org.graffiti.plugin.editcomponent.NumberEditComponent
 */
public class LongEditComponent extends NumberEditComponent {

    /**
     * Creates a new LongEditComponent object.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public LongEditComponent(Displayable<?> disp) {
        super(disp);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
