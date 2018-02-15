//=============================================================================
//
//   ByteEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: ByteEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.text.ParseException;

import org.graffiti.plugin.Displayable;

/**
 * Represents a component which can edit a byte value.
 */
public class ByteEditComponent extends SliderEditComponent {

    /**
     * Constructs a new byte edit component, referencing the given displayable.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public ByteEditComponent(Displayable<?> disp) {
        super(disp);
    }

    @Override
    protected Number doubleToNumber(double value) {
        return new Byte((byte) value);
    }

    @Override
    protected Number stringToNumber(String value) throws ParseException {
        return new Byte(value);
    }

    @Override
    public String getErrorMessageOfInvalidParameter() {
        // return verifier.verify(textField);

        double currentValue = 0;
        try {
            currentValue = Double.parseDouble(textField.getText());
        } catch (NumberFormatException e) {
            textField.selectAll();
            return "has to be a numeric expression";
        }

        if (currentValue < textFieldMin) {
            textField.selectAll();
            return "has to be at least " + (byte) textFieldMin;
        }
        if (currentValue > textFieldMax) {
            textField.selectAll();
            return "may be at most " + (byte) textFieldMax;
        }
        return "";
    }

}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
