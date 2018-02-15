// =============================================================================
//
//   DoubleEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: DoubleEditComponent.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.editcomponents.defaults;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.NumberEditComponent;

/**
 * Represents a gui component, which handles double values. Can be left empty
 * because superclass handles all primitive types.
 * 
 * @see NumberEditComponent
 */
public class DoubleEditComponent extends NumberEditComponent {

    /**
     * Creates a new DoubleEditComponent object.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public DoubleEditComponent(Displayable<?> disp) {
        super(disp);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
