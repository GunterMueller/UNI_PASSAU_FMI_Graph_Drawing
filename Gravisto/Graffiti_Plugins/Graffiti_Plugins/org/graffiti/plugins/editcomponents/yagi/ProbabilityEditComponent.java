// =============================================================================
//
//   ProbabilityEditComponent.java
//
//   Copyright (c) 2001-2006, Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ProbabilityEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.text.ParseException;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.parameter.ProbabilityParameter;

/**
 * Represents a component which can edit a double value.
 */
public class ProbabilityEditComponent extends SliderEditComponent {

    /**
     * Constructs a new probability edit component, referencing the given
     * displayable.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public ProbabilityEditComponent(Displayable<?> disp) {
        super(disp);
    }

    public ProbabilityEditComponent(ProbabilityParameter disp) {
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
