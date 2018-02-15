//=============================================================================
//
//   ArrowShapeEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
// $Id: ArrowShapeEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.ComboBoxEditComponent;

/**
 * Class used to display different edge shapes.
 */
public class ArrowShapeEditComponent extends ComboBoxEditComponent implements
        ActionListener {

    /**
     * Constructs a new Combobox with the possible entries for arrow shapes.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public ArrowShapeEditComponent(Displayable<?> disp) {
        super(disp);
        this.comboText = new String[] { "none", "Standard" };
        this.comboValue = new String[] { "",
                "org.graffiti.plugins.views.defaults.StandardArrowShape", };
        this.comboBox = new JComboBox(this.comboText);
        this.comboBox.addActionListener(this);
    }

    /**
     * Returns the <code>JComponent</code> for editing this edit component.
     * 
     * @return the <code>JComponent</code> for editing this edit component.
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#getComponent()
     */
    @Override
    public JComponent getComponent() {
        return this.comboBox;
    }

    /**
     * Is called after the comboBox changes. Informs the editPanel about the
     * change.
     * 
     * @param event
     *            the event describing the action
     */
    public void actionPerformed(ActionEvent event) {
        if (this.showEmpty
                && !this.comboBox.getSelectedItem().equals(EMPTY_STRING)) {

            this.showEmpty = false;
            if (comboBox.getItemAt(0).equals(EMPTY_STRING)) {
                comboBox.removeItemAt(0);
            }
        }

        // inform the editPanel about the change.
        // we don't care about the parameters...
        // this.comboBox.firePropertyChange(VEC_VALUE, true, false);

        fireVECChanged();
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
