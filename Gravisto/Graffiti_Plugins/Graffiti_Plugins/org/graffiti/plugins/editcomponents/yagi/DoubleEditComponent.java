//=============================================================================
//
//   DoubleEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: DoubleEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.text.ParseException;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.parameter.DoubleParameter;

/**
 * Represents a component which can edit a double value.
 */
public class DoubleEditComponent extends SliderEditComponent {

    /**
     * Constructs a new double edit component, referencing the given
     * displayable.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public DoubleEditComponent(Displayable<?> disp) {
        super(disp);
    }

    public DoubleEditComponent(DoubleParameter disp) {
        super(disp);
    }

    @Override
    protected Number doubleToNumber(double value) {
        return new Double(value);
    }

    @Override
    protected Number stringToNumber(String value) throws ParseException {
        return new Double(value);
    }

}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
