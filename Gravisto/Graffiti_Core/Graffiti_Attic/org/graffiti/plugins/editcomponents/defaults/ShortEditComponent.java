// =============================================================================
//
//   ShortEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ShortEditComponent.java 5772 2010-05-07 18:47:22Z gleissner $

package org.graffiti.plugins.editcomponents.defaults;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.NumberEditComponent;

/**
 * Represents a gui component, which handles short values. Can be empty since
 * NumberEditComponent handles all primitive types.
 * 
 * @see NumberEditComponent
 */
public class ShortEditComponent extends NumberEditComponent {

    /**
     * Creates a new ShortEditComponent object.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public ShortEditComponent(Displayable<?> disp) {
        super(disp);
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
