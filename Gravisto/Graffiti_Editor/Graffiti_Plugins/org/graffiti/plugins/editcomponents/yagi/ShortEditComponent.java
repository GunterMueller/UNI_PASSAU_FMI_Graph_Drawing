//=============================================================================
//
//   ShortEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: ShortEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.text.ParseException;

import org.graffiti.plugin.Displayable;

/**
 * Represents a component which can edit a short value.
 */
public class ShortEditComponent extends SliderEditComponent {

    /**
     * Constructs a new short edit component, referencing the given displayable.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public ShortEditComponent(Displayable<?> disp) {
        super(disp);
    }

    @Override
    protected Number doubleToNumber(double value) {
        return new Short((short) value);
    }

    @Override
    protected Number stringToNumber(String value) throws ParseException {
        return new Short(value);
    }

}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
