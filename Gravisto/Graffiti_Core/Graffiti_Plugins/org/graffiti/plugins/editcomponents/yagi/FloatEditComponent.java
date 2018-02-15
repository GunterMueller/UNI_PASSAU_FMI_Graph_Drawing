//=============================================================================
//
//   FloatEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: FloatEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.text.ParseException;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.parameter.FloatParameter;

/**
 * Represents a component which can edit a float value.
 */
public class FloatEditComponent extends SliderEditComponent {

    /**
     * Constructs a new float edit component, referencing the given displayable.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public FloatEditComponent(Displayable<?> disp) {
        super(disp);
    }

    public FloatEditComponent(FloatParameter disp) {
        super(disp);
    }

    @Override
    protected Number doubleToNumber(double value) {
        return new Float(value);
    }

    @Override
    protected Number stringToNumber(String value) throws ParseException {
        return new Float(value);
    }

}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
