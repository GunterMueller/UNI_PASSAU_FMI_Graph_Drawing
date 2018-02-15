// =============================================================================
//
//   ComboBoxEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
// =============================================================================
// $Id: ComboBoxEditComponent.java 5768 2010-05-07 18:42:39Z gleissner $

package org.graffiti.plugin.editcomponent;

import java.awt.Dimension;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.graffiti.plugin.Displayable;

/**
 * Displays a combo box to let the user choose from several possibilities.
 * 
 * @version $Revision: 5768 $
 */
public class ComboBoxEditComponent extends AbstractValueEditComponent {

    /** The combobox component used. */
    protected JComboBox comboBox;

    /** Text that is displayed in the combo box. */
    protected Object[] comboText;

    /** The value that corresponds to the text specified in comboText. */
    protected Object[] comboValue;

    /**
     * Creates a new ComboBoxEditComponent object.
     * 
     * @param disp
     *            DOCUMENT ME!
     */
    public ComboBoxEditComponent(Displayable<?> disp) {
        super(disp);
    }

    /**
     * Creates a new ComboBoxEditComponent object.
     * 
     * @param disps
     *            DOCUMENT ME!
     */
    public ComboBoxEditComponent(Displayable<?>[] disps) {
        super(disps);
    }

    /**
     * Returns the <code>ValueEditComponent</code>'s <code>JComponent</code>.
     * 
     * @return DOCUMENT ME!
     */
    public JComponent getComponent() {
        this.comboBox.setMinimumSize(new Dimension(0, 30));
        this.comboBox.setPreferredSize(new Dimension(50, 30));
        this.comboBox.setMaximumSize(new Dimension(2000, 30));

        return this.comboBox;
    }

    /**
     * Sets the current value of the <code>Attribute</code> in the corresponding
     * <code>JComponent</code>.
     */
    @Override
    protected void setDispEditFieldValue() {
        if (showEmpty) {
            comboBox.insertItemAt(EMPTY_STRING, 0);
            comboBox.setSelectedIndex(0);
        } else {
            if (comboBox.getItemAt(0).equals(EMPTY_STRING)) {
                comboBox.removeItemAt(0);
            }

            Object value = this.displayable.getValue();

            for (int i = comboValue.length - 1; i >= 0; i--) {
                if (value.equals(comboValue[i])) {
                    this.comboBox.setSelectedIndex(i);

                    break;
                }
            }
        }
    }

    /**
     * Sets the value of the displayable specified in the
     * <code>JComponent</code>. Probably not useful or overwritten by
     * subclasses.
     */
    @Override
    protected void setDispValue() {
        if (this.comboBox.getSelectedItem().equals(EMPTY_STRING)
                || this.displayable.getValue().equals(
                        this.comboBox.getSelectedItem())) {
            System.out.println("return");
            return;
        }

        @SuppressWarnings("unchecked")
        Displayable<Object> displayable = (Displayable<Object>) this.displayable;
        if (this.comboBox.getItemAt(0).equals(EMPTY_STRING)) {
            displayable
                    .setValue(comboValue[this.comboBox.getSelectedIndex() - 1]);
        } else {
            displayable.setValue(comboValue[this.comboBox.getSelectedIndex()]);
        }
    }
}

// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
