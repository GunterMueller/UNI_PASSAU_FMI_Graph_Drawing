//=============================================================================
//
//   FontEditComponent.java
//
//   Copyright (c) 2001-2006 Gravisto Team, University of Passau
//
//=============================================================================
// $Id: FontEditComponent.java 5766 2010-05-07 18:39:06Z gleissner $

package org.graffiti.plugins.editcomponents.yagi;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.graffiti.attributes.IntegerAttribute;
import org.graffiti.attributes.StringAttribute;
import org.graffiti.graphics.GraphicAttributeConstants;
import org.graffiti.plugin.Displayable;
import org.graffiti.plugin.editcomponent.AbstractValueEditComponent;
import org.graffiti.plugin.editcomponent.VECChangeEvent;
import org.graffiti.plugin.editcomponent.ValueEditComponentListener;

/**
 * Provides components for editing the size and face of a font.
 */
public class FontEditComponent extends AbstractValueEditComponent implements
        ValueEditComponentListener {

    /** The component for editing the font's size. */
    private FontSizeEditComponent size;

    /** The component for editing the font's face. */
    private FontFaceEditComponent face;

    /** The panel holding the face and size components. */
    private JPanel panel;

    /**
     * Constructs a new <code>FontEditComponent</code> with two Comboboxes for
     * editing font size and font face.
     * 
     * @param disp
     *            the displayable (e.g., an attribute) this VEC belongs to
     */
    public FontEditComponent(Displayable<?> disp) {
        super(disp);
        String[] values = getValues();
        this.size = new FontSizeEditComponent(new IntegerAttribute("",
                (new Integer(values[0])).intValue()));
        this.face = new FontFaceEditComponent(
                new StringAttribute("", values[1]));
        this.size.addVECChangeListener(this);
        this.face.addVECChangeListener(this);

        JComponent sizeEC = this.size.getComponent();
        JComponent faceEC = this.face.getComponent();
        JLabel sizeLabel = new JLabel("size:");
        JLabel faceLabel = new JLabel("face:");

        SpringLayout layout = new SpringLayout();
        this.panel = new JPanel(layout);
        this.panel.add(faceLabel);
        this.panel.add(sizeLabel);
        this.panel.add(faceEC);
        this.panel.add(sizeEC);

        layout.putConstraint(SpringLayout.NORTH, faceLabel, 0,
                SpringLayout.NORTH, this.panel);
        layout.putConstraint(SpringLayout.WEST, faceLabel, 0,
                SpringLayout.WEST, this.panel);

        layout.putConstraint(SpringLayout.NORTH, faceEC, 0, SpringLayout.NORTH,
                this.panel);
        layout.putConstraint(SpringLayout.WEST, faceEC, 5, SpringLayout.EAST,
                faceLabel);

        layout.putConstraint(SpringLayout.NORTH, sizeLabel, 5,
                SpringLayout.SOUTH, faceEC);
        layout.putConstraint(SpringLayout.WEST, sizeLabel, 0,
                SpringLayout.WEST, this.panel);

        layout.putConstraint(SpringLayout.NORTH, sizeEC, 5, SpringLayout.SOUTH,
                faceEC);
        layout.putConstraint(SpringLayout.WEST, sizeEC, 5, SpringLayout.EAST,
                faceLabel);

        layout.putConstraint(SpringLayout.EAST, this.panel, 0,
                SpringLayout.EAST, faceEC);
        layout.putConstraint(SpringLayout.SOUTH, this.panel, 0,
                SpringLayout.SOUTH, sizeEC);

        this.setEditFieldValue();
    }

    /**
     * Returns an array with the current values for size and face of the
     * displayable. The value of the displayable is expected to have the form
     * <b>face="XXX" size="YYY"</b>.
     * 
     * @return an array with the size value at index 0 and the face value at
     *         index 1
     */
    private String[] getValues() {
        String[] returnValue = new String[] {
                "" + GraphicAttributeConstants.DEFAULT_FONT_SIZE,
                FontFaceEditComponent.DEFAULT_FONTFACE };
        String value = (String) this.displayable.getValue();
        if (value == null)
            return returnValue;
        int sizeIndex = value.indexOf("size=\"");
        int faceIndex = value.indexOf("face=\"");
        String sizeValue;
        String faceValue;
        if (sizeIndex != -1 && faceIndex != -1) {
            faceValue = value.substring(6, sizeIndex - 2);
            sizeValue = value.substring(sizeIndex + 6, value.length() - 1);
            returnValue[0] = sizeValue;
            returnValue[1] = faceValue;
        }
        return returnValue;
    }

    /**
     * Returns the <code>JComponent</code> for editing this edit component.
     * 
     * @return the <code>JComponent</code> for editing this edit component.
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#getComponent()
     */
    public JComponent getComponent() {
        return this.panel;
    }

    /**
     * Sets the current value of the attribute in the corresponding
     * <code>JComponent</code>.
     * 
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent
     *      #setEditFieldValue()
     */
    @Override
    protected void setDispEditFieldValue() {
        this.size.setEditFieldValue();
        this.face.setEditFieldValue();
    }

    /**
     * Sets the value of the displayable specified in the
     * <code>JComponent</code>.
     * 
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#setValue()
     */
    @Override
    protected void setDispValue() {
        this.face.setValue();
        this.size.setValue();
        // String newValue = "face=\"" + this.face.getDisplayable().getValue()
        // + "\" size=\"" + this.size.getDisplayable().getValue() + "\"";
        String newValue = "" + this.face.getDisplayable().getValue();
        @SuppressWarnings("unchecked")
        Displayable<String> displayable = (Displayable<String>) this.displayable;
        displayable.setValue(newValue);
    }

    /**
     * Specifies whether this component should allow editing.
     * 
     * @param enabled
     *            <code>true</code>, if the component should allow editing,
     *            <code>false</code> if not
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent
     *      #setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        this.size.setEnabled(enabled);
        this.face.setEnabled(enabled);
    }

    /**
     * Returns if this component allows editing.
     * 
     * @see org.graffiti.plugin.editcomponent.ValueEditComponent#isEnabled()
     * @return <code>true</code>, if the component allows editing,
     *         <code>false</code> if not
     */
    @Override
    public boolean isEnabled() {
        return this.size.isEnabled();
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
            this.size.setShowEmpty(showEmpty);
            this.face.setShowEmpty(showEmpty);
        }
        this.setEditFieldValue();
    }

    /**
     * Reacts on changes on font size and font face edit components.
     */
    public void vecChanged(VECChangeEvent event) {
        JComboBox faceEC = (JComboBox) this.face.getComponent();
        JTextField sizeEC = (JTextField) ((JPanel) this.size.getComponent())
                .getComponent(0);
        if (this.showEmpty && !faceEC.getSelectedItem().equals(EMPTY_STRING)
                && !sizeEC.getText().equals(EMPTY_STRING)) {
            this.showEmpty = false;
            if (faceEC.getItemAt(0).equals(EMPTY_STRING)) {
                faceEC.removeItemAt(0);
            }
        }

        fireVECChanged(event);
    }
}
// -----------------------------------------------------------------------------
// end of file
// -----------------------------------------------------------------------------
