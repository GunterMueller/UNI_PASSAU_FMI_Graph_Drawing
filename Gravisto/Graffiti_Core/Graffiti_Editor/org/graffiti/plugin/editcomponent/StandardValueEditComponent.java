// =============================================================================
//
//   StandardValueEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: StandardValueEditComponent.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.editcomponent;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JTextField;

import org.graffiti.plugin.Displayable;

/**
 * DOCUMENT ME!
 * 
 * @version $Revision: 5768 $
 */
public class StandardValueEditComponent extends AbstractValueEditComponent {

    /** DOCUMENT ME! */
    private JTextField textField;

    /**
     * Standard constructor.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public StandardValueEditComponent(Displayable<?> disp) {
        super(disp);
        this.textField = new JTextField();
        textField.setEditable(false);
    }

    /**
     * Return the component used to display the displayable.
     * 
     * @return DOCUMENT ME!
     */
    public JComponent getComponent() {
        // textField = new JTextField(displayable.getValue().toString());
        // textField.setEditable(false);
        // textField.setColumns(30);
        // panel.add(textField);
        textField.setMinimumSize(new Dimension(0, 20));
        textField.setPreferredSize(new Dimension(50, 30));
        textField.setMaximumSize(new Dimension(2000, 40));

        // textField.setSize(100, 30);
        return textField;
    }

    /**
     * Updates this component with the value from the displayable.
     */
    @Override
    protected void setDispEditFieldValue() {
        if (showEmpty) {
            this.textField.setText(EMPTY_STRING);
        } else {
            if (this != null && displayable != null && this.textField != null
                    && displayable.getValue() != null) {
                this.textField.setText(displayable.getValue().toString());
            }
        }
    }

    /**
     * Standard edit component is not editable.
     */
    @Override
    protected void setDispValue() {
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
