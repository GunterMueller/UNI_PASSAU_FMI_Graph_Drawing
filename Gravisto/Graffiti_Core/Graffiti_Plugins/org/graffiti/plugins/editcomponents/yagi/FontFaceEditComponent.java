//=============================================================================
//
//   FontFaceEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
//$Id: FontFaceEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import static org.graffiti.plugins.editcomponents.yagi.GraffitiValueEditComponents.VEC_VALUE;

import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.ComboBoxEditComponent;

/**
 * Provides a ComboBox for selecting a font's face.
 */
public class FontFaceEditComponent extends ComboBoxEditComponent implements
        ActionListener, PropertyChangeListener {

    public static final String DEFAULT_FONTFACE = "SYSTEM DEFAULT";

    /**
     * Constructs a new Combobox with the possible entries for font face.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public FontFaceEditComponent(Displayable<?> disp) {
        super(disp);
        // the values for the combobox "font face".
        String[] tmp = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        this.comboText = new String[tmp.length + 1];
        this.comboText[0] = DEFAULT_FONTFACE;
        System.arraycopy(tmp, 0, comboText, 1, tmp.length);
        this.comboValue = this.comboText;
        this.comboBox = new JComboBox(this.comboText);
        comboBox.addActionListener(this);
        comboBox.addPropertyChangeListener(this);
        this.setEditFieldValue();
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
     * Specifies if the component shows a value or a dummy (e.g. "---").
     * 
     * @param showEmpty
     *            <code>true</code>, if the component should display a dummy,
     *            <code>false</code> if not
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent
     *      #setShowEmpty(boolean)
     */
    @Override
    public void setShowEmpty(boolean showEmpty) {
        if (this.showEmpty != showEmpty) {
            this.showEmpty = showEmpty;
            this.setEditFieldValue();
        }
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
     * <code>JComponent</code>. Probably not usefull or overwritten by
     * subclasses.
     */
    @Override
    protected void setDispValue() {
        if (this.comboBox.getSelectedItem().equals(EMPTY_STRING)
                || this.displayable.getValue().equals(
                        this.comboBox.getSelectedItem()))
            return;

        @SuppressWarnings("unchecked")
        Displayable<Object> displayable = (Displayable<Object>) this.displayable;
        if (this.comboBox.getItemAt(0).equals(EMPTY_STRING)) {
            displayable
                    .setValue(comboValue[this.comboBox.getSelectedIndex() - 1]);
        } else if (this.comboBox.getSelectedItem().equals(DEFAULT_FONTFACE)) {
            displayable.setValue("");
        } else {
            displayable.setValue(comboValue[this.comboBox.getSelectedIndex()]);
        }
    }

    public void propertyChange(PropertyChangeEvent event) {
        if (!event.getPropertyName().equals(VEC_VALUE))
            return;

        if (this.showEmpty
                && !this.comboBox.getSelectedItem().equals(EMPTY_STRING)) {
            this.showEmpty = false;
            if (this.comboBox.getItemAt(0).equals(EMPTY_STRING)) {
                this.comboBox.removeItemAt(0);
            }
        }
    }

    /*
     * @see
     * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e) {
        if (this.showEmpty && !comboBox.getSelectedItem().equals(EMPTY_STRING)) {
            this.showEmpty = false;
            if (comboBox.getItemAt(0).equals(EMPTY_STRING)) {
                comboBox.removeItemAt(0);
            }
        }

        // inform the editPanel about the change.
        // we don't care about the parameters...
        // this.getComponent().firePropertyChange(VEC_VALUE, true, false);

        fireVECChanged();
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
